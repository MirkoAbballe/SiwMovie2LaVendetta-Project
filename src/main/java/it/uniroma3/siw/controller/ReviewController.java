package it.uniroma3.siw.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import it.uniroma3.siw.controller.validator.ReviewValidator;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.service.CredentialsService;

@Controller
public class ReviewController 
{
	@Autowired ReviewRepository reviewRepository;
	@Autowired MovieRepository movieRepository;
	@Autowired ArtistRepository artistRepository;
	@Autowired ReviewValidator reviewValidator;
	@Autowired private CredentialsService credentialsService;

	@GetMapping("/default/review/{reviewid}/{movieid}")
	public String myReview(Model model,@PathVariable("reviewid") Long reviewid,
            				@PathVariable("movieid") Long movieid)
	{
		model.addAttribute("review",this.reviewRepository.findById(reviewid).get());
		model.addAttribute("movie",this.reviewRepository.findById(movieid).get());
		
		return "default/reviewMovie";
	}
	
	@GetMapping("/admin/Reviews")
	public String Reviews(Model model)
	{
		model.addAttribute("reviews",this.reviewRepository.findAll());
		return "admin/Reviews";
	}
	
	
	@GetMapping("/admin/deleteReview/{id}")
	public String deleteReview(@PathVariable("id") Long id, Model model)
	{
		
		Review review = this.reviewRepository.findById(id).get();
		
		Movie movie=review.getMovie();
		
	
        this.reviewRepository.delete(review);
        
        float valutazione=0;
    	
    	List<Review> AllReviewMovie=this.reviewRepository.findByMovie(movie);
    	
    	for(Review reviews: AllReviewMovie)
    	{
    		valutazione=valutazione+reviews.getValutation();
    	}
    	
    	if(AllReviewMovie.size()==0)
    		movie.setScore(0);
    	else
    	{
    		float val= (float)(valutazione/AllReviewMovie.size());
    		movie.setScore(val);
    	}
    	
    	this.movieRepository.save(movie);
    		
    	model.addAttribute("reviews", this.reviewRepository.findAll());
		
		return "admin/Reviews";
	}
	
	
	@GetMapping("/default/Newreview/{id}")
	public String getNewReview(Model model,@PathVariable("id") Long id)
	{
		Movie movie=this.movieRepository.findById(id).get();
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
    	
    	List<Review>reviewMovie= this.reviewRepository.findByMovie(movie);
    	for(Review review: reviewMovie)
    	{
    		if( review.getWriter().getId() == credentials.getUser().getId() ) 
    		{
    			List<Movie> movies = this.movieRepository.findAllByOrderByScoreDesc();
    			int size=movies.size();
    			model.addAttribute("movies",movies);
    			model.addAttribute("size",size);
    			return "index";
    		}
    	}
    		
		model.addAttribute("review", new Review());
		model.addAttribute("movie",this.movieRepository.findById(id).get());
		
		return "default/newReviewMovie";
	}
	
	
	@GetMapping("/default/myReviews")
	public String myReviews(Model model)
	{
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
    	
		model.addAttribute("reviews", this.reviewRepository.findByWriter(credentials.getUser()));
		
		return "default/myReviews";
	}
	
	
	
	@PostMapping("/default/setReview/{id}")
	public String newReview(@Valid @ModelAttribute("review") Review review,BindingResult bindingResult,
	                       Model model,@PathVariable("id") Long id)
	{
		
		Movie movie= this.movieRepository.findById(id).get();
		
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
    	
    	
	    this.reviewValidator.validate(review, bindingResult);

	    	if (!bindingResult.hasErrors()) 
	    	{
	    		this.movieRepository.save(movie);
	    		
	    		credentials.getUser().setUsername(credentials.getUsername());
	    		
	    		review.setWriter(credentials.getUser());
	    		review.setMovie(movie);
	    		review.setDate(LocalDateTime.now());
	    		
	    		this.reviewRepository.save(review);
	    		
	    		
	    		float valutazione=0;
		    	
		    	List<Review> AllReviewMovie=this.reviewRepository.findByMovie(movie);
		    	
		    	for(Review reviews: AllReviewMovie)
		    	{
		    		valutazione=valutazione+reviews.getValutation();
		    	}
		    		
		    		float val= (float)(valutazione/AllReviewMovie.size());
		    		movie.setScore(val);
		    		this.movieRepository.save(movie);
		    	
		    	model.addAttribute("movie",movie);
		    	model.addAttribute("review",review);
		    	return "public/movie";
	    	}
	    	else
	    	{
	    		model.addAttribute("movie",movie);
	    		return "default/newReviewMovie";
	    	}	
	}
}