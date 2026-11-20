package ua.edu.nupp.kpp.client;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.logging.Logger;

import java.nio.file.Path;

public class MainApp extends Application {
    private static final Logger LOG = Logger.getLogger(MainApp.class.getName());
    private final HttpEvChargingClient api = new HttpEvChargingClient();

    private final TableView<Station> stations = new TableView<>();
    private final TableView<ChargingModule> modules = new TableView<>();
    private final TableView<Incident> incidents = new TableView<>();
    private final TableView<RepairTask> tasks = new TableView<>();

    @Override
    public void start(Stage stage) {
        TabPane tabs = new TabPane(
                new Tab("Станції", stationPane()),
                new Tab("Модулі", modulePane()),
                new Tab("Інциденти", incidentPane()),
                new Tab("Завдання", taskPane())
        );
        tabs.getTabs().forEach(t -> t.setClosable(false));
        stage.setScene(new Scene(tabs, 1050, 650));
        stage.setTitle("Система ЕЗС (Клієнт)");
        stage.show();

        testCsvImport();
        refreshAll();
    }

    private Pane stationPane() {
        stations.getColumns().setAll(
                col("ID", "id"), col("Назва", "name"), col("Місто/Адреса", "address"), col("Статус", "status")
        );
        TextField name = new TextField(), address = new TextField(), status = new TextField();
        Button add = new Button("Додати"), update = new Button("Змінити"),
                delete = new Button("Видалити"), refresh = new Button("Оновити");

        add.setOnAction(e -> safe(() -> {
            api.addStation(new Station(null, name.getText(), address.getText(), status.getText()));
            refreshStations();
        }));

        update.setOnAction(e -> safe(() -> {
            Station s = selected(stations);
            s.setName(name.getText());
            s.setAddress(address.getText());
            s.setStatus(status.getText());
            api.updateStation(s);
            refreshStations();
        }));

        delete.setOnAction(e -> safe(() -> {
            api.deleteStation(selected(stations).getId());
            refreshAll();
        }));

        refresh.setOnAction(e -> refreshStations());

        stations.getSelectionModel().selectedItemProperty().addListener((o, a, s) -> {
            if (s != null) {
                name.setText(s.getName());
                address.setText(s.getAddress());
                status.setText(s.getStatus());
            }
        });

        return layout(stations, createForm(
                new HBox(8, add, update, delete, refresh),
                new Label("Назва"), name,
                new Label("Місто/Адреса"), address,
                new Label("Статус (напр. ACTIVE)"), status
        ));
    }

    private Pane modulePane() {
        modules.getColumns().setAll(
                col("ID", "id"), col("ID Станції", "stationId"), col("Номер порту", "portNumber"),
                col("Тип", "portType"), col("Потужність", "power")
        );
        TextField stationId = new TextField(), portNumber = new TextField(),
                portType = new TextField(), power = new TextField();

        Button add = new Button("Додати"), update = new Button("Змінити"),
                delete = new Button("Видалити"), refresh = new Button("Оновити");

        add.setOnAction(e -> safe(() -> {
            api.addModule(readModule(null, stationId, portNumber, portType, power));
            refreshModules();
        }));

        update.setOnAction(e -> safe(() -> {
            ChargingModule m = selected(modules);
            api.updateModule(readModule(m.getId(), stationId, portNumber, portType, power));
            refreshModules();
        }));

        delete.setOnAction(e -> safe(() -> {
            api.deleteModule(selected(modules).getId());
            refreshAll();
        }));

        refresh.setOnAction(e -> refreshModules());

        modules.getSelectionModel().selectedItemProperty().addListener((o, a, m) -> {
            if (m != null) {
                stationId.setText(String.valueOf(m.getStationId()));
                portNumber.setText(String.valueOf(m.getPortNumber()));
                portType.setText(m.getPortType());
                power.setText(String.valueOf(m.getPower()));
            }
        });

        return layout(modules, createForm(
                new HBox(8, add, update, delete, refresh),
                new Label("ID Станції"), stationId,
                new Label("Номер порту"), portNumber,
                new Label("Тип конектора (CCS/CHAdeMO)"), portType,
                new Label("Потужність (кВт)"), power
        ));
    }

