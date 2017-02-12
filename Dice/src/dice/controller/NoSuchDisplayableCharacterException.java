package dice.controller;

public class NoSuchDisplayableCharacterException extends Exception {
    public NoSuchDisplayableCharacterException() {}
    
    public NoSuchDisplayableCharacterException(String msg) {
        super(msg);
    }
}
