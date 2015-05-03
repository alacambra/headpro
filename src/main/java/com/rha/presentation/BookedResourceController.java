package com.rha.presentation;

import com.rha.entity.BookedResource;
import com.rha.presentation.util.JsfUtil;
import com.rha.presentation.util.JsfUtil.PersistAction;
import com.rha.boundary.BookedResourceFacade;
import com.rha.boundary.ProjectFacade;
import com.rha.entity.StepPeriod;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.*;
import javax.ejb.EJB;
import javax.ejb.EJBException;
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
import java.util.stream.IntStream;
import javafx.scene.control.Cell;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import org.primefaces.component.api.UIColumn;
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
    Logger logger = Logger.getLogger(this.getClass().getName());

    @EJB
    private com.rha.boundary.BookedResourceFacade ejbFacade;
    private List<BookedResource> items = null;
    private BookedResource selected;

    List<BookingRow> rows;
    List<BookingRow> originalRows;
    List<Integer> totalBooking;

    @Inject
    ProjectFacade projectFacade;

    Cell selectedCell;

    public BookedResourceController() {
    }

    public BookedResource getSelected() {
        return selected;
    }

    public void setSelected(BookedResource selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private BookedResourceFacade getFacade() {
        return ejbFacade;
    }

    public BookedResource prepareCreate() {
        selected = new BookedResource();
        initializeEmbeddableKey();
        return selected;
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

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("BookedResourceCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("BookedResourceUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("BookedResourceDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<BookedResource> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    public List<StepPeriod> getPeriods() {
        return getFacade().getPeriods();
    }

    private void loadRows() {
        if (rows == null) {

            Map<Project, List<BookedResource>> bookings = getFacade()
                    .getBookedResourcesForDivision(1).stream()
                    .collect(groupingBy(booking -> booking.getProject()));

            rows = bookings.keySet().stream()
                    .map(pr -> new BookingRow(pr,
                                    bookings.get(pr).stream()
                                    .sorted()
                                    .collect(toList())))
                    .collect(toList());
        }
    }

    public List<BookingRow> getBookings() {
        loadRows();
        return rows;
    }

    public List<Project> getProjects() {
        return projectFacade.findAll();
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public BookedResource getBookedResource(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<BookedResource> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<BookedResource> getItemsAvailableSelectOne() {
        return getFacade().findAll();
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

        yAxis.setLabel(
                "Resources");
        yAxis.setMin(
                0);
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
