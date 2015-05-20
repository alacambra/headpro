package com.rha.presentation;

import com.rha.control.CalendarEntriesGenerator;
import com.rha.control.CalendarPeriodsGenerator;
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
import java.util.function.Supplier;
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

    List<LocalDate[]> periods;

    List<BookingRow> bookingRows;

    @Inject
    BookedResourceFacade bookedResourceFacade;

    @Inject
    DivisionFacade divisionFacade;
    List<Integer> totalBooking;

    @Inject
    CalendarPeriodsGenerator calendarPeriodsGenerator;

    @Inject
    CalendarEntriesGenerator<BookedResource> calendarEntriesGenerator;

    LocalDate startDate = LocalDate.of(2014, Month.JANUARY, 1);
    LocalDate endDate = LocalDate.of(2016, Month.JANUARY, 1);

    public void loadBookedResourcesForPeriod() {

        List<BookedResource> bookedResources 
                = bookedResourceFacade.getBookedResourcesForDivision(1, startDate, endDate);

        final Map<Project, List<BookedResource>> resourcesByProject
                = bookedResources.stream().collect(groupingBy(br -> br.getProject()));

        bookingRows = new ArrayList<>();

        if (periods == null) {
            loadPeriods();
        }

        resourcesByProject.keySet().stream().forEach(project -> {

            Supplier<BookedResource> supplier = () -> {
                BookedResource br = new BookedResource();
                br.setPersisted(false);
                br.setProject(project);
                return br;
            };

            List<BookedResource> resources = calendarEntriesGenerator
                    .getCalendarEntries(resourcesByProject.get(project), periods, supplier);

            bookingRows.add(new BookingRow(
                    project,
                    resources,
                    divisionFacade.find(1)));
        });
    }

    private void loadPeriods() {
        periods = calendarPeriodsGenerator
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setStep(Step.BIWEEK)
                .generatePeriods();
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
            loadPeriods();
        }

        return periods.stream().sorted().map(period -> period[0]).collect(toList());
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
