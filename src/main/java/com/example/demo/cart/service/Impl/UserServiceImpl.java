
package com.example.demo.cart.service.Impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.cart.exception.AddException;
import com.example.demo.cart.exception.LoginException;
import com.example.demo.cart.exception.ProductNotFoundException;
import com.example.demo.cart.exception.UserNotFoundException;
import com.example.demo.cart.model.dto.FavoriteProductDTO;
import com.example.demo.cart.model.dto.FavoriteUserDTO;
import com.example.demo.cart.model.dto.LoginDTO;
import com.example.demo.cart.model.dto.RegisterDTO;
import com.example.demo.cart.model.dto.ResetPasswordDTO;
import com.example.demo.cart.model.dto.UpdateUserDTO;
import com.example.demo.cart.model.dto.UserDTO;
import com.example.demo.cart.model.entity.Product;
import com.example.demo.cart.model.entity.User;
import com.example.demo.cart.repository.ProductRepository;
import com.example.demo.cart.repository.UserRepository;
import com.example.demo.cart.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	// 🔐 密碼加鹽加密／驗證用
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public UserDTO register(RegisterDTO registerDTO) throws AddException {
	    // 1. 檢查帳號是否已存在
	    Optional<User> optUser = userRepository.findFirstByUsername(registerDTO.getUsername());
	    if (optUser.isPresent()) {
	        throw new AddException("帳號已被使用: " + registerDTO.getUsername());
	    }

	    // 2. 建立 User
	    User user = new User();
	    user.setUsername(registerDTO.getUsername());
	    user.setPassword(passwordEncoder.encode(registerDTO.getPassword())); // BCrypt
	    user.setEnabled(true);
	    user.setName(registerDTO.getName());
	    user.setIdNumber(registerDTO.getIdNumber());
	    user.setPhone(registerDTO.getPhone());
	    user.setBirthday(registerDTO.getBirthday());

	    // 預設角色 ROLE_USER
	    if (user.getRoles() == null) {
	        user.setRoles(new java.util.HashSet<>());
	    }
	    user.getRoles().add("ROLE_USER");

	    // 3. 儲存
	    try {
	        user = userRepository.save(user);
	    } catch (Exception e) {
	        throw new AddException("註冊失敗: " + e.getMessage());
	    }

	    // 4. 回傳 UserDTO（密碼遮蔽）
	    UserDTO userDTO = modelMapper.map(user, UserDTO.class);
	    userDTO.setPassword("******");
	    return userDTO;
	}
	
	@Override
	public UserDTO findByUsername(String username) throws UserNotFoundException {
		Optional<User> optUser = userRepository.findFirstByUsername(username);
		if(optUser.isEmpty()) {
			throw new UserNotFoundException("查無使用者:" + username);
		}
		// 得到 User 物件
		User user = optUser.get();
		// 將 User 轉 UserDTO
		UserDTO userDTO = modelMapper.map(user, UserDTO.class);
		return userDTO;
	}
	
	@Override
	public void resetPasswordWithVerify(ResetPasswordDTO dto)
	        throws UserNotFoundException, LoginException {

	    User user = userRepository.findFirstByUsername(dto.getUsername())
	            .orElseThrow(() -> new UserNotFoundException("查無使用者: " + dto.getUsername()));

	    // 比對身分證號與電話
	    if (user.getIdNumber() == null || !user.getIdNumber().equals(dto.getIdNumber())) {
	        throw new LoginException("身分證號不正確");
	    }

	    if (user.getPhone() == null || !user.getPhone().equals(dto.getPhone())) {
	        throw new LoginException("電話號碼不正確");
	    }

	    // 驗證通過 → 更新密碼
	    String encoded = passwordEncoder.encode(dto.getNewPassword());
	    user.setPassword(encoded);
	    userRepository.save(user);
	}
	
	@Override
	public UserDTO updateUser(UpdateUserDTO dto) throws UserNotFoundException, AddException {
	    User user = userRepository.findById(dto.getId())
	            .orElseThrow(() -> new UserNotFoundException("查無使用者 id: " + dto.getId()));

	    user.setName(dto.getName());
	    user.setPhone(dto.getPhone());
	    user.setBirthday(dto.getBirthday());

	    try {
	        user = userRepository.save(user);
	    } catch (Exception e) {
	        throw new AddException("更新使用者資料失敗: " + e.getMessage());
	    }

	    UserDTO userDTO = modelMapper.map(user, UserDTO.class);
	    userDTO.setPassword("******");
	    return userDTO;
	}
	
	@Override
	public void deleteUser(Long id) throws UserNotFoundException {
	    if (!userRepository.existsById(id)) {
	        throw new UserNotFoundException("查無使用者 id: " + id);
	    }
	    userRepository.deleteById(id);
	}


	@Override
	public UserDTO login(LoginDTO loginDTO) throws LoginException {
		// 1. 先查 username
		Optional<User> optUser = userRepository.findFirstByUsername(loginDTO.getUsername());
		if(optUser.isEmpty()) {
			//顯示「帳號或密碼錯誤」，避免暴力猜帳號
			throw new LoginException("帳號或密碼錯誤");
		}
		// 得到 User 物件
		User user = optUser.get();
		
		// 2. 用 PasswordEncoder 比對密碼（loginDTO 是使用者輸入的原始密碼）
		if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
					throw new LoginException("密碼錯誤");
				}
		
		// 將 User 轉 UserDTO
		UserDTO userDTO = modelMapper.map(user, UserDTO.class);
		return userDTO;
	}

	@Override
	public UserDTO saveUser(UserDTO userDTO) throws AddException{
		//UserDTO 轉user
		User user = modelMapper.map(userDTO, User.class);
		
		
		// 🔐 關鍵：這裡把原始密碼轉成 BCrypt 雜湊再存
				// 假設 userDTO.getPassword() 是使用者輸入的明碼
		if (userDTO.getPassword() != null && !userDTO.getPassword().isBlank()) {
				String encoded = passwordEncoder.encode(userDTO.getPassword());
				user.setPassword(encoded);
			}
				
		try {
			user= userRepository.save(user);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new AddException("新增user失敗"+e.getMessage()) ;
		}
		
		//user 轉UserDTO
		userDTO  =modelMapper.map(user, UserDTO.class);
		
		return userDTO ;
	}

	@Override
	public List<FavoriteProductDTO> getFavoriteProducts(Long userId) throws UserNotFoundException {
	    User user = userRepository.findById(userId)
	            .orElseThrow(() -> new UserNotFoundException("查無使用者id:" + userId));

	    return user.getFavoriteProducts()
	            .stream()
	            .map(product -> {
	                FavoriteProductDTO dto = new FavoriteProductDTO();
	                dto.setId(product.getId());
	                dto.setName(product.getName());
	                dto.setPrice(product.getPrice());

	                if (product.getProductImage() != null) {
	                    dto.setImageBase64(product.getProductImage().getImageBase64());
	                }

	                return dto;
	            })
	            .toList();
	}

	@Override
	public List<FavoriteUserDTO> getFavoriteUsers(Long productId) throws ProductNotFoundException {
		Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("查無商品id:" + productId));
		// 查詢該商品被那些用戶所關注
		Set<User> users = product.getFavoriteUsers();
		return users.stream()
					.map(user -> modelMapper.map(user, FavoriteUserDTO.class))
					.toList();
	}

	@Override
	public void addFavoriteProduct(Long userId, Long productId) throws UserNotFoundException,ProductNotFoundException {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("查無使用者id:" + userId));
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException("查無商品id:" + productId));
	
		//將商品加入用戶的關注清單
		user.getFavoriteProducts().add(product);
		
		//保存
		userRepository.save(user);
	}

	@Override
	public void removeFavoriteProduct(Long userId, Long productId) throws UserNotFoundException,ProductNotFoundException {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("查無使用者id:" + userId));
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException("查無商品id:" + productId));
		
		//將商品從用戶的關注清單中移除
		user.getFavoriteProducts().remove(product);
		//保存
		userRepository.save(user);
		
	}

}
