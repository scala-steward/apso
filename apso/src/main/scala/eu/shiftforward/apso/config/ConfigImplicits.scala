package eu.shiftforward.apso.config

import com.typesafe.config.{ ConfigException, Config }
import com.typesafe.config.ConfigException.BadValue

/**
 * Provides useful extension methods for `Config` instances.
 */
trait ConfigImplicits {

  final implicit class ApsoConfig(val conf: Config) {

    @inline private[this] def getOption[A](path: String, f: (Config, String) => A) =
      if (conf.hasPath(path)) Some(f(conf, path)) else None

    /**
     * Gets the value as a boolean wrapped in a `Some` if one is defined and `None` if not. This method throws an
     * exception if the path has a value associated but it is not of the requested type.
     *
     * @param path the path in the config
     */
    def getBooleanOption(path: String) = getOption(path, _.getBoolean(_))

    /**
     * Gets the value as a int wrapped in a `Some` if one is defined and `None` if not. This method throws an
     * exception if the path has a value associated but it is not of the requested type.
     *
     * @param path the path in the config
     */
    def getIntOption(path: String) = getOption(path, _.getInt(_))

    /**
     * Gets the value as a long wrapped in a `Some` if one is defined and `None` if not. This method throws an
     * exception if the path has a value associated but it is not of the requested type.
     *
     * @param path the path in the config
     */
    def getLongOption(path: String) = getOption(path, _.getLong(_))

    /**
     * Gets the value as a double wrapped in a `Some` if one is defined and `None` if not. This method throws an
     * exception if the path has a value associated but it is not of the requested type.
     *
     * @param path the path in the config
     */
    def getDoubleOption(path: String) = getOption(path, _.getDouble(_))

    /**
     * Gets the value as a string wrapped in a `Some` if one is defined and `None` if not. This method throws an
     * exception if the path has a value associated but it is not of the requested type.
     *
     * @param path the path in the config
     */
    def getStringOption(path: String) = getOption(path, _.getString(_))

    /**
     * Gets the percentage value as a double wrapped in a `Some` if one is defined and `None` if not.
     *
     * @param path the path in the config
     * @throws ConfigException.BadValue if the percentage does not end with '%'.
     */
    def getPercentageOption(path: String): Option[Double] = {
      getStringOption(path).map { value =>
        if (value.last != '%')
          throw new ConfigException.BadValue(conf.origin, path, "A percentage must end with '%'.")
        else
          value.dropRight(1).toDouble / 100.0
      }
    }

    /**
     * Gets the value as a double from a percentage.
     *
     * @param path the path in the config
     * @throws ConfigException.Missing if value is absent or null
     * @throws ConfigException.BadValue if the percentage does not end with '%'.
     */
    def getPercentage(path: String): Double = {
      getPercentageOption(path) match {
        case None => throw new ConfigException.Missing(path)
        case Some(value) => value
      }
    }

  }
}
