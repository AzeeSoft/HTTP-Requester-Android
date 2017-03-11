package com.azeesoft.libs.httprequester.core;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Azee on 4/19/2015.
 */
public abstract class AZHTTPRequester {

    public enum AZHTTPResult {
        OK,
        NETWORK_ERROR,
        JSON_ERROR,
        CANCELLED,
        NULL
    }

    private Context context;
    private JSONParser jparser;
    private JSONObject jobj;
    private CustomProgressDialog pd;

    private boolean showDialog = true, cancelable = false;

    private String progressText = "Connecting.. Please Wait..";
    private String requestString = "";
    private String url;

    private List<OnPrepareListener> onPrepareListenerList = new ArrayList<>();
    private List<OnResultListener> onResultListenerList = new ArrayList<>();
    private List<OnErrorListener> onErrorListenerList = new ArrayList<>();

    public AZHTTPRequester(Context context, String url) {
        initRequester(context, url, "POST");
    }

    public AZHTTPRequester(Context context, String url, String method) {
        initRequester(context, url, method);
    }

    public AZHTTPRequester(Context context, String url, String method, String progressText) {
        initRequester(context, url, method);
        this.progressText = progressText;
    }

    private void initRequester(Context context, String u, String m) {
        url = u;
        this.context = context;
        int method = Request.Method.POST;
        if (m.equals("GET"))
            method = Request.Method.GET;
        jparser = new JSONParser(method, u, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    QT.log("Response: " + response);
                    jobj = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                QT.log("Successful Response: " + jobj);
                onPostExecute(doInBackground(requestString));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                QT.log("Error in Response! jobj Value: " + jobj);
                onPostExecute(AZHTTPResult.NETWORK_ERROR);
                error.printStackTrace();
            }
        });
        jparser.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void addParam(String name, int value) {
        addParam(name, Integer.toString(value));
    }

    public void addParam(String name, String value) {
        addParam(name, value, true);
    }

    public void addParam(String name, String value, boolean leavable) {
        addParam(name, value, leavable, -1);
    }

    public void addParam(String name, String value, int id) {
        addParam(name, value, true, id);
    }

    public void addParam(String name, String value, boolean leaveable, int id) {
        if (jparser != null) {
            try {
                jparser.add(name, value, leaveable);
            } catch (IllegalArgumentException e) {
                throw new AZHTTPFormException(id);
            }
        } else {
            throw new NullPointerException("JParser is null");
        }
    }

    public void addOnResultListener(OnResultListener orl) {
        onResultListenerList.add(orl);
    }

    public void addOnPrepareListener(OnPrepareListener oril) {
        onPrepareListenerList.add(oril);
    }

    public void addOnErrorListener(OnErrorListener oel) {
        onErrorListenerList.add(oel);
    }

    public void sendRequest() {
        String s = "";


        RequestQueue queue = Volley.newRequestQueue(context);
        requestString = s;
        if (!onPreExecute())
            queue.add(jparser);
        //executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, s);
    }

    public void setShowDialog(boolean show) {
        showDialog = show;
    }

    public void setProgressText(String text) {
        progressText = text;
    }

    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }

    private boolean execPrepares() {
        boolean cancel = onPrepare();
        for (OnPrepareListener onPrepareListener : onPrepareListenerList) {
            if(onPrepareListener.onPrepare(this)){
                cancel=true;
            }
        }

        return cancel;
    }

    private void execResults(JSONObject jsonObject) {
        onResult(jsonObject);
        for (OnResultListener onResultListener : onResultListenerList)
            onResultListener.onResult(jsonObject);
    }

    private void execErrors(AZHTTPResult errCode, String msg) {
        onError(errCode, msg);
        for (OnErrorListener onErrorListener : onErrorListenerList)
            onErrorListener.onError(errCode, msg);
    }

    private boolean onPreExecute() {
        boolean cancel=execPrepares();

        if (!cancel && showDialog) {
            pd = new CustomProgressDialog(context);
            pd.setMessage(progressText);
            pd.setCancelable(cancelable);
            pd.show();
            QT.log("Showing PD...");
        }

        if(cancel){
            execErrors(AZHTTPResult.CANCELLED,AZHTTPResult.CANCELLED.toString());
        }

        return cancel;
    }

    private AZHTTPResult doInBackground(String... s) {
        if (jparser != null) {
            if (jobj == null) {
                /*if (s[0].equals("add")) {
                    if (!pendingAdded) {
                        if (!pendingCode.equals("-1"))
                            pendingAddedName=LocalTimecardManager.saveData(context, pendingCode, pendingDate, pendingTime);
                    }
                    return "local";

                } else*/
                return AZHTTPResult.JSON_ERROR;
            }

            System.out.println("Made HTTP Request");

            return AZHTTPResult.OK;
        } else {
            return AZHTTPResult.NULL;
        }

    }

    private void onPostExecute(AZHTTPResult code) {
        if (showDialog) {
            pd.dismiss();
            QT.log("Dismissing PD...");
        }

        if(code==AZHTTPResult.OK){
            execResults(jobj);
        }else{
            execErrors(code,code.toString());
        }
    }

    public Context getContext() {
        return context;
    }

    public JSONObject getJobj() {
        return jobj;
    }

    public String getUrl() {
        return url;
    }

    private class JSONParser extends StringRequest {

        Map<String, String> params;

        public JSONParser(int m, String u, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(m, u, listener, errorListener);
            params = new HashMap<>();
        }

        public void add(String name, String value) {
            add(name, value, false);
        }

        public void add(String name, String value, boolean leaveable) {
            if (!(value.trim().equals("")) || leaveable == true)
                params.put(name, value);
            else
                throw new IllegalArgumentException("Field cannot be empty");

            System.out.println("Name: " + name);
            System.out.println("Value: " + value);
            System.out.println("----------------");
        }

        @Override
        protected Map<String, String> getParams() {
            QT.log("Getting Params " + params.values());
            return params;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            QT.log("Getting Headers ");
            Map<String, String> params = new HashMap<>();
            params.put("Content-Type", "application/x-www-form-urlencoded");

            return params;
        }
    }


    abstract public boolean onPrepare();

    abstract public void onResult(JSONObject jsonObject);

    abstract public void onError(AZHTTPResult errCode, String errMsg);

    public interface OnResultListener {
        void onResult(JSONObject jobj);
    }

    public interface OnPrepareListener {
        boolean onPrepare(AZHTTPRequester httpRequester);
    }

    public interface OnErrorListener {
        void onError(AZHTTPResult errCode, String errMsg);
    }
}
