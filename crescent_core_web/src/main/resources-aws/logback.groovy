import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender

import static ch.qos.logback.classic.Level.DEBUG
import static ch.qos.logback.classic.Level.INFO
import static ch.qos.logback.classic.Level.WARN
import static ch.qos.logback.classic.Level.ERROR

appender("STDOUT", ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
  }
}

appender("FILE", RollingFileAppender) {
  file = "./logs/daily/search.log"
  rollingPolicy(TimeBasedRollingPolicy) {
    fileNamePattern = "search.log.%d{yyyy-MM-dd}"
    maxHistory = 30
  }
  encoder(PatternLayoutEncoder) {
    pattern = "%-4relative [%thread] %-5level %logger{35} - %msg%n"
  }
}

logger("com.tistory.devyongsik.analyzer", WARN, ["STDOUT"])
logger("com.tistory.devyongsik.admin", INFO, ["STDOUT"])
logger("com.tistory.devyongsik.logger", INFO, ["STDOUT", "FILE"])
logger("com.tistory.devyongsik.config", INFO, ["STDOUT"])
logger("com.tistory.devyongsik.highlight", INFO, ["STDOUT"])
logger("com.tistory.devyongsik.query", INFO, ["STDOUT"])
logger("org.apache.lucene.analysis.kr", WARN, ["STDOUT"])
logger("com.tistory.devyongsik", DEBUG, ["STDOUT"])
logger("com.tistory.devyongsik.admin.IndexFileManageServiceImpl", DEBUG, ["STDOUT"])

root(DEBUG, ["STDOUT"])