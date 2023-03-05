package org.example.springsecurityproject.controllers;

import org.example.springsecurityproject.models.Person;
import org.example.springsecurityproject.services.RegisterService;
import org.example.springsecurityproject.util.PersonValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final RegisterService registerService;
    private final PersonValidator personValidator;

    @Autowired
    public AuthController(RegisterService registerService, PersonValidator personValidator) {
        this.registerService = registerService;
        this.personValidator = personValidator;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(@ModelAttribute("person") Person person) {
        return "auth/register";
    }

    @PostMapping("/register")
    public String performRegister(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult) {
        personValidator.validate(person, bindingResult);
        if(bindingResult.hasErrors())
            return "/auth/register";
        registerService.register(person);
        return "redirect:/auth/login";
    }

}
