## **Room 마이그레이션 하기**

Room 을 사용하면 데이터를 보존하도록 마이그레이션 클래스를 작성할 수 있음

각 마이그레이션 클래스에는 startVersion 및 endVersion을 지정할 수 있음

런 타임에 Room은 올바른 순서를 사용하여 각 마이그레이션 클래스의 `migrate()` 메서드를 실행, 데이터베이스를 다음 버전으로 마이그레이션 한다.

```kotlin
val MIGRATION_1_2 = object:Migration(1, 2){
  override fun migrate(database:SupportSQLiteDatabase){
    database.execSQL(
      """
      CREATE TABLE Fruite(
      	id INTEGER,
      	name TEXT,
      	PRIMARY KEY(id)
      )
      """
    )
  }
}

val MIGRATION_2_3 = object:Migration(2, 3){
  override fun migrate(database:SupportSQLiteDatabase){
    database.execSQL(
      """
      ALTER TABLE Book
      ADD COLUMN pub_year INTEGER
      """
    )
  }
}

Room.databaseBuilder(
  context.applicationContext,
  MyDb::class.java,
  "database-name"
).addMigration(MIGRATION_1_2)
 .addMigration(MIGRATION_2_3)
 .build()
```

만약 Room 에서 문제를 발견하면, 잘못된 내용이 포함된 예외를 발생 시킴

<br>

<br>

### **마이그레이션 테스트하기**

Room 은 마이그레이션 테스트 프로세스를 지원하는 테스트 Maven 아티팩트를 제공하지만 작동하려면 데이터베이스 스키마를 먼저 내보내야한다.

<br>

#### **데이터베이스 스키마 내보내기**

컴파일 타임에 Room은 데이터베이스의 스키마 정보를 JSON 파일로 내보낼 수 있음

> build.gradle 파일에서 애노테이션 프로세서 특성을 설정

```groovy
android{
  ...
    defaultConfig{
      ...
        javaCompileOptions{
          annotationProcessorOptions{
            arguments=["room.schemaLocation":"$projectDir/schemas".toString()]
          }
        }
    }
}
```

<br>

마이그레이션을 테스트하려면 `android.arch.persistence.room:testing` 아티팩트를 테스트 의존성에 추가 및

스키마 위치를 assest 폴더로 추가한다.

```groovy
android{
  ...
    sourceSets{
      androidTest.assests.srcDirs +=files("$projectDir/schemas".toString())
    }
}
```

<br>

> 마이그레이션 테스트 예제 코드

```kotlin
@RunWith(AndroidJUnit4::class)
class MigrationTest {
    private val TEST_DB = "migration-test"

    @Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            MigrationDb::class.java.canonicalName,
            FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        var db = helper.createDatabase(TEST_DB, 1).apply {
            execSQL(...)

            close()
        }
        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, MIGRATION_1_2)
    }
}
```

<br>

#### **모든 마이그레이션 테스트하기**

앞의 예제는 하나의 버전에서 다른 버전으로 단일 증분 마이그레이션을 보여주지만, 모든 마이그레이션을 수행하는 테스트를 하는 것이 좋다. 이러한 테스트는 데이터베이스의 불일치를 잡아내는데 유용하다.

```kotlin
@RunWith(AndroidJUnit4::class)
class MigrationTest {
    private val TEST_DB = "migration-test"

    private val ALL_MIGRATIONS = arrayOf(
            MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)

    @Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            AppDatabase::class.java.canonicalName,
            FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrateAll() {
        helper.createDatabase(TEST_DB, 1).apply {
            close()
        }

        Room.databaseBuilder(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                AppDatabase.class,
                TEST_DB
        ).addMigrations(*ALL_MIGRATIONS).build().apply {
            getOpenHelper().getWritableDatabase()
            close()
        }
    }
}
```

<br>

#### **누락된 마이그레이션 경로를 정상적으로 처리하기**

데이터베이스의 스키마를 업데이트한 후에도 일부 데이터베이스는 여전히 이전 스키마 버전을 사용할 수 있다.

이전 버전에서 현재 버전으로 업그레이드하는 마이그레이션 규칙을 찾을 수 없으면 `IllegalStateException` 이 발생

이때의 방법은 데이터 베이스를 작성 시 `fallbackToDestructiveMigration()` 메서드를 빌더에서 호출한다.

```kotlin
Room.databaseBuilder(applicationContext, MyDb::class.java, "database-name")
        .fallbackToDestructiveMigration()
        .build()
```

위 로직을 포함시 스키마 버전 간 마이그레이션 경로 누락된 경우에 데이터베이스 테이블을 영구적으로 파괴하고 다시 만든다.

> 파괴 후 테이블을 재생성하는 다른 옵션

- [`fallbackToDestructiveMigrationFrom()`](https://developer.android.com/reference/kotlin/androidx/room/RoomDatabase.Builder?hl=ko#fallbacktodestructivemigrationfrom) : 특정 버전에서 이전시 Room 이 fallback 하도록 지시
-  [`fallbackToDestructiveMigrationOnDowngrade()`](https://developer.android.com/reference/kotlin/androidx/room/RoomDatabase.Builder?hl=ko#fallbacktodestructivemigrationondowngrade) : 스키마 다운그레이드를 시도할 때만 테이블을 파괴하고 재생성

<br>

<br>

### **데이터베이스 테스트하기**

앱에 대한 테스트를 실행할 때 Room을 사용하면 DAO 클래스의 Mock 인스턴스를 만들 수 있다. 이렇게 하면 전체 데이터베이스를 만들 필요가 없다. DAO가 데이터베이스의 세부 정보를 가지지 않아야 가능

<br>

데이터베이스 구현을 테스트하는 데 권장 방법은 Android 디바이스에서 실행되는 JUnit 테스트를 작성하는 것.

액티비티를 만들 필요가 없으므로 UI 테스트보다 실행속도가 빠르다.

<br>

테스트 설정시 다음 예제와 같이 메모리 내 데이터베이스를 작성하여 테스트를 더욱 긴밀하게 해야한다.

```kotlin
RunWith(AndroidJUnit4::class)
class SimpleEntityReadWriteTest {
    private lateinit var userDao: UserDao
    private lateinit var db: TestDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
                context, TestDatabase::class.java).build()
        userDao = db.getUserDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() {
        val user: User = TestUtil.createUser(3).apply {
            setName("george")
        }
        userDao.insert(user)
        val byName = userDao.findUsersByName("george")
        assertThat(byName.get(0), equalTo(user))
    }
}
```

<br>

<br>

---

