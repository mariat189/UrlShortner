package com.url.urlshortnerservice.Controller;

import com.url.urlshortnerservice.model.Url;
import com.url.urlshortnerservice.model.UrlDto;
import com.url.urlshortnerservice.model.UrlErrorResponseDto;
import com.url.urlshortnerservice.model.UrlResponseDto;
import com.url.urlshortnerservice.service.UrlService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@RestController
public class UrlShorteningcontroller {
    @Autowired
    private UrlService urlService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateShortLink(@RequestBody UrlDto urlDto)
    {
        Url urlToRet = urlService.generateShortLink(urlDto);
        if(urlToRet != null){
            UrlResponseDto urlResponseDto = new UrlResponseDto();
            urlResponseDto.setOriginalUrl(urlToRet.getOriginalUrl());
            urlResponseDto.setExpirationDate(urlToRet.getExpirationDate());
            urlResponseDto.setShortLink(urlToRet.getShortLink());
            return new ResponseEntity<UrlResponseDto>(urlResponseDto, HttpStatus.OK);
        }
        UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
        urlErrorResponseDto.setStatus("404");
        urlErrorResponseDto.setError("There was an error processing your request. Please try again");
        return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto, HttpStatus.OK);
    }

    @GetMapping("/{shortLink}")
    public ResponseEntity<?> redirectToOriginalUrl(@PathVariable String shortLink, HttpServletResponse response) throws IOException {
        if(StringUtils.isBlank(shortLink)){
            UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
            urlErrorResponseDto.setError("Invalid");
            urlErrorResponseDto.setStatus("400");
            return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto, HttpStatus.OK);
        }
        Url urlToRet = urlService.getEncodedUrl(shortLink);
        if(urlToRet == null)
        {
            UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
            urlErrorResponseDto.setError("Does not exist");
            urlErrorResponseDto.setStatus("400");
            return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto, HttpStatus.OK);
        }
        if (urlToRet.getExpirationDate().isBefore(LocalDateTime.now())) {
            UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
            urlErrorResponseDto.setError("Url has expired");
            urlErrorResponseDto.setStatus("400");
            return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto, HttpStatus.OK);
        }
        response.sendRedirect(urlToRet.getOriginalUrl());
        return null;
    }

}
