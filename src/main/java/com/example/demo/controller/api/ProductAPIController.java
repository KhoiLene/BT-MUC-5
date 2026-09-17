package com.example.demo.controller.api;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.model.Response;
import com.example.demo.service.ICategoryService;
import com.example.demo.service.IProductService;
import com.example.demo.service.IStorageService;

@RestController
@RequestMapping(path = "/api/product")
public class ProductAPIController {

    @Autowired
    private IProductService productService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IStorageService storageService;

    @GetMapping
    public ResponseEntity<?> getAllProduct() {
        return new ResponseEntity<Response>(
            new Response(true, "Thành công", productService.findAll()), HttpStatus.OK);
    }

    @GetMapping(path = "/paged")
    public ResponseEntity<?> getProductsPaged(
            @RequestParam(name = "name", required = false, defaultValue = "") String name,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> result;
        if (name != null && !name.trim().isEmpty()) {
            result = productService.findByProductNameContaining(name, pageable);
        } else {
            result = productService.findAll(pageable);
        }
        return new ResponseEntity<Response>(new Response(true, "Thành công", result), HttpStatus.OK);
    }

    @GetMapping(path = "/price-asc")
    public ResponseEntity<?> getProductsSortedByPriceAsc() {
        List<Product> list = productService.findAllByOrderByUnitPriceAsc();
        return new ResponseEntity<Response>(new Response(true, "Lấy danh sách sản phẩm giá từ thấp đến cao thành công", list), HttpStatus.OK);
    }

    @GetMapping(path = "/category/{categoryId}")
    public ResponseEntity<?> getProductsByCategory(@PathVariable("categoryId") Long categoryId) {
        List<Product> list = productService.findByCategoryCategoryId(categoryId);
        return new ResponseEntity<Response>(new Response(true, "Lấy danh sách sản phẩm theo Category thành công", list), HttpStatus.OK);
    }

    @PostMapping(path = "/addProduct")
    public ResponseEntity<?> saveOrUpdate(
            @Validated @RequestParam("productName") String productName,
            @RequestParam(value = "imageFile", required = false) MultipartFile productImages,
            @Validated @RequestParam("unitPrice") Double productPrice,
            @RequestParam(value = "discount", defaultValue = "0") Double promotionalPrice,
            @RequestParam(value = "description", defaultValue = "") String productDescription,
            @Validated @RequestParam("categoryId") Long categoryId,
            @Validated @RequestParam("quantity") Integer quantity,
            @RequestParam(value = "status", defaultValue = "1") Short status) {

        Optional<Product> optProduct = productService.findByProductName(productName);
        if (optProduct.isPresent()) {
            return new ResponseEntity<Response>(
                new Response(false, "Sản phẩm này đã tồn tại trong hệ thống", optProduct.get()),
                HttpStatus.BAD_REQUEST);
        } else {
            Product product = new Product();
            product.setProductName(productName);
            product.setUnitPrice(productPrice);
            product.setDiscount(promotionalPrice);
            product.setDescription(productDescription);
            product.setQuantity(quantity);
            product.setStatus(status);

            Optional<Category> cateOpt = categoryService.findById(categoryId);
            cateOpt.ifPresent(product::setCategory);

            Timestamp timestamp = new Timestamp(new Date().getTime());
            product.setCreateDate(timestamp);

            if (productImages != null && !productImages.isEmpty()) {
                UUID uuid = UUID.randomUUID();
                String uuString = uuid.toString();
                product.setImages(storageService.getStorageFilename(productImages, uuString));
                storageService.store(productImages, product.getImages());
            }

            productService.save(product);
            return new ResponseEntity<Response>(new Response(true, "Thành công", product), HttpStatus.OK);
        }
    }

    @PutMapping(path = "/updateProduct")
    public ResponseEntity<?> updateProduct(
            @Validated @RequestParam("productId") Long productId,
            @Validated @RequestParam("productName") String productName,
            @RequestParam(value = "imageFile", required = false) MultipartFile productImages,
            @Validated @RequestParam("unitPrice") Double productPrice,
            @RequestParam(value = "discount", defaultValue = "0") Double promotionalPrice,
            @RequestParam(value = "description", defaultValue = "") String productDescription,
            @Validated @RequestParam("categoryId") Long categoryId,
            @Validated @RequestParam("quantity") Integer quantity,
            @RequestParam(value = "status", defaultValue = "1") Short status) {

        Optional<Product> optProduct = productService.findById(productId);
        if (optProduct.isEmpty()) {
            return new ResponseEntity<Response>(
                new Response(false, "Không tìm thấy Product", null), HttpStatus.BAD_REQUEST);
        } else {
            Product product = optProduct.get();
            product.setProductName(productName);
            product.setUnitPrice(productPrice);
            product.setDiscount(promotionalPrice);
            product.setDescription(productDescription);
            product.setQuantity(quantity);
            product.setStatus(status);

            Optional<Category> cateOpt = categoryService.findById(categoryId);
            cateOpt.ifPresent(product::setCategory);

            if (productImages != null && !productImages.isEmpty()) {
                UUID uuid = UUID.randomUUID();
                String uuString = uuid.toString();
                product.setImages(storageService.getStorageFilename(productImages, uuString));
                storageService.store(productImages, product.getImages());
            }

            productService.save(product);
            return new ResponseEntity<Response>(new Response(true, "Cập nhật Thành công", product), HttpStatus.OK);
        }
    }

    @DeleteMapping(path = "/deleteProduct")
    public ResponseEntity<?> deleteProduct(@Validated @RequestParam("productId") Long productId) {
        Optional<Product> optProduct = productService.findById(productId);
        if (optProduct.isEmpty()) {
            return new ResponseEntity<Response>(
                new Response(false, "Không tìm thấy Product", null), HttpStatus.BAD_REQUEST);
        } else {
            productService.delete(optProduct.get());
            return new ResponseEntity<Response>(new Response(true, "Xóa Thành công", optProduct.get()), HttpStatus.OK);
        }
    }
}

