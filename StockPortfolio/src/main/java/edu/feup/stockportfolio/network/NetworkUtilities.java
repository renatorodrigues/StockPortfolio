package edu.feup.busphone.util.network;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import edu.feup.busphone.BusPhone;

public class NetworkUtilities {
    private static final String TAG = "NetworkUtilities";

    protected static final String HOST = "172.30.63.229";
    protected static final String SCHEME = "http";
    protected static final int PORT = 3000;
    protected static final String BASE_URL = SCHEME + "://" + HOST;

    protected static final int TIMEOUT_CONNECTION = 3000;
    protected static final int TIMEOUT_SOCKET = 5000;

    private static HttpClient http_client_;
    private static HttpHost http_host_;

    public static final class Status {
        public static final int OK = 0;
        public static final int BAD_USERNAME_OR_PASSWORD = 1;
        public static final int BAD_PARAMETERS = 2;
        public static final int ERROR = 3;
        public static final int ALREADY_IN_USE = 4;
        public static final int INVALID_TOKEN = 5;
        public static final int TOKEN_EXPIRED = 6;
        public static final int EXCESS_TICKETS = 7;
        public static final int TICKET_USED = 8;
    }

    private static void maybeCreateHttpClient() {
        if (http_client_ == null) {
            http_client_ = new DefaultHttpClient();
            ClientConnectionManager manager = http_client_.getConnectionManager();
            HttpParams params = http_client_.getParams();

            HttpConnectionParams.setConnectionTimeout(params, TIMEOUT_CONNECTION);
            HttpConnectionParams.setSoTimeout(params, TIMEOUT_SOCKET);

            http_client_ = new DefaultHttpClient(new ThreadSafeClientConnManager(params, manager.getSchemeRegistry()), params);
        }
    }

    private static void maybeCreateHttpHost() {
        if (http_host_ == null) {
            http_host_ = new HttpHost(HOST, PORT, SCHEME);
        }
    }

    private static JSONObject executeRequest(HttpRequest request) {
        maybeCreateHttpClient();
        maybeCreateHttpHost();

        HttpResponse response;
        try {
            response = http_client_.execute(http_host_, request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return new JSONObject(EntityUtils.toString(response.getEntity()));
            }

            Log.d(TAG, "status code = " + response.getStatusLine().getStatusCode());
        } catch (Exception e) {
            // TODO
        }

        return null;
    }

    private static JSONObject simpleRequest(HttpRequestBase request) {
        return executeRequest(request);
    }

    private static JSONObject enclosingRequest(HttpEntityEnclosingRequestBase request, ArrayList<NameValuePair> parameters) {
        HttpEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(parameters, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        request.addHeader(entity.getContentType());
        request.setEntity(entity);

        return executeRequest(request);
    }

    protected static JSONObject get(String uri) {
        return simpleRequest(new HttpGet(uri));
    }

    protected static JSONObject delete(String uri) {
        return simpleRequest(new HttpDelete(uri));
    }

    protected static JSONObject post(String uri,
                                     ArrayList<NameValuePair> parameters) {
        return enclosingRequest(new HttpPost(uri), parameters);
    }

    protected static JSONObject put(String uri,
                                    ArrayList<NameValuePair> parameters) {
        return enclosingRequest(new HttpPut(uri), parameters);
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivity_manager = (ConnectivityManager) BusPhone.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo active_network_info = connectivity_manager.getActiveNetworkInfo();
        return active_network_info != null && active_network_info.isConnected();
    }

    public static void showNoConnectionDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false)
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle("No connection")
                .setMessage("This application requires network access. Enable mobile network or Wi-Fi to download data.")
                .setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                context.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

        builder.create().show();
    }
}
