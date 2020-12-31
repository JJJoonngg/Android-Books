package kr.co.jjjoonngg.dagger_sample_project.setmultibinding

import javax.inject.Inject

/*
* Created by JJJoonngg
*/

class Foo {

    @Inject
    lateinit var strings: Set<String>

    fun print() {
        for (itr in strings.iterator()) {
            println(itr)
        }
    }
}