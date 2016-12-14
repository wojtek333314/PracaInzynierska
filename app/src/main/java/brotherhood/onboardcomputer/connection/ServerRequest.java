package brotherhood.onboardcomputer.connection;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import brotherhood.onboardcomputer.connection.enums.ServiceType;
import brotherhood.onboardcomputer.connection.parameters.Parameters;


public class ServerRequest extends AsyncTask<Void, Void, String> {
    private static final int CONNECTION_TIMEOUT = 10000;
    private static final String CODING = "UTF-8";
    private ServiceType serviceType;
    private Parameters parameters;
    private ServerRequestListener serverRequestListener;
    private Type type = Type.POST;

    public ServerRequest(ServiceType serviceType, Parameters parameters) {
        this.serviceType = serviceType;
        this.parameters = parameters;
    }

    public ServerRequest(ServiceType serviceType) {
        this.serviceType = serviceType;
        this.parameters = null;
    }

    public ServerRequest setServerRequestListener(ServerRequestListener serverRequestListener) {
        this.serverRequestListener = serverRequestListener;
        return this;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(Void... urls) {

        try {
            HashMap<String, String> params = new HashMap<>();
            String stringUrl = ServiceType.getURL(serviceType);
            if (type == Type.GET) {
                if (parameters != null) {
                    for (String key : parameters.getParameters().keySet()) {
                        params.put(key, parameters.getParameters().get(key));
                    }
                }
                stringUrl += "&" + getQuery(params);
            }
            System.out.println("Calling url: " + stringUrl);
            URL url = new URL(stringUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(type.name().toUpperCase());
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            connection.setRequestProperty("Accept", "*/*");
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            if (type == Type.POST) {
                connection.setDoOutput(true);
                connection.setDoInput(true);
                if (parameters != null) {
                    for (String key : parameters.getParameters().keySet()) {
                        params.put(key, parameters.getParameters().get(key));
                    }
                }
                System.out.println("POST PARAMS:" + getQuery(params));
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, CODING));
                writer.write(getQuery(params));
                writer.flush();
                writer.close();
                os.close();
            }
            int status = connection.getResponseCode();
            System.out.println("Response code:" + status);
            if (status >= 400 && status < 600) {
                InputStream in = connection.getErrorStream();
                return streamToString(in);
            } else {
                InputStream in = connection.getInputStream();
                return streamToString(in);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }


    private String getQuery(HashMap<String, String> params) {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (String key : params.keySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            try {
                result.append(URLEncoder.encode(key, CODING));
                result.append("=");
                result.append(URLEncoder.encode(params.get(key), CODING));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return result.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        if (result == null) {
            Log.e("ServerRequest", "PROBABLY YOU WANT TO CALL URL THAT DOESN'T EXIST!");
            if (serverRequestListener != null) {
                serverRequestListener.onError(500, "something goes wrong. Result is null!");
            }
        } else {
            if (serverRequestListener != null) {
                serverRequestListener.onSuccess(result);
            }
        }
    }

    public static String streamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    public ServerRequest setType(Type type) {
        this.type = type;
        return this;
    }

    public interface ServerRequestListener {
        void onSuccess(String json);

        void onError(int code, String description);
    }

    public enum Type {
        GET,
        POST
    }
}
