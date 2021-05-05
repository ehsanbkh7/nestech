package ir.sitecoder.nestech;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static org.json.JSONObject.wrap;

public class Nestech {
    private static Socket mSocket;
    private static SharedPreferences preferences=null;
    public static RequestQueue requestQueue;
    private Nestech(Context context)
    {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        preferences  = PreferenceManager.getDefaultSharedPreferences(context);
        //other stuf if you need
    }

    AsyncCallback callback = new AsyncCallback() {
        @Override
        public void handleResponse(NestechUser response) {
        }
        @Override
        public void handleFault(NestechFault fault) {
        }
    };
    public static void initApp(Context context, String appid, String token, AsyncInitCallback callback) throws UnsupportedEncodingException {
//        RequestQueue requestQueue = Volley.newRequestQueue(context);
//        String s = URLEncoder.encode(content, "utf-8");
        NestechFault f = new NestechFault();
        NestechInit ninit = new NestechInit();

        new Nestech(context);
        String url = "http://ne20.ir/initApp?appid="+appid+"&token="+token;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //do dome thing
                try {
                    JSONObject data = new JSONObject(response);
                    boolean ok = data.getBoolean("ok");
                    String msg = data.getString("msg");
                    if (ok) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("NesTechAppid", appid);
                        editor.putString("NesTechToken", token);
                        editor.apply();
                        ninit.setMessage(msg);
                        callback.handleResponse(ninit);
                    } else {
                        f.setMessage(msg);
                        callback.handleFault(f);
                    }
                } catch (JSONException e) {
                    f.setMessage("Error in connect");
                    callback.handleFault(f);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //do dome thing
                f.setMessage("Error in connect");
                callback.handleFault(f);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> Data = new HashMap<>();
                Data.put("appid", appid);
                Data.put("token", token);
                Data.put("initApp", "initApp");
                return Data;
            }
        };
        requestQueue.add(stringRequest);
    }

    public static class UserService {

        public static void register(  NestechUser user, AsyncCallback callback) throws UnsupportedEncodingException {
            NestechFault f = new NestechFault();
            if(preferences!=null){
                Map<String, Object> map = new HashMap<String, Object>();
                map = user.getProperties();
                if(!preferences.getString("NesTechAppid", "").equals("")){
                    map.put("appid", preferences.getString("NesTechAppid", ""));
                    map.put("token", preferences.getString("NesTechToken", ""));
                }
                String url = "http://ne20.ir/register"+mapToQuery(map);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //do dome thing
                        try {
                            JSONObject data = new JSONObject(response);
                            boolean ok = data.getBoolean("ok");
                            String msg = data.getString("msg");
                            if (ok) {
                                callback.handleResponse(user);
                            } else {
                                f.setMessage(msg);
                                callback.handleFault(f);
                            }
                        } catch (JSONException e) {
                            f.setMessage(e.getMessage());
                            callback.handleFault(f);
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //do dome thing
                        f.setMessage("Error in connect");
                        callback.handleFault(f);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> Data = new HashMap<>();
                        return Data;
                    }
                };
                requestQueue.add(stringRequest);
            }
            else{
                f.setMessage("First call the initApp method");
                callback.handleFault(f);
            }
        }
        public static void login(  NestechUser user, AsyncCallback callback) throws UnsupportedEncodingException {
            NestechFault f = new NestechFault();
            if(preferences!=null){
                Map<String, Object> map = new HashMap<String, Object>();
                map = user.getProperties();
                if(!preferences.getString("NesTechAppid", "").equals("")){
                    map.put("appid", preferences.getString("NesTechAppid", ""));
                    map.put("token", preferences.getString("NesTechToken", ""));
                }
                String url = "http://ne20.ir/login"+mapToQuery(map);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //do dome thing
                        try {
                            JSONObject data = new JSONObject(response);
                            boolean ok = data.getBoolean("ok");
                            String msg = data.getString("msg");
                            if (ok) {
                                String created_at = data.getString("created_at");
                                user.setProperty("created_at",created_at);
                                callback.handleResponse(user);
                            } else {
                                f.setMessage(msg);
                                callback.handleFault(f);
                            }
                        } catch (JSONException e) {
                            f.setMessage(e.getMessage());
                            callback.handleFault(f);
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //do dome thing
                        f.setMessage("Error in connect");
                        callback.handleFault(f);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> Data = new HashMap<>();
                        return Data;
                    }
                };
                requestQueue.add(stringRequest);
            }
            else{
                f.setMessage("First call the initApp method");
                callback.handleFault(f);
            }
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
        static StringBuilder mapToQuery(Map<?, ?> data) {
            JSONObject object = new JSONObject();
            StringBuilder obj = new StringBuilder();
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
                    if(i==0){
                        obj.append("?").append(key).append("=").append(entry.getValue());
                    }
                    else{
                        obj.append("&").append(key).append("=").append(entry.getValue());
                    }
                    i++;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return obj;
        }
    }

}
