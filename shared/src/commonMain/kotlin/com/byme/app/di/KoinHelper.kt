package com.byme.app.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(dataModule, domainModule, platformModule, screenModelModule)
}

// Esta función es la que llamará el iPhone desde Swift
fun initKoin() = initKoin {}