package com.rr.nn;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FisherIrisesController {
    @FXML
    private TextField irisDataVectorField;
    @FXML
    private TextField sigmaField;
    @FXML
    private TextArea irisesDataArea;
    @FXML
    private TextArea processingArea;
    @FXML
    private TextArea calculatedProbabilitiesArea;
    @FXML
    private TextField resultField;
    @FXML
    private CheckBox showFileContentCheckbox;
    @FXML
    private CheckBox showProcessingAreaCheckbox;

    @FXML
    public void processResult() throws IOException {
        clearFields();

        String irisStringParams = irisDataVectorField.getText();

        double sigma;
        try {
            sigma = Double.parseDouble(sigmaField.getText());
            if (sigma < 0 || sigma > 1)
                throw new NumberFormatException("Sigma must be in interval (0, 1]");
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Incorrect sigma");
            if (!e.getMessage().equals("empty String"))
                alert.setHeaderText(e.getMessage());
            else
                alert.setHeaderText(null);
            alert.setContentText("Make sure that your sigma is in interval (0, 1]!");

            alert.showAndWait();
            return;
        }

        Double[] irisParams;
        try {
            irisParams = Arrays.stream(irisStringParams.split(" ")).map(Double::valueOf).toArray(Double[]::new);
            if (irisParams.length != 4)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Incorrect iris data set");
            alert.setHeaderText(null);
            alert.setContentText("Make sure that your pass 4 double iris parameters separated by space!");

            alert.showAndWait();
            return;
        }

        Iris iris = new Iris(irisParams[0], irisParams[1], irisParams[2], irisParams[3]);
        List<Iris> setosaIrises = new ArrayList<>();
        List<Iris> versicolorIrises = new ArrayList<>();
        List<Iris> virginicaIrises = new ArrayList<>();
        List<Double> listR1 = new ArrayList<>();
        List<Double> listR2 = new ArrayList<>();
        List<Double> listR3 = new ArrayList<>();
        List<Double> listD1 = new ArrayList<>();
        List<Double> listD2 = new ArrayList<>();
        List<Double> listD3 = new ArrayList<>();

        fillListsWithCorrespondingIrisesData(setosaIrises, versicolorIrises, virginicaIrises, irisStringParams);

        StringBuilder processingAreaSb = new StringBuilder();

        final boolean isShowProcessingAreaContent = showProcessingAreaCheckbox.isSelected();
        calculateR(setosaIrises, listR1, iris, 1, processingAreaSb, isShowProcessingAreaContent);
        calculateR(versicolorIrises, listR2, iris, 2, processingAreaSb, isShowProcessingAreaContent);
        calculateR(virginicaIrises, listR3, iris, 3, processingAreaSb, isShowProcessingAreaContent);

        calculateD(listD1, listR1, sigma, 1, processingAreaSb, isShowProcessingAreaContent);
        calculateD(listD2, listR2, sigma, 2, processingAreaSb, isShowProcessingAreaContent);
        calculateD(listD3, listR3, sigma, 3, processingAreaSb, isShowProcessingAreaContent);

        if (showProcessingAreaCheckbox.isSelected())
            processingArea.appendText(processingAreaSb.toString());

        double sumD1;
        double sumD2;
        double sumD3;
        double totalDSum;
        sumD1 = getSumD(listD1);
        sumD2 = getSumD(listD2);
        sumD3 = getSumD(listD3);
        totalDSum = sumD1 + sumD2 + sumD3;

        double setosaProbability;
        double versicolorProbability;
        double virginicaProbability;
        setosaProbability = getResultProbabilities(sumD1, totalDSum);
        versicolorProbability = getResultProbabilities(sumD2, totalDSum);
        virginicaProbability = getResultProbabilities(sumD3, totalDSum);

        calculatedProbabilitiesArea.appendText("P(setosa) = " + setosaProbability +
                "\nP(versicolor) = " + versicolorProbability + "\nP(virginica) = " + virginicaProbability + "\n");

        String irisType = getResultIrisType(setosaProbability, versicolorProbability, virginicaProbability);
        resultField.setText(irisType);
    }

    private void clearFields() {
        irisesDataArea.clear();
        processingArea.clear();
        calculatedProbabilitiesArea.clear();
        resultField.clear();
    }

    private void calculateRIfShowProcessingAreaContentNotChecked(List<Iris> irisesList, List<Double> listR, Iris iris) {
        for (Iris currentIris : irisesList) {
            double sum = 0;
            for (int i = 0; i < 4; ++i)
                sum += Math.pow(currentIris.getXi(i) - iris.getXi(i), 2.0);
            listR.add(Math.sqrt(sum));
        }
    }

    private void calculateRIfShowProcessingAreaContentChecked(List<Iris> irisesList, List<Double> listR, Iris iris, int number, StringBuilder sb) {
        int j = 0;
        for (Iris currentIris : irisesList) {
            double sum = 0;
            for (int i = 0; i < 4; ++i)
                sum += Math.pow(currentIris.getXi(i) - iris.getXi(i), 2.0);
            Double currentR = Math.sqrt(sum);
            listR.add(currentR);
            sb.append("R").append(number).append(",").append(++j).append(" = ").append(currentR).append("\n");
        }
    }

    private void calculateR(List<Iris> irisesList, List<Double> listR, Iris iris, int number, StringBuilder sb, boolean isShowProcessingAreaContent) {
        if (isShowProcessingAreaContent)
            calculateRIfShowProcessingAreaContentChecked(irisesList, listR, iris, number, sb);
        else
            calculateRIfShowProcessingAreaContentNotChecked(irisesList, listR, iris);
    }

    private void calculateDIfShowProcessingAreaContentNotChecked(List<Double> listD, List<Double> listR, double sigma) {
        for (Double currentR : listR)
            listD.add(Math.exp(-Math.pow(currentR, 2.0) / Math.pow(sigma, 2.0)));
    }

    private void calculateDIfShowProcessingAreaContentChecked(List<Double> listD, List<Double> listR, double sigma, int number, StringBuilder sb) {
        int i = 0;
        for (Double currentR : listR) {
            double currentD = Math.exp(-Math.pow(currentR, 2.0) / Math.pow(sigma, 2.0));
            listD.add(currentD);
            sb.append("D").append(number).append(",").append(++i).append(" = ").append(currentD).append("\n");
        }
    }

    private void calculateD(List<Double> listD, List<Double> listR, double sigma, int number, StringBuilder sb, boolean isShowProcessingAreaContent) {
        if (isShowProcessingAreaContent)
            calculateDIfShowProcessingAreaContentChecked(listD, listR, sigma, number, sb);
        else
            calculateDIfShowProcessingAreaContentNotChecked(listD, listR, sigma);
    }

    private double getSumD(List<Double> listD) {
        double sum = 0;
        for (Double d : listD)
            sum += d;
        return sum;
    }

    private double getResultProbabilities(double concreteDSum, double totalDSum) {
        if (totalDSum == 0)
            throw new ArithmeticException("totalDSum equals 0");
        return concreteDSum / totalDSum;
    }

    private String getResultIrisType(double setosaProbability, double versicolorProbability, double virginicaProbability) {
        return setosaProbability > versicolorProbability && setosaProbability > virginicaProbability ? "setosa" :
                versicolorProbability > virginicaProbability ? "versicolor" : "virginica";
    }

    private void fillListsWithCorrespondingIrisesData(List<Iris> setosaIrises, List<Iris> versicolorIrises, List<Iris> virginicaIrises, String iris) throws IOException {
        String fileName = new File(getClass().getClassLoader().getResource("irises.txt").getFile()).getAbsolutePath();

        List<String> list = new ArrayList<>();
        list.addAll(Files.lines(Paths.get(fileName)).collect(Collectors.toList()));

        list.forEach(currentIrisTextLine -> {
            String[] params = currentIrisTextLine.split(" ");
            Iris currentIris = new Iris(Double.parseDouble(params[0]), Double.parseDouble(params[1]), Double.parseDouble(params[2]), Double.parseDouble(params[3]));
            IrisType irisType;
            switch (params[4]) {
                case "setosa":
                    irisType = IrisType.SETOSA;
                    currentIris.setIrisType(irisType);
                    setosaIrises.add(currentIris);
                    break;
                case "versicolor":
                    irisType = IrisType.VERSICOLOR;
                    currentIris.setIrisType(irisType);
                    versicolorIrises.add(currentIris);
                    break;
                case "virginica":
                    irisType = IrisType.VIRGINICA;
                    currentIris.setIrisType(irisType);
                    virginicaIrises.add(currentIris);
                    break;
                default:
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Unknown iris type");
                    alert.setHeaderText("Problem occurred with irises file data");
                    alert.setContentText("Unknown iris type, check the file and try again!");

                    alert.showAndWait();
                    System.exit(1);
            }

            if (showFileContentCheckbox.isSelected()) {
                int i = 1;
                StringBuilder irisesData = new StringBuilder();
                for (Iris setosaIris : setosaIrises)
                    irisesData.append(i++).append(". ").append(setosaIris).append("\n");
                for (Iris versicolorIris : versicolorIrises)
                    irisesData.append(i++).append(". ").append(versicolorIris).append("\n");
                for (Iris virginicaIris : virginicaIrises)
                    irisesData.append(i++).append(". ").append(virginicaIris).append("\n");
                irisesDataArea.setText(irisesData.toString());
            }
        });
    }
}