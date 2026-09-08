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
            || e instanceof ReservationFailedException
        ) {
            return 409;
        } else if (e instanceof ProductNotFoundException) {
            return 404;
        } else if (e instanceof PaymentFailedException) {
            return 402;
        } else if (e instanceof CartServiceException
                || e instanceof InventoryServiceException
                || e instanceof OrderServiceException
                || e instanceof PaymentServiceException
                || e instanceof ProductServiceException
        ) {
            return 500;
        }

        return 500;
    }
}
