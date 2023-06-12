package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import it.uniroma3.siw.controller.validator.MovieUpdateValidator;
import it.uniroma3.siw.controller.validator.MovieValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.service.CredentialsService;

@Controller
public class MovieController 
{
	@Autowired MovieRepository movieRepository;
	
	@Autowired ArtistRepository artistRepository;
	
	@Autowired MovieValidator movieValidator;
	
	@Autowired MovieUpdateValidator movieUpdateValidator;
	
	@Autowired ReviewRepository reviewRepository;
	
	@Autowired 
	private CredentialsService credentialsService;
	
	 
	@InitBinder
	public void initBinder(WebDataBinder binder) 
	{
	    binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
	}

	
	@GetMapping("/admin/formNewMovie")
	public String formNewMovie(Model model) 
	{
		model.addAttribute("movie", new Movie());
		return "admin/formNewMovie";
	}

	@GetMapping("/admin/manageMovies")
	public String manageMovies(Model model) 
	{
		model.addAttribute("movies", this.movieRepository.findAll());
		return "admin/manageMovies";
	}
	
	
	@PostMapping("/admin/movie/edit/{id}")
	public String editMovie(@Valid @ModelAttribute("movie") Movie movie,BindingResult bindingResult,@PathVariable("id") Long id,Model model,
			@RequestParam("image") MultipartFile imageProfileFile,@RequestParam("plot")String plot,
			@RequestParam("genre")String genre, @RequestParam(name = "wonOscar", required = false) Boolean wonOscar) throws IOException
	{		
		  
		  this.movieUpdateValidator.validate(movie, bindingResult);

		  if (!bindingResult.hasErrors()) 
		  {
			 
    		 if (plot != null && !plot.isEmpty()) 
    		 {
    			 movie.setPlot(plot);
    		 }
		    
    		 if (genre != null && !genre.isEmpty()) 
    		 {
    			 movie.setGenre(genre);
    		 }
		 
    		 if (wonOscar != null && wonOscar == true ) 
    		 {
    			 movie.setWonOscar(true);
    		 }
    		 
    		 if (imageProfileFile != null) 
    		 {
    			 byte[] imageBytes = imageProfileFile.getBytes();
    			 
    			 movie.setImage(imageBytes);
    		 }
		 
    		 this.movieRepository.save(movie);
    		 model.addAttribute("movie",movie);
    		 return "public/movie";
		  }
    	  else 
    	  {
    		return "admin/formUpdateMovie";
    	  }
	}
	
	@GetMapping("/admin/deleteMovie/{id}")
	public String deleteMovie(@PathVariable("id") Long id, Model model)
	{
		
		Movie movie = this.movieRepository.findById(id).get();
		
		List<Review> reviews= this.reviewRepository.findByMovie(movie);
        
        
		//Cancello prima le recensioni associate al film..
        for(Review review: reviews) 
        	this.reviewRepository.delete(review);
        
        
        this.movieRepository.delete(movie);
		model.addAttribute("movies", this.movieRepository.findAll());
		
		return "admin/manageMovies";
	}

	
	@GetMapping("/admin/addDirectorToMovie/{id}")
	public String addDirectorToMovie(@PathVariable("id") Long id, Model model) 
	{
		
		Artist regista= this.movieRepository.findById(id).get().getArtist_asdirector();
		
		
		List<Artist> artisti= (List<Artist>) this.artistRepository.findAll();
		
		artisti.remove(regista);
		
		
		model.addAttribute("movie",this.movieRepository.findById(id).get());
		model.addAttribute("artists",artisti);
		
		return "admin/directorsToAdd";
	}
	
	
	@GetMapping("/admin/updateActorsOfMovie/{id}")
	public String updateActorsOfMovie(@PathVariable("id") Long id, Model model)
	{
		Movie movie= this.movieRepository.findById(id).get();
		
		model.addAttribute("actorsmovie",movie.getActors());
		
		model.addAttribute("movie",movie);
		
		model.addAttribute("artists",this.artistRepository.getArtistByActorsOfmovieNotContains(movie)); 
		/*restituisco tutti gli artisti che non sono attori di questo film*/
		
		return "admin/actorsToAdd";
	}
	
	@GetMapping("/admin/addActorToMovie/{artistid}/{movieid}")
	public String addActorToMovie(@PathVariable("artistid") Long artistid,@PathVariable("movieid")Long movieid, Model model) 
	{
		
	        Movie movie = this.movieRepository.findById(movieid).get();
	        Artist artist = this.artistRepository.findById(artistid).get();
	        
	        Set<Artist> movieActors = movie.getActors();
	        movieActors.add(artist);
	        
	        movie.setActors(movieActors);
	        this.movieRepository.save(movie);
	        
	        
	        artist.getActorsOfmovie().add(movie);
	        this.artistRepository.save(artist);

	        
	        model.addAttribute("movie",movie);
	        model.addAttribute("actorsmovie",this.movieRepository.findById(movieid).get().getActors());
	        
	        model.addAttribute("artists",this.artistRepository.getArtistByActorsOfmovieNotContains(movie));
	        
	        return "admin/actorsToAdd";
	    }
	
	
	@GetMapping("/admin/removeActorFromMovie/{artistid}/{movieid}")
	public String removeActorFromMovie(@PathVariable("artistid") Long artistid,@PathVariable("movieid")Long movieid, Model model) 
	{
		
	        Movie movie = this.movieRepository.findById(movieid).get();
	        Artist artist = this.artistRepository.findById(artistid).get();
	        
	        Set<Artist> movieActors = movie.getActors();
	        
	        movieActors.remove(artist);
	        movie.setActors(movieActors);
	        this.movieRepository.save(movie);
	        
	        artist.getActorsOfmovie().remove(movie);
	        this.artistRepository.save(artist);

	        model.addAttribute("movie",movie);
	        model.addAttribute("actorsmovie",this.movieRepository.findById(movieid).get().getActors());
	        model.addAttribute("artists",this.artistRepository.getArtistByActorsOfmovieNotContains(movie));
	        
	        return "admin/actorsToAdd";
	}

