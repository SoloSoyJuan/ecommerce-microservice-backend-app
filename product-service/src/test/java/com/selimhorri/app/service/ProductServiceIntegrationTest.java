package com.selimhorri.app.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.selimhorri.app.domain.Category;
import com.selimhorri.app.domain.Product;
import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.exception.wrapper.ProductNotFoundException;
import com.selimhorri.app.repository.CategoryRepository;
import com.selimhorri.app.repository.ProductRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    private ProductDto productDto;

    private Product product;

    private static final String DEFAULT_PRODUCT_TITLE = "Product Title";
	private static final String DEFAULT_IMAGEURL = "http://example.com/image.jpg";
	private static final String DEFAULT_SKU = "SKU12345";
	private static final Double DEFAULT_PRICEUNIT = 19.99;
	private static final Integer DEFAULT_QUANTITY = 100;

	private static final String DEFAULT_CATEGORYTYTLE = "Category Title";
    
    private static final String UPDATE_PRODUCT_TITLE = "Updated Product Title";
    private static final String UPDATE_IMAGEURL = "http://example.com/updated_image.jpg";
    private static final String UPDATE_SKU = "SKU54321";
    private static final Double UPDATE_PRICEUNIT = 29.99;
    private static final Integer UPDATE_QUANTITY = 50;

    private CategoryDto categoryDto;
    
    @BeforeEach
    public void setUp() {
        productRepository.deleteAll();

        
        
        categoryDto = CategoryDto.builder()
                .categoryId(1)
                .categoryTitle(DEFAULT_CATEGORYTYTLE)
                .imageUrl("http://example.com/category_image.jpg")
                .parentCategoryDto(null)
                .build();

        productDto = ProductDto.builder()
                .productId(1)
                .productTitle(DEFAULT_PRODUCT_TITLE)
                .imageUrl(DEFAULT_IMAGEURL)
                .sku(DEFAULT_SKU)
                .priceUnit(DEFAULT_PRICEUNIT)
                .quantity(DEFAULT_QUANTITY)
                .categoryDto(categoryDto)
                .build();

        Category category = Category.builder()
                .categoryTitle(DEFAULT_CATEGORYTYTLE)
                .build();

        product = Product.builder()
                .productTitle(DEFAULT_PRODUCT_TITLE)
                .imageUrl(DEFAULT_IMAGEURL)
                .sku(DEFAULT_SKU)
                .priceUnit(DEFAULT_PRICEUNIT)
                .quantity(DEFAULT_QUANTITY)
                .category(category)
                .build();

        if (category != null) {
            category.setProducts(Set.of(product));
        }
    }

    @Test
    public void getAllProducts_shouldReturnAllProducts() {

        productRepository.save(product);

        List<ProductDto> products = productService.findAll();

        assertThat(products).isNotNull();
        assertThat(products).hasSize(1);
        assertThat(products.iterator().next().getProductTitle()).isEqualTo(DEFAULT_PRODUCT_TITLE);
    }

    @Test
    public void saveProduct_shouldSaveProduct() {

        categoryService.save(categoryDto);

        ProductDto savedProductDto = productService.save(productDto);


        assertThat(savedProductDto).isNotNull();
        assertThat(savedProductDto.getProductId()).isNotNull();
        assertThat(savedProductDto.getProductTitle()).isEqualTo(DEFAULT_PRODUCT_TITLE);
        assertThat(savedProductDto.getImageUrl()).isEqualTo(DEFAULT_IMAGEURL);
        assertThat(savedProductDto.getSku()).isEqualTo(DEFAULT_SKU);
        assertThat(savedProductDto.getPriceUnit()).isEqualTo(DEFAULT_PRICEUNIT);
        assertThat(savedProductDto.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(savedProductDto.getCategoryDto().getCategoryTitle()).isEqualTo(DEFAULT_CATEGORYTYTLE);

        Optional<Product> repoProduct = productRepository.findById(savedProductDto.getProductId());
        assertThat(repoProduct).isPresent();
        assertThat(repoProduct.get().getProductTitle()).isEqualTo(DEFAULT_PRODUCT_TITLE);
        assertThat(repoProduct.get().getImageUrl()).isEqualTo(DEFAULT_IMAGEURL);
        assertThat(repoProduct.get().getSku()).isEqualTo(DEFAULT_SKU);
        assertThat(repoProduct.get().getPriceUnit()).isEqualTo(DEFAULT_PRICEUNIT);
        assertThat(repoProduct.get().getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(repoProduct.get().getCategory().getCategoryTitle()).isEqualTo(DEFAULT_CATEGORYTYTLE);
    }

    @Test
    public void getProductById_shouldReturnProduct() {
        Product savedProduct = productRepository.save(product);
        Integer productId = savedProduct.getProductId();

        ProductDto foundProductDto = productService.findById(productId);

        assertThat(foundProductDto).isNotNull();
        assertThat(foundProductDto.getProductId()).isEqualTo(productId);
        assertThat(foundProductDto.getProductTitle()).isEqualTo(DEFAULT_PRODUCT_TITLE);
    }

    @Test
    public void updateProduct_shouldUpdateProduct() {
        Product savedProduct = productRepository.save(product);
        Integer productId = savedProduct.getProductId();

        CategoryDto category = CategoryDto.builder()
                .categoryTitle(DEFAULT_CATEGORYTYTLE+" 2")
                .build();

        ProductDto updatedProductDto = ProductDto.builder()
                .productId(productId)
                .productTitle(UPDATE_PRODUCT_TITLE)
                .imageUrl(UPDATE_IMAGEURL)
                .sku(UPDATE_SKU)
                .priceUnit(UPDATE_PRICEUNIT)
                .quantity(UPDATE_QUANTITY)
                .categoryDto(category)
                .build();

        ProductDto result = productService.update( savedProduct.getProductId(), updatedProductDto);

        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo(productId);
        assertThat(result.getProductTitle()).isEqualTo(UPDATE_PRODUCT_TITLE);
        assertThat(result.getImageUrl()).isEqualTo(UPDATE_IMAGEURL);
        assertThat(result.getSku()).isEqualTo(UPDATE_SKU);
        assertThat(result.getPriceUnit()).isEqualTo(UPDATE_PRICEUNIT);
        assertThat(result.getQuantity()).isEqualTo(UPDATE_QUANTITY);

        Optional<Product> repoProduct = productRepository.findById(productId);
        assertThat(repoProduct).isPresent();
        assertThat(repoProduct.get().getProductTitle()).isEqualTo(UPDATE_PRODUCT_TITLE);
        assertThat(repoProduct.get().getImageUrl()).isEqualTo(UPDATE_IMAGEURL);
        assertThat(repoProduct.get().getSku()).isEqualTo(UPDATE_SKU);
        assertThat(repoProduct.get().getPriceUnit()).isEqualTo(UPDATE_PRICEUNIT);
        assertThat(repoProduct.get().getQuantity()).isEqualTo(UPDATE_QUANTITY);
        assertThat(repoProduct.get().getCategory().getCategoryTitle()).isEqualTo(DEFAULT_CATEGORYTYTLE+" 2");
    }

    @Test
    public void getProductById_shouldNotFound() {
        Integer nonExistentId = 9999;

        assertThrows(NullPointerException.class, () -> {
            productService.findById(nonExistentId);
        });
    }
}
