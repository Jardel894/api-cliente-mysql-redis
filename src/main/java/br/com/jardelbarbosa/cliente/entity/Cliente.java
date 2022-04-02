package br.com.jardelbarbosa.cliente.entity;


import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@Entity // REPRESENTA UMA TABELA NO BANCO DE DADOS
@AllArgsConstructor
@NoArgsConstructor
@Table
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "sobrenome", nullable = false)
    private String sobrenome;

    @Column(nullable = false, unique = true ,length = 11)
    private String cpf;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 3)
    private String ddd;

    @Column(length = 50)
    private String telefone;

}
