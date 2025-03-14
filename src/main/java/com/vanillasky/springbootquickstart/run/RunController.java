package com.vanillasky.springbootquickstart.run;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/runs") //get prefix mapping
public class RunController {

    //Controller basically delegate things to other class, itself does not do any logic operation
    private final RunRepository runRepository;

    public RunController(RunRepository runRepository) {
        this.runRepository = runRepository;
    }

    @GetMapping("")
    List<Run> findAll() {
        return runRepository.findAll();
    }

    @GetMapping("/{id}") // use placeholder {id} and @PathVariable
    Run findById(@PathVariable Integer id) {
        Optional<Run> run = runRepository.findById(id);
        if(run.isEmpty()) {
            throw new RunNotFoundException();
        }
        return run.get();
    }

    //post
    //once create will get a return code
    @ResponseStatus(HttpStatus.CREATED)
    // Tell controller the run is from the request body and pass to the repository
    @PostMapping("")
    void create(@Valid @RequestBody Run run) {
        //@valid helps us sanitized things
        runRepository.save(run);//save instead of creat
    }

    //put
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    void update(@RequestBody Run run, @PathVariable Integer id) {
        runRepository.save(run);//save instead of update
        //does not distinguish between insert and update.
    }

    //delete
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void delete(@PathVariable Integer id){
        runRepository.delete(runRepository.findById(id).get());
    }

    @GetMapping("/location/{location}")
    List<Run> findAllByLocation(@PathVariable String location) {
        return runRepository.findAllByLocation(location);
    }
}
