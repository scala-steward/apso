package eu.shiftforward.apso.json

import eu.shiftforward.apso.json.JsonFormatBuilder._
import eu.shiftforward.apso.json.JsonFormatBuilder.Field
import shapeless._
import spray.json._

/**
 * A type-safe way to construct a `JSONFormat` by incrementally adding, removing or updating fields.
 *
 * @param fields the fields currently in this builder
 * @tparam C the type of the `HList` of fields currently in this builder
 * @tparam FC the type of the `HList` of field definitions currently in this builder
 */
case class JsonFormatBuilder[C <: HList, FC <: HList](fields: FC)(implicit aux: FormatterAux[C, FC]) {

  /**
   * Adds a field to this builder.
   *
   * @param name the name of the new field
   * @tparam A the type of the new field
   * @return a new instance of `JsonFormatBuilder` with the new field
   */
  def field[A](name: String)(implicit jf: JsonFormat[A], ev: AppenderAux[A, C, FC]) =
    JsonFormatBuilder(ev.append(fields, Field(name, jf, None)))(ev.formatter)

  /**
   * Adds a field to this builder.
   *
   * @param name the name of the new field
   *             @param default the default value of the new field
   * @tparam A the type of the new field
   * @return a new instance of `JsonFormatBuilder` with the new field
   */
  def field[A](name: String, default: A)(implicit jf: JsonFormat[A], ev: AppenderAux[A, C, FC]) =
    JsonFormatBuilder(ev.append(fields, Field(name, jf, Some(default))))(ev.formatter)

  /**
   * Adds a field to this builder.
   *
   * @param name the name of the new field
   *             @param default the default value of the new field
   *                            @param jf a `JSONFormat` to use in the new field
   * @tparam A the type of the new field
   * @return a new instance of `JsonFormatBuilder` with the new field
   */
  def field[A](name: String, default: A, jf: JsonFormat[A])(implicit ev: AppenderAux[A, C, FC], dummy: DummyImplicit) =
    JsonFormatBuilder(ev.append(fields, Field(name, jf, Some(default))))(ev.formatter)

  /**
   * Replaces a field in this builder with another one.
   *
   * @param name the name of the new field
   * @tparam N the index of the field to replace
   * @tparam A the type of the new field
   * @return a new instance of `JsonFormatBuilder` with the field replaced
   */
  def replaceField[N <: Nat, A](name: String)(implicit jf: JsonFormat[A], ev: ReplacerAux[A, C, FC, N]) =
    JsonFormatBuilder(ev.replace(fields, Field(name, jf, None)))(ev.formatter)

  /**
   * Replaces a field in this builder with another one.
   *
   * @param name the name of the new field
   *             @param default the default value of the new field
   * @tparam N the index of the field to replace
   * @tparam A the type of the new field
   * @return a new instance of `JsonFormatBuilder` with the field replaced
   */
  def replaceField[N <: Nat, A](name: String, default: A)(implicit jf: JsonFormat[A], ev: ReplacerAux[A, C, FC, N]) =
    JsonFormatBuilder(ev.replace(fields, Field(name, jf, Some(default))))(ev.formatter)

  /**
   * Replaces a field in this builder with another one.
   *
   * @param name the name of the new field
   *             @param default the default value of the new field
   *                            @param jf a `JSONFormat` to use in the new field
   * @tparam N the index of the field to replace
   * @tparam A the type of the new field
   * @return a new instance of `JsonFormatBuilder` with the field replaced
   */
  def replaceField[N <: Nat, A](name: String, default: A, jf: JsonFormat[A])(implicit ev: ReplacerAux[A, C, FC, N], dummy: DummyImplicit) =
    JsonFormatBuilder(ev.replace(fields, Field(name, jf, Some(default))))(ev.formatter)

  /**
   * Removes a field in this builder.
   *
   * @tparam N the index of the field to remove
   * @return a new instance of `JsonFormatBuilder` with the field removed
   */
  def removeField[N <: Nat](implicit ev: RemoverAux[C, FC, N]): JsonFormatBuilder[ev.COut, ev.FCOut] =
    JsonFormatBuilder(ev.remove(fields))(ev.formatter)

  /**
   * Returns a `JSONFormat` for objects of a type using the current list of fields defined.
   *
   * @param readFunc a function converting the list of fields to an instance of `A`
   * @param writeFunc a function extracting the list of fields from an instance of `A`
   * @tparam A the type of objects for which a `JSONFormat` is to be returned
   * @return a `JSONFormat` for objects of type `A`.
   */
  def jsonFormat[A](readFunc: C => A, writeFunc: A => C): RootJsonFormat[A] =
    customJsonFormat(identity, readFunc, writeFunc, { (a, json) => json })

  /**
   * Returns a `JSONFormat` for objects of a type using the current list of fields defined and custom transformations.
   *
   * @param preRead a function transforming the JSON content before reads
   * @param readFunc a function converting the list of fields to an instance of `A`
   * @param writeFunc a function extracting the list of fields from an instance of `A`
   *                  @param postWrite a function transforming the JSON content after writes
   * @tparam A the type of objects for which a `JSONFormat` is to be returned
   * @return a `JSONFormat` for objects of type `A`.
   */
  def customJsonFormat[A](preRead: JsObject => JsObject, readFunc: C => A,
                          writeFunc: A => C, postWrite: (A, JsObject) => JsObject): RootJsonFormat[A] =
    new RootJsonFormat[A] {
      def read(json: JsValue): A = {
        val jsObject = preRead(json.asJsObject)
        val fieldValues = aux.read(jsObject.fields, fields)
        readFunc(fieldValues)
      }

      def write(obj: A): JsValue = {
        val fieldValues = writeFunc(obj)
        postWrite(obj, JsObject(aux.write(fields, fieldValues)))
      }
    }
}

