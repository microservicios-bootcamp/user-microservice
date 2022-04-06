package com.demo.app.user.entities;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Document(collection = "user")
@Data
public class User {
    @Id
    private String id;

    @NotEmpty
    private String name;

    @Field(name = "last_name")
    @NotEmpty
    private String lastName;

    @NotEmpty
    @Email
    private String email;

    @CreatedDate
    @Field(name = "create_at")
    private Date createAt;
}
