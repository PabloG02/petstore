package pablog.petstore.jsf.controller;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import pablog.petstore.domain.entities.AnimalType;
import pablog.petstore.domain.entities.Pet;
import pablog.petstore.service.PetService;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Named
@ViewScoped
public class PetManagedBean implements Serializable {

    @EJB
    private PetService petService;

    private List<Pet> pets;
    
    // Form fields
    private String name;
    private AnimalType type;
    private Date birth;

    @PostConstruct
    public void init() {
        loadPets();
    }

    public void loadPets() {
        this.pets = petService.list();
    }

    public void createPet() {
        try {
            Pet pet = new Pet(name, type, birth);
            // The service will assign the owner based on the logged-in user
            petService.create(pet);
            
            // Reset form
            this.name = null;
            this.type = null;
            this.birth = null;
            
            loadPets();
            
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Pet added successfully"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void deletePet(int id) {
        try {
            petService.remove(id);
            loadPets();
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Pet removed successfully"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not remove pet."));
        }
    }

    public AnimalType[] getAnimalTypes() {
        return AnimalType.values();
    }

    // Getters and Setters
    public List<Pet> getPets() {
        return pets;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AnimalType getType() {
        return type;
    }

    public void setType(AnimalType type) {
        this.type = type;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }
}
