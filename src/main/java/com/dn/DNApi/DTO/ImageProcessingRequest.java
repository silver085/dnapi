package com.dn.DNApi.DTO;

public class ImageProcessingRequest {
    private String imageData; //B64 image
    private String fileName;

    private Double bsize = null;
    private Double asize = null;
    private Double nsize = null;
    private Double vsize = null;
    private Double hsize = null;

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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
