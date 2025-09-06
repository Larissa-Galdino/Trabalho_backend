package com.example.LivrosAPP.repository;

import com.example.LivrosAPP.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LivroRepository extends JpaRepository<Livro,String> {
    List<Livro>findByNome(String nome);
}
