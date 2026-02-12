package de.ksbrwsk.people;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static de.ksbrwsk.people.PersonRestController.API;

@RestController
@RequestMapping(API)
class PersonRestController {

    protected final static String API = "/api/people";

    private final PersonService personService;

    PersonRestController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public ResponseEntity<List<Person>> handleFindAll() {
        var people = personService.findAll();
        return ResponseEntity.ok(people);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> handleFindById(@NotNull @PathVariable Long id) {
        var person = personService.findById(id);
        return person.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Person> handleCreate(@NotNull @Valid @RequestBody Person person) {
        var createdPerson = personService.save(person);
        if (createdPerson != null) {
            return ResponseEntity.created(URI.create(API + "/" + createdPerson.id()))
                    .body(createdPerson);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> handleDelete(@NotNull @PathVariable Long id) {
        var person = personService.findById(id);
        if (person.isPresent()) {
            personService.delete(person.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Person> handleUpdate(@NotNull @PathVariable Long id,
                                               @NotNull @Valid @RequestBody Person person) {
        var existingPerson = personService.findById(id);
        if (existingPerson.isPresent()) {
            var updatedPerson = new Person(id, person.name());
            personService.save(updatedPerson);
            return ResponseEntity.ok(updatedPerson);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
