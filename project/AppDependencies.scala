import sbt.*

object AppDependencies {

  private val bootstrapVersion = "9.14.0"

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"                %% "bootstrap-frontend-play-30"              % bootstrapVersion,
    "uk.gov.hmrc"                %% "play-frontend-hmrc-play-30"              % "12.7.0",
    "org.typelevel"              %% "cats-core"                               % "2.13.0",
    "org.apache.commons"          % "commons-text"                            % "1.13.1"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatest"              %% "scalatest"                % "3.2.19",
    "uk.gov.hmrc"                %% "bootstrap-test-play-30"   % bootstrapVersion,
    "org.mockito"                 % "mockito-core"             % "5.18.0",
    "org.scalatestplus"          %% "mockito-5-12"             % "3.2.19.0",
    "org.scalacheck"             %% "scalacheck"               % "1.18.1",
    "org.scalatestplus"          %% "scalacheck-1-18"          % "3.2.19.0",
    "io.github.wolfendale"       %% "scalacheck-gen-regexp"    % "1.1.0",
    "org.jsoup"                   % "jsoup"                    % "1.21.1"
  ).map(_ % "test")

  def apply(): Seq[ModuleID] = compile ++ test
}
