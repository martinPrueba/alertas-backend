package com.kim21.alertas.repository;

import com.kim21.alertas.model.ProcessAssociateIconModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProcessAssociateIconRepository extends JpaRepository<ProcessAssociateIconModel, Integer> 
{
    Optional<ProcessAssociateIconModel> findByProceso(String proceso);
}