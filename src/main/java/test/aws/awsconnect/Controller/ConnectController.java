package test.aws.awsconnect.Controller;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.services.securitytoken.model.GetSessionTokenRequest;
import com.amazonaws.services.securitytoken.model.GetSessionTokenResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConnectController {
    public static final String BUCKET = "ocp-sandbox";
    @GetMapping(value="/byKey")
    public String connectByKey() {
        BasicAWSCredentials credentials =
                new BasicAWSCredentials("xxx", "xxx");
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new StaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_1).build();
        return getFileFromS3(s3Client);
    }

    @GetMapping(value="/sts")
    public String connectBySTS() {
        AWSSecurityTokenService sts_client = AWSSecurityTokenServiceClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("sts.amazonaws.com", "us-east-1")).build();
        GetSessionTokenRequest session_token_request = new GetSessionTokenRequest();
        GetSessionTokenResult session_token_result =
                sts_client.getSessionToken(session_token_request);
        Credentials session_creds = session_token_result.getCredentials();
        BasicSessionCredentials credentials = new BasicSessionCredentials(
                session_creds.getAccessKeyId(), session_creds.getSecretAccessKey(), session_creds.getSessionToken());
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(Regions.US_EAST_1).build();
        return getFileFromS3(s3Client);
    }

    private String getFileFromS3(AmazonS3 s3Client) {
        ListObjectsRequest listRequest = new ListObjectsRequest();
        listRequest.setBucketName(BUCKET);

        ObjectListing objectListing = s3Client.listObjects(listRequest);
        StringBuilder filesList = new StringBuilder();
        for (S3ObjectSummary summary:  objectListing.getObjectSummaries()) {
            filesList.append(summary.getKey());
            filesList.append("\n");
        }
        return filesList.toString();
    }
}