    private ChargingModule readModule(Long id, TextField stationId, TextField portNum, TextField portType, TextField power) {
        return new ChargingModule(id, Long.parseLong(stationId.getText()),
                Integer.parseInt(portNum.getText()), portType.getText(),
                Double.parseDouble(power.getText()));
    }

    private Pane incidentPane() {
        incidents.getColumns().setAll(
                col("ID", "id"), col("ID Модуля", "moduleId"), col("Стан", "state"),
                col("F1", "f1"), col("F2", "f2"), col("F3", "f3"), col("I (Індекс)", "integralIndex")
        );
        TextField moduleId = new TextField(), f1 = new TextField(), f2 = new TextField(), f3 = new TextField();
        ComboBox<IncidentState> state = new ComboBox<>(FXCollections.observableArrayList(IncidentState.values()));

        Button add = new Button("Створити"), update = new Button("Змінити параметри"),
                change = new Button("Змінити стан"), delete = new Button("Видалити"), refresh = new Button("Оновити");

        add.setOnAction(e -> safe(() -> {
            api.addIncident(readIncident(null, moduleId, f1, f2, f3));
            refreshIncidents();
        }));

        update.setOnAction(e -> safe(() -> {
            api.updateIncident(readIncident(selected(incidents).getId(), moduleId, f1, f2, f3));
            refreshIncidents();
        }));

        change.setOnAction(e -> safe(() -> {
            api.changeIncidentState(selected(incidents).getId(), state.getValue());
            refreshIncidents();
        }));

        delete.setOnAction(e -> safe(() -> {
            api.deleteIncident(selected(incidents).getId());
            refreshAll();
        }));

        refresh.setOnAction(e -> refreshIncidents());

        incidents.getSelectionModel().selectedItemProperty().addListener((o, a, i) -> {
            if (i != null) {
                moduleId.setText(String.valueOf(i.getModuleId()));
                f1.setText(String.valueOf(i.getF1()));
                f2.setText(String.valueOf(i.getF2()));
                f3.setText(String.valueOf(i.getF3()));
            }
        });

        return layout(incidents, createForm(
                new HBox(8, add, update, change, delete, refresh),
                new Label("ID Модуля"), moduleId,
                new Label("F1 (Час очікування)"), f1,
                new Label("F2 (Помилки)"), f2,
                new Label("F3 (Скарги)"), f3,
                new Label("Новий стан життєвого циклу"), state
        ));
    }

    private Incident readIncident(Long id, TextField modId, TextField f1, TextField f2, TextField f3) {
        return new Incident(id, Long.parseLong(modId.getText()), null,
                Double.parseDouble(f1.getText()), Double.parseDouble(f2.getText()),
                Double.parseDouble(f3.getText()), 0.0);
    }

    private Pane taskPane() {
        tasks.getColumns().setAll(
                col("ID", "id"), col("ID Інциденту", "incidentId"),
                col("Опис", "description"), col("Дата", "plannedDate")
        );
        TextField incidentId = new TextField(), description = new TextField(), date = new TextField();
        Button add = new Button("Додати"), update = new Button("Змінити"),
                delete = new Button("Видалити"), refresh = new Button("Оновити");

        add.setOnAction(e -> safe(() -> {
            api.addTask(new RepairTask(null, Long.parseLong(incidentId.getText()), description.getText(), date.getText()));
            refreshTasks();
        }));

        update.setOnAction(e -> safe(() -> {
            api.updateTask(new RepairTask(selected(tasks).getId(), Long.parseLong(incidentId.getText()), description.getText(), date.getText()));
            refreshTasks();
        }));

        delete.setOnAction(e -> safe(() -> {
            api.deleteTask(selected(tasks).getId());
            refreshTasks();
        }));

        refresh.setOnAction(e -> refreshTasks());

        tasks.getSelectionModel().selectedItemProperty().addListener((o, a, t) -> {
            if (t != null) {
                incidentId.setText(String.valueOf(t.getIncidentId()));
                description.setText(t.getDescription());
                date.setText(t.getPlannedDate());
            }
        });

        return layout(tasks, createForm(
                new HBox(8, add, update, delete, refresh),
                new Label("ID Інциденту"), incidentId,
                new Label("Опис робіт"), description,
                new Label("Запланована дата (YYYY-MM-DD)"), date
        ));
    }

