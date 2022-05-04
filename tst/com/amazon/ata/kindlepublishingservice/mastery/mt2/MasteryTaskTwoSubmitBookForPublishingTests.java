package com.amazon.ata.kindlepublishingservice.mastery.mt2;

import com.amazon.ata.kindlepublishingservice.converters.BookPublishRequestConverter;
import com.amazon.ata.kindlepublishingservice.helpers.KindlePublishingServiceTctTestDao;
import com.amazon.ata.kindlepublishingservice.models.requests.GetPublishingStatusRequest;
import com.amazon.ata.kindlepublishingservice.models.requests.SubmitBookForPublishingRequest;
import com.amazon.ata.kindlepublishingservice.models.response.GetPublishingStatusResponse;
import com.amazon.ata.kindlepublishingservice.models.response.SubmitBookForPublishingResponse;
import com.amazon.ata.kindlepublishingservice.dagger.ApplicationComponent;
import com.amazon.ata.kindlepublishingservice.dagger.DaggerApplicationComponent;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.helpers.IntegrationTestBase;
import com.amazon.ata.kindlepublishingservice.helpers.KindlePublishingServiceTctTestDao.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.helpers.KindlePublishingServiceTctTestDao.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.helpers.KindlePublishingServiceTctTestDao.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.publishing.BookPublishRequest;
import com.amazon.ata.kindlepublishingservice.publishing.BookPublishRequestManager;
import com.amazon.ata.kindlepublishingservice.publishing.BookPublishTask;
import com.amazon.ata.recommendationsservice.types.BookGenre;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertThrows;

public class MasteryTaskTwoSubmitBookForPublishingTests extends IntegrationTestBase {

    private static final ApplicationComponent COMPONENT = DaggerApplicationComponent.create();

    @Test
    public void submitBookForPublishing_noBookId_submitsBook() {

        // GIVEN
        SubmitBookForPublishingRequest submitBookForPublishingRequest = SubmitBookForPublishingRequest.builder()
            .withAuthor("author")
            .withGenre(String.valueOf(BookGenre.ACTION))
            .withText("text")
            .withTitle("title")
            .build();

        // WHEN
        SubmitBookForPublishingResponse response = COMPONENT.provideSubmitBookForPublishingActivity().execute2(submitBookForPublishingRequest);

        // THEN
        PublishingStatusItem key = new PublishingStatusItem();
        key.setPublishingRecordId(response.getPublishingRecordId());
        key.setStatus(PublishingRecordStatus.QUEUED);

        PublishingStatusItem publishingStatusRecord = getTestDao().load(key);

        assertNotNull(publishingStatusRecord, String.format("Expected a publishing status record with " +
            "[id: %s, status: %s] to be saved in DynamoDB",
            key.getPublishingRecordId(), PublishingRecordStatus.QUEUED));
        assertNotNull(publishingStatusRecord.getStatusMessage(), String.format("Expected the saved " +
            "publishing status record %s to have a non null status message.", publishingStatusRecord));
        assertNull(publishingStatusRecord.getBookId(), String.format("Expected the saved " +
            "publishing status record %s to have a null book ID", publishingStatusRecord));
    }

    @Test
    public void submitBookForPublishing_existingBookId_submitsBook() {
        // GIVEN
        CatalogItemVersion catalogItemVersion = saveNewCatalogItemVersion(false);

        SubmitBookForPublishingRequest submitBookForPublishingRequest = SubmitBookForPublishingRequest.builder()
            .withAuthor(catalogItemVersion.getAuthor())
            .withBookId(catalogItemVersion.getBookId())
            .withGenre(catalogItemVersion.getGenre().name())
            .withText(catalogItemVersion.getText())
            .withTitle(catalogItemVersion.getTitle())
            .build();

        // WHEN
        SubmitBookForPublishingResponse response = COMPONENT.provideSubmitBookForPublishingActivity().execute2(submitBookForPublishingRequest);

        // THEN
        PublishingStatusItem key = new PublishingStatusItem();
        key.setPublishingRecordId(response.getPublishingRecordId());
        key.setStatus(PublishingRecordStatus.QUEUED);

        PublishingStatusItem publishingStatusRecord = getTestDao().load(key);

        assertNotNull(publishingStatusRecord, String.format("Expected a publishing record status item with " +
            "[id: %s, status: %s] to be saved in DynamoDB", key, PublishingRecordStatus.QUEUED));
        assertEquals(catalogItemVersion.getBookId(), publishingStatusRecord.getBookId(), "Expected " +
            "the saved publishing status record to have the same book ID as the request.");
        assertNotNull(publishingStatusRecord.getStatusMessage(), String.format("Expected the saved " +
            "publishing status record %s to have a non null status message.", publishingStatusRecord));    }

