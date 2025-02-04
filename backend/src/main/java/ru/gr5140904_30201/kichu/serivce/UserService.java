package ru.gr5140904_30201.kichu.serivce;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gr5140904_30201.kichu.dao.UserDao;
import ru.gr5140904_30201.kichu.model.User;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDao userDao;

    public boolean existsByEmail(String email) {
        return userDao.existsByEmail(email);
    }

    public void saveUser(User user) {
        userDao.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userDao.getUserByEmail(email);
    }
}
