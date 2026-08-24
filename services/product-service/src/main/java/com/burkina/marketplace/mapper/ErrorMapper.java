package com.burkina.marketplace.mapper;

import com.burkina.common.mapper.AbstractErrorMapper;
import com.burkina.marketplace.exception.*;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Component
public class ErrorMapper extends AbstractErrorMapper {

    @Override
    protected int getErrorCode(Throwable e) {
        if (e instanceof CategoryNotFoundException ||
            e instanceof ProductCategoryNotFoundException ||
            e instanceof ProductMediaNotFoundException ||
            e instanceof ProductNotFoundException ||
            e instanceof SellerNotFoundException
        ) {
            return 404;
        } else if (e instanceof MethodArgumentNotValidException ||
                   e instanceof IllegalMediaPositionException
        ) {
            return 400;
        } else if (e instanceof AuthorizationException) {
            return 403;
        } else if (e instanceof CategoryNotActiveException ||
                   e instanceof ProductCanNotBePublished ||
                   e instanceof ProductCategoryAlreadyExistsException ||
                   e instanceof ProductCategoryLimitExceededException ||
                   e instanceof ProductMediaLimitExceededException ||
                   e instanceof ProductMediaAlreadyExistsException
        ) {
            return 409;
        }

        return 500;
    }
}
