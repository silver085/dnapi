package com.dn.DNApi.Facades.Jobs.QueueProcessor;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.*;
import org.bson.internal.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class QueueThread extends Thread {
    private String queueId;
    private boolean isOnError = false;
    private String processedFileName;
    private String processedImageData;
    private String toProcessFileName;
    private String errorMessage;
    private String dockerImageName;
    private String dnLib;
    private String dnExec;
    private String dnCmd;
    private String imageData;
    private String imageFile;
    private String getWorkingPath;
    private boolean debug = false;
    private static final Logger logger = LoggerFactory.getLogger(QueueThread.class);

    public QueueThread(String imageData, String imageFile) {
        this.imageData = imageData;
        this.imageFile = imageFile;
    }

    @Override
    public void run() {
        prepareFile();
        if(debug) return;
        try {
            DockerClient docker = DefaultDockerClient.fromEnv().build();
            dnExec = dnExec.replace("$PWD", dnLib);
            String outFilePath = "out_"+processedFileName;
            String inFilePath = "in_"+processedFileName;
            dnCmd = dnCmd.replace("%infile%", inFilePath).replace("%outfile%", outFilePath);
            String[] exec = dnCmd.split(" ");
            HostConfig hostConfig = HostConfig.builder()
                    .privileged(true)
                    .appendBinds(dnLib + ":/app:rw")
                    .build();
            ContainerConfig containerConfig = ContainerConfig.builder()
                    .hostConfig(hostConfig)
                    .image(dockerImageName)
                    .cmd(exec)
                    .build();
            ContainerCreation creation = docker.createContainer(containerConfig);
            String id = creation.id();
            // Inspect container
            ContainerInfo info = docker.inspectContainer(id);

            // Start container
            docker.startContainer(id);
            // Exec command inside running container with attached STDOUT and STDERR
            String[] command = dnCmd.split(" ");
            ExecCreation execCreation = docker.execCreate(
                    id, command, DockerClient.ExecCreateParam.attachStdout(),
                    DockerClient.ExecCreateParam.attachStderr());
            LogStream output = docker.execStart(execCreation.id());
            String execOutput = output.readFully();
            while (execOutput.length() !=0){
                logger.info("Output: {}" , execOutput);
                execOutput = output.readFully();
            }
            // Kill container
            logger.info("Container [{}] started.", id);
            docker.waitContainer(id);
            logger.info("Container [{}] done, removing...", id);
            // Remove container
            Thread.sleep(10 * 1000);
            docker.removeContainer(id);
            // Close the docker client
            docker.close();

            logger.info("Container [{}] done, removed, docker closed, coping {} to destination for serving...", id, outFilePath);
            outFilePath = "output_" + inFilePath;
            Path outP = Paths.get(dnLib + "/" + outFilePath );
            Path inP = Paths.get(dnLib + "/" + inFilePath);
            Path serveP = Paths.get(getWorkingPath + "/" + outFilePath);
            Files.copy(outP, serveP);
            logger.info("Deleting from container app..");
            Files.delete(outP);
            Files.delete(inP);
            logger.info("Done!");
            processedFileName = serveP.toString();
        } catch (DockerCertificateException | InterruptedException | DockerException | IOException e) {
            logger.error("Error: {}", e.getMessage());
            this.errorMessage = e.getMessage();
            this.isOnError = true;
        }
    }


    private void prepareFile(){
        //image/jpeg;base64,
        getWorkingPath =  new File("").getAbsolutePath();
        getWorkingPath = getWorkingPath + "/processed/" + new Date().getTime() + "";
        byte[] fileData = Base64.decode(imageData);
        toProcessFileName = getWorkingPath + "/" + imageFile;
        Path pathToFile = Paths.get(getWorkingPath);
        try {
            Files.createDirectories(pathToFile);
        } catch (IOException e) {
            logger.error("Error creating directories: {}" , e.getMessage());
            this.isOnError = true;
            this.errorMessage = e.getMessage();
        }
        try(OutputStream stream = new FileOutputStream(toProcessFileName)){
            stream.write(fileData);
        } catch (IOException e) {
            this.isOnError = true;
           logger.error("Error preparing file: {}" , e.getMessage());
            this.errorMessage = e.getMessage();
        }
        //convert from jpg to png
        // read a jpeg from a inputFile
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new File(toProcessFileName));
        } catch (IOException e) {
            logger.error("Error reading jpg file: {}" , e.getMessage());
            this.isOnError = true;
            this.errorMessage = e.getMessage();
        }
        processedFileName = new Date().getTime() + ".png";

        try {
            ImageIO.write(bufferedImage, "png", new File(dnLib + "/" + "in_" + processedFileName));
        } catch (IOException e) {
            logger.error("Error writing png: {}" , e.getMessage());
            this.isOnError = true;
            this.errorMessage = e.getMessage();
        }
    }


    public String getQueueId() {
        return queueId;
    }

    public void setQueueId(String queueId) {
        this.queueId = queueId;
    }

    public boolean isOnError() {
        return isOnError;
    }

    public void setOnError(boolean onError) {
        isOnError = onError;
    }

    public String getProcessedFileName() {
        return processedFileName;
    }

    public void setProcessedFileName(String processedFileName) {
        this.processedFileName = processedFileName;
    }

    public String getProcessedImageData() {
        return processedImageData;
    }

    public void setProcessedImageData(String processedImageData) {
        this.processedImageData = processedImageData;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getDnCmd() {
        return dnCmd;
    }

    public void setDnCmd(String dnCmd) {
        this.dnCmd = dnCmd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueueThread that = (QueueThread) o;
        return queueId.equals(that.queueId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(queueId);
    }

    public String getDockerImageName() {
        return dockerImageName;
    }

    public void setDockerImageName(String dockerImageName) {
        this.dockerImageName = dockerImageName;
    }

    public String getDnLib() {
        return dnLib;
    }

    public void setDnLib(String dnLib) {
        this.dnLib = dnLib;
    }

    public String getDnExec() {
        return dnExec;
    }

    public void setDnExec(String dnExec) {
        this.dnExec = dnExec;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public String getImageFile() {
        return imageFile;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }
}
