package com.example.ec.explorecali.repo;

import com.example.ec.explorecali.domain.Difficulty;
import com.example.ec.explorecali.domain.Region;
import com.example.ec.explorecali.domain.Tour;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Collection;
import java.util.List;

public interface TourRepository extends CrudRepository<Tour,Integer> {
    Page<Tour> findByTourPackageCode(@Param("code")String code, Pageable pageable);
    Tour findById(@Param("id")int tourId);

	@Override
	@RestResource(exported=false)
	<S extends Tour> S save(S entity);

	@Override
	@RestResource(exported=false)
	<S extends Tour> Iterable<S> saveAll(Iterable<S> entities);

	@Override
	@RestResource(exported=false)
	void deleteById(Integer id);

	@Override
	@RestResource(exported=false)
	void delete(Tour entity);

	@Override
	@RestResource(exported=false)
	void deleteAll(Iterable<? extends Tour> entities);

	@Override
	@RestResource(exported=false)
	void deleteAll();
    
}
