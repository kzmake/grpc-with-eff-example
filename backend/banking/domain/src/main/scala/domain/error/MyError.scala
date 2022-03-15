package domain.error

sealed class MyError(message: String)            extends Exception(message)
case class NotFoundAccountError(message: String) extends MyError(message)
case class UnauthorizedError(message: String)    extends MyError(message)
