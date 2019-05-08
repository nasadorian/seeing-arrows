package portal

import cats.arrow.Profunctor
import cats.{Contravariant, Functor}
import cats.implicits._

object FunctorArrows {

  final case class Person(name: String, age: Int)
  final case class PFeature(nmLn: Int, nmFrq: Map[Char, Int])

  def getPerson(s: String): Option[Person] =
    Map(
      "Jerry" -> Person("Jerry", 45),
      "George" -> Person("George", 30),
      "Elaine" -> Person("Elaine", 32)
    ).get(s)


  /**
   * Compute length and character frequency of Person#name
   */
  def personFeature(p: Person): PFeature =
    PFeature(p.name.length, p.name.groupBy(identity).mapValues(_.length))

  /**
   * Mapping [[personFeature]] over the `Option[Person]` returned by [[getPerson]]
   */
  def nameToFeature(s: String): Option[PFeature] =
    getPerson(s) map personFeature

  /**
   *  An alternative formulation at the function level.
   */
  val nameToFeature2: String => Option[PFeature] =
    getPerson _ andThen (Functor[Option] lift personFeature)

  /**
   * [[optLift]] lifts function `B => C`  and composes it with arrow `A => Option[B]`
   */
  def optLift[A, B, C]: (B => C) => (A => Option[B]) => (A => Option[C]) =
    f => Functor[A => ?] compose Functor[Option] lift f

  def composeF[A, B, C, F[_]](implicit F: Functor[F]): (B => C) => (A => F[B]) => (A => F[C]) =
    f => Functor[A => ?] compose F lift f

  /**
   * Third implementation in terms of [[optLift]]
   */
  val nameToFeature3: String => Option[PFeature] =
    optLift(personFeature)(getPerson)

  /**
   * Pull many people from the "database"
   */
  val getPeople: List[String] => List[Option[Person]] =
    _ map getPerson

  /**
   * Extract features for every Person entry
   */
  val peopleFeatures: List[Option[Person]] => List[Option[PFeature]] =
    (Functor[List] compose Functor[Option]) lift personFeature

  /**
   * Contramapping [[getPeople]] before [[peopleFeatures]]
   */
  val getAndFeaturizePeople: List[String] => List[Option[PFeature]] =
    Contravariant[? => List[Option[PFeature]]]
      .contramap(peopleFeatures)(getPeople)

  val getAndFeaturizePeople2: List[String] => List[Option[PFeature]] =
      getPeople andThen peopleFeatures

  /**
   * Or more simply... [[Function1.andThen]]
   */
  val getAndFeaturePeople2: List[String] => List[Option[PFeature]] =
    getPeople andThen peopleFeatures

  /**
   * Tying it all together and extracting out the relevant features
   */
  val featurePipeline: List[String] => List[PFeature] =
    Profunctor[Function1].dimap(peopleFeatures)(getPeople)(_.mapFilter(x => x))

  val featurePipeline2: List[String] => List[PFeature] =
    peopleFeatures.dimap(getPeople)(_.mapFilter(x => x))

  /**
   * Or in terms of [[Function1.andThen]]
   */
  val featurePipeline3: List[String] => List[PFeature] =
    getPeople andThen peopleFeatures andThen (_.mapFilter(x => x))

  /**
   * Or even fancier in terms of [[Profunctor.dimap]]
   */
  val featurePipeline3p: List[String] => List[PFeature] =
    peopleFeatures.dimap(getPeople)(_.mapFilter(x => x))

}
