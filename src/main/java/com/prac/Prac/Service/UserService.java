package com.prac.Prac.Service;

import com.prac.Prac.Dto.UserDto;
import com.prac.Prac.Entity.UserEntity;
import com.prac.Prac.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    public boolean createUser(UserDto u){
        UserEntity user=new UserEntity();
        user.setUser_id(u.getUser_id());
        user.setUserName(u.getUser_name());
        user.setUser_address(u.getUser_address());
        user.setPw(u.getPw());
        userRepository.save(user);

        return true;
    }
    @Transactional
    public boolean deleteUser(int user_id) {
        if (userRepository.existsById(user_id)) {
            userRepository.deleteById(user_id);
            return true;
        }
        return false;
    }
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user=userRepository.findByUserName(username);
        if(user==null){
            throw  new UsernameNotFoundException("U N F");
        }
        return new User(user.getUserName(),user.getPw(),new ArrayList<>());
    }
}
