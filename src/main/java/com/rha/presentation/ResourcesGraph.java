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
public class ResourcesGraph<R, C extends PeriodWithValue> {
    
    BarChartModel resourcesGraph;
    List<ResourcesRow<R, C>> resourcesRows;
    List<LocalDate[]> periods;
    Step step;
    String graphTitle;
    List<PeriodTotal> totalResources;
    
    protected BarChartModel createResourcesGraph() {
        resourcesGraph = new BarChartModel();
        ChartSeries total = new ChartSeries();
        total.setLabel("Estimation of required work resources");

        int size = resourcesRows.size() * periods.size();

        if (size < 1200) {

            resourcesRows.stream().forEach(row -> {

                ChartSeries chartSerie = new ChartSeries();
                chartSerie.setLabel(row.getTitle());

                row.getResources().stream().forEach(resource -> {

                    String columnName;
                    long booked = Optional.ofNullable(resource.getValue()).orElse(0L);

                    if (step == Step.WEEK) {
                        WeekFields fields = WeekFields.of(Locale.GERMANY);
                        int kw = resource.getStartDate().get(fields.weekOfYear());
                        columnName = "CW" + kw;
                    } else {
                        columnName = Utils.defaultDateFormat(LocalDateConverter.toDate(resource.getStartDate()));
                    }

                    Double d = 100D;
                    chartSerie.set(columnName, booked);
                });

                resourcesGraph.addSeries(chartSerie);
            });
        } else {
            ChartSeries chartSerie = new ChartSeries();
            chartSerie.setLabel("total");

            int i = 0;
            totalResources.stream().forEach((value) -> {
                chartSerie.set(value.getStartDate(), value.getTotal());
            });
            resourcesGraph.addSeries(chartSerie);
        }

        resourcesGraph.setTitle(graphTitle);
        resourcesGraph.setLegendPosition("ne");
        resourcesGraph.setStacked(true);
        resourcesGraph.setShowPointLabels(true);
        resourcesGraph.setZoom(true);

        Axis xAxis = new CategoryAxis("Period (" + step.name().toLowerCase() + ")");
        xAxis.setTickAngle(90);

        resourcesGraph.getAxes().put(AxisType.X, xAxis);
        Axis yAxis = resourcesGraph.getAxis(AxisType.Y);

        yAxis.setLabel("Resources (hours)");
        
        return resourcesGraph;
    }

    public BarChartModel getResourcesGraph() {
        return resourcesGraph;
    }

    public ResourcesGraph<R, C> setResourcesGraph(BarChartModel resourcesGraph) {
        this.resourcesGraph = resourcesGraph;
        return this;
    }

    public List<ResourcesRow<R, C>> getResourcesRows() {
        return resourcesRows;
    }

    public ResourcesGraph<R, C> setResourcesRows(List<ResourcesRow<R, C>> resourcesRows) {
        this.resourcesRows = resourcesRows;
        return this;
    }
    
    public List<LocalDate[]> getPeriods() {
        return periods;
    }

    public ResourcesGraph<R, C> setPeriods(List<LocalDate[]> periods) {
        this.periods = periods;
        return this;
    }

    public Step getStep() {
        return step;
    }

    public ResourcesGraph<R, C> setStep(Step step) {
        this.step = step;
        return this;
    }

    public String getGraphTitle() {
        return graphTitle;
    }

    public ResourcesGraph<R, C> setGraphTitle(String graphTitle) {
        this.graphTitle = graphTitle;
        return this;
    }

    public List<PeriodTotal> getTotalResources() {
        return totalResources;
    }

    public ResourcesGraph<R, C> setTotalResources(List<PeriodTotal> totalResources) {
        this.totalResources = totalResources;
        return this;
    }
    
}
