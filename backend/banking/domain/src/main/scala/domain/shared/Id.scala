package domain.shared

import domain.base.{EntityId, ResourceScope}

final case class Id[T](value: String) extends EntityId with ResourceScope {
  val resourceScope = Set(value)
}
