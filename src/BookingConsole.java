import java.sql.SQLOutput;
import java.util.*;

import org.json.*;

public class BookingConsole {

    public BookingConsole(){}

    public void startConsole(){
        boolean finished = false;
        while (!finished){
            Scanner scan = new Scanner(System.in);
            System.out.println("Choose option number:");
            System.out.println("1.Order taxi");
            System.out.println("2.Quit");
            String input = scan.nextLine();

            switch (input){
                case "1":
                    inputLocations();
                    break;
                case "2":
                    System.out.println("complete");

                    finished = true;
                    break;
                default:
                    System.out.println("invalid input");
            }

        }
    }


    private void inputLocations(){
        Scanner scan = new Scanner(System.in);
        System.out.println("Input your pick up location");
        System.out.println("Input a latitude");
        String latitudePick = scan.nextLine();
        while(!checkIfDouble(latitudePick)){
            System.out.println("invalid input, please input a number.");
            latitudePick= scan.nextLine();
        }
        System.out.println("Input a longitude");
        String longitudePick = scan.nextLine();
        while(!checkIfDouble(longitudePick)){
            System.out.println("invalid input, please input a number.");
            longitudePick= scan.nextLine();
        }

        System.out.println("Input your drop off location");
        System.out.println("Input a latitude");
        String latitudeDrop = scan.nextLine();
        while(!checkIfDouble(latitudeDrop)){
            System.out.println("invalid input, please input a number.");
            latitudeDrop= scan.nextLine();
        }
        System.out.println("Input a longitude");
        String longitudeDrop = scan.nextLine();
        while(!checkIfDouble(longitudeDrop)){
            System.out.println("invalid input, please input a number.");
            longitudeDrop= scan.nextLine();
        }

        int numOfPass = inputPassengers();


        try {
            JSONObject obj = new JSONObject(HttpsRequest.sendGET("https://techtest.rideways.com/dave?pickup=" + latitudePick + "," + longitudePick + "&dropoff=" + latitudeDrop + "," + longitudeDrop).toString());

            JSONArray arr = obj.getJSONArray("options");
            JSONArray sortedArr = jsonSortedDescending(arr);
            boolean carFound = false;
            for (int i = 0; i < sortedArr.length(); i++)
            {
                String carType = sortedArr.getJSONObject(i).getString("car_type");
                if(numOfPass > 6 && carType.equals("MINIBUS")) {
                    System.out.println(carType + " - " + sortedArr.getJSONObject(i).getString("price"));
                    carFound = true;
                }
                else if(numOfPass > 4 && numOfPass < 7 && (carType.equals("MINIBUS") || carType.equals("LUXURY_PEOPLE_CARRIER") || carType.equals("PEOPLE_CARRIER"))){
                    System.out.println(carType + " - " + sortedArr.getJSONObject(i).getString("price"));
                    carFound = true;
                }
                else if(numOfPass < 5){
                    System.out.println(carType + " - " + sortedArr.getJSONObject(i).getString("price"));
                    carFound = true;
                }
            }
            if(!carFound) {
                System.out.println("Sorry no cars available at the moment");
            }

        }
        catch (NullPointerException e) {
            System.out.println("Oops, couldn't connect to server.");
//            System.err.println("failed to connect to server, error information: " + e);
        }
        catch (JSONException e){
            System.out.println("JSON exception");
        }
    }

    private JSONArray jsonSortedDescending(JSONArray unsorted){
        JSONArray sortedJsonArray = new JSONArray();

        List<JSONObject> jsonValues = new ArrayList<JSONObject>();
        for (int i = 0; i < unsorted.length(); i++) {
            try {
                jsonValues.add(unsorted.getJSONObject(i));
            }
            catch (JSONException e){
                System.out.println("JSON exception");
            }
        }
        Collections.sort( jsonValues, new Comparator<JSONObject>() {
            //You can change "Name" with "ID" if you want to sort by ID
            private static final String KEY_NAME = "price";

            @Override
            public int compare(JSONObject a, JSONObject b) {
                Integer valA = 0;
                Integer valB = 0;

                try {
                    valA = (Integer) a.get(KEY_NAME);
                    valB = (Integer) b.get(KEY_NAME);
                }
                catch (JSONException e) {
                    System.out.println("JSON exception");
                }

                // return valA.compareTo(valB);
                //if you want to change the sort order, simply use the following:
                return -valA.compareTo(valB);
            }
        });

        for (int i = 0; i < unsorted.length(); i++) {
            //System.out.println(jsonValues.get(i));
            sortedJsonArray.put(jsonValues.get(i));
        }

        return sortedJsonArray;
    }


    public boolean checkIfDouble(String input){
        try{
            Double.parseDouble(input);
        }catch (NumberFormatException ex) {
            return false;
        }
        return true;

    }

    public boolean checkIfInt(String input){
        try{
            Integer.parseInt(input);
        }catch (NumberFormatException ex) {
            return false;
        }
        return true;

    }

    private int inputPassengers(){
        Scanner scan = new Scanner(System.in);
        System.out.println("Input number of passengers");
        String passNum = scan.nextLine();
        while(!checkIfInt(passNum) || Integer.parseInt(passNum) <= 0){
            System.out.println("invalid input, please input a number.");
            passNum= scan.nextLine();
        }
        return Integer.parseInt(passNum);
    }

}