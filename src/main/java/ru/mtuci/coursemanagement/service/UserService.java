package ru.mtuci.coursemanagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.coursemanagement.model.User;
import ru.mtuci.coursemanagement.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repo;

    public Optional<User> findByUsername(String u) {
        return repo.findByUsername(u);
    }

    private final BCryptPasswordEncoder encoder;

    public User save(User u) {
        u.setPassword(encoder.encode(u.getPassword()));
        return repo.save(u);
    }
}