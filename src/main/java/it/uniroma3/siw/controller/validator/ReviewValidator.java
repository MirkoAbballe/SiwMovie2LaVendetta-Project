package it.uniroma3.siw.controller.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;

@Component
public class ReviewValidator implements Validator 
{

	
	@Override 
	public void validate(Object o, Errors errors) 
	{
		
		Review review=(Review)o;
		
		if(review.getTitle().length()>254)
			errors.rejectValue("title","maxcharplot");
		if(review.getText().length()>254)
			errors.rejectValue("text", "maxcharplot");
	}
	
	@Override
	public boolean supports(Class<?> aClass) 
	{
		return Movie.class.equals(aClass);
	}
}
