package com.shop.admin.category;

import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("/categories")
public class CategoryRestController {

    @Resource
    private CategoryService service;
    
    @PostMapping("/check_unique")
    public String checkUnique(@Param("id") Integer id, @Param("name") String name, @Param("alias") String alias) {
	return service.checkUnique(id, name, alias);
    }
}
