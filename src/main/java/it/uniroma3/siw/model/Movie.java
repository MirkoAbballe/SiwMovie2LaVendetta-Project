package it.uniroma3.siw.model;


import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.Hibernate;

@Entity
public class Movie {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)

	private Long id;
	
	@NotNull
	@NotBlank 
	private String title;

	@Min(1900)
	@NotNull
	private Integer year;

	@Lob
	@Column(name = "image")
	private byte[] image;

	@ManyToOne
	private Artist artist_asdirector;

	@OneToMany(mappedBy = "movie")
	private List<Review> movieReviews;

	@ManyToMany(fetch = FetchType.LAZY)
	private Set<Artist> actors; 

	
	private String plot;

	private Boolean wonOscar = false;

	private String genre;

	private float score;

	public Long getId() 
	{
		return this.id;
	}

	public void setId(Long id) 
	{
		this.id = id;
	}

	public String getTitle() 
	{
		return this.title;
	}

	public void setTitle(String title) 
	{
		this.title = title;
	}

	public Integer getYear() 
	{
		return this.year;
	}

	public void setYear(Integer year) 
	{
		this.year = year;
	}

	public String getPlot() 
	{
		return plot;
	}

	public void setPlot(String plot) 
	{
		this.plot = plot;
	}

	public Artist getArtist_asdirector() 
	{
		return artist_asdirector;
	}

	public void setArtist_asdirector(Artist artist_asdirector) 
	{
		this.artist_asdirector = artist_asdirector;
	}

	public List<Review> getMovieReviews() 
	{
		return this.movieReviews;
	}

	public void setMovieReviews(List<Review> movieReviews) 
	{
		this.movieReviews = movieReviews;
	}

	@Override
	public int hashCode() 
	{
		return Objects.hash(title, year);
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Movie other = (Movie) obj;
		return Objects.equals(title, other.title) && year.equals(other.year);
	}

	public String getGenre() 
	{
		return genre;
	}

	public void setGenre(String genre) 
	{
		this.genre = genre;
	}

	public float getScore() 
	{
		return score;
	}

	public void setScore(float score) 
	{
		this.score = score;
	}
	
	public byte[] getImage() 
	{
		return image;
	}

	public void setImage(byte[] image) 
	{
		this.image = image;
	}

	public Set<Artist> getActors() 
	{
		Hibernate.initialize(actors);
		return actors;
	}

	public void setActors(Set<Artist> actors) 
	{
		this.actors = actors;
	}

	public Boolean getWonOscar() 
	{
		return wonOscar;
	}

	public void setWonOscar(Boolean wonOscar) 
	{
		this.wonOscar = wonOscar;
	}

}