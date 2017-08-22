package code.parser.ui.popup.actions;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import code.parser.core.convertor.JavaCode2JsonConvertor;
import code.parser.core.util.FileUtil;
import code.parser.ui.util.UIUtil;

/**
 * GenerateJavaJson
 * 
 * @author dinghn
 *
 */
public class GenerateJavaJson implements IObjectActionDelegate {

	private Shell shell;
	private Object[] selects;

	/**
	 * Constructor for GenerateJavaJson.
	 */
	public GenerateJavaJson() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		if (selects == null) {
			return;
		}
		try {
			if (!(selects[0] instanceof IFile))
				return;
			IProject project = ((IFile) selects[0]).getProject();
			DirectoryDialog dialog = new DirectoryDialog(shell, SWT.SAVE);
			dialog.setText("code.parser.java.json");
			dialog.setMessage("please select the folder to save json");
			dialog.setFilterPath(project.getLocation().toOSString());
			String path = dialog.open();
			if (StringUtils.isEmpty(path))
				return;

			JavaCode2JsonConvertor convertor = new JavaCode2JsonConvertor();
			for (Object object : selects) {
				if (!(object instanceof IFile)) {
					continue;
				}
				IFile ifile = (IFile) object;
				String name = ifile.getName().replace(".java", ".json");
				File outFile = new File(path, name);
				String content = FileUtils.readFileToString(ifile.getLocation()
						.toFile());
				String data = convertor.convert(content);
				FileUtil.createFile(outFile);
				FileUtils.writeStringToFile(outFile, data);
				UIUtil.refresh(project, outFile);
			}

			MessageDialog.openInformation(shell, "Parser",
					"Generate java json was executed.");
		} catch (Exception e) {
			MessageDialog.openError(shell, "Parser",
					"Generate java json write Failed.\n" + e.toString());
		}

	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (!(selection instanceof IStructuredSelection))
			return;
		IStructuredSelection ss = (IStructuredSelection) selection;
		selects = ss.toArray();
	}

}
