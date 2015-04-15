package io.strandberg.jta.examples;

import org.apache.camel.Converter;

@Converter
public class StringToPersonConverter {

    @Converter
    public static Person stringToPerson(String string) {
        Person person = new Person();
        person.setName(string);
        return person;
    }
}
