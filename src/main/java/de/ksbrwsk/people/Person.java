package de.ksbrwsk.people;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;

public record Person(@Id Long id,
                     @NotNull @NotBlank @Size(min = 1, max = 10) String name) {
    public Person(String name) {
        this(null, name);
    }
}
