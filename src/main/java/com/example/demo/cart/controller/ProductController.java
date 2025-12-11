package com.example.demo.cart.controller;

import java.net.http.HttpRequest;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.cart.exception.AddException;
import com.example.demo.cart.exception.ProductNotFoundException;
import com.example.demo.cart.model.dto.ProductDTO;
import com.example.demo.cart.model.dto.ProductPageResponse;
import com.example.demo.cart.response.ApiResponse;
import com.example.demo.cart.service.ProductService;

/**
 * ProductController:
 * Request Mapping: "/products"
 * ---------------------------------------------------------------------------------------------------
 * GET  "", "/"   查詢所有商品(多筆) 	範例: "/products"   或 "/products/"
 * GET  "/{id}"   查詢指定商品(單筆) 	範例: "/products/1" 或 "/products/2"
 * POST "", "/"   新增商品        	範例: "/products"   或 "/products/"
 * PUT  "/{id}"		更新商品			範例: "/products/1" 或 "/products/2"
 * DELETE "/{id}"	刪除商品 		範例: "/products/1" 或 "/products/2"
 * */
@RestController
@RequestMapping(value = {"/products", "/product"})
@CrossOrigin(origins = {"http://localhost:5173","http://127.0.0.1:5173"}, allowCredentials = "true")
public class ProductController {
	
	@Autowired
	private ProductService productService;
	
	@GetMapping(value = {"", "/"})
	public ApiResponse<ProductPageResponse> getAllProducts(
			@RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "6") int size,
            @RequestParam(name = "keyword", required = false) String keyword) {
		ProductPageResponse resp= productService.getProducts(keyword, page, size);
		return new ApiResponse<>(200, "查詢成功", resp);
	}
	
	@GetMapping("/{id}")
	public ApiResponse<ProductDTO> getProduct(@PathVariable(name = "id") Long productId) {
		try {
			ProductDTO productDTO = productService.getProductById(productId);
			//return new ApiResponse<>(200, "查詢成功", productDTO);
			return new ApiResponse<>(HttpStatus.OK.value(), "查詢成功", productDTO);
		} catch (ProductNotFoundException e) {
			//return new ApiResponse<>(404, e.getMessage(), null);
			return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
		}
	}
	
	@PostMapping(value = {"", "/"})
	// 會自動將 client 上傳的 json 轉成 ProductDTO
	public ApiResponse<ProductDTO> addProduct(@RequestBody ProductDTO productDTO) {
		try {
			ProductDTO savedProductDTO = productService.saveProduct(productDTO);
			return new ApiResponse<>(HttpStatus.OK.value(), "新增成功", savedProductDTO);
		} catch (AddException e) {
			//return new ApiResponse<>(400, e.getMessage(), null);
			return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
		} catch (Exception e2) {
			return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "其他錯誤:" + e2.getMessage(), null);
		}	
	}
	
	@PutMapping("/{id}")
    public ApiResponse<ProductDTO> updateProduct(
            @PathVariable("id") Long id,
            @RequestBody ProductDTO productDTO) {
        try {
            ProductDTO updated = productService.updateProduct(id, productDTO);
            return new ApiResponse<>(HttpStatus.OK.value(), "更新成功", updated);
        } catch (ProductNotFoundException e) {
            return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (AddException e) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "其他錯誤: " + e.getMessage(), null);
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable("id") Long id) {
        try {
            productService.deleteProduct(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "刪除成功", null);
        } catch (ProductNotFoundException e) {
            return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "其他錯誤: " + e.getMessage(), null);
        }
    }
	
	
	
}