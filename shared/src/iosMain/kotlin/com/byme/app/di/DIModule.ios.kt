package com.byme.app.di

import com.byme.app.db.ByMeDatabase
import com.byme.app.db.DatabaseDriverFactory
import org.koin.dsl.module

actual val platformModule = module {
    single { DatabaseDriverFactory().createDriver() }
    single { ByMeDatabase(get()) }
}