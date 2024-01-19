package hu.modeldriven.astah.linefix;


import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.ITransactionManager;
import com.change_vision.jude.api.inf.exception.BadTransactionException;
import com.change_vision.jude.api.inf.presentation.ILinkPresentation;
import com.change_vision.jude.api.inf.presentation.IPresentation;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;
import com.change_vision.jude.api.inf.view.IDiagramViewManager;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class FixLineAction implements IPluginActionDelegate {

    public Object run(IWindow window) throws UnExpectedException {
        try {
            AstahAPI api = AstahAPI.getAstahAPI();
            ProjectAccessor projectAccessor = api.getProjectAccessor();
            IDiagramViewManager diagramViewManager = projectAccessor.getViewManager().getDiagramViewManager();

            ILinkPresentation connector = null;
            List<ILinkPresentation> itemFlows = new ArrayList<>();

            for (IPresentation presentation : diagramViewManager.getSelectedPresentations()) {

                if (presentation instanceof ILinkPresentation) {
                    ILinkPresentation selectedLinkPresentation = (ILinkPresentation) presentation;

                    for (ILinkPresentation link : selectedLinkPresentation.getSource().getLinks()) {

                        if (connector == null && "Connector".equals(link.getType())) {
                            connector = link;
                        }

                        if ("ItemFlow".equals(link.getType())) {
                            itemFlows.add(link);
                        }
                    }
                }
            }

            ITransactionManager transactionManager = projectAccessor.getTransactionManager();

            try {

                transactionManager.beginTransaction();

                if (connector != null && itemFlows.size() > 0) {
                    for (ILinkPresentation itemFlow : itemFlows) {
                        itemFlow.setAllPoints(connector.getAllPoints());
                    }
                }

                transactionManager.endTransaction();

            } catch (BadTransactionException e) {
                e.printStackTrace();
                transactionManager.abortTransaction();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(window.getParent(), "Unexpected error has occurred.", "Alert", JOptionPane.ERROR_MESSAGE);
            throw new UnExpectedException();
        }

        return null;
    }


}
