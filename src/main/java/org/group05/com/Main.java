package org.group05.com;

import org.group05.com.entity.Employee;
import org.group05.com.entity.Salary;
import org.group05.com.entityManager.EntityManager;
import org.group05.com.entityManager.EntityManagerFactory;
import org.group05.com.logging.proxy.RepositoryLoggingProxy;
import org.group05.com.logging.strategy.impl.ConsoleLogging;
import org.group05.com.repository.EmployeeRepository;
import org.group05.com.repository.IRepository;
import org.group05.com.repository.SalaryRepository;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = new EntityManagerFactory();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        IRepository<?> repo = entityManager.createRepo(Salary.class);
        RepositoryLoggingProxy<?> repositoryLoggingProxy = new RepositoryLoggingProxy<>(repo, new ConsoleLogging());

        SalaryRepository repository = entityManager.createRepository(SalaryRepository.class,repositoryLoggingProxy);
        List<Salary>data = repository.findAll();
        System.out.println(data);
    }
}