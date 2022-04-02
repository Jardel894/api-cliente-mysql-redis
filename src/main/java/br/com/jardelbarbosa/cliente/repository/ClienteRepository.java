package br.com.jardelbarbosa.cliente.repository;

import br.com.jardelbarbosa.cliente.entity.Cliente;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteRepository extends CrudRepository<Cliente, Long > {

    public List<Cliente> findByNomeContainingIgnoreCase(String nome);
    public Cliente findByEmail(String email);
    public Cliente findByCpf(String cpf);

}