package plentybase.plugin.hue.extensions;

import com.plentymarkets.tool.plugins.api.extensions.PopupMenuExtension;
import org.pf4j.Extension;
import plentybase.plugin.hue.context.Context;
import plentybase.plugin.hue.savePath.ApplicationSettings;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Locale;

@Extension
public class MenuExtension implements PopupMenuExtension {

    Locale currentLocale = Locale.getDefault();

    @Override
    public void buildMenu(Menu menu) {

        MenuItem item;

        if (currentLocale.getLanguage().equals("de")) {
            item = new MenuItem("Plugin-Ordner öffnen");
        }
        else
        {
            item = new MenuItem("Show plugin directory");
        }

        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().open(Context.getPluginDataPath().toFile());
                    } catch (IOException e1) {
                        Context.getLogger().error(e1.toString());
                    }
                }
            }
        });

        menu.add(item);
    }
}
