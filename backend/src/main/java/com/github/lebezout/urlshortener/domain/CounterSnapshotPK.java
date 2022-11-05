package com.github.lebezout.urlshortener.domain;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CounterSnapshotPK implements Serializable {
    private String counterId;
    private LocalDateTime snapshotDate;
}
