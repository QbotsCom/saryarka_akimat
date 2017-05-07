import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by user on 1/14/17.
 */
public class Poligon {
    public static void main(String[] args) {
        Date x = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy hh:mm");
        System.out.println(format.format(x));
        System.out.println(x);
    }
}
