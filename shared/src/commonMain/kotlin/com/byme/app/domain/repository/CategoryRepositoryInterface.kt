package com.byme.app.domain.repository

import com.byme.app.domain.model.Category


interface CategoryRepositoryInterface {
    suspend fun getCategories(): Result<List<Category>>
}