package com.byme.app.di

import androidx.lifecycle.get
import com.byme.app.data.local.DraftManager
import com.byme.app.data.local.UserLocalDataSource
import com.byme.app.data.remote.repository.*
import com.byme.app.domain.repository.*
import com.byme.app.domain.usecase.*
import com.byme.app.viewmodel.HomeScreenModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import org.koin.core.module.Module
import org.koin.dsl.module

// 1. Módulo de Datos (Firebase, Base de Datos, Repositorios)
val dataModule = module {
    // Firebase
    single { Firebase.auth }
    single { Firebase.firestore }

    // Local
    single { DraftManager() }
    // UserLocalDataSource necesita la base de datos que proveeremos por plataforma
    single { UserLocalDataSource(get()) }

    // Repositorios
    single<UserRepositoryInterface> { UserRepositoryImpl(get(), get()) }
    single<CategoryRepositoryInterface> { CategoryRepositoryImpl(get()) }
    single<ServiceRepositoryInterface> { ServiceRepositoryImpl(get()) }
    single<ScheduleRepositoryInterface> { ScheduleRepositoryImpl(get()) }
    single<ChatRepositoryInterface> { ChatRepositoryImpl(get()) }
    single<AppointmentRepositoryInterface> { AppointmentRepositoryImpl(get()) }
    single<ReviewRepositoryInterface> { ReviewRepositoryImpl(get()) }
}

// 2. Módulo de Dominio (Use Cases)
val domainModule = module {
    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get(), get()) }
    factory { GetUserUseCase(get()) }
    factory { UpdateUserUseCase(get()) }
    factory { GetProfessionalsUseCase(get()) }
    factory { SearchProfessionalsUseCase(get()) }
    factory { GetChatsUseCase(get()) }
    factory { GetMessagesUseCase(get()) }
    factory { SendMessageUseCase(get()) }
    factory { AddServiceUseCase(get()) }
    factory { GetServicesUseCase(get()) }
    factory { AddScheduleUseCase(get()) }
    factory { GetSchedulesUseCase(get()) }
    factory { AddReviewUseCase(get()) }
    factory { GetReviewsUseCase(get()) }
    factory { CreateAppointmentUseCase(get()) }
}

val screenModelModule = module {
    factory { HomeScreenModel(get(), get()) }
}

// 3. Declaramos un expect para el módulo de plataforma (Base de datos)
expect val platformModule: Module