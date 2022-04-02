package br.com.jardelbarbosa.cliente.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@Builder
@RedisHash("Cliente")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClienteRedis {

        @Id
        private String cpf;

        private String nome;

        private String sobrenome;

        private String email;

        private String ddd;


        private String telefone;

}
