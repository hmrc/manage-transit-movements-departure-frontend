import sbt._
import play.core.PlayVersion.current

object AppDependencies {

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"                %% "logback-json-logger"             % "5.2.0",
    "uk.gov.hmrc"                %% "play-conditional-form-mapping"   % "1.11.0-play-28",
    "uk.gov.hmrc"                %% "bootstrap-frontend-play-28"      % "7.3.0",
    "uk.gov.hmrc"                %% "play-allowlist-filter"           % "1.1.0",
    "uk.gov.hmrc"                %% "play-frontend-hmrc"              % "3.24.0-play-28",
    "org.typelevel"              %% "cats-core"                       % "2.8.0",
    "com.chuusai"                %% "shapeless"                       % "2.3.9"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatest"              %% "scalatest"                % "3.2.12",
    "org.scalatestplus.play"     %% "scalatestplus-play"       % "5.1.0",
    "com.typesafe.play"          %% "play-test"                % current,
    "org.mockito"                 % "mockito-core"             % "4.8.0",
    "org.scalatestplus"          %% "mockito-4-5"              % "3.2.12.0",
    "org.scalacheck"             %% "scalacheck"               % "1.16.0",
    "org.scalatestplus"          %% "scalacheck-1-16"          % "3.2.12.0",
    "wolfendale"                 %% "scalacheck-gen-regexp"    % "0.1.1",
    "org.pegdown"                 % "pegdown"                  % "1.6.0",
    "org.jsoup"                   % "jsoup"                    % "1.15.3",
    "com.github.tomakehurst"      % "wiremock-standalone"      % "2.27.2",
    "com.vladsch.flexmark"        % "flexmark-all"             % "0.62.2"
  ).map(_ % "test")

  def apply(): Seq[ModuleID] = compile ++ test
}
