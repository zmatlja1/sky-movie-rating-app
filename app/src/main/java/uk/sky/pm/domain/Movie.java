package uk.sky.pm.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "PM_MOVIE")
public class Movie extends CommonEntity {

    private String name;

    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
