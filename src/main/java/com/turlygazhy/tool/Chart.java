package com.turlygazhy.tool;


import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.io.IOException;
import java.util.*;

/**
 * Created by user on 3/2/17.
 */
public class Chart {
    private static final String PATH = "D:\\charts";
    //    private String PATH = "/home/user/Documents/charts";
    private String fileName;
    private Map<Date, Integer> data = new LinkedHashMap<>();

    public String getChart(String goalName) {
        List<Double> yData = new ArrayList<>();
        List<Date> xData = new ArrayList<>();

        for (Map.Entry<Date, Integer> entry : data.entrySet()) {
            xData.add(entry.getKey());
            yData.add(Double.valueOf(entry.getValue()));
        }

        // Create Chart
        XYChart chart = new XYChart(500, 400);
        chart.setTitle(goalName);
        XYSeries series = chart.addSeries("y(x)", xData, yData);
        series.setMarker(SeriesMarkers.CIRCLE);

        String fullPath = PATH + "/" + this.fileName;
        try {
            BitmapEncoder.saveBitmap(chart, fullPath, BitmapEncoder.BitmapFormat.JPG);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fullPath;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void addPair(Date date, int result) {
        data.put(date, result);
    }
}