package de.ksbrwsk.people;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PersonTest {

    @Test
    void shouldCreatePersonWithName() {
        Person person = new Person("Alice");
        assertEquals("Alice", person.name());
    }

    @Test
    void shouldCreatePersonWithIdAndName() {
        Person person = new Person(1L, "Bob");
        assertEquals(1L, person.id());
        assertEquals("Bob", person.name());
    }
}