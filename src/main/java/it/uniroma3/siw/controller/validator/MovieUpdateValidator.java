package it.uniroma3.siw.controller.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Movie;

@Component
public class MovieUpdateValidator implements Validator 
{
	
	@Override 
	public void validate(Object o, Errors errors) 
	{
		Movie movie= (Movie)o;
			if(movie.getPlot().length() > 254 ) 
			{
				errors.rejectValue("plot", "maxcharplot");
			}
			
			if(movie.getGenre().equals(""))
			{
				errors.rejectValue("genre", "genreinvalid");
			}
	}
	
	@Override
	public boolean supports(Class<?> aClass) 
	{
		return Movie.class.equals(aClass);
	}
}
