package com.irsyad.springmysqlredis.service.impl;

import com.irsyad.springmysqlredis.exception.ResourceNotFoundException;
import com.irsyad.springmysqlredis.model.Employee;
import com.irsyad.springmysqlredis.repository.EmployeeRepository;
import com.irsyad.springmysqlredis.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final RedisTemplate<String, Employee> redisTemplate;

    @Override
    public Employee findEmployeeById(Long id) {
        var key = "emp_" + id;
        final ValueOperations<String, Employee> operations = redisTemplate.opsForValue();
        final boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            final Employee post = operations.get(key);
            log.info("EmployeeServiceImpl.findEmployeeById() : cache post >> " + post.toString());
            return post;
        }
        final Optional<Employee> employee = employeeRepository.findById(id);
        if(employee.isPresent()) {
            operations.set(key, employee.get(), 10, TimeUnit.SECONDS);
            log.info("EmployeeServiceImpl.findEmployeeById() : cache insert >> " + employee.get().toString());
            return employee.get();
        } else {
            throw new ResourceNotFoundException();
        }
    }

    @Override
    public Page<Employee> getAllEmployees(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(
                Sort.Order.desc("id")));
        return employeeRepository.findAll(pageable);
    }

    @Override
    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    public Employee updateEmployee(Employee employee) {
        final String key = "emp_" + employee.getId();
        final boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            redisTemplate.delete(key);
            log.info("EmployeeServiceImpl.updateEmployee() : cache delete >> " + employee.toString());
        }
        return employeeRepository.save(employee);
    }

    @Override
    public void deleteEmployee(Long id) {
        final String key = "emp_" + id;
        final boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            redisTemplate.delete(key);
            log.info("EmployeeServiceImpl.deletePost() : cache delete ID >> " + id);
        }
        final Optional<Employee> employee = employeeRepository.findById(id);
        if(employee.isPresent()) {
            employeeRepository.delete(employee.get());
        } else {
            throw new ResourceNotFoundException();
        }
    }
}
