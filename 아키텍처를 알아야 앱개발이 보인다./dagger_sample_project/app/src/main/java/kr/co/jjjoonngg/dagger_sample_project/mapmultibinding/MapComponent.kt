package kr.co.jjjoonngg.dagger_sample_project.mapmultibinding

import dagger.Component

/*
* Created by JJJoonngg
*/

@Component(modules = [MapModule::class])
interface MapComponent {
    fun getLongByString(): Map<String, Long>
    fun getStringByClass(): Map<Class<*>, String>
}