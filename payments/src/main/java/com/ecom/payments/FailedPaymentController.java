package com.ecom.payments;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class FailedPaymentController {

    private static final Logger log = LoggerFactory.getLogger(FailedPaymentController.class);
    @Autowired
    PaymentRepository repository;

    @Autowired
    RabbitTemplate rabbitTemplate;

    private static final String HEADER_X_RETRIES_COUNT = "x-retries-count";
//    private static final String HEADER_X_DELAY = "x-delay";
//    private static final Integer DELAY = 5000;
    private static final Integer MAX_RETRIES_COUNT = 3;

    @RabbitListener(queues = RabbitMQConfiguration.DLQ_NAME)
    public void processFailedPaymentMessage(Message failedMessage) {
        Integer retriesCnt = (Integer) failedMessage.getMessageProperties()
                .getHeaders().get(HEADER_X_RETRIES_COUNT);
        if (retriesCnt == null) retriesCnt = 1;
        if (retriesCnt > MAX_RETRIES_COUNT) {
            log.info("Max retries count reached for message");
            processFailedPayment(failedMessage);
            return;
        }
        log.info("Retrying message for the {} time", retriesCnt);
        failedMessage.getMessageProperties()
                .getHeaders().put(HEADER_X_RETRIES_COUNT, ++retriesCnt);
//        failedMessage
//                .getMessageProperties()
//                .getHeaders()
//                .put(HEADER_X_DELAY, DELAY);
        rabbitTemplate.send(RabbitMQConfiguration.EXCHANGE_NAME, RabbitMQConfiguration.ROUTING_KEY, failedMessage);
    }

    // after 3 retries, save the failed payment
    private void processFailedPayment(Message message) {
        try {
            Payment failedPayment = new ObjectMapper().readValue(message.getBody(), Payment.class);
            failedPayment.setStatus("FAILED");
            repository.save(failedPayment);
        } catch (IOException e) {
            log.error("Error while converting message to Payment", e);
        } catch (Exception e) {
            log.error("Error while saving failed payment", e);
        }
    }

}