/**
 * A companion object containing auxiliary types and factories for `JsonFormatBuilder`.
 */
object JsonFormatBuilder {

  /**
   * Returns a `JsonFormatBuilder` with no fields.
   *
   * @return a `JsonFormatBuilder` with no fields.
   */
  def apply() = new JsonFormatBuilder[HNil, HNil](HNil)(FormatterAux.HNilFormatter)

  case class Field[A](name: String, jf: JsonFormat[A], default: Option[A])

  /**
   * Trait defining how to read and write JSON from/to a typed list of fields.
   */
  trait FormatterAux[C <: HList, FC <: HList] {
    def read(obj: Map[String, JsValue], fa: FC): C
    def write(fa: FC, a: C): Map[String, JsValue]
  }

  object FormatterAux {

    def readValue[T](obj: Map[String, JsValue], field: Field[T]) = (obj.get(field.name), field.default) match {
      case (Some(jsValue), _) => field.jf.read(jsValue)
      case (None, Some(v)) => v
      case (None, None) => throw new DeserializationException(s"Mandatory parameter ${field.name} is missing")
    }

    implicit object HNilFormatter extends FormatterAux[HNil, HNil] {
      def read(obj: Map[String, JsValue], fa: HNil) = HNil
      def write(fa: HNil, a: HNil) = Map.empty
    }

    implicit def hConsBuilder[A, AS <: HList, FC <: HList](implicit ev: FormatterAux[AS, FC]) =
      new FormatterAux[A :: AS, Field[A]:: FC] {
        def read(obj: Map[String, JsValue], fa: Field[A] :: FC) =
          readValue(obj, fa.head) :: ev.read(obj, fa.tail)

        def write(fa: Field[A] :: FC, a: A :: AS) =
          ev.write(fa.tail, a.tail) + (fa.head.name -> fa.head.jf.write(a.head))
      }
  }

  import FormatterAux._

  /**
   * Evidence that appending a field of type `A` to a list of fields `C` (with definitions `FC`) results in a field
   * list of type `COut` (with definitions `FCOut`). Also contains ways to return a formatter for the list of
   * fields after the operation and to append concrete field definitions to a `FC`.
   */
  trait AppenderAux[A, C <: HList, FC <: HList] {
    type COut <: HList
    type FCOut <: HList
    def formatter: FormatterAux[COut, FCOut]
    def append(fa: FC, field: Field[A]): FCOut
  }

