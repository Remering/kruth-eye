package com.github.remering.krutheye.controller

import com.github.remering.krutheye.YggdrasilException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.boot.web.servlet.error.ErrorAttributes
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.WebRequest
import javax.servlet.RequestDispatcher
import javax.servlet.http.HttpServletRequest

@Component
class YggdrasilErrorAttributes: DefaultErrorAttributes() {
    override fun getErrorAttributes(webRequest: WebRequest, options: ErrorAttributeOptions?): Map<String, Any?> {
        val error = getError(webRequest)
        val errorAttributes = hashMapOf<String, Any?>()
        error?.cause?.let { errorAttributes["cause"] = it }
        if (error is YggdrasilException) {
            errorAttributes["error"] = error.yggdrasilError
            errorAttributes["errorMessage"] = error.yggdrasilMessage
        } else {

            val status = if (error is HttpStatusCodeException) {
                error.statusCode
            } else {
                HttpStatus.valueOf(webRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE, RequestAttributes.SCOPE_REQUEST) as Int)
            }

            errorAttributes["error"] = status.reasonPhrase
            errorAttributes["errorMessage"] = error?.message?:"${status.value()} ${status.reasonPhrase}"
        }
        return errorAttributes
    }
}

@RestController
@RequestMapping("\${server.error.path:\${error.path:/error}}")
class YggdrasilErrorController(
    errorAttributes: ErrorAttributes,
    errorViewResolvers: List<ErrorViewResolver?>
): AbstractErrorController(errorAttributes, errorViewResolvers) {

    @Autowired
    constructor(errorAttributes: ErrorAttributes): this(errorAttributes, emptyList())

    override fun getErrorPath() = null

    @RequestMapping
    fun error(request: HttpServletRequest): ResponseEntity<Map<String, *>> {
        val status = getStatus(request)
        if(status == HttpStatus.NO_CONTENT) return ResponseEntity.status(status).build()

        val errorAttributes = getErrorAttributes(request, null)
        return ResponseEntity.status(status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorAttributes)
    }

}