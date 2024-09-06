package com.nishant.stream.repositories;

import com.nishant.stream.models.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerRepository extends JpaRepository<Owner,Long> {
}
