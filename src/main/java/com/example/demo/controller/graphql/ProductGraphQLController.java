package com.example.demo.controller.graphql;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.service.ICategoryService;
import com.example.demo.service.IProductService;

@Controller
public class ProductGraphQLController {

    @Autowired
    private IProductService productService;

    @Autowired
    private ICategoryService categoryService;

    @QueryMapping
    public List<Product> products() {
        return productService.findAll();
    }

    @QueryMapping
    public List<Product> productsByPriceAsc() {
        return productService.findAllByOrderByUnitPriceAsc();
    }

    @QueryMapping
    public List<Product> productsByCategoryId(@Argument Long categoryId) {
        return productService.findByCategoryCategoryId(categoryId);
    }

    @QueryMapping
    public Optional<Product> productById(@Argument Long id) {
        return productService.findById(id);
    }

    @QueryMapping
    public List<Category> categories() {
        return categoryService.findAll();
    }

    @QueryMapping
    public Optional<Category> categoryById(@Argument Long id) {
        return categoryService.findById(id);
    }

    @MutationMapping
    public Category createCategory(@Argument Map<String, Object> category) {
        Category cat = new Category();
        cat.setCategoryName((String) category.get("categoryName"));
        cat.setIcon((String) category.get("icon"));
        return categoryService.save(cat);
    }

    @MutationMapping
    public Category updateCategory(@Argument Long id, @Argument Map<String, Object> category) {
        Optional<Category> opt = categoryService.findById(id);
        if (opt.isPresent()) {
            Category cat = opt.get();
            if (category.containsKey("categoryName")) {
                cat.setCategoryName((String) category.get("categoryName"));
            }
            if (category.containsKey("icon")) {
                cat.setIcon((String) category.get("icon"));
            }
            return categoryService.save(cat);
        }
        return null;
    }

    @MutationMapping
    public Boolean deleteCategory(@Argument Long id) {
        Optional<Category> opt = categoryService.findById(id);
        if (opt.isPresent()) {
            categoryService.delete(opt.get());
            return true;
        }
        return false;
    }

    @MutationMapping
    public Product createProduct(@Argument Map<String, Object> product) {
        Product p = new Product();
        p.setProductName((String) product.get("productName"));
        p.setQuantity(((Number) product.get("quantity")).intValue());
        p.setUnitPrice(((Number) product.get("unitPrice")).doubleValue());
        if (product.get("images") != null) p.setImages((String) product.get("images"));
        if (product.get("description") != null) p.setDescription((String) product.get("description"));
        if (product.get("discount") != null) p.setDiscount(((Number) product.get("discount")).doubleValue());
        if (product.get("status") != null) p.setStatus(((Number) product.get("status")).shortValue());

        Long categoryId = Long.valueOf(product.get("categoryId").toString());
        categoryService.findById(categoryId).ifPresent(p::setCategory);
        p.setCreateDate(new Date());

        return productService.save(p);
    }

    @MutationMapping
    public Product updateProduct(@Argument Long id, @Argument Map<String, Object> product) {
        Optional<Product> opt = productService.findById(id);
        if (opt.isPresent()) {
            Product p = opt.get();
            if (product.get("productName") != null) p.setProductName((String) product.get("productName"));
            if (product.get("quantity") != null) p.setQuantity(((Number) product.get("quantity")).intValue());
            if (product.get("unitPrice") != null) p.setUnitPrice(((Number) product.get("unitPrice")).doubleValue());
            if (product.get("images") != null) p.setImages((String) product.get("images"));
            if (product.get("description") != null) p.setDescription((String) product.get("description"));
            if (product.get("discount") != null) p.setDiscount(((Number) product.get("discount")).doubleValue());
            if (product.get("status") != null) p.setStatus(((Number) product.get("status")).shortValue());
            if (product.get("categoryId") != null) {
                Long categoryId = Long.valueOf(product.get("categoryId").toString());
                categoryService.findById(categoryId).ifPresent(p::setCategory);
            }
            return productService.save(p);
        }
        return null;
    }

    @MutationMapping
    public Boolean deleteProduct(@Argument Long id) {
        Optional<Product> opt = productService.findById(id);
        if (opt.isPresent()) {
            productService.delete(opt.get());
            return true;
        }
        return false;
    }
}

