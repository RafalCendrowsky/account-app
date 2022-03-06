package com.rafalcendrowski.AccountApplication.models;

import com.rafalcendrowski.AccountApplication.controllers.UserController;
import com.rafalcendrowski.AccountApplication.user.User;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class UserModelAssembler implements RepresentationModelAssembler<User, EntityModel<User>> {

    @Override
    public EntityModel<User> toModel(User user) {
        return EntityModel.of(user,
                linkTo(methodOn(UserController.class).getUser(user.getUsername())).withSelfRel(),
                linkTo(methodOn(UserController.class).getUsers()).withRel("users")
        );
    }
}
