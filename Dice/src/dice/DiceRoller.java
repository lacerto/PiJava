package dice;

import java.util.Random;
import java.util.LinkedHashMap;
import java.util.Map;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.*;

public class DiceRoller {
    private static final int LOWER_BOUND = 1;
    private static final int UPPER_BOUND = 6;
    private static final long DELAY_BETWEEN_NUMBERS = 30;
    private static final long DELAY_INCREASE = 10;
    private static final long DELAY_MAX = 200;
    private static final long DELAY_SHOW = 3000;
    
    private boolean buttonPressed = false;
    private Map<Integer, Byte> numMap;
    
    private GpioPinDigitalOutput sd;
    private GpioPinDigitalOutput stcp;
    private GpioPinDigitalOutput shcp;
    private GpioPinDigitalInput button;
    
    public DiceRoller(final GpioController gpio) {
        numMap = new LinkedHashMap<Integer, Byte>();
        numMap.put(0, (byte) 0xFC);        
        numMap.put(1, (byte) 0x60);
        numMap.put(2, (byte) 0xDA);
        numMap.put(3, (byte) 0xF2);
        numMap.put(4, (byte) 0x66);
        numMap.put(5, (byte) 0xB6);
        numMap.put(6, (byte) 0xBE);
        numMap.put(7, (byte) 0xE0);
        numMap.put(8, (byte) 0xFE);
        numMap.put(9, (byte) 0xF6);        
        
        sd = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "SD", PinState.LOW);
        stcp = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "STCP", PinState.LOW);
        shcp = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "SHCP", PinState.LOW);
        button = gpio.provisionDigitalInputPin(RaspiPin.GPIO_03, "BUTTON");

        button.addListener((GpioPinListenerDigital) (GpioPinDigitalStateChangeEvent e) -> {
            if (e.getState().isLow()) {
                buttonPressed = true;
            }
        });
    }
    
    public void roll() {
        shiftOut(sd, shcp, (byte) 0x00);
        stcp.pulse(1, true);
        
        Random rnd = new Random();
        
        long delay = DELAY_BETWEEN_NUMBERS;
        int num;
        do {
            num = rnd.nextInt(UPPER_BOUND) + LOWER_BOUND;
            shiftOut(sd, shcp, numMap.get(num));
            stcp.pulse(1, true);
            try { Thread.sleep(delay); } catch(InterruptedException e) {}
            if (buttonPressed) delay += DELAY_INCREASE;            
        } while(delay < DELAY_MAX);
                
        shiftOut(sd, shcp, (byte) (numMap.get(num) + 0x01));
        stcp.pulse(1, true);
        try { Thread.sleep(DELAY_SHOW); } catch(InterruptedException e) {}        
        
        shiftOut(sd, shcp, (byte) 0x00);
        stcp.pulse(1, true);        
    }
    
    private void shiftOut(GpioPinDigitalOutput serialData, GpioPinDigitalOutput clock, byte data) {
        int pow2 = 1;
        for (int i=0; i<8; i++) {
            if ((data & pow2) == pow2) {
                serialData.high();
            } else {
                serialData.low();
            }
            clock.pulse(1, true);
            pow2 *= 2;
        }        
    }        
}
