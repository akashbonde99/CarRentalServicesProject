package com.carRental.service;

import com.carRental.entity.CarLocation;

public interface LocationService {
    CarLocation addLocation(Long carId, CarLocation location);

    CarLocation updateLocation(Long carId, CarLocation location);

    CarLocation getLocationByCarId(Long carId);
}
