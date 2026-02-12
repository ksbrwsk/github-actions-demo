package de.ksbrwsk.people;

import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface PersonRepository extends ListCrudRepository<Person, Long> {
    Optional<Person> findTopByOrderByIdAsc();
}
