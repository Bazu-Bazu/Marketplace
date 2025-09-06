package marketplace.serviceproduct.service;

import lombok.RequiredArgsConstructor;
import marketplace.serviceproduct.repository.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

}
