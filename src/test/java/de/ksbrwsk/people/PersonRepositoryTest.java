package de.ksbrwsk.people;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PersonRepositoryTest implements PostgresContainer {

    @Autowired
    PersonRepository personRepository;

    @BeforeEach
    void setUp() {
        List<Person> people = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            people.add(new Person("Person@" + i));
        }
        personRepository.deleteAll();
        personRepository.saveAll(people);
    }

    private Person fetchFirstPerson() {
        return personRepository.findTopByOrderByIdAsc()
                .orElseThrow();
    }

    @Test
    void testFindAll() {
        List<Person> people = personRepository.findAll();
        assertEquals(100, people.size());
    }

    @Test
    void testFindById() {
        var firstPerson = fetchFirstPerson();
        var optionalPerson = personRepository.findById(firstPerson.id());
        assertTrue(optionalPerson.isPresent());
        var person = optionalPerson.get();
        assertEquals(firstPerson.id(), person.id());
        assertEquals(firstPerson.name(), person.name());
    }

    @Test
    void testFindByIdNotFound() {
        var optionalPerson = personRepository.findById(-1L);
        assertFalse(optionalPerson.isPresent());
    }

    @Test
    void testSave() {
        Person person = new Person("New Person");
        Person savedPerson = personRepository.save(person);
        assertNotNull(savedPerson.id());
        assertEquals("New Person", savedPerson.name());
    }

    @Test
    void testDelete() {
        var firstPerson = fetchFirstPerson();
        personRepository.delete(firstPerson);
        var optionalPerson = personRepository.findById(firstPerson.id());
        assertFalse(optionalPerson.isPresent());
    }

    @Test
    void testDeleteAll() {
        personRepository.deleteAll();
        List<Person> people = personRepository.findAll();
        assertTrue(people.isEmpty());
    }

    @Test
    void testSaveAll() {
        List<Person> newPeople = List.of(
                new Person("Alice"),
                new Person("Bob"),
                new Person("Charlie")
        );
        List<Person> savedPeople = personRepository.saveAll(newPeople);
        assertEquals(3, savedPeople.size());
    }

    @Test
    void testCount() {
        long count = personRepository.count();
        assertEquals(100, count);
    }

    @Test
    void testFindTopByOrderByIdAsc() {
        var firstPerson = fetchFirstPerson();
        assertNotNull(firstPerson.id());
        assertEquals("Person@1", firstPerson.name());
    }

    @Test
    void testFindTopByOrderByIdAscEmpty() {
        personRepository.deleteAll();
        var optionalPerson = personRepository.findTopByOrderByIdAsc();
        assertFalse(optionalPerson.isPresent());
    }
}