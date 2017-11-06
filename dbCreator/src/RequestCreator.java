import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.apache.commons.lang.StringEscapeUtils.unescapeHtml;


public class RequestCreator {

    private String baseUrl;
    private String charset;
    private String contentType;
    private String prodFilename, parentCatFilename, catFilename;
    private int timeout;
    private Map<String, String> params;
    private File prodFile, parentCatFile, catFile;
    private ArrayList<String> parentCategories, categories;

    public RequestCreator(String baseUrl, String charset, String contentType, int timeout) {
        this.baseUrl = baseUrl;
        this.charset = charset;
        this.contentType = contentType;
        this.timeout = timeout;

        this.prodFilename = "csv/prod.csv";
        this.parentCatFilename = "csv/parentCat.csv";
        this.catFilename = "csv/cat.csv";

        this.prodFile = new File(prodFilename,true);
        this.parentCatFile = new File(parentCatFilename,true);
        this.catFile = new File (catFilename, true);

        categories = new ArrayList<>();
        parentCategories = new ArrayList<>();

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

                System.out.println("Id: " + id +
                        "; Nazwa: " + result.getData().getProduct_name() +
                        "; Cena: "+ result.getData().getPrice() +
                        "; URL: "+ result.getData().getImage()
                );
                saveCategories(result);
                if(saveProducts(result) == false) return null;
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

    public void saveCategories(RequestResponse result) throws IOException {
        String parentCat = result.getData().getCategory();
        switch(parentCat){
            case "TV, Audio i Video": parentCat = "Telewizory i RTV"; break;
            case "Telefony i zegarki": parentCat = "Telefony, GPS i zegarki"; break;
            case "Telefony i GPS": parentCat = "Telefony, GPS i zegarki"; break;
        }
        if (!parentCat.contains("AGD")
                && !parentCat.equals("Narzędzia")) {
            String cat = result.getData().getProduct_type();
            if (!parentCategories.contains(parentCat) && parentCat != null) {
                parentCategories.add(parentCat);
                parentCatFile.saveToFile(parentCat);
            }

            switch(cat){
                case "komputer all-in-one": cat = "Komputery All-In-One"; break;
                case "urządzenie wielofunkcyjne": cat = "Urządzenia wielofunkcyjne"; break;
                case "telefon komórkowy": cat = "Telefony i smartfony"; break;
                case "kamera sportowa": cat = "Kamery sportowe"; break;
                case "etui dedykowane": cat = "Etui do telefonów"; break;
            }
            if (!categories.contains(cat) && cat != null) {
                categories.add(cat);
                catFile.saveToFile(
                        cat + ';' +
                                parentCat
                );
            }
        }
    }

    public String parseFeatures(RequestResponse result) {
        String info = result.getData().getInfo();
        String finalString = "";
        String[] features;
        if (info != null && info != "") {
            info = info
                    .replace("[", "")
                    .replace("]", "");

            features = info.split("\",\"");

            for (int i = 0; i < features.length; i++) {
                features[i] = features[i]
                        .replace("\"", "")
                        .replace(":::", ":");
                features[i] += ":" + i + " | ";
                finalString += features[i];
            }
        }
        return finalString;
    }

    public boolean saveProducts(RequestResponse result) throws IOException {
        boolean productSaved = false;
        if(result.getData().getImage().startsWith("//f")) {
            StringBuilder newUrl = new StringBuilder();
            newUrl.append("http:");
            newUrl.append(result.getData().getImage());
            result.getData().setImage(newUrl.toString());
        } else if (result.getData().getImage().startsWith("/foto")) {
            StringBuilder newUrl = new StringBuilder();
            newUrl.append("http://f01.esfr.pl");
            newUrl.append(result.getData().getImage());
            result.getData().setImage(newUrl.toString());
        }

        if (!result.getData().getCategory().contains("AGD")
                && !result.getData().getCategory().equals("Narzędzia")
                && !result.getData().getPrice().equals("0.00")) {
            switch(result.getData().getCategory()){
                case "TV, Audio i Video": result.getData().setCategory("Telewizory i RTV"); break;
                case "Telefony i zegarki": result.getData().setCategory("Telefony, GPS i zegarki"); break;
                case "Telefony i GPS": result.getData().setCategory("Telefony, GPS i zegarki"); break;
            }
            switch(result.getData().getProduct_type()){
                case "komputer all-in-one": result.getData().setProduct_type("Komputery All-In-One"); break;
                case "urządzenie wielofunkcyjne": result.getData().setProduct_type("Urządzenia wielofunkcyjne"); break;
                case "telefon komórkowy": result.getData().setProduct_type("Telefony i smartfony"); break;
                case "kamera sportowa": result.getData().setProduct_type("Kamery sportowe"); break;
                case "etui dedykowane": result.getData().setProduct_type("Etui do telefonów"); break;
            }
            prodFile.saveToFile(unescapeHtml(
                    result.getData().getProduct_id_string() + ';' +
                            result.getData().getProduct_type() + ';' + //K - kategoria
                            result.getData().getPrice() + ';' + //L - cena
                            //result.getData().getCategoryName() + ';' + //R - opis
                            //result.getData().getBrandName() + ';' + //W - producent
                            result.getData().getProduct_name() + ';' + //AI - nazwa produktu
                            result.getData().getImage() + ';' + //AL - url do zdjecia
                            //result.getData().getCategory() + ';' + //AO - nadrzedna kategoria
                            result.getData().getProduct_id() + ';' + //AR - ilosc?
                            parseFeatures(result) + ';' //cechy
            ));
            productSaved = true;
        }
        return productSaved;
    }

    public void finishSaving() {
        try {
            parentCatFile.closeFile();
            catFile.closeFile();
            prodFile.closeFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
