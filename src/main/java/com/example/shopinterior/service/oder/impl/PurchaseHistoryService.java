package com.example.shopinterior.service.oder.impl;

import com.example.shopinterior.dto.oder.IPurchaseHistoryDto;
import com.example.shopinterior.dto.oder.SellingProducts;
import com.example.shopinterior.entity.oder.PurchaseHistory;
import com.example.shopinterior.repository.oder.IPurchaseHistoryRepository;
import com.example.shopinterior.service.oder.IPurchaseHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PurchaseHistoryService implements IPurchaseHistoryService {
    @Autowired
    private IPurchaseHistoryRepository iPurchaseHistoryRepository;
    @Override
    public PurchaseHistory save(PurchaseHistory purchaseHistory) {
        return iPurchaseHistoryRepository.save(purchaseHistory);
    }

    @Override
    public List<IPurchaseHistoryDto> showListPurchaseHistory(int idOder) {
        return iPurchaseHistoryRepository.showListPurchaseHistory(idOder);
    }

    @Override
    public Page<SellingProducts> showListSellingProducts(Pageable pageable) {
        return iPurchaseHistoryRepository.showListSellingProducts(pageable);
    }
}
