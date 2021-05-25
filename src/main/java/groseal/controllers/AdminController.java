package groseal.controllers;

import groseal.models.Role;
import groseal.models.User;
import groseal.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import groseal.service.UserService;

import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private UserService userService;
    private RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    //Показывает всех юзеров из БД
    @GetMapping
    public String findAll(Model model) {
        model.addAttribute("users", userService.getAllUser());
        return "allUsers";
    }

    //Создает форму для создания юзера
    @GetMapping("/new")
    public String newUser(ModelMap model) {
        model.addAttribute("user", new User());
        return "newUser";
    }

    //Получает данные из формы и создает юзера
    @PostMapping("/addUser")
    public String createUser(@ModelAttribute("user") User user) {
        try {
            Set<Role> roles = new HashSet<>();
            for (Role r : roleService.getAllRoles()) {
                if (user.getUserRoles().toString().contains(r.getRole())) {
                    roles.add(roleService.getRoleByName(r.getRole()));
                }
            }
            user.setUserRoles(roles);
            userService.createUser(user);
            return "redirect:/admin";
        }catch (Exception e){
            return "redirect:/new";
        }
    }

    //Создает форму для редактирования юзера
    @GetMapping("/edit/{id}")
    public String editUser(Model model, @PathVariable(value = "id") Long id) {
        model.addAttribute("user", userService.readUser(id));
        return "/editUser";
    }

    //Получает данные из формы и обновляет данные юзера
    @PatchMapping(path = "/edit/{id}")
    @RequestMapping(value = "/edit/{id}", method = {RequestMethod.PATCH, RequestMethod.POST})
    public String updateUser(@ModelAttribute("user") User user) {
        try {
            Set<Role> roles = new HashSet<>();
            for (Role r : roleService.getAllRoles()) {
                if (user.getUserRoles().toString().contains(r.getRole())) {
                    roles.add(roleService.getRoleByName(r.getRole()));
                }
            }
            user.setUserRoles(roles);
            userService.updateUser(user);
            return "redirect:/admin";
        }catch (Exception e){
            return "redirect:/editUser";
        }

    }

    //Удаление юзера
    @PostMapping({"/delete{id}"})
//    @DeleteMapping({"/delete{id}"})
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }

}
