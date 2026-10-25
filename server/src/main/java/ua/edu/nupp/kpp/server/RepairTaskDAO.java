package ua.edu.nupp.kpp.server;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepairTaskDAO {
    public void add(RepairTask task) throws SQLException {
        String sql = "INSERT INTO repair_task (incident_id, description, planned_date) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            bind(ps, task);
            ps.executeUpdate();
        }
    }

    public List<RepairTask> getAll() throws SQLException {
        List<RepairTask> tasks = new ArrayList<>();
        String sql = "SELECT id, incident_id, description, planned_date FROM repair_task ORDER BY id";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) tasks.add(map(rs));
        }
        return tasks;
    }

    public RepairTask getById(long id) throws SQLException {
        String sql = "SELECT id, incident_id, description, planned_date FROM repair_task WHERE id = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    public void update(RepairTask task) throws SQLException {
        String sql = "UPDATE repair_task SET incident_id = ?, description = ?, planned_date = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            bind(ps, task);
            ps.setLong(4, task.getId());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM repair_task WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private void bind(PreparedStatement ps, RepairTask task) throws SQLException {
        ps.setLong(1, task.getIncidentId());
        ps.setString(2, task.getDescription());
        ps.setString(3, task.getPlannedDate());
    }

    private RepairTask map(ResultSet rs) throws SQLException {
        return new RepairTask(rs.getLong("id"), rs.getLong("incident_id"), rs.getString("description"), rs.getString("planned_date"));
    }
}
