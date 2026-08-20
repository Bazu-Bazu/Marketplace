package com.burkina.marketplace.mapper;

import com.burkina.common.mapper.AbstractErrorMapper;
import com.burkina.marketplace.exception.*;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Component
public class ErrorMapper extends AbstractErrorMapper {

    @Override
    protected int getErrorCode(Throwable e) {
        if (e instanceof UserAlreadySellerException) {
            return 409;
        } else if (e instanceof SellerNotFoundException) {
            return 404;
        } else if (e instanceof MethodArgumentNotValidException
                || e instanceof MaxPhoneLimitExceededException
                || e instanceof MaxEmailLimitExceededException
                || e instanceof MaxBankAccountLimitExceededException
        ) {
            return 400;
        }

        return 500;
    }
}
