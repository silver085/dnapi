package com.dn.DNApi.Facades.Jobs.QueueProcessor;

import com.dn.DNApi.Domain.ImageQueue;
import io.netty.handler.codec.base64.Base64Decoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;

public class SrvQueueThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(SrvQueueThread.class);

    private ImageQueue imageQueue;
    private String command;
    private String dnLibBaseFolder;
    private String dnLibStorageFolder;
    private String currentTime;
    private String inFilePath;
    private String bsizeCommand;
    private String asizeCommand;
    private String nsizeCommand;
    private String vsizeCommand;
    private String hsizeCommand;
    private String ownerEmail;



    public SrvQueueThread(ImageQueue imageQueue) {
        this.imageQueue = imageQueue;
        this.currentTime = String.valueOf(new Date().getTime());
        this.dnLibStorageFolder += "/" + currentTime;
    }

    @Override
    public void run() {
        logger.info("Starting task {}" , currentTime);
        logger.info("Token owner: {}" , imageQueue.getToken());
        logger.info("Queue owner: {}" , ownerEmail);
        try {
            prepare();
        } catch (Exception e) {
            logger.error("Error during preparing of the files: {}" , e.getMessage());
            markAsError(e);
        }
        Runtime runtime = Runtime.getRuntime();
        this.command = command.replace("%infile%" , inFilePath).replace("%outfile%" , inFilePath.replace(currentTime, "P"+currentTime));
        this.command = addOptionalParametersToCommand(command);
        logger.info("Task [{}] - CMD: {}", currentTime , command);
        String[] cmd = command.split(" ");
        try {
            Process proc = runtime.exec(cmd, null , new File(dnLibBaseFolder));
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(proc.getErrorStream()));
            StringBuilder output = new StringBuilder();
            String shellOutput = "";
            boolean errors = false;
            while ((shellOutput = stdInput.readLine()) != null) {
                output.append(shellOutput).append("\n");
            }
            while ((shellOutput = stdError.readLine()) != null) {
               output.append(shellOutput).append("\n");
               errors = true;
            }
            logger.info("Output from script: {}" , output);
            if(errors){
               // logger.error("Errors during processing: {}" , output.toString());
            }
            logger.info("Task {} completed!" , currentTime );
            Path originalFile = Paths.get(inFilePath);
            Path processedFile = Paths.get(inFilePath.replace(currentTime, "P"+currentTime));
            Path processedDestinationFile = Paths.get(dnLibStorageFolder + "/P" + currentTime + ".png");
            Files.delete(originalFile);
            logger.info("Original file {} deleted." , originalFile);
            Files.copy(processedFile, processedDestinationFile);
            logger.info("Copied {} to {}" , processedFile,processedDestinationFile);
            Files.delete(processedFile);
            logger.info("Deleted {}" , processedFile);
            markAsDone(processedDestinationFile.toString());

            logger.info("Marking thread completed");
            logger.info("Terminated.");
        } catch (IOException e) {
            logger.error("Error during process execution: {}" , e.getMessage());
            markAsError(e);
        }


    }

    private void markAsDone(String pathToFile){
        this.getImageQueue().setCompleted(true);
        this.getImageQueue().setCompletedOn(new Date());
        this.getImageQueue().setProcessedFilename(pathToFile);
    }

    private void markAsError(Exception e){
        this.imageQueue.setOnError(true);
        this.imageQueue.setProcessedFilename("none");
        this.imageQueue.setErrorMessage(e.getMessage());
    }

    private void prepare() throws IOException {
        String dataToken = "";
        if(imageQueue.getFileData().contains("data:image")) {
            dataToken = imageQueue.getFileData().split(",")[1];
        }  else {
            dataToken = imageQueue.getFileData();
        }
        byte[] imageBytes = Base64.getDecoder().decode(dataToken);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        BufferedImage bufferedImage = ImageIO.read(bis);
        bis.close();
        //create file to API storage
        dnLibStorageFolder += "/" + currentTime;
        Path storagePath = Paths.get(dnLibStorageFolder);
        Files.createDirectories(storagePath);
        String inImageName = currentTime + ".png";
        File storageOutput = new File(storagePath + "/" + inImageName);
        ImageIO.write(bufferedImage, "png" , storageOutput);
        //create File to Lib dir
        Path libStorage = Paths.get(dnLibBaseFolder);
        File libOutput = new File(libStorage + "/" + inImageName);
        ImageIO.write(bufferedImage, "png", libOutput);
        inFilePath = libStorage + "/" +inImageName;
    }

    private String addOptionalParametersToCommand(String baseCommand){
        StringBuilder builder = new StringBuilder();
        String valuePattern = "%VALUE%";
        builder.append(baseCommand);
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(1);

        if(this.getImageQueue().getBsize() != null){
            builder.append(" ");
            String val = nf.format(getImageQueue().getBsize()).replace(",",".");
            builder.append(getBsizeCommand().replace(valuePattern, val));
        }
        if(this.getImageQueue().getAsize() != null){
            builder.append(" ");
            String val = nf.format(getImageQueue().getAsize()).replace(",",".");
            builder.append(getAsizeCommand().replace(valuePattern, val));
        }
        if(this.getImageQueue().getNsize() != null){
            builder.append(" ");
            String val = nf.format(getImageQueue().getNsize()).replace(",",".");
            builder.append(getNsizeCommand().replace(valuePattern, val));
        }
        if(this.getImageQueue().getVsize() != null){
            builder.append(" ");
            String val = nf.format(getImageQueue().getVsize()).replace(",",".");
            builder.append(getVsizeCommand().replace(valuePattern, val));
        }
        if(this.getImageQueue().getHsize() != null){
            builder.append(" ");
            String val = nf.format(getImageQueue().getHsize()).replace(",",".");
            builder.append(getHsizeCommand().replace(valuePattern, val));
        }
        return builder.toString();
    }


    @Override
    public void interrupt() {
        super.interrupt();
        markAsError(new Exception("Interrupted by admin"));
    }

    public ImageQueue getImageQueue() {
        return imageQueue;
    }

    public void setImageQueue(ImageQueue imageQueue) {
        this.imageQueue = imageQueue;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getDnLibBaseFolder() {
        return dnLibBaseFolder;
    }

    public void setDnLibBaseFolder(String dnLibBaseFolder) {
        this.dnLibBaseFolder = dnLibBaseFolder;
    }

    public String getDnLibStorageFolder() {
        return dnLibStorageFolder;
    }

    public void setDnLibStorageFolder(String dnLibStorageFolder) {
        this.dnLibStorageFolder = dnLibStorageFolder;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getBsizeCommand() {
        return bsizeCommand;
    }

    public void setBsizeCommand(String bsizeCommand) {
        this.bsizeCommand = bsizeCommand;
    }

    public String getAsizeCommand() {
        return asizeCommand;
    }

    public void setAsizeCommand(String asizeCommand) {
        this.asizeCommand = asizeCommand;
    }

    public String getNsizeCommand() {
        return nsizeCommand;
    }

    public void setNsizeCommand(String nsizeCommand) {
        this.nsizeCommand = nsizeCommand;
    }

    public String getVsizeCommand() {
        return vsizeCommand;
    }

    public void setVsizeCommand(String vsizeCommand) {
        this.vsizeCommand = vsizeCommand;
    }

    public String getHsizeCommand() {
        return hsizeCommand;
    }

    public void setHsizeCommand(String hsizeCommand) {
        this.hsizeCommand = hsizeCommand;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SrvQueueThread thread = (SrvQueueThread) o;
        return currentTime.equals(thread.currentTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentTime);
    }




}
