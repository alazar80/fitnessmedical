package com.example.sql;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VolleyMultipartRequest extends Request<NetworkResponse> {

    private final Map<String, String> headers;
    private final Map<String, String> params;
    private final Map<String, DataPart> byteData;
    private final Response.Listener<NetworkResponse> listener;

    public VolleyMultipartRequest(int method, String url,
                                  Response.Listener<NetworkResponse> listener,
                                  Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = listener;
        this.headers = new HashMap<>();
        this.params = new HashMap<>();
        this.byteData = new HashMap<>();
    }

    public void setParams(Map<String, String> params) {
        this.params.putAll(params);
    }

    public void setByteData(Map<String, DataPart> byteData) {
        this.byteData.putAll(byteData);
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data;boundary=" + boundary;
    }

    @Override
    protected Map<String, String> getParams() {
        return params;
    }

    @Override
    public byte[] getBody() {
        return buildMultipartData();
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        listener.onResponse(response);
    }

    // You must implement this method to build multipart body
    private String boundary = "apiclient-" + System.currentTimeMillis();
    private byte[] buildMultipartData() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                bos.write(("--" + boundary + "\r\n").getBytes());
                bos.write(("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n").getBytes());
                bos.write((entry.getValue() + "\r\n").getBytes());
            }

            for (Map.Entry<String, DataPart> entry : byteData.entrySet()) {
                bos.write(("--" + boundary + "\r\n").getBytes());
                bos.write(("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"; filename=\"" + entry.getValue().getFileName() + "\"\r\n").getBytes());
                bos.write(("Content-Type: " + entry.getValue().getType() + "\r\n\r\n").getBytes());
                bos.write(entry.getValue().getContent());
                bos.write("\r\n".getBytes());
            }

            bos.write(("--" + boundary + "--\r\n").getBytes());
            return bos.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

    public static class DataPart {
        private final String fileName;
        private final byte[] content;
        private final String type;

        public DataPart(String fileName, byte[] content, String type) {
            this.fileName = fileName;
            this.content = content;
            this.type = type;
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getContent() {
            return content;
        }

        public String getType() {
            return type;
        }
    }
}

