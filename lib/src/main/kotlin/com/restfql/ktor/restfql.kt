package com.restfql.ktor

import graphql.language.Field
import graphql.language.OperationDefinition
import io.ktor.content.*
import io.ktor.server.application.*
import kotlinx.serialization.json.*

val RestFQL = createApplicationPlugin(name = "RestFQL") {

    fun getSubset(fields: List<Field>, parsedResponse: JsonObject): Map<String, JsonElement> {
        val response = mutableMapOf<String, JsonElement>()
        fields.forEach { field -> response.put(
            field.name,
            field.selectionSet
                ?.let { JsonObject(getSubset(it.selections as List<Field>, parsedResponse.get(field.name) as JsonObject)) }
                ?: parsedResponse.get(field.name)!!)
        }
        return response
    }

    onCallRespond { call ->
        transformBody { data ->
            call.request.queryParameters.get("fql")
                ?.let {
                    val fql = graphql.parser.Parser.parse(it)
                    val textContent = data as TextContent
                    val result = getSubset(
                            (fql.definitions[0] as OperationDefinition).selectionSet.selections as List<Field> ,
                            Json.parseToJsonElement(textContent.text) as JsonObject
                        )
                    TextContent(
                        JsonObject(result).toString(),
                        textContent.contentType,
                        textContent.status
                    )
                }
                ?: data
        }
    }
}