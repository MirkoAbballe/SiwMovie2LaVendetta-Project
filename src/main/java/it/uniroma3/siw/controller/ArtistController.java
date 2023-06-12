package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
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

import it.uniroma3.siw.controller.validator.ArtistUpdateValidator;
import it.uniroma3.siw.controller.validator.ArtistValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.MovieRepository;

@Controller
public class ArtistController 
{
	@Autowired ArtistRepository artistRepository;
	@Autowired MovieRepository movieRepository;
	@Autowired ArtistValidator artistValidator;
	@Autowired ArtistUpdateValidator artistUpdateValidator;
	
	 
	@InitBinder
	public void initBinder(WebDataBinder binder) 
	{
	    binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
	}

	
	@GetMapping("/admin/formNewArtist")
	public String formNewArtist(Model model) 
	{
		model.addAttribute("artist", new Artist());
		return "admin/formNewArtist";	
	}
	
	@GetMapping("/public/formSearchArtists")
	public String formSearchArtists()
	{
		return "public/formSearchArtists";
	}
	
	@GetMapping("/admin/formUpdateArtist/{id}")
	public String getUpdateArtist(@PathVariable("id") Long id, Model model) 
	{
		model.addAttribute("artist", this.artistRepository.findById(id).get());
		return "admin/formUpdateArtist";
	}
	
	@PostMapping("/admin/artist/edit/{id}")
	public String editArtist(@Valid @ModelAttribute("artist") Artist artist, BindingResult bindingResult,@PathVariable("id")Long id,Model model,
			@RequestParam("name")String name,@RequestParam("surname")String surname, @RequestParam("nationality")String nationality,
			@RequestParam(required=false) @DateTimeFormat(pattern="yyyy-MM-dd")LocalDate dateOfBirth, 
			@RequestParam(required=false) @DateTimeFormat(pattern="yyyy-MM-dd")LocalDate dateOfDeath,
			@RequestParam("image") MultipartFile imageProfileFile) throws IOException
	{
		
		this.artistUpdateValidator.validate(artist, bindingResult);
		
		if (!bindingResult.hasErrors()) 
		{
		    
		    if (nationality != null && !nationality.isEmpty()) 
		    {
		        artist.setNationality(nationality);
		    }
		    
		    if (dateOfBirth != null) 
		    {
		        artist.setDateOfBirth(dateOfBirth);
		    }
		    
		    if (dateOfDeath != null) 
		    {
		        artist.setDateOfDeath(dateOfDeath);
		    }
		    else
		    	artist.setDateOfDeath(null);
		    
		    if (imageProfileFile != null) 
		    {
		    	byte[] imageBytes = imageProfileFile.getBytes();
	    		artist.setImage(imageBytes);
		    }
		    
		    this.artistRepository.save(artist);
		    model.addAttribute("artist",artist);
	
		    return "public/artist";
		}
		model.addAttribute("artist",artist);
		return "admin/formUpdateArtist";
	}
	
	
	@GetMapping("/admin/deleteArtist/{id}")
	public String deleteArtist(@PathVariable("id") Long id, Model model) 
	{
		Artist artist = this.artistRepository.findById(id).get();
		
		List<Movie> movieAct= artist.getActorsOfmovie();
		
		for(Movie movieActor: movieAct)
		{
			movieActor.getActors().remove(artist);
			this.movieRepository.save(movieActor);
		}
		
		List<Movie> movieDir= artist.getMovieasdirector();
		for(Movie movieDirector: movieDir)
		{
			movieDirector.setArtist_asdirector(null);
			this.movieRepository.save(movieDirector);
		}
		
        this.artistRepository.delete(artist);
        
		model.addAttribute("artists", this.artistRepository.findAll());
		
		return "admin/manageArtists";
		
	}
	
	@GetMapping("/admin/manageArtists")
	public String manageArtists(Model model) 
	{

		model.addAttribute("artists", this.artistRepository.findAll());
		return "admin/manageArtists";
		
	}
	
	@PostMapping("/public/searchArtists")
	public String SearchArtists(Model model, @RequestParam String name) 
	{
		model.addAttribute("artists", this.artistRepository.findByName(name));
		return "public/foundArtists.html";
	}
	
	
	@GetMapping("/public/artists/{id}")
	public String getArtist(@PathVariable("id") Long id, Model model) 
	{
		model.addAttribute("artist", this.artistRepository.findById(id).get());
		return "public/artist.html";
	}
	
	 @GetMapping("/public/artists")
	 public String showArtists(Model model) 
	 {
		 model.addAttribute("artists",this.artistRepository.findAll());
		 return "public/artists";
	 }
	
	
	@PostMapping("/admin/artists") 
	public String newArtist(@Valid @ModelAttribute("artist") Artist artist, BindingResult bindingResult, 
			Model model,@RequestParam("image") MultipartFile imageProfileFile)throws IOException 
	{
		
		this.artistValidator.validate(artist, bindingResult);
		
		if (!bindingResult.hasErrors()) 
		{
			byte[] imageBytes = imageProfileFile.getBytes();
			
    		artist.setImage(imageBytes);
    		this.artistRepository.save(artist);
			model.addAttribute("artist", artist);
			
			return "public/artist"; 
		}
		else 
		{
			return "admin/formNewArtist";
		}
	}
	
	@GetMapping("/image/artist/{id}")
	@ResponseBody
	public byte[] getImage(@PathVariable("id") Long id) 
	{
		
	    Artist artist = artistRepository.findById(id).orElse(null);
	    
	    if (artist != null && artist.getImage() != null) 
	    {
	        return artist.getImage();
	    }
	    return new byte[0];
	}
}