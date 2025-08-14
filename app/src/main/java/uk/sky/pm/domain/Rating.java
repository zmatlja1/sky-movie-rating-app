package uk.sky.pm.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "PM_RATING")
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Rating extends CommonEntity {

    private Integer rating;
    private User user;
    private Movie movie;

    @Column(name = "RATING")
    public Integer getRating() {
        return rating;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    public User getUser() {
        return user;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MOVIE_ID")
    public Movie getMovie() {
        return movie;
    }

}
