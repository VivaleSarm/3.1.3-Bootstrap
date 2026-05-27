package kata.security.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class UserDto {

    private Long id;

    @NotBlank(message = "Firstname is required", groups = {Create.class, Update.class})
    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    private String firstname;

    @NotBlank(message = "Last name is required", groups = {Create.class, Update.class})
    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    private String lastname;

    @NotNull(message = "Age is required", groups = {Create.class, Update.class})
    @Min(value = 1, message = "Age must be positive")
    @Max(value = 150, message = "Age must be realistic")
    private int age;

    @NotBlank(message = "Email is required", groups = {Create.class, Update.class})
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required", groups = {Create.class})
    @Size(min = 3, message = "Password must be at least 3 characters", groups = {Create.class, Update.class})
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private List<Long> roleIds;
    private List<String> roleNames;


    public UserDto() {
    }

    public UserDto(Long id, String firstname, String lastname, int age, String email, String password, List<Long> roleIds, List<String> roleNames) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.age = age;
        this.email = email;
        this.password = password;
        this.roleIds = roleIds;
        this.roleNames = roleNames;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }

    public List<String> getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(List<String> roleNames) {
        this.roleNames = roleNames;
    }



    public interface Create {

    }

    public interface Update {

    }

    @Override
    public String toString() {
        return "UserDto{" +
               "id=" + id +
               ", firstName='" + firstname + '\'' +
               ", lastName='" + lastname + '\'' +
               ", age=" + age +
               ", email='" + email + '\'' +
               ", password='" + password + '\'' +
               ", roleIds=" + roleIds +
               ", roleNames=" + roleNames +
               '}';
    }
}
