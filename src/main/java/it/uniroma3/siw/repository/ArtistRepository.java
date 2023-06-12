package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;


import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;


public interface ArtistRepository extends CrudRepository<Artist, Long> {

	
	public List<Artist> findByName(String name);
	
	public List<Artist> findByMovieasdirector(Movie movie);
	
	public List<Artist> getArtistByActorsOfmovieNotContains(Movie movie);

	
	public boolean existsByNameAndSurname(String name, String surname);
	
	public boolean existsByMovieasdirector(Movie movie);
	

	
	
}