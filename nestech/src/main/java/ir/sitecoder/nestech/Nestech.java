package ir.sitecoder.nestech;
import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
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
    public static Socket mSocket;

    static {
        try {
            mSocket = IO.socket("http://ne20.ir/");
            mSocket.connect();
        } catch (URISyntaxException e) {
            Log.d("SOCKET", "ABC");
            e.printStackTrace();
        }
    }

    AsyncCallback callback = new AsyncCallback() {
        @Override
        public void handleResponse(String result) {
        }

        @Override
        public void handleFault(String result) {
        }
    };

    public static void init(Activity ac, String appid, String Token, AsyncCallback callback) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ac);
        mSocket.on("init", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                ac.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("RESP__", args[0] + "");
                        try {
                            JSONObject data = new JSONObject(args[0].toString());
                            boolean ok = data.getBoolean("ok");
                            String msg = data.getString("msg");
                            if (ok) {
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("NesTechAppid", appid);
                                editor.putString("NesTechToken", Token);
                                editor.apply();
                                callback.handleResponse("OK!!!" + msg);
                            } else {
                                callback.handleFault("NOQO!");
                            }
                        } catch (JSONException e) {
                            callback.handleFault("NOO");
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        mSocket.connect();
        String jsonStr = "{\"type\":\"init\", \"appid\":\"" + appid + "\", \"Token\":\"" + Token + "\"}";
        mSocket.emit("init", jsonStr);
    }

    public static class UserService {
        public static void register(Activity ac, HashMap<String, String> map, AsyncCallback callback) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ac);
            mSocket.on("register", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    ac.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject data = null;
                            try {
                                data = new JSONObject(args[0].toString());
                                boolean ok = data.getBoolean("ok");
                                String msg = data.getString("msg");
                                if (ok) {
                                    callback.handleResponse(msg);
                                } else {
                                    callback.handleFault(msg);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
            mSocket.connect();
            map.put("appid", preferences.getString("NesTechAppid", ""));
            map.put("token", preferences.getString("NesTechToken", ""));
            mSocket.emit("register", mapToJson(map));
        }

        public static void login(Activity ac, HashMap<String, String> map, AsyncCallback callback) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ac);
            mSocket.on("login", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    ac.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject data = null;
                            try {
                                data = new JSONObject(args[0].toString());
                                boolean ok = data.getBoolean("ok");
                                String msg = data.getString("msg");
                                if (ok) {
                                    callback.handleResponse(msg);
                                } else {
                                    callback.handleFault(msg);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
            mSocket.connect();
            map.put("appid", preferences.getString("NesTechAppid", ""));
            map.put("token", preferences.getString("NesTechToken", ""));
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
