package ua.edu.nupp.kpp.server;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class RepairTaskServlet extends HttpServlet {
    private final RepairTaskService service = new RepairTaskService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            writeJson(resp, service.getAll());
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            String action = req.getParameter("action");
            if (action == null) {
                throw new IllegalArgumentException("Не вказано операцію (action)");
            }

            if ("delete".equals(action)) {
                service.delete(id(req));
            } else {
                RepairTask value = new RepairTask(
                        "update".equals(action) ? id(req) : null,
                        Long.parseLong(req.getParameter("incidentId")),
                        req.getParameter("description"),
                        req.getParameter("plannedDate")
                );

                if ("add".equals(action)) {
                    service.add(value);
                } else if ("update".equals(action)) {
                    service.update(value);
                } else {
                    throw new IllegalArgumentException("Невідома операція: " + action);
                }
            }
            writeJson(resp, service.getAll());

        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    private void writeJson(HttpServletResponse resp, Object data) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), data);
    }

    private void writeError(HttpServletResponse resp, Exception e) throws IOException {
        boolean clientError = e instanceof IllegalArgumentException;
        int status = clientError
                ? HttpServletResponse.SC_BAD_REQUEST
                : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        String message = clientError
                ? (e.getMessage() == null ? "Некоректний запит" : e.getMessage())
                : "Внутрішня помилка сервера";
        resp.setStatus(status);
        writeJson(resp, Map.of("error", message));
    }

    private long id(HttpServletRequest req) {
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            throw new IllegalArgumentException("Параметр id обов'язковий для цієї операції");
        }
        return Long.parseLong(idParam);
    }
}
