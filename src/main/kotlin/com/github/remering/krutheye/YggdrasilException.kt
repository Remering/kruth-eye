package com.github.remering.krutheye

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException


private const val INVALID_TOKEN = "Invalid token."
private const val INVALID_CREDENTIALS = "Invalid credentials. Invalid username or password."
private const val INVALID_PROFILE = "Invalid profile."
private const val TOKEN_ALREADY_ASSIGNED = "Access token already has a profile assigned."
//private const val ACCESS_DENIED = "Access denied."
private const val NO_SUCH_PROFILE = "No such profile."
private const val NO_SUCH_TEXTURE = "No such texture."
private const val NO_SUCH_SESSION = "No such session."

open class YggdrasilException(status: HttpStatus, val yggdrasilError: String, val yggdrasilMessage: String) :
    ResponseStatusException(status, yggdrasilError)

open class YggdrasilNoContentException(yggdrasilMessage: String):
    YggdrasilException(HttpStatus.NO_CONTENT, "206 No Content", yggdrasilMessage)

open class YggdrasilForbiddenOperationException(yggdrasilMessage: String) :
    YggdrasilException(HttpStatus.FORBIDDEN, "ForbiddenOperationException", yggdrasilMessage)

open class YggdrasilIllegalArgumentException(yggdrasilMessage: String) :
    YggdrasilException(HttpStatus.FORBIDDEN, "IllegalArgumentException", yggdrasilMessage)


//class InternalServerErrorException(exception: Exception): YggdrasilException(HttpStatus.INTERNAL_SERVER_ERROR, exception.message?:HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase, exception.localizedMessage)

class InvalidTokenException: YggdrasilForbiddenOperationException(INVALID_TOKEN)

class InvalidProfileException: YggdrasilForbiddenOperationException(INVALID_PROFILE)

class InvalidCredentialsException: YggdrasilForbiddenOperationException(INVALID_CREDENTIALS)

class TokenAlreadyAssignedException: YggdrasilIllegalArgumentException(TOKEN_ALREADY_ASSIGNED)

class NoSuchProfileException: YggdrasilNoContentException(NO_SUCH_PROFILE)

class NoSuchTextureException: YggdrasilNoContentException(NO_SUCH_TEXTURE)

class NoSuchSessionException: YggdrasilNoContentException(NO_SUCH_SESSION)