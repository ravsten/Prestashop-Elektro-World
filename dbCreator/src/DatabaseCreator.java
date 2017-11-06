public class DatabaseCreator {

    private static final int REQUEST_LIMIT = 1200;
    private static final int TIMEOUT = 5000;
    private static final int INITIAL_ID = 1053493; //1050

    public static void main(String[] args) throws Exception {

        RequestCreator creator = new RequestCreator(
                "http://euro_com_pl_api.frosmo.com",
                "UTF-8",
                "application/json",
                TIMEOUT);

        int counter = 0, i = 0;
        while(counter < REQUEST_LIMIT){
            if (creator.getResponse(INITIAL_ID + i) != null) {
                counter++;
                System.out.println(counter + "/" + REQUEST_LIMIT + " done!");
            }
            i++;
        }
        creator.finishSaving();

        System.out.println(counter + "/" + REQUEST_LIMIT + " products has been properly found!");
    }
}
