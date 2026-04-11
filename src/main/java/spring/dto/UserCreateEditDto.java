package spring.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;
import lombok.experimental.FieldNameConstants;
import spring.database.entity.Role;
import spring.validator.MinAge;
import spring.validator.UserInfo;

import java.time.LocalDate;

@UserInfo
@Value
@FieldNameConstants
public class UserCreateEditDto {

    @Email
    String username;
    String password;
    @MinAge
    LocalDate birthDate;

    @Size(min = 3, max = 30)
    String firstname;
    String lastname;
    Role role;
    Integer companyId;
}
