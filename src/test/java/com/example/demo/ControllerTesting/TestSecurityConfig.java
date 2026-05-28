package com.example.demo.ControllerTesting;

import com.example.demo.Security.SecurityUtil;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    @Primary
    public SecurityUtil testSecurityUtil() {
        SecurityUtil mockSecurityUtil = mock(SecurityUtil.class);

        // Make all security checks return true by default
        when(mockSecurityUtil.isCurrAdmin(anyLong())).thenReturn(true);
        when(mockSecurityUtil.isCurrTeacher(anyLong())).thenReturn(true);
        when(mockSecurityUtil.isCurrStudent(anyLong())).thenReturn(true);
        when(mockSecurityUtil.isCurrLibrarian(anyLong())).thenReturn(true);
        when(mockSecurityUtil.isCurrAccountant(anyLong())).thenReturn(true);
        when(mockSecurityUtil.isCurrParent(anyLong())).thenReturn(true);

        return mockSecurityUtil;
    }
}