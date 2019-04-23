package com.BookingGoRepo.RestApp;

import java.util.*;

import org.json.*;

/**
 * This class is responsible for making the
 * interactive console that is used to order taxis
 *
 * @author Mufid Alkhaddour
 */


public class BookingConsole {

    private HashMap<String, Integer> daveMap = new HashMap<>();
    private HashMap<String, Integer> ericMap = new HashMap<>();
    private HashMap<String, Integer> jeffMap = new HashMap<>();


    public BookingConsole() {
    }

    /**
     * initiates the console and gives the
     * initial options for the user
     */
    public void startConsole() {
        boolean finished = false;
        while (!finished) {
            Scanner scan = new Scanner(System.in);
            System.out.println("Choose option number:");
            System.out.println("1.Order taxi by using dave's taxis");
            System.out.println("2.Order taxi with filtering by number of passengers");
            System.out.println("3.Quit");
            String input = scan.nextLine();

            switch (input) {
                case "1":
                    generateConsoleDave(); // method for the simple part in part 1
                    break;
                case "2":
                    generateConsole(); // method for complete part 1
                    break;
                case "3":
                    System.out.println("complete");
                    finished = true; // stops the console
                    break;
                default:
                    System.out.println("invalid input");
            }

        }
    }

    /**
     * returns the get request for dave's taxis only
     */
    private void generateConsoleDave() {
        System.out.println("Input your pick up location");
        System.out.println("Input a latitude");
        String latitudePick = inputLatitude();
        System.out.println("Input a longitude");
        String longitudePick = inputLongitude();
        System.out.println("Input your drop off location");
        System.out.println("Input a latitude");
        String latitudeDrop = inputLatitude();
        System.out.println("Input a longitude");
        String longitudeDrop = inputLongitude();


        try {
            JSONObject obj = new JSONObject(HttpsRequest.sendGET("https://techtest.rideways.com/dave?pickup=" + latitudePick + "," + longitudePick + "&dropoff=" + latitudeDrop + "," + longitudeDrop).toString()); //gets the json
            JSONArray arr = obj.getJSONArray("options");
            JSONArray sortedArr = jsonSortedDescending(arr);
            for (int i = 0; i < sortedArr.length(); i++) {
                String carType = sortedArr.getJSONObject(i).getString("car_type");
                System.out.println(carType + " - " + sortedArr.getJSONObject(i).getString("price")); //prints out the correct format
            }

        } catch (NullPointerException e) {
            System.out.println("Null pointer");
        } catch (JSONException e) {
            System.out.println("JSON exception");
        }
    }

    /**
     * returns the get request for all taxis in the right format
     */
    private void generateConsole() {
        System.out.println("Input your pick up location");
        System.out.println("Input a latitude");
        String latitudePick = inputLatitude();
        System.out.println("Input a longitude");
        String longitudePick = inputLongitude();
        System.out.println("Input your drop off location");
        System.out.println("Input a latitude");
        String latitudeDrop = inputLatitude();
        System.out.println("Input a longitude");
        String longitudeDrop = inputLongitude();

        String numOfPass = inputPassengers(); // the number of passengers for car ride
        generateAnswer(latitudePick, longitudePick, latitudeDrop, longitudeDrop, numOfPass, true);


    }


