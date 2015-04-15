package io.strandberg.jta.examples;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CamelRoute extends SpringRouteBuilder {

    private boolean fail = false;
    private FailProcessor failProcessor = new FailProcessor();

    @Override
    public void configure() throws Exception {

        transactionErrorHandler()
                .maximumRedeliveries(0);

        onException(Exception.class)
                .maximumRedeliveries(0)
                .handled(false);


        from("direct:putOnQueue")
                .transacted()
                .split(body().tokenize("\n"))
                .to("jms:queue:person2Db")
                .to("jms:queue:person2Queue")
                .process(failProcessor);


        from("jms:queue:person2Db")
                .transacted()
                .convertBodyTo(Person.class)
                .to("jpa:io.strandberg.jta.examples.Person");
    }


    private class FailProcessor implements Processor {
        public void process(Exchange exchange) throws Exception {
            if (fail) throw new IllegalStateException("BOOM");
        }
    }


    public void setFail(boolean fail) {
        this.fail = fail;
    }

}
