package cn.newcraft.terminal.screen.console.other;

import java.text.DecimalFormat;

public class ConsoleProgressBar {

    private long minimum = 0;

    private long maximum = 100;

    private long barLen = 100;

    private char showChar = '=';

    private String string = "";
    private String prefix = "";

    public ConsoleProgressBar() {
    }

    public ConsoleProgressBar(long minimum, long maximum,
                              long barLen) {
        this(minimum, maximum, barLen, '=');
    }

    public ConsoleProgressBar(long minimum, long maximum,
                              long barLen, char showChar) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.barLen = barLen;
        this.showChar = showChar;
    }

    public void show(long value) {
        if (value < minimum || value > maximum) {
            return;
        }

        reset();
        minimum = value;
        float rate = (float) (minimum * 1.0 / maximum);
        long len = (long) (rate * barLen);
        draw(len, string);
        if (minimum == maximum) {
            afterComplete();
        }
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setString(String string) {
        this.string = string;
    }

    private void draw(long len, String string) {
        System.out.print(prefix);
        for (int i = 0; i < len; i++) {
            System.out.print(showChar);
        }
        System.out.print(' ');
        System.out.print(string);
    }


    private void reset() {
        System.out.print('\r');
    }

    private void afterComplete() {
        System.out.print('\n');
    }

    private String format(float num) {
        return new DecimalFormat("#.##%").format(num);
    }
}
