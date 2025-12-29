package pablog.petstore.rest.entity;

import pablog.petstore.domain.entities.AnimalType;
import pablog.petstore.domain.entities.Pet;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

public class PetData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private AnimalType animal;
    private Date birth;

    protected PetData() {
    }

    public PetData(String name, AnimalType animal, Date birth) {
        this.name = name;
        this.animal = animal;
        this.birth = birth;
    }

    public String getName() {
        return name;
    }

    public AnimalType getAnimal() {
        return animal;
    }

    public Date getBirth() {
        return birth;
    }

    public Pet assignData(Pet pet) {
        pet.setName(this.name);
        pet.setAnimal(this.animal);
        pet.setBirth(this.birth);

        return pet;
    }

    public Pet toPet() {
        return new Pet(this.name, this.animal, this.birth);
    }
}
