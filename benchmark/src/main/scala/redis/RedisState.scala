package redis

import org.openjdk.jmh.annotations.{Setup, Level, TearDown}

import scala.concurrent.Await

case class RedisState(initF: () => Unit = () => ()) {
  val pekkoSystem = org.apache.pekko.actor.ActorSystem()
  val redis = RedisClient()(pekkoSystem)

  implicit val exec = pekkoSystem.dispatchers.lookup(Redis.dispatcher.name)

  import scala.concurrent.duration._

  Await.result(redis.ping(), 2 seconds)

  @TearDown(Level.Trial)
  def down: Unit = {
    redis.stop()
    pekkoSystem.shutdown
    pekkoSystem.awaitTermination()
  }
}

trait RedisStateHelper {
  var rs: RedisState = _

  @Setup(Level.Trial)
  def up() = {
    rs = RedisState()
    initRedisState()
  }

  @TearDown(Level.Trial)
  def down() = {
    rs.down
  }

  def initRedisState(): Unit = {}
}
