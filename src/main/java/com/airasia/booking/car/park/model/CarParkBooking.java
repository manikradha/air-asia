package com.airasia.booking.car.park.model;

import com.airasia.booking.car.park.utils.Constant;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.validation.constraints.NotBlank;

import java.util.Objects;


@Document(indexName = Constant.CAR_SLOT_INDEX)
public class CarParkBooking {
    @Id
    private String slotNo;
    @NotBlank(message = "carNumber is mandatory")
    @Field(type = FieldType.keyword)
    private  String carNumber;
    @NotBlank(message = "carColor is mandatory")
    @Field(type = FieldType.keyword)
    private  String carColor;
    @NotBlank(message = "carType is mandatory")
    @Field(type = FieldType.keyword)
    private  String carType;
    @Field(type = FieldType.keyword)
    private Long bookingTime;

    public String getSlotNo() {
        return slotNo;
    }

    public void setSlotNo(String slotNo) {
        this.slotNo = slotNo;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getCarColor() {
        return carColor;
    }

    public void setCarColor(String carColor) {
        this.carColor = carColor;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public Long getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(Long bookingTime) {
        this.bookingTime = bookingTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CarParkBooking)) return false;
        CarParkBooking that = (CarParkBooking) o;
        return Objects.equals(slotNo, that.slotNo) &&
                Objects.equals(carNumber, that.carNumber) &&
                Objects.equals(carColor, that.carColor) &&
                Objects.equals(carType, that.carType) &&
                Objects.equals(bookingTime, that.bookingTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(slotNo, carNumber, carColor, carType, bookingTime);
    }
}
