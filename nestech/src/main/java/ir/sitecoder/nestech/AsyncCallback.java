package ir.sitecoder.nestech;

public interface AsyncCallback {
    public abstract void handleResponse(String result);
    public abstract void handleFault(String result);
}
