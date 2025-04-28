package com.rbm.product;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbm.product.controller.ProductController;
import com.rbm.product.message.ProductMessage;
import com.rbm.product.repository.ProductRepository;
// import com.rbm.product.config.SqsConfig; // Importa la configuración si necesitas referenciarla

import com.rbm.product.service.ProductSqsProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

//@Testcontainers
//@SpringBootTest
//@ActiveProfiles("test") // Activamos el perfil de prueba
public class ProductSqsProducerIntegrationTest {

    private static final String QUEUE_NAME = "product-queue";

    @Container
    static LocalStackContainer localStack = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:latest"))
            .withServices(SQS);

    @MockBean
    private ProductRepository productRepository;

    @Autowired
    private ProductController productController;

    @Autowired
    private ProductSqsProducer productSqsProducer;

    @Autowired
    private AmazonSQSAsync testAmazonSQSAsync;

    private String queueUrl;
    private ObjectMapper objectMapper = new ObjectMapper();

    @TestConfiguration
    @Profile("test") // Esta configuración solo se aplica con el perfil "test"
    static class ProductSqsTestConfig {

        @Bean(name = "testAmazonSQSAsync")
        @Primary // Marca este bean como primario
        public AmazonSQSAsync testAmazonSQSAsync() {
            return AmazonSQSAsyncClientBuilder.standard()
                    .withEndpointConfiguration(
                            new AwsClientBuilder.EndpointConfiguration(
                                    localStack.getEndpointOverride(SQS).toString(),
                                    localStack.getRegion()
                            )
                    )
                    .withCredentials(
                            new AWSStaticCredentialsProvider(
                                    new BasicAWSCredentials("test", "test")
                            )
                    )
                    .build();
        }

        @Bean
        @Primary // Marca este bean como primario
        public QueueMessagingTemplate queueMessagingTemplate(AmazonSQSAsync testAmazonSQSAsync) {
            MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
            messageConverter.setSerializedPayloadClass(String.class);
            QueueMessagingTemplate template = new QueueMessagingTemplate(testAmazonSQSAsync);
            template.setMessageConverter(messageConverter);
            return template;
        }
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("aws.endpoint-url", () -> localStack.getEndpointOverride(SQS).toString());
        registry.add("aws.region", () -> localStack.getRegion());
        registry.add("aws.sqs.product-queue", () -> QUEUE_NAME);
        // Si tu configuración Spring utiliza cloud.aws.stack.auto, debes desactivarla
        registry.add("cloud.aws.stack.auto", () -> false);
    }

    //@BeforeEach
    void setup() {
        // Crear la cola si no existe
        CreateQueueRequest createQueueRequest = new CreateQueueRequest(QUEUE_NAME);
        queueUrl = testAmazonSQSAsync.createQueue(createQueueRequest).getQueueUrl();
    }

    //@Test
    void testSendProductMessage_ShouldSendToSqs() throws Exception {

        ProductMessage productMessage = new ProductMessage();
        productMessage.setProductId(1L);
        productMessage.setName("Test Product");
        productMessage.setStock(29);

        // Act
        productSqsProducer.sendProductMessage(productMessage);

        // Esperar a que el mensaje se procese
        Thread.sleep(2000);

        // Assert - Verificar que el mensaje está en la cola
        ReceiveMessageRequest receiveRequest = new ReceiveMessageRequest()
                .withQueueUrl(queueUrl)
                .withMaxNumberOfMessages(10)
                .withWaitTimeSeconds(5);

        List<Message> messages = testAmazonSQSAsync.receiveMessage(receiveRequest).getMessages();

        // Verificar que recibimos al menos un mensaje
        assertFalse(messages.isEmpty(), "No se encontraron mensajes en la cola SQS");


    }


}