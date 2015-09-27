package com.moneyManagement.gui;

import com.moneyManagement.data.CUser;
import com.moneyManagement.gui.frame.CMainFrame;
import com.moneyManagement.gui.utils.CPropertyHolder;
import com.moneyManagement.gui.frame.CLoginFrame;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.swing.*;
import java.util.prefs.Preferences;

@Configuration
@ComponentScan(basePackages = {"com.moneyManagement.gui"})
public class CGUIContext {

    @Bean
    public CPropertyHolder<Boolean> useInternalFrames(){
        Preferences prefs = Preferences.userNodeForPackage(CLoginFrame.class);
        return new CPropertyHolder<>(prefs.getBoolean("use_internal_frames", true));
    }

    @Bean
    public CLoginFrame loginFrame(){
        return new CLoginFrame(){
            @Override
            public CMainFrame getMainFrame() {
                return CGUIContext.this.mainFrame();
            }
        };
    }

    @Bean
    public CPropertyHolder<CUser> mainUser(){
        return new CPropertyHolder<>();
    }

    @Bean
    @Lazy
    public CMainFrame mainFrame(){
        return new CMainFrame();
    }

    @Bean
    @Lazy
    public JDesktopPane desktopPane(){
        return new JDesktopPane();
    }
}
