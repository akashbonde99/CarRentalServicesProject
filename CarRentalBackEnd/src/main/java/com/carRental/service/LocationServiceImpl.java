package com.carRental.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carRental.entity.Car;
import com.carRental.entity.CarLocation;
import com.carRental.repository.CarLocationRepository;
import com.carRental.repository.CarRepository;

@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    private CarLocationRepository locationRepository;

    @Autowired
    private CarRepository carRepository;

    @Override
    public CarLocation addLocation(Long carId, CarLocation location) {
        Car car = carRepository.findById(carId).orElseThrow(() -> new RuntimeException("Car not found"));
        location.setCar(car);
        return locationRepository.save(location);
    }

    @Override
    public CarLocation updateLocation(Long carId, CarLocation location) {
        CarLocation existingLocation = locationRepository.findByCar_CarId(carId).orElse(null);
        if (existingLocation != null) {
            existingLocation.setAddress(location.getAddress());
            existingLocation.setLatitude(location.getLatitude());
            existingLocation.setLongitude(location.getLongitude());
            return locationRepository.save(existingLocation);
        }
        // If not exists, create new
        return addLocation(carId, location);
    }

    @Override
    public CarLocation getLocationByCarId(Long carId) {
        return locationRepository.findByCar_CarId(carId).orElse(null);
    }
}
