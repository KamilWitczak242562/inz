package com.example.recipe_management_service.service;

import com.example.recipe_management_service.model.Program;
import com.example.recipe_management_service.repository.BlockRepo;
import com.example.recipe_management_service.repository.ProgramRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProgramService implements ServiceTemplate<Program> {
    private final ProgramRepo programRepository;
    private final BlockRepo blockRepo;

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
        validateBlockIds(program.getBlockIds());
        return programRepository.save(program);
    }

    @Override
    public Program update(Long id, Program program) {
        Program programToUpdate = programRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No Program found with id: " + id));
        programToUpdate.setName(program.getName());
        programToUpdate.setBlockIds(program.getBlockIds());
        validateBlockIds(program.getBlockIds());
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

    public void addBlockToProgram(Long programId, Long blockId) {
        if (!blockRepo.existsById(blockId)) {
            throw new IllegalArgumentException("No Block found with id: " + blockId);
        }
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new IllegalArgumentException("No Program found with id: " + programId));
        program.getBlockIds().add(blockId);
        programRepository.save(program);
    }

    public void removeBlockFromProgram(Long programId, Long blockId) {
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new IllegalArgumentException("No Program found with id: " + programId));
        program.getBlockIds().remove(blockId);
        programRepository.save(program);
    }

    private void validateBlockIds(List<Long> blockIds) {
        for (Long blockId : blockIds) {
            if (!blockRepo.existsById(blockId)) {
                throw new IllegalArgumentException("No Block found with id: " + blockId);
            }
        }
    }
}
