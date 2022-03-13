package domain.base

trait Repository[T <: AggregateRoot[T]]
    extends CreateRepository[T]
    with ReadRepository[T]
    with UpdateRepository[T]
    with DeleteRepository[T]
