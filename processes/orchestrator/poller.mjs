import { S3Client } from "@aws-sdk/client-s3";
import {
  ReceiveMessageCommand,
  DeleteMessageCommand,
  SQSClient,
  DeleteMessageBatchCommand,
} from "@aws-sdk/client-sqs";
import { fromIni } from "@aws-sdk/credential-provider-ini";

const credentials = fromIni({ profile: "transcoder" });

const s3 = new S3Client({
  region: "ap-south-1",
  credentials,
});

const sqs = new SQSClient({
  region: "ap-south-1",
  credentials,
});

const queueUrl = "https://sqs.ap-south-1.amazonaws.com/865003063641/VideoProcessingQueue2"

async function pollQueue() {
   const {Messages} = await receiveMessage(queueUrl)
   Messages.forEach(m => {
      console.log(m.Body)
      deleteMessage(queueUrl, m);
   })
}

const deleteMessage = async (queueUrl, message) => {
    await sqs.send(
        new DeleteMessageCommand({
          QueueUrl: queueUrl,
          ReceiptHandle: message.ReceiptHandle,
        }),
      );
}

const receiveMessage = (queueUrl) =>
    sqs.send(
      new ReceiveMessageCommand({
        AttributeNames: ["SentTimestamp"],
        MaxNumberOfMessages: 10,
        MessageAttributeNames: ["All"],
        QueueUrl: queueUrl,
        WaitTimeSeconds: 20,
        VisibilityTimeout: 20,
      }),
    );

setInterval(pollQueue, 5000);

