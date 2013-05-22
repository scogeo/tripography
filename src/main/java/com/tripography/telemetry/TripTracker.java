package com.tripography.telemetry;

/**
 * Tracks trips by periodically polling the vehicle to determine its location.  Records the beginning and end of a trip
 *
 * // each trip
 * {
 *     vehicleId: ObjectId(...)
 *     start : {
 *         location: [1.2, 3.4]
 *         locationId: // TBD a unique identiifer for this place
 *         time: Date(..)
 *     },
 *     end : {
 *         location: [lat, lon],
 *         locationId: ObjectId(..)
 *         time: Date(...)
 *     }
 *     distance: 3.4
 *
 * }
 *
 * // Sample queries
 * // Today's drives "vehicleId" = xxx && start.time (gte: some date, gte: end date) || end.time(gte: some date, lt: some date)
 * // All trips to some location :  start.location == objectId or end.locationId = objectId
 * // All trips within an area: start.  near { point: distance. }
 *
 * // Should we have public/private locations
 * // For example superchargers
 * Location id
 * {
 *     _id: ObjectId(...)
 *     center: [lat, lon]
 *     count: n // number of times car has been parked here
 * }
 *
 * // trip pairs
 * {
 *     pairs: [ObjectId(...), ObjectId(...)],
 *     count: n // number of times of trips between these places
 * }
 *
 * @author gscott
 */
public class TripTracker {
}
