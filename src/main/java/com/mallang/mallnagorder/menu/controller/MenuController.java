package com.mallang.mallnagorder.menu.controller;

import com.mallang.mallnagorder.menu.dto.MenuRequest;
import com.mallang.mallnagorder.menu.dto.MenuResponse;
import com.mallang.mallnagorder.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;


    // 메뉴 삭제
    @DeleteMapping("/menus/{menuId}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long menuId) {
        menuService.deleteMenu(menuId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // 메뉴 ID로 단건 조회
    @GetMapping("/menus/{menuId}")
    public ResponseEntity<MenuResponse> getMenuById(@PathVariable Long menuId) {
        MenuResponse response = menuService.getMenuById(menuId);
        return ResponseEntity.ok(response);
    }

    // 메뉴 수정
    @PutMapping("/menus/{menuId}")
    public ResponseEntity<MenuResponse> updateMenu(
            @PathVariable Long menuId,
            @RequestBody MenuRequest request
    ) {
        MenuResponse response = menuService.updateMenu(menuId, request);
        return ResponseEntity.ok(response);
    }
}
