package org.group05.com.entity;

import org.group05.com.annotations.*;

import java.util.Date;

@Entity(tableName = "salaries")
public class Salary {
    @Id
    @Column
    Integer id;

    @Column(name = "salary")
    int salary;

    @Column(name = "from_date")
    Date fromDate;

    @Column(name = "to_date")
    Date toDate;

    @ManyToOne(cascade = "ALL")
    @JoinColumn(name = "emp_no")
    Employee employee;

    public Salary(int id) {
        this.id = id;
    }

    public Salary() {
    }

    public Salary(Integer id, int salary, Date fromDate, Date toDate) {
        this.id = id;
        this.salary = salary;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public Salary(int salary, Date fromDate, Date toDate) {
        this.salary = salary;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    @Override
    public String toString() {
        String str = "Salary {" +
                ",\n salary=" + salary +
                ",\n fromDate=" + fromDate +
                ",\n toDate=" + toDate +
                "\n employee=" ;
        if (employee != null) {
            str += employee.getFirstName() + "  " + employee.getId() + " " +"}\n";
        } else {
            str += "null" + "}\n";
        }
        return str;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }
}
