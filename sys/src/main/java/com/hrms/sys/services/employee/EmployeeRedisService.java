package com.hrms.sys.services.employee;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrms.sys.responses.EmployeeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class EmployeeRedisService implements IEmployeeRedisService{

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper redisObjectMapper;
    @Value("${spring.data.redis.use-redis-cache}")
    private boolean useRedisCache;

    private String getKeyFrom(String keyword,
                              Long departmentId,
                              PageRequest pageRequest) {
        int pageNumber = pageRequest.getPageNumber();
        int pageSize = pageRequest.getPageSize();
        Sort sort = pageRequest.getSort();
        String sortDirection = sort.getOrderFor("id")
                .getDirection() == Sort.Direction.ASC ? "asc" : "desc";
        String key = String.format("all_employees:%s:%d:%d:%d:%s",
                keyword, departmentId, pageNumber, pageSize, sortDirection);
        return key;
    }
    @Override
    public void clear() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Override
    public List<EmployeeResponse> getAllEmployees(String keyword, Long departmentId, PageRequest pageRequest) throws JsonProcessingException {
        if(useRedisCache == false) {
            return null;
        }
        String key = this.getKeyFrom(keyword, departmentId, pageRequest);
        String json;
        json = (String) redisTemplate.opsForValue().get(key);
        List<EmployeeResponse> employeeResponses =
                json != null ?
                        redisObjectMapper.readValue(json, new TypeReference<List<EmployeeResponse>>() {})
                        : null;
        return employeeResponses;
    }

    @Override
    public void saveAllEmployees(List<EmployeeResponse> employeeResponses, String keyword, Long departmentId, PageRequest pageRequest) throws JsonProcessingException {
        String key = this.getKeyFrom(keyword, departmentId, pageRequest);
        String json = redisObjectMapper.writeValueAsString(employeeResponses);
        redisTemplate.opsForValue().set(key, json);
    }
}
