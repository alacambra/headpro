package com.rha.presentation;

import com.rha.control.ResourcesCalendar;
import com.rha.boundary.BookedResourceFacade;
import com.rha.boundary.DivisionFacade;
import com.rha.entity.BookedResource;
import com.rha.entity.Project;
import com.rha.entity.Step;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import static java.util.stream.Collectors.*;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.CellEditEvent;

/**
 *
 * @author alacambra
 */
@SessionScoped
@Named("brc")
public class BookedResourceController2 implements Serializable {

    @Inject
    transient Logger logger;

    Map<LocalDate, List<LocalDate[]>> periods;
    List<BookingRow> bookingRows;

    @Inject
    BookedResourceFacade bookedResourceFacade;

    @Inject
    DivisionFacade divisionFacade;
    List<Integer> totalBooking;
    ResourcesCalendar resourcesCalendar = new ResourcesCalendar();
    LocalDate startDate = LocalDate.of(2014, Month.JANUARY, 1);
    LocalDate endDate = LocalDate.of(2016, Month.JANUARY, 1);
    

    public void loadBookedResourcesForPeriod() {

        List<BookedResource> bookedResources = bookedResourceFacade.getBookedResourcesForDivision(
                1, startDate, endDate);

        final Map<Project, List<BookedResource>> resourcesByProject
                = bookedResources.stream().collect(groupingBy(br -> br.getProject()));

        bookingRows = new ArrayList<>();

        resourcesByProject.keySet().stream().forEach(project -> {
            bookingRows.add(new BookingRow(
                    project,
                    resourcesCalendar
                    .setStartDate(startDate)
                    .setEndDate(endDate)
                    .setExistentResources(resourcesByProject.get(project))
                    .setStep(Step.BIWEEK)
                    .getCalendarEntries().parallelStream()
                    .map(br -> {
                        br.setProject(project);
                        return br;
                    })
                    .collect(toList()),
                    divisionFacade.find(1)));
        });
    }

    private void resetValues() {
        bookingRows = null;
        periods = null;
    }

    public List<BookingRow> getBookingRow() {
        if (bookingRows == null) {
            loadBookedResourcesForPeriod();
        }

        return bookingRows;
    }

    public List<LocalDate> getPeriods() {

        if (periods == null) {
            periods = getBookingRow().get(0).getResources().stream()
                    .map(br -> new LocalDate[]{br.getStartDate(), br.getEndDate()})
                    .collect(groupingBy(period -> period[0]));
        }

        return periods.keySet().stream().sorted().collect(toList());
    }

    private LocalDate getDate(BookedResource br) {
        return br.getStartDate();
    }

    public void onCellEdit(CellEditEvent event) {

        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();

        FacesContext context = FacesContext.getCurrentInstance();
        BookingRow entity = context.getApplication().evaluateExpressionGet(context, "#{booking}", BookingRow.class);

        if (newValue != null && !newValue.equals(oldValue)) {

            bookedResourceFacade.updateOrCreateBookings(entity.getResources());

            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Old: " + oldValue + ", New:" + newValue);
            FacesContext.getCurrentInstance().addMessage(null, msg);

//            areaModel = null;
//            totalBooking = null;
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cell not changed", "Old: " + oldValue + ", New:" + newValue);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public List<List<Integer>> getTotalBooking() {

        if (totalBooking == null) {
            totalBooking = bookedResourceFacade
                    .getTotalBookedResourcesByDivisionForPeriod(1, startDate, endDate);
        }

        List<List<Integer>> r = new ArrayList();
        r.add(totalBooking);
        return r;
    }

}
