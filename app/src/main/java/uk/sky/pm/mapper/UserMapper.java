package uk.sky.pm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.sky.pm.domain.User;
import uk.sky.pm.dto.UserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "username", source = "email")
    UserDto map(User user);

}
