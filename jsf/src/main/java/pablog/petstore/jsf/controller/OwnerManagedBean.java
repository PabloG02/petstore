package pablog.petstore.jsf.controller;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import pablog.petstore.domain.entities.Owner;
import pablog.petstore.service.OwnerService;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class OwnerManagedBean implements Serializable {

    @EJB
    private OwnerService ownerService;

    private List<Owner> owners;
    private String newLogin;
    private String newPassword;

    @PostConstruct
    public void init() {
        loadOwners();
    }

    public void loadOwners() {
        this.owners = ownerService.list();
    }

    public void createOwner() {
        try {
            Owner owner = new Owner(newLogin, newPassword);
            ownerService.create(owner);
            
            // Reset form
            this.newLogin = null;
            this.newPassword = null;
            
            // Reload list
            loadOwners();
            
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Owner created successfully"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void deleteOwner(String login) {
        try {
            ownerService.remove(login);
            loadOwners();
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Owner deleted successfully"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not delete owner."));
        }
    }

    // Getters and Setters
    public List<Owner> getOwners() {
        return owners;
    }

    public String getNewLogin() {
        return newLogin;
    }

    public void setNewLogin(String newLogin) {
        this.newLogin = newLogin;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
