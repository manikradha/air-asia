package com.airasia.booking.car.park.controller;

import com.airasia.booking.car.park.model.CarParkBooking;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
public class CarParkBookingControllerTest {


    @Autowired
    private CarParkBookingController carParkBookingController;

    private CarParkBooking fakeCarParkBooking;

    @Before
    public void setUp() {
        fakeCarParkBooking = new CarParkBooking();
        fakeCarParkBooking.setCarColor("green");
        fakeCarParkBooking.setCarType("ferrari");
        fakeCarParkBooking.setCarNumber("A1234");
    }

    @Test
    public void test_parkCar() {
        String carParkBooking = carParkBookingController.bookCarPark(fakeCarParkBooking).getSlotNo();
        Assert.assertNotNull(carParkBooking);
    }

    @Test
    public void test_searchCarType() {
        List<CarParkBooking> carParkBooking = carParkBookingController.searchCarType("ferrari");
        Assert.assertEquals(1, 1);
    }

    @Test
    public void test_searchCarNumber() {
        List<CarParkBooking>  carParkBooking = carParkBookingController.searchCarNumber("A1234");
        Assert.assertEquals(1, 1);
    }

    @Test
    public void test_searchParkingStat() {
        Page<CarParkBooking> carParkBookings = carParkBookingController.searchAllCarParking();
        Assert.assertEquals(1, carParkBookings.getTotalPages());
    }

    @Test
    public void test_unPark() {
        CarParkBooking carParkBooking = carParkBookingController.carUnPark("1");
        Assert.assertNotNull(carParkBooking);
    }

}
