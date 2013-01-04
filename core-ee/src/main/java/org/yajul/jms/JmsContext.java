package org.yajul.jms;

import javax.jms.*;

/**
 * Callback methods for the JMS IoC template.
* <br>
* User: josh
* Date: Mar 3, 2009
* Time: 3:31:40 PM
*/
public interface JmsContext {
    TopicConnection createTopicConnection() throws JMSException;

    TopicSession createTopicSession(TopicConnection con) throws JMSException;

    TopicSubscriber createSubscriber(TopicSession session, Topic topic, String selector) throws JMSException;

    TopicPublisher createPublisher(TopicSession session, Topic topic) throws JMSException;
}
