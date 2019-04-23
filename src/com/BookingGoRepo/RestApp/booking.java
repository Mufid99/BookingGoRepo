package com.BookingGoRepo.RestApp;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class booking {

    @RequestMapping("/")
    public String index(@RequestParam(value="picklat" ) String pickLat, @RequestParam(value="picklon" ) String pickLon, @RequestParam (value = "droplat") String dropLat, @RequestParam (value = "droplon") String dropLon, @RequestParam (value = "pass") String numPass){
        BookingConsole con = new BookingConsole();
        return con.generateAnswer(pickLat, pickLon, dropLat, dropLon, numPass, false);
    }


}
