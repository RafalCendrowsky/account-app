package com.rafalcendrowski.accountapp.mapper;

import com.rafalcendrowski.accountapp.api.user.request.RegisterUserRequest;
import com.rafalcendrowski.accountapp.api.user.response.UserResponse;
import com.rafalcendrowski.accountapp.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserMapper {
    @Mapping(target = "name", source = "employee.name")
    @Mapping(target = "surname", source = "employee.surname")
    UserResponse toResponse(User user);

    @Mapping(target = "employee.id", source = "employeeId")
    @Mapping(target = "username", source = "email")
    User toModel(RegisterUserRequest registerUserRequest);
}
