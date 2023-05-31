package com.ucbjudge.ujsubjects.api

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Service
@RestController
@RequestMapping("/api/v1/subjects")
class SubjectsApi {

    companion object {
        private val logger = LoggerFactory.getLogger(SubjectsApi::class.java.name)
    }

    @GetMapping()
    fun findAll(): String {
        logger.info("Starting the API call to find all subjects")
        logger.info("Finishing the API call to find all subjects")
        return "Hello World"
    }


}