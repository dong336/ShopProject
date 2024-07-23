package com.shop.admin.user.category;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import com.shop.admin.category.CategoryRepository;
import com.shop.common.entity.Category;

import jakarta.annotation.Resource;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTests {

    @Resource
    private CategoryRepository repo;

    @Test
    public void testCreateAll() {
	Category category1 = new Category("컴퓨터");
	Category category2 = new Category("전자제품");

	repo.saveAll(List.of(category1, category2));

	Category desktops = new Category("데스크탑", category1);
	Category laptops = new Category("노트북", category1);
	Category components = new Category("컴퓨터 부품", category1);
	Category cameras = new Category("카메라", category2);
	Category smartphones = new Category("스마트폰", category2);
	Category memory = new Category("메모리", components);
	Category gamingLaptops = new Category("게이밍 노트북", category2);
	Category iPhone = new Category("아이폰", smartphones);
	
	repo.saveAll(List.of(desktops, laptops, components, cameras, smartphones, memory, gamingLaptops, iPhone));
    }
    
    @Test
    public void testCreateRootCategory() {
	Category category1 = new Category("컴퓨터");
	Category category2 = new Category("전자제품");
	List<Category> savedCategories = (List<Category>) repo.saveAll(List.of(category1, category2));

	assertThat(savedCategories.size()).isEqualTo(2);
    }

    @Test
    public void testCreateSubCategory() {
	Category parent = new Category(1);
//	Category desktops = new Category("데스크탑", parent);
//	Category laptops = new Category("노트북", parent);
//	Category components = new Category("컴퓨터 부품", parent);
//	Category cameras = new Category("카메라", parent);
//	Category smartphones = new Category("스마트폰", parent);

//	repo.saveAll(List.of(cameras, smartphones));
	Category subCategory = new Category("아이폰", parent);
	Category savedCategory = repo.save(subCategory);

	assertThat(savedCategory.getId()).isGreaterThan(0);
    }

    @Test
    public void testGetCategory() {
	Category category = repo.findById(2).get();
	Set<Category> children = category.getChildren();

	System.out.println(category.getName());

	children.forEach(cate -> System.out.println(cate.getName()));

	assertThat(children.size()).isGreaterThan(0);
    }

    @Test
    public void testPrintHierarchicalCategories() {
	Iterable<Category> categories = repo.findAll();

	for (Category category : categories) {
	    if (category.getParent() == null) {
		System.out.println(category.getName());

		Set<Category> children = category.getChildren();

		for (Category subCategory : children) {
		    System.out.println("--" + subCategory.getName());
		    printChildren(subCategory, 1);
		}
	    }
	}
    }

    private void printChildren(Category parent, int subLevel) {
	int newSubLevel = subLevel + 1;
	Set<Category> children = parent.getChildren();

	for (Category subCategory : children) {
	    for (int i = 0; i < newSubLevel; i++) {
		System.out.print("--");
	    }
	    System.out.println(subCategory.getName());

	    printChildren(subCategory, newSubLevel);
	}
    }
    
    @Test
    public void testListRootCategories() {
	List<Category> rootCategories = repo.findRootCategories(Sort.by("name"));
	rootCategories.forEach(cate -> System.out.println(cate.getName()));
    }
    
    @Test
    public void testFindByName() {
	String name = "컴퓨터";
	Category category = repo.findByName(name);
	
	assertThat(category).isNotNull();
	assertThat(category.getName()).isEqualTo(name);
    }
    
    @Test
    public void testFindByAlias() {
	String alias = "전자제품";
	Category category = repo.findByAlias(alias);
	
	assertThat(category).isNotNull();
	assertThat(category.getAlias()).isEqualTo(alias);
    }
}
