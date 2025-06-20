package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.Admin;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
	Optional<Admin> findByName(String name);
}