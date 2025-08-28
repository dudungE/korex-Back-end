package com.project.korex.account.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReorderFavoriteRequestDto {
    @NotEmpty(message = "즐겨찾기 ID 목록이 비어있습니다")
    @Size(max = 4, message = "즐겨찾기는 최대 4개까지만 가능합니다")
    private List<Long> favoriteIds;
}
