package com.example.recipe_management_service.service;

import com.example.recipe_management_service.model.Program;
import com.example.recipe_management_service.model.Block;
import com.example.recipe_management_service.repository.ProgramRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProgramService implements ServiceTemplate<Program> {
    private final ProgramRepo programRepository;

    @Override
    public List<Program> getAll() {
        return programRepository.findAll();
    }

    @Override
    public Program getById(Long id) {
        return programRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No Program found with id: " + id));
    }

    @Override
    public Program create(Program program) {
        return programRepository.save(program);
    }

    @Override
    public Program update(Long id, Program program) {
        Program programToUpdate = programRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No Program found with id: " + id));
        programToUpdate.setName(program.getName());
        programToUpdate.setBlocks(program.getBlocks());
        return programRepository.save(programToUpdate);
    }

    @Override
    public boolean delete(Long id) {
        if (programRepository.existsById(id)) {
            programRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public void addBlockToProgram(Long programId, Block block) {
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new IllegalArgumentException("No Program found with id: " + programId));
        program.getBlocks().add(block);
        programRepository.save(program);
    }

    public void removeBlockFromProgram(Long programId, Long blockId) {
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new IllegalArgumentException("No Program found with id: " + programId));
        program.getBlocks().removeIf(block -> block.getBlockId().equals(blockId));
        programRepository.save(program);
    }
}
