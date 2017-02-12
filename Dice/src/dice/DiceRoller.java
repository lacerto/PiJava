package dice;

import java.util.Random;
import dice.controller.ButtonPressedListener;
import dice.controller.Controller;
import dice.controller.NoSuchDisplayableCharacterException;
import java.util.stream.IntStream;

public class DiceRoller implements ButtonPressedListener {
    private static final long DELAY_BETWEEN_NUMBERS = 30;
    private static final long DELAY_INCREASE = 10;
    private static final long DELAY_MAX = 200;
    private static final long DELAY_SHOW = 3000;

    private Controller controller;
    private boolean buttonPressed = false;
    private int[] values;
    private int radix;
    

    public DiceRoller(final Controller controller, IntStream intStream) {
        this(controller, intStream, 10);
    }
    
    public DiceRoller(final Controller controller, IntStream intStream, int radix) {
        values = intStream.toArray();
        this.radix = radix;
        this.controller = controller;        
        this.controller.addListener(this);        
    }
    
    public void roll() {
        controller.clear();
        
        Random rnd = new Random();        
        long delay = DELAY_BETWEEN_NUMBERS;
        char ch;
        
        try {
            do {
                int index = rnd.nextInt(values.length);
                ch = Character.forDigit(values[index], radix);
                controller.show(ch, false);
                try { Thread.sleep(delay); } catch(InterruptedException e) {}
                if (buttonPressed) delay += DELAY_INCREASE;            
            } while(delay < DELAY_MAX);
                
            controller.show(ch, true);
        } catch(NoSuchDisplayableCharacterException nsdche) {
            System.err.println(nsdche.getMessage());
        } finally {        
            try { Thread.sleep(DELAY_SHOW); } catch(InterruptedException e) {}        
            controller.clear();
        }
    }
    
    @Override
    public void buttonPressed() {
        buttonPressed = true;
    }
}
