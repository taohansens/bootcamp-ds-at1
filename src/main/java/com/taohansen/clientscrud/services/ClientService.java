package com.taohansen.clientscrud.services;

import com.taohansen.clientscrud.dto.ClientDTO;
import com.taohansen.clientscrud.entities.Client;
import com.taohansen.clientscrud.repositories.ClientRepository;
import com.taohansen.clientscrud.services.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository repository;

    @Transactional(readOnly = true)
    public Page<ClientDTO> findAllPaged(PageRequest pageRequest) {
        Page<Client> list = repository.findAll(pageRequest);
        return list.map(ClientDTO::new);
    }

    @Transactional(readOnly = true)
    public ClientDTO findById(Long id) {
        Optional<Client> obj = repository.findById(id);
        Client entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        return new ClientDTO(entity);
    }

    @Transactional
    public ClientDTO insert(ClientDTO dto) {
        Client entity = new Client();
        return fromEntity(dto, entity);
    }

    @Transactional
    public ClientDTO update(Long id, ClientDTO dto) {
        try {
            Client entity = repository.getOne(id);
            return fromEntity(dto, entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found to update");
        }
    }

    private ClientDTO fromEntity(ClientDTO dto, Client entity) {
        entity.setName(dto.getName());
        entity.setBirthDate(dto.getBirthDate());
        entity.setChildren(dto.getChildren());
        entity.setCpf(dto.getCpf());
        entity.setIncome(dto.getIncome());
        entity = repository.save(entity);
        return new ClientDTO(entity);
    }

    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Id not found to delete");
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity database violation");
        }
    }
}