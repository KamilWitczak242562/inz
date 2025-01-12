package com.example.recipe_management_service.configuration;

import com.example.recipe_management_service.model.Program;
import com.example.recipe_management_service.repository.ProgramRepo;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;


@Component
@AllArgsConstructor
public class ProgramDataInitializer implements CommandLineRunner {
    private final ProgramRepo programRepository;

    @Override
    @Transactional
    public void run(String... args) {
        initPrograms();
    }

    private void initPrograms() {
        Program program1 = new Program();
        program1.setName("Program A");
        program1.setBlockIds(new ArrayList<>(Arrays.asList(1L, 2L, 3L)));
        programRepository.save(program1);

        Program program2 = new Program();
        program2.setName("Program B");
        program2.setBlockIds(new ArrayList<>(Arrays.asList(4L, 5L)));
        programRepository.save(program2);

        Program program3 = new Program();
        program3.setName("Program C");
        program3.setBlockIds(new ArrayList<>(Arrays.asList(6L, 7L)));
        programRepository.save(program3);

        Program program4 = new Program();
        program4.setName("Program D");
        program4.setBlockIds(new ArrayList<>(Arrays.asList(8L, 9L)));
        programRepository.save(program4);
    }
}