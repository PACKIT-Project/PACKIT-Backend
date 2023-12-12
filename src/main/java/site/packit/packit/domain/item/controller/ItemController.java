package site.packit.packit.domain.item.controller;

import org.springframework.web.bind.annotation.*;
import site.packit.packit.domain.item.service.ItemService;

import java.util.List;

@RequestMapping("/api")
@RestController
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }


}
