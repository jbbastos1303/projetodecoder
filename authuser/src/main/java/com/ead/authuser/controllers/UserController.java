package com.ead.authuser.controllers;

import com.ead.authuser.dto.UserDTO;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import com.ead.authuser.specifications.SpecificationTemplate;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserModel>> getAllUsers(SpecificationTemplate.UserSpec spec,
                                                       @PageableDefault(page = 0, size = 10, sort = "userId", direction = Sort.Direction.ASC)
                                                       Pageable pageable){
        Page<UserModel> userModelPage = userService.findAll(spec, pageable);
        if(!userModelPage.isEmpty()){
            for(UserModel user : userModelPage.toList()){
                user.add(linkTo(methodOn(UserController.class).getOneUser(user.getUserId())).withSelfRel());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(userModelPage);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getOneUser(@PathVariable(value = "userId")UUID userId){
        Optional<UserModel> userModelOptional = userService.findById(userId);
        if(!userModelOptional.isPresent()){
            log.warn("Usuário não encontrado userId {} ", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(userModelOptional.get());
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable(value = "userId")UUID userId){
        log.debug("DELETE deleteUser userId received {} ", userId);
        Optional<UserModel> userModelOptional = userService.findById(userId);
        if(!userModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }else{
            userService.delete(userModelOptional.get());
            log.debug("DELETE deleteUser userId deletado {} ", userId);
            log.info("Usuário {} foi deletado com sucesso", userId);
            return ResponseEntity.status(HttpStatus.OK).body("Usuário deletado com sucesso!");
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable(value = "userId")UUID userId,
                                             @RequestBody
                                             @Validated(UserDTO.UserView.UserPut.class)
                                             @JsonView(UserDTO.UserView.UserPut.class) UserDTO userDTO){
        log.debug("PUT updateUser userDTO received {} ", userDTO.toString());
        Optional<UserModel> userModelOptional = userService.findById(userId);
        if(!userModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }else{
            var userModel = userModelOptional.get();
            userModel.setFullName(userDTO.getFullName());
            userModel.setPhoneNumber(userDTO.getPhoneNumber());
            userModel.setCpf(userDTO.getCpf());
            userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
            userService.salvar(userModel);
            log.debug("POST updateUser userModel saved {} ", userDTO.toString());
            log.info("Usuário {} foi atualizado com sucesso", userModel.getUserId());
            return ResponseEntity.status(HttpStatus.OK).body(userModel);
        }
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<Object> updatePassword(@PathVariable(value = "userId")UUID userId,
                                                 @RequestBody
                                                 @Validated(UserDTO.UserView.PasswordPut.class)
                                                 @JsonView(UserDTO.UserView.PasswordPut.class) UserDTO userDTO){
        Optional<UserModel> userModelOptional = userService.findById(userId);
        if(!userModelOptional.isPresent()){
            log.warn("Usuário não encontrado userId {} ", userDTO.getUserId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }
        if(!userModelOptional.get().getPassword().equals(userDTO.getOldPassword())){
            log.warn("Senha incorreta userId {} ", userDTO.getUserId());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Senha incorreta");
        }
        else{
            var userModel = userModelOptional.get();
            userModel.setPassword(userDTO.getPassword());
            userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
            userService.salvar(userModel);
            log.debug("PUT updatePassword userId saved {} ", userId);
            log.info("A senha do usuário {} foi atualizada com sucesso", userId);
            return ResponseEntity.status(HttpStatus.OK).body("Senha atualizada com sucesso!");
        }
    }

    @PutMapping("/{userId}/image")
    public ResponseEntity<Object> updateImage(@PathVariable(value = "userId")UUID userId,
                                              @RequestBody
                                              @Validated(UserDTO.UserView.ImagePut.class)
                                              @JsonView(UserDTO.UserView.ImagePut.class) UserDTO userDTO){
        Optional<UserModel> userModelOptional = userService.findById(userId);
        if(!userModelOptional.isPresent()){
            log.warn("Usuário não encontrado userId {} ", userDTO.getUserId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }
        else{
            var userModel = userModelOptional.get();
            userModel.setImageUrl(userDTO.getImageUrl());
            userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
            userService.salvar(userModel);
            log.debug("PUT updateImage userId saved {} ", userId);
            log.info("A imagem do usuário {} foi atualizada com sucesso", userId);
            return ResponseEntity.status(HttpStatus.OK).body(userModel);
        }
    }


}
