package dev.elpu7.piechart.client;

public final class PiechartState {
    private static boolean modKeyPieChartVisible;

    private PiechartState() {
    }

    public static boolean isModKeyPieChartVisible() {
        return modKeyPieChartVisible;
    }

    public static void toggleModKeyPieChart() {
        modKeyPieChartVisible = !modKeyPieChartVisible;
    }

    public static void hide() {
        modKeyPieChartVisible = false;
    }
}
