package spring.http.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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

    @GetMapping("/login")
    public String loginPage() {
        return "user/login";
    }

    @PostMapping("/login")
    public String login (@RequestParam String username,
                         @RequestParam String password) {
        System.out.println("Попытка входа " + username);
        Optional<UserReadDto> maybeUser = userService.authenticate(username, password);
        System.out.println("Result: " + maybeUser.isPresent());

        if (maybeUser.isEmpty()) {
            return "redirect:/login?error";
        }
        UserReadDto user = maybeUser.get();
        if (user.getRole() == Role.ADMIN) {
            return "redirect:/users";
        }
        return "redirect:/users/" + user.getId();
    }

    @GetMapping("/registration")
    public String registration(Model model, @ModelAttribute("user") UserCreateEditDto user) {
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        model.addAttribute("companies", companyService.findAll());
        return "user/registration";
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable("id") Long id, Model model) {
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
            // 🔍 Проверяем, есть ли ошибка именно в поле birthDate
            boolean hasAgeError = bindingResult.getFieldErrors("birthDate").stream()
                    .anyMatch(error -> error.getCode() != null && error.getCode().contains("MinAge"));

            if (hasAgeError) {
                // ❌ Возраст < 18 → перенаправляем на страницу предупреждения
                return "redirect:/users/underage-warning";
            }

            // ❌ Другие ошибки валидации → возвращаем на форму регистрации
            redirectAttributes.addFlashAttribute("user", user);
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/users/registration";
        }

        // ✅ Всё ок → создаём пользователя
        UserReadDto dto = userService.create(user);
        return "redirect:/users/" + dto.getId();
    }

    // 📄 Страница предупреждения (новый метод)
    @GetMapping("/underage-warning")
    public String underageWarning() {
        return "error/underage-warning";
    }

    //    @PutMapping("/{id}")
    @PostMapping("{id}/update")
    public String update(@PathVariable("id") Long id, @ModelAttribute @Validated UserCreateEditDto user) {
//        userService.update(id, user);
        return userService.update(id, user)
                .map(it -> "redirect:/users/{id}")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    //    @DeleteMapping("/{id}")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
        if (!userService.delete(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "redirect:/users";
    }

}
