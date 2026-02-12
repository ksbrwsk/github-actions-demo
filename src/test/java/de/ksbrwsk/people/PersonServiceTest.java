package de.ksbrwsk.people;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(TestcontainersConfiguration.class)
class PersonServiceTest {

    @Autowired
    PersonService personService;

    @MockitoBean
    PersonRepository personRepository;

    @Test
    void testFindById() {
        var person = new Person(1L, "Alice");
        Mockito.when(personRepository.findById(1L)).thenReturn(Optional.of(person));
        var optionalPerson = personService.findById(1L);
        assertTrue(optionalPerson.isPresent());
        var foundPerson = optionalPerson.get();
        assertEquals(1L, foundPerson.id());
        assertEquals("Alice", foundPerson.name());
    }

    @Test
    void testFindByIdNotFound() {
        Mockito.when(personRepository.findById(-1L)).thenReturn(Optional.empty());
        var optionalPerson = personService.findById(-1L);
        assertTrue(optionalPerson.isEmpty());
    }

    @Test
    void testSave() {
        var person = new Person("New Person");
        var savedPerson = new Person(1L, "New Person");
        Mockito.when(personRepository.save(person)).thenReturn(savedPerson);
        var result = personService.save(person);
        assertEquals(1L, result.id());
        assertEquals("New Person", result.name());
    }

    @Test
    void testCount() {
        Mockito.when(personRepository.count()).thenReturn(42L);
        var count = personService.count();
        assertEquals(42L, count);
    }

    @Test
    void testDeleteAll() {
        personService.deleteAll();
        Mockito.verify(personRepository).deleteAll();
    }

    @Test
    void testFindAll() {
        var people = List.of(new Person(1L, "Alice"), new Person(2L, "Bob"));
        Mockito.when(personRepository.findAll()).thenReturn(people);
        var result = personService.findAll();
        assertEquals(2, result.size());
    }

    @Test
    void testSaveAll() {
        var people = List.of(new Person("Alice"), new Person("Bob"));
        var savedPeople = List.of(new Person(1L, "Alice"), new Person(2L, "Bob"));
        Mockito.when(personRepository.saveAll(people)).thenReturn(savedPeople);
        var result = personService.saveAll(people);
        assertEquals(2, result.size());
    }

    @Test
    void testDelete() {
        var person = new Person(1L, "Alice");
        personService.delete(person);
        Mockito.verify(personRepository).delete(person);
    }

    @Test
    void testFindTopByOrderByIdAsc() {
        var person = new Person(1L, "Alice");
        Mockito.when(personRepository.findTopByOrderByIdAsc()).thenReturn(Optional.of(person));
        var optionalPerson = personService.findTopByOrderByIdAsc();
        assertTrue(optionalPerson.isPresent());
        var foundPerson = optionalPerson.get();
        assertEquals(1L, foundPerson.id());
        assertEquals("Alice", foundPerson.name());
    }
}