package com.lontsi.rubberduckmetierservicediscussion.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
public abstract class AbstractEntity implements Serializable {

    @MongoId
    private String id;

    @CreatedDate
    private Instant creationDate;

    @LastModifiedDate
    private Instant modificationDate;
}
