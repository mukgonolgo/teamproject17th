package com.test.project.restaurant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping("/")
    public String getAllRestaurants(Model model) {
        List<Restaurant> restaurants = restaurantService.getAllRestaurants();
        model.addAttribute("restaurants", restaurants);
        return "index"; // 식당 목록 페이지
    }

    @GetMapping("/restaurant/{id}")
    public String getRestaurant(@PathVariable Long id, Model model) {
        Restaurant restaurant = restaurantService.getRestaurantById(id);
        model.addAttribute("restaurant", restaurant);
        return "restaurant"; // 식당 상세 페이지
    }

    @PostMapping("/addRestaurant")
    public String addRestaurant(@RequestParam("name") String name,
                                 @RequestParam("tags") String tags,
                                 @RequestParam("menuItems") String[] menuItems,
                                 @RequestParam("prices") double[] prices,
                                 Model model) {
        
        List<String> tagList = Arrays.asList(tags.split(","));
        
        List<MenuItem> menuList = new ArrayList<>();
        for (int i = 0; i < menuItems.length; i++) {
            MenuItem menuItem = new MenuItem();
            menuItem.setName(menuItems[i]);
            menuItem.setPrice(prices[i]);
            menuList.add(menuItem);
        }
        
        Restaurant restaurant = restaurantService.saveRestaurant(name, tagList, menuList);
        model.addAttribute("restaurant", restaurant);
        return "result"; // 결과를 보여줄 뷰 이름
    }
}


