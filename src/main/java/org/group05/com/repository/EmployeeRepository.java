package org.group05.com.repository;

import org.group05.com.annotations.Query;
import org.group05.com.entity.Employee;

import java.util.List;

public interface EmployeeRepository extends IRepository<Employee>{
    @Query("SELECT YEAR(hire_date) FROM employees GROUP BY YEAR(hire_date) HAVING COUNT(*) >= 2")
    List<Object[]> groupBy();
    @Query("SELECT * FROM employees WHERE first_name = ?")
    List<Employee> findByFirstName(String firstName);
}
