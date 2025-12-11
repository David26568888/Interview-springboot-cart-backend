package com.example.demo.cart.service.Impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.cart.exception.AddException;
import com.example.demo.cart.exception.ProductNotFoundException;
import com.example.demo.cart.model.dto.ProductDTO;
import com.example.demo.cart.model.dto.ProductPageResponse;
import com.example.demo.cart.model.entity.Product;
import com.example.demo.cart.model.entity.ProductImage;
import com.example.demo.cart.repository.ProductImageRepository;
import com.example.demo.cart.repository.ProductRepository;
import com.example.demo.cart.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductImageRepository productImageRepository;

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private ModelMapper modelMapper;

    ProductServiceImpl(ProductImageRepository productImageRepository) {
        this.productImageRepository = productImageRepository;
    }
	
	@Override
	public List<ProductDTO> getAllProducts() {
		return productRepository.findAll()//[Product]-> [Product] [Product] [Product] ....
								.stream() // [Product] [Product] [Product] ....
								.map(product-> modelMapper.map(product, ProductDTO.class))// [ProductDTO] [ProductDTO] [ProductDTO] ...
								.toList(); //[ProductDTO]->[ProductDTO]->[ProductDTO]......... 
	}

	@Override
	public ProductDTO getProductById(Long id) throws ProductNotFoundException {
		Product product = productRepository.findById(id)
										   .orElseThrow(() -> new ProductNotFoundException("查無商品"));
		ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
		return productDTO;
	}

	@Override
	public ProductDTO saveProduct(ProductDTO productDTO) throws AddException {
		// 1.建立 ProductImage
		ProductImage productImage = new ProductImage();
		productImage.setImageBase64(productDTO.getImageBase64());
		
		// 2.ProductDTO 轉 Product
		Product product = modelMapper.map(productDTO, Product.class);
		product.setProductImage(productImage); // 配置 ProductImage
		
		// 3.儲存 Product
		product = productRepository.save(product);
		
		// 4.Product 轉 ProductDTO
		productDTO = modelMapper.map(product, ProductDTO.class); 
				
		return productDTO;
	}

	@Override
	public ProductDTO updateProduct(Long id, ProductDTO productDTO)
	        throws ProductNotFoundException, AddException {

	    // 1. 先查出原本的商品
	    Product product = productRepository.findById(id)
	            .orElseThrow(() -> new ProductNotFoundException("查無商品 id: " + id));

	    // 2. 更新基本欄位
	    product.setName(productDTO.getName());
	    product.setPrice(productDTO.getPrice());

	    // 3. 更新圖片（如果有傳）
	    String imgBase64 = productDTO.getImageBase64();
	    if (imgBase64 != null && !imgBase64.isBlank()) {
	        ProductImage productImage = product.getProductImage();
	        if (productImage == null) {
	            productImage = new ProductImage();
	        }
	        productImage.setImageBase64(imgBase64);
	        product.setProductImage(productImage);
	    }
	    // 若前端沒帶 imageBase64，這裡選擇「保留原圖」
	    // 如果你希望「沒帶代表清空圖片」，可以改成：
	    // else { product.setProductImage(null); }

	    try {
	        // 4. 儲存更新後的商品
	        product = productRepository.save(product);
	    } catch (Exception e) {
	        throw new AddException("更新商品失敗: " + e.getMessage());
	    }

	    // 5. 轉回 DTO
	    return modelMapper.map(product, ProductDTO.class);
	}

	@Override
	public void deleteProduct(Long id) throws ProductNotFoundException {
	    Product product = productRepository.findById(id)
	            .orElseThrow(() -> new ProductNotFoundException("查無商品 id: " + id));

	    // 如果 Product 與 ProductImage 沒有 cascade，可以視需要手動刪圖片：
	    // ProductImage img = product.getProductImage();
	    // if (img != null) {
	    //     product.setProductImage(null);
	    //     productRepository.save(product);
	    //     productImageRepository.delete(img);
	    // }

	    productRepository.delete(product);
	}

	 @Override
	    public ProductPageResponse getProducts(String keyword, int page, int size) {

	        Pageable pageable = PageRequest.of(page, size);

	        Page<Product> productPage;

	        if (keyword == null || keyword.trim().isEmpty()) {
	            // 沒有搜尋字串 → 查全部（分頁）
	            productPage = productRepository.findAll(pageable);
	        } else {
	            // 有搜尋字串 → 依名稱模糊查詢
	            productPage = productRepository.findByNameContainingIgnoreCase(keyword.trim(), pageable);
	        }

	        // Page<Product> -> List<ProductDTO>
	        List<ProductDTO> productDTOs = productPage.getContent()
	                .stream()
	                .map(p -> modelMapper.map(p, ProductDTO.class))
	                .toList();

	        // 組裝回應
	        ProductPageResponse resp = new ProductPageResponse();
	        resp.setProducts(productDTOs);
	        resp.setPage(productPage.getNumber());
	        resp.setSize(productPage.getSize());
	        resp.setTotalElements(productPage.getTotalElements());
	        resp.setTotalPages(productPage.getTotalPages());
	        resp.setLast(productPage.isLast());

	        return resp;
	    }
	 	
	 
}
