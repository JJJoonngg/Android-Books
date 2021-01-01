package kr.co.jjjoonngg.dagger_sample_project.subcomponentexample

import javax.inject.Inject

/*
* Created by JJJoonngg
*/

class CoffeeBean {}

class Water {}

class Coffee @Inject constructor(coffeeBean: CoffeeBean, water: Water) {}