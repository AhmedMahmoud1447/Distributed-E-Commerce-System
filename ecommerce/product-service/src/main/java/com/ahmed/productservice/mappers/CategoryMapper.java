package com.ahmed.productservice.mappers;

import com.ahmed.productservice.domain.Category;
import com.ahmed.productservice.dtos.CategoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {

    CategoryDto.Response toResponse(Category category);

    Category toEntity(CategoryDto.Request request);
}