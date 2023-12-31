package Tools;

import Data.BankAccount;
import Data.GlobalObj;
import Data.UserObj;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChartGenerator {

    public void chartAccountData() {
        LineChart<String, Number> lineChart = new LineChart<>(new CategoryAxis(), new NumberAxis());
        lineChart.setTitle("Account Data Over Time");

        Map<String, BankAccount> accountMap = UserObj.accountMap;
        List<String> sortedDates = getSortedDates(accountMap);
        XYChart.Series<String, Number> currencySeries = createSeries("Currency");
        XYChart.Series<String, Number> depositSeries = createSeries("Deposit");
        XYChart.Series<String, Number> debtSeries = createSeries("Debt");
        XYChart.Series<String, Number> sumSeries = createSeries("SUM");

        for (int i = Math.max(sortedDates.size() - 30, 0); i < sortedDates.size(); i++) {
            String date = sortedDates.get(i);
            BankAccount account = accountMap.get(date);

            addDataPoint(currencySeries, date, account.currency);
            addDataPoint(depositSeries, date, account.deposit);
            addDataPoint(debtSeries, date, account.debt);

            float sumForDate = account.currency + account.deposit + account.debt;
            addDataPoint(sumSeries, date, sumForDate);
        }

        showAccountData(lineChart, currencySeries, depositSeries, debtSeries, sumSeries);
    }

    public static void chartGlobal() {
        LineChart<String, Number> lineChart = new LineChart<>(new CategoryAxis(), new NumberAxis());
        lineChart.setTitle("Stock Values Over Time");

        Map<String, Map<String, Float>> dataMap = GlobalObj.dataMap;
        List<Map.Entry<String, Map<String, Float>>> entries = getSortedEntries(dataMap);

        int maxEntriesToShow = Math.min(entries.size(), 30);

        for (int i = entries.size() - maxEntriesToShow; i < entries.size(); i++) {
            Map.Entry<String, Map<String, Float>> entry = entries.get(i);
            String dateString = entry.getKey();

            for (Map.Entry<String, Float> companyEntry : entry.getValue().entrySet()) {
                String companyName = companyEntry.getKey();
                Float stockValue = companyEntry.getValue();

                XYChart.Series<String, Number> series = findOrCreateSeries(lineChart, companyName);
                addDataPoint(series, dateString, stockValue);
            }
        }

        showStock(lineChart);
    }

    public static void chartSelf() {
        LineChart<String, Number> lineChart = new LineChart<>(new CategoryAxis(), new NumberAxis());
        lineChart.setTitle("Self Investments Over Time");

        Map<String, BankAccount> accountMap = UserObj.accountMap;
        List<String> sortedDates = getSortedDates(accountMap);
        XYChart.Series<String, Number> sumSeries = createSeries("SUM");

        for (String date : sortedDates) {
            BankAccount account = accountMap.get(date);
            Map<String, Float> investMap = account.investment.investMap;
            float sumForDate = 0.0f;

            for (Map.Entry<String, Float> investmentEntry : investMap.entrySet()) {
                String companyName = investmentEntry.getKey();
                Float investmentAmount = investmentEntry.getValue();
                Map<String, Map<String, Float>> dataMap = GlobalObj.dataMap;
                Float stockValue = dataMap.containsKey(date) ? dataMap.get(date).getOrDefault(companyName, 0.0f) : 0.0f;
                float totalInvestmentValue = investmentAmount * stockValue;

                XYChart.Series<String, Number> series = findOrCreateSeries(lineChart, companyName);
                addDataPoint(series, date, totalInvestmentValue);

                sumForDate += totalInvestmentValue;
            }

            addDataPoint(sumSeries, date, sumForDate);
        }

        showInvestment(lineChart, sumSeries);
    }

    private static List<String> getSortedDates(Map<String, ?> map) {
        List<String> sortedDates = new ArrayList<>(map.keySet());
        sortedDates.sort(Comparator.comparing(ChartGenerator::parseDate));
        return sortedDates;
    }

    private static List<Map.Entry<String, Map<String, Float>>> getSortedEntries(Map<String, Map<String, Float>> map) {
        List<Map.Entry<String, Map<String, Float>>> entries = new ArrayList<>(map.entrySet());
        entries.sort((entry1, entry2) -> Objects.requireNonNull(parseDate(entry1.getKey())).compareTo(parseDate(entry2.getKey())));
        return entries;
    }

    private static XYChart.Series<String, Number> createSeries(String seriesName) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(seriesName);
        return series;
    }

    private static void addDataPoint(XYChart.Series<String, Number> series, String date, Number value) {
        series.getData().add(new XYChart.Data<>(date, value));
    }

    private static void showInvestment(LineChart<String, Number> lineChart, XYChart.Series<String, Number> additionalSeries) {
        Alert alert = createAlert("Investment Chart");

        VBox vBox = new VBox();
        vBox.getChildren().add(lineChart);
        lineChart.getData().add(additionalSeries);

        alert.getDialogPane().setContent(vBox);
        alert.showAndWait();
    }

    private static XYChart.Series<String, Number> findOrCreateSeries(LineChart<String, Number> lineChart, String seriesName) {
        for (XYChart.Series<String, Number> existingSeries : lineChart.getData()) {
            if (existingSeries.getName().equals(seriesName)) {
                return existingSeries;
            }
        }

        XYChart.Series<String, Number> newSeries = createSeries(seriesName);
        lineChart.getData().add(newSeries);
        return newSeries;
    }

    private static void showStock(LineChart<String, Number> lineChart) {
        Alert alert = createAlert("Stock Chart");

        VBox vBox = new VBox();
        vBox.getChildren().add(lineChart);

        alert.getDialogPane().setContent(vBox);
        alert.showAndWait();
    }

    @SafeVarargs
    private static void showAccountData(LineChart<String, Number> lineChart, XYChart.Series<String, Number>... seriesList) {
        Alert alert = createAlert("Account Data Chart");

        VBox vBox = new VBox();
        vBox.getChildren().add(lineChart);

        for (XYChart.Series<String, Number> series : seriesList) {
            lineChart.getData().add(series);
        }

        alert.getDialogPane().setContent(vBox);
        alert.showAndWait();
    }

    private static Alert createAlert(String title) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle(title);
        return alert;
    }

    private static Date parseDate(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }
}
