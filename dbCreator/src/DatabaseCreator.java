public class DatabaseCreator {

    private static final int REQUEST_LIMIT = 100;
    private static final int TIMEOUT = 5000;
    private static final int INITIAL_ID = 1087360;

    public static void main(String[] args) throws Exception{

        RequestCreator creator = new RequestCreator(
                "http://euro_com_pl_api.frosmo.com", "UTF-8",
                "application/json", TIMEOUT);

        int counter = 0;

        for (int i = 0; i < REQUEST_LIMIT; i++) {
            if (creator.getResponse(INITIAL_ID + i) != null) {
                counter++;
            }
        }

        System.out.println(counter + "/" + REQUEST_LIMIT + " products has been properly found!");
    }
}
