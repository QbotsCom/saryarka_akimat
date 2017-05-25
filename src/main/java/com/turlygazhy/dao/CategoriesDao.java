package com.turlygazhy.dao;

import com.turlygazhy.command.impl.work_around.entity.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by user on 4/26/17.
 */
public class CategoriesDao {
    public static final int IS_MAIN_COLUMN_INDEX = 3;
    public static final int ID_COLUMN_INDEX = 1;
    public static final int NAME_COLUMN_INDEX = 2;
    public static final int CHAILD_IDS_COLUMN_INDEX = 4;
    public static final int EXECUTORS_IDS_COLUMN_INDEX = 5;
    public static final int AFTER_TEXT_COLUMN_INDEX = 6;
    public static final int DEADLINE_COLUMN_INDEX = 7;
    private final Connection connection;

    public CategoriesDao(Connection connection) {
        this.connection = connection;
    }

    public List<Category> selectAll() throws SQLException {
        List<Category> result = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM CATEGORIES");
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()) {
            boolean main = rs.getBoolean(IS_MAIN_COLUMN_INDEX);
            if (main) {
                result.add(select(rs.getInt(ID_COLUMN_INDEX)));
            }
        }
        Collections.sort(result, new Comparator<Category>() {
            @Override
            public int compare(Category o1, Category o2) {
                return o1.getId() - o2.getId();
            }
        });
        return result;
    }

    public Category select(int id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM CATEGORIES where id=?");
        ps.setInt(1, id);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        rs.next();
        Category category = new Category();
        category.setId(rs.getInt(ID_COLUMN_INDEX));
        category.setName(rs.getString(NAME_COLUMN_INDEX));
        category.setMain(rs.getBoolean(IS_MAIN_COLUMN_INDEX));
        String childIds = rs.getString(CHAILD_IDS_COLUMN_INDEX);
        category.setExecutorsIds(rs.getString(EXECUTORS_IDS_COLUMN_INDEX));
        category.setAfterText(rs.getString(AFTER_TEXT_COLUMN_INDEX));
        category.setDeadline(rs.getString(DEADLINE_COLUMN_INDEX));
        category.setGroupId();//todo
        if (!childIds.equals("0")) {
            String[] child = childIds.split(",");
            List<Category> childs = new ArrayList<>();
            for (String ch : child) {
                childs.add(select(Integer.parseInt(ch)));
            }
            category.setChilds(childs);
        }
        return category;
    }

    public Category select(String name) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM CATEGORIES where name=?");
        ps.setString(1, name);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        rs.next();
        return select(rs.getInt(ID_COLUMN_INDEX));
    }
}
