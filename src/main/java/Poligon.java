import com.turlygazhy.command.impl.work_around.entity.Category;
import com.turlygazhy.dao.CategoriesDao;
import com.turlygazhy.dao.DaoFactory;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by user on 1/14/17.
 */
public class Poligon {
    private static final int BOT_MOTHER_ID = 0;
    private static final long YERASSYL_CHAT_ID = 293188753L;

    public static void main(String[] args) throws SQLException {
        /*setChatId VAR set value='7=КСК;7.1=Водо-, тепло-, электро-, газоснабжение;7.2=Лифт;7.3=Ремонт и содержание домов;7.4=Жалобы на КСК;8=Благоустройство дворов;8.1=Заявка на благоустройство;8.2=Ремонт детских площадок;8.3=Открытые колодцы;9=Благоустройство скверов;10=Уборка;10.1=Уборка дворовой территорий;10.2=Уборка улиц и проспектов;10.3=Уборка скверов;11=Общественный транспорт;11.1=Остановки;11.2=Маршруты;11.3=Жалобы;12=Правопорядок;13=Дороги;14=Тротуар;15=Освещение;16=Паводки;16.1=Откача воды;16.1.1=Частный сектор;16.1.2=Внутриквартальные;16.2=Дресва;16.2=Вывоз снега;16.2.1=Частный сектор;16.2.2=Внутриквартальные;17=Дератизация и дезинсекция;17.1=Жилые дома;17.2=Территория района;18=Ремонт тротуаров, фонтанов и подземных переходов;19=Инфраструктура;19.1=Строительные объекты;19.2=Самозахват земельных участков;19.3=Дачные общества;' where id=1*/
        CategoriesDao categoriesDao = DaoFactory.getFactory().getCategoriesDao();
        List<Category> categories = categoriesDao.selectAll();
    }
}
