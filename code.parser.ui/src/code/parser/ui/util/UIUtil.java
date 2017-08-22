/**
 * 
 */
package code.parser.ui.util;

import java.io.File;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * @author dinghn
 *
 */
public abstract class UIUtil {
	public static void refresh(IContainer container, File outFile)
			throws CoreException {
		Path path = new Path(outFile.getAbsolutePath());
		IPath location = container.getLocation();
		if (location.isPrefixOf(path)) {
			IFile file = container.getFile(path.makeRelativeTo(location));
			file.refreshLocal(IResource.DEPTH_ZERO, null);
		}

	}
}
