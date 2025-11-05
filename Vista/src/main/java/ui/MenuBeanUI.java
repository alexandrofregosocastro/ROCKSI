package ui;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;

@Named("menuBeanUI")
@SessionScoped
public class MenuBeanUI implements Serializable {
    private String activePage = "home";

    public String getActivePage() {
        return activePage;
    }

    public void setActivePage(String activePage) {
        this.activePage = activePage;
    }
}