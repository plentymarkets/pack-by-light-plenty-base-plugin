package plentybase.plugin.hue.extensions;

import com.plentymarkets.tool.core.event.BaseEventListener;
import com.plentymarkets.tool.core.event.EventBean;
import com.plentymarkets.tool.plugins.api.extensions.EventExtension;
import org.pf4j.Extension;
import plentybase.plugin.hue.PluginStart;
import plentybase.plugin.hue.context.Context;
import plentybase.plugin.hue.controller.Controller;

import java.util.HashMap;
import java.util.Map;

@Extension
public class EventListenerExtension implements EventExtension {
    private static Controller controller = PluginStart.getController();
    @Override

    public Map<String, BaseEventListener> getEventListeners() {

        BaseEventListener scan = new BaseEventListener() {
            @Override
            public void fireEvent(EventBean eventBean) {
                Context.getLogger().info("onScan Triggered");
                controller.onScan(eventBean);
            }
        };

        BaseEventListener end = new BaseEventListener() {
            @Override
            public void fireEvent(EventBean eventBean) {
                controller.OnProcessEnd();
            }
        };

        BaseEventListener start = new BaseEventListener() {
            @Override
            public void fireEvent(EventBean eventBean) {
                controller.OnProcessStart();
            }
        };
        Map<String, BaseEventListener> eventListeners = new HashMap<>();
        eventListeners.put("registeritems/scan", scan);
        eventListeners.put("process/start", start);
        eventListeners.put("process/stop", end);
        return eventListeners;
    }

}
