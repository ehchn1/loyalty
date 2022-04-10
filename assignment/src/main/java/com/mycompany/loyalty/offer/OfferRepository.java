package com.mycompany.loyalty.offer;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OfferRepository extends JpaRepository<Offer, String> {

    @Query("select o from Offer o where :currentDate between o.startDate and o.endDate")
    Page<Offer> findAllByDate(@Param("currentDate") LocalDate currentDate, Pageable pageable);

    Page<Offer> findByEndDateAfter(LocalDate currentDate, Pageable pageable);

    Page<Offer> findByStartDateBefore(LocalDate currentDate, Pageable pageable);
}
