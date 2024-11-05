package com.tbemerencio;

import com.tbemerencio.catalog.controllers.dtos.ProductDTO;
import com.tbemerencio.catalog.entities.Category;
import com.tbemerencio.catalog.entities.Product;

import java.time.Instant;

public class Factory {

    public static Product createProduct(){
        Product product = new Product(
                1L,
                "new product type",
                "new product description",
                800.0,
                "new product image",
                Instant.parse("2020-10-20T03:00:00Z"));
        product.getCategories().add(new Category(1L, "new category"));
        return product;
    }

    public static ProductDTO createProductDTO(){
        Product productWithCategory = createProduct();
        return new ProductDTO(productWithCategory, productWithCategory.getCategories());
    }
}
