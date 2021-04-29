package ir.sitecoder.nestech;
public class NestechInit extends Nestech{
    private String msg ="";
    protected void setMessage(String msg)
    {
       this.msg=msg;
    }
    public String getMessage()
    {
       return this.msg;
    }
}
