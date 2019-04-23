import com.BookingGoRepo.RestApp.BookingConsole;
import org.junit.Test;
import java.util.HashMap;
import org.json.*;
import static org.junit.Assert.*;


/**
 * class to test the functionality of the methods in bookingConsole
 */
public class BookingConsoleTest {

    BookingConsole con = new BookingConsole();

    @Test
    public void testCheckIfDouble(){
        assertTrue(con.checkIfDouble("0", 50));
        assertTrue(con.checkIfDouble("-1", 50));
        assertTrue(con.checkIfDouble("1", 50));
        assertTrue(con.checkIfDouble("0.1", 50));
        assertTrue(con.checkIfDouble("-0.1", 50));

        assertFalse(con.checkIfDouble("aaa", 50));
        assertFalse(con.checkIfDouble(" ", 50));
        assertFalse(con.checkIfDouble("50.01", 50));
        assertFalse(con.checkIfDouble("-50.01", 50));
    }

    @Test
    public void testCheckIfInt(){
        assertTrue(con.checkIfInt("0"));
        assertTrue(con.checkIfInt("-1"));
        assertTrue(con.checkIfInt("1"));

        assertFalse(con.checkIfInt("aaa"));
        assertFalse(con.checkIfInt("-1.1"));
        assertFalse(con.checkIfInt("1.1"));
        assertFalse(con.checkIfInt(" "));
    }

    @Test
    public void testFillMap(){
        HashMap<String, Integer> map = new HashMap<>();
        JSONArray arr = new JSONArray();
        JSONObject list1 = new JSONObject();
        try {
            list1.put("car_type", "STANDARD");
            list1.put("price", "20");
            arr.put(list1);
        }
        catch(JSONException e){
            System.out.println("Json exception");
        }
        con.fillMap(map, arr);
        Integer mapPrice = map.get("STANDARD");
        assertTrue(map.containsKey("STANDARD"));
        assertEquals((Object) 20, (Object) mapPrice);
    }

    @Test
    public void testCheckMap(){
        HashMap<String, Integer> map = new HashMap<>();
        map.put("STANDARD", 20);
        con.checkMap(map);
        assertTrue(map.containsKey("STANDARD"));
        assertTrue(map.containsKey("EXECUTIVE"));
        assertTrue(map.containsKey("LUXURY"));
        assertTrue(map.containsKey("PEOPLE_CARRIER"));
        assertTrue(map.containsKey("LUXURY_PEOPLE_CARRIER"));
        assertTrue(map.containsKey("MINIBUS"));
        assertEquals((Object) 20, (Object) map.get("STANDARD"));
        assertEquals((Object) Integer.MAX_VALUE, (Object) map.get("EXECUTIVE"));
        assertEquals((Object) 6, (Object) map.size());
    }

}