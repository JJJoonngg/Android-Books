package kr.co.jjjoonngg.dagger_sample_project

import kr.co.jjjoonngg.dagger_sample_project.mapmultibinding.DaggerMapComponent
import kr.co.jjjoonngg.dagger_sample_project.mapmultibinding.FooForMapMultibinding
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
}