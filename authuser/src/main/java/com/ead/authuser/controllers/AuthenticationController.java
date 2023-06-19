package com.ead.authuser.controllers;

import com.ead.authuser.dto.UserDTO;
import com.ead.authuser.enums.UserStatus;
import com.ead.authuser.enums.UserType;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/auth")
public class AuthenticationController {

    //Logger logger = LogManager.getLogger(AuthenticationController.class);

    @Autowired
    UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(@RequestBody
                                                   @Validated(UserDTO.UserView.RegistrationPost.class)
                                                   @JsonView(UserDTO.UserView.RegistrationPost.class) UserDTO userDTO){

        log.debug("POST registerUser userDTO received {} ", userDTO.toString());
        if(userService.existsByUsername(userDTO.getUsername())){
            log.warn("Ocorreu um erro pois o usuário {} já existe ", userDTO.getUsername());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ocorreu um erro pois o usuário já existe");
        }
        if(userService.existsByEmail(userDTO.getEmail())){
            log.warn("Ocorreu um erro pois o email {} já existe ", userDTO.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ocorreu um erro pois o email já existe");
        }
        var userModel = new UserModel();
        BeanUtils.copyProperties(userDTO, userModel);
        userModel.setUserStatus(UserStatus.ACTIVE);
        userModel.setUserType(UserType.STUDENT);
        userModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        userService.salvar(userModel);
        log.debug("POST registerUser userModel saved {} ", userModel.getUserId());
        log.info("Usuário {} foi salvo com sucesso", userModel.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(userModel);
    }

}
