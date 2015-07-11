package com.rha.presentation;

import com.rha.boundary.AvailableResourceFacade;
import com.rha.boundary.ServiceFacade;
import com.rha.entity.AvailableResource;
import com.rha.entity.PeriodTotal;
import com.rha.entity.Service;
import com.rha.entity.Step;
import java.io.Serializable;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;

@Named("arc")
@SessionScoped
public class AvailableResourceController extends ResourceController<Service, AvailableResource> implements Serializable {

    @Inject
    transient Logger logger;

    @Inject
    AvailableResourceFacade availableResourceFacade;

    @Inject
    ServiceFacade serviceFacade;

    @Override
    protected List<AvailableResource> getResourcesInPeriod() {
        return availableResourceFacade.getAvailableResourcesInPeriod(startDate, endDate);
    }

    @Override
    protected List<Service> getKeysWithoutValues() {
        return serviceFacade.findAll();
    }

    @Override
    protected Service collectResourceByKey(AvailableResource value) {
        return value.getService();
    }

    @Override
    protected Supplier<AvailableResource> getResourceSupplierForKey(Service key) {
        return  () -> {
                AvailableResource ar = new AvailableResource();
                ar.setPersisted(false);
                ar.setService(key);
                return ar;
            };
    }

    @Override
    protected void createAreaModel() {
        barModel = new BarChartModel();
        ChartSeries total = new ChartSeries();
        total.setLabel("Estimation of required work resources");

        int size = resourceRow.size() * periods.size();

        if (size < 1200) {

            resourceRow.stream().forEach(row -> {

                ChartSeries chartSerie = new ChartSeries();
                chartSerie.setLabel(row.getKey().getName());

                row.getResources().stream().forEach(availableResource -> {
                    
                    String columnName = "";
                    
                    if(step == Step.WEEK){
                        WeekFields fields = WeekFields.of(Locale.GERMANY);
                        int kw = availableResource.getStartDate().get(fields.weekOfYear());
                        columnName = "CW" + kw;
                    }else{
                        columnName = Utils.defaultDateFormat(availableResource.getStartDateAsDate());
                    }
                    long available = Optional.ofNullable(availableResource.getAvailable()).orElse(0L);
                    Double d = 100D;
                    chartSerie.set(columnName, available);
                });

                barModel.addSeries(chartSerie);
            });
        } else {
            ChartSeries chartSerie = new ChartSeries();
            chartSerie.setLabel("total");

            int i = 0;
            for (PeriodTotal value : totalResources) {
                chartSerie.set(Utils.defaultDateFormat(value.getStartDateAsDate()), value.getTotal());
            }
            barModel.addSeries(chartSerie);
        }

        barModel.setTitle("Available resources");
        barModel.setLegendPosition("ne");
        barModel.setStacked(true);
        barModel.setShowPointLabels(true);
        barModel.setZoom(true);

        Axis xAxis = new CategoryAxis("Period (" + step.name().toLowerCase() + ")");
        xAxis.setTickAngle(90);

        barModel.getAxes().put(AxisType.X, xAxis);
        Axis yAxis = barModel.getAxis(AxisType.Y);

        yAxis.setLabel("Resources (hours)");
        yAxis.setMin(0);
    }

    @Override
    protected List<PeriodTotal> getTotalResourcesInPeriod() {
        return availableResourceFacade.getTotalAvailableResourcesInPeriod(startDate, endDate);
    }

    @Override
    protected void updateOrCreateResource(List<AvailableResource> resources) {
        availableResourceFacade.updateOrCreateBookings(resources);
    }
    
    
}
