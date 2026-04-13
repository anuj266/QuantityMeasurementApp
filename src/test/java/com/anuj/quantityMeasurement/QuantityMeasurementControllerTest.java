package com.anuj.quantityMeasurement;

import com.anuj.quantityMeasurement.model.*;
import com.anuj.quantityMeasurement.Controller.QuantityMeasurementController;
import com.anuj.quantityMeasurement.security.JwtAuthenticationFilter;
import com.anuj.quantityMeasurement.service.IQuantityMeasurementService;
import com.anuj.quantityMeasurement.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(QuantityMeasurementController.class)
class QuantityMeasurementControllerTest {

    @Autowired private MockMvc       mockMvc;
    @Autowired private ObjectMapper  objectMapper;

    @MockBean private IQuantityMeasurementService service;

    @MockBean private JwtService          jwtService;
    @MockBean private UserDetailsService  userDetailsService;

    private QuantityInputDTO input(double v1, String u1, String t1,
                                   double v2, String u2, String t2) {
        return new QuantityInputDTO(
            new QuantityDTO(v1, u1, t1),
            new QuantityDTO(v2, u2, t2));
    }

    @Test
    @WithMockUser
    void testCompare_Returns200_WithResultTrue() throws Exception {
        QuantityMeasurementDTO mockResult = new QuantityMeasurementDTO();
        mockResult.setResultString("true");
        mockResult.setOperation("compare");
        mockResult.setError(false);

        Mockito.when(service.compareQuantities(Mockito.any(), Mockito.any()))
               .thenReturn(mockResult);

        mockMvc.perform(
                post("/api/v1/quantities/compare")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf())
                    .content(objectMapper.writeValueAsString(
                        input(1.0, "FEET", "LengthUnit",
                              12.0, "INCHES", "LengthUnit"))))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.resultString").value("true"))
               .andExpect(jsonPath("$.operation").value("compare"))
               .andExpect(jsonPath("$.error").value(false));
    }

    @Test
    @WithMockUser
    void testAdd_Returns200_WithResultValue() throws Exception {
        QuantityMeasurementDTO mockResult = new QuantityMeasurementDTO();
        mockResult.setResultValue(2.0);
        mockResult.setResultUnit("FEET");
        mockResult.setOperation("add");

        Mockito.when(service.addQuantities(Mockito.any(), Mockito.any()))
               .thenReturn(mockResult);

        mockMvc.perform(
                post("/api/v1/quantities/add")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf())
                    .content(objectMapper.writeValueAsString(
                        input(1.0, "FEET", "LengthUnit",
                              12.0, "INCHES", "LengthUnit"))))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.resultValue").value(2.0))
               .andExpect(jsonPath("$.resultUnit").value("FEET"));
    }

    @Test
    @WithMockUser
    void testCompare_InvalidInput_NullValue_Returns400() throws Exception {
        QuantityInputDTO badInput = new QuantityInputDTO(
            new QuantityDTO(null, "FEET", "LengthUnit"),
            new QuantityDTO(12.0, "INCHES", "LengthUnit")
        );

        mockMvc.perform(
                post("/api/v1/quantities/compare")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf())
                    .content(objectMapper.writeValueAsString(badInput)))
               .andExpect(status().isBadRequest());

        Mockito.verify(service, Mockito.never())
               .compareQuantities(Mockito.any(), Mockito.any());
    }

    @Test
    @WithMockUser
    void testConvert_Returns200() throws Exception {
        QuantityMeasurementDTO mockResult = new QuantityMeasurementDTO();
        mockResult.setResultValue(12.0);
        mockResult.setOperation("convert");

        Mockito.when(service.convertQuantity(Mockito.any(), Mockito.any()))
               .thenReturn(mockResult);

        mockMvc.perform(
                post("/api/v1/quantities/convert")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf())
                    .content(objectMapper.writeValueAsString(
                        input(1.0, "FEET", "LengthUnit",
                              0.0, "INCHES", "LengthUnit"))))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.resultValue").value(12.0));
    }

    @Test
    @WithMockUser
    void testDivide_ServiceThrowsArithmetic_Returns500() throws Exception {
        Mockito.when(service.divideQuantities(Mockito.any(), Mockito.any()))
               .thenThrow(new ArithmeticException("Divide by zero"));

        mockMvc.perform(
                post("/api/v1/quantities/divide")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf())
                    .content(objectMapper.writeValueAsString(
                        input(1.0, "FEET", "LengthUnit",
                              0.0, "INCHES", "LengthUnit"))))
               .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser
    void testGetHistoryByOperation_Returns200() throws Exception {
        Mockito.when(service.getHistoryByOperation("ADD"))
               .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/quantities/history/operation/ADD"))
               .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testGetCount_Returns200_WithCount() throws Exception {
        Mockito.when(service.getOperationCount("COMPARE")).thenReturn(3L);

        mockMvc.perform(get("/api/v1/quantities/count/COMPARE"))
               .andExpect(status().isOk())
               .andExpect(content().string("3"));
    }

    @Test
    @WithMockUser
    void testGetErrorHistory_Returns200() throws Exception {
        Mockito.when(service.getErrorHistory()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/quantities/history/errored"))
               .andExpect(status().isOk());
    }
}