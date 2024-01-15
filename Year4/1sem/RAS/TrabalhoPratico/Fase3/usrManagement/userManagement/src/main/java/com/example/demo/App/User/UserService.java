package com.example.demo.App.User;

import com.example.demo.App.JsonModels.UserInfoRequest;
import com.example.demo.App.JsonModels.UserInfoResponse;
import com.example.demo.App.JsonModels.UserVerificationResponse;
import com.example.demo.App.Mail.MailService;
import com.example.demo.App.security.SafePasswordGenerator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final SafePasswordGenerator safePasswordGenerator;

    private final MailService mailService;

    @Autowired
    public UserService(UserRepository userRepository, SafePasswordGenerator safePasswordGenerator, MailService mailService){
        this.userRepository = userRepository;
        this.safePasswordGenerator = safePasswordGenerator;
        this.mailService = mailService;
    }

    @Transactional
    public List<String> addUsers(String userType, List<User> users){
        List<String> invalidUsers = new ArrayList<>();
        String password;
        for(User user: users) {
            password = this.safePasswordGenerator.generateStrongPassword();
            System.out.println(password); // TODO: remover isto após testes
            user.setPassword(this.safePasswordGenerator.generateEncodedPassword(password));
            user.setUserType(new UsersType(user, userType));
            if(this.userRepository.findById(user.getNumber()).isEmpty()) {
                this.userRepository.save(user);
                mailService.sendAccountCreationMail(user.getEmail(), user.getNumber(), password);
            } else invalidUsers.add(user.getNumber());
        }

        return invalidUsers;
    }

    public boolean authenticateUser(String password, String number){
        return this.userRepository.findById(number)
                                    .filter(user -> this.safePasswordGenerator.verifyPassword(password, user.getPassword()))
                                    .isPresent();
    }

    @Transactional
    public boolean changeUserInfo(UserInfoRequest user){
        if(this.userRepository.findById(user.getNumber()).isPresent()){
            if(user.getPassword()!=null) {
                if(user.getPassword().length() < 8) return false;
                this.userRepository.updateUserPasswordByNumber(user.getNumber(), user.getPassword());
            }
            if(user.getEmail()!=null)
                this.userRepository.updateUserEmailByNumber(user.getNumber(), user.getEmail());

            return true;
        }

        return false;
    }

    @Transactional
    public UserInfoResponse getUserInfo(String number){
        Optional<User> userOptional = this.userRepository.findById(number);

        if(userOptional.isPresent()){
            User user = userOptional.get();
            return new UserInfoResponse(user.getName(), user.getEmail());
        }

        return null;
    }


    @Transactional
    public List<UserVerificationResponse> verifyUsers(String info, List<String> usersInfo){
        List<UserVerificationResponse> userVerificationResponses = new ArrayList<>();
        for(String userInfo: usersInfo){
            userVerificationResponses.add(new UserVerificationResponse(
                    userInfo,
                    !this.userRepository.findUserByInfo(info, userInfo).isEmpty()));
        }

        return userVerificationResponses;
    }

    @Transactional
    public boolean isEveryUserValid(List<String> usersNumbers){
        for(String userNumber: usersNumbers){
            if(this.userRepository.findById(userNumber).isEmpty())
                return false;
        }

        return true;
    }
}
