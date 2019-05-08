package portal

import cats.data.Kleisli
import cats.effect.IO
import cats.implicits._

import scala.util.Random

object KleisliArrows {

  /*
   *Any of the non-Option fields can potentially be `null`!
   */
  sealed trait IP { val addr: String }
  final case class IPV4(addr: String) extends IP
  final case class IPV6(addr: String) extends IP

  final case class App(name: String, appConf: Map[String, String])
  final case class MicroService(name: String, locale: String)
  type ServerType = Either[App, MicroService]


  /**
   * [[ServerConf]] is a nested product of sum and product types.
   */
  final case class ServerConf(host: String, ip: IP, serverType: ServerType)


  /**
   * Say we wanted to pull the `locale` of a server
   */
  val getLocale: ServerType => Option[String] = {
    case Left(app) => app.appConf.get("locale")
    case Right(ms) => Option(ms.locale)
  }

  /**
   * `locale` is not standard but we want to parse out territory
   */
  val getTerritory: String => Option[String] =
    "(NA|EU|AP|AF)".r.findFirstIn(_)


  val getLanguage: String => Option[String] =
    "(EN|NO|DE|FR)".r.findFirstIn(_)


  /**
   * Get the `ServerType` field
   */
  val getServerType: ServerConf => Option[ServerType] =
    sc => Option(sc.serverType)

  /**
   * Lifting our effectful functions into [[Kleisli]], we can easily compose them.
   */
  val typeToTerritory: Kleisli[Option, ServerConf, String] =
    Kleisli(getServerType) >>> Kleisli(getLocale) >>> Kleisli(getTerritory)

  /**
   * _locale using [[cats.data.Kleisli]] and [[cats.arrow.Choice]]
   */
  val _serverType: Kleisli[Option, ServerConf, ServerType] =
    Kleisli(s => Option(s.serverType))

  val _appLocale: Kleisli[Option, App, String] =
    Kleisli(_.appConf.get("locale"))

  val _msLocale: Kleisli[Option, MicroService, String] =
    Kleisli(m => Option(m.locale))

  val _locale: Kleisli[Option, ServerConf, String] =
    (_appLocale choice _msLocale) compose _serverType


  /**
   * Kleisli category is the category of monad... so it allows chaining of `F[_]` computations.
   * For example when parsing out the subnet from IP addresses
   */

  val getIP = Kleisli((s: ServerConf) => Option(s.ip))

  /**
   * What about changing effect types? [[Kleisli]]'s API has us covered
   */
  def ping(ip: IP): IO[Double] = IO {
    Random.setSeed(ip.addr.head.toLong); Random.nextDouble()
  }

  def health(ip: IP): IO[Boolean] = ip match {
    case ipv4: IPV4 => true.pure[IO]
    case ipv6: IPV6 => false.pure[IO]
  }

  val pingHealth: Kleisli[IO, IP, (Double, Boolean)] =
    Kleisli(ping) merge Kleisli(health)


  /**
   * Use [[Kleisli.mapF]] to change functor types
   */
  val default = (0.0, false).pure[IO]

  val extractPingHealth: Kleisli[IO, ServerConf, (Double, Boolean)] =
    getIP.mapF(_.fold(default)(pingHealth.run))

}
