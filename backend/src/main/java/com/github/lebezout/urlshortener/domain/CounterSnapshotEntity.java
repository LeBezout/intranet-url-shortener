package com.github.lebezout.urlshortener.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * The Counter Snapshot Entity.
 * @author lebezout@gmail.com
 */
@Getter
@Setter
@Entity
@IdClass(CounterSnapshotPK.class)
@Table(name = "counter_snapshot")
public class CounterSnapshotEntity {
    @Id
    @Column(name = "counter_id")
    private String counterId;
    @Id
    @Column(name = "snapshot_date")
    private LocalDateTime snapshotDate;
    @Column
    private String claimant;
    @Column(name = "counter_value")
    private long counterValue;
}
