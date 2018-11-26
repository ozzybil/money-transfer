package com.assignment.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import org.hibernate.validator.constraints.Length;

/**
 * Base class for {@link Account} and {@link Transaction} since both has id in common
 */
public class Entity {

    @Length(max = 16)
    private String id;

    Entity() { }

    public Entity(String id) {
        this.id = id;
    }

    @JsonGetter
    public String getId() {
        return id;
    }

    protected void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "id=" + id;
    }
}
