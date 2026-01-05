package com.franchise.seti.api.config;

import com.franchise.seti.api.FranchiseHandler;
import com.franchise.seti.usecase.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HandlerConfig {

    @Bean
    public FranchiseHandler franchiseHandler(
            FranchiseUseCase franchiseUseCase,
            BranchUseCase branchUseCase,
            ProductUseCase productUseCase,
            FranchiseQueryUseCase queryUseCase
    ) {
        return new FranchiseHandler(franchiseUseCase, branchUseCase, productUseCase, queryUseCase);
    }
}

