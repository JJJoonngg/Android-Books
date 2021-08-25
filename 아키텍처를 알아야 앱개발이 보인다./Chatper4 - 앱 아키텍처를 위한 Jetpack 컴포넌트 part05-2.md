### **DAO를 사용한 데이터 접근**

DAO (Date Access Object)

Room 라이브러리를 사용하여 앱 데이터 접근시 DAO를 이용

각 DAO 에는 앱 데이터베이스에 대한 접근을 제공하는 추상 메서드를 포함해야함

<br>

DAO 는 인터페이스 또는 추상클래스로 만든다.

<br>

### **삽입, 수정, 삭제 메서드 정의하기**

DAO 클래스에서는 애노테이션을 사용한 메서드 정의로 편리하게 구현 가능하다.

<br>

#### **삽입하기**

`@Insert` 애노테이션을 추가한 DAO 메서드 작성을 통해, Room은 모든 매개 변수를 단일 트랜잭션으로 데이터베이스에 삽입하는 구현체를 컴파일 타임에 생성한다.

만약 `@Insert`  메서드가 하나의 매개 변수를 받는다면, 메서드는 상빙되는 아이템의 long 형 id 를 반환할 수 있다.

```kotlin
@Dao
interface MyDao{
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertUsers(users ... : User)
  
  @Insert
  fun insertBothUsers(user1 : User, user2 : User)
  
  @Insert
  fun insertUsersAndFriends(user:User, friends:List<User>)
}
```

<br>

#### **수정하기**

`@Update` 애노테이션을 추가한 DAO 메서드는 어진 매개 변수로부터 데이터베이스의 엔티티들을 수정할 수 있다.

```kotlin
@Dao
interface MyDao{
  @Update
  fun updateUser(user:User)
}
```

<br>

#### **삭제하기**

`@Delete` 애노테이션을 추가한 DAO 메서드는 주어진 매개 변수로부터 데이터베이스 내의 엔터티들을 삭제한다.

```kotlin
@Dao
interface MyDao{
	@Delete
  fun deleteUser(user:User)
}
```

<br>

<br>

---

### **쿼리하기**

쿼리 응답의 해당 칼럼 이름과 일치 하지 않을 때 다음과 같은 경고를 한다.

- 필드 이름이 일치하지 않는 경우 경고 출력
- 필드 이름이 전부 일치하지 않는 경우 에러 출력

<br>

#### **간단한 쿼리**

```kotlin
@Dao
interface MyDao{
  @Query("SELECT * FROM user")
  fun loadAllUsers() : ArrayList<User>
}
```

<br>

#### **쿼리에 매개 변수 전달하기**

```kotlin
@Dao
interface MyDao{
  @Query("SELECT * FROM user WHERE age>:minAge")
  fun loadAllUsersOlderThan(minAge:Int)
}
```

Room은 각각의 매개 변수명이 일치하는 경우 컴파일 타임 처리될 때 바인딩 하여 처리

일치되지 않는 이름에 대해서는 컴파일 타임에 오류가 발생

<br>

#### **테이블 내 칼럼의 일부만 반환하기**

대부분의 경우, 엔터티의 일부 필드만 가져온다. Room 을 사용시 칼럼의 일부만 매핑하는 객체를 반환하는 쿼리 메서드를 만들 수 있다.

> firstName 과 lastName만 가져오는 쿼리

```kotlin
data class NameTuple{
  @ColumnInfo(name="first_name")
  val firstName:String
  
  @ColumnInfo(name="last_name")
  @NonNull
  val lastName:String
}


@Dao
interface MyDao{
  @Query("SELECT first_name, lastName FROM user")
  fun loadFullName() : List<NameTuple>
}

```

<br>

#### **컬렉션을 매개변수로 전달하기**

일부 쿼리는 런 타임 전까지 정확한 수의 매개 변수를 알 수 없다.

Room은 매개 변수로 컬렉션이 사용되는 경우 이를 이해하고 제공된 매개변수 수에 따라 런타임에 자동으로 확장한다.

```kotlin
@Dao
interface MyDao{
  @Query("SELECT first_name, last_name FROM user WHERE region IN (:regions)")
  fun loadUsers(regions:List<String>):List<NameTuple>
}
```

<br>

#### **Observable 쿼리하기**

Room은 컴파일 타임에 데이터베이스가 변경될 때 LiveData를 갱신하는데 필요한 모든 코드를 생선한다.

```kotlin
@Dao
interface MyDao{
  @Query("SELECT * FROM user WHERE region IN (:regions)")
  fun loadUsers(regions:List<String>):LiveData<List<User>>
}
```

<br>

#### **커서로 직접 접근하기**

