package com.aws.s3.demo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region; // ✅ Correct AWS Region class
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketAlreadyExistsException;
import software.amazon.awssdk.services.s3.model.BucketAlreadyOwnedByYouException;
import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

public class S3StorageServiceDemo {

	private static final String BUCKET_NAME = "my-s3-bucket-java-demo-2025";
	private static final String OBJECT_KEY = "sample-upload.txt";
	private static final String UPLOAD_FILE_PATH = "upload.txt";
	private static final String DOWNLOAD_FILE_PATH = "downloaded.txt";

	public static void main(String[] args) throws IOException {

		S3Client s3 = S3Client.builder().region(Region.US_EAST_1) // Change as per your region
				.credentialsProvider(ProfileCredentialsProvider.create()).build();

		try {
			// Create a sample file to upload
			createSampleFile(UPLOAD_FILE_PATH);

			// 1. Create bucket
			createBucket(s3);

			// 2. Upload file
			uploadObject(s3);

			// 3. List objects
			listObjects(s3);

			// 4. Download file
			downloadObject(s3);

			// 5. Delete object
			deleteObject(s3);

			// 6. Delete bucket
			deleteBucket(s3);

		} finally {
			s3.close();
		}
	}

	public static void createBucket(S3Client s3) {
		try {
			CreateBucketRequest request = CreateBucketRequest.builder().bucket(BUCKET_NAME)
					.createBucketConfiguration(
							CreateBucketConfiguration.builder().locationConstraint(Region.US_EAST_1.id()).build())
					.build();

			s3.createBucket(request);
			System.out.println("✅ Bucket created: " + BUCKET_NAME);
		} catch (BucketAlreadyExistsException | BucketAlreadyOwnedByYouException e) {
			System.out.println("⚠️ Bucket already exists or owned.");
		}
	}

	public static void uploadObject(S3Client s3) {
		PutObjectRequest putRequest = PutObjectRequest.builder().bucket(BUCKET_NAME).key(OBJECT_KEY).build();

		s3.putObject(putRequest, RequestBody.fromFile(Paths.get(UPLOAD_FILE_PATH)));
		System.out.println("✅ File uploaded: " + OBJECT_KEY);
	}

	public static void listObjects(S3Client s3) {
		ListObjectsV2Request listReq = ListObjectsV2Request.builder().bucket(BUCKET_NAME).build();

		ListObjectsV2Response listRes = s3.listObjectsV2(listReq);
		List<S3Object> objects = listRes.contents();

		System.out.println("📦 Objects in bucket:");
		for (S3Object obj : objects) {
			System.out.println("  - " + obj.key() + " (" + obj.size() + " bytes)");
		}
	}

	public static void downloadObject(S3Client s3) throws IOException {
		GetObjectRequest getRequest = GetObjectRequest.builder().bucket(BUCKET_NAME).key(OBJECT_KEY).build();

		FileOutputStream fos = new FileOutputStream(DOWNLOAD_FILE_PATH);
		s3.getObject(getRequest, Paths.get(DOWNLOAD_FILE_PATH));
		System.out.println("✅ File downloaded to: " + DOWNLOAD_FILE_PATH);
		fos.close();
	}

	public static void deleteObject(S3Client s3) {
		DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder().bucket(BUCKET_NAME).key(OBJECT_KEY).build();

		s3.deleteObject(deleteRequest);
		System.out.println("🗑️ Object deleted: " + OBJECT_KEY);
	}

	public static void deleteBucket(S3Client s3) {
		DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder().bucket(BUCKET_NAME).build();

		s3.deleteBucket(deleteBucketRequest);
		System.out.println("🗑️ Bucket deleted: " + BUCKET_NAME);
	}

	public static void createSampleFile(String fileName) throws IOException {
		File file = new File(fileName);
		// logic to write something to the file (optional)
		file.createNewFile(); // simplified
	}

}