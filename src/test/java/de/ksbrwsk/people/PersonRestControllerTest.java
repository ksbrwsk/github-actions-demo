package de.ksbrwsk.people;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.Optional;

@WebFluxTest
class PersonRestControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockitoBean
    PersonService personService;

    @Test
    void handleNotFound() {
        webTestClient.get()
                .uri("/api/pepl")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void handleFindAll() {
        Mockito.when(personService.findAll()).thenReturn(List.of(
                new Person(1L, "Alice"),
                new Person(2L, "Bob")
        ));
        webTestClient.get()
                .uri("/api/people")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Person.class)
                .hasSize(2)
                .contains(
                        new Person(1L, "Alice"),
                        new Person(2L, "Bob")
                );
    }

    @Test
    void handleFindById() {
        Mockito.when(personService.findById(1L))
                .thenReturn(Optional.of(new Person(1L, "Alice")));
        webTestClient.get()
                .uri("/api/people/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Person.class)
                .isEqualTo(new Person(1L, "Alice"));
    }

    @Test
    void handleFindByIdNotFound() {
        Mockito.when(personService.findById(-1L)).thenReturn(Optional.empty());
        webTestClient.get()
                .uri("/api/people/-1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void handleCreatePerson() {
        var personToCreate = new Person("New Person");
        var createdPerson = new Person(1L, "New Person");
        Mockito.when(personService.save(personToCreate)).thenReturn(createdPerson);
        webTestClient.post()
                .uri("/api/people")
                .bodyValue(personToCreate)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals("Location", "/api/people/1")
                .expectBody(Person.class)
                .isEqualTo(createdPerson);
    }

    @Test
    void handleCreatePersonBadRequest() {
        var personToCreate = new Person("New Person");
        Mockito.when(personService.save(personToCreate)).thenReturn(null);
        webTestClient.post()
                .uri("/api/people")
                .bodyValue(personToCreate)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void handleDeletePerson() {
        var personToDelete = new Person(1L, "Alice");
        Mockito.when(personService.findById(1L)).thenReturn(Optional.of(personToDelete));
        webTestClient.delete()
                .uri("/api/people/1")
                .exchange()
                .expectStatus().isNoContent();
        Mockito.verify(personService).delete(personToDelete);
    }

    @Test
    void handleDeletePersonNotFound() {
        Mockito.when(personService.findById(-1L)).thenReturn(Optional.empty());
        webTestClient.delete()
                .uri("/api/people/-1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void handleUpdatePerson() {
        var existingPerson = new Person(1L, "Alice");
        var updatedPerson = new Person(1L, "Updated");
        Mockito.when(personService.findById(1L)).thenReturn(Optional.of(existingPerson));
        webTestClient.put()
                .uri("/api/people/1")
                .bodyValue(new Person("Updated"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Person.class)
                .isEqualTo(updatedPerson);
        Mockito.verify(personService).save(updatedPerson);
    }

    @Test
    void handleUpdatePersonNotFound() {
        Mockito.when(personService.findById(-1L)).thenReturn(Optional.empty());
        webTestClient.put()
                .uri("/api/people/-1")
                .bodyValue(new Person("Updated"))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void handleUpdatePersonBadRequest() {
        var existingPerson = new Person(1L, "Alice");
        Mockito.when(personService.findById(1L)).thenReturn(Optional.of(existingPerson));
        webTestClient.put()
                .uri("/api/people/1")
                .exchange()
                .expectStatus().isBadRequest();
    }
}