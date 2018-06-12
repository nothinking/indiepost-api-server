package com.indiepost.controller.admin;

import com.indiepost.dto.AdminInitResponseDto;
import com.indiepost.service.AdminService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

/**
 * Created by jake on 10/8/16.
 */
@RestController
@RequestMapping(value = "/admin/init", produces = {"application/json; charset=UTF-8"})
public class AdminInitialDataController {

    private final AdminService adminService;

    @Inject
    public AdminInitialDataController(AdminService adminService) {
        this.adminService = adminService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public AdminInitResponseDto getInitialResponse() {
        return adminService.buildInitialResponse();
    }
}







