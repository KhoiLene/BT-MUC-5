package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.example.demo.config.StorageProperties;
import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.service.ICategoryService;
import com.example.demo.service.IProductService;
import com.example.demo.service.IStorageService;

import java.util.Date;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class Muc5Application {

    public static void main(String[] args) {
        SpringApplication.run(Muc5Application.class, args);
    }

    @Bean
    CommandLineRunner init(IStorageService storageService, ICategoryService categoryService, IProductService productService) {
        return (args -> {
            storageService.init();

            // Seed initial sample data if DB is empty
            if (categoryService.count() == 0) {
                Category cat1 = new Category();
                cat1.setCategoryName("Điện thoại");
                cat1.setIcon("phone.png");
                cat1 = categoryService.save(cat1);

                Category cat2 = new Category();
                cat2.setCategoryName("Laptop");
                cat2.setIcon("laptop.png");
                cat2 = categoryService.save(cat2);

                Category cat3 = new Category();
                cat3.setCategoryName("Quần Áo");
                cat3.setIcon("fashion.png");
                cat3 = categoryService.save(cat3);

                Product p1 = new Product();
                p1.setProductName("Laptop Asus VivoBook");
                p1.setUnitPrice(12500000);
                p1.setQuantity(10);
                p1.setDescription("Laptop giá rẻ cho học sinh sinh viên");
                p1.setCategory(cat2);
                p1.setCreateDate(new Date());
                p1.setStatus((short) 1);
                productService.save(p1);

                Product p2 = new Product();
                p2.setProductName("iPhone 15 Pro Max");
                p2.setUnitPrice(32990000);
                p2.setQuantity(5);
                p2.setDescription("Flagship đỉnh cao 2024");
                p2.setCategory(cat1);
                p2.setCreateDate(new Date());
                p2.setStatus((short) 1);
                productService.save(p2);

                Product p3 = new Product();
                p3.setProductName("Áo thun Nam Unisex");
                p3.setUnitPrice(150000);
                p3.setQuantity(50);
                p3.setDescription("Áo thun cotton thoáng mát");
                p3.setCategory(cat3);
                p3.setCreateDate(new Date());
                p3.setStatus((short) 1);
                productService.save(p3);

                Product p4 = new Product();
                p4.setProductName("Tai nghe Bluetooth Hifi");
                p4.setUnitPrice(450000);
                p4.setQuantity(20);
                p4.setDescription("Tai nghe âm thanh cực hay");
                p4.setCategory(cat1);
                p4.setCreateDate(new Date());
                p4.setStatus((short) 1);
                productService.save(p4);
            }
        });
    }
}
