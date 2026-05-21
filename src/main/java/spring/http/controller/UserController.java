package spring.http.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spring.database.entity.CustomUserDetails;
import spring.database.entity.Role;
import spring.dto.PageResponse;
import spring.dto.UserCreateEditDto;
import spring.dto.UserFilter;
import spring.dto.UserReadDto;
import spring.service.CompanyService;
import spring.service.UserService;

import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final CompanyService companyService;

    @GetMapping
    public String findAll(Model model, UserFilter filter, Pageable pageable) {
        Page<UserReadDto> page = userService.findAll(filter, pageable);
        model.addAttribute("users", PageResponse.of(page));
        model.addAttribute("filter", filter);
        return "user/users";
    }

    @GetMapping("/registration")
    public String registration(Model model, @ModelAttribute("user") UserCreateEditDto user) {
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        model.addAttribute("companies", companyService.findAll());
        return "user/registration";
    }


    @GetMapping("/{id}")
    public String findById(@PathVariable("id") Long id,
                           Model model,
                           @AuthenticationPrincipal CustomUserDetails userDetails) {

        var currentUser = userService.findById(userDetails.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (!currentUser.getId().equals(id)
                && !currentUser.getRole().equals(Role.ADMIN)
                && !currentUser.getRole().equals(Role.OPERATOR)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return userService.findById(id)
                .map(user -> {
                    model.addAttribute("user", user);
                    model.addAttribute("roles", Role.values());
                    model.addAttribute("companies", companyService.findAll());
                    return "user/user";
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public String create(@ModelAttribute @Validated UserCreateEditDto user,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            boolean hasAgeError = bindingResult.getFieldErrors("birthDate").stream()
                    .anyMatch(error -> error.getCode() != null && error.getCode().contains("MinAge"));

            if (hasAgeError) {
                return "redirect:/users/underage-warning";
            }

            redirectAttributes.addFlashAttribute("user", user);
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/users/registration";
        }

        UserReadDto dto = userService.create(user);
        return "redirect:/users/" + dto.getId();
    }

    @GetMapping("/underage-warning")
    public String underageWarning() {
        return "error/underage-warning";
    }

    //    @PutMapping("/{id}")
    @PostMapping("{id}/update")
    public String update(@PathVariable("id") Long id, @ModelAttribute @Validated UserCreateEditDto user,
                         @AuthenticationPrincipal CustomUserDetails userDetails) {
//        userService.update(id, user);
        var currentUser = userService.findById(userDetails.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (!currentUser.getId().equals(id)
                && !currentUser.getRole().equals(Role.ADMIN)
                && !currentUser.getRole().equals(Role.OPERATOR)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return userService.update(id, user)
                .map(it -> "redirect:/users/{id}")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    //    @DeleteMapping("/{id}")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id,
                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        var currentUser = userService.findById(userDetails.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (!currentUser.getId().equals(id)
                && !currentUser.getRole().equals(Role.ADMIN)
                && !currentUser.getRole().equals(Role.OPERATOR)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (!userService.delete(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "redirect:/users";
    }

}
