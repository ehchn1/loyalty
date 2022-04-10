package com.mycompany.loyalty.offer;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
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
@RequestMapping("/api/v1.0/offers")
public class OfferController {

    private final OfferRepository offerRepository;

    public OfferController(OfferRepository offerRepository) {
      this.offerRepository = offerRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Offer> one(@PathVariable String id) {
        Optional<Offer> offer = offerRepository.findById(id);
        if (offer.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(offer.get());
    }

    @GetMapping
    public ResponseEntity<List<Offer>> list(
            @RequestParam int pageNo,
            @RequestParam int pageSize,
            @RequestParam(required = false) String mode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Offer> list = Page.empty();
        if (StringUtils.equals(mode, "pending")) {
            list = offerRepository.findByEndDateAfter(date, pageable);
        }
        else if (StringUtils.equals(mode, "pass")) {
            list = offerRepository.findByStartDateBefore(date, pageable);
        }
        else {
            list = offerRepository.findAllByDate(date, pageable);
        }
        return ResponseEntity.ok(list.getContent());
    }

    @PostMapping
    public ResponseEntity<Offer> create(@RequestBody Offer offer) {
        Offer saved = offerRepository.save(offer);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(saved.getId()).toUri()).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Offer> update(@RequestBody Offer offer, @PathVariable String id) {
        if (offerRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        offer.setId(id);
        Offer saved = offerRepository.save(offer);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Offer> delete(@PathVariable String id) {
        if (offerRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        offerRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
