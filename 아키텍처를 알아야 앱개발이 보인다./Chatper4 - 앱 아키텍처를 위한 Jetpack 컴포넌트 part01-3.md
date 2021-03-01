#### **바인딩 어댑터 사용하기**

값을 설정하는 데 적절한 프레임 워크 호출을 담당.

데이터 바인딩 라이브러리를 사용시 값을 설정할 메서드를 선언, 자신만의 비즈니스 로직을 바인딩 시 적용가능

<br>

<br>

**자동 메서드 선택**

속성의 이름과 타입이 메서드를 찾는 데 사용 됨

예시로 `android:text="@{user.name}"` 표현식이 주어지면, `user.getName()` 메서드가 반환하는 타입에 맞는 `setText(arg)` 를 찾음

바인딩 표현식은 반드시 정확한 타입을 반환해야 하며, 필요하다면 반환 타입을 캐스팅 할 수 있음

<br>

심지어 주어진 이름의 속성이 존재하지 않더라고 동작. setter 에 맞는 속성 이름을 만들 면 됨.

> `setScrimColor(int)` 와 `setDrawerListener(DrawerListener)` 메서드를 사용하는
>
> `app:scrimColor`, `app:drawerListener`

```xml
<android.support.v4.widget.DrawerLayout
                                        android:layout_width="wrap_content"
                                        android:layout_height="wrap_content"
                                        android:scrimColor="@{@color/scrim}"
                                        android:drawerListener="@{fragment.drawerListener}"
```

<br>

<br>

**@BindingMethods 사용하기**

이 어노테이션은 클래스와 함께 사용되고 어느 클래스에도 추가될 수 있다.

여러 개의 @BindingMethod 어노테이션을 포함할 수 있다. 하나의 @BindingMethod가 setter와 속성의 이름을 연관 짓는다.

<br>

데이터 바인딩 라이브러리에서는 자주 사용되는 속성에 대해서 이미 @BindingMethos 어노테이션을 정의한 클래스를 제공한다. 

일반적으로 클래스 이름은 위젯 이름 + BindingAdapter

> ImageView 에 대한 BindingAdapter 클래스는 ImageViewBindingAdapter

```kotlin
@BindingMethods({
  @BindingMethod(
    type=android.widget.ImageView.class,
    attribute="android:tint",
    method="setImageTintList"),
  @BindingMethod(
    type=android.widget.ImageView.class,
    attribute="android:tintMode",
    method="setImageTintMode")
  )
})
class ImageViewBindingAdapter{...}
```

자주 사용되는 메서드에 대한 속성은 위젯별로 정의 되므로 필요한 경우에만 @BindingMethods 를 사용하여 메서드 이름을 명시하도록 한다.

<br>

<br>

**@BindingAdapter 사용하기**

몇몇 속성은 사용자 정의 바인딩 로직이 필요하므로 이 어노테이션과 함께 정적 바인딩 어댑터 메서드를 사용하면 

레이아웃 속성에 대해 어떤 식으로 사용자 정의 메서드를 만들지 결정할 수 있다.

<br>

> paddingLeft 속성에 대한 바인딩 어탭터 예제

```kotlin
@BindingAdapter("android:paddingLeft")
fun setPaddingLeft(view: View, padding: Int) {
    view.setPadding(
        padding,
        view.paddingTop,
        view.paddingRight,
        view.paddingBottom
    )
}
```

매개 변수의 타입이 매우 중요. 첫 번쨰 매개 변수는 사용한 속성이 연관 되는 뷰의 자료형, 두 번째 매개 변수는 주어진 속성의 바인딩 표현식에서 반환 되는 타입의 자료형으로 결정

<br>

바인딩 어댑터는 이미지를 작업 스레드에서 호출하는 로더 클래스를 사용하는 경우와 같이 사용자 정의된 자료형에 대해 사용할 때 유용하게 쓰임

<br>

> 여러 개의 속성을 하나의 바인딩 어댑터 메서드에서 처리

```kotlin
@BindingAdapter("imageUrl", "error")
fun loadImage(view: ImageView?, url: String?, error: Drawable?) {
    Picasso.get().load(url).error(error!!).into(view)
}
```

> 레이아웃에서 바인딩 표현식으로 다음과 같이 작성

```xml
<ImageView
           app:imageUrl="@{venue.imageUrl}"
           app:error="@{@drawable/venueError}"
```

<br>

사용자가 정의한 바인딩 어댑터 메서드를 찾을 때 네임 스페이스는 무시됨

여러 개의 속성을 다 사용하고 싶지 않다면 requireAll 플래그를 false 로 설정하면 선택적으로 적용할 수 있음

<br>

바인딩 어탭터 메서드는 선택적으로 이전 값을 그대로 유지도 가능

이전 값과 새롭게 얻은 값을 비교하여 중복 등록을 피하도록 사용가능

> 속성에 대한 매개 변수 첫번째는 이전, 그다음은 새로운 값

```kotlin
@BindingAdapter("android:paddingLeft")
fun setPaddingLeft(view: View, oldPadding: Int, newPadding: Int) {
    if (oldPadding != newPadding) {
        view.setPadding(
            newPadding,
            view.paddingTop,
            view.paddingRight,
            view.paddingBottom
        )
    }
}
```

<br>

<br>

**자동 객체 전환**

바인딩 표현식으로 부터 Object 가 반환될 떄, 데이터 바인딩 라이브러리는 속성값을 설정하는 데 사용되는 메서드를 선택.

선택된 메서드의 매개 변수 타입에 맞게 Obejct 캐스팅을 하는데, 이러한 동작은 ObservableMap 클래스에 저장된 데이터에 접근 시 사용하면 편리

```xml
<TextView
          android:text='@{userMap["lastName"]}'
          ... />
```

map 사용시 object.key 형식으로도 접근 가능

> `@{userMap.lastName}` 으로 대체 가능

<br>

매개 변수 타입이 애매모호한 경우 반드시 바인딩 표현식 내에서 타입 캐스팅을 해야함

<br>

<br>

**사용자 정의 객체 전환**

몇몇 경우에서는 특정 타입들 간 사용자 정의 객체 전환이 필요

> Drawable 이 반환 되어야 하는 데 int형이 반환될 때, int형은 ColorDrawable 로 변환 되어야함. 
>
> 이러한 변환은 @BindingConversion 과 정적 메서드의 사용으로 이루어짐

```xml
<View
      android:background="@{isError ? @color/red : @color/white}"
      ... />
```

```kotlin
@BindingConversion
fun convertColorToDrawable(color: Int): ColorDrawable {
    return ColorDrawable(color)
}
```

<br>

> 바인딩 표현식에서 주어진 값의 자료형은 일관성이 있어야함, 다음과 같은 경우에는 오류 발생

```xml
<View
      android:background="@{isError ? @drawble/error : @color/white}"
      ... />
```

<br>

---

