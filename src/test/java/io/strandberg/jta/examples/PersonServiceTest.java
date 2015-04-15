package io.strandberg.jta.examples;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class PersonServiceTest {

    @Autowired
    private CamelRoute camelRoute;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonService personService;

    @Autowired
    private CamelContext camelContext;


    @Test
    public void testCamelRoute() throws InterruptedException {

        ProducerTemplate template = camelContext.createProducerTemplate();

        DefaultExchange exchange = new DefaultExchange(camelContext);
        exchange.getIn().setBody("Niels Peter\nLucas");
        template.send("direct:putOnQueue", exchange);

        Thread.sleep(2000);

        assertEquals(2, personRepository.count());
        assertEquals("Niels Peter", personRepository.findByName("Niels Peter").getName());
        assertEquals("Lucas", personRepository.findByName("Lucas").getName());

        assertEquals("Niels Peter", personService.receivePersonCreatedMessage());
        assertEquals("Lucas", personService.receivePersonCreatedMessage());
    }

    @Test
    public void testCamelRouteRollback() throws InterruptedException {

        camelRoute.setFail(true);
        ProducerTemplate template = camelContext.createProducerTemplate();

        DefaultExchange exchange = new DefaultExchange(camelContext);
        exchange.getIn().setBody("Niels Peter\nLucas");
        template.send("direct:putOnQueue", exchange);

        Thread.sleep(2000);

        assertEquals(0, personRepository.count());
        assertNull(personService.receivePersonCreatedMessage());
    }
}
