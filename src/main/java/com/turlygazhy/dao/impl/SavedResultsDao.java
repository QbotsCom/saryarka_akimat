package com.turlygazhy.dao.impl;

import com.turlygazhy.entity.SavedResult;
import com.turlygazhy.entity.UserReadingResult;
import com.turlygazhy.entity.UserResult;
import com.turlygazhy.tool.DateUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Yerassyl_Turlygazhy on 03-Mar-17.
 */
public class SavedResultsDao {
    private final Connection connection;
    private SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy");


    public SavedResultsDao(Connection connection) {
        this.connection = connection;
    }

    public List<SavedResult> select(Integer userId, Date dateFrom, Date dateTill) throws SQLException {
        List<SavedResult> results = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM SAVED_RESULTS where user_id=?");
        ps.setInt(1, userId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()) {
            Date dbFrom = convertDBDate(rs.getString(3));
            boolean checkFromDate = isFirstGreaterOrEqual(dbFrom, dateFrom);
            boolean checkTillDate = isFirstGreaterOrEqual(dateTill, dbFrom);
            if (checkFromDate && checkTillDate) {
                SavedResult savedResult = new SavedResult();
                savedResult.setId(rs.getInt(1));
                savedResult.setUserId(userId);
                savedResult.setDate(dbFrom);
                savedResult.setResult(rs.getInt(4));
                savedResult.setGoalId(rs.getInt(5));
                results.add(savedResult);
            }
        }

        Collections.sort(results, new Comparator<SavedResult>() {
            @Override
            public int compare(SavedResult first, SavedResult second) {
                Date firstDate = first.getDate();
                Date secondDate = second.getDate();
                int yearDifference = firstDate.getYear() - secondDate.getYear();
                if (yearDifference != 0) {
                    return yearDifference;
                } else {
                    int monthDifference = firstDate.getMonth() - secondDate.getMonth();
                    if (monthDifference != 0) {
                        return monthDifference;
                    } else {
                        return firstDate.getDate() - secondDate.getDate();
                    }
                }
            }
        });

        return results;
    }

    private boolean isFirstGreaterOrEqual(Date first, Date second) {
        if (first.getYear() > second.getYear()) {
            return true;
        } else {
            if (first.getYear() == second.getYear()) {
                if (first.getMonth() > second.getMonth()) {
                    return true;
                } else {
                    if (first.getMonth() == second.getMonth()) {
                        if (first.getDate() >= second.getDate()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private Date convertDBDate(String date) {
        try {
            return format.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public void insert(Integer userId, List<UserResult> results, UserReadingResult reading) throws SQLException {
        List<SavedResult> alreadyCounted = selectThisWeek(userId);
        PreparedStatement ps = connection.prepareStatement("INSERT INTO SAVED_RESULTS VALUES(default, ?, ?, ?, ?)");
        ps.setInt(1, userId);
        ps.setString(2, getPastDay());
        for (UserResult result : results) {
            int completed = result.getCompleted();
            int goalId = result.getGoalId();
            int counted = 0;
            for (SavedResult savedResult : alreadyCounted) {
                if (savedResult.getGoalId() == goalId) {
                    counted = counted + savedResult.getResult();
                }
            }
            ps.setInt(3, completed - counted);
            ps.setInt(4, goalId);
            ps.execute();
        }
        insertReading(userId, reading);
    }

    public List<SavedResult> selectThisWeek(Integer userId) throws SQLException {
        return select(userId, DateUtil.getThisMonday(), DateUtil.getThisSunday());
    }

    private void insertReading(Integer userId, UserReadingResult reading) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO SAVED_RESULTS_READING VALUES(default, ?, ?, ?)");
        ps.setInt(1, userId);
        ps.setString(2, getPastDay());
        ps.setInt(3, reading.getCompleted());
        ps.execute();
    }

    private String getPastDay() {
        Date date = new Date();
        date.setDate(date.getDate() - 1);
        return format.format(date);
    }

    public List<SavedResult> selectThisWeekForReading(Integer userId) throws SQLException {
        return selectForReading(userId, DateUtil.getThisMonday(), DateUtil.getThisSunday());
    }

    public List<SavedResult> selectForReading(Integer userId, Date dateFrom, Date dateTill) throws SQLException {
        List<SavedResult> results = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM SAVED_RESULTS_READING where user_id=?");
        ps.setInt(1, userId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()) {
            Date dbFrom = convertDBDate(rs.getString(3));
            boolean checkFromDate = isFirstGreaterOrEqual(dbFrom, dateFrom);
            boolean checkTillDate = isFirstGreaterOrEqual(dateTill, dbFrom);
            if (checkFromDate && checkTillDate) {
                SavedResult savedResult = new SavedResult();
                savedResult.setId(rs.getInt(1));
                savedResult.setUserId(userId);
                savedResult.setDate(dbFrom);
                savedResult.setResult(rs.getInt(4));
                results.add(savedResult);
            }
        }

        Collections.sort(results, new Comparator<SavedResult>() {
            @Override
            public int compare(SavedResult first, SavedResult second) {
                Date firstDate = first.getDate();
                Date secondDate = second.getDate();
                int yearDifference = firstDate.getYear() - secondDate.getYear();
                if (yearDifference != 0) {
                    return yearDifference;
                } else {
                    int monthDifference = firstDate.getMonth() - secondDate.getMonth();
                    if (monthDifference != 0) {
                        return monthDifference;
                    } else {
                        return firstDate.getDate() - secondDate.getDate();
                    }
                }
            }
        });

        return results;
    }
}
