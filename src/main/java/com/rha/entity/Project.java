package com.rha.entity;

import com.rha.control.LocalDateConverter;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "PROJECT")
@NamedQueries({
    @NamedQuery(name = Project.projectsInPeriod,
            query = "SELECT pr FROM Project pr LEFT JOIN pr.bookedResources br "
            + "WHERE ((pr.startDate>=:startDate AND pr.startDate<=:endDate)"
            + " OR  "
            + "(pr.endDate>=:startDate AND pr.endDate<=:endDate) "
            + " OR  "
            + "(pr.startDate<=:startDate AND pr.endDate>=:endDate)) "
    )
})
public class Project implements Serializable {

    public static final String projectsInPeriod = "com.rha.entity.Project.projectsInPeriod";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE)
    List<RequiredResource> bookedResources;

    String name;
    Integer probability = 100;
    Integer abscence = 0;
    Step step = Step.MONTH;

    @Temporal(TemporalType.DATE)
    Date startDate;

    @Temporal(TemporalType.DATE)
    Date endDate;

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }

    public Integer getProbability() {
        return probability;
    }

    public void setProbability(Integer probability) {
        this.probability = probability;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAbscence() {
        return abscence;
    }

    public void setAbscence(Integer abscence) {
        this.abscence = abscence;
    }

    public LocalDate getStartLocalDate() {
        if (startDate != null) {
            return LocalDateConverter.toLocalDate(startDate);
        } else {
            return null;
        }
    }

    public Date getStartDate() {
        return startDate;
    }
    
    public void setStartLocalDate(LocalDate startDate) {
        this.startDate = LocalDateConverter.toDate(startDate);
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndLocalDate() {
        if (endDate != null) {
            return LocalDateConverter.toLocalDate(endDate);
        } else {
            return null;
        }
    }

    public Date getEndDate() {
        return endDate;
    }
    
    public Date getStartDateAsDate(){
        if(startDate == null) return null;
        return new Date(startDate.getTime());
    }
    
    public Date getEndDateAsDate(){
        if(endDate == null) return null;
        return new Date(endDate.getTime());
    }

    public void setEndLocalDate(LocalDate endDate) {
        this.endDate = LocalDateConverter.toDate(endDate);
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Project other = (Project) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.id);
        return hash;
    }

}
