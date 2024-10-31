package com.hudson.multitenancy.controller;

import com.hudson.multitenancy.model.Person;
import com.hudson.multitenancy.service.PersonService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping(value = "/person")
    public List<Person> getAllPerson() {
        return personService.listAll();
    }
}
