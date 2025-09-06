package com.example.LivrosAPP.controller;

import com.example.LivrosAPP.model.Livro;
import com.example.LivrosAPP.repository.LivroRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("livros")
public class LivroController {

    private LivroRepository livroRepository;

    public LivroController(LivroRepository livroRepository){ this.livroRepository = livroRepository;}

    // --- GET por ID ---
    @GetMapping("/{id}")
    public ResponseEntity<Livro> obterPorId(@PathVariable("id") String id) {
        return livroRepository.findById(id)
               .map(ResponseEntity::ok) // 200 OK
               .orElse(ResponseEntity.notFound().build()); // 404 Not Found
    }

    // --- POST: 1 livro ---
    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody Livro livro) {
        if (livro.getNome() == null || livro.getNome().isEmpty()) {
            return ResponseEntity.badRequest().body("O compo 'nome' é obrigado"); //400
        }
        livro.setId(UUID.randomUUID().toString());
        Livro salvo = livroRepository.save(livro);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo); // 201 Created
    }

    // --- POST: lista de livros ---
    @PostMapping("/lista")
    public ResponseEntity<?> salvarLista(@RequestBody List<Livro> livros) {
        // Validação simples
        for (Livro livro : livros) {
            if (livro.getNome() == null || livro.getNome().isEmpty() ||
                    livro.getAutor() == null || livro.getAutor().isEmpty() ||
                    livro.getPreco() == null || livro.getPreco() < 0) {
                return ResponseEntity
                        .badRequest()
                        .body("Todos os livros devem ter nome, autor e preço válido."); // 400
            }
            // Gera ID único para cada livro
            livro.setId(UUID.randomUUID().toString());
        }

        // Salva todos os livros
        List<Livro> salvos = livroRepository.saveAll(livros);

        // Retorna 201 Created + lista de livros
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(salvos);
    }

    //
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable("id") String id){
        if (!livroRepository.existsById(id)) {
            return ResponseEntity.notFound().build(); // 404
        }
        livroRepository.deleteById(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @PutMapping("{id}")
    public ResponseEntity<?> atualizar (@PathVariable("id") String id,@RequestBody Livro livro){
        if (!livroRepository.existsById(id)) {
            return ResponseEntity.notFound().build(); // 404
        }
        if (livro.getNome() == null || livro.getNome().isEmpty()) {
            return ResponseEntity.badRequest().body("Nome inválido."); // 400
        }

        livro.setId(id);
        Livro atualizado = livroRepository.save(livro);
        return ResponseEntity.ok(atualizado); // 200 OK
    }

    @GetMapping
    public ResponseEntity<List<Livro>> buscar(@RequestParam(value = "nome", required = false) String nome) {
        List<Livro> livros;

        if (nome != null && !nome.isEmpty()) {
            livros = livroRepository.findByNome(nome);
        } else {
            livros = livroRepository.findAll();
        }

        if (livros.isEmpty()) {
            return ResponseEntity.notFound().build(); // 404 se não encontrar nenhum livro
        }

        return ResponseEntity.ok(livros); // 200 OK com a lista
    }
}
