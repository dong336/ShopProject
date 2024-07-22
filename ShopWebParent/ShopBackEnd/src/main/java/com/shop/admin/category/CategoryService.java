package com.shop.admin.category;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.shop.common.entity.Category;

import jakarta.annotation.Resource;

@Service
public class CategoryService {

    @Resource
    private CategoryRepository repo;

    public String checkUnique(Integer id, String name, String alias) {
	boolean isCreatingNew = (id == null || id == 0);

	Category categoryByName = repo.findByName(name);

	if (isCreatingNew) {
	    if (categoryByName != null) {
		
		return "중복된 이름";
	    } else {
		Category categoryByAlias = repo.findByAlias(alias);
		
		if(categoryByAlias != null) {
		    return "중복된 별칭";
		}
	    }
	} else {
	    if (categoryByName != null && categoryByName.getId() != id) {
		return "중복된 이름";
	    }
	    
	    Category categoryByAlias = repo.findByAlias(alias);
	    
	    if(categoryByAlias != null && categoryByAlias.getId() != id) {
		return "중복된 별칭";
	    }
	}

	return "OK";
    }

    public Category get(Integer id) throws CategoryNotFoundException {
	try {
	    return repo.findById(id).get();
	} catch (NoSuchElementException e) {
	    throw new CategoryNotFoundException("해당 카테고리를 찾을 수 없습니다 ID: " + id);
	}
    }

    public List<Category> listAll() {
	List<Category> rootCategories = repo.findRootCategories();

	return listHierarchialCategories(rootCategories);
    }

    private List<Category> listHierarchialCategories(List<Category> rootCategories) {
	List<Category> hierarchicalCategories = new ArrayList<>();

	for (Category rootCategory : rootCategories) {
	    hierarchicalCategories.add(Category.copyFull(rootCategory));

	    Set<Category> children = rootCategory.getChildren();

	    for (Category subCategory : children) {
		String name = "--" + subCategory.getName();
		hierarchicalCategories.add(Category.copyFull(subCategory, name));

		listSubHierarchialCategories(hierarchicalCategories, subCategory, 1);
	    }
	}

	return hierarchicalCategories;
    }

    private List<Category> listSubHierarchialCategories(List<Category> hierarchicalCategories, Category paren, int subLevel) {
	Set<Category> children = paren.getChildren();
	int newSubLevel = subLevel + 1;

	for (Category subCategory : children) {
	    String name = "";

	    for (int i = 0; i < newSubLevel; i++) {
		name += "--";
	    }
	    name += subCategory.getName();

	    hierarchicalCategories.add(Category.copyFull(subCategory, name));

	    listSubHierarchialCategories(hierarchicalCategories, subCategory, newSubLevel);
	}

	return hierarchicalCategories;
    }

    public Category save(Category category) {
	return repo.save(category);
    }

    public List<Category> listCategoriesUsedInForm() {
	List<Category> categoriesUsedInForm = new ArrayList<>();
	Iterable<Category> categoriesInDB = repo.findAll();

	for (Category category : categoriesInDB) {
	    if (category.getParent() == null) {
		categoriesUsedInForm.add(Category.copyIdAndName(category));

		Set<Category> children = category.getChildren();

		for (Category subCategory : children) {
		    String name = "--" + subCategory.getName();
		    categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));

		    listSubCategoiesUsedInForm(categoriesUsedInForm, subCategory, 1);
		}
	    }
	}

	return categoriesUsedInForm;
    }

    private void listSubCategoiesUsedInForm(List<Category> categoriesUsedInForm, Category parent, int subLevel) {
	int newSubLevel = subLevel + 1;
	Set<Category> children = parent.getChildren();

	for (Category subCategory : children) {
	    String name = "";

	    for (int i = 0; i < newSubLevel; i++) {
		name += "--";
	    }

	    name += subCategory.getName();

	    categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));

	    listSubCategoiesUsedInForm(categoriesUsedInForm, subCategory, newSubLevel);
	}
    }
}
