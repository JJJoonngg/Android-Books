package kr.co.jjjoonngg.dagger_sample_project

import kr.co.jjjoonngg.dagger_sample_project.subcomponentexample.Cafe
import org.junit.Test

/*
* Created by JJJoonngg
*/

class CoffeeUnitTest {
    @Test
    fun testCafe(){
        val cafe = Cafe()
        println(cafe.orderCoffee())
        println(cafe.orderCoffee())
        println(cafe.orderCoffee())
    }
}
/*
결과
kr.co.jjjoonngg.dagger_sample_project.subcomponentexample.Coffee@1175e2db
kr.co.jjjoonngg.dagger_sample_project.subcomponentexample.Coffee@36aa7bc2
kr.co.jjjoonngg.dagger_sample_project.subcomponentexample.Coffee@76ccd017
 */