/**
 * Flexmojos is a set of maven goals to allow maven users to compile, optimize and test Flex SWF, Flex SWC, Air SWF and Air SWC.
 * Copyright (C) 2008-2012  Marvin Froeder <marvin@flexmojos.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.rvin.mojo.flexmojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.MavenProjectHelper;

/**
 * 
 * Encapsulate the access to Maven API. Some times just to hide Java 5 warnings
 * 
 * @author tech35212
 * 
 */
public abstract class AbstractIrvinMojo extends AbstractMojo {

	/**
	 * The maven project.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;

	/**
	 * @parameter expression="${project.build}"
	 * @required
	 * @readonly
	 */
	protected Build build;

	/**
	 * @component
	 */
	protected MavenProjectHelper projectHelper;

	/**
	 * @component
	 */
	protected ArtifactFactory artifactFactory;

	/**
	 * @component
	 */
	protected ArtifactResolver resolver;

	/**
	 * @component
	 */
	protected ArtifactMetadataSource artifactMetadataSource;

	/**
	 * @component
	 */
	protected MavenProjectBuilder mavenProjectBuilder;

	/**
	 * @parameter expression="${localRepository}"
	 */
	protected ArtifactRepository localRepository;

	/**
	 * @parameter expression="${project.remoteArtifactRepositories}"
	 */
	@SuppressWarnings("unchecked")
	protected List remoteRepositories;

	public AbstractIrvinMojo() {
		super();
	}

	@SuppressWarnings("unchecked")
	protected List<Resource> getResources() {
		// I wanna maven on Java 5
		return build.getResources();
	}

	@SuppressWarnings("unchecked")
	protected Set<Artifact> getDependencyArtifacts()
			throws MojoExecutionException {
		ArtifactResolutionResult arr;
		try {
			arr = resolver
					.resolveTransitively(project.getDependencyArtifacts(),
							project.getArtifact(), remoteRepositories,
							localRepository, artifactMetadataSource);
		} catch (ArtifactResolutionException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (ArtifactNotFoundException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
		Set<Artifact> result = arr.getArtifacts();
		return result;
	}

	protected List<Artifact> getDependencyArtifacts(String scope)
			throws MojoExecutionException {
		if (scope == null)
			return null;

		List<Artifact> artifacts = new ArrayList<Artifact>();
		for (Artifact artifact : getDependencyArtifacts()) {
			if (scope.equals(artifact.getScope())) {
				artifacts.add(artifact);
			}
		}
		return artifacts;
	}

	protected Artifact getArtifact(Dependency dependency)
			throws MojoExecutionException {
		Artifact artifact = artifactFactory.createArtifactWithClassifier(
				dependency.getGroupId(), dependency.getArtifactId(), dependency
						.getVersion(), dependency.getType(), dependency
						.getClassifier());
		resolveArtifact(artifact);
		return artifact;
	}

	protected void resolveArtifact(Artifact artifact)
			throws MojoExecutionException {
		try {
			resolver.resolve(artifact, remoteRepositories, localRepository);
		} catch (ArtifactResolutionException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (ArtifactNotFoundException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	public final void execute() throws MojoExecutionException,
			MojoFailureException {
		setUp();
		run();
		tearDown();
	}

	protected abstract void setUp() throws MojoExecutionException,
			MojoFailureException;

	protected abstract void run() throws MojoExecutionException,
			MojoFailureException;

	protected abstract void tearDown() throws MojoExecutionException,
			MojoFailureException;

}