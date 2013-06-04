package com.tripography.telemetry.analytics;

/**
 *
 * // yearly aggregates
 * {
 *     _id: "..."
 *     v : 1 // schema version
 *     v : ObjectId(...)
 *     y : "2013"
 *     l : { o: 323.4 t: DateTime(...) }
 *     a : { s: 334, d: 3 },
 *     "01" : {
 *         aggs : { sum : 3},
 *         "01" : { s: 3.4, d: 1 }
 *         "02" : 4,
 *         ...
 *     },
 *     "02" :
 *
 *
 * }
 *
 * We need to track the difference between recording a daily mileage of zero due to an accurate reading
 * versus not reading any data, or an error.  Therefore, we introduce a flag. as follows
 *
 *
 * @author gscott
 */
public interface DailyDistance {

    public static final String COLLECTION_NAME = "dailyDistance";
    public static final int SCHEMA_VERSION = 1;

    public enum READING_FLAG {
        NO_DATA(-1), // initial value
        NO_READING(-2), // Could not read the value for the day
        RESYNCING(-3), // Previous days had no reads, so we are resyncing to get daily mileage.  The mileage will be updated on vehicle only.
        HAS_READING(1);

        private final int value;

        private READING_FLAG(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}
