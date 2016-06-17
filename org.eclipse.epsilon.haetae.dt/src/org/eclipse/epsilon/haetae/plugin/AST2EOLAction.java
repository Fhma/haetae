package org.eclipse.epsilon.haetae.plugin;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.common.parse.AST;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.ast2eol.context.Ast2EolContext;
import org.eclipse.epsilon.eol.metamodel.EOLElement;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class AST2EOLAction implements IObjectActionDelegate{

	private Shell shell;

	public AST2EOLAction() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(IAction action) {

		IWorkbenchWindow window =
			    PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			ISelection selection = window.getSelectionService().getSelection("org.eclipse.jdt.ui.PackageExplorer");

		
		IFile file = (IFile) ((IStructuredSelection) selection).getFirstElement();

		IPath path = file.getLocation();
		
		if (!path.getFileExtension().equals("eol")) {
			MessageDialog dialog = new MessageDialog(shell, "Haetae goes woof", null,
				    "AST2EOL converter only works for .eol files", MessageDialog.ERROR, new String[] { "OK" }, 0);
				int result = dialog.open();
		}
		else {
			EolModule eolModule = new EolModule();
			
			try {
				eolModule.parse(new File(path.toPortableString()));
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog dialog = new MessageDialog(shell, "Haetae goes woof", null,
					    "Unable to parse file, please ensure the .eol file does not contain syntax errors", MessageDialog.ERROR, new String[] { "OK" }, 0);
					int result = dialog.open();
			}

			Ast2EolContext context = new Ast2EolContext(eolModule);
			EOLElement dom = (EOLElement) context.getEolElementCreatorFactory().createEOLElement(eolModule.getAst(), null, context);
			
			ResourceSet resourceSet = new ResourceSetImpl();
			resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
			Resource resource = resourceSet.createResource(URI.createFileURI(new File(path.toPortableString() + ".model").getAbsolutePath()));
			resource.getContents().add(dom);
			try {
				resource.save(null);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				MessageDialog dialog = new MessageDialog(shell, "Haetae goes woof", null,
					    "Unable to save file", MessageDialog.ERROR, new String[] { "OK" }, 0);
					int result = dialog.open();
			}
			
			MessageDialog dialog = new MessageDialog(shell, "Haetae says:", null,
				    "Transformation completed, " + path.toFile().getName() + ".model saved to directory", MessageDialog.INFORMATION, new String[] { "OK" }, 0);
				int result = dialog.open();
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	
}
