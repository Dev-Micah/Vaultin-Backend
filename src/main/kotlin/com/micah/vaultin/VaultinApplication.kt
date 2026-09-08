package com.micah.vaultin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class VaultinApplication

fun main(args: Array<String>) {
	runApplication<VaultinApplication>(*args)
}
