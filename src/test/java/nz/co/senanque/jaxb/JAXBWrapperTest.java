/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package nz.co.senanque.jaxb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.Test;
import org.jvnet.mjiip.v_2_2.XJC22Mojo;
import org.slf4j.LoggerFactory;
import org.sonatype.plexus.build.incremental.DefaultBuildContext;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

/**
 * @author Roger Parkinson
 *
 */
public class JAXBWrapperTest {

	/**
	 * Test method for {@link nz.co.senanque.maduradocs.MaduraDocsMojo#execute()}.
	 * @throws MojoFailureException 
	 * @throws MojoExecutionException 
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testExecute() throws MojoExecutionException, MojoFailureException, FileNotFoundException, IOException, XmlPullParserException {

		// Messing about with the logger ensures we don't get excessive logging. This just makes sure we are using
		// the logback-test.xml file without having to get maven move it into the classpath.
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
	    
	    try {
	      JoranConfigurator configurator = new JoranConfigurator();
	      configurator.setContext(context);
	      // Call context.reset() to clear any previous configuration, e.g. default 
	      // configuration. For multi-step configuration, omit calling context.reset().
	      context.reset(); 
	      configurator.doConfigure("src/test/resources/logback-test.xml");
	    } catch (JoranException je) {
	      // StatusPrinter will handle this
	    }
	    StatusPrinter.printInCaseOfErrorsOrWarnings(context);


	    // This is the actual test code.
		MavenXpp3Reader r = new MavenXpp3Reader();
		Model model = r.read(new FileReader(new File("pom.xml")));

		XJC22Mojo xjcMojo = new XJC22Mojo();
		xjcMojo.setAddTestCompileSourceRoot(true); // this does nothing because we aren't referencing the main project
		xjcMojo.setSchemaDirectory(new File("src/test/resources"));
		xjcMojo.setSchemaIncludes(new String[]{"sandbox.xsd"});
		xjcMojo.setGenerateDirectory(new File("generated-sources/xjc"));
		xjcMojo.setVerbose(false);
//		xjcMojo.setCleanPackageDirectories(false);
		xjcMojo.setForceRegenerate(true);
		xjcMojo.setRemoveOldOutput(false);
		List<String> args = new ArrayList<String>();
		args.add("-extension");
		args.add("-Xequals");
		args.add("-XtoString");
		args.add("-Xannotate");
		args.add("-XhashCode");
		args.add("-Xhyperjaxb3-ejb");
		args.add("-Xmadura-objects");
		args.add("-Xvalidator");
		xjcMojo.setArgs(args);
		xjcMojo.setProject(new MavenProject(model));
//		xjcMojo.setBuildContext(new DefaultBuildContext());
		xjcMojo.execute();
	}
}
