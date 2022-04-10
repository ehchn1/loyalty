package com.mycompany.loyalty.merchant;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1.0/merchants")
public class MerchantController {

    private final MerchantRepository merchantRepository;

    public MerchantController(MerchantRepository merchantRepository) {
      this.merchantRepository = merchantRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Merchant> one(@PathVariable String id) {
        Optional<Merchant> merchant = merchantRepository.findById(id);
        if (merchant.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(merchant.get());
    }

    @GetMapping
    public ResponseEntity<List<Merchant>> list(
            @RequestParam int pageNo,
            @RequestParam int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Merchant> list = merchantRepository.findAll(pageable);
        return ResponseEntity.ok(list.getContent());
    }

    @PostMapping
    public ResponseEntity<Merchant> create(@RequestBody Merchant merchant) {
        Merchant saved = merchantRepository.save(merchant);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(saved.getId()).toUri()).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Merchant> update(@RequestBody Merchant merchant, @PathVariable String id) {
        if (merchantRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        merchant.setId(id);
        Merchant saved = merchantRepository.save(merchant);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Merchant> delete(@PathVariable String id) {
        if (merchantRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        merchantRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
