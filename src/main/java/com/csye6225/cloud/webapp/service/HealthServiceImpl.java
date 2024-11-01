package com.csye6225.cloud.webapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;

@Service
public class HealthServiceImpl implements HealthService {

    DataSource dataSource;

    @Autowired
    public HealthServiceImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    Logger logger = LoggerFactory.getLogger(HealthServiceImpl.class);
    @Override
    public boolean isDatabaseConnected() {
        try(Connection conn = dataSource.getConnection()) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
