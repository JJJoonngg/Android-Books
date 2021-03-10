### **데이터 바인딩과 ViewStub 활용하기**

ViewStub는 사이즈가 없는 보이지 않는 뷰로, 런 타임에서 lazy-inflate를 원할 때 사용 할 수 있다.

보이게 하거나 inflate() 메서드를 호출하면 레이아웃이 전개 되면서 ViewStub를 대체해 ViewStub은 사라짐.

전개 된 뷰는 ViewStub의 부모 뷰에 추가됨.

<br>

> 레이아웃에서 ViewStub를 사용하는 예제

```xml
<ViewStub android:id="@+id/stub"
          android:inflatedId="@+id/subTree"
          android:layout="@layout/mySubTree"
          .../>
```

`findViewById()` 호출을 통해 ViewSutb에 접근 가능

```kotlin
val viewStub:ViewStub = findViewById(R.id.stub)
```

생성되는 바인딩 클래스에서 ViewStub는 ViewStubProxy 로 표현되며, ViewStub에 대해 접근 가능하게 한다.

```kotlin
val binding : ActivityMainBinding = ...
val viewStubProxy : ViewStubProxy = binding.stub
val viewStub = viewStubProxy.ViewStub
```

ViewStub에 지정된 레이아웃을 전개시, `setVisibility()` 또는 `inflate()` 를 호출 할 수 있음

```kotlin
val viewStub : ViewStub =...

viewStub.inflate()
//or
viewStub.visibility = View.VISIBLE
```

<br>

ViewStub은 복잡하게 구성된 레이아웃을 빠르게 전개해야 하는 상황에서, 레이아웃의 전개 시기를 선택적으로 늦출 수 있음

<br>

<br>

#### **ViewStub와 바인딩 어댑터의 사용**

만약 ViewStub에 지정된 레이아웃이 전개된 상태(inflated)라면 `app:user="@{user}"` 와 같은 바인딩 표현식이 사용 가능

ViewStub와 바인딩 표현식을 사용하는 경우 ViewStub에 반드시 아이디를 선언해야함

<br>

```xml
<!--activity_user.xml-->
<layout ...>
  <data>
    <variable
              name="user"
              type="..."/>
  </data>
  ...
  
  		<!--id 필수-->
  		<ViewStub
                android:id="@+id/user_view_stub"
                android:layout="@layout/view_user"
                app:user="@{user}"/>
  
</layout>
```

```xml
<!--view_user.xml-->
<layout ...>
  <data>
    <variable
              name="user"
              type="..."/>
  </data>
  <LinearLayout ...>
    
    <!--user 데이터와 뷰를 바인딩-->
    ...
  </LinearLayout>
  
</layout>
```

> activity_user.xml 파일로 인해 생성되는 ActivityUserBindingImpl

```java
@Override
protected void executeBindings(){
  ...
    if(this.userViewStub.isInflated())
      this.userViewStub.getBinding().setVariable(BR.user, user);
  ...
}
```

전개되지 않은 바인딩 인스턴스에 접근하면 NPE 가 발생하기 때문에 전개되지 않은 ViewStub은 바인딩 표현식이 적용 되지 않도록 분기됨. 

전개되지 않은 ViewStub에 바인딩 표현식이 적용되지 않으므로, ViewStub에 지정된 레이아웃을 먼저 전개해야함

<br>

코드에서 `inflate() ` 또는 `setVisibility(View.VISIBLE)` 를 호출해도 되지만, `android:visibility` 속성을 이용 하여 다음과 같이 전개 가능

```xml
<!--activity_user.xml-->
<layout ...>
  <data>
    <import type="android.view.View"/>
    <variable
              name="user"
              type="..."/>
  </data>
  ...
  		<ViewStub
                android:id="@+id/user_view_stub"
                android:layout="@layout/view_user"
                app:user="@{user}"
                android:visibility="@{user==null ? View.GONE : View.VISIBLE}"
                .../>
  
</layout>
```

> activity_user.xml 파일로 인해 생성되는 ActivityUserBindingImpl

```java
@Override
protected void executeBindings(){
  ...
    if(!this.userViewStub.isInflated())
      this.userViewStub.getViewStub()
      		.setVisibility(userJavaLangObjectNullViewGoneViewVISIBLE);
    if(this.userViewStub.isInflated())
      this.userViewStub.getBinding().setVariable(BR.user, user);
  ...
}
```

ViewStub로 부터 레이아웃 전개를 먼저 하고 user 데이터를 바인딩 하는 것을 확인 가능

<br>

하지만 레이아웃을 전개하는 메서드는 ViewStub가 전개되지 않았을 때만 호출되어 레이아웃을 동적으로 숨기고 싶을 경우

바인딩 표현식을 사용할 수 없음

이런 문제 해결을 위해 사용자 정의 바인딩을 사용할 수 있음

> User데이터를 바인딩 하도록 사용자 정의 바인딩 어댑터를 정의한 코드

```kotlin
@JvmStatic
@BindingAdapter(value = ["user", ""], requireAll = false)
fun setUser(view: View, user: View, nothing: Unit) {
    //do nothing
}

fun setUser(proxy: ViewStubProxy, user: User, nothing: Unit) {
    if (proxy.viewStub != null) {
        if (user != null) {
            proxy.viewStub?.visibility = View.VISIBLE
            proxy.binding?.setVariable(BR.user, user)
            proxy.binding.executePendingBindings()
        } else {
            proxy.binding?.setVariable(BR.user, null)
            proxy.binding?.root.visibility = View.GONE
        }
    }
}
```

```xml
<!--activity_user.xml-->
...
<ViewStub
       	 android:id="@+id/user_view_stub"
         android:layout="@layout/view_user"
         app:user="@{user}"
         .../>
<!-- 사용자 정의 바인딩 적용 -->
  
...
```

<br>

ViewStub와 @BindingAdapter 메서드 사용시 2가지 주의 사항이 존재

- 레이아웃에 선언된 ViewStub는 바인딩 클래스에서 ViewStubProxy로 표현됨, 따라서 @BindingAdapter 메서드의 첫 번째 매개 변수로 View를 지정해도 ViewStubProxy는 View가 아니므로 바인딩 클래스 내부에서 참조하지 못함
  - ViewStubProxy를 첫 번째 매개 변수로 갖는 이름이 같은 @BindingAdpater 메서드를 하나 더 만들어야함
- BindingAdapter의 속성이 1개라면 레이아웃이 전개되었을 때만 바인딩을 수행하는 코드가 바인딩 클래스에 생성됨, 2개 이상을 속성을 정의해야만 레이아웃의 전개 여부와 관계없이 바인딩을 수행함
  - 레이아웃을 동적으로 감추려면 반드시 @BindingAdapter의 2개 이상의 속성을 선언해야함(더미 속성을 추가하는 방법 존재)

<br>

---

