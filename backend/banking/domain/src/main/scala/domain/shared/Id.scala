package domain.shared

import domain.base.ValueObject

final case class Id[T](value: String) extends ValueObject
