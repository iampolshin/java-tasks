package ru.mail.polis.homework.oop.vet.animals;

import ru.mail.polis.homework.oop.vet.AbstractAnimal;
import ru.mail.polis.homework.oop.vet.MoveType;
import ru.mail.polis.homework.oop.vet.WildAnimal;

public class Shark extends AbstractAnimal implements WildAnimal {
    private String organizationName;

    public Shark() {
        super(0);
    }

    @Override
    public String say() {
        return "Clack Clack";
    }

    @Override
    public MoveType moveType() {
        return MoveType.SWIM;
    }

    @Override
    public String getOrganizationName() {
        return organizationName;
    }

    @Override
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
}
