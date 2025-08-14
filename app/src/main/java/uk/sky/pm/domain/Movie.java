package uk.sky.pm.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "PM_MOVIE")
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Movie extends CommonEntity {

    private String name;

    @Column(name = "NAME")
    public String getName() {
        return name;
    }

}
