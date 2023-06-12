package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import it.uniroma3.siw.model.Movie;

public interface MovieRepository extends CrudRepository<Movie, Long> 
{
	public List<Movie> findByYear(Integer year);
	public List<Movie> findByGenre(String genre);
	public List<Movie> findByYearAndGenre(Integer year,String genre);
	public List<Movie> findAllByOrderByScoreDesc();
	public boolean existsByTitleAndYear(String title, Integer year);
}