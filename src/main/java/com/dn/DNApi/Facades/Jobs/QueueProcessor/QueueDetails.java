package com.dn.DNApi.Facades.Jobs.QueueProcessor;

public class QueueDetails {
    int position;
    int timeLeft;
    int totalQueue;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public int getTotalQueue() {
        return totalQueue;
    }

    public void setTotalQueue(int totalQueue) {
        this.totalQueue = totalQueue;
    }
}
