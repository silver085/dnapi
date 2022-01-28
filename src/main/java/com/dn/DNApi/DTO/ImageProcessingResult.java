package com.dn.DNApi.DTO;

public class ImageProcessingResult extends BaseResponse {
    private String id;
    private String fileName;
    private String status;
    private String fileData;
    private String processedFileName;
    private int queuePosition;
    private int waitingTime;
    private int totalQueue;
    private int waitingMsg;

    public String getFileData() {
        return fileData;
    }

    public void setFileData(String fileData) {
        this.fileData = fileData;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProcessedFileName() {
        return processedFileName;
    }

    public void setProcessedFileName(String processedFileName) {
        this.processedFileName = processedFileName;
    }

    public int getQueuePosition() {
        return queuePosition;
    }

    public void setQueuePosition(int queuePosition) {
        this.queuePosition = queuePosition;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public int getTotalQueue() {
        return totalQueue;
    }

    public void setTotalQueue(int totalQueue) {
        this.totalQueue = totalQueue;
    }

    public int getWaitingMsg() {
        return waitingMsg;
    }

    public void setWaitingMsg(int waitingMsg) {
        this.waitingMsg = waitingMsg;
    }
}
