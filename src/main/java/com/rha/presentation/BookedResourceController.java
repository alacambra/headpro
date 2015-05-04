package com.rha.presentation;

import com.rha.entity.BookedResource;
import com.rha.boundary.BookedResourceFacade;
import com.rha.boundary.DivisionFacade;
import com.rha.boundary.ProjectFacade;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.*;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import com.rha.entity.Project;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import javafx.scene.control.Cell;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import org.primefaces.event.CellEditEvent;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

@Named("bookedResourceController")
@SessionScoped
public class BookedResourceController implements Serializable {

    private LineChartModel areaModel;

    @EJB
    private com.rha.boundary.BookedResourceFacade ejbFacade;
    List<BookingRow> rows;
    List<BookingRow> originalRows;
    List<Integer> totalBooking;

    @Inject
    ProjectFacade projectFacade;

    @Inject
    DivisionFacade divisionFacade;

    Cell selectedCell;

    public BookedResourceController() {
    }

    protected void setEmbeddableKeys() {
    }

    private BookedResourceFacade getFacade() {
        return ejbFacade;
    }

    public void onCellEdit(CellEditEvent event) {

        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();

        FacesContext context = FacesContext.getCurrentInstance();
        BookingRow entity = context.getApplication().evaluateExpressionGet(context, "#{booking}", BookingRow.class);

        if (newValue != null && !newValue.equals(oldValue)) {

            getFacade().updateOrCreateBookings(entity.getResources());

            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Old: " + oldValue + ", New:" + newValue);
            FacesContext.getCurrentInstance().addMessage(null, msg);

            areaModel = null;
            totalBooking = null;

        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cell not changed", "Old: " + oldValue + ", New:" + newValue);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

//    public List<StepPeriod> getPeriods() {
//        return getFacade().getPeriods();
//    }
    private void loadRows() {
        if (rows == null) {

            List<Project> emptyProjects = projectFacade.getProjectsWithoutBookedResources(1);

            Map<Project, List<BookedResource>> bookings = getFacade()
                    .getBookedResourcesForDivision(1).stream()
                    .collect(groupingBy(booking -> booking.getProject()));

            emptyProjects.stream().forEach(pr -> bookings.put(pr, new ArrayList<>()));

            rows = bookings.keySet().stream()
                    .map(pr -> new BookingRow(pr,
                                    bookings.get(pr).stream()
                                    .sorted()
                                    .collect(toList()),
                                    divisionFacade.find(1))
                    ).collect(toList());
        }
    }

    public List<BookingRow> getBookings() {
        loadRows();
        return rows;
    }

    public List<Project> getProjects() {
        return projectFacade.findAll();
    }

    public BookedResource getBookedResource(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public LineChartModel getAreaModel() {

        if (areaModel == null) {
            createAreaModel();
        }

        return areaModel;
    }

    private void createAreaModel() {
        areaModel = new LineChartModel();
        LineChartSeries total = new LineChartSeries();
        total.setFill(true);
        total.setLabel("Estimation of required work resources");

        rows.stream().forEach(row -> {

            LineChartSeries brc = new LineChartSeries();
            brc.setFill(true);
            brc.setLabel(row.getProject().getName());

            row.getResources().stream().forEach(b -> {
                int position = Optional.ofNullable(b.getPosition()).orElse(brc.getData().size());
                int booked = Optional.ofNullable(b.getBooked()).orElse(0);
                if (brc.getData().size() < 12) {
                    brc.set(position + 1, booked);
                }
            });
            areaModel.addSeries(brc);
        });

        areaModel.setTitle("Resources booked for service X");
        areaModel.setLegendPosition("ne");
        areaModel.setStacked(true);
        areaModel.setShowPointLabels(true);
        areaModel.setZoom(true);

        Axis xAxis = new CategoryAxis("Month");

        areaModel.getAxes().put(AxisType.X, xAxis);
        Axis yAxis = areaModel.getAxis(AxisType.Y);

        yAxis.setLabel("Resources");
        yAxis.setMin(0);
    }

    @FacesConverter(forClass = BookedResource.class)
    public static class BookedResourceControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            BookedResourceController controller = (BookedResourceController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "bookedResourceController");
            return controller.getBookedResource(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof BookedResource) {
                BookedResource o = (BookedResource) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), BookedResource.class.getName()});
                return null;
            }
        }
    }

    public List<List<Integer>> getTotalBooking() {

        if (totalBooking == null) {
            totalBooking = getFacade().getTotalBookedResourcesPerProjectForDivision(1);
        }

        List<List<Integer>> r = new ArrayList();
        r.add(totalBooking);
        return r;
    }
}