	@GetMapping("/admin/setDirectorToMovie/{artistid}/{movieid}")
	public String setDirectorToMovie(@PathVariable("artistid") Long artistid, @PathVariable("movieid") Long movieid,Model model) 
	{
		
		Movie movie= this.movieRepository.findById(movieid).get();
		Artist artist= this.artistRepository.findById(artistid).get();
		
		movie.setArtist_asdirector(artist);
		
		this.movieRepository.save(movie);

		model.addAttribute("movie",movie);
		model.addAttribute("artist_asdirector",artist);
        model.addAttribute("artists", movie.getArtist_asdirector()); 
		
		return "admin/formUpdateMovie";
	}
	

	@PostMapping("/admin/movies")
	public String newMovie(@Valid @ModelAttribute("movie") Movie movie,
	                       BindingResult bindingResult,
	                       Model model,
	                       @RequestParam("image") MultipartFile imageFile)throws IOException
	   {

	    this.movieValidator.validate(movie, bindingResult);

	    	if (!bindingResult.hasErrors()) 
	    	{
	    		
	    		byte[] imageBytes = imageFile.getBytes();
	        
	    		movie.setImage(imageBytes);
	        
	    		this.movieRepository.save(movie);
	    		model.addAttribute("movie", movie);
	    		return "/public/movie";
	    	}
	    	else 
	    	{
	    		return "admin/formNewMovie";
	    	}
	   }
	
	@GetMapping("/image/{id}")
	@ResponseBody
	public byte[] getImage(@PathVariable("id") Long id) 
	{
	    Movie movie = movieRepository.findById(id).orElse(null);
	    if (movie != null && movie.getImage() != null) 
	    {
	        return movie.getImage();
	    }
	    return new byte[0];
	}
	
	
	@GetMapping("/admin/formUpdateMovie/{id}")
	public String getUpdateMovie(@PathVariable("id") Long id, Model model) 
	{
		
		model.addAttribute("movie", this.movieRepository.findById(id).get());
		
		Movie movie=this.movieRepository.findById(id).get();
		Artist artist= this.movieRepository.findById(id).get().getArtist_asdirector();
		
		model.addAttribute("movie",movie);
		model.addAttribute("artist_asdirector",artist); 
        model.addAttribute("artists", movie.getArtist_asdirector());
        
		return "admin/formUpdateMovie";
	}
	
	
	
	@GetMapping("/public/movies/{id}")
	public String getMovie(@PathVariable("id") Long id, Model model, Authentication authentication) 
	{
		
		Movie movie= this.movieRepository.findById(id).get();
		
		
		
		List<Review>reviewAllMovie= this.reviewRepository.findByMovie(movie); /*contiene tutte le recensioni di tale film*/
	    if (authentication != null && authentication.isAuthenticated()) 
	    {
	    	
	        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	        Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
	        
	        
	        
	        for(Review review: reviewAllMovie)
	        {
	        	if( review.getWriter().getId().equals(credentials.getUser().getId())) 
	        	{
	        		model.addAttribute("review",review); //recensione dell'utente sul film
	        		model.addAttribute("reviews",reviewAllMovie); //tutte le recensioni del film
	        		model.addAttribute("director",movie.getArtist_asdirector()); //passaggio del regista
	        		model.addAttribute("actors",movie.getActors()); //passaggio degli attori
	        		model.addAttribute("movie", movie);
	        		return "public/movie";
	        	}
	        }
	    }
	    
		model.addAttribute("reviews",reviewAllMovie); //Passaggio della lista di tutte le recensioni associate al film
		model.addAttribute("director",movie.getArtist_asdirector());
		model.addAttribute("actors",movie.getActors()); 
	    model.addAttribute("review",null);
	    model.addAttribute("movie",movie);
	    return "public/movie";
	}

	@GetMapping("/public/movies")
	public String showMovies(Model model) 
	{
		model.addAttribute("movies", this.movieRepository.findAll());
		return "public/movies";
	}
	
	@GetMapping("/public/formSearchMovies")
	public String formSearchMovies() 
	{
		return "public/formSearchMovies";
	}

	@PostMapping("/public/searchMovies")
	public String searchMovies(Model model,@RequestParam String genre, @RequestParam Integer year)
	{
		if(genre == "" && year == null)
		{
			model.addAttribute("movies", null);
		}
		
		if(genre == "" && year != null)
		{
			model.addAttribute("movies", this.movieRepository.findByYear(year));
		}
		
		if(year == null && genre != "")
		{
			model.addAttribute("movies", this.movieRepository.findByGenre(genre));
		}
		
		if(genre != "" && year != null)
		{
			model.addAttribute("movies", this.movieRepository.findByYearAndGenre(year,genre));
		}
		
		return "public/foundMovies";
	}
}