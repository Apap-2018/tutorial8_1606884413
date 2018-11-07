package com.apap.tutorial8.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.apap.tutorial8.model.UserRoleModel;
import com.apap.tutorial8.service.UserRoleService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Controller
@RequestMapping("/user")
public class UserRoleController {
	@Autowired
	private UserRoleService userService;

	@RequestMapping(value = "/addUser", method = RequestMethod.POST)
	private String addUserSubmit(@ModelAttribute UserRoleModel user, Model model) {
		userService.addUser(user);
		return "home";
	}

	@RequestMapping(value = "/changePassword", method = RequestMethod.GET)
	private String changePassword(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();
		UserRoleModel user = userService.getUserByUsername(username);
		model.addAttribute("user", user);
		return "change-password";
	}

	@RequestMapping(value = "/changePassword", method = RequestMethod.POST)

	private String changePasswordSubmit(RedirectAttributes redirAttr, Model model,
			@RequestParam(value = "passwordBaru") String passwordBaru,
			@RequestParam(value = "passwordLama") String passwordLama,
			@RequestParam(value = "passwordBaruUlang") String passwordBaruUlang) {
		System.out.println(passwordBaru);
		System.out.println(passwordBaruUlang);
		System.out.println(passwordLama);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();
		UserRoleModel user = userService.getUserByUsername(username);
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		if (passwordEncoder.matches(passwordLama, user.getPassword())) {

		} else {
			redirAttr.addFlashAttribute("pesan", "Password Lama Anda Tidak Sesuai");
			return "redirect:/user/changePassword";
		}
		if (passwordBaru.equals(passwordBaruUlang)) {

		} else {
			redirAttr.addFlashAttribute("pesan", "Password Baru Dengan Pengulangan Tidak Cocok");
			return "redirect:/user/changePassword";
		}
		if (confirmPassword(passwordBaru)) {

		} else {
			redirAttr.addFlashAttribute("pesan", "Password Baru Tidak Memenuhi Syarat");
			return "redirect:/user/changePassword";
		}
		user.setPassword(passwordBaru);
		userService.addUser(user);
		model.addAttribute("pesan", "Password Berhasil Diubah");
		return "home";
	}

	public boolean confirmPassword(String password) {
		boolean passwordLength = false;
		boolean passwordContainsDigit = false;
		boolean passwordContainsLetter = false;

		if (password.length() >= 8)
			passwordLength = true;

		for (char c : password.toCharArray()) {
			if (Character.isDigit(c))
				passwordContainsDigit = true;
		}

		passwordContainsLetter = password.matches(".*[a-zA-Z]+.*");

		return (passwordLength && passwordContainsDigit && passwordContainsLetter);
	}
}
