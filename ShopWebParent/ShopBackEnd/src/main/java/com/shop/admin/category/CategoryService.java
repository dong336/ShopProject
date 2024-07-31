package com.shop.admin.category;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.common.entity.Category;

import jakarta.annotation.Resource;

@Service
@Transactional
public class CategoryService {
    
    public static final int ROOT_CATEGORIES_PER_PAGE = 4;
    
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

		if (categoryByAlias != null) {
		    return "중복된 별칭";
		}
	    }
	} else {
	    if (categoryByName != null && categoryByName.getId() != id) {
		return "중복된 이름";
	    }

	    Category categoryByAlias = repo.findByAlias(alias);

	    if (categoryByAlias != null && categoryByAlias.getId() != id) {
		return "중복된 별칭";
	    }
	}

	return "OK";
    }

    public Category get(Integer id) throws CategoryException {
	try {
	    return repo.findById(id).get();
	} catch (NoSuchElementException e) {
	    throw new CategoryException("해당 카테고리를 찾을 수 없습니다 ID: " + id);
	}
    }

    public List<Category> listByPage(CategoryPageInfo pageInfo, int pageNum, String sortDir,
	    String keyword) {
	Sort sort = Sort.by("name");

	if (sortDir.equals("asc")) {
	    sort = sort.ascending();
	} else if (sortDir.equals("desc")) {
	    sort = sort.descending();
	}
	
	Pageable pageable = PageRequest.of(pageNum - 1, ROOT_CATEGORIES_PER_PAGE, sort);
	Page<Category> pageCategories;
	
	if (keyword != null && !keyword.isEmpty()) {
	    pageCategories = repo.search(keyword, pageable);
	} else {
	    pageCategories = repo.findRootCategories(pageable);
	}
	
	List<Category> rootCategories = pageCategories.getContent();
	
	pageInfo.setTotalElement(pageCategories.getTotalElements());
	pageInfo.setTotalPages(pageCategories.getTotalPages());

	if (keyword != null && !keyword.isEmpty()) {
	    List<Category> searchResult = pageCategories.getContent();
	    
	    for(Category category : searchResult) {
		category.setHasChildren(category.getChildren().size() > 0);
	    }
	    
	    return searchResult;
	} else {
	    return listHierarchialCategories(rootCategories, sortDir);
	}
    }

    private List<Category> listHierarchialCategories(List<Category> rootCategories, String sortDir) {
	List<Category> hierarchicalCategories = new ArrayList<>();

	for (Category rootCategory : rootCategories) {
	    hierarchicalCategories.add(Category.copyFull(rootCategory));

	    Set<Category> children = sortSubCategories(rootCategory.getChildren(), sortDir);

	    for (Category subCategory : children) {
		String name = "--" + subCategory.getName();
		hierarchicalCategories.add(Category.copyFull(subCategory, name));

		listSubHierarchialCategories(hierarchicalCategories, subCategory, 1, sortDir);
	    }
	}

	return hierarchicalCategories;
    }

    private List<Category> listSubHierarchialCategories(List<Category> hierarchicalCategories, Category paren, int subLevel, String sortDir) {
	Set<Category> children = sortSubCategories(paren.getChildren(), sortDir);
	int newSubLevel = subLevel + 1;

	for (Category subCategory : children) {
	    String name = "";

	    for (int i = 0; i < newSubLevel; i++) {
		name += "--";
	    }
	    name += subCategory.getName();

	    hierarchicalCategories.add(Category.copyFull(subCategory, name));

	    listSubHierarchialCategories(hierarchicalCategories, subCategory, newSubLevel, sortDir);
	}

	return hierarchicalCategories;
    }

    public Category save(Category category) {
	return repo.save(category);
    }

    public List<Category> listCategoriesUsedInForm(String seperator) {
	List<Category> categoriesUsedInForm = new ArrayList<>();
	Iterable<Category> categoriesInDB = repo.findRootCategories(Sort.by("name").ascending());

	for (Category category : categoriesInDB) {
	    if (category.getParent() == null) {
		categoriesUsedInForm.add(Category.copyIdAndName(category));

		Set<Category> children = sortSubCategories(category.getChildren());

		for (Category subCategory : children) {
		    String name = seperator + subCategory.getName();
		    categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));

		    listSubCategoiesUsedInForm(categoriesUsedInForm, subCategory, 1, seperator);
		}
	    }
	}

	return categoriesUsedInForm;
    }

    private void listSubCategoiesUsedInForm(List<Category> categoriesUsedInForm, Category parent, int subLevel, String seperator) {
	int newSubLevel = subLevel + 1;
	Set<Category> children = sortSubCategories(parent.getChildren());

	for (Category subCategory : children) {
	    String name = "";

	    for (int i = 0; i < newSubLevel; i++) {
		name += seperator;
	    }

	    name += subCategory.getName();

	    categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));

	    listSubCategoiesUsedInForm(categoriesUsedInForm, subCategory, newSubLevel, seperator);
	}
    }

    private SortedSet<Category> sortSubCategories(Set<Category> children) {
	return sortSubCategories(children, "asc");
    }

    private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir) {
	SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {

	    @Override
	    public int compare(Category o1, Category o2) {
		if (sortDir.equals("asc")) {
		    return o1.getName().compareTo(o2.getName());
		} else {
		    return o2.getName().compareTo(o1.getName());
		}
	    }
	});
	sortedChildren.addAll(children);

	return sortedChildren;
    }
    
    public void updateCategoryEnabledStatus(Integer id, boolean enabled) {
	repo.updateEnabledStatus(id, enabled);
    }
    
    public void delete(Integer id) throws CategoryException {
	Long countById = repo.countById(id);
	
	if (countById == null || countById < 1) {
	    throw new CategoryException("해당하는 카테고리를 찾을 수 없습니다 ID : " + id);
	}
	
	repo.deleteById(id);
    }
}
