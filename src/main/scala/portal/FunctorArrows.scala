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
    getPerson _ map (Functor[Option] lift personFeature)


  /**
   * Pull many people from the "database"
   */
  def getPeople(names: List[String]): List[Option[Person]] =
    names map getPerson

  /**
   * Extract features for every Person entry
   */
  def peopleFeatures(ppl: List[Option[Person]]): List[Option[PFeature]] =
    ((Functor[List] compose Functor[Option]) lift personFeature)(ppl)

  /**
   * Contramapping [[getPeople]] before [[peopleFeatures]]
   */
  val getAndFeaturePeople: List[String] => List[Option[PFeature]] =
    Contravariant[? => List[Option[PFeature]]]
      .contramap(peopleFeatures)(getPeople)

  /**
   * Or more simply... [[Function1.andThen]]
   */
  val getAndFeaturePeople2: List[String] => List[Option[PFeature]] =
    getPeople _ andThen peopleFeatures

  /**
   * Tying it all together and extracting out the relevant features
   */
  val featurePipeline: List[String] => List[PFeature] =
    Profunctor[Function1].dimap(peopleFeatures)(getPeople)(_.mapFilter(x => x))

}
