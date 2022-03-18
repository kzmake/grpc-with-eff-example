package domain.base

import domain.shared.Id

trait Entity[T <: Entity[_]] {
  def id: Id[T]

  override def hashCode: Int = id.hashCode
}
