package kr.co.jjjoonngg.dagger_sample_project

import kr.co.jjjoonngg.dagger_sample_project.mapmultibinding.DaggerMapComponent
import kr.co.jjjoonngg.dagger_sample_project.mapmultibinding.FooForMapMultibinding
import kr.co.jjjoonngg.dagger_sample_project.mapmultibinding.cutsomkey.Animal
import kr.co.jjjoonngg.dagger_sample_project.mapmultibinding.cutsomkey.DaggerMapKeyComponentForCustomKey
import org.junit.Test

/*
* Created by JJJoonngg
*/

class MultibindingMapTest {
    @Test
    fun testMultibindingMap() {
        val component = DaggerMapComponent.create()
        val value = component.getLongByString()["foo"]
        val str = component.getStringByClass()[FooForMapMultibinding::class.java]

        println(value)
        println(str)
    }

    @Test
    fun testCustomKey() {
        val component = DaggerMapKeyComponentForCustomKey.create()

        println(component.getStringByAnimal()[Animal.CAT])
        println(component.getStringByAnimal()[Animal.DOG])
        println(component.getStringByNUmber()[Float::class.java])
        println(component.getStringByNUmber()[Int::class.java])
    }
}