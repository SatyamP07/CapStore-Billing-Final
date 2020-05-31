package com.capstore.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capstore.app.models.CustomerDetails;
import com.capstore.app.models.User;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerDetails, Integer> {

}
