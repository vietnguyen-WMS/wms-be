package com.vietnguyen.ums.controller;

import com.vietnguyen.ums.dto.PagedResponse;
import com.vietnguyen.ums.dto.ViewQueryRequest;
import com.vietnguyen.ums.service.ViewService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/views")
public class ViewController {
    private final ViewService viewService;

    public ViewController(ViewService viewService) {
        this.viewService = viewService;
    }

    @PostMapping
    public PagedResponse<Map<String, Object>> query(@RequestBody ViewQueryRequest req) {
        return viewService.query(req);
    }
}
