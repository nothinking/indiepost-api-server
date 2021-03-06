package com.indiepost.dto

import com.indiepost.validation.ContentType
import org.springframework.util.MimeTypeUtils.*
import org.springframework.web.multipart.MultipartFile
import javax.validation.constraints.NotNull


data class AdminImageUploadDto(
        @NotNull
        @ContentType(value = [IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE], message = "PNG나 JPG 포멧 이미지를 올려주세요!")
        var multipartFile: MultipartFile? = null
)
