import play.sbt.routes.RoutesKeys
import sbt.Def
import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion
import java.io.File

lazy val appName: String = "manage-transit-movements-departure-frontend"

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val root = (project in file("."))
  .enablePlugins(
    ScalaxbPlugin,
    PlayScala,
    SbtAutoBuildPlugin,
    SbtDistributablesPlugin
  )
  .disablePlugins(JUnitXmlReportPlugin) //Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .configs(A11yTest)
  .settings(inConfig(A11yTest)(org.scalafmt.sbt.ScalafmtPlugin.scalafmtConfigSettings): _*)
  .settings(DefaultBuildSettings.scalaSettings: _*)
  .settings(DefaultBuildSettings.defaultSettings(): _*)
  .settings(SbtDistributablesPlugin.publishingSettings: _*)
  .settings(inConfig(Test)(testSettings): _*)
  .settings(majorVersion := 0)
  .settings(scalaVersion := "2.13.8")
  .settings(headerSettings(A11yTest): _*)
  .settings(automateHeaderSettings(A11yTest))
  .settings(
    Compile / scalaxb / scalaxbXsdSource := new File("./conf/xsd"),
    Compile / scalaxb / scalaxbDispatchVersion := "1.1.3",
    Compile / scalaxb / scalaxbPackageName := "generated")
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
      "templates._",
      "views.utils.ViewUtils._"
    ),
    PlayKeys.playDefaultPort := 10120,
    ScoverageKeys.coverageExcludedFiles := "<empty>;Reverse.*;.*handlers.*;.*repositories.*;" +
      ".*BuildInfo.*;.*javascript.*;.*Routes.*;.*GuiceInjector;" +
      ".*ControllerConfiguration",
    ScoverageKeys.coverageExcludedPackages := ".*views.html.components.*;.*scalaxb.*;.*generated.*",
    ScoverageKeys.coverageMinimumStmtTotal := 90,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting  := true,
    ThisBuild / useSuperShell := false,
    scalacOptions ++= Seq(
      "-feature",
      "-language:implicitConversions",
      "-language:postfixOps",
      "-language:higherKinds",
      "-Wconf:src=routes/.*:s",
      "-Wconf:cat=unused-imports&src=html/.*:s",
      "-Wconf:src=src_managed/.*:s"
    ),
    libraryDependencies ++= AppDependencies(),
    retrieveManaged := true,
    update / evictionWarningOptions :=
      EvictionWarningOptions.default.withWarnScalaVersionEviction(false),
    resolvers ++= Seq(
      Resolver.jcenterRepo
    ),
    Concat.groups := Seq(
      "javascripts/application.js" -> group(Seq("javascripts/ctc.js"))
    ),
    uglifyCompressOptions := Seq("unused=false", "dead_code=false", "warnings=false"),
    Assets / pipelineStages := Seq(digest, concat, uglify),
    ThisBuild / useSuperShell := false,
    uglify / includeFilter := GlobFilter("application.js"),
    ThisBuild / scalafmtOnCompile := true
  )

lazy val testSettings: Seq[Def.Setting[_]] = Seq(
  fork := true,
  unmanagedResourceDirectories += baseDirectory.value / "test" / "resources",
  javaOptions ++= Seq(
    "-Dconfig.resource=test.application.conf"
  )
)
