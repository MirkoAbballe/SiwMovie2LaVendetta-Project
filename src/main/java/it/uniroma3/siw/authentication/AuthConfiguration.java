package it.uniroma3.siw.authentication;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.repository.CredentialsRepository;
import it.uniroma3.siw.repository.UserRepository;

import javax.sql.DataSource;

import static it.uniroma3.siw.model.Credentials.ADMIN_ROLE;
import static it.uniroma3.siw.model.Credentials.DEFAULT_ROLE;
@Configuration
@EnableWebSecurity
public class AuthConfiguration extends WebSecurityConfigurerAdapter 
{
	
	@Autowired DataSource datasource;
	@Override
	protected void configure(HttpSecurity http) throws Exception 
	{
		http.anonymous().authorities("ROLE_ANONYMOUS");
		
		http.authorizeRequests() //authorization paragraph: qui definiamo chi può accedere alle risorse
		.antMatchers(HttpMethod.GET, "/", "/signup", "/login", "/default", "/css/**","/images/**","/image/**").permitAll() //chiunque (autenticato o no) può accedere alle pagine index, login, register, ai css e alle immagini
		
		.antMatchers(HttpMethod.POST, "/login", "/signup", "/register").permitAll() //chiunque (autenticato o no) può mandare richieste POST al punto di accesso per login e register
		
		.antMatchers(HttpMethod.GET, "/admin/**").hasAnyAuthority(ADMIN_ROLE) //solo gli utenti autenticati con ruolo ADMIN possono accedere a risorse con path /admin/** per richieste HTTP di tipo GET
		.antMatchers(HttpMethod.POST, "/admin/**").hasAnyAuthority(ADMIN_ROLE)//solo gli utenti autenticati con ruolo ADMIN possono accedere a risorse con path /admin/** per richieste HTTP di tipo POST
		
		
		.antMatchers(HttpMethod.GET, "/default/**").hasAnyAuthority(DEFAULT_ROLE) //solo gli utenti autenticati con ruolo DEFAULT possono accedere a risorse con path /default/** per richieste HTTP di tipo GET
		.antMatchers(HttpMethod.POST,"/default/**").hasAnyAuthority(DEFAULT_ROLE) //solo gli utenti autenticati con ruolo DEFAULT possono accedere a risorse con path /default/** per richieste HTTP di tipo POST
		
		.antMatchers(HttpMethod.GET, "/public/**").hasAnyAuthority("ROLE_ANONYMOUS",DEFAULT_ROLE) //solo gli utenti con ruolo ROLE_ANONYMOUS e DEFAULT possono accedere a risorse con path /public/** per richieste HTTP di tipo GET
		.antMatchers(HttpMethod.POST,"/public/**").hasAnyAuthority("ROLE_ANONYMOUS",DEFAULT_ROLE) //solo gli utenti con ruolo ROLE_ANONYMOUS e DEFAULT possono accedere a risorse con path /public/** per richieste HTTP di tipo POST
		
		
		.anyRequest().authenticated() //tutti gli utenti autenticati possono accere alle pagine rimanenti
		//login paragraph: qui definiamo come è gestita l'autenticazione
		.and().formLogin() //usiamo il protocollo formlogin
		
		.loginPage("/login") //la pagina di login viene restituita da "/login".
		
		.defaultSuccessUrl("/default",true) //se il login ha successo si viene reindirizzati al path /default
		
		//logout paragraph: qui definiamo il logout
		.and()
		.logout()
		.logoutUrl("/logout") //il logout è attivato con una richiesta GET a "/logout"
		.logoutSuccessUrl("/") //in caso di successo, si viene reindirizzati alla richiesta "/".
		.invalidateHttpSession(true)
		.deleteCookies("JSESSIONID")
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
		.clearAuthentication(true).permitAll();
	}
	
	/**
	* This method provides the SQL queries to get username and password.
	*/
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception 
	{
		auth.jdbcAuthentication()
		
		.dataSource(this.datasource) //use the autowired datasource to access the saved credentials
		//retrieve username and role
		.authoritiesByUsernameQuery("SELECT username, role FROM credentials WHERE username=?")
		//retrieve username, password and a bool specifying whether the user is enabled (always enabled for us) 
		.usersByUsernameQuery("SELECT username, password, 1 as enabled FROM credentials WHERE username=?");
		
		//PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
	    
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        auth.inMemoryAuthentication()
            .passwordEncoder(passwordEncoder)
            .withUser("admin")
            .password(passwordEncoder.encode("admin"))
            .roles("ADMIN");
        
        auth.userDetailsService(userDetailsService());
	}
	
	
	/**
	* This method defines a "passwordEncoder" Component (a Bean in the EE slang).
	* The passwordEncoder Bean is used to encrypt and decrpyt the Credentials passwords.
	*/
	@Bean
	PasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
	
	
	/*
	@Bean
	public CommandLineRunner initDB(UserRepository userRepository, CredentialsRepository credentialsRepository) {
	    return (args) -> 
	    {
	        // creazione dell'utente
	    	
	        Credentials adminCredentials = new Credentials();
	        adminCredentials.setUsername("admin");
	        adminCredentials.setPassword(new BCryptPasswordEncoder().encode("admin"));

	        // creazione delle credenziali associate all'utente
	        adminCredentials.setRole("ADMIN");
	        credentialsRepository.save(adminCredentials);
	    };
	    
	}
	*/
}

	

