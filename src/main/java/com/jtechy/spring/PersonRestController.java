package com.jtechy.spring;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.jtechy.spring.model.Person;
import com.jtechy.spring.service.PersonService;

@RestController
public class PersonRestController {
	
	@Autowired(required=true)
	@Qualifier(value="personService")
	private PersonService personService;	
	
	public void setPersonService(PersonService ps){
		this.personService = ps;
	}
	
	@RequestMapping(value = "/users", method = RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Person>>  restListPersons(Model model) {
		List<Person> personList =  this.personService.listPersons();
		return new ResponseEntity<List<Person>>(personList, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/user/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Person> restGetUser(@PathVariable("id") int id) {
        System.out.println("Fetching User with id " + id);
        Person user = personService.getPersonById(id);
        if (user == null) {
            System.out.println("User with id " + id + " not found");
            return new ResponseEntity<Person>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Person>(user, HttpStatus.OK);
    }
	
	//For add and update person both
	@RequestMapping(value= "/user/add", method = RequestMethod.POST)
	public ResponseEntity<Void> restAddPerson(@RequestBody Person p, UriComponentsBuilder ucBuilder){
		
		if(p.getId() == 0){
			//new person, add it
			this.personService.addPerson(p);
		}else{
			//existing person, call update
			this.personService.updatePerson(p);
		}
		
		HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/user/{id}").buildAndExpand(p.getId()).toUri());
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
		
	}
	
	@RequestMapping("/user/remove/{id}")
    public ResponseEntity<Person> restRemovePerson(@PathVariable("id") int id){
		
        this.personService.removePerson(id);
        
        Person user = personService.getPersonById(id);
        if (user == null) {
            System.out.println("Unable to delete. User with id " + id + " not found");
            return new ResponseEntity<Person>(HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<Person>(HttpStatus.NO_CONTENT);
    }
 
    @RequestMapping("/user/edit/{id}")
    public ResponseEntity<Person> restEditPerson(@PathVariable("id") int id, @RequestBody Person person){
        Person person1 = this.personService.getPersonById(id);
        person1.setCountry(person.getCountry());
        person1.setName(person.getName());
        personService.updatePerson(person1);
        return new ResponseEntity<Person>(person, HttpStatus.OK);
    }
	
}
