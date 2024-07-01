package com.url.urlshortnerservice.service;

import com.url.urlshortnerservice.model.Url;
import com.url.urlshortnerservice.model.UrlDto;
import org.springframework.stereotype.Service;

@Service
public interface UrlService {
    public Url generateShortLink(UrlDto urlDto);
    public Url persistShortLink(Url url);
    public Url getEncodedUrl(String url);
    public void deleteShortLink(Url url);
}