반환되는 행에 대해 직접적인 접근을 위해 반환되는 타입을 Cursor 객체로 만들 수 있다.

```kotlin
@Dao
interface MyDao{
  @Query("SELECT * FROM user WHERE age > :minAge LIMIT 5")
  val loadRawUsersOlderThan(minAge:Int):Cursor
}
```

<br>

#### **여러 테이블 쿼리하기**

Room을 사용시 어떤 쿼리든 작성할 수 있으므로 테이블을 조인할 수도 있다.

Flowable 또는 LiveData처럼 Observable 타입인 경우 Room은 쿼리에서 참조된 모든 테이블을 주시한다.

```kotlin
@Dao
interface MyDao{
  @Query("SELECT * FROM book + 
         "INNER JOIN loan ON loan.book_id = book.id" +
         "INNER JOIN user ON user.id = loan.user_id" +
         "WHERE user.name LIKE :userName")
  fun findBooksBorrowedByNameSync(userName:String):List<Book>
}
```

<br>

#### **코루틴과 비동기 메서드 작성하기**

suspend 키워드를 통해 비동기 식으로 만들 수 있음 (메인 스레드에서 실행은 불가)

```kotlin
@Dao
interface MyDao{
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUsers(vararg users:User)
  
  @Update
  suspend fun updateUsers(vararg users:User)
  
  @Delete
  suspend fun deleteUsers(vararg users:User)
  
  @Query("SELECT * FROM user")
  suspend fun loadAllUsers():Array<User>
}
```

<br>

#### 트랜잭션 메서드 만들기

추상 Dao 클래스에서 비추상 메서드를 구현할 떄 `@Transaction` 애노테이션을 추가 하여 트랜잭션 메서드로 만들 수 있다.

이 메서드 내부에서는 Dao 클래스 내의 메서드를 호출 할 수 있음

```kotlin
@Dao
interface SongDao{
  @Insert
  fun insert(song:Song)
  
  @Delete
  suspend fun delete(song:Song)
  
  @Transaction
  fun insertAndDelete(newSong:Song, oldSong:Song){
  	//내부 코드는 단일 트랙잭션으로 동작
  	insert(newSong)
  	delete(oldSong)
  }
}
```

<br>

#### 관계있는 엔터티 가져오기

`@Relation` 은 POJO에서 관계 엔터티를 자동으로 가져오는데 사용할 수 있는 애노테이션

POJO가 쿼리에서 리턴시, 해당 POJO 안 모든 관계도 Room 에 의해 가져와짐

```kotlin
@Entity
data class Pet{
  @PrimaryKey val id:Int
  val userId:Int
  val name:String    
  // other fields
}

data class UserNameAndAllPets{
  val id:Int
  val name:String
  @Relation(parentColumn="id", entityColumn="userId")
  val pets:List<Pet>
}

@Dao
interface UserPetDao{
  @Query("SELECT id, name from User")
  fun loadUserAndPets() : List<UserNameAndAllPets>
}
```

`@Relation` 애노테이션이 달린 필드의 유형은 반드시 List 또는 Set 이여야한다.

엔터티 타입은 리턴 타입에서 유추되지만, 다른 객체를 반환하려면 애노테이션에 entity 속성을 지정 가능

<br>

<br>

### **타입 컨버터 사용하기**

사용자 지정 타입을 추가하도록 Room 에서는 `@TypeConverter` 애노테이션을 제공

`@TypeConverter` 는 사용자 정의 클래스를 Room에서 다루는 클래스로 변환

> Data 인스턴스를 Long 타입으로 변환하는 컨버터 설정

```kotlin
class Converters{
  @TypeConverter
  fun fromTimestamp(value:Long):Date?{
    return if(value == null){
      null
    }else{
      Date(value)
    }
  }
  
  @TypeConverter
  fun dateToTimestamp(date:Date):Long?{
    return if(date == null){
      null
    }else{
      date.time
    }
  }
}
```

<br>

> `@TypeConverters` 애노테이션을 AppDatabase 클래스에 추가하여 Room이 해당 AppDatabase의 각 앤터티 및 DAO에 대해 정의한 컨버터를 사용할 수 있도록 함

```kotlin
@Database(entities=[User::class], version =1)
@TypeConverters([Converters::class])
abstract class AppDatabase : RoomDatabase{
  abstract fun userDao():UserDao
}
```

>쿼리 사용 예시

```kotlin
@Entity
data class User{
  val birthday:Date
}

@Dao
interface UserDao{
  @Query("SELECT * FROM user WHERE birthday BETWWN :from AND :to")
  fun findUsersBornBetweenDates(from:Date, to:Date):List<User>
}
```

<br>

<br>

---

