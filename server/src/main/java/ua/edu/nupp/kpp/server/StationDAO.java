package ua.edu.nupp.kpp.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StationDAO {
    public void add(Station station) throws SQLException {
        String sql = "INSERT INTO station (name, address, status) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            bind(ps, station);
            ps.executeUpdate();
        }
    }

    public List<Station> getAll() throws SQLException {
        List<Station> stations = new ArrayList<>();
        String sql = "SELECT id, name, address, status FROM station ORDER BY id";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) stations.add(map(rs));
        }
        return stations;
    }

    public Station getById(long id) throws SQLException {
        String sql = "SELECT id, name, address, status FROM station WHERE id = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    public void update(Station station) throws SQLException {
        String sql = "UPDATE station SET name = ?, address = ?, status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            bind(ps, station);
            ps.setLong(4, station.getId());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM station WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private void bind(PreparedStatement ps, Station station) throws SQLException {
        ps.setString(1, station.getName());
        ps.setString(2, station.getAddress());
        ps.setString(3, station.getStatus());
    }

    private Station map(ResultSet rs) throws SQLException {
        return new Station(rs.getLong("id"), rs.getString("name"), rs.getString("address"), rs.getString("status"));
    }
}
