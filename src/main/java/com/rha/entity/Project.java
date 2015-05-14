package com.rha.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@NamedQueries({
    @NamedQuery(name = Project.emptyProjects, 
            query = "SELECT pr FROM Project pr LEFT JOIN pr.bookedResources br "
                    + "GROUP BY pr HAVING count(br) = 0")
})
public class Project implements Serializable {

    public static final String emptyProjects = "com.rha.entity.Project.emptyProjects";
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;
    
    @OneToMany(mappedBy = "project")
    List<BookedResource> bookedResources;

    String name;
    Integer probability = 100;
    Integer abscence = 0;
    Step step = Step.MONTH;

    public Step getStep() {
        return step;
    }

    public Project setStep(Step step) {
        this.step = step;
        return this;
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

    public Project setName(String name) {
        this.name = name;
        return this;
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
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Project) {
            final Project other = (Project) obj;
            return new EqualsBuilder()
                    .append(id, other.getId())
                    .isEquals();
        } else {
            return false;
        }
    }

}
