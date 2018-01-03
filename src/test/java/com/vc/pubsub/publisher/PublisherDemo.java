package com.vc.pubsub.publisher;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Vincent on 2018/1/3.
 */
public class PublisherDemo {

    private Logger logger = LoggerFactory.getLogger(PublisherDemo.class);

    @Test
    public void testSingleMessagePublish() throws Exception {

        // From - mvn -Dtest=PublisherDemo#testArgLine -DargLine="-Dproject=AAA -Dtopic=BBB" test
        String topic = System.getProperty("topic");
        String project = System.getProperty("project");

        logger.info("***** TestSingleMessagePublish Starts  ***** ");

        TopicName topicName = TopicName.of(project, topic);
        Publisher publisher = null;
        List<ApiFuture<String>> messageIdFutures = new ArrayList<>();


        try {
            // Create publisher with default setting & bound to topic
            publisher = Publisher.newBuilder(topicName).build();

            for (int i = 0; i < 10; i++) {

                String message = i + "-" + UUID.randomUUID().toString();

                // Publish message to Pubsub
                PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8(message)).build();

                // Once published, return a server-assigned msg id
                ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
                messageIdFutures.add(messageIdFuture);

            }


        } finally {
            // wait on any pending publish requests
            List<String> msgIds = ApiFutures.allAsList(messageIdFutures).get();

            for (String msgId : msgIds) {
                logger.info("Published message with ID: " + msgId);
            }


            if (publisher != null) {
                publisher.shutdown();
            }
        }

    }
}
