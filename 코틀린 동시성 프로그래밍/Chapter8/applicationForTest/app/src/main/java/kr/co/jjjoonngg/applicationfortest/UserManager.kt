package kr.co.jjjoonngg.applicationfortest

/*
* Created by JJJoonngg
*/

class UserManager(private val dataSource: DataSource) {
    suspend fun getUser(id: Int): User {
        val name = dataSource.getNameAsync(id)
        val age = dataSource.getAgeAsync(id)
        val profession = dataSource.getProfessionAsync(id)

        return User(
            name.await(),
            age.await(),
            profession.await()
        )
    }
}