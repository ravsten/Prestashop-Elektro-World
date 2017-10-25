import java.net.URL;

public class UrlGenerator {
    public static URL generate(String baseUrl, String params) throws Exception{
        String result = baseUrl;

        if (baseUrl.charAt(baseUrl.length() - 1) != '/') {
            result += "/";
        }

        if (!params.isEmpty()) {
            result += "?";
            result += params;
        }

        return new URL(result);
    }
}
