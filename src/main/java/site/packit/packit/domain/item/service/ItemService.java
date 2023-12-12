package site.packit.packit.domain.item.service;

import org.springframework.stereotype.Service;
import site.packit.packit.domain.category.repository.CategoryRepository;
import site.packit.packit.domain.item.repository.ItemRepository;
import site.packit.packit.domain.travel.repository.TravelRepository;

@Service
public class ItemService {
    private final CategoryRepository categoryRepository;
    private final TravelRepository travelRepository;
    private final ItemRepository itemRepository;

    public ItemService(CategoryRepository categoryRepository, TravelRepository travelRepository, ItemRepository itemRepository) {
        this.categoryRepository = categoryRepository;
        this.travelRepository = travelRepository;
        this.itemRepository = itemRepository;
    }


}
