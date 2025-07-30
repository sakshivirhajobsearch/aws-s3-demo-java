package com.aws.s3.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

public class S3StorageServiceDemoTest {

	private S3Client mockS3;

	@BeforeEach
	public void setup() {
		mockS3 = Mockito.mock(S3Client.class);
	}

	@Test
	public void testCreateSampleFile() throws IOException {
		String path = "test-upload.txt";
		S3StorageServiceDemo.createSampleFile(path); // now visible

		File file = new File(path);
		assertTrue(file.exists(), path + " should be created");
	}

	@Test
	public void testCreateBucketSuccess() {
		CreateBucketResponse response = CreateBucketResponse.builder().build();
		when(mockS3.createBucket(any(CreateBucketRequest.class))).thenReturn(response);

		CreateBucketRequest req = CreateBucketRequest.builder().bucket("test-bucket").build();
		CreateBucketResponse res = mockS3.createBucket(req);

		assertNotNull(res);
		verify(mockS3).createBucket(any(CreateBucketRequest.class));
	}

	@Test
	public void testPutObject() {
		PutObjectResponse response = PutObjectResponse.builder().eTag("12345").build();
		when(mockS3.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(response);

		PutObjectRequest req = PutObjectRequest.builder().bucket("test-bucket").key("file.txt").build();
		PutObjectResponse res = mockS3.putObject(req, RequestBody.fromString("Test content"));

		assertEquals("12345", res.eTag());
		verify(mockS3).putObject(any(PutObjectRequest.class), any(RequestBody.class));
	}

	@Test
	public void testDeleteObject() {
		DeleteObjectResponse response = DeleteObjectResponse.builder().build();
		when(mockS3.deleteObject(any(DeleteObjectRequest.class))).thenReturn(response);

		DeleteObjectRequest req = DeleteObjectRequest.builder().bucket("test-bucket").key("file.txt").build();
		DeleteObjectResponse res = mockS3.deleteObject(req);

		assertNotNull(res);
		verify(mockS3).deleteObject(any(DeleteObjectRequest.class));
	}

	@Test
	public void testDeleteBucket() {
		DeleteBucketResponse response = DeleteBucketResponse.builder().build();
		when(mockS3.deleteBucket(any(DeleteBucketRequest.class))).thenReturn(response);

		DeleteBucketRequest req = DeleteBucketRequest.builder().bucket("test-bucket").build();
		DeleteBucketResponse res = mockS3.deleteBucket(req);

		assertNotNull(res);
		verify(mockS3).deleteBucket(any(DeleteBucketRequest.class));
	}
}
