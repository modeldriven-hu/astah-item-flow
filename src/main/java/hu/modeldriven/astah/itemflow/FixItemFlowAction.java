package hu.modeldriven.astah.itemflow;


import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.ITransactionManager;
import com.change_vision.jude.api.inf.presentation.ILinkPresentation;
import com.change_vision.jude.api.inf.presentation.IPresentation;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;
import com.change_vision.jude.api.inf.view.IDiagramViewManager;

import javax.swing.*;
import java.util.Arrays;

public class FixItemFlowAction implements IPluginActionDelegate {

    public Object run(IWindow window) throws UnExpectedException {
        try {
            AstahAPI api = AstahAPI.getAstahAPI();
            ProjectAccessor projectAccessor = api.getProjectAccessor();
            IDiagramViewManager diagramViewManager = projectAccessor.getViewManager().getDiagramViewManager();

            ILinkPresentation connector = null;
            ILinkPresentation itemFlow = null;

            for (IPresentation presentation : diagramViewManager.getSelectedPresentations()) {

                if (presentation instanceof ILinkPresentation) {
                    ILinkPresentation selectedLinkPresentation = (ILinkPresentation) presentation;

                    if ("ItemFlow".equals(selectedLinkPresentation.getType())) {

                        itemFlow = selectedLinkPresentation;
                        connector = findConnector(itemFlow);

                        break;
                    }
                }
            }

            if (connector == null || itemFlow == null){
                JOptionPane.showMessageDialog(window.getParent(), "No item flow or connection found!", "Alert", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            modifyItemFlowPoints(window, projectAccessor, itemFlow, connector);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(window.getParent(), "Unexpected error has occurred.", "Alert", JOptionPane.ERROR_MESSAGE);
            throw new UnExpectedException();
        }

        return null;
    }

    private void modifyItemFlowPoints(IWindow window, ProjectAccessor projectAccessor, ILinkPresentation itemFlow, ILinkPresentation connector){

        ITransactionManager transactionManager = projectAccessor.getTransactionManager();

        try {

            transactionManager.beginTransaction();

            itemFlow.setAllPoints(connector.getAllPoints());

            transactionManager.endTransaction();

        } catch (Exception e) {
            transactionManager.abortTransaction();
            JOptionPane.showMessageDialog(window.getParent(), "Unexpected error has occurred.", "Alert", JOptionPane.ERROR_MESSAGE);
        }
    }

    private ILinkPresentation findConnector(ILinkPresentation itemFlow){
        return Arrays.asList(itemFlow.getSource().getLinks())
                .stream()
                .filter(link -> "Connector".equals(link.getType()))
                .findFirst().orElse(null);
    }


}
