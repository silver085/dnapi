package com.dn.DNApi.Domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("ImageQueue")
public class ImageQueue {
    @Id
    private String id;
    private String fileName;
    private String fileData;
    private Date submitOn = new Date();
    private Date completedOn;
    private boolean completed = false;
    private boolean onError = false;
    private String token;
    private String processedData;
    private String processedFilename;
    private Date startedOn;
    private Date createOn = new Date();
    private String errorMessage;
    private boolean isRunning = false;
    private Double bsize = null;
    private Double asize = null;
    private Double nsize = null;
    private Double vsize = null;
    private Double hsize = null;


    public Date getCreateOn() {
        return createOn;
    }

    public void setCreateOn(Date createOn) {
        this.createOn = createOn;
    }

    public Date getStartedOn() {
        return startedOn;
    }

    public void setStartedOn(Date startedOn) {
        this.startedOn = startedOn;
    }

    public String getProcessedData() {
        return processedData;
    }

    public void setProcessedData(String processedData) {
        this.processedData = processedData;
    }

    public boolean isOnError() {
        return onError;
    }

    public void setOnError(boolean onError) {
        this.onError = onError;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileData() {
        return fileData;
    }

    public void setFileData(String fileData) {
        this.fileData = fileData;
    }

    public Date getSubmitOn() {
        return submitOn;
    }

    public void setSubmitOn(Date submitOn) {
        this.submitOn = submitOn;
    }

    public Date getCompletedOn() {
        return completedOn;
    }

    public void setCompletedOn(Date completedOn) {
        this.completedOn = completedOn;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getProcessedFilename() {
        return processedFilename;
    }

    public void setProcessedFilename(String processedFilename) {
        this.processedFilename = processedFilename;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public Double getBsize() {
        return bsize;
    }

    public void setBsize(Double bsize) {
        this.bsize = bsize;
    }

    public Double getAsize() {
        return asize;
    }

    public void setAsize(Double asize) {
        this.asize = asize;
    }

    public Double getNsize() {
        return nsize;
    }

    public void setNsize(Double nsize) {
        this.nsize = nsize;
    }

    public Double getVsize() {
        return vsize;
    }

    public void setVsize(Double vsize) {
        this.vsize = vsize;
    }

    public Double getHsize() {
        return hsize;
    }

    public void setHsize(Double hsize) {
        this.hsize = hsize;
    }
}
