package dice.controller;

public interface Controller {
    public void addListener(ButtonPressedListener listener);
    public void show(char value, boolean showDP) throws NoSuchDisplayableCharacterException;
    public void clear();
}
