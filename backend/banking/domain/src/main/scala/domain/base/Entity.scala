package domain.base

import domain.shared.Id

trait Entity[T <: Entity[_]] {
  def id: Id[T]

  override def hashCode: Int = id.hashCode
  override def equals(that: Any): Boolean = that match {
    case x: Entity[T] => this.id.eq(x.id)
    case _            => false
  }
}
