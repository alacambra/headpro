/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.presentation;

import com.rha.control.LocalDateConverter;
import com.rha.entity.PeriodTotal;
import com.rha.entity.PeriodWithValue;
import com.rha.entity.Step;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;

/**
 *
 * @author alacambra
 * @param <R> class representing the row keys
 * @param <C> class representing the cell values
 */
public class ResourcesChart<R, C extends PeriodWithValue> {

    BarChartModel resourcesChart;
    List<ResourcesRow<R, C>> resourcesRows;
    List<LocalDate[]> periods;
    Step step;
    String graphTitle;
    List<PeriodTotal> totalResources;
    int detailedGraphMax = 1200;
    Locale locale;
    Optional<String> extender = Optional.empty();

    private void buildDetailedGraph() {
        resourcesRows.stream().forEach(row -> {

            ChartSeries chartSerie = new ChartSeries();
            chartSerie.setLabel(row.getTitle());

            row.getResources().stream().forEach(resource -> {

                String columnName;
                float value = Optional.ofNullable(resource.getValue()).orElse(0f);

                if (step == Step.WEEK) {
                    WeekFields fields = WeekFields.of(locale);
                    int kw = resource.getStartDate().get(fields.weekOfYear());
                    columnName = "CW" + kw;
                } else {
                    columnName = Utils.defaultDateFormat(LocalDateConverter.toDate(resource.getStartDate()));
                }

                chartSerie.set(columnName, value);
            });

            resourcesChart.addSeries(chartSerie);
        });
    }

    private void buildSmallGraph() {
        ChartSeries chartSerie = new ChartSeries();
        chartSerie.setLabel("total");

        totalResources.stream().forEach((value) -> {
            chartSerie.set(value.getStartDate(), value.getTotal());
        });
        resourcesChart.addSeries(chartSerie);
    }
    
    private void configureGraph(){
        resourcesChart.setTitle(graphTitle);
        resourcesChart.setLegendPosition("ne");
        resourcesChart.setStacked(true);
        resourcesChart.setShowPointLabels(true);
        resourcesChart.setZoom(false);

        Axis xAxis = new CategoryAxis("Period (" + step.name().toLowerCase() + ")");
        xAxis.setTickAngle(90);

        resourcesChart.getAxes().put(AxisType.X, xAxis);
        Axis yAxis = resourcesChart.getAxis(AxisType.Y);

        yAxis.setLabel("Resources (hours)");
    }

    protected BarChartModel createResourcesGraph() {
        resourcesChart = new BarChartModel();
        ChartSeries total = new ChartSeries();
        total.setLabel("Estimation of required work resources");

        int size = resourcesRows.size() * periods.size();
        extender.ifPresent(this::addExtenderToChart);

        if (size < detailedGraphMax) {
            buildDetailedGraph();
        } else {
            buildSmallGraph();
        }

        configureGraph();

        return resourcesChart;
    }
    
    private void addExtenderToChart(String extender){
        resourcesChart.setExtender(extender);
    }

    public int getDetailedGraphMax() {
        return detailedGraphMax;
    }

    public void setDetailedGraphMax(int detailedGraphMax) {
        this.detailedGraphMax = detailedGraphMax;
    }

    public Locale getLocale() {
        return locale;
    }

    public ResourcesChart<R, C> setLocale(Locale locale) {
        this.locale = locale;
        return this;
    }

    public BarChartModel getResourcesGraph() {
        return resourcesChart;
    }

    public ResourcesChart<R, C> setResourcesGraph(BarChartModel resourcesGraph) {
        this.resourcesChart = resourcesGraph;
        return this;
    }

    public List<ResourcesRow<R, C>> getResourcesRows() {
        return resourcesRows;
    }

    public ResourcesChart<R, C> setResourcesRows(List<ResourcesRow<R, C>> resourcesRows) {
        this.resourcesRows = resourcesRows;
        return this;
    }

    public List<LocalDate[]> getPeriods() {
        return periods;
    }

    public ResourcesChart<R, C> setPeriods(List<LocalDate[]> periods) {
        this.periods = periods;
        return this;
    }

    public Step getStep() {
        return step;
    }

    public ResourcesChart<R, C> setStep(Step step) {
        this.step = step;
        return this;
    }

    public String getGraphTitle() {
        return graphTitle;
    }

    public ResourcesChart<R, C> setGraphTitle(String graphTitle) {
        this.graphTitle = graphTitle;
        return this;
    }

    public List<PeriodTotal> getTotalResources() {
        return totalResources;
    }

    public ResourcesChart<R, C> setTotalResources(List<PeriodTotal> totalResources) {
        this.totalResources = totalResources;
        return this;
    }
    
    public ResourcesChart<R, C> setExetender(String extender){
        this.extender = Optional.of(extender);
        return this;
    }
}
