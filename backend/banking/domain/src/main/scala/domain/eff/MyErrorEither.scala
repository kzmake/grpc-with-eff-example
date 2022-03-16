package domain.eff

import domain.error.MyError
import org.atnos.eff._

object MyErrorEither {
  type MyErrorEither[A]  = Either[MyError, A]
  type _myErrorEither[R] = MyErrorEither /= R
}
