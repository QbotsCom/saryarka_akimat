package com.turlygazhy.google_sheets;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.turlygazhy.dao.DaoFactory;
import com.turlygazhy.dao.VariablesDao;
import com.turlygazhy.entity.Ticket;
import com.turlygazhy.entity.User;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SheetsAdapter {
    private static final String APPLICATION_NAME = "Google spreadsheet";
    private static final JsonFactory JSON_FACTORY = new GsonFactory();
    private static HttpTransport httpTransport;
    private static final List<String> SPREADSHEET_SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS);
    private static final String SPREAD_SHEET_ID = "15RGqEzUz0HyybVPsDOeBs5-CPT2kVNS_FkebiALj6Fg";
    private static SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy hh:mm");


    private static Sheets service;

    private static void authorize(String securityFileName) throws Exception {
        try (InputStream stream = new FileInputStream(securityFileName)) {
            authorize(stream);
        }
    }

    private static void authorize(InputStream stream) throws Exception {

        httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        GoogleCredential credential = GoogleCredential.fromStream(stream)
                .createScoped(SPREADSHEET_SCOPES);

        service = new Sheets.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static void writeData(String spreadsheetId,
                                 String sheetName,
                                 char colStart, int rowId,
                                 Ticket ticket) throws Exception {
//        authorize("C:\\bots-data\\members-36a5849089da.json");
        authorize("/home/user/Downloads/members-36a5849089da.json");

        String writeRange = sheetName + "!" + colStart + rowId + ":" + (char) (colStart + 7);

        List<List<Object>> writeData = new ArrayList<>();

        List<Object> dataRow = new ArrayList<>();

        dataRow.add(ticket.getId());
        dataRow.add(ticket.getCategory().getName());
        dataRow.add(ticket.getText());
        List<User> executors = ticket.getExecutors();
        String executorFullName = "";
        if (executors == null) {
            executorFullName = "-";
        } else {
            for (User executor : executors) {
                executorFullName = executorFullName + "\n" + executor.getUserName();
            }
        }
        dataRow.add(executorFullName.trim());
        dataRow.add(ticket.getState());
        Date receivingTime = new Date();
        dataRow.add(format.format(receivingTime));
        try {
            Date date = handleDeadline(receivingTime, ticket.getCategory().getDeadline());
            dataRow.add(format.format(date));
        } catch (Exception e) {
            dataRow.add("-");
        }

        writeData.add(dataRow);

        ValueRange vr = new ValueRange().setValues(writeData).setMajorDimension("ROWS");
        service.spreadsheets().values()
                .update(spreadsheetId, writeRange, vr)
                .setValueInputOption("RAW")
                .execute();
    }

    private static Date handleDeadline(Date receivingTime, String deadline) throws SQLException {
        Date result = (Date) receivingTime.clone();
        VariablesDao variablesDao = DaoFactory.getFactory().getVariablesDao();
        String dayFirstLetter = variablesDao.select("day_first_letter");
        String hourFirstLetter = variablesDao.select("hour_first_letter");
        String[] split = deadline.split(" ");
        if (split[1].startsWith(dayFirstLetter)) {
            result.setDate(result.getDate() + Integer.parseInt(split[0]));
        }
        if (split[1].startsWith(hourFirstLetter)) {
            result.setHours(result.getHours() + Integer.parseInt(split[0]));
        }
        return result;
    }

    public static void writeTicket(Ticket ticket) {
        try {
            writeData(SPREAD_SHEET_ID, "list", 'A', ticket.getGoogleSheetRowId(), ticket);
        } catch (Exception e) {
            e.printStackTrace();
            //todo log
        }
    }

/*
Этот метод умеет добавлять данные в хвост, но с ним еще нужно разбираться
    private void  AddData() throws Exception {
        service = getSheetsService();
        String spreadSheetID = "1ZAFFrDgmkCcCVrw_zMFvOnogy0bQ258CxROT11R7LD0";
        //Integer sheetID = 123;
        String DateValue = "2015-07-13";
        List<RowData> rowData = new ArrayList<RowData>();
        List<CellData> cellData = new ArrayList<CellData>();
        CellData cell = new CellData();
        cell.setUserEnteredValue(new ExtendedValue().setStringValue(DateValue));
        cell.setUserEnteredFormat(new CellFormat().setNumberFormat(new NumberFormat().setType("DATE")));
        cellData.add(cell);
        rowData.add(new RowData().setValues(cellData));
        //Sheets.Spreadsheets
        BatchUpdateSpreadsheetRequest batchRequests = new BatchUpdateSpreadsheetRequest();
        BatchUpdateSpreadsheetResponse response;
        List<Request> requests = new ArrayList<Request>();
        AppendCellsRequest appendCellReq = new AppendCellsRequest();
        //appendCellReq.setSheetId( sheetID);
        appendCellReq.setRows(rowData);
        appendCellReq.setFields("userEnteredValue,userEnteredFormat.numberFormat");
        requests = new ArrayList<Request>();
        requests.add(new Request().setAppendCells(appendCellReq));
        batchRequests = new BatchUpdateSpreadsheetRequest();
        batchRequests.setRequests(requests);
        response = service.spreadsheets().batchUpdate(spreadSheetID, batchRequests).execute();
        System.out.println("Request \n\n");
        System.out.println(batchRequests.toPrettyString());
        System.out.println("\n\nResponse \n\n");
        System.out.println(response.toPrettyString());
    }
    */
    /*private static final int LAST_ROW_DATA_ID = 3;
    private String userId;
    private static final String KEY = "src/main/resources/members-36a5849089da.json";
//    private static final String KEY = "C:\\bots-data\\members-36a5849089da.json";

    private static final String SPREAD_SHEET_ID = "1HyLocKj3xc-auD2zCbk5zpXNioHveMJEYYvpHHVvCEM";

    public AddToGoogleSheetsCommand(String userId) {
        super();
        this.userId = userId;
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        Integer userId = Integer.valueOf(this.userId);
        Member member = memberDao.selectByUserId(userId);
        SheetsAdapter sheets = new SheetsAdapter();

        ArrayList<Member> list = new ArrayList<>();
        list.add(member);
        int lastRow = Integer.parseInt(constDao.select(LAST_ROW_DATA_ID));
        int puttedRow = lastRow + 1;
        try {
            sheets.authorize(KEY);
            sheets.writeData(SPREAD_SHEET_ID, "list", 'A', puttedRow, list);
            constDao.update(LAST_ROW_DATA_ID, String.valueOf(puttedRow));
            memberDao.setAddedToGroup(userId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        sendMessage(60, update.getCallbackQuery().getMessage().getChatId(), bot);
        sendMessage(72, member.getChatId(), bot);
        sendMessage(70, member.getChatId(), bot);
        return true;
    }*/
}