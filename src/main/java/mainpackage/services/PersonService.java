package mainpackage.services;

import mainpackage.dto.PersonDTO;
import mainpackage.models.Person;
import mainpackage.repositories.PersonRepo;
import mainpackage.util.PersonNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PersonService {
    private final PersonRepo personRepo;

    @Autowired
    public PersonService(PersonRepo personRepo) {
        this.personRepo = personRepo;
    }

    public List<Person> findAll(){
        return personRepo.findAll();
    }
    public Person findById(int id){
        Optional<Person> Person = personRepo.findById(id);
        return Person.orElseThrow(PersonNotFoundException::new);
    }

    @Transactional
    public void save(Person person){
        enrichPerson(person);
        personRepo.save(person);
    }

    @Transactional
    public void update(int id, Person person){
        if(personRepo.findById(id).isEmpty())
            throw new PersonNotFoundException();
        person.setId(id);
        enrichPerson(person);
        personRepo.save(person);
    }

    @Transactional
    public void deleteById(int id){
        if(personRepo.findById(id).isEmpty())
            throw new PersonNotFoundException();
       personRepo.deleteById(id);
    }
    private void enrichPerson(Person person) {
        Optional<Person> foundedPerson = personRepo.findById(person.getId());
        if (foundedPerson.isEmpty()){
            person.setCreatedAt(LocalDateTime.now());
            person.setCreatedWho("admin");
        } else {
            person.setCreatedAt(foundedPerson.get().getCreatedAt());
            person.setCreatedWho(foundedPerson.get().getCreatedWho());
        }

        person.setUpdatedAt(LocalDateTime.now());

    }
}
