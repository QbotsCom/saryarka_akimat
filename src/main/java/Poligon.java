import java.sql.SQLException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 1/14/17.
 */
public class Poligon {
    public static void main(String[] args) throws SQLException {
        String input = "18.05.11 15:00";
        Date date = new Date(input);
        System.out.println(date);
    }
}