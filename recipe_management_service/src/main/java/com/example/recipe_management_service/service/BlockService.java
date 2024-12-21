package com.example.recipe_management_service.service;

import com.example.recipe_management_service.model.Block;
import com.example.recipe_management_service.repository.BlockRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BlockService implements ServiceTemplate<Block> {
    private final BlockRepo blockRepository;

    @Override
    public List<Block> getAll() {
        return blockRepository.findAll();
    }

    @Override
    public Block getById(Long id) {
        return blockRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No Block found with id: " + id));
    }

    @Override
    public Block create(Block block) {
        return blockRepository.save(block);
    }

    @Override
    public Block update(Long id, Block block) {
        Block blockToUpdate = blockRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No Block found with id: " + id));

        blockToUpdate.updatePropertiesFrom(block);
        return blockRepository.save(blockToUpdate);
    }

    @Override
    public boolean delete(Long id) {
        if (blockRepository.existsById(id)) {
            blockRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
