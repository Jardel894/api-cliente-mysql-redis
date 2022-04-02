package br.com.jardelbarbosa.cliente.repository;

import br.com.jardelbarbosa.cliente.document.ClienteRedis;
import org.springframework.data.repository.CrudRepository;

public interface ClienteRedisRepository extends CrudRepository<ClienteRedis, String> {
}
