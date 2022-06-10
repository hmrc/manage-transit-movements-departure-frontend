package a11ySpecBase

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import uk.gov.hmrc.scalatestaccessibilitylinter.AccessibilityMatchers

trait A11ySpecBase  extends AnyWordSpec with Matchers with GuiceOneAppPerSuite with AccessibilityMatchers
