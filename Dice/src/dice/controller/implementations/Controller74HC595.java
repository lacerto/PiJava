package dice.controller.implementations;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import dice.controller.ButtonPressedListener;
import dice.controller.Controller;
import dice.controller.NoSuchDisplayableCharacterException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Controller74HC595 implements Controller {
    private List<ButtonPressedListener> listeners = new ArrayList<>();
    private final GpioPinDigitalOutput sd;
    private final GpioPinDigitalOutput stcp;
    private final GpioPinDigitalOutput shcp;
    private final GpioPinDigitalInput button;
    private final Map<Character, Byte> numMap;
    
    public Controller74HC595(final GpioController gpio) {
        numMap = new LinkedHashMap<>();
        numMap.put('0', (byte) 0xFC);        
        numMap.put('1', (byte) 0x60);
        numMap.put('2', (byte) 0xDA);
        numMap.put('3', (byte) 0xF2);
        numMap.put('4', (byte) 0x66);
        numMap.put('5', (byte) 0xB6);
        numMap.put('6', (byte) 0xBE);
        numMap.put('7', (byte) 0xE0);
        numMap.put('8', (byte) 0xFE);
        numMap.put('9', (byte) 0xF6);
        numMap.put('a', (byte) 0xEE);
        numMap.put('b', (byte) 0x3E);
        numMap.put('c', (byte) 0x9C);
        numMap.put('d', (byte) 0x7A);
        numMap.put('e', (byte) 0x9E);
        numMap.put('f', (byte) 0x8E);        
        
        sd = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "SD", PinState.LOW);
        stcp = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "STCP", PinState.LOW);
        shcp = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "SHCP", PinState.LOW);
        button = gpio.provisionDigitalInputPin(RaspiPin.GPIO_03, "BUTTON");

        button.addListener((GpioPinListenerDigital) (GpioPinDigitalStateChangeEvent e) -> {
            if (e.getState().isLow()) {
                for (ButtonPressedListener bpl : listeners) {
                    bpl.buttonPressed();
                }
            }
        });        
    }
    
    @Override
    public void addListener(ButtonPressedListener listener) {
        listeners.add(listener);
    }
    
    @Override
    public void show(char value, boolean showDP) throws NoSuchDisplayableCharacterException {
        if (numMap.containsKey(value)) {
            byte dp = 0;
            if (showDP) dp = 1;
            shiftOut(sd, shcp, (byte) (numMap.get(value) + dp));
            stcp.pulse(1, true);         
        } else {
            throw new NoSuchDisplayableCharacterException(
                    "Cannot display character: " + value
            );
        }
    }
    
    @Override
    public void clear() {
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
