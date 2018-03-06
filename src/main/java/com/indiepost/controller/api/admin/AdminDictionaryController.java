package com.indiepost.controller.api.admin;

import com.indiepost.model.Word;
import com.indiepost.service.DictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/admin/dictionary", produces = {"application/json; charset=UTF-8"})
public class AdminDictionaryController {

    private final DictionaryService dictionaryService;

    @Autowired
    public AdminDictionaryController(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @PutMapping
    public void save(@RequestBody Word word) {
        dictionaryService.save(word);
    }

    @DeleteMapping
    public void remove(@RequestBody Word word) {
        dictionaryService.remove(word.getSurface());
    }

    @GetMapping
    public List<Word> getWords() {
        return dictionaryService.getWords();
    }

}
