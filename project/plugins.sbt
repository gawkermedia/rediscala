resolvers := Seq(
	"Kinja Public" at sys.env.getOrElse("KINJA_PUBLIC_REPO", "https://kinjajfrog.jfrog.io/kinjajfrog/sbt-virtual"),
	"sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"
)

val credentialsFile = Path.userHome / ".ivy2" / ".kinja-artifactory.credentials"

credentials ++= (if (credentialsFile.exists) {
	Seq(Credentials(credentialsFile))
} else {
	sys.env.get("KINJA_JFROG_PASSWORD")
		.map(Credentials("Artifactory Realm", "kinjajfrog.jfrog.io", "kinja", _)).toSeq
})

addSbtPlugin("pl.project13.scala" % "sbt-jmh" % "0.3.7")

addSbtPlugin("com.kinja.sbtplugins" %% "kinja-build-plugin" % "3.2.7")
