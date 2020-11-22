package com.airasia.booking.car.park.exception;

import com.airasia.booking.car.park.model.CarParkBooking;

import javax.validation.ConstraintViolation;
import java.util.Set;
import java.util.stream.Collectors;

public class CarParkBookingException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private String msg;

    public CarParkBookingException(String msg) {
        this.msg = msg;
    }

    public CarParkBookingException(Set<ConstraintViolation<CarParkBooking>> violations) {
        this.msg = String.join(", ", violations.stream().map(violation -> violation.getMessage()).collect(Collectors.toList()));
    }

    public String getMsg() {
        return msg;
    }
}
