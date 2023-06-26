package mainpackage.controllers;

import jakarta.validation.Valid;
import mainpackage.dto.PersonDTO;
import mainpackage.models.Person;
import mainpackage.services.PersonService;
import mainpackage.util.PersonErrorResponse;
import mainpackage.util.PersonNotCreatedException;
import mainpackage.util.PersonNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController //@Controller + @ResponseBody над каждым методом
@RequestMapping("/people")
public class MyController {
    private final PersonService personService;
    private final ModelMapper modelMapper;

    @Autowired
    public MyController(PersonService personService, ModelMapper modelMapper) {
        this.personService = personService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/sayHello")
    public String sayHello(){
        return "Hello world!";
    }

    @GetMapping("")
    public List<PersonDTO> getPeople(){
        return personService.findAll().stream().map(this::convertPersonToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/all")//равносильно предыдущему методу
    public ResponseEntity<List<PersonDTO>> allPeople(){
        return ResponseEntity.ok(personService.findAll().stream().map(this::convertPersonToDTO)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public PersonDTO getPerson(@PathVariable("id") int id){
        return convertPersonToDTO(personService.findById(id));
    }

    @PostMapping("")
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid PersonDTO personDTO, BindingResult bindingResult){

        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();//получить ошибки из bindingResult
            for (FieldError error:errors) {
                errorMsg.append(error.getField()).append(" - ")
                        .append(error.getDefaultMessage()).append("; ");
            }
            throw new PersonNotCreatedException(errorMsg.toString());//выбросить исключение с сообщением об ошибке
        }
        personService.save(convertDTOToPerson(personDTO));
        return ResponseEntity.ok(HttpStatus.OK);//HTTP ответ с пустым телом и статусом 200
    }



    @PutMapping("/{id}")
    public ResponseEntity<HttpStatus> edit(@PathVariable("id") int id, @RequestBody @Valid PersonDTO personDTO, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();//получить ошибки из bindingResult
            for (FieldError error:errors) {
                errorMsg.append(error.getField()).append(" - ")
                        .append(error.getDefaultMessage()).append("; ");
            }
            throw new PersonNotCreatedException(errorMsg.toString());//выбросить исключение с сообщением об ошибке
        }
        personService.update(id,convertDTOToPerson(personDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") int id){
        personService.deleteById(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }


    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotFoundException e){
        PersonErrorResponse response = new PersonErrorResponse("Person with this id wasn't found", System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotCreatedException e){
        PersonErrorResponse response = new PersonErrorResponse(e.getMessage(), System.currentTimeMillis());//получает сообщение ошибки от PersonNotCreatedException
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private Person convertDTOToPerson(PersonDTO personDTO) {
        return modelMapper.map(personDTO, Person.class);
    }

    private PersonDTO convertPersonToDTO(Person person) {
        return modelMapper.map(person, PersonDTO.class);
    }

}