    /**
     * Either returns prints to the console or a string to be displayed on the api.
     * @param latitudePick pick up latitude
     * @param longitudePick pick up longitude
     * @param latitudeDrop drop off latitude
     * @param longitudeDrop drop off longitude
     * @param numOfPassStr the number of passengers
     * @param shouldPrint whether the method would be used for the console or the api
     * @return
     */
    public String generateAnswer(String latitudePick, String longitudePick, String latitudeDrop, String longitudeDrop, String numOfPassStr, boolean shouldPrint) {
        String answer = ""; // the string to be returned for the api
        int numOfPass;
        try {
            numOfPass = Integer.parseInt(numOfPassStr);

        } catch (NumberFormatException e) {
            answer += "Invalid number of passengers, please input an integer";
            return answer;
        }

        if (!shouldPrint) {
            if (numOfPass < 1) {
                answer += "input a number of passengers greater than 0";
                return answer;
            }
            if (!checkIfDouble(latitudePick, 90)) {
                answer += "your latitude input should be a double between -" + 90 + " and " + 90 + " please try again";
                return answer;
            }
            if (!checkIfDouble(longitudePick, 180)) {
                answer += "your longitude input should be a double between -" + 180 + " and " + 180 + " please try again";
                return answer;
            }
            if (!checkIfDouble(latitudeDrop, 90)) {
                answer += "your latitude input should be a double between -" + 90 + " and " + 90 + " please try again";
                return answer;
            }
            if (!checkIfDouble(longitudeDrop, 180)) {
                answer += "your longitude input should be a double between -" + 180 + " and " + 180 + " please try again";
                return answer;
            }

        }


        try {
            JSONObject dave = new JSONObject();
            JSONObject eric = new JSONObject();
            JSONObject jeff = new JSONObject();
            try {
                dave = new JSONObject(HttpsRequest.sendGET("https://techtest.rideways.com/dave?pickup=" + latitudePick + "," + longitudePick + "&dropoff=" + latitudeDrop + "," + longitudeDrop).toString());
            } catch (NullPointerException e) { // so that the program continues even if one request fails
                if (shouldPrint) {
                    System.out.println("while retrieving from Dave's taxis");
                }

                dave.put("supplier_id", "DAVE"); // fill the null object with template values
                JSONArray empty = new JSONArray();
                dave.put("options", empty);
            }

            try {
                eric = new JSONObject(HttpsRequest.sendGET("https://techtest.rideways.com/dave?pickup=" + latitudePick + "," + longitudePick + "&dropoff=" + latitudeDrop + "," + longitudeDrop).toString());
            } catch (NullPointerException e) {
                if (shouldPrint) {
                    System.out.println("while retrieving from Eric's taxis");
                }
                eric.put("supplier_id", "ERIC");
                JSONArray empty = new JSONArray();
                eric.put("options", empty);
            }

            try {
                jeff = new JSONObject(HttpsRequest.sendGET("https://techtest.rideways.com/dave?pickup=" + latitudePick + "," + longitudePick + "&dropoff=" + latitudeDrop + "," + longitudeDrop).toString());
            } catch (NullPointerException e) {
                if (shouldPrint) {
                    System.out.println("while retrieving from Jeff's taxis");
                }
                jeff.put("supplier_id", "JEFF");
                JSONArray empty = new JSONArray();
                jeff.put("options", empty);
            }

            JSONArray daveArr = dave.getJSONArray("options"); // gets the array of cars available
            JSONArray ericArr = eric.getJSONArray("options");
            JSONArray jeffArr = jeff.getJSONArray("options");

            fillMap(daveMap, daveArr);
            fillMap(ericMap, ericArr);
            fillMap(jeffMap, jeffArr);

            JSONArray arr = new JSONArray(); // the final array which will have the cheapest car from each

            checkMap(daveMap);
            checkMap(ericMap);
            checkMap(jeffMap);

            addCheapest(arr, "STANDARD");
            addCheapest(arr, "EXECUTIVE");
            addCheapest(arr, "LUXURY");
            addCheapest(arr, "PEOPLE_CARRIER");
            addCheapest(arr, "LUXURY_PEOPLE_CARRIER");
            addCheapest(arr, "MINIBUS");

            JSONArray sortedArr = jsonSortedDescending(arr);
            boolean carFound = false; // checks whether there is something to return
            for (int i = 0; i < sortedArr.length(); i++) {
                if (numOfPass > 16) {
                    if (shouldPrint) {
                        System.out.println("we do not have any cars that fit more than 16 people");
                    } else {
                        answer += "we do not have any cars that fit more than 16 people";
                    }
                    carFound = true;
                    break;
                }
                String carType = sortedArr.getJSONObject(i).getString("car_type");
                String carInfo = carType + " - " + sortedArr.getJSONObject(i).getString("supplier") + " - " + sortedArr.getJSONObject(i).getString("price");
                if (numOfPass > 6 && numOfPass < 17 && carType.equals("MINIBUS")) {
                    if (shouldPrint) {
                        System.out.println(carInfo);
                    } else {
                        answer += (carInfo + "<br>");
                    }
                    carFound = true;
                } else if (numOfPass > 4 && numOfPass < 7 && (carType.equals("MINIBUS") || carType.equals("LUXURY_PEOPLE_CARRIER") || carType.equals("PEOPLE_CARRIER"))) {
                    if (shouldPrint) {
                        System.out.println(carInfo);
                    } else {
                        answer += (carInfo + "<br>");
                    }
                    carFound = true;
                } else if (numOfPass < 5) {
                    if (shouldPrint) {
                        System.out.println(carInfo);
                    } else {
                        answer += (carInfo + "<br>");
                    }
                    carFound = true;
                }

            }
            if (!carFound) {
                if (shouldPrint) {
                    System.out.println("Sorry no cars available at the moment");
                } else {
                    answer += "Sorry no cars available at the moment";
                }
            }
            return answer;


        } catch (JSONException e) {
            System.out.println("JSON exception");
            return answer;
        }
    }

    /**
     * sorts the array according to price
     *
     * @param unsorted unsorted array to be sorted
     * @return array sorted descending
     */
    private JSONArray jsonSortedDescending(JSONArray unsorted) {
        JSONArray sortedJsonArray = new JSONArray();

        List<JSONObject> jsonValues = new ArrayList<JSONObject>();
        for (int i = 0; i < unsorted.length(); i++) {
            try {
                jsonValues.add(unsorted.getJSONObject(i));
            } catch (JSONException e) {
                System.out.println("JSON exception");
            }
        }
        Collections.sort(jsonValues, new Comparator<JSONObject>() {
            private static final String KEY_NAME = "price";

            @Override
            public int compare(JSONObject a, JSONObject b) {
                Integer valA = 0;
                Integer valB = 0;

                try {
                    valA = (Integer) a.get(KEY_NAME);
                    valB = (Integer) b.get(KEY_NAME);
                } catch (JSONException e) {
                    System.out.println("JSON exception");
                }

                return -valA.compareTo(valB); // descending
            }
        });

        for (int i = 0; i < unsorted.length(); i++) {
            sortedJsonArray.put(jsonValues.get(i));
        }

        return sortedJsonArray;
    }

