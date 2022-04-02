package br.com.jardelbarbosa.cliente.service;

import br.com.jardelbarbosa.cliente.document.ClienteRedis;
import br.com.jardelbarbosa.cliente.dto.ClienteRequestDTO;
import br.com.jardelbarbosa.cliente.repository.ClienteRedisRepository;
import br.com.jardelbarbosa.cliente.utils.TextoUltils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteServiceRedis {

    @Autowired
    private ClienteRedisRepository clienteRedisRepository;

    @Autowired
    private ModelMapper modelMapper;


    public ClienteRedis salvar(ClienteRequestDTO clienteRequestDTO){
        ClienteRedis clienteRedis = convertClienteRedis(clienteRequestDTO);
        return clienteRedisRepository.save(clienteRedis);
    }

    public ClienteRedis convertClienteRedis(ClienteRequestDTO clienteRequestDTO) {
        clienteRequestDTO.setCpf(TextoUltils.removeEspecialCaracter(clienteRequestDTO.getCpf()));
        ClienteRedis clienteRedis = modelMapper.map(clienteRequestDTO, ClienteRedis.class);
        setNomeSobreNome(clienteRequestDTO, clienteRedis);
        return clienteRedis;
    }


    public void setNomeSobreNome(ClienteRequestDTO clienteRequestDTO, ClienteRedis clienteRedis) {
        int delimitadorIndex = clienteRequestDTO.getNomeCompleto().indexOf(" ");
        String nome = clienteRequestDTO.getNomeCompleto().substring(0, delimitadorIndex);
        String sobrenome = clienteRequestDTO.getNomeCompleto().substring(delimitadorIndex + 1, clienteRequestDTO.getNomeCompleto().length());

        clienteRedis.setNome(nome);
        clienteRedis.setSobrenome(sobrenome);


    }




    public List<ClienteRedis> sicronizarClienteBancoDados(){
        return (List<ClienteRedis>)clienteRedisRepository.findAll();
    }
}
