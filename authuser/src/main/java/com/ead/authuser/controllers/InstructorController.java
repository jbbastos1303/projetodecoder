package com.ead.authuser.controllers;

import com.ead.authuser.dto.InstructorDto;
import com.ead.authuser.dto.UserCourseDto;
import com.ead.authuser.enums.UserType;
import com.ead.authuser.models.UserCourseModel;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/instructors")
public class InstructorController {
    @Autowired
    UserService userService;

    @PostMapping("/subscription")
    public ResponseEntity<Object> saveSubscriptionInstructor(@RequestBody @Valid InstructorDto instructorDto){

        Optional<UserModel> userModelOptional = userService.findById(instructorDto.getUserId());
        if(!userModelOptional.isPresent()){
            log.warn("Usuário não encontrado userId {} ", instructorDto.getUserId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado!");
        }
        else{
            var userModel = userModelOptional.get();
            userModel.setUserType(UserType.INSTRUCTOR);
            userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
            userService.salvar(userModel);
            return ResponseEntity.status(HttpStatus.OK).body(userModel);
        }
    }

}
