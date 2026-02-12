package de.ksbrwsk.people;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonValidationTest {

    private final static Validator VALIDATOR = Validation.buildDefaultValidatorFactory()
            .getValidator();

    @ParameterizedTest
    @ValueSource(strings = {"N", "Name", "0123456789"})
    void testValidPerson(String name) {
        Person person = new Person(name);
        var violations = VALIDATOR.validate(person);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "01234567890"})
    @NullAndEmptySource
    void testInvalidPerson(String name) {
        Person person = new Person(name);
        var violations = VALIDATOR.validate(person);
        assertFalse(violations.isEmpty());
    }
}