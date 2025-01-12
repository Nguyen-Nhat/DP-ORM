package org.group05.com;

import org.group05.com.entity.Employee;
import org.group05.com.entity.Salary;
import org.group05.com.entityManager.EntityManager;
import org.group05.com.entityManager.EntityManagerFactory;

import java.util.Date;
import java.util.List;

public class Demo {
//    private EntityManager entityManager;
//
//    Demo() {
//        EntityManagerFactory entityManagerFactory = new EntityManagerFactory();
//        this.entityManager = entityManagerFactory.createEntityManager();
//    }
//
//    public void testFind() {
//        Salary salary = entityManager.find(Salary.class, 2);
//        System.out.println(salary);
//    }
//
//    public void testFind2() {
//        List<Salary> salaries = entityManager.find(Salary.class, "salary",  1000);
//        System.out.println(salaries);
//    }
//
//    public void testDelete() {
////        Salary salary = new Salary(3);
////        int nRows = entityManager.delete(salary);
//
//        Employee employee = entityManager.find(Employee.class, 2);
//        int nRows = entityManager.delete(employee);
//        System.out.println("Deleted " + nRows + " rows");
//    }
//
//    public void testInsert1() {
//        Salary s1 = new Salary(1000, new Date(), new Date());
//        Salary s2 = new Salary(2000, new Date(), new Date());
//        List<Salary> salaries = List.of(s1, s2);
//
//        Employee employee = new Employee( "John", "Doe", new Date(), salaries);
//        Employee e = entityManager.insert(employee);
//
//        System.out.println(e);
//    }
//
//    public void testInsert2() {
//        Salary s = new Salary(3000, new Date(), new Date());
//        s.setEmployee(new Employee("Nhan", "Nguyen", new Date(), null));
//
//        Salary salary = entityManager.insert(s);
//        System.out.println(salary);
//    }
//
//    public void testUpdate() {
//        Salary salary = entityManager.find(Salary.class, 7);
//        if (salary == null) {
//            System.out.println("Salary not found");
//            return;
//        }
//        salary.setSalary(5000);
//        salary.setFromDate(new Date());
//        salary.setToDate(new Date());
//        salary.setEmployee(new Employee(5, "Nhan", "Nguyen", new Date(), null));
//
//        Salary s = entityManager.update(salary);
//        System.out.println(s);
//    }
}
