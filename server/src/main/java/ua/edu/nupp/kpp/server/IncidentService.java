package ua.edu.nupp.kpp.server;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class IncidentService {
    private static final Logger LOG = Logger.getLogger(IncidentService.class.getName());
    private final IncidentDAO dao = new IncidentDAO();
    private final ChargingModuleDAO moduleDAO = new ChargingModuleDAO();
    public List<Incident> getAll() throws SQLException {
        return dao.getAll();
    }
    public Incident getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(Incident incident) throws SQLException {
        validate(incident);
        requireModule(incident.getModuleId());
        double riskIndex = RiskCalculator.calculate(incident.getF1(), incident.getF2(), incident.getF3());
        incident.setIntegralIndex(riskIndex);
        incident.setState(IncidentState.REPORTED);
        dao.add(incident);
        LOG.info("Створено інцидент");
    }

    public void update(Incident incident) throws SQLException {
        validate(incident);
        requireModule(incident.getModuleId());
        Incident old = dao.getById(incident.getId());
        if (old == null) {
            throw new IllegalArgumentException("Інцидент не знайдено");
        }
        double riskIndex = RiskCalculator.calculate(incident.getF1(), incident.getF2(), incident.getF3());
        incident.setIntegralIndex(riskIndex);
        incident.setState(old.getState());
        dao.update(incident);
        LOG.info("Оновлено інцидент");
    }

    public void changeState(long id, IncidentState next) throws SQLException {
        Incident current = dao.getById(id);
        if (current == null) {
            throw new IllegalArgumentException("Інцидент не знайдено");
        }
        if (!isAllowedTransition(current.getState(), next)) {
            LOG.warning("Недопустимий перехід стану: " + current.getState() + " -> " + next);
            throw new IllegalArgumentException("Недопустимий перехід стану");
        }
        dao.updateState(id, next);
        LOG.info("Змінено стан: " + current.getState() + " -> " + next);
    }

    public static boolean isAllowedTransition(IncidentState from, IncidentState to) {
        return (from == IncidentState.REPORTED && to == IncidentState.DIAGNOSED)
                || (from == IncidentState.DIAGNOSED && to == IncidentState.REPAIR)
                || (from == IncidentState.REPAIR && to == IncidentState.RESTORED);
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено інцидент");
    }

    static void validate(Incident i) {
        if (i == null || i.getModuleId() <= 0) {
            throw new IllegalArgumentException("Некоректні основні дані інциденту");
        }
        if (i.getF1() < 0 || i.getF1() > 100 || i.getF2() < 0 || i.getF2() > 100 || i.getF3() < 0 || i.getF3() > 100) {
            throw new IllegalArgumentException("Фактори F1, F2 та F3 мають бути в діапазоні 0..100");
        }
    }

    private void requireModule(long id) throws SQLException {
        if (moduleDAO.getById(id) == null) {
            throw new IllegalArgumentException("Зарядний модуль не знайдено");
        }
    }
}
