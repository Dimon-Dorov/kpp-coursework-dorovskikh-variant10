package ua.edu.nupp.kpp.server;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class StationService {
    private static final Logger LOG = Logger.getLogger(StationService.class.getName());
    private final StationDAO dao = new StationDAO();
    public List<Station> getAll() throws SQLException {
        return dao.getAll();
    }
    public Station getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(Station s) throws SQLException {
        validate(s);
        dao.add(s);
        LOG.info("Додано станцію");
    }

    public void update(Station s) throws SQLException {
        validate(s);
        dao.update(s);
        LOG.info("Оновлено станцію");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено станцію");
    }

    static void validate(Station s) {
        if (s == null || blank(s.getName()) || blank(s.getAddress()) || blank(s.getStatus())) {
            throw new IllegalArgumentException("Усі поля станції є обов'язковими");
        }
    }

    private static boolean blank(String v) {
        return v == null || v.isBlank();
    }
}
