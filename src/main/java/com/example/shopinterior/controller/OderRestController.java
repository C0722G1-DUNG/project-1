package com.example.shopinterior.controller;

import com.example.shopinterior.dto.oder.IOderDto;
import com.example.shopinterior.dto.oder.ITotalOrderValue;
import com.example.shopinterior.dto.oder.OderDto;
import com.example.shopinterior.dto.oder.PurchaseHistoryDto;
import com.example.shopinterior.entity.account.User;
import com.example.shopinterior.entity.cart.Cart;
import com.example.shopinterior.entity.oder.Oder;
import com.example.shopinterior.entity.oder.PurchaseHistory;
import com.example.shopinterior.entity.product.Product;
import com.example.shopinterior.service.account.IUserService;
import com.example.shopinterior.service.cart.ICartService;
import com.example.shopinterior.service.oder.IOderService;
import com.example.shopinterior.service.oder.IPurchaseHistoryService;
import com.example.shopinterior.service.product.IProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/oder")
public class OderRestController {
    @Autowired
    private ICartService iCartService;
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IOderService iOderService;
    @Autowired
    private IPurchaseHistoryService iPurchaseHistoryService;

    @GetMapping("")
    public ResponseEntity<Page<IOderDto>> getList(@PageableDefault(page = 0, size = 6) Pageable pageable) {
        Page<IOderDto> oderList = iOderService.showListOder(pageable);
        if (!oderList.isEmpty()) {
            return new ResponseEntity<>(oderList, HttpStatus.OK);
        }
        return new ResponseEntity<>(oderList, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("{id}")
    public ResponseEntity<Page<IOderDto>> getListDetail(@PageableDefault(page = 0, size = 6) Pageable pageable, @PathVariable int id) {
        Page<IOderDto> oderList = iOderService.showListOderDetailUser(id,pageable);
        if (!oderList.isEmpty()) {
            return new ResponseEntity<>(oderList, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("totalUser/{id}")
    public ResponseEntity<ITotalOrderValue> getListTotalOderUser(@PageableDefault(page = 0, size = 6) Pageable pageable, @PathVariable int id) {
        ITotalOrderValue iTotalOrderValue = iOderService.totalOderDetailUser(id);
            return new ResponseEntity<>(iTotalOrderValue, HttpStatus.OK);
    }

    @PostMapping("create")
    public ResponseEntity<Oder> create(@RequestBody OderDto oderDto) {
        Optional<User> user = iUserService.findById(oderDto.getUserDto().getId());
        if (user.isPresent()) {
            Oder oder = new Oder();
            BeanUtils.copyProperties(oderDto, oder);
            oder.setUser(user.get());
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = currentDateTime.format(formatter);
            oder.setOrderDate(formattedDateTime);
            oder = iOderService.save(oder);
            Set<PurchaseHistory> histories = new LinkedHashSet<>();
            for (PurchaseHistoryDto item : oderDto.getPurchaseHistorySet()) {
                Optional<Cart> cart = iCartService.findById(item.getIdCart());
                if (cart.isPresent()) {
                    Optional<Product> productOptional = iProductService.findById(cart.get().getProduct().getIdProduct());
                    Product product = new Product();
                    BeanUtils.copyProperties(productOptional.get(), product);
                    product.setQuantity(productOptional.get().getQuantity() - item.getQuantity());
                    iProductService.update(product);
                    PurchaseHistory purchaseHistory = new PurchaseHistory();
                    purchaseHistory.setQuantity(item.getQuantity());
                    purchaseHistory.setProduct(cart.get().getProduct());
                    purchaseHistory.setOder(oder);
                    histories.add(iPurchaseHistoryService.save(purchaseHistory));
                    iCartService.remove(cart.get());
                }
            }
            oder.setPurchaseHistorySet(histories);
            iOderService.save(oder);
            return new ResponseEntity<>(oder, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
