package com.capstore.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capstore.app.models.MerchantDetails;

@Repository
public interface MerchantRepository extends JpaRepository<MerchantDetails, Integer> {

}
