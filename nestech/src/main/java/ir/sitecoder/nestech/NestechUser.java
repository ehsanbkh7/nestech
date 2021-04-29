package ir.sitecoder.nestech;

import java.util.HashMap;
import java.util.Map;

public class NestechUser {
    public static final String PASSWORD_KEY = "password";
    public static final String EMAIL_KEY = "email";
    public static final String ID_KEY = "objectId";
    public static final String LOCALE = "blUserLocale";

    private final Map<String, Object> properties = new HashMap<String, Object>();
    public void setPassword( String password )
    {
        setProperty( PASSWORD_KEY, password );
    }
    public void setProperty( String key, Object value )
    {
        synchronized( this )
        {
            properties.put( key, value );
        }
    }
    public void setEmail( String email )
    {
        setProperty( EMAIL_KEY, email );
    }
    public Map<String, Object> getProperties()
    {
        return new HashMap<String, Object>( properties );
    }
    public void clearProperties()
    {
        synchronized( this )
        {
            properties.clear();
        }
    }
    public Object getProperty( String key )
    {
        synchronized( this )
        {
            if( properties == null )
                return null;

            return properties.get( key );
        }
    }
}
