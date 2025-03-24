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

    public <T> T doPost(String urlString, Object request, Class<T> responseClass, String authToken) throws ResponseException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            connection.setReadTimeout(5000);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            if (authToken!=null) {
                connection.addRequestProperty("authorization", authToken);
            }
            writeBody(request, connection);

            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return readBody(connection, responseClass);
            } else if (connection.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
                throw new ResponseException(400, "Error: bad request");
            } else if (connection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                throw new ResponseException(401, "Error: no existing user or wrong password");
            } else {
                throw new ResponseException(connection.getResponseCode(), "Unknown failure: " + connection.getResponseCode());
            }
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        } finally {
            if (connection!=null) {
                connection.disconnect();
            }
        }
    }

    public void doDelete(String urlString, String authToken) throws ResponseException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            connection.setReadTimeout(5000);
            connection.setRequestMethod("DELETE");
            connection.setDoOutput(true);

            if (authToken!=null) {
                connection.addRequestProperty("authorization", authToken);
            }
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new ResponseException(401, "Error: unauthorized");
            }

        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        } finally {
            if (connection!=null) {
                connection.disconnect();
            }
        }
    }

    public <T> T doGet(String urlString, Class<T> responseClass, String authToken) throws ResponseException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(5000);
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            if (authToken!=null) {
                connection.addRequestProperty("authorization", authToken);
            }
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new ResponseException(401, "Error: unauthorized");
            }
            return readBody(connection, responseClass);
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        } finally {
            if (connection!=null) {
                connection.disconnect();
            }
        }
    }

    public void doPut(String urlString, Object request, String authToken) throws ResponseException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(5000);
            connection.setRequestMethod("PUT");
            connection.setDoOutput(true);
            if (authToken!=null) {
                connection.addRequestProperty("authorization", authToken);
            }
            writeBody(request, connection);
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                if (connection.getResponseCode()==HttpURLConnection.HTTP_UNAUTHORIZED) {
                    throw new ResponseException(401, "Error: unauthorized");
                } else if (connection.getResponseCode()==HttpURLConnection.HTTP_FORBIDDEN) {
                    throw new ResponseException(403, "Error: already taken");
                } else if (connection.getResponseCode()==HttpURLConnection.HTTP_BAD_REQUEST) {
                    throw new ResponseException(400, "Error: bad request");
                }
                System.out.println(connection.getResponseCode());
                try (InputStream responseError = connection.getErrorStream()) {
                    if (responseError != null) {
                        throw ResponseException.fromJson(responseError);
                    }
                }
            }

        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        } finally {
            if (connection!=null) {
                connection.disconnect();
            }
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
