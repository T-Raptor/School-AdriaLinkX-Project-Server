package be.howest.ti.adria.web.exceptions;

public class MalformedRequestException extends RuntimeException {
    public static final long serialVersionUID = 10000;

    public MalformedRequestException(String msg) {
        super(msg);
    }

}
