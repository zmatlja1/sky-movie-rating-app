package uk.sky.pm.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import uk.sky.pm.domain.Movie;
import uk.sky.pm.dto.MovieDto;

public interface MovieRepository extends PagingAndSortingRepository<Movie, Long>, CrudRepository<Movie, Long> {

    @Query(value = """
        select m.name, avg(r.rating) as movieRating from pm_movie m
        left join pm_rating r on r.movie_id=m.id
        group by m.name
        """,
        countQuery = """
        select count(distinct m.name)
        from pm_movie m
        left join pm_rating r on r.movie_id = m.id
        """, nativeQuery = true)
    Page<MovieDto> findAllMovies(Pageable pageable);

}