  object AppenderAux {

    implicit def hNilAppender[A](implicit aux: FormatterAux[A :: HNil, Field[A] :: HNil]) =
      new AppenderAux[A, HNil, HNil] {
        type COut = A :: HNil
        type FCOut = Field[A] :: HNil
        def formatter = aux
        def append(fa: HNil, field: Field[A]) = field :: HNil
      }

    implicit def hConsAppender[A, A2, C <: HList, FC <: HList](implicit ev: AppenderAux[A, C, FC]) =
      new AppenderAux[A, A2 :: C, Field[A2]:: FC] {
        type COut = A2 :: ev.COut
        type FCOut = Field[A2] :: ev.FCOut
        def formatter = hConsBuilder[A2, ev.COut, ev.FCOut](ev.formatter)
        def append(fa: Field[A2] :: FC, field: Field[A]) = fa.head :: ev.append(fa.tail, field)
      }
  }

  /**
   * Evidence that replacing the field with index `N` with a field of type `A` in a list of fields `C` (with
   * definitions `FC`) results in a field list of type `COut` (with definitions `FCOut`). Also contains ways to return
   * a formatter for the list of fields after the operation and to replace concrete field definitions in a `FC`.
   */
  trait ReplacerAux[A, C <: HList, FC <: HList, N <: Nat] {
    type COut <: HList
    type FCOut <: HList
    def formatter: FormatterAux[COut, FCOut]
    def replace(fa: FC, field: Field[A]): FCOut
  }

  object ReplacerAux {

    implicit def hConsZeroUpdater[A, A2, C <: HList, FC <: HList](implicit aux: FormatterAux[C, FC]) =
      new ReplacerAux[A2, A :: C, Field[A]:: FC, _0] {
        type COut = A2 :: C
        type FCOut = Field[A2] :: FC
        def formatter = hConsBuilder[A2, C, FC](aux)
        def replace(fa: Field[A] :: FC, field: Field[A2]) = field :: fa.tail
      }

    implicit def hConsSuccUpdater[A, A2, C <: HList, FC <: HList, N <: Nat](implicit ev: ReplacerAux[A2, C, FC, N]) =
      new ReplacerAux[A2, A :: C, Field[A]:: FC, Succ[N]] {
        type COut = A :: ev.COut
        type FCOut = Field[A] :: ev.FCOut
        def formatter = hConsBuilder[A, ev.COut, ev.FCOut](ev.formatter)
        def replace(fa: Field[A] :: FC, field: Field[A2]) = fa.head :: ev.replace(fa.tail, field)
      }
  }

  /**
   * Evidence that removing the field with index `N` from a list of fields `C` (with definitions `FC`) results in a
   * field list of type `COut` (with definitions `FCOut`). Also contains ways to return a formatter for the list of
   * fields after the operation and to remove concrete field definitions from a `FC`.
   */
  trait RemoverAux[C <: HList, FC <: HList, N <: Nat] {
    type COut <: HList
    type FCOut <: HList
    def formatter: FormatterAux[COut, FCOut]
    def remove(fa: FC): FCOut
  }

  object RemoverAux {

    implicit def hConsZeroRemover[A, C <: HList, FC <: HList](implicit aux: FormatterAux[C, FC]) =
      new RemoverAux[A :: C, Field[A]:: FC, _0] {
        type COut = C
        type FCOut = FC
        def formatter = aux
        def remove(fa: Field[A] :: FC) = fa.tail
      }

    implicit def hConsSuccRemover[A, C <: HList, FC <: HList, N <: Nat](implicit ev: RemoverAux[C, FC, N]) =
      new RemoverAux[A :: C, Field[A]:: FC, Succ[N]] {
        type COut = A :: ev.COut
        type FCOut = Field[A] :: ev.FCOut
        def formatter = hConsBuilder[A, ev.COut, ev.FCOut](ev.formatter)
        def remove(fa: Field[A] :: FC) = fa.head :: ev.remove(fa.tail)
      }
  }
}
