package spring.http.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import spring.service.UserGalleryService;
import spring.service.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/gallery")
public class UserGalleryController {

    private final UserService userService;
    private final UserGalleryService userGalleryService;

    @GetMapping
    public String gallery(@PathVariable Long userId, Model model) {
        model.addAttribute("user", userService.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
        model.addAttribute("images", userGalleryService.findAll(userId));
        return "user/gallery";
    }

    @PostMapping("/upload")
    public String upload(@PathVariable Long userId,
                         @RequestParam("image") MultipartFile image) {
        userGalleryService.upload(userId, image);
        return "redirect:/users/" + userId + "/gallery";
    }

    @PostMapping("/{imageId}/delete")
    public String delete(@PathVariable Long userId,
                         @PathVariable Long imageId) {
        userGalleryService.delete(userId, imageId);
        return "redirect:/users/" + userId + "/gallery";
    }

    @PostMapping("/{imageId}/replace")
    public String replace(@PathVariable Long userId,
                          @PathVariable Long imageId,
                          @RequestParam("image") MultipartFile image) {
        userGalleryService.replace(userId, imageId, image);
        return "redirect:/users/" + userId + "/gallery";
    }
}