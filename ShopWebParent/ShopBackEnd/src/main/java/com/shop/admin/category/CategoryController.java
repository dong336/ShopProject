package com.shop.admin.category;

import java.io.IOException;
import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shop.admin.FileUploadUtil;
import com.shop.common.entity.Category;

import jakarta.annotation.Resource;

@Controller
@RequestMapping("/categories")
public class CategoryController {
    @Resource
    private CategoryService service;

    @GetMapping("")
    public String listFirstPage(@Param("sortDir") String sortDir, Model model) {
	return listByPage(1, model, sortDir, null);
    }
    
    @GetMapping("/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model, 
	    @Param("sortDir") String sortDir,
	    @Param("keyword") String keyword) {
	if (sortDir == null || sortDir.isEmpty()) {
	    sortDir = "asc";
	}
	
	CategoryPageInfo pageInfo = new CategoryPageInfo();
	List<Category> listCategories = service.listByPage(pageInfo, pageNum, sortDir, keyword);
	
	long startCount = (pageNum - 1) * CategoryService.ROOT_CATEGORIES_PER_PAGE + 1;
	long endCount = startCount + CategoryService.ROOT_CATEGORIES_PER_PAGE - 1;

	if (endCount > pageInfo.getTotalElements()) {
	    endCount = pageInfo.getTotalElements();
	}
	
	String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

	model.addAttribute("totalPages", pageInfo.getTotalPages());
	model.addAttribute("totalItems", pageInfo.getTotalElements());
	model.addAttribute("currentPage", pageNum);
	model.addAttribute("sortField", "name");
	model.addAttribute("sortDir", sortDir);
	model.addAttribute("startCount", startCount);
	model.addAttribute("endCount", endCount);
	model.addAttribute("keyword", keyword);
	
	model.addAttribute("listCategories", listCategories);
	model.addAttribute("reverseSortDir", reverseSortDir);

	return "categories/categories";
    }

    @GetMapping("/new")
    public String newCategory(Model model) {
	List<Category> listCategories = service.listCategoriesUsedInForm();

	model.addAttribute("category", new Category());
	model.addAttribute("listCategories", listCategories);
	model.addAttribute("pageTitle", "새 카테고리 등록");

	return "categories/category_form";
    }

    @PostMapping("/save")
    public String saveCategory(Category category, @RequestParam("fileImage") MultipartFile multipartFile, RedirectAttributes redirectAttributes) throws IOException {
	if (!multipartFile.isEmpty()) {
	    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
	    category.setImage(fileName);

	    Category savedCategory = service.save(category);
	    String uploadDir = "../category-images/" + savedCategory.getId();
	    FileUploadUtil.cleanDir(uploadDir);
	    FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
	} else {
	    service.save(category);
	}
	
	if (category.getId() == null) {
	    redirectAttributes.addFlashAttribute("message", "카테고리 등록 성공");
	} else {
	    redirectAttributes.addFlashAttribute("message", "카테고리 변경 성공");
	}

	return "redirect:/categories";
    }

    @GetMapping("/edit/{id}")
    public String editCategory(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
	try {
	    Category category = service.get(id);
	    List<Category> listCategories = service.listCategoriesUsedInForm();

	    model.addAttribute("category", category);
	    model.addAttribute("listCategories", listCategories);
	    model.addAttribute("pageTitle", "카테고리 수정 (ID: " + id + ")");

	    return "categories/category_form";
	} catch (CategoryException e) {
	    redirectAttributes.addFlashAttribute("message", e.getMessage());

	    return "redirect:/categories";
	}
    }
    
    @GetMapping("/{id}/enabled/{status}")
    public String updateCategoryEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled,
	    RedirectAttributes redirectAttributes) {
	service.updateCategoryEnabledStatus(id,  enabled);
	String status = enabled ? "활성화" : "비활성화";
	String message = "이 카테고리는 " + status + " 되었습니다. (ID : " + id + ")";
	
	redirectAttributes.addFlashAttribute("message", message);
	
	return "redirect:/categories";
    }
    
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
	try {
	    service.delete(id);
	    String categoryDir = "../category-images/" + id;
	    FileUploadUtil.removeDir(categoryDir);
	    
	    redirectAttributes.addFlashAttribute("message", "카테고리가 삭제되었습니다. ID : " + id);
	} catch (CategoryException e) {
	    redirectAttributes.addFlashAttribute("message", e.getMessage());
	}
	
	return "redirect:/categories";
    }
}
