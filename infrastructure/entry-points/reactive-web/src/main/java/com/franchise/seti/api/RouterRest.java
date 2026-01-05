package com.franchise.seti.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class RouterRest {
    @Bean
    RouterFunction<ServerResponse> routes(FranchiseHandler handler, ApiErrorFilter errorFilter) {
        return route(POST("/franchises").and(accept(MediaType.APPLICATION_JSON)), handler::createFranchise)
                //Branch
                .andRoute(POST("/franchises/{franchiseId}/branches").and(accept(MediaType.APPLICATION_JSON)), handler::addBranch)
                //Product
                .andRoute(POST("/branches/{branchId}/products").and(accept(MediaType.APPLICATION_JSON)), handler::addProduct)
                .andRoute(DELETE("/branches/{branchId}/products/{productId}"), handler::deleteProduct)
                .andRoute(PATCH("/products/{productId}/stock").and(accept(MediaType.APPLICATION_JSON)), handler::updateStock)
                //Query
                .andRoute(GET("/franchises/{franchiseId}/products/max-stock"), handler::maxStockByBranch)

                //Optional extra points (rename)
                /*.andRoute(PUT("/franchises/{franchiseId}").and(accept(MediaType.APPLICATION_JSON)), handler::renameFranchise)
                .andRoute(PUT("/branches/{branchId}").and(accept(MediaType.APPLICATION_JSON)), handler::renameBranch)
                .andRoute(PUT("/products/{productId}").and(accept(MediaType.APPLICATION_JSON)), handler::renameProduct)

                 */


                // Global exception mapping for functional endpoints:
                .filter(errorFilter);
    }
}
