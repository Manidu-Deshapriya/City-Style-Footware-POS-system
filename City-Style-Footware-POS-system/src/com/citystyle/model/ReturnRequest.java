package com.citystyle.model;

import java.sql.Timestamp;

public class ReturnRequest {
    private int returnId;
    private int saleItemId;
    private int saleId;
    private String modelName;
    private String reason;
    private String status;
    private String approvedBy;
    private Timestamp returnDate;

    public ReturnRequest(int returnId, int saleItemId, String reason, String status, String approvedBy,
            Timestamp returnDate) {
        this.returnId = returnId;
        this.saleItemId = saleItemId;
        this.reason = reason;
        this.status = status;
        this.approvedBy = approvedBy;
        this.returnDate = returnDate;
    }

    public int getReturnId() {
        return returnId;
    }

    public int getSaleItemId() {
        return saleItemId;
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public Timestamp getReturnDate() {
        return returnDate;
    }
}
