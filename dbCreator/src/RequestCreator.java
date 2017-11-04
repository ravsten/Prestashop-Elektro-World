import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class RequestCreator {

    private String baseUrl;
    private String charset;
    private String contentType;
    private int timeout;
    private Map<String, String> params;

    public RequestCreator(String baseUrl, String charset, String contentType, int timeout) {
        this.baseUrl = baseUrl;
        this.charset = charset;
        this.contentType = contentType;
        this.timeout = timeout;

        params = new HashMap<>();
        params.put("method", "updateProduct");
        params.put("productId", "");
        params.put("attributes", "");
    }

    public RequestResponse getResponse(int id) throws Exception {
        RequestResponse result = null;
        params.replace("productId", String.valueOf(id));
        URL url = UrlGenerator.generate(baseUrl, ParameterBuilder.getParamsString(params));

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Accept-Charset", charset);
        connection.setRequestProperty("Content-Type", contentType);
        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout);
        InputStream response = connection.getInputStream();

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            Scanner scanner = new Scanner(response);
            String responseBody = scanner.useDelimiter("\\A").next();

            if (productWasFound(responseBody)) {
                result = new ObjectMapper().readValue(responseBody, RequestResponse.class);
                System.out.println("Product with id " + id + " is called: " + result.getData().getProduct_name());
            } else {
                System.out.println("Product with id " + id + " has not been found!");
            }
        } else {
            System.out.println("Server responded with status: " + connection.getResponseCode());
        }

        return result;
    }

    private boolean productWasFound(String responseBody) {
        return !responseBody.equals("{\"success\":true,\"data\":[]}");
    }
}
