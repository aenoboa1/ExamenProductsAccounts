package com.banquito.core.productsaccounts.service;

import com.banquito.core.productsaccounts.controller.dto.ProductAccountRQRS;
import com.banquito.core.productsaccounts.controller.mapper.ProductAccountMapper;
import com.banquito.core.productsaccounts.exception.CRUDException;
import com.banquito.core.productsaccounts.model.ProductAccount;
import com.banquito.core.productsaccounts.repository.ProductAccountRepository;
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
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductAccountServiceTest {

    private static final Logger LOG = Logger.getLogger(ProductAccountServiceTest.class.getName());
    @Mock
    private ProductAccountRepository productAccountRepository;

    @InjectMocks
    private ProductAccountService productAccountService;

    @BeforeEach
    void setUp() {
        LOG.info("Starting Product Account service testCase");
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListAllActivesProductAccounts() {
        // Given
        List<ProductAccount> activeProductAccounts = new ArrayList<>();
        activeProductAccounts.add(ProductAccount.builder().id("1").build());
        activeProductAccounts.add(ProductAccount.builder().id("2").build());

        when(productAccountRepository.findByState("ACT")).thenReturn(activeProductAccounts);

        // When
        List<ProductAccount> result = productAccountService.listAllActives();

        // Then
        assertEquals(2, result.size());
        verify(productAccountRepository, times(1)).findByState("ACT");
    }

    @Test
    void testObtainByIdExistingProductAccount() throws CRUDException {
        // Given
        String accountId = "1";
        ProductAccount sampleAccount = ProductAccount.builder().id(accountId).build();

        when(productAccountRepository.findById(accountId)).thenReturn(Optional.of(sampleAccount));

        // When
        ProductAccount result = productAccountService.obtainById(accountId);

        // Then
        assertNotNull(result);
        assertEquals(accountId, result.getId());
    }

    @Test
    void testObtainByIdNonExistingAccountProductAccountThrowsException() {
        // Given
        String accountId = "nonExistentId";

        when(productAccountRepository.findById(accountId)).thenReturn(Optional.empty());

        // Then
        assertThrows(CRUDException.class, () -> productAccountService.obtainById(accountId));
    }

    @Test
    void testCreateProductAccount() throws CRUDException {
        // Given
        ProductAccountRQRS accountRQRS = ProductAccountRQRS.builder()
                .id("1")
                .name("Sample Account")
                .description("Sample description")
                .minimunBalance(BigDecimal.ZERO)
                .payInterest("Yes")
                .acceptsChecks("No")
                .state("ACT")
                .build();

        when(productAccountRepository.save(any())).thenReturn(new ProductAccount());
        // When
        productAccountService.create(ProductAccountMapper.mapToProductAccount(accountRQRS));

        // Then
        verify(productAccountRepository, times(1)).save(any());
    }
}
