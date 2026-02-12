package de.ksbrwsk.people;

import org.springframework.boot.SpringApplication;

public class TestPeopleMvcApplication {

    public static void main(String[] args) {
        SpringApplication.from(PeopleMvcApplication::main).with(TestcontainersConfiguration.class).run(args);
    }
}
