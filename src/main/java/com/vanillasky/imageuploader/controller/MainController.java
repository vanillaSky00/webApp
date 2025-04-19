package com.vanillasky.imageuploader.controller;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/main")
public class MainController {


    public MainController() {

    }

    //get
    @GetMapping("")
    List<?> findAll() {
        return null;
    }

    @GetMapping("")
    List<?> findById() {
        return null;
    }

    //post
    @PostMapping("")
    void create(){

    }

    //put
    @PutMapping("")
    void upadate(){

    }

    //delete
    @DeleteMapping("")
    void delete(){

    }
}
