package org.yunshanmc.lmc.core.command;

public class ArgConverterFailException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String   arg;
    private final Class<?> convertTo;

    public ArgConverterFailException(String arg, Class<?> convertTo) {
        this(arg, convertTo, null);
    }

    public ArgConverterFailException(String arg, Class<?> convertTo, Throwable cause) {
        super(cause);
        this.arg = arg;
        this.convertTo = convertTo;
    }

    public String getArg() {
        return this.arg;
    }

    public Class<?> getConvertTo() {
        return this.convertTo;
    }

}