    private VBox createForm(HBox buttonBox, Node... elements) {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(170);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);

        for (int i = 0; i < elements.length; i += 2) {
            if (elements[i + 1] instanceof Control) {
                ((Control) elements[i + 1]).setMaxWidth(Double.MAX_VALUE);
            }
            grid.addRow(i / 2, elements[i], elements[i + 1]);
        }

        VBox form = new VBox(15, grid, buttonBox);
        form.setPadding(new Insets(15, 0, 0, 0));
        return form;
    }

    private Pane layout(TableView<?> table, Pane form) {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        SplitPane p = new SplitPane(table, form);
        p.setOrientation(Orientation.VERTICAL);
        p.setDividerPositions(0.60);

        BorderPane root = new BorderPane(p);
        root.setPadding(new Insets(10));
        return root;
    }

    private void refreshAll() {
        refreshStations(); refreshModules(); refreshIncidents(); refreshTasks();
    }

    private void refreshStations() { safe(() -> stations.setItems(FXCollections.observableArrayList(api.getStations()))); }
    private void refreshModules() { safe(() -> modules.setItems(FXCollections.observableArrayList(api.getModules()))); }
    private void refreshIncidents() { safe(() -> incidents.setItems(FXCollections.observableArrayList(api.getIncidents()))); }
    private void refreshTasks() { safe(() -> tasks.setItems(FXCollections.observableArrayList(api.getTasks()))); }

    private <T> T selected(TableView<T> table) {
        T v = table.getSelectionModel().getSelectedItem();
        if (v == null) throw new IllegalArgumentException("Виберіть запис у таблиці");
        return v;
    }

    private void safe(Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            LOG.warning(e.getMessage());
            showError(e);
        }
    }

    private void showError(Throwable e) {
        Alert d = new Alert(Alert.AlertType.ERROR, e == null ? "Невідома помилка" : e.getMessage(), ButtonType.OK);
        d.showAndWait();
    }

    private <S, T> TableColumn<S, T> col(String title, String property) {
        TableColumn<S, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>(property));
        return c;
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void testCsvImport() {
        try {
            Path file = Path.of("sample-data", "stations_modules.csv");
            if (!java.nio.file.Files.exists(file)) {
                LOG.warning("Файл CSV не знайдено: " + file.toAbsolutePath());
                return;
            }
            java.util.List<String> lines = java.nio.file.Files.readAllLines(file);
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;
                String[] p = line.split(";");
                if (p.length == 7) {
                    String stationName = p[1].trim();
                    String stationAddress = p[2].trim();
                    String stationStatus = p[3].trim();
                    int portNumber = Integer.parseInt(p[4].trim());
                    String portType = p[5].trim();
                    double power = Double.parseDouble(p[6].trim());
                    Station s = new Station(null, stationName, stationAddress, stationStatus);
                    api.addStation(s);

                    java.util.List<Station> currentStations = api.getStations();
                    Long actualStationId = currentStations.stream()
                            .filter(st -> stationName.equals(st.getName()))
                            .map(Station::getId)
                            .reduce((first, second) -> second)
                            .orElse(null);

                    if (actualStationId != null) {
                        ChargingModule m = new ChargingModule(null, actualStationId, portNumber, portType, power);
                        api.addModule(m);
                    }
                } else {
                    LOG.warning("Невірна кількість полів у рядку CSV №" + (i + 1));
                }
            }
            System.out.println("CSV успішно імпортовано!");
        } catch (Exception e) {
            LOG.warning("Помилка імпорту CSV: " + e.getMessage());
            showError(e);
        }
    }
}