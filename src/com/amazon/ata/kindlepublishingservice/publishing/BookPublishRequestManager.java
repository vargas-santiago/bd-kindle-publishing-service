package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazon.ata.kindlepublishingservice.converters.BookPublishRequestConverter;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.utils.KindlePublishingUtils;
import com.amazon.ata.recommendationsservice.types.BookGenre;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import javax.inject.Inject;
import java.util.*;

public class BookPublishRequestManager {
    private List<BookPublishRequest> requests;

    public BookPublishRequestManager() {

    }

    public void addBookPublishRequest(BookPublishRequest bookPublishRequest) {
        if (requests == null) {
            requests = new ArrayList<>() {
            };
        }

        requests.add(bookPublishRequest);
    }

    public void populateRequest() {
        if (requests == null) {
            requests = new ArrayList<>();
        }

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_WEST_2).build();
        DynamoDBMapper mapper = new DynamoDBMapper(client);

        Map<String, AttributeValue> test = new HashMap<>();

        test.put(":statuss", new AttributeValue().withS("QUEUED"));

        DynamoDBQueryExpression<PublishingStatusItem> query = new DynamoDBQueryExpression<PublishingStatusItem>()
                .withConsistentRead(false)
                .withIndexName("statuss-index")
                .withKeyConditionExpression("statuss = :statuss")
                .withExpressionAttributeValues(test)
                    .withScanIndexForward(false)
                .withLimit(1);

        List<PublishingStatusItem> items = mapper.query(PublishingStatusItem.class, query);

        for (int i = 0; i < items.size(); i ++) {
            if (i == items.size() - 1) {
                BookPublishRequest bookPublishRequest = BookPublishRequest.builder()
                        .withBookId(items.get(i).getBookId())
                        .withAuthor("author")
                        .withGenre(BookGenre.valueOf("TRAVEL"))
                        .withText("text")
                        .withPublishingRecordId(items.get(i).getPublishingRecordId())
                        .withTitle("title")
                        .build();
                this.requests.add(bookPublishRequest);
            }
        }
    }

    public BookPublishRequest getBookPublishRequestsToProcess() {

        if (requests.size() == 0) {
            return null;
        }

        BookPublishRequest request = requests.get(requests.size() -1);


        return request;
    }
}
