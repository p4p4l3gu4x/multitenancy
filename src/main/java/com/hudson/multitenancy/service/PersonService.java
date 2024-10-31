package com.hudson.multitenancy.service;

import com.hudson.multitenancy.model.Person;
import com.hudson.multitenancy.repository.PersonRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    public List<Person> listAll(){
        return personRepository.findAll();
    }
}
