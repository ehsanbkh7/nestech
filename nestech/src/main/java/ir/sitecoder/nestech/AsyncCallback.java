package ir.sitecoder.nestech;

public interface AsyncCallback {
    //    public abstract void handleResponse(String result);
    public abstract void handleResponse( NestechUser response );
    public abstract void handleFault( NestechFault fault );

//    void handleFault( BackendlessFault fault );
//    public abstract void handleFault(String result);

}
