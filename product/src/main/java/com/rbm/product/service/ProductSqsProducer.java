package com.rbm.product.service;


import com.rbm.product.message.ProductMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ProductSqsProducer {

    private static final Logger logger = LoggerFactory.getLogger(ProductSqsProducer.class);

    private final QueueMessagingTemplate queueMessagingTemplate;

    @Value("${aws.sqs.product-queue}")
    private String productQueueName;

    public ProductSqsProducer(QueueMessagingTemplate queueMessagingTemplate) {
        this.queueMessagingTemplate = queueMessagingTemplate;
    }

    public void sendProductMessage(ProductMessage productMessage) {
        logger.info("Sending message to SQS queue: {}", productMessage);
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonPayload = objectMapper.writeValueAsString(productMessage);
    
            queueMessagingTemplate.send(productQueueName, 
            MessageBuilder.withPayload(jsonPayload).build()); 
            
    
            queueMessagingTemplate.send(productQueueName,
                    MessageBuilder.withPayload(productMessage).build());
            logger.info("Message sent successfully");
            
        } catch (Exception e) {
            logger.error("Error sending message to SQS queue: {}", e.getMessage(), e);      
        }
       
    }
}

/*
 * 
 * 

 */