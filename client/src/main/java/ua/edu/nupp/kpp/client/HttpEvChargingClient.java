package ua.edu.nupp.kpp.client;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class HttpEvChargingClient {
    private static final String BASE = "http://localhost:8080/ev-charging-server";
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Station> getStations() {
        return Arrays.asList(read(get("/stations"), Station[].class));
    }

    public void addStation(Station s) {
        post("/stations", "action=add&name=" + enc(s.getName()) +
                "&address=" + enc(s.getAddress()) + "&status=" + enc(s.getStatus()));
    }

    public void updateStation(Station s) {
        post("/stations", "action=update&id=" + s.getId() + "&name=" + enc(s.getName()) +
                "&address=" + enc(s.getAddress()) + "&status=" + enc(s.getStatus()));
    }

    public void deleteStation(long id) {
        post("/stations", "action=delete&id=" + id);
    }

    public List<ChargingModule> getModules() {
        return Arrays.asList(read(get("/modules"), ChargingModule[].class));
    }

    public void addModule(ChargingModule m) {
        post("/modules", moduleForm("add", m));
    }

    public void updateModule(ChargingModule m) {
        post("/modules", moduleForm("update", m) + "&id=" + m.getId());
    }

    public void deleteModule(long id) {
        post("/modules", "action=delete&id=" + id);
    }

    public List<Incident> getIncidents() {
        return Arrays.asList(read(get("/incidents"), Incident[].class));
    }

    public void addIncident(Incident i) {
        post("/incidents", incidentForm("add", i));
    }

    public void updateIncident(Incident i) {
        post("/incidents", incidentForm("update", i) + "&id=" + i.getId());
    }

    public void changeIncidentState(long id, IncidentState state) {
        post("/incidents", "action=transition&id=" + id + "&state=" + state.name());
    }

    public void deleteIncident(long id) {
        post("/incidents", "action=delete&id=" + id);
    }

    public List<RepairTask> getTasks() {
        return Arrays.asList(read(get("/tasks"), RepairTask[].class));
    }

    public void addTask(RepairTask t) {
        post("/tasks", taskForm("add", t));
    }

    public void updateTask(RepairTask t) {
        post("/tasks", taskForm("update", t) + "&id=" + t.getId());
    }

    public void deleteTask(long id) {
        post("/tasks", "action=delete&id=" + id);
    }

    private String moduleForm(String action, ChargingModule m) {
        return "action=" + action + "&stationId=" + m.getStationId() +
                "&portNumber=" + m.getPortNumber() + "&portType=" + enc(m.getPortType()) +
                "&power=" + m.getPower();
    }

    private String incidentForm(String action, Incident i) {
        return "action=" + action + "&moduleId=" + i.getModuleId() +
                "&f1=" + i.getF1() + "&f2=" + i.getF2() + "&f3=" + i.getF3();
    }
    private String taskForm(String action, RepairTask t) {
        return "action=" + action + "&incidentId=" + t.getIncidentId() +
                "&description=" + enc(t.getDescription()) + "&plannedDate=" + enc(t.getPlannedDate());
    }

    private String get(String path) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + path)).GET().build();
        return send(req);
    }

    private String post(String path, String form) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + path))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(form)).build();
        return send(req);
    }

    private String send(HttpRequest req) {
        try {
            HttpResponse<String> r = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (r.statusCode() >= 400) throw new RuntimeException(r.body());
            return r.body();
        } catch (Exception e) {
            throw new RuntimeException("Помилка HTTP: " + e.getMessage(), e);
        }
    }

    private <T> T read(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (Exception e) {
            throw new RuntimeException("Помилка JSON", e);
        }
    }

    private String enc(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }
}
