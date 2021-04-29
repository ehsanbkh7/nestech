package ir.sitecoder.nestech;

public interface AsyncInitCallback {
    public abstract void handleResponse(NestechInit response);

    public abstract void handleFault(NestechFault fault);
//    public abstract void handleFault(String result);
}
