package org.example.springsecurityproject.controllers;

import org.example.springsecurityproject.dto.AuthDTO;
import org.example.springsecurityproject.dto.PersonDTO;
import org.example.springsecurityproject.models.Person;
import org.example.springsecurityproject.security.JWTUtil;
import org.example.springsecurityproject.services.RegisterService;
import org.example.springsecurityproject.util.PersonValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final RegisterService registerService;
    private final PersonValidator personValidator;
    private final JWTUtil jwtUtil;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(RegisterService registerService, PersonValidator personValidator, JWTUtil jwtUtil, ModelMapper modelMapper, AuthenticationManager authenticationManager) {
        this.registerService = registerService;
        this.personValidator = personValidator;
        this.jwtUtil = jwtUtil;
        this.modelMapper = modelMapper;
        this.authenticationManager = authenticationManager;
    }

//    @GetMapping("/login")
//    public String loginPage() {
//        return "auth/login";
//    }

    @GetMapping("/register")
    public String registerPage(@ModelAttribute("person") Person person) {
        return "auth/register";
    }

    @PostMapping("/register")
    public Map<String, String> performRegister(@RequestBody @Valid PersonDTO personDTO, BindingResult bindingResult) {
        Person person = convertToPerson(personDTO);
        personValidator.validate(person, bindingResult);
        if(bindingResult.hasErrors())
            return Map.of("message", "error");
        registerService.register(person);
        String token = jwtUtil.generateToken(person.getUsername());
        return Map.of("jwt-token", token);
    }

    @PostMapping("/login")
    public Map<String, String> performLogin(@RequestBody AuthDTO authenticationDTO) {
        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(authenticationDTO.getUsername(),
                        authenticationDTO.getPassword());

        try {
            authenticationManager.authenticate(authInputToken);
        } catch (BadCredentialsException e) {
            return Map.of("message", "Incorrect credentials!");
        }

        String token = jwtUtil.generateToken(authenticationDTO.getUsername());
        return Map.of("jwt-token", token);
    }

    public Person convertToPerson(PersonDTO personDTO) {
        return this.modelMapper.map(personDTO, Person.class);
    }

}
