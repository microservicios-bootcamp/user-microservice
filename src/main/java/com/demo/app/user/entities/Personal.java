package com.demo.app.user.entities;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.*;

@JsonPropertyOrder({"id","name","lastName","email","number","dni","createdAt","updateAt"})
@Document(collection = "personal")
@Data
public class Personal extends Audit{
    @Id
    private String id;

    @NotEmpty
    private String name;

    @Field(name = "last_name")
    @NotEmpty
    private String lastName;

    @Email
    private String email;

    @NotEmpty
    @Size(min = 8,max = 8)
    private String dni;

    @NotEmpty
    @Size(min = 9,max = 9)
    private String number;
}
