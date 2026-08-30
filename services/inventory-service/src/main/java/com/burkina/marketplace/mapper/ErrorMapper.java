package com.burkina.marketplace.mapper;

import com.burkina.common.mapper.AbstractErrorMapper;
import com.burkina.marketplace.exception.AuthorizationException;
import com.burkina.marketplace.exception.SellerNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Component
public class ErrorMapper extends AbstractErrorMapper {

    @Override
    protected int getErrorCode(Throwable e) {
        if (e instanceof SellerNotFoundException) {
            return 404;
        } else if (e instanceof MethodArgumentNotValidException) {
            return 400;
        } else if (e instanceof AuthorizationException) {
            return 403;
        }

        return 500;
    }
}
