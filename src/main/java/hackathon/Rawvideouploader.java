package hackathon;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileChannel;
import java.security.InvalidKeyException;
import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.storage.blob.*;

/**
 * Azure Functions with HTTP Trigger.
 */

//http://localhost:7071/api/Rawvideouploader?name=vidoename.mp4

public class Rawvideouploader {


    static File createTempFile(String data) throws IOException {

        // Here we are creating a temporary file to use for download and upload to Blob storage
        File sampleFile = File.createTempFile("video", ".mp4");
        System.out.println(">> Creating a NOT sample file at: " + sampleFile.toString());
        Writer output = new BufferedWriter(new FileWriter(sampleFile));
        output.write(data);
        output.close();

        return sampleFile;
    }
    static File createTempBinaryFile(byte[] bytes) throws IOException {

        File file = File.createTempFile("video", ".mp4");

        // Initialize a pointer
        // in file using OutputStream
        OutputStream
                os
                = new FileOutputStream(file);

        // Starts writing the bytes in it
        os.write(bytes);
        System.out.println("Successfully"
                + " byte inserted");

        // Close the file
        os.close();
        return file;
    }
    static void     uploadFile(BlockBlobURL blob, File sourceFile) throws IOException {


        AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(sourceFile.toPath());
        //AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path);

        // Uploading a file to the blobURL using the high-level methods available in TransferManager class
        // Alternatively call the PutBlob/PutBlock low-level methods from BlockBlobURL type 8*1024*1024
        TransferManager.uploadFileToBlockBlob(fileChannel, blob, 8*1024*1024, null)
                .subscribe(response-> {
                    System.out.println("Completed upload request.");
                    System.out.println(response.response().statusCode());
                });
    }
    /**
     * This function listens at endpoint "/api/RawVideoRecieverFunction". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/RawVideoRecieverFunction
     * 2. curl {your host}/api/RawVideoRecieverFunction?name=HTTP%20Query
     */

    /**
     * This function listens at endpoint "/api/Rawvideouploader". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/Rawvideouploader
     * 2. curl {your host}/api/Rawvideouploader?name=HTTP%20Query
     */
//https://stackoverflow.com/questions/54944607/how-to-retrieve-bytes-data-from-request-body-in-azure-function-app
 //https://github.com/Azure/azure-functions-java-worker/issues/239 broken lol
    @FunctionName("Rawvideouploader")
    public HttpResponseMessage run( //buggy ;/
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.FUNCTION,  dataType = "binary")  HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) throws IOException, InvalidKeyException {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        String query = request.getQueryParameters().get("name");
        //Byte[] name = request.getBody().orElse(query);
        String rawVideo =  request.getBody().orElse(null);
       //System.out.println("RAW Video : " + rawVideo);
        //   String blobName  = query.substring(0, Math.min(query.length(), 5)); INCASE NAME TO LONG USE THIS!

        System.out.println("Blob Name is ");
        System.out.println("Query Name is : " + query);



        ContainerURL containerURL;
        // Retrieve the credentials and initialize SharedKeyCredentials
        String accountName = "avisionstorage";// System.getenv("AZURE_STORAGE_ACCOUNT");
        String accountKey =  "dMPOVPZSqlV04jXnr9V50y/LsjxME/TLaNTs/HTsxCesQxmtFCZT2ubsYVSgbNtf/nGIVKedvEBPjZoSTz40ew==" ; //System.getenv("AZURE_STORAGE_ACCESS_KEY");


        // Create a ServiceURL to call the Blob service. We will also use this to construct the ContainerURL
        SharedKeyCredentials creds = new SharedKeyCredentials(accountName, accountKey);
        // We are using a default pipeline here, you can learn more about it at https://github.com/Azure/azure-storage-java/wiki/Azure-Storage-Java-V10-Overview
        final ServiceURL serviceURL = new ServiceURL(new URL("https://" + accountName + ".blob.core.windows.net"), StorageURL.createPipeline(creds, new PipelineOptions()));

        // Let's create a container using a blocking call to Azure Storage
        // If container exists, we'll catch and continue
        containerURL = serviceURL.createContainerURL("videos");
        System.out.println("container created!");
        // Create a BlockBlobURL to run operations on Blobs
        final BlockBlobURL blobURL = containerURL.createBlockBlobURL(query);
        //       final BlockBlobURL blobURL = containerURL.createBlockBlobURL(sasKey);

        uploadFile(blobURL, createTempBinaryFile(rawVideo.getBytes()));


        return request.createResponseBuilder(HttpStatus.OK).body("File Recieved!,  file with name : ->" ).build();
    }
//    curl -d "123.mp4" -X POST http://localhost:7071/api/RawVideoRecieverFunction?name=ThedataName10

//    curl -d "@123.mp4" -X POST https://v-20190301135405899.azurewebsites.net/api/RawVideoRecieverFunction?name=ThedataName10



}
