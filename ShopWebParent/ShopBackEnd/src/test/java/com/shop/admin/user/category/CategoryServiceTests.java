package com.shop.admin.user.category;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.shop.admin.category.CategoryRepository;
import com.shop.admin.category.CategoryService;
import com.shop.common.entity.Category;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class CategoryServiceTests {
    @MockBean
    private CategoryRepository repo;
    
    @InjectMocks
    private CategoryService service;
    
    @Test
    public void testCheckUniqueInNewModeReturnDuplicatedName() {
	Integer id = null;
	String name = "컴퓨터";
	String alias = "abc";
	
	Category category = new Category(id, name, alias);
	
	Mockito.when(repo.findByName(name)).thenReturn(category);
	Mockito.when(repo.findByAlias(alias)).thenReturn(null);
	
	String result = service.checkUnique(id, name, alias);
	
	// Mock 데이터는 중복된 이름의 데이터를 생성할 것이다
	assertThat(result).isEqualTo("중복된 이름");
    }
    
    @Test
    public void testCheckUniqueInNewModeReturnDuplicatedAlias() {
	Integer id = null;
	String name = "상품1";
	String alias = "컴퓨터";
	
	Category category = new Category(id, name, alias);
	
	Mockito.when(repo.findByName(name)).thenReturn(null);
	Mockito.when(repo.findByAlias(alias)).thenReturn(category);
	
	String result = service.checkUnique(id, name, alias);
	
	// Mock 데이터는 중복된 별칭의 데이터를 생성할 것이다
	assertThat(result).isEqualTo("중복된 별칭");
    }
    
    @Test
    public void testCheckUniqueInNewModeReturnOK() {
	Integer id = null;
	String name = "상품1";
	String alias = "컴퓨터";
	
	Mockito.when(repo.findByName(name)).thenReturn(null);
	Mockito.when(repo.findByAlias(alias)).thenReturn(null);
	
	String result = service.checkUnique(id, name, alias);
	
	// Mock 데이터는 중복된 별칭의 데이터를 생성할 것이다
	assertThat(result).isEqualTo("OK");
    }
    
    @Test
    public void testCheckUniqueInEditModeReturnDuplicatedName() {
	Integer id = 1;
	String name = "컴퓨터";
	String alias = "abc";
	
	Category category = new Category(2, name, alias);
	
	Mockito.when(repo.findByName(name)).thenReturn(category);
	Mockito.when(repo.findByAlias(alias)).thenReturn(null);
	
	String result = service.checkUnique(id, name, alias);
	
	// Mock 데이터는 중복된 이름의 데이터를 생성할 것이다
	assertThat(result).isEqualTo("중복된 이름");
    }
    
    @Test
    public void testCheckUniqueInEditModeReturnDuplicatedAlias() {
	Integer id = 1;
	String name = "abc";
	String alias = "컴퓨터";
	
	Category category = new Category(2, name, alias);
	
	Mockito.when(repo.findByName(name)).thenReturn(null);
	Mockito.when(repo.findByAlias(alias)).thenReturn(category);
	
	String result = service.checkUnique(id, name, alias);
	
	// Mock 데이터는 중복된 별칭의 데이터를 생성할 것이다
	assertThat(result).isEqualTo("중복된 별칭");
    }
    
    @Test
    public void testCheckUniqueInEditModeReturnOK() {
	Integer id = 1;
	String name = "abc";
	String alias = "컴퓨터";
	
	Category category = new Category(id, name, alias);
	
	Mockito.when(repo.findByName(name)).thenReturn(null);
	Mockito.when(repo.findByAlias(alias)).thenReturn(category);
	
	String result = service.checkUnique(id, name, alias);
	
	// Mock 데이터는 중복된 별칭의 데이터를 생성할 것이다
	assertThat(result).isEqualTo("OK");
    }
}
