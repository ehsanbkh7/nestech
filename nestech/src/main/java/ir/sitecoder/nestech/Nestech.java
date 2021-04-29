package ir.sitecoder.nestech;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static org.json.JSONObject.wrap;

public class Nestech {
    private static Socket mSocket;
    private static SharedPreferences preferences;
    private static boolean initConnection=false;
    private static boolean loginConnection=false;
    private static boolean registerConnection=false;
    static {
        try {
            mSocket = IO.socket("http://ne20.ir/");
            mSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    AsyncCallback callback = new AsyncCallback() {
        @Override
        public void handleResponse(NestechUser response) {
        }
        @Override
        public void handleFault(NestechFault fault) {
        }
    };

//    public static boolean isInitialized=initConnection;

    public static void init(Context context, String appid, String Token, AsyncInitCallback callback) {
        NestechFault f = new NestechFault();
        NestechInit ninit = new NestechInit();
        preferences  = PreferenceManager.getDefaultSharedPreferences(context);
        if(!initConnection){
            mSocket.on("init", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject data = new JSONObject(args[0].toString());
                                boolean ok = data.getBoolean("ok");
                                String msg = data.getString("msg");
                                if (ok) {
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("NesTechAppid", appid);
                                    editor.putString("NesTechToken", Token);
                                    editor.apply();
                                    ninit.setMessage(msg);
                                    callback.handleResponse(ninit);
                                } else {
                                    callback.handleFault(f);
//                                    isInitialized = false;
                                }
                            } catch (JSONException e) {
//                                isInitialized = false;
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
            initConnection=true;
        }

        mSocket.connect();
        String jsonStr = "{\"type\":\"init\", \"appid\":\"" + appid + "\", \"Token\":\"" + Token + "\"}";
        mSocket.emit("init", jsonStr);
    }
    public static class UserService {
        public static void register( NestechUser user, AsyncCallback callback) {
//            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            if(!registerConnection){
                mSocket.on("register", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                JSONObject data = null;
                                try {
                                    data = new JSONObject(args[0].toString());
                                    boolean ok = data.getBoolean("ok");
                                    String msg = data.getString("msg");
                                    if (ok) {
                                        callback.handleResponse(user);
                                    } else {
                                        NestechFault f = new NestechFault();
                                        f.setMessage(msg);
                                        callback.handleFault(f);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
                registerConnection=true;
            }

            mSocket.connect();
            Map<String, Object> map = new HashMap<String, Object>();
            map = user.getProperties();
            if(initConnection && !preferences.getString("NesTechAppid", "").equals("")){
                map.put("appid", preferences.getString("NesTechAppid", ""));
                map.put("token", preferences.getString("NesTechToken", ""));
            }
            mSocket.emit("register", mapToJson(map));
        }

        public static void login(NestechUser user, AsyncCallback callback, boolean stayLoggedIn) {
//                Activity ac =((Activity)context);
//            Activity ac=(Activity)context;
            NestechFault f = new NestechFault();
//            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            if(!loginConnection){
                mSocket.on("login", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                JSONObject data = null;
                                try {
                                    data = new JSONObject(args[0].toString());
                                    boolean ok = data.getBoolean("ok");
                                    String msg = data.getString("msg");

                                    if (ok) {
                                        String created_at = data.getString("created_at");
                                        user.setProperty("created_at",created_at);
                                        callback.handleResponse(user);
                                    } else {
//                                        user.clearProperties();

                                        f.setMessage(msg);
                                        callback.handleFault(f);

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
                loginConnection=true;
            }

            mSocket.connect();
            Map<String, Object> map = new HashMap<String, Object>();
            map = user.getProperties();
            if(initConnection && !preferences.getString("NesTechAppid", "").equals("")) {
                map.put("appid", preferences.getString("NesTechAppid", ""));
                map.put("token", preferences.getString("NesTechToken", ""));
            }
            mSocket.emit("login", mapToJson(map));
        }

        static StringBuilder mapToJson(Map<?, ?> data) {
            JSONObject object = new JSONObject();
            StringBuilder obj = new StringBuilder();
            obj.append("{");
            int i = 0;
            for (Map.Entry<?, ?> entry : data.entrySet()) {
                /*
                 * Deviate from the original by checking that keys are non-null and
                 * of the proper type. (We still defer validating the values).
                 */
                String key = (String) entry.getKey();
                if (key == null) {
                    throw new NullPointerException("key == null");
                }
                try {
                    object.put(key, wrap(entry.getValue()));
                    if (i == 0) {
                        obj.append("\"").append(key).append("\":").append("\"").append(entry.getValue()).append("\"");
                    } else {
                        obj.append(",\"").append(key).append("\":").append("\"").append(entry.getValue()).append("\"");
                    }
                    i++;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            obj.append("}");
            return obj;
        }
    }

}
