package com.demo.app.user.entities;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document(collection = "user")
@Data
public class User {
    @Id
    private String id;

    private String name;

    @Field(name = "last_name")
    private String lastName;

    private String email;

    @CreatedDate
    @Field(name = "create_at")
    private Date createAt;

    @LastModifiedDate
    @Field(name = "update_at")
    private Date updateAt;
}
