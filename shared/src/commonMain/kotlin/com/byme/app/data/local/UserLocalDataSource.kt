package com.byme.app.data.local

import com.byme.app.db.ByMeDatabase
import com.byme.app.domain.model.User
import com.byme.app.db.UserEntity

class UserLocalDataSource(database: ByMeDatabase) {
    private val queries = database.userEntityQueries

    fun insertUser(user: User) {
        // SQLDelight genera una clase UserEntity que coincide con tu tabla
        queries.insertUser(
            UserEntity(
                id = user.id,
                name = user.name,
                lastname = user.lastname,
                email = user.email,
                phone = user.phone,
                photoUrl = user.photoUrl,
                isProfessional = user.isProfessional,
                category = user.category,
                description = user.description
            )
        )
    }

    fun getUserById(id: String): User? {
        return queries.getUserById(id).executeAsOneOrNull()?.let {
            User(
                id = it.id,
                name = it.name,
                lastname = it.lastname,
                email = it.email,
                phone = it.phone,
                photoUrl = it.photoUrl,
                isProfessional = it.isProfessional,
                category = it.category,
                description = it.description
            )
        }
    }

    // Vital para cumplir con el flujo de la app - Cerrar sesión
    fun clearData() {
        queries.deleteAll()
    }
}