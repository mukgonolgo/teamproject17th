//package com.test.project.restaurant;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class RestaurantServiceImpl implements RestaurantService {
//
//    @Autowired
//    private RestaurantRepository restaurantRepository;
//
//    @Override
//    public Restaurant saveRestaurant(String name, List<String> tags, List<MenuItem> menuItems) {
//        Restaurant restaurant = new Restaurant();
//        restaurant.setName(name);
//        restaurant.setTags(tags);
//       restaurant.setMenuItems(menuItems);
//        return restaurantRepository.save(restaurant);
//    }
//
//    @Override
//    public List<Restaurant> getAllRestaurants() {
//        return restaurantRepository.findAll();
//    }
//
//    @Override
//    public Restaurant getRestaurantById(Long id) {
//        return restaurantRepository.findById(id).orElse(null);
//    }
//}
