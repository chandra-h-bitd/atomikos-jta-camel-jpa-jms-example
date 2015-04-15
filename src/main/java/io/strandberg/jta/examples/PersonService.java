package io.strandberg.jta.examples;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonService {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Transactional
    public String receivePersonCreatedMessage() {
        return (String) jmsTemplate.receiveAndConvert("person2Queue");
    }
}

