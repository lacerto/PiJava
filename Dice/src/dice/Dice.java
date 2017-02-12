package dice;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

public class Dice {

    public static void main(String[] args) {
        final GpioController gpio = GpioFactory.getInstance();
        DiceRoller diceRoller = new DiceRoller(gpio);
        diceRoller.roll();
        gpio.shutdown();
    }
    
}
