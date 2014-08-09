package net.imadz.performance

/**
 * Created by geek on 14-8-10.
 */
package object monitoring {
  implicit def covert(testName: String): MonitoringSessionBuilder = new MonitoringSessionBuilder(testName)
}
