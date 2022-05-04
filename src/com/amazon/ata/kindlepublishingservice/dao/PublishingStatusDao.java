package com.amazon.ata.kindlepublishingservice.dao;

import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.exceptions.PublishingStatusNotFoundException;
import com.amazon.ata.kindlepublishingservice.models.PublishingStatus;
import com.amazon.ata.kindlepublishingservice.utils.KindlePublishingUtils;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import javax.inject.Inject;
import javax.management.Query;

/**
 * Accesses the Publishing Status table.
 */
public class PublishingStatusDao {

    private static final String ADDITIONAL_NOTES_PREFIX = " Additional Notes: ";
    private final DynamoDBMapper dynamoDbMapper;

    /**
     * Instantiates a new PublishingStatusDao object.
     *
     * @param dynamoDbMapper The {@link DynamoDBMapper} used to interact with the publishing status table.
     */
    @Inject
    public PublishingStatusDao(DynamoDBMapper dynamoDbMapper) {
        this.dynamoDbMapper = dynamoDbMapper;
    }

    /**
     * Updates the publishing status table for the given publishingRecordId with the provided
     * publishingRecordStatus. If the bookId is provided it will also be stored in the record.
     *
     * @param publishingRecordId The id of the record to update
     * @param publishingRecordStatus The PublishingRecordStatus to save into the table.
     * @param bookId The id of the book associated with the request, may be null
     * @return The stored PublishingStatusItem.
     */
    public PublishingStatusItem setPublishingStatus(String publishingRecordId,
                                                    PublishingRecordStatus publishingRecordStatus,
                                                    String bookId) {

        return setPublishingStatus(publishingRecordId, publishingRecordStatus, bookId, null);
    }

    /**
     * Updates the publishing status table for the given publishingRecordId with the provided
     * publishingRecordStatus. If the bookId is provided it will also be stored in the record. If
     * a message is provided, it will be appended to the publishing status message in the datastore.
     *
     * @param publishingRecordId The id of the record to update
     * @param publishingRecordStatus The PublishingRecordStatus to save into the table.
     * @param bookId The id of the book associated with the request, may be null
     * @param message additional notes stored with the status
     * @return The stored PublishingStatusItem.
     */
    public PublishingStatusItem setPublishingStatus(String publishingRecordId,
                                                    PublishingRecordStatus publishingRecordStatus,
                                                    String bookId,
                                                    String message) {
        String statusMessage = KindlePublishingUtils.generatePublishingStatusMessage(publishingRecordStatus);
        if (StringUtils.isNotBlank(message)) {
            statusMessage = new StringBuffer()
                .append(statusMessage)
                .append(ADDITIONAL_NOTES_PREFIX)
                .append(message)
                .toString();
        }

        PublishingStatusItem item = new PublishingStatusItem();
        item.setPublishingRecordId(publishingRecordId);
        item.setStatus(publishingRecordStatus);
        item.setStatusMessage(statusMessage);
        item.setBookId(bookId);

        PublishingStatusItem old = new PublishingStatusItem();
        old.setPublishingRecordId(publishingRecordId);

        if (publishingRecordStatus == PublishingRecordStatus.QUEUED) {
            dynamoDbMapper.save(item);
        }

        if (publishingRecordStatus == PublishingRecordStatus.SUCCESSFUL || publishingRecordStatus == PublishingRecordStatus.FAILED) {
            old.setStatus(PublishingRecordStatus.IN_PROGRESS);
            dynamoDbMapper.save(item);
            this.removePublishingStatus(old);
        }

        if (publishingRecordStatus == PublishingRecordStatus.IN_PROGRESS) {
            old.setStatus(PublishingRecordStatus.QUEUED);
            dynamoDbMapper.save(item);
            this.removePublishingStatus(old);
        }




        return item;
    }

    public PublishingStatusItem removePublishingStatus(PublishingStatusItem item) {
        dynamoDbMapper.delete(item);
        return item;
    }






    public List<PublishingStatusItem> getPublishingStatuses(String publishingStatusId) {

        List<PublishingStatusItem> statuses = new ArrayList<>();

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                .withRegion(Regions.US_WEST_2)
                .build();

        DynamoDB dynamoDB = new DynamoDB(client);

        Table table = dynamoDB.getTable("PublishingStatus");


        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("publishingRecordId = :publishingRecordId")
                .withValueMap(new ValueMap()
                        .withString(":publishingRecordId", publishingStatusId));

        ItemCollection<QueryOutcome> items = table.query(spec);

        Iterator<Item> iterator = items.iterator();
        Item item = null;

        if (iterator.hasNext() == false) {
            throw new PublishingStatusNotFoundException("No publishing status found for status id: " + publishingStatusId);
        }

        while (iterator.hasNext()) {
            item = iterator.next();
            PublishingStatusItem pItem = new PublishingStatusItem();
            if (item.get("bookId") != null) {
                pItem.setBookId(item.get("bookId").toString());
            }

            if (item.get("publishingRecordId") != null) {
                pItem.setPublishingRecordId(item.get("publishingRecordId").toString());
            }

            if (item.get("statuss") != null) {
                pItem.setStatus(PublishingRecordStatus.valueOf(item.get("statuss").toString()));
            }

            if (item.get("statusMessage") != null) {
                pItem.setStatusMessage(item.get("statusMessage").toString());
            }
            System.out.println(pItem.getPublishingRecordId());

            statuses.add(pItem);
        }

        return statuses;
    }


}
