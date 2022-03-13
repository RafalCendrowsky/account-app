package com.rafalcendrowski.AccountApplication.models;

import com.rafalcendrowski.AccountApplication.controllers.EmployeeController;
import com.rafalcendrowski.AccountApplication.user.User;
import com.rafalcendrowski.AccountApplication.user.UserDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class EmployeeModelAssembler implements RepresentationModelAssembler<User, EntityModel<UserDto>> {

    @Override
    public EntityModel<UserDto> toModel(User user) {
        return EntityModel.of(UserDto.of(user),
                linkTo(methodOn(EmployeeController.class).getEmployee(null)).withSelfRel(),
                linkTo(methodOn(EmployeeController.class).getPayments(null)).withRel("payments")
        );
    }
}
