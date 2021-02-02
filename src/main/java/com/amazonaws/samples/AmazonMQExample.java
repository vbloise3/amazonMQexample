package com.amazonaws.samples;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.jms.pool.PooledConnectionFactory;

import javax.jms.*;

public class AmazonMQExample {

// Specify the connection parameters here. Use your OpenWire endpoint shown in your broker on the console.
// Also open port 61617 in the security group associated with your broker.
// And change the user name and password to the values you used when you created your broker
private final static String WIRE_LEVEL_ENDPOINT 
        = "ssl://b-c45ce31b-dce3-4887-9217-b3cf64428dda-1.mq.us-east-1.amazonaws.com:61617";
private final static String ACTIVE_MQ_USERNAME = "admin";
private final static String ACTIVE_MQ_PASSWORD = "admin1234567";
private final static String MESSAGE_QUEUE = "ArchitectAssociateQueue";

public static void main(String[] args) throws JMSException {
    final ActiveMQConnectionFactory connectionFactory =
            createActiveMQConnectionFactory();
    final PooledConnectionFactory pooledConnectionFactory =
            createPooledConnectionFactory(connectionFactory);

    sendMessage(pooledConnectionFactory);
    receiveMessage(connectionFactory);

    pooledConnectionFactory.stop();
}

private static void
sendMessage(PooledConnectionFactory pooledConnectionFactory) throws JMSException {
    // Establish a connection for the producer.
    final Connection producerConnection = pooledConnectionFactory
            .createConnection();
    producerConnection.start();

    // Create a session.
    final Session producerSession = producerConnection
            .createSession(false, Session.AUTO_ACKNOWLEDGE);

    // Create a queue using the static final MESSAGE_QUEUE.
    final Destination producerDestination = producerSession
            .createQueue(MESSAGE_QUEUE);

    // Create a producer from the session to the queue.
    final MessageProducer producer = producerSession
            .createProducer(producerDestination);
    producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

    // Create a message.
    final String text = "AWS Architect Associate Certification Amazon MQ tutorial intro message.";
    final TextMessage producerMessage = producerSession
            .createTextMessage(text);

    // Send the message.
    producer.send(producerMessage);
    System.out.println("Message sent to MyBroker Amazon MQ message broker on queue: " + MESSAGE_QUEUE);

    // Clean up the producer.
    producer.close();
    producerSession.close();
    producerConnection.close();
}

private static void
receiveMessage(ActiveMQConnectionFactory connectionFactory) throws JMSException {
    // Establish a connection for the consumer.
    // Note: Consumers should not use PooledConnectionFactory.
    final Connection consumerConnection = connectionFactory.createConnection();
    consumerConnection.start();

    // Create a session.
    final Session consumerSession = consumerConnection
            .createSession(false, Session.AUTO_ACKNOWLEDGE);

    // Create a queue using the static final MESSAGE_QUEUE.
    final Destination consumerDestination = consumerSession
            .createQueue(MESSAGE_QUEUE);

    // Create a message consumer from the session to the queue.
    final MessageConsumer consumer = consumerSession
            .createConsumer(consumerDestination);

    // Begin to wait for messages.
    final Message consumerMessage = consumer.receive(1000);

    // Receive the message when it arrives.
    final TextMessage consumerTextMessage = (TextMessage) consumerMessage;
    System.out.println("Message retrieved from " + MESSAGE_QUEUE + ": " + "\"" + consumerTextMessage.getText() + "\"");

    // Clean up the consumer.
    consumer.close();
    consumerSession.close();
    consumerConnection.close();
}

private static PooledConnectionFactory
createPooledConnectionFactory(ActiveMQConnectionFactory connectionFactory) {
    // Create a pooled connection factory.
    final PooledConnectionFactory pooledConnectionFactory =
            new PooledConnectionFactory();
    pooledConnectionFactory.setConnectionFactory(connectionFactory);
    pooledConnectionFactory.setMaxConnections(10);
    return pooledConnectionFactory;
}

private static ActiveMQConnectionFactory createActiveMQConnectionFactory() {
    // Create a connection factory.
    final ActiveMQConnectionFactory connectionFactory =
            new ActiveMQConnectionFactory(WIRE_LEVEL_ENDPOINT);

    // Pass the username and password.
    connectionFactory.setUserName(ACTIVE_MQ_USERNAME);
    connectionFactory.setPassword(ACTIVE_MQ_PASSWORD);
    return connectionFactory;
}
}