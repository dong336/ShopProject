package com.shop.admin.user.controller;

import java.io.IOException;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shop.admin.FileUploadUtil;
import com.shop.admin.security.ShopUserDetails;
import com.shop.admin.user.UserService;
import com.shop.common.entity.User;

import jakarta.annotation.Resource;

@Controller
@RequestMapping("/account")
public class AccountController {

    @Resource
    private UserService service;

    @GetMapping("")
    public String viewDetails(@AuthenticationPrincipal ShopUserDetails loggedUser, Model model) {
	String email = loggedUser.getUsername();
	User user = service.getByEmail(email);

	model.addAttribute("user", user);

	return "users/account_form";
    }

    @PostMapping("/update")
    public String saveDetails(User user, RedirectAttributes redirectAttributes, @AuthenticationPrincipal ShopUserDetails loggedUser, @RequestParam("image") MultipartFile multipartFile) throws IOException {
	if (!multipartFile.isEmpty()) {
	    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

	    user.setPhotos(fileName);
	    User savedUser = service.updateAccount(user);

	    String uploadDir = "user-photos/" + savedUser.getId();

	    FileUploadUtil.cleanDir(uploadDir);
	    FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
	} else {
	    if (user.getPhotos().isEmpty()) {
		user.setPhotos(null);
	    }

	    service.updateAccount(user);
	}

	// 이름 변경 시 재로그인 없이 view layer에도 변경을 알려준다
	loggedUser.setName(user.getName());
	
	redirectAttributes.addFlashAttribute("message", "계정정보가 수정 되었습니다.");

	return "redirect:/account";
    }
}
