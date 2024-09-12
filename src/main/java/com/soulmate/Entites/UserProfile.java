package com.soulmate.Entites;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "user_Profile")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Upload profile pic")
    private String profilePic;

    @NotBlank(message = "Full name is required..")
    private String fullname;

    @Email(message = "Email is required ")
    private String email;

    @NotBlank(message = "Address is required..")
    private String address;

    @NotBlank(message = "city is required")
    private String city;

    //this is required for our String matching process
    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Contact number is required")
    @Size(min = 10,message = "Number should have 10 digits")
    private Long contactno;

    @NotBlank(message = "Education is required")
    private String education;


    //this is important for our string matching process
    @NotBlank(message = "Write about your likes and dislikes")
    @Column(length = 200)
    private String likesAndDislikes;

    //this is also important for our string matching process
    @NotBlank(message = "Caste is required")
    private String caste;
}
