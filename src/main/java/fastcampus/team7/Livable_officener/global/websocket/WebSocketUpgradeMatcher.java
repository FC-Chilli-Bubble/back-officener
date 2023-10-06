package fastcampus.team7.Livable_officener.global.websocket;

import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class WebSocketUpgradeMatcher implements RequestMatcher {

    @Override
    public boolean matches(HttpServletRequest request) {
        if (!request.getMethod().equals(HttpMethod.GET.name())) {
            return false;
        }
        String upgradeHeader = request.getHeader("Upgrade");
        if (!"websocket".equalsIgnoreCase(upgradeHeader)) {
            return false;
        }
        String uri = request.getRequestURI();
        if (!uri.matches("/api/chat/[0-9]+")) {
            return false;
        }
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap.size() != 1) {
            return false;
        }
        String[] tickets = parameterMap.get("ticket");
        return tickets != null && tickets.length == 1;
    }
}