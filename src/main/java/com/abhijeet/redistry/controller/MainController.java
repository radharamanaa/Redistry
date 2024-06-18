package com.abhijeet.redistry.controller;

import com.abhijeet.redistry.model.User;
import jakarta.persistence.Cacheable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Transient;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class MainController {


    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @PersistenceContext
    private EntityManager entityManager;


    @PostMapping
    @Transactional
    public ResponseEntity<?> makeUser(@RequestBody User user) {
        entityManager.persist(user);
        redisTemplate.opsForValue().set("user::" + user.getId(), user);
        return ResponseEntity.ok().body(user.getId());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable long id) {
        User user = (User) redisTemplate.opsForValue().get("user::" + id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            User user1 = entityManager.find(User.class, id);
            if (user1 == null)
                return ResponseEntity.badRequest().body("Could not find id:" + id);
            else return ResponseEntity.ok(user1);
        }
    }
}
