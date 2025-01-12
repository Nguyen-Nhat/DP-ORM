package org.group05.com.repository;

import org.group05.com.annotations.Query;
import org.group05.com.entity.Salary;

import java.util.List;

public interface SalaryRepository extends IRepository<Salary>{
    @Query("SELECT * FROM salaries")
    List<Salary> findAll();
}
