package com.github.lebezout.urlshortener.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * The Counter Snapshot POJO.
 * @author lebezout@gmail.com
 */
@Data
public class CounterSnapshotDTO {
    private String counterId;
    private long counterValue;
    private String snapshotClaimant;
    private LocalDateTime snapshotDate;

    public CounterSnapshotDTO(CounterSnapshotEntity entity) {
        counterId = entity.getCounterId();;
        counterValue = entity.getCounterValue();
        snapshotClaimant = entity.getClaimant();
        snapshotDate = entity.getSnapshotDate();
    }
}
