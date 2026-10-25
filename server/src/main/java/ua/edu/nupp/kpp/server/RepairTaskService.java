package ua.edu.nupp.kpp.server;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class RepairTaskService {
    private static final Logger LOG = Logger.getLogger(RepairTaskService.class.getName());
    private final RepairTaskDAO dao = new RepairTaskDAO();
    private final IncidentDAO incidentDAO = new IncidentDAO();

    public List<RepairTask> getAll() throws SQLException {
        return dao.getAll();
    }
    public RepairTask getById(long id) throws SQLException {
        return dao.getById(id);
    }
    public void add(RepairTask t) throws SQLException {
        validate(t);
        requireIncident(t.getIncidentId());
        dao.add(t);
        LOG.info("Додано завдання на ремонт");
    }
    public void update(RepairTask t) throws SQLException {
        validate(t);
        requireIncident(t.getIncidentId());
        dao.update(t);
        LOG.info("Оновлено завдання на ремонт");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено завдання на ремонт");
    }

    static void validate(RepairTask t) {
        if (t == null || t.getIncidentId() <= 0 || blank(t.getDescription())) {
            throw new IllegalArgumentException("Некоректні дані завдання на ремонт");
        }
    }

    private void requireIncident(long id) throws SQLException {
        if (incidentDAO.getById(id) == null) {
            throw new IllegalArgumentException("Інцидент не знайдено");
        }
    }

    private static boolean blank(String v) {
        return v == null || v.isBlank();
    }
}
