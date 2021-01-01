package kr.co.jjjoonngg.dagger_sample_project.componentinheritance

import javax.inject.Inject
import javax.inject.Named
import kotlin.properties.Delegates

/*
* Created by JJJoonngg
*/

class FooForComponentInheritance {
    @Inject
    lateinit var str: String

    @set: [Inject Named("int")]
    var int: Int? = null
}