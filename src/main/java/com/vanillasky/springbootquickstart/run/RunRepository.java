package com.vanillasky.springbootquickstart.run;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class RunRepository {

    private static final Logger log = LoggerFactory.getLogger(RunRepository.class);
    private final JdbcClient jdbcClient;

    public RunRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Run> findAll() {
        return jdbcClient.sql("SELECT * FROM Run")
                .query(Run.class)
                .list();
    }

    Optional<Run> findById(Integer id){
        return jdbcClient.sql("SELECT id,title,started_on,completed_on,miles,location FROM Run WHERE id = :id")
                .param("id", id)
                .query(Run.class)
                .optional();
    }

    public void create(Run run) {
        var updated = jdbcClient.sql("INSERT INTO Run(id,title,started_on,completed_on,miles,location) VALUES (?,?,?,?,?,?)")
                .params(List.of(run.id(),run.title(),run.startedOn(),run.completedOn(),run.miles(),run.location().toString()))
                .update();

        Assert.state(updated == 1, "Failed to update Run" + run.title());
    }

    //problem when update with give id in json then output error
    public void update(Run run, Integer id) {
        var updated = jdbcClient.sql("UPDATE run SET title = ?, started_on = ?, completed_on = ?,miles = ?,location = ?")
                .params(List.of(run.title(),run.startedOn(),run.completedOn(),run.miles(),run.location().toString(), id))
                .update();
    }

    public void delete(Integer id) {
        var updated = jdbcClient.sql("DELETE FROM run WHERE id = :id")
                .param("id", id)
                .update();

        Assert.state(updated == 1, "Failed to delete Run" + id);
    }

    public int count() { return jdbcClient.sql("select * from run").query().listOfRows().size(); }

    public void saveAll(List<Run> runs) { runs.stream().forEach(this::create);}

    public  List<Run> findByLocation(String location) {
        return jdbcClient.sql("select * from run where location = :location")
                .param("location", location)
                .query(Run.class)
                .list();
    }
    // The following are hardcore test
    //private List<Run> runs = new ArrayList<>();
//    //"/"
//    List<Run> findAll() {
//        return runs;
//    }
//
//    Optional<Run> findById(int id) {
//        return runs.stream()
//                .filter(run -> run.id() == id)
//                .findFirst();
//    }
//
//
//    void create(Run run) {
//        runs.add(run);
//    }
//
//    void update(Run run, Integer id) {
//        Optional<Run> existingRun = findById(id);
//        if (existingRun.isPresent()) {
//            runs.set(runs.indexOf(existingRun.get()), run);
//        }
//    }
//
//    void delete(Integer id) {
//        runs.removeIf(run -> run.id().equals(id));
//    }
//
//
//    //"run/id"
//    @PostConstruct
//    private void init() {
//        runs.add(new Run(1,
//                "Monday Morning Run",
//                LocalDateTime.now(),
//                LocalDateTime.now().plus(30, ChronoUnit.HOURS),
//                3,
//                Location.INDOOR
//        ));
//
//        runs.add(new Run(2,
//                "Wednesday Evening Run",
//                LocalDateTime.now(),
//                LocalDateTime.now().plus(60, ChronoUnit.HOURS),
//                6,
//                Location.INDOOR
//        ));
//    }
}
