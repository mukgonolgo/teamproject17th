package com.test.project.restaurant;

import java.util.List;

public interface RestaurantService {
    Restaurant saveRestaurant(String name, List<String> tags, List<MenuItem> menuItems);
    List<Restaurant> getAllRestaurants();
    Restaurant getRestaurantById(Long id);
}