    /**
     * checks if an input is a double
     *
     * @param input input from the console
     * @return whether the input is a double
     */
    public boolean checkIfDouble(String input, int maxDouble) {
        try {
            Double.parseDouble(input);
        } catch (NumberFormatException ex) {
            System.out.println("invalid input, please input a number.");
            return false;
        }
        if (Double.parseDouble(input) > maxDouble || Double.parseDouble(input) < -maxDouble) {
            System.out.println("your input should be a double between -" + maxDouble + " and " + maxDouble + " please try again");
            return false;
        }
        return true;

    }

    /**
     * checks if an input is an int
     *
     * @param input input from the console
     * @return whether the input is an int
     */
    public boolean checkIfInt(String input) {
        try {
            Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;

    }

    /**
     * allows the user to input the number of passengers
     *
     * @return the number of passengers inputed
     */
    private String inputPassengers() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Input number of passengers");
        String passNum = scan.nextLine();
        while (!checkIfInt(passNum) || Integer.parseInt(passNum) <= 0) {
            System.out.println("invalid input, please input a number.");
            passNum = scan.nextLine();
        }
        return passNum;
    }

    /**
     * allows the user to input a latitude
     *
     * @return the location
     */
    public String inputLatitude() {
        Scanner scan = new Scanner(System.in);

        String location = scan.nextLine();
        while (!checkIfDouble(location, 90)) {
            location = scan.nextLine();
        }
        return location;
    }

    /**
     * allows the user to input a location
     *
     * @return the location
     */
    public String inputLongitude() {
        Scanner scan = new Scanner(System.in);

        String location = scan.nextLine();
        while (!checkIfDouble(location, 180)) {
            location = scan.nextLine();
        }
        return location;
    }

    /**
     * fills the map with whatever is in the JSONArray
     *
     * @param map map to be filled in
     * @param arr array with all cars and prices
     */
    public void fillMap(HashMap<String, Integer> map, JSONArray arr) {
        try {
            for (int i = 0; i < arr.length(); i++) {
                String carType = arr.getJSONObject(i).getString("car_type");
                Integer price = Integer.parseInt(arr.getJSONObject(i).getString("price"));
                map.put(carType, price);
            }
        } catch (JSONException e) {
            System.out.println("JSON exception");
        }

    }

    /**
     * pads the maps with car types that are not already there with prices of max int
     *
     * @param map the map to fill
     */
    public void checkMap(HashMap<String, Integer> map) {
        if (map.get("STANDARD") == null) {
            map.put("STANDARD", Integer.MAX_VALUE);
        }
        if (map.get("EXECUTIVE") == null) {
            map.put("EXECUTIVE", Integer.MAX_VALUE);
        }
        if (map.get("LUXURY") == null) {
            map.put("LUXURY", Integer.MAX_VALUE);
        }
        if (map.get("PEOPLE_CARRIER") == null) {
            map.put("PEOPLE_CARRIER", Integer.MAX_VALUE);
        }
        if (map.get("LUXURY_PEOPLE_CARRIER") == null) {
            map.put("LUXURY_PEOPLE_CARRIER", Integer.MAX_VALUE);
        }
        if (map.get("MINIBUS") == null) {
            map.put("MINIBUS", Integer.MAX_VALUE);
        }
    }

    /**
     * adds the cheapest car from each supplier to the final array
     *
     * @param arr array to add cars to
     * @param car the car type to add
     */
    public void addCheapest(JSONArray arr, String car) {
        try {
            if (daveMap.get(car) < ericMap.get(car)) {
                if (daveMap.get(car) < jeffMap.get(car)) {
                    JSONObject list1 = new JSONObject();
                    list1.put("car_type", car);
                    list1.put("supplier", "dave");
                    list1.put("price", daveMap.get(car));
                    arr.put(list1);
                } else {
                    if (jeffMap.get(car) != Integer.MAX_VALUE) {
                        JSONObject list1 = new JSONObject();
                        list1.put("car_type", car);
                        list1.put("supplier", "jeff");
                        list1.put("price", jeffMap.get(car));
                        arr.put(list1);
                    }
                }
            } else {
                if (ericMap.get(car) < jeffMap.get(car)) {
                    JSONObject list1 = new JSONObject();
                    list1.put("car_type", car);
                    list1.put("supplier", "eric");
                    list1.put("price", ericMap.get(car));
                    arr.put(list1);
                } else {
                    if (jeffMap.get(car) != Integer.MAX_VALUE) {
                        JSONObject list1 = new JSONObject();
                        list1.put("car_type", car);
                        list1.put("supplier", "jeff");
                        list1.put("price", jeffMap.get(car));
                        arr.put(list1);
                    }
                }
            }

        } catch (JSONException e1) {
            System.out.println("Json exception");
        }
    }
}