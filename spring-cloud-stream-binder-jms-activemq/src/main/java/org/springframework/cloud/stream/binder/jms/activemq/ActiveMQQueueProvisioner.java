package org.springframework.cloud.stream.binder.jms.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.cloud.stream.binder.jms.spi.QueueProvisioner;

import javax.jms.*;

/**
 * @author José Carlos Valero
 * @since 1.1
 */
public class ActiveMQQueueProvisioner implements QueueProvisioner{

    private final ActiveMQConnectionFactory connectionFactory;

    public ActiveMQQueueProvisioner(ActiveMQConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Destinations provisionTopicAndConsumerGroup(String topicName, String... consumerGroupName) {
        Connection activeMQConnection;
        Session session;
        Topic topic = null;
        Destination[] groups = null;
        try {
            activeMQConnection = connectionFactory.createConnection();
            session = activeMQConnection.createSession(true, Session.CLIENT_ACKNOWLEDGE);
            topic = session.createTopic(String.format("VirtualTopic.%s", topicName));
            if (ArrayUtils.isNotEmpty(consumerGroupName)) {
                groups = new Destination[consumerGroupName.length];
                for (int i = 0; i < consumerGroupName.length; i++) {
                    String consumerName = consumerGroupName[i];
                    Queue queue = session.createQueue(String.format("Consumer.%s.VirtualTopic.%s", consumerName, topicName));
                    groups[i] = queue;
                }
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }

        return new Destinations(topic, groups);
    }

    @Override
    public String provisionDeadLetterQueue() {
        return null;
    }

}
