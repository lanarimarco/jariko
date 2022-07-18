/*
 * Copyright 2019 Sme.UP S.p.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:JvmName("StandardFeaturesFactory")
package com.smeup.rpgparser.interpreter

import kotlin.reflect.full.createInstance

/**
 * Allows enable features
 * */
interface IFeaturesFactory {
    fun createSymbolTable(): ISymbolTable
}

object FeaturesFactory {

    private fun dumpVersion() {
        IFeaturesFactory::class.java.getResource("/META-INF/com.smeup.jariko/version.txt")?.apply {
            readText()?.let {
                println("JaRIKo - Java Rpg Interpreter written in Kotlin")
                println(it)
                println("************************************************")
            }
        }
    }

    private val factory: IFeaturesFactory by lazy {
        dumpVersion()
        val property = System.getProperty("featuresFactory", "")
        val featuresFactoryId = if (property == "") "default" else {
            property
        }
        val featuresFactoryImpl = if (featuresFactoryId.contains('.', false)) {
            featuresFactoryId
        } else {
            val property = java.util.Properties()
            IFeaturesFactory::class.java.getResource("/META-INF/com.smeup.jariko/features.properties")!!
                .openStream()!!.use {
                    property.load(it)
                }
            property.getProperty(featuresFactoryId)
                ?: throw IllegalArgumentException("Not found factory identified by: $featuresFactoryId")
        }
        println("Creating features factory: $featuresFactoryImpl")
        Class.forName(featuresFactoryImpl).kotlin.createInstance() as IFeaturesFactory
    }

    fun newInstance() = factory
}

class StandardFeaturesFactory : IFeaturesFactory {
    override fun createSymbolTable() = SymbolTable()
}
