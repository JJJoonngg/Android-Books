package kr.co.jjjoonngg.dagger_sample_project.mapmultibinding.cutsomkey

import dagger.Component

/*
 * Created by JJJoonngg
 */

@Component(modules = [MapModuleForCustomKey::class])
interface MapKeyComponentForCustomKey {
    fun getStringByAnimal(): Map<Animal, String>
    fun getStringByNUmber(): Map<Class<out Number>, String>
}