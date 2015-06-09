package com.rha.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;
import java.util.Objects;

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
    
    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE)
    List<BookedResource> bookedResources;

    String name;
    Integer probability = 100;
    Integer abscence = 0;
    Step step = Step.MONTH;

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
