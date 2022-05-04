package com.amazon.ata.kindlepublishingservice.dynamodb.models;

import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;

import com.amazonaws.services.dynamodbv2.datamodeling.*;

@DynamoDBTable(tableName = "PublishingStatuss")
public class PublishingStatusItem {
    private String publishingRecordId;
    private PublishingRecordStatus statuss;
    private String statusMessage;
    private String bookId;

    @DynamoDBHashKey(attributeName = "publishingRecordId")
    public String getPublishingRecordId() {
        return publishingRecordId;
    }

    public void setPublishingRecordId(String publishingRecordId) {
        this.publishingRecordId = publishingRecordId;
    }

    @DynamoDBTypeConvertedEnum
    @DynamoDBRangeKey(attributeName = "statuss")
    public PublishingRecordStatus getStatus() {
        return statuss;
    }

    public void setStatus(PublishingRecordStatus statuss) {
        this.statuss = statuss;
    }

    @DynamoDBAttribute(attributeName = "statusMessage")
    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    @DynamoDBAttribute(attributeName = "bookId")
    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}
