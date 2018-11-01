package cz.fit.metacentrum

import cz.fit.metacentrum.service.CommandLineParser


fun main(args: Array<String>) {

    val parsedArgs = CommandLineParser().parseArguments(args)

    println("Hello, world!")
    println(parsedArgs)
}