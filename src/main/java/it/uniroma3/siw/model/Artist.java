package it.uniroma3.siw.model;


import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Artist 
{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	
	private Long id;
	private String name;
	private String surname;
	
	
	@Basic(fetch = FetchType.LAZY)
	@Lob
	@Column(name = "image")
	private byte[] image;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateOfBirth; 
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateOfDeath; 
	
	private String nationality;
	
	
	@OneToMany(mappedBy= "artist_asdirector")
	private List<Movie> movieasdirector;
	
	
	@ManyToMany(mappedBy="actors")
	private List<Movie> actorsOfmovie;
	
	
	public Long getId()
	{
		return this.id;
	}
	
	public void setId(Long id)
	{
		this.id = id;
	}
	
	public String getSurname() 
	{
		return this.surname;
	}
	
	public void setSurname(String surname)
	{
		this.surname = surname;
	}
	
	public LocalDate getDateOfBirth() 
	{
		return this.dateOfBirth;
	}
	
	public void setDateOfBirth(LocalDate dateOfBirth)
	{
		this.dateOfBirth = dateOfBirth;
	}
	
	public String getNationality() 
	{
		return this.nationality;
	}
	
	public void setNationality(String nationality)
	{
		this.nationality = nationality;
	}
	
	public String getName() 
	{
		return this.name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}
	
	public List<Movie> getMovieasdirector() 
	{
		return movieasdirector;
	}

	public void setMovieasdirector(List<Movie> movieasdirector)
	{
		this.movieasdirector = movieasdirector;
	}

	@Override
	public int hashCode() 
	{
		return Objects.hash(name, surname);
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
		Artist other = (Artist) obj;
		return Objects.equals(name, other.name) && Objects.equals(surname, other.surname);
	}

	public byte[] getImage() 
	{
		return image;
	}

	public void setImage(byte[] image) 
	{
		this.image = image;
	}

	public LocalDate getDateOfDeath() 
	{
		return dateOfDeath;
	}

	public void setDateOfDeath(LocalDate dateOfDeath) 
	{
		this.dateOfDeath = dateOfDeath;
	}

	public List<Movie> getActorsOfmovie() 
	{
		return actorsOfmovie;
	}

	public void setActorsOfmovie(List<Movie> actorsOfmovie) 
	{
		this.actorsOfmovie = actorsOfmovie;
	}
}