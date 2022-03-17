package domain.error

sealed class MyError(message: String)         extends Exception(message)
case class InvalidError(message: String)      extends MyError(message)
case class StateError(message: String)        extends MyError(message)
case class NotFoundError(message: String)     extends MyError(message)
case class UnauthorizedError(message: String) extends MyError(message)
