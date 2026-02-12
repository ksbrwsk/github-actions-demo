package de.ksbrwsk.people;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WebIntegrationTest implements PostgresContainer {

    @Autowired
    WebTestClient webTestClient;

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

    private Person fetchFirst() {
        return personRepository.findTopByOrderByIdAsc()
                .orElseThrow();
    }

    @Test
    void testHandleNotFound() {
        webTestClient.get()
                .uri("/api/pepl")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testHandleFindAll() {
        webTestClient.get()
                .uri("/api/people")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(100)
                .jsonPath("$[0].name").isEqualTo("Person@1")
                .jsonPath("$[99].name").isEqualTo("Person@100");
    }

    @Test
    void testHandleFindById() {
        var firstPerson = fetchFirst();
        webTestClient.get()
                .uri("/api/people/" + firstPerson.id())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(firstPerson.id())
                .jsonPath("$.name").isEqualTo(firstPerson.name());
    }

    @Test
    void testHandleFindByIdNotFound() {
        webTestClient.get()
                .uri("/api/people/-1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @ParameterizedTest
    @ValueSource(strings = {"N", "Name", "0123456789"})
    void testHandleCreateValidPerson(String name) {
        webTestClient.post()
                .uri("/api/people")
                .bodyValue(new Person(name))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists("Location")
                .expectBody()
                .jsonPath("$.id").isNumber()
                .jsonPath("$.name").isEqualTo(name);
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "01234567890"})
    @NullAndEmptySource
    void testHandleCreateInvalidPerson(String name) {
        webTestClient.post()
                .uri("/api/people")
                .bodyValue(new Person(name))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void handleCreateNull() {
        webTestClient.post()
                .uri("/api/people")

                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testHandleDeletePerson() {
        var firstPerson = fetchFirst();
        webTestClient.delete()
                .uri("/api/people/" + firstPerson.id())
                .exchange()
                .expectStatus().isNoContent();
        webTestClient.get()
                .uri("/api/people/" + firstPerson.id())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testHandleDeletePersonNotFound() {
        webTestClient.delete()
                .uri("/api/people/-1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "01234567890"})
    @NullAndEmptySource
    void testHandleUpdateInvalidPerson(String name) {
        var firstPerson = fetchFirst();
        webTestClient.put()
                .uri("/api/people/" + firstPerson.id())
                .bodyValue(new Person(name))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @ParameterizedTest
    @ValueSource(strings = {"N", "Name", "0123456789"})
    void testHandleUpdateValidPerson(String name) {
        var firstPerson = fetchFirst();
        webTestClient.put()
                .uri("/api/people/" + firstPerson.id())
                .bodyValue(new Person(name))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(firstPerson.id())
                .jsonPath("$.name").isEqualTo(name);
    }

    @Test
    void testHandleUpdatePersonNotFound() {
        webTestClient.put()
                .uri("/api/people/-1")
                .bodyValue(new Person("Updated"))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testHandleUpdatePersonNull() {
        var firstPerson = fetchFirst();
        webTestClient.put()
                .uri("/api/people/" + firstPerson.id())
                .exchange()
                .expectStatus().isBadRequest();
    }
}