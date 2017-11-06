public class DatabaseCreator {

    private static final int REQUEST_LIMIT = 50;
    private static final int TIMEOUT = 5000;
    private static final int INITIAL_ID = 1056632; //1050
    // 1056120; //1000
    // 1055957; //950
    // 1055861; //900
    // 1055801; //850
    // 1055447; //800
    // 1055369; //750
    // 1055311; //700
    // 1055153; //650
    // 1055051; //600
    // 1054919; //550
    // 1054700; //500
    // 1054420; //450
    // 1054330; //400
    // 1054250; //350
    // 1054117; //300
    // 1054014; //250
    // 1053921; //200
    // 1053783; //150
    // 1053604; //100
    // 1053493; //50 <- init

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
