package mainpackage.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

public class PersonDTO {
    @Getter
    @Setter
    @NotEmpty(message = "Name should noy be empty")
    @Size(min = 2, max = 30, message = "name should be between 2 and 30 characters")
    private String name;

    @Getter
    @Setter
    @Min(value = 0, message = "age should be greater than 0")
    private int age;

    @Getter
    @Setter
    @Email
    @NotEmpty(message = "email should not be empty")
    private String email;
}
