package com.nishant.stream.services;

import com.nishant.stream.util.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;

@Service
public class QueueService {

    @Value("${aws.queue}")
    private String queueName;

    private SqsClient sqsClient;

    private Logger logger = LoggerFactory.getLogger(Logger.class);

    @Autowired
    public QueueService(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    public String getQueueUrl() {
        logger.info("Executing getQueueUrl");
        GetQueueUrlResponse getQueueUrlResponse =
                sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build());
        logger.info("Queue url {}", Mapper.mapToJson(getQueueUrlResponse));
        return getQueueUrlResponse.queueUrl();
    }


    @Scheduled(fixedRate = 5000, scheduler = "taskScheduler")
    public void pollQueue() {
        try {
            ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                    .queueUrl(getQueueUrl())
                    .maxNumberOfMessages(5)
                    .build();
            List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).messages();
            messages.forEach(System.out::println);
        } catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
