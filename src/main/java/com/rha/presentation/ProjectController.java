package com.rha.presentation;

import com.rha.entity.Project;
import com.rha.presentation.util.JsfUtil;
import com.rha.presentation.util.JsfUtil.PersistAction;
import com.rha.boundary.ProjectFacade;
import com.rha.control.LocalDateConverter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("projectController")
@SessionScoped
public class ProjectController implements Serializable {

    @EJB
    private com.rha.boundary.ProjectFacade ejbFacade;
    private List<Project> items = null;
    private Project selected;

    public ProjectController() {
    }

    public Date getSelectedProjectStartDate() {
        if (selected != null && selected.getStartDate() != null) {
            return LocalDateConverter.toDate(selected.getStartDate());
        } else {
            return null;
        }
    }
    
    public Date getSelectedProjectEndDate() {
        if (selected != null && selected.getEndDate() != null) {
            return LocalDateConverter.toDate(selected.getEndDate());
        } else {
            return null;
        }
    }
    
    public void setSelectedProjectStartDate(Date date){
        if (selected != null) {
            selected.setStartDate(LocalDateConverter.toLocalDate(date));
        } 
    }
    
    public void setSelectedProjectEndDate(Date date){
        if (selected != null) {
            selected.setEndDate(LocalDateConverter.toLocalDate(date));
        } 
    }

    public Project getSelected() {
        return selected;
    }

    public void setSelected(Project selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ProjectFacade getFacade() {
        return ejbFacade;
    }

    public Project prepareCreate() {
        selected = new Project();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("ProjectCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("ProjectUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("ProjectDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Project> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
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

    public Project getProject(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Project> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Project> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Project.class)
    public static class ProjectControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ProjectController controller = (ProjectController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "projectController");
            return controller.getProject(getKey(value));
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
            if (object instanceof Project) {
                Project o = (Project) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Project.class.getName()});
                return null;
            }
        }

    }

}
