package com.indiepost.controller.api.admin;

import com.indiepost.model.ImageSet;
import com.indiepost.service.ImageService;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Created by jake on 10/8/16.
 */
@RestController
@RequestMapping(value = "/api/admin/images", produces = {"application/json; charset=UTF-8"})
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }


    @RequestMapping(method = RequestMethod.GET)
    public Page<ImageSet> getImages(Pageable pageable) {
        return imageService.findAll(pageable);
    }

    @RequestMapping(method = RequestMethod.POST)
    public List<ImageSet> handleImageUpload(@RequestParam("files") MultipartFile[] multipartFiles) throws IOException, FileUploadException {
        return imageService.saveUploadedImages(multipartFiles);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Long handleImageDelete(@PathVariable Long id) throws IOException {
        imageService.deleteById(id);
        return id;
    }
}
