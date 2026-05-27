package kata.security.mapper;

import kata.security.dto.UserDto;
import kata.security.model.Role;
import kata.security.model.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "firstname", target = "firstname")
    @Mapping(source = "lastname", target = "lastname")
    @Mapping(source = "roles", target = "roleNames", qualifiedByName = "rolesToRoleNames")
    @Mapping(source = "roles", target = "roleIds", qualifiedByName = "rolesToRoleIds")
    @Mapping(target = "password", ignore = true)
    UserDto toDto(User user);

    List<UserDto> toDtoList(List<User> users);

    @Mapping(source = "firstname", target = "firstname")
    @Mapping(source = "lastname", target = "lastname")
    @Mapping(source = "roleIds", target = "roles", qualifiedByName = "idsToRoles")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toEntity (UserDto userDto);

    @Mapping(source = "firstname", target = "firstname")
    @Mapping(source = "lastname", target = "lastname")
    @Mapping(source = "roleIds", target = "roles", qualifiedByName = "idsToRoles")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity (UserDto userDto, @MappingTarget User user);

    @Named("rolesToRoleNames")
    default List<String> rolesToRoleNames (Set<Role> roles) {
        if (roles == null){
            return null;
        }
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }

    @Named("rolesToRoleIds")
    default List<Long> rolesToRoleIds(Set<Role> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .map(Role::getId)
                .collect(Collectors.toList());
    }

    @Named("idsToRoles")
    default Set<Role> idsToRoles(List<Long> roleIds){
        return new HashSet<>();
    }

}
