package com.nishant.stream.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nishant.stream.models.Owner;
import com.nishant.stream.models.Video;
import com.nishant.stream.repositories.OwnerRepository;
import com.nishant.stream.repositories.VideoRepository;
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
import java.util.Optional;

@Service
public class QueueService {

    @Value("${aws.queue1}")
    private String queue1;

    @Value("${aws.queue2}")
    private String queue2;
    private SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private Logger logger = LoggerFactory.getLogger(Logger.class);
    private OwnerRepository ownerRepository;
    private VideoRepository videoRepository;

    @Autowired
    public QueueService(SqsClient sqsClient, OwnerRepository ownerRepository, VideoRepository videoRepository) {
        this.sqsClient = sqsClient;
        this.objectMapper = new ObjectMapper();
        this.ownerRepository = ownerRepository;
        this.videoRepository = videoRepository;
    }

    public String getQueueUrl(String name) {
        logger.info("Executing getQueueUrl");
        GetQueueUrlResponse getQueueUrlResponse =
                sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(name).build());
        logger.info("Queue url {}", Mapper.mapToJson(getQueueUrlResponse));
        return getQueueUrlResponse.queueUrl();
    }


    @Scheduled(fixedRate = 5000, scheduler = "taskScheduler")
    public void pollQueue() {
        try {
            ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                    .queueUrl(getQueueUrl(queue1))
                    .maxNumberOfMessages(5)
                    .build();
            List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).messages();
            processMessage(messages);
        } catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public void processMessage(List<Message> messages){
        messages.forEach(m ->{
            String body = m.body();
            try {
                JsonNode root = objectMapper.readTree(body);
                JsonNode recordsNode = root.path("Records");
                for (JsonNode recordNode : recordsNode) {
                    JsonNode s3Node = recordNode.path("s3");
                    String bucketName = s3Node.path("bucket").path("name").asText();
                    String objectKey = s3Node.path("object").path("key").asText();
                    Optional<Owner> owner = ownerRepository.findById(1L);
                    Video video = new Video(owner.get(), objectKey, bucketName);
                    videoRepository.save(video);
                }
                DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                        .queueUrl(getQueueUrl(queue1))
                        .receiptHandle(m.receiptHandle())
                        .build();
                sqsClient.deleteMessage(deleteMessageRequest);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
