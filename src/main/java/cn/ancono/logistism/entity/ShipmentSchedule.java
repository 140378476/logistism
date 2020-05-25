package cn.ancono.logistism.entity;
/*
 * Created by liyicheng at 2020-05-12 14:15
 */

import javax.persistence.*;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * @author liyicheng
 */
@Entity
@Table(name = "shipment_schedule")
public class ShipmentSchedule {
    @Id
    @Column(name = "shipment_id")
    private Long shipmentId;

    private String description;

    private String transportation;

    @Column(name = "source_id")
    private Long sourceId;
    @Column(name = "destination_id")
    private Long destinationId;

    //    @Temporal(TemporalType.TIME)
    @Column(name = "time_cost")
    private Long timeCost;

    @Column(name = "repetition_type")
    private String repetitionType;

    //    @Temporal(TemporalType.TIME)
    @Column(name = "repetition_time")
    private LocalDateTime repetitionTime;

    public Long getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(Long shipmentId) {
        this.shipmentId = shipmentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTransportation() {
        return transportation;
    }

    public void setTransportation(String transportation) {
        this.transportation = transportation;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Long getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(Long destinationId) {
        this.destinationId = destinationId;
    }


    public String getRepetitionType() {
        return repetitionType;
    }

    public void setRepetitionType(String repetitionType) {
        this.repetitionType = repetitionType;
    }


    public Long getTimeCost() {
        return timeCost;
    }

    public void setTimeCost(Long timeCost) {
        this.timeCost = timeCost;
    }

    public Duration getTimeCostDuration(){
        return Duration.ofSeconds(timeCost);
    }

    public LocalDateTime getRepetitionTime() {
        return repetitionTime;
    }

    public void setRepetitionTime(LocalDateTime repetitionTime) {
        this.repetitionTime = repetitionTime;
    }
}
