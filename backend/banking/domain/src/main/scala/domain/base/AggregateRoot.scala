package domain.base

trait AggregateRoot[T <: Entity[_]] extends Entity[T]
