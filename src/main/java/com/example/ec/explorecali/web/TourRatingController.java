package com.example.ec.explorecali.web;

import com.example.ec.explorecali.domain.Tour;
import com.example.ec.explorecali.domain.TourRating;
import com.example.ec.explorecali.domain.TourRatingPk;
import com.example.ec.explorecali.repo.TourRatingRepository;
import com.example.ec.explorecali.repo.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.AbstractMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

/**
 * Tour Rating Controller
 *
 * Created by Mary Ellen Bowman
 */
@RestController
@RequestMapping(path = "/tours/{tourId}/ratings")
public class TourRatingController {
    TourRatingRepository tourRatingRepository;
    TourRepository tourRepository;

    @Autowired
    public TourRatingController(TourRatingRepository tourRatingRepository, TourRepository tourRepository) {
        this.tourRatingRepository = tourRatingRepository;
        this.tourRepository = tourRepository;
    }

    protected TourRatingController() {

    }
    
    @RequestMapping(method=RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void createTourRating(@PathVariable(value="tourId") int tourId,@RequestBody @Validated RatingDto ratingDto ) {
    	Tour tour=verifyTour(tourId);
    	tourRatingRepository.save(new TourRating(new TourRatingPk(tour,ratingDto.getCustomerId()),ratingDto.getScore(),ratingDto.getComment()));
    }

    @RequestMapping(method=RequestMethod.GET)
    public Page<RatingDto> getAllRatings(@PathVariable(value="tourId") int tourId,Pageable pageable){
    	verifyTour(tourId);
    	Page<TourRating> tourPage=tourRatingRepository.findByPkTourId(tourId,pageable);
    	List<RatingDto> ratingDtoList=tourPage.getContent().stream().map(tourRating->toDto(tourRating)).collect(Collectors.toList());
    	return new PageImpl<RatingDto>(ratingDtoList,pageable,tourPage.getTotalPages());
    }
    @RequestMapping(method=RequestMethod.GET,path="/average")
    public AbstractMap.SimpleEntry<String, Double> getAverage(@PathVariable(value="tourId")int tourId){
    	verifyTour(tourId);
    	List<TourRating> tourRating=tourRatingRepository.findByPkTourId(tourId);
    	OptionalDouble average=tourRating.stream().mapToInt(TourRating::getScore).average();
    	return new AbstractMap.SimpleEntry<String, Double>("average", average.isPresent()?average.getAsDouble():null);
    }
    /**
     * Convert the TourRating entity to a RatingDto
     *
     * @param tourRating
     * @return RatingDto
     */
    private RatingDto toDto(TourRating tourRating) {
        return new RatingDto(tourRating.getScore(), tourRating.getComment(), tourRating.getPk().getCustomerId());
    }

    @RequestMapping(method=RequestMethod.PUT)
    public RatingDto updateTourRating(@PathVariable(value="tourId")int tourId,@RequestBody @Validated RatingDto ratingDto){
    	
    TourRating tourRating=verifyTourRating(tourId,ratingDto.getCustomerId());
    tourRating.setScore(ratingDto.getScore());
    tourRating.setComment(ratingDto.getComment());
    return toDto(tourRatingRepository.save(tourRating));
    	
    	
    }
    @RequestMapping(method=RequestMethod.PATCH)
    public RatingDto updatePatchTourRating(@PathVariable(value="tourId")int tourId,@RequestBody @Validated RatingDto ratingDto){
    	
    TourRating tourRating=verifyTourRating(tourId,ratingDto.getCustomerId());
    if(ratingDto.getScore()!=null)
    tourRating.setScore(ratingDto.getScore());
    if(ratingDto.getComment()!=null)
    tourRating.setComment(ratingDto.getComment());
    return toDto(tourRatingRepository.save(tourRating));
    	
    	
    }
    
    @RequestMapping(method=RequestMethod.DELETE,path="/{customerId}")
    public void deleteTourRating(@PathVariable(value="tourId")int tourId,@PathVariable(value="customerId")int customerId){
    	
    TourRating tourRating=verifyTourRating(tourId,customerId);
    tourRatingRepository.delete(tourRating);
    
    	
    }
    /**
     * Verify and return the TourRating for a particular tourId and Customer
     * @param tourId
     * @param customerId
     * @return the found TourRating
     * @throws NoSuchElementException if no TourRating found
     */
    private TourRating verifyTourRating(int tourId, int customerId) throws NoSuchElementException {
        TourRating rating = tourRatingRepository.findByPkTourIdAndPkCustomerId(tourId, customerId);
        if (rating == null) {
            throw new NoSuchElementException("Tour-Rating pair for request("
                    + tourId + " for customer" + customerId);
        }
        return rating;
    }

    /**
     * Verify and return the Tour given a tourId.
     *
     * @param tourId
     * @return the found Tour
     * @throws NoSuchElementException if no Tour found.
     */
    private Tour verifyTour(int tourId) throws NoSuchElementException {
        Tour tour=tourRepository.findById(tourId);
        if (tour == null) {
            throw new NoSuchElementException("Tour does not exist " + tourId);
        }
        return tour;
    }

    /**
     * Exception handler if NoSuchElementException is thrown in this Controller
     *
     * @param ex
     * @return Error message String.
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public String return400(NoSuchElementException ex) {
        return ex.getMessage();

    }

}
