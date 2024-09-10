package com.test.project.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StoreService {
    @Autowired
    private StoreRepository postRepository;

    public List<Store> findAll() {
        return postRepository.findAll();
    }
}