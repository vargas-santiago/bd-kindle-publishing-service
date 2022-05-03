package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.converters.BookPublishRequestConverter;
import com.amazon.ata.kindlepublishingservice.converters.CatalogItemConverter;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.exceptions.PublishingStatusNotFoundException;
import com.amazon.ata.kindlepublishingservice.models.PublishingStatus;
import com.amazon.ata.kindlepublishingservice.models.PublishingStatusRecord;
import com.amazon.ata.kindlepublishingservice.models.requests.GetPublishingStatusRequest;
import com.amazon.ata.kindlepublishingservice.models.response.GetPublishingStatusResponse;
import com.amazonaws.services.lambda.runtime.Context;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class GetPublishingStatusActivity {

    private PublishingStatusDao publishingStatusDao;

    private List<PublishingStatusItem> items;
    private List<PublishingStatusRecord> records;
    @Inject
    public GetPublishingStatusActivity(PublishingStatusDao publishingStatusDao) {
        this.publishingStatusDao = publishingStatusDao;
    }

    public GetPublishingStatusResponse execute(GetPublishingStatusRequest publishingStatusRequest) {
        items = publishingStatusDao.getPublishingStatuses(publishingStatusRequest.getPublishingRecordId());

        if (records == null) {
            records = new ArrayList<>();
        }

        for (PublishingStatusItem item : items) {
            PublishingStatusRecord temp = new PublishingStatusRecord(
                    item.getStatus().toString(),
                    item.getStatusMessage(),
                    item.getBookId());
            records.add(temp);
        }


        return GetPublishingStatusResponse.builder()
                .withPublishingStatusHistory(records)
                .build();
    }
}
