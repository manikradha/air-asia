package com.airasia.booking.car.park.controller;

import com.airasia.booking.car.park.exception.CarParkBookingException;
import com.airasia.booking.car.park.model.CarParkBooking;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.Instant;
import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

@RestController
public class CarParkBookingController {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    public Validator validator;

    @Value("${display.total.record}")
    private int totalRecords;

    @Value("${airasia.carslot.total}")
    private int totalSlots;

    @GetMapping("/")
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("carParkBooking");
        mav.addObject("reports", Collections.emptyList());
        mav.addObject("searchReports", Collections.emptyList());
        mav.addObject("countReports", Collections.emptyList());
        mav.addObject("viewName", "Left1");
        return mav;
    }


    @PostMapping(value = "/web/book/carParking")
    public ModelAndView bookCarPark(@RequestParam(value = "carNumber", required = false) String carNumber,
                                    @RequestParam(value = "carColor", required = false) String carColor,
                                    @RequestParam(value = "carType", required = false) String carType) {
        ModelAndView mav = new ModelAndView();
        CarParkBooking carParkBooking = new CarParkBooking();
        carParkBooking.setCarColor(carColor);
        carParkBooking.setCarNumber(carNumber);
        carParkBooking.setCarType(carType);
        carParkBooking.setBookingTime(Instant.now().toEpochMilli());
        mav.addObject("viewName", "Left1");
        mav.addObject("bookings", bookCarPark(carParkBooking));
        mav.setViewName("carParkBooking");
        return mav;
    }

    @PostMapping(value = "/web/search/carParking")
    public ModelAndView searchCarPark(@RequestParam(value = "carNumberSearch", required = false) String carNumber,
                                      @RequestParam(value = "carTypeSearch", required = false) String carType) {
        ModelAndView mav = new ModelAndView();
        List<CarParkBooking> carParkBookings = new ArrayList<>();
        if (StringUtils.hasLength(carNumber)) {
            carParkBookings.addAll(searchCarNumber(carNumber));
        }
        if (StringUtils.hasLength(carType)) {
            carParkBookings.addAll(searchCarType(carType));
        }

        if (!StringUtils.hasLength(carNumber) && !StringUtils.hasLength(carType)) {
            mav.addObject("bookings", searchAllCarParking());
        } else {
            mav.addObject("bookings", carParkBookings);
        }
        mav.addObject("viewName", "Left2");
        mav.setViewName("carParkBooking");
        return mav;
    }

    @PostMapping(value = "/park/car", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CarParkBooking bookCarPark(@RequestBody CarParkBooking carParkBooking) {
        Set<ConstraintViolation<CarParkBooking>> violations = validator.validate(carParkBooking);
        if (violations != null && !violations.isEmpty()) {
            throw new CarParkBookingException(violations);
        }
        CarParkBooking carParkBooking1 = getCarParkingBookedInfo(carParkBooking);
        if (carParkBooking1 == null) {
            throw new CarParkBookingException("Car park fully booked!");
        }
        return carParkBooking1;
    }

    @PostMapping(value = "/web/unpark/car")
    public ModelAndView unparkCar(@RequestParam(value = "slotNo", required = false) String slotNo){
        ModelAndView mav = new ModelAndView();
        mav.addObject("bookings", carUnPark(slotNo));
        mav.addObject("viewName", "Left3");
        mav.setViewName("carParkBooking");
        return mav;
    }
    @DeleteMapping(value = "/unpark/car/{slotNo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CarParkBooking carUnPark(@PathVariable String slotNo) {
        CarParkBooking carParkBooking = getCarParkBooking(slotNo);
        if (carParkBooking != null) {
            elasticsearchOperations.delete(CarParkBooking.class, slotNo);
            elasticsearchOperations.refresh(CarParkBooking.class);
        }
        return carParkBooking;
    }

    public CarParkBooking getCarParkBooking(String slotNo) {
        GetQuery query = new GetQuery();
        query.setId(slotNo);
        return elasticsearchOperations.queryForObject(query, CarParkBooking.class);
    }

    public CarParkBooking getCarParkingBookedInfo(CarParkBooking carParkBooking) {
        for (int slot = 1; slot <= totalSlots; slot++) {
            CarParkBooking carPBook = getCarParkBooking(String.valueOf(slot));
            if (carPBook != null) {
                continue;
            }
            IndexQuery indexQuery = new IndexQuery();
            indexQuery.setId(String.valueOf(slot));
            indexQuery.setObject(carParkBooking);
            elasticsearchOperations.index(indexQuery);
            elasticsearchOperations.refresh(CarParkBooking.class);
            return carParkBooking;
        }
        return null;
    }

    @GetMapping(value = "/web/searchAllCarParking")
    public ModelAndView webSearchAllBookings() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("viewName", "Left1");
        mav.addObject("totalSlots", totalSlots);
        Page<CarParkBooking> carParkBookings = searchAllCarParking();
        mav.addObject("availableSlots", totalSlots - carParkBookings.getTotalElements());
        mav.addObject("bookings", carParkBookings);
        mav.setViewName("carParkBooking");
        return mav;
    }

    @GetMapping(value = "/search/allCarParking", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<CarParkBooking> searchAllCarParking() {
        Pageable pageable = PageRequest.of(0, totalRecords, Sort.by("bookingTime").descending());
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder().withQuery(matchAllQuery());
        builder.withPageable(pageable);
        SearchQuery searchQuery = builder.build();
        return elasticsearchOperations.queryForPage(searchQuery, CarParkBooking.class);
    }

    @GetMapping(value = "/search/car/type/{carType}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CarParkBooking> searchCarType(@PathVariable String carType) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchQuery("carType", carType)).build();
        return elasticsearchOperations.queryForList(searchQuery, CarParkBooking.class);
    }


    @GetMapping(value = "/search/car/number/{carNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CarParkBooking> searchCarNumber(@PathVariable String carNumber) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchQuery("carNumber", carNumber)).build();
        return elasticsearchOperations.queryForList(searchQuery, CarParkBooking.class);
    }

}
