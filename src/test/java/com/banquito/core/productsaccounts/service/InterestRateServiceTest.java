package com.banquito.core.productsaccounts.service;

import com.banquito.core.productsaccounts.exception.CRUDException;
import com.banquito.core.productsaccounts.model.InterestRate;
import com.banquito.core.productsaccounts.repository.InterestRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class InterestRateServiceTest {

    @InjectMocks
    private InterestRateService interestRateService;

    @Mock
    private InterestRateRepository interestRateRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListAllActivesInterestRate() {
        // Given
        List<InterestRate> activeRates = new ArrayList<>();
        InterestRate rate1 = InterestRate.builder()
                .id(1)
                .name("Sample Rate")
                .interestRate(BigDecimal.valueOf(0.05))
                .state("ACT")
                .build();
        InterestRate rate2 = InterestRate.builder()
                .id(2)
                .name("Sample Rate")
                .interestRate(BigDecimal.valueOf(0.05))
                .state("ACT")
                .build();
        activeRates.add(rate1);
        activeRates.add(rate2);

        when(interestRateRepository.findByState("ACT")).thenReturn(activeRates);

        // When
        List<InterestRate> result = interestRateService.listAllActives();

        // Then
        assertEquals(2, result.size());
        verify(interestRateRepository, times(1)).findByState("ACT");
    }

    @Test
    void testObtainInterestRateById() {
        // Given
        Integer rateId = 1;
        InterestRate sampleRate = InterestRate.builder().id(rateId).build();

        when(interestRateRepository.findById(rateId)).thenReturn(Optional.of(sampleRate));

        // When
        InterestRate result = interestRateService.obtainById(rateId);

        // Then
        assertNotNull(result);
        assertEquals(rateId, result.getId());
    }

    @Test
    void testObtainByIdNonExistingRate() {
        // Given
        Integer rateId = 1;
        when(interestRateRepository.findById(rateId)).thenReturn(Optional.empty());
        // Then
        assertThrows(CRUDException.class, () -> interestRateService.obtainById(rateId));
    }

    @Test
    void testCreateInterestRate() {
        // Given
        InterestRate interestRate = InterestRate.builder()
                .name("Sample Rate")
                .interestRate(BigDecimal.valueOf(0.05))
                .build();
        // When
        interestRateService.create(interestRate);

        // Then
        verify(interestRateRepository, times(1)).save(interestRate);
    }

    @Test
    void testCreateInterestRateWithException() {
        // Given
        InterestRate interestRate = InterestRate.builder()
                .name("Sample Rate")
                .interestRate(BigDecimal.valueOf(0.05))
                .build();

        doThrow(new RuntimeException("Interest rate cannot be created"))
                .when(interestRateRepository).save(interestRate);
        // Then
        assertThrows(CRUDException.class, () -> interestRateService.create(interestRate));
    }

    @Test
    void testUpdateInterestRate() {
        // Given
        InterestRate existingRate = InterestRate.builder().id(1).build();
        InterestRate updatedRate = InterestRate.builder().id(1).name("Updated Rate").build();

        when(interestRateRepository.findById(1)).thenReturn(Optional.of(existingRate));

        // When
        interestRateService.update(1, updatedRate);

        // Then
        verify(interestRateRepository, times(1)).save(updatedRate);
    }

    @Test
    void testInactivateInterestRate() {
        // Given
        InterestRate existingRate = InterestRate.builder().id(1).build();
        when(interestRateRepository.findById(1)).thenReturn(Optional.of(existingRate));

        // When
        interestRateService.inactivate(1);

        // Then
        verify(interestRateRepository, times(1)).save(existingRate);
    }
}
