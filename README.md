

## On Demand Video Transcoding And Streaming:

 - The user uploads the video directly to an S3 bucket using a pre-signed URL.
 - S3 triggers an event on SQS
 - The server polls event from SQS and runs a task on ECS to transcode the video . 
 - Transcoded files are uploaded to another bucket.
 - This triggers another event which creates a cloudfront distribution .
 - The metadata is updated on the database.