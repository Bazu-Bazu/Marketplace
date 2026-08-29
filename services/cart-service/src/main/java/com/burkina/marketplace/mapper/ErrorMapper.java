package com.burkina.marketplace.mapper;

import com.burkina.common.mapper.AbstractErrorMapper;
import com.burkina.marketplace.exception.*;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Component
public class ErrorMapper extends AbstractErrorMapper {

    @Override
    protected int getErrorCode(Throwable e) {
        if (e instanceof CartItemNotFoundException ||
            e instanceof ProductNotFoundException
        ) {
            return 404;
        } else if (e instanceof MethodArgumentNotValidException) {
            return 400;
        } else if (e instanceof CartItemLimitExceededException ||
                   e instanceof ProductNotAvailableException
        ) {
            return 409;
        } else if (e instanceof ProductServiceUnavailableException) {
            return 503;
        }

        return 500;
    }
}
