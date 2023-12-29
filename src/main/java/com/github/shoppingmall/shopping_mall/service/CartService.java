package com.github.shoppingmall.shopping_mall.service;

import com.github.shoppingmall.shopping_mall.repository.Cart.Cart;
import com.github.shoppingmall.shopping_mall.repository.Cart.CartItem;
import com.github.shoppingmall.shopping_mall.repository.Cart.CartItemRepository;
import com.github.shoppingmall.shopping_mall.repository.Cart.CartRepository;
import com.github.shoppingmall.shopping_mall.repository.Item.Item;
import com.github.shoppingmall.shopping_mall.repository.Item.ItemOption;
import com.github.shoppingmall.shopping_mall.repository.Item.ItemOptionRepository;
import com.github.shoppingmall.shopping_mall.repository.Item.ItemRepository;
import com.github.shoppingmall.shopping_mall.repository.users.User;
import com.github.shoppingmall.shopping_mall.repository.users.UserRepository;
import com.github.shoppingmall.shopping_mall.web.dto.cart.CartDetailDto;
import com.github.shoppingmall.shopping_mall.web.dto.cart.CartItemDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemOptionRepository itemOptionRepository;

    public Integer addCart(CartItemDto cartItemDto, String email){
        Item item = itemRepository.findById(cartItemDto.getItemId()).orElseThrow(EntityNotFoundException::new); // 상품 조회
        User user = userRepository.findByEmail(email); // 회원 조회
        ItemOption itemOption = itemOptionRepository.findById(cartItemDto.getOptionId()).orElseThrow(EntityNotFoundException::new);


        Cart cart = cartRepository.findByUserUserId(user.getUserId()); // 장바구니 조회
        if(cart == null){ // 장바구니 처음 사용이면 장바구니 생성
            cart = Cart.createCart(user);
            cartRepository.save(cart);
        }

        CartItem savedCartItem = cartItemRepository.findByCartCartIdAndItemItemId(cart.getCartId(), item.getItemId()); // 현재 상품 장바구니에 있는지 조회

        if(savedCartItem != null){
            savedCartItem.addCount(cartItemDto.getCount()); // 이미 있으면 장바구니 수량 더하기
            return savedCartItem.getCartItemId();
        }else {
            CartItem cartItem = CartItem.createCartItem(cart, item, itemOption, cartItemDto.getCount()); // cartItem 엔티티 생성
            cartItemRepository.save(cartItem); // 장바구니에 들어갈 상품 저장
            return cartItem.getCartItemId();
        }
    }

    // TODO. 장바구니에서 주문 기능 구현 X, 상품 이미지 가져와서 장바구니 리스트 생성 X
//    @Transactional
//    public List<CartDetailDto> getCartList(String email) { // 장바구니에 들어있는 상품 조회
//        List<CartDetailDto>    cartDetailDtoList = new ArrayList<>();
//
//        User user = userRepository.findByEmail(email);
//
//        Cart cart = cartRepository.findByUserUserId(user.getUserId());
//
//        if (cart == null){
//            return cartDetailDtoList;
//        }
//
//        cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getCartId());
//
//        return cartDetailDtoList;
//    }

    @Transactional
    public boolean validateCartItem(Integer cartItemId, String email){
        User user = userRepository.findByEmail(email);
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);
        User savedUser = cartItem.getCart().getUser();

        if(!StringUtils.equals(user.getEmail(),savedUser.getEmail())){
            return false;
        }
        return true;
    }

    public void updateCartItemCount(Integer cartItemId, Integer count){
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);

        cartItem.updateCount(count);
    }

    public void deleteCartItem(Integer cartItemId){
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);
        cartItemRepository.delete(cartItem);
    }
}