package sharding.storage.itmo

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
	fromApplication<ItmoApplication>().with(TestcontainersConfiguration::class).run(*args)
}