    @Test
    public void submitBookForPublishing_existingInactiveBookId_submitsBook() {
        // GIVEN
        CatalogItemVersion catalogItemVersion = saveNewCatalogItemVersion(true);

        SubmitBookForPublishingRequest submitBookForPublishingRequest = SubmitBookForPublishingRequest.builder()
            .withAuthor(catalogItemVersion.getAuthor())
            .withBookId(catalogItemVersion.getBookId())
            .withGenre(catalogItemVersion.getGenre().name())
            .withText(catalogItemVersion.getText())
            .withTitle(catalogItemVersion.getTitle())
            .build();

        // WHEN
        SubmitBookForPublishingResponse response = COMPONENT.provideSubmitBookForPublishingActivity().execute2(submitBookForPublishingRequest);

      // THEN
        PublishingStatusItem key = new PublishingStatusItem();
        key.setPublishingRecordId(response.getPublishingRecordId());
        key.setStatus(PublishingRecordStatus.QUEUED);

        PublishingStatusItem publishingStatusRecord = getTestDao().load(key);

        assertNotNull(publishingStatusRecord, String.format("Expected a publishing record status item with " +
            "[id: %s, status: %s] to be saved in DynamoDB", key, PublishingRecordStatus.QUEUED));
        assertEquals(catalogItemVersion.getBookId(), publishingStatusRecord.getBookId(), "Expected " +
            "the saved publishing status record to have the same book ID as the request.");
        assertNotNull(publishingStatusRecord.getStatusMessage(), String.format("Expected the saved " +
            "publishing status record %s to have a non null status message.", publishingStatusRecord));    }

    @Test
    public void submitBookForPublishing_bookIdThatDoesNotExist_throwsBookNotFoundException() {
        // GIVEN
        SubmitBookForPublishingRequest submitBookForPublishingRequest = SubmitBookForPublishingRequest.builder()
            .withAuthor("author")
            .withBookId(UUID.randomUUID().toString())
            .withGenre(String.valueOf(BookGenre.ACTION))
            .withText("text")
            .withTitle("title")
            .build();

        // WHEN + THEN
        assertThrows(BookNotFoundException.class, () ->
                COMPONENT.provideSubmitBookForPublishingActivity().execute(submitBookForPublishingRequest));
    }

    private void cleanTables(String status) {

    }

    @Test
    public void test() {
        List<String> statuses = new ArrayList<>();
        statuses.add("QUEUED");
        statuses.add("IN_PROGRESS");
        statuses.add("FAILED");
        statuses.add("SUCCESSFUL");

        for (String status : statuses) {
            cleanTables(status);
        }
    }

    private CatalogItemVersion saveNewCatalogItemVersion(boolean inactive) {
        CatalogItemVersion catalogItemVersion = new CatalogItemVersion();
        catalogItemVersion.setBookId("MT02_SubmitBookForPublishing_" + UUID.randomUUID().toString());
        catalogItemVersion.setVersion(1);
        catalogItemVersion.setAuthor("author");
        catalogItemVersion.setGenre(BookGenre.ACTION);
        catalogItemVersion.setText("text");
        catalogItemVersion.setTitle("title");
        catalogItemVersion.setInactive(inactive);

        super.getTestDao().save(catalogItemVersion);

        return catalogItemVersion;
    }



}
