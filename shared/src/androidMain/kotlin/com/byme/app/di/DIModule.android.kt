package com.byme.app.di

import com.byme.app.db.ByMeDatabase
import com.byme.app.db.DatabaseDriverFactory
import org.koin.dsl.module

actual val platformModule = module {
    single {
        // Usamos el androidContext() de Koin para el DriverFactory
        DatabaseDriverFactory(get()).createDriver()
    }
    single { ByMeDatabase(get()) }
}