package cn.newcraft.terminal.exception;

public class InvalidCommandException extends Exception {

    public InvalidCommandException(String name) {
        super(name);
    }
}
