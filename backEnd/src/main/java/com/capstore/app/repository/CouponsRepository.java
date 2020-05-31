package com.capstore.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.capstore.app.models.Coupon;

public interface CouponsRepository extends JpaRepository<Coupon, Integer>{

	public Optional<Coupon> findByCouponCode(String couponCode);
}
