package core.verdandi

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform