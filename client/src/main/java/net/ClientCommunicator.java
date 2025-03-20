package net;

import com.google.gson.Gson;
import exception.ResponseException;
import result.ErrorResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

// communicates with server based on get and post
public class ClientCommunicator {

    public <T> T doPost(String urlString, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setReadTimeout(5000);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            writeBody(request, connection);
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return readBody(connection, responseClass);
            } else {
                try (InputStream responseError = connection.getErrorStream()) {
                    if (responseError != null) {
                        throw ResponseException.fromJson(responseError);
                    }
                }
                throw new ResponseException(connection.getResponseCode(), "Unknown failure: " + connection.getResponseCode());
            }
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }


    private void writeBody(Object request, HttpURLConnection connection) throws IOException {
         String jsonRequest = new Gson().toJson(request);
        try(OutputStream requestBody = connection.getOutputStream();) {
            requestBody.write(jsonRequest.getBytes());
        }
    }

    private <T> T readBody(HttpURLConnection connection, Class<T> responseClass) throws IOException {
        T response = null;
        if (connection.getContentLength() < 0) {
            try (InputStream responseBody = connection.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(responseBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


}
