package com.tripography.telemetry;

import com.rumbleware.mongodb.BaseDocument;
import com.tripography.vehicles.OdometerReading;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.TimeZone;

/**
 * @author gscott
 */
@Document(collection="dailyVehicleReading")
public class DailyVehicleReading extends BaseDocument {

    public static final String NEXT_READING_DATE = "n";
    public static final String TARGET_READING_DATE = "td";
    public static final String FOR_DATE = "d";
    public static final String STATUS = "s";
    public static final String ODOMETER_READING = "o";
    public static final String MESSAGE = "m";
    public static final String ERROR_COUNT = "ec";


    public enum Status {
        OK(0),
        SYNCING(1),
        READ_ERROR(-1),
        AUTH_ERROR(-2),
        INTERNAL_ERROR(-100);

        private final int value;

        Status(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Status fromValue(final int value) {
            for (Status s : Status.values()) {
                if (s.value == value) {
                    return s;
                }
            }
            return null;
        }

    }

    @Field("e")
    private Boolean enabled = true;

    // This is the next reading date including retries.
    @Field(NEXT_READING_DATE)
    private Date nextReadingDate;

    // This is the date that we are attempting to read the data on.
    @Field(TARGET_READING_DATE)
    private Date targetReadingDate;

    // the date we are reading for, which is set to midnight of the day the gets the "credit" for the reading.
    // The actual reading takes place 24 hours +/- 15 mins
    @Field(FOR_DATE)
    private Date forDate;

    @Field(ODOMETER_READING)
    private OdometerReading lastReading;

    @Field(STATUS)
    private int statusValue;

    @Transient
    private Status status;

    @Field(MESSAGE)
    private String message;

    @Transient
    private TimeZone timeZone;

    @Field("t")
    private String timeZoneId;

    @Field(ERROR_COUNT)
    private int errorCount = 0;


    @PersistenceConstructor
    public DailyVehicleReading(ObjectId id) {
        super(id);
    }

    public DailyVehicleReading() {
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Status getStatus() {
        if (status != null) {
            return status;
        }
        status = Status.fromValue(statusValue);
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        if (status == Status.OK) {
            errorCount = 0;
        }
        this.statusValue = status.getValue();
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setError(String message) {
        errorCount++;
        this.message = message;
        // disable if this error has occurred more than 7 times.
        if (errorCount > 7) {
            enabled = false;
        }
    }

    public Date getForDate() {
        return forDate;
    }

    public void setForDate(Date forDate) {
        this.forDate = forDate;
    }

    public Date getNextReadingDate() {
        return nextReadingDate;
    }

    public void setNextReadingDate(Date nextReadingDate) {
        this.nextReadingDate = nextReadingDate;
    }

    public Date getTargetReadingDate() {
        return targetReadingDate;
    }

    public void setTargetReadingDate(Date targetReadingDate) {
        this.targetReadingDate = targetReadingDate;
    }

    public void updateTargetAndNextReadingDate(Date date) {
        this.targetReadingDate = date;
        this.nextReadingDate = date;
    }

    public OdometerReading getLastReading() {
        return lastReading;
    }

    public void setLastReading(OdometerReading lastReading) {
        this.lastReading = lastReading;
    }

    public TimeZone getTimeZone() {
        if (timeZone != null) {
            return timeZone;
        }
        if (timeZoneId != null) {
            timeZone = TimeZone.getTimeZone(timeZoneId);
            return timeZone;
        }
        return null;
    }

    public void setTimeZone(TimeZone tz) {
        this.timeZone = tz;
        this.timeZoneId = tz.getID();
    }

    @Override
    public String toString() {
        return "DailyVehicleReading{" +
                "enabled=" + enabled +
                ", nextReadingDate=" + nextReadingDate +
                ", forDate=" + forDate +
                ", lastReading=" + lastReading +
                ", statusValue=" + statusValue +
                ", status=" + status +
                ", message='" + message + '\'' +
                ", timeZone=" + timeZone +
                ", timeZoneId='" + timeZoneId + '\'' +
                ", errorCount=" + errorCount +
                '}';
    }
}
