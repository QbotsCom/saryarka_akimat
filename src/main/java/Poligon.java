import com.turlygazhy.dao.DaoFactory;
import com.turlygazhy.dao.ScriptExecutor;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by user on 1/14/17.
 */
public class Poligon {
    public static void main(String[] args) throws SQLException {
        ScriptExecutor scriptExecutor = DaoFactory.getFactory().getScriptExecutor();
        scriptExecutor.execute("select * from user");
    }
}
