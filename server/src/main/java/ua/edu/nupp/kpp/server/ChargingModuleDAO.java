package ua.edu.nupp.kpp.server;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChargingModuleDAO {
    public void add(ChargingModule module) throws SQLException {
        String sql = "INSERT INTO charging_module (station_id, port_number, port_type, power) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            bind(ps, module);
            ps.executeUpdate();
        }
    }
    public List<ChargingModule> getAll() throws SQLException {
        List<ChargingModule> modules = new ArrayList<>();
        String sql = "SELECT id, station_id, port_number, port_type, power FROM charging_module ORDER BY id";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) modules.add(map(rs));
        }
        return modules;
    }
    public ChargingModule getById(long id) throws SQLException {
        String sql = "SELECT id, station_id, port_number, port_type, power FROM charging_module WHERE id = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }
    public void update(ChargingModule module) throws SQLException {
        String sql = "UPDATE charging_module SET station_id = ?, port_number = ?, port_type = ?, power = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            bind(ps, module);
            ps.setLong(5, module.getId());
            ps.executeUpdate();
        }
    }
    public void delete(long id) throws SQLException {
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM charging_module WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }
    private void bind(PreparedStatement ps, ChargingModule module) throws SQLException {
        ps.setLong(1, module.getStationId());
        ps.setInt(2, module.getPortNumber());
        ps.setString(3, module.getPortType());
        ps.setDouble(4, module.getPower());
    }
    private ChargingModule map(ResultSet rs) throws SQLException {
        return new ChargingModule(rs.getLong("id"), rs.getLong("station_id"), rs.getInt("port_number"), rs.getString("port_type"), rs.getDouble("power"));
    }
}
