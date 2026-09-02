package com.burkina.marketplace.mapper;

import com.burkina.common.mapper.AbstractErrorMapper;
import com.burkina.marketplace.exception.*;
import org.springframework.stereotype.Component;

@Component
public class ErrorMapper extends AbstractErrorMapper {

    @Override
    protected int getErrorCode(Throwable e) {
        if (e instanceof EmptyCartException
            || e instanceof ProductNotAvailableException
            || e instanceof ReserveProductsException
        ) {
            return 409;
        } else if (e instanceof CartNotFoundException
                || e instanceof ProductNotFoundException
        ) {
            return 404;
        } else if (e instanceof CartServiceUnavailableException
                || e instanceof InventoryServiceUnavailableException
                || e instanceof OrderServiceUnavailableException
                || e instanceof PaymentServiceUnavailableException
                || e instanceof ProductServiceUnavailableException
        ) {
            return 503;
        } else if (e instanceof PaymentException) {
            return 402;
        }

        return 500;
    }
}
