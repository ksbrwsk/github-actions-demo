package de.ksbrwsk.people;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
class PersonService {

    private final PersonRepository personRepository;

    PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Optional<Person> findTopByOrderByIdAsc() {
        return personRepository.findTopByOrderByIdAsc();
    }

    public <S extends Person> S save(S entity) {
        return personRepository.save(entity);
    }

    public long count() {
        return personRepository.count();
    }

    public void deleteAll() {
        personRepository.deleteAll();
    }

    public List<Person> findAll() {
        return personRepository.findAll();
    }

    public Optional<Person> findById(Long aLong) {
        return personRepository.findById(aLong);
    }

    public <S extends Person> List<S> saveAll(Iterable<S> entities) {
        return personRepository.saveAll(entities);
    }

    public void delete(Person entity) {
        personRepository.delete(entity);
    }
}
