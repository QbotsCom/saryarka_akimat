import com.turlygazhy.entity.Ticket;
import com.turlygazhy.google_sheets.SheetsAdapter;

/**
 * Created by user on 1/14/17.
 */
public class Poligon {
    public static void main(String[] args) {
        SheetsAdapter.writeTicket(new Ticket(), 1);
    }
}
