import play.sbt.routes.RoutesKeys
import sbt.Def
import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

lazy val appName: String = "manage-transit-movements-departure-frontend"

val silencerVersion = "1.7.9"

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val root = (project in file("."))
  .enablePlugins(
    PlayScala,
    SbtAutoBuildPlugin,
    SbtDistributablesPlugin
  )
  .disablePlugins(JUnitXmlReportPlugin) //Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(itSettings): _*)
  .settings(inConfig(IntegrationTest)(org.scalafmt.sbt.ScalafmtPlugin.scalafmtConfigSettings): _*)
  .settings(DefaultBuildSettings.scalaSettings: _*)
  .settings(DefaultBuildSettings.defaultSettings(): _*)
  .settings(SbtDistributablesPlugin.publishingSettings: _*)
  .settings(inConfig(Test)(testSettings): _*)
  .settings(majorVersion := 0)
  .settings(scalaVersion := "2.12.15")
  .settings(headerSettings(IntegrationTest): _*)
  .settings(automateHeaderSettings(IntegrationTest))
  .settings(
    name := appName,
    RoutesKeys.routesImport ++= Seq("models._", "models.OptionBinder._"),
    TwirlKeys.templateImports ++= Seq(
      "play.twirl.api.HtmlFormat",
      "play.twirl.api.HtmlFormat._",
      "uk.gov.hmrc.govukfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.helpers._",
      "models.Mode",
      "controllers.routes._",
      "views.html.helper.CSPNonce",
      "viewModels.{InputSize, LabelSize, LegendSize}",
      "templates._"
    ),
    PlayKeys.playDefaultPort := 10120,
    ScoverageKeys.coverageExcludedFiles := "<empty>;Reverse.*;.*handlers.*;.*repositories.*;" +
      ".*BuildInfo.*;.*javascript.*;.*Routes.*;.*GuiceInjector;" +
      ".*ControllerConfiguration",
    ScoverageKeys.coverageExcludedPackages := ".*views.html.components.*",
    ScoverageKeys.coverageMinimumStmtTotal := 90,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting  := true,
    ThisBuild /useSuperShell            := false,
    scalacOptions ++= Seq(
      "-feature",
      "-language:implicitConversions",
      "-language:postfixOps",
      "-language:higherKinds",
      "-Ypartial-unification"
    ),
    libraryDependencies ++= AppDependencies(),
    retrieveManaged                        := true,
    update / evictionWarningOptions   :=
      EvictionWarningOptions.default.withWarnScalaVersionEviction(false),
    resolvers ++= Seq(
      Resolver.bintrayRepo("hmrc", "releases"),
      Resolver.jcenterRepo
    ),
    Concat.groups := Seq(
      "javascripts/application.js" -> group(Seq("javascripts/ctc.js", "javascripts/accessible-autocomplete.js", "javascripts/autocomplete.js"))
    ),
    uglifyCompressOptions := Seq("unused=false", "dead_code=false", "warnings=false"),
    Assets / pipelineStages := Seq(digest, concat, uglify),
    ThisBuild / useSuperShell := false,
    uglify / includeFilter := GlobFilter("application.js"),
    ThisBuild / scalafmtOnCompile := true
  )
  .settings(
    // ***************
    // Use the silencer plugin to suppress warnings
    scalacOptions ++= Seq(
      "-feature",
      "-P:silencer:pathFilters=routes;views"
    ),
    libraryDependencies ++= Seq(
      compilerPlugin(
        "com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.full
      ),
      "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.full
    )
    // ***************
  )

lazy val testSettings: Seq[Def.Setting[_]] = Seq(
  fork := true,
  unmanagedResourceDirectories += baseDirectory.value / "test" / "resources",
  javaOptions ++= Seq(
    "-Dconfig.resource=test.application.conf"
  )
)

lazy val itSettings = Defaults.itSettings ++ Seq(
  unmanagedSourceDirectories := Seq(
    baseDirectory.value / "it"
  ),
  unmanagedResourceDirectories := Seq(
    baseDirectory.value / "it" / "resources"
  ),
  parallelExecution := false,
  fork              := true,
  javaOptions ++= Seq(
    "-Dconfig.resource=it.application.conf"
  )
)
