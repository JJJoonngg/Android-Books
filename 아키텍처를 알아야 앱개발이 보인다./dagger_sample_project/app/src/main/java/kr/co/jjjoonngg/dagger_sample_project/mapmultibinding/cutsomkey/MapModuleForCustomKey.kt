package kr.co.jjjoonngg.dagger_sample_project.mapmultibinding.cutsomkey

import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

/*
* Created by JJJoonngg
*/

enum class Animal {
    CAT,
    DOG
}

@MapKey
annotation class AnimalKey(val value: Animal)

@MapKey
annotation class NumberKey(val value: KClass<out Number>)

@Module
class MapModuleForCustomKey {

    @IntoMap
    @AnimalKey(Animal.CAT)
    @Provides
    fun provideCat() = "Meow"

    @IntoMap
    @AnimalKey(Animal.DOG)
    @Provides
    fun provideDog() = "Bow-wow"

    @IntoMap
    @NumberKey(Float::class)
    @Provides
    fun provideFloatValue() = "100f"


    @IntoMap
    @NumberKey(Int::class)
    @Provides
    fun provideIntegerValue() = "1"

}