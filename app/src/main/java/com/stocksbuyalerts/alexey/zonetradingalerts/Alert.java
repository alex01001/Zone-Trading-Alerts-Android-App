package com.stocksbuyalerts.alexey.zonetradingalerts;

public class Alert {
    private String symbol;
    private String name;
    private String chartURL;
    private String alertID;
    private String time;
    private String timeStr;
    private String price;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getChartURL() {
        return chartURL;
    }

    public void setChartURL(String chartURL) {
        this.chartURL = chartURL;
    }

    public String getAlertID() {
        return alertID;
    }

    public void setAlertID(String alertID) {
        this.alertID = alertID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimeStr() {
        return timeStr;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }

    @Override
    public String toString() {
        return "Alert{" +
                "symbol='" + symbol + '\'' +
                ", chartURL='" + chartURL + '\'' +
                ", alertID='" + alertID + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
