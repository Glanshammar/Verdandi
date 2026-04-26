# TabsComponent

```Kotlin
val tabs = listOf(
        Tab(
            title = "Tab 1",
            icon = painterResource(Res.drawable.house),
            content = {  }
        ),
        Tab(
            title = "Tab 2",
            icon = painterResource(Res.drawable.user),
            content = {  }
        ),
        Tab(
            title = "Tab 3",
            icon = painterResource(Res.drawable.settings),
            content = {  }
        )
    )

TabsComponent(tabs = tabs)
```