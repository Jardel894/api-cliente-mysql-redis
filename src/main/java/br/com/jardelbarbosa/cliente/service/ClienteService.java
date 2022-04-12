package br.com.jardelbarbosa.cliente.service;

import br.com.jardelbarbosa.cliente.document.ClienteRedis;
import br.com.jardelbarbosa.cliente.dto.ClienteRequestDTO;
import br.com.jardelbarbosa.cliente.dto.ClienteResponseDTO;
import br.com.jardelbarbosa.cliente.entity.Cliente;
import br.com.jardelbarbosa.cliente.repository.ClienteRedisRepository;
import br.com.jardelbarbosa.cliente.repository.ClienteRepository;
import br.com.jardelbarbosa.cliente.utils.TextoUltils;
import jdk.jfr.Enabled;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Log4j2
@Service
@EnableScheduling
public class ClienteService {

    private final int MINUTO = 1000 * 60;
    private final int MINUTOS = MINUTO * 1;
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ClienteRedisRepository clienteRedisRepository;

    @Autowired
    private ModelMapper modelMapper;

    @CacheEvict(value = "clientes", allEntries = true)
    public ClienteResponseDTO criar(ClienteRequestDTO clienteRequestDTO) {

        Cliente cliente = convertCliente(clienteRequestDTO);

        Cliente clienteSalvo = clienteRepository.save(cliente);

        return convertClienteResponseDTO(clienteSalvo);
    }

    @Cacheable("clientes")
    public List<ClienteResponseDTO> listarClientes(String nome) {

        List<Cliente> clienteList = null;

        if (nome == null) {
            clienteList = (List<Cliente>) clienteRepository.findAll();
        } else {
            clienteList = (List<Cliente>) clienteRepository.findByNomeContainingIgnoreCase(nome);
        }

        Collections.sort(clienteList, Comparator.comparing(Cliente::getNome));

        List<ClienteResponseDTO> clienteResponseDTOList = new ArrayList<>();

        clienteList.forEach(cliente -> {
            ClienteResponseDTO clienteResponseDTO = convertClienteResponseDTO(cliente);
            clienteResponseDTOList.add(clienteResponseDTO);
        });
        return clienteResponseDTOList;
    }

    @Cacheable("clientes")
    public ClienteResponseDTO consultarPorCpf(String cpf) {
        cpf = TextoUltils.removeEspecialCaracter(cpf);
        if (!TextoUltils.contemTexto(cpf)) {
            Cliente cliente = clienteRepository.findByCpf(cpf);
            return modelMapper.map(cliente, ClienteResponseDTO.class);
        }
        return null;
    }

    @CacheEvict(value = "clientes", allEntries = true)
    public void deletarCliente(String email) throws Exception {
        Cliente cliente = clienteRepository.findByEmail(email);
        if (cliente == null) {
            throw new Exception("Cliente não encontrado. Verifique o Email digitado.");
        }
        clienteRepository.deleteById(cliente.getId());
    }

    @Cacheable("clientes")
    public ClienteResponseDTO consultarPorEmail(String email) {
        Cliente cliente = clienteRepository.findByEmail(email);
        return modelMapper.map(cliente, ClienteResponseDTO.class);
    }

    @CacheEvict(value = "clientes", allEntries = true)
    public void atualizarCliente(ClienteRequestDTO clienteRequestDTO, String email) throws Exception {
        Cliente cliente = clienteRepository.findByEmail(email);
        if (cliente == null) {
            throw new Exception("Cliente não encontrado.");
        }
        modelMapper.map(clienteRequestDTO, cliente);
        clienteRepository.save(cliente);
    }
    @Scheduled(fixedDelay = MINUTOS)
    public void sicronizarClienteBancoDados(){
        List<ClienteRedis> clienteRedisList = (List<ClienteRedis>) clienteRedisRepository.findAll();

        if (CollectionUtils.isEmpty(clienteRedisList)) {
            log.info("Lista de clientes Redis nula ou invalida.");
        } else{
            List<Cliente> clienteList = new ArrayList<>();
            clienteRedisList.stream().forEach(
                    clienteRedis -> {
                        log.info(clienteRedis);
                        Cliente cliente = modelMapper.map(clienteRedis, Cliente.class);
                        clienteList.add(cliente);
                    }
            );
            clienteRepository.saveAll(clienteList);
            clienteRedisRepository.deleteAll(clienteRedisList);
        }

    }


    private Cliente convertCliente(ClienteRequestDTO clienteRequestDTO) {
        clienteRequestDTO.setCpf(TextoUltils.removeEspecialCaracter(clienteRequestDTO.getCpf()));
        Cliente cliente = modelMapper.map(clienteRequestDTO, Cliente.class);
        setNomeSobreNome(clienteRequestDTO, cliente);
        return cliente;
    }

    //Passagem de valor dos Objetos por referencia
    private void setNomeSobreNome(ClienteRequestDTO clienteRequestDTO, Cliente cliente) {
        int delimitadorIndex = clienteRequestDTO.getNomeCompleto().indexOf(" ");
        String nome = clienteRequestDTO.getNomeCompleto().substring(0, delimitadorIndex);
        String sobrenome = clienteRequestDTO.getNomeCompleto().substring(delimitadorIndex + 1, clienteRequestDTO.getNomeCompleto().length());

        cliente.setNome(nome);
        cliente.setSobrenome(sobrenome);
    }

    private ClienteResponseDTO convertClienteResponseDTO(Cliente clienteSalvo) {
        ClienteResponseDTO clienteResponseDTO = modelMapper.map(clienteSalvo, ClienteResponseDTO.class);

        clienteResponseDTO.setNomeCompleto(clienteSalvo.getNome() + " " + clienteSalvo.getSobrenome());
        clienteResponseDTO.setEnderecoEletronico(clienteSalvo.getEmail());

        return clienteResponseDTO;
    }

}