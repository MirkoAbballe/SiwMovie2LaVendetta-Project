package it.uniroma3.siw.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Review 
{

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	
	private Long id;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime date;
	
	
	private String title; 
	
	private String text;
	
	private Integer valutation;
	
	@ManyToOne
	private Movie movie; 
	
	@ManyToOne
	private User writer;


	public Long getId() 
	{
		return id;
	}


	public void setId(Long id) 
	{
		this.id = id;
	}
	
	
	public LocalDateTime getDate() 
	{
		return date;
	}


	public void setDate(LocalDateTime date) 
	{
		this.date = date;
	}

	public String getText()
	{
		return text;
	}


	public void setText(String text) 
	{
		this.text = text;
	}


	public Integer getValutation() 
	{
		return valutation;
	}


	public void setValutation(Integer valutation)
	{
		this.valutation = valutation;
	}


	public User getWriter() 
	{
		return writer;
	}

	public void setWriter(User writer) 
	{
		this.writer = writer;
	}

	
	public Movie getMovie() 
	{
		return this.movie;
	}


	public void setMovie(Movie movie) 
	{
		this.movie = movie;
	}


	public String getTitle() 
	{
		return title;
	}


	public void setTitle(String title) 
	{
		this.title = title;
	}
}
