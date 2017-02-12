package dice;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import dice.controller.implementations.Controller74HC595;
import java.util.stream.IntStream;

public class Dice {

    public static void main(String[] args) {
        final GpioController gpio = GpioFactory.getInstance();
        DiceRoller diceRoller = new DiceRoller(
                new Controller74HC595(gpio),
                IntStream.rangeClosed(1, 6)
        );
        // hex
        /*DiceRoller diceRoller = new DiceRoller(
                new Controller74HC595(gpio),
                IntStream.rangeClosed(0, 15),
                16
        );*/
        diceRoller.roll();
        gpio.shutdown();
    }
    
}
