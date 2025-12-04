package Controllers;
import Views.SystemView;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
public class SettingsControllers  implements MouseListener{
    
    private SystemView views;
    public SettingsControllers(SystemView views){
        this.views = views;
        
        this.views.jLabelVendedor.addMouseListener(this);
        this.views.jLabelJefe.addMouseListener(this);
        this.views.jLabelAdmin.addMouseListener(this);
    }
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
       
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if(e.getSource()== views.jLabelVendedor){
            views.jPanelVendedor.setBackground(new Color(0, 150, 136));
        }
        else if(e.getSource()== views.jLabelAdmin)
            views.jPanelAdmin.setBackground(new Color(0, 150, 136));
         
         else if(e.getSource()== views.jLabelJefe)
            views.jPanelJefe.setBackground(new Color(0, 150, 136));
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
           if(e.getSource()== views.jLabelVendedor){
            views.jPanelVendedor.setBackground(new Color(26,32,40));
        }
        else if(e.getSource()== views.jLabelAdmin)
            views.jPanelAdmin.setBackground(new Color(26,32,40));
        
         else if(e.getSource()== views.jLabelJefe)
            views.jPanelJefe.setBackground(new Color(26,32,40));
    }
    
}
