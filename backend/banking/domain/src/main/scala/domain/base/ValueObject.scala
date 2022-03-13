package domain.base

trait ValueObject extends Serializable {
  def hashCode: Int
  def equals(that: Any): Boolean
}
