package uk.sky.pm.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class CommonEntity {

    private Long id;
    private Long version;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    public Long getId() {
        return id;
    }

    @Version
    @Column(name = "VERSION")
    public Long getVersion() {
        return version;
    }

}
