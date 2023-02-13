package com.sip.ams.controllers;


import com.sip.ams.entities.Article;
import com.sip.ams.entities.Provider;
import com.sip.ams.repositories.ArticleRepository;
import com.sip.ams.repositories.ProviderRepository;
//import jakarta.validation.Path;
import java.nio.file.Path;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Controller
@RequestMapping("/article/")

public class ArticleController {
    public static String uploadDirectory = System.getProperty("user.dir")+"/src/main/resources/static/uploads";

    private final ArticleRepository articleRepository;
    private final ProviderRepository providerRepository;
    @Autowired
    public ArticleController(ArticleRepository articleRepository, ProviderRepository providerRepository) {
        this.articleRepository = articleRepository;
        this.providerRepository = providerRepository;
    }

    @GetMapping("list")
    public String listProviders(Model model) {
        //model.addAttribute("articles", null);
        model.addAttribute("articles", articleRepository.findAll());
        return "provider/article/listArticles";
    }

    @GetMapping("add")
    public String showAddArticleForm(Article article, Model model) {

        model.addAttribute("providers", providerRepository.findAll());
        //model.addAttribute("article", new Article());
        return "provider/article/addArticle";
    }

    @PostMapping("add")
    //@ResponseBody
    public String addArticle(@Valid Article article, BindingResult result, @RequestParam(name = "providerId", required = false) Long p,
                             @RequestParam("files") MultipartFile[] files) {

        Provider provider = providerRepository.findById(p)
                .orElseThrow(()-> new IllegalArgumentException("Invalid provider Id:" + p));
        article.setProvider(provider);

        /// part upload

        StringBuilder fileName = new StringBuilder();
        MultipartFile file = files[0];
        Path fileNameAndPath = Paths.get(uploadDirectory, file.getOriginalFilename());

        fileName.append(file.getOriginalFilename());
        try {
            Files.write(fileNameAndPath, file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        article.setPicture(fileName.toString());
        articleRepository.save(article);
        return "redirect:list";

        //return article.getLabel() + " " +article.getPrice() + " " + p.toString();
    }


    @GetMapping("delete/{id}")
    public String deleteProvider(@PathVariable("id") long id, Model model) {
        Article artice = articleRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("Invalid provider Id:" + id));
        articleRepository.delete(artice);
        model.addAttribute("articles", articleRepository.findAll());
        return "provider/article/listArticles";
    }

    @GetMapping("edit/{id}")
    public String showArticleFormToUpdate(@PathVariable("id") long id, Model model) {
        Article article = articleRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("Invalid provider Id:" + id));

        model.addAttribute("article", article);
        model.addAttribute("providers", providerRepository.findAll());
        model.addAttribute("idProvider", article.getProvider().getId());

        return "provider/article/updateArticle";
    }
    @PostMapping("edit/{id}")
    public String updateArticle(@PathVariable("id") long id, @Valid Article article, BindingResult result,
                                Model model, @RequestParam(name = "providerId", required = false) Long p) {
        if (result.hasErrors()) {
            article.setId(id);
            return "provider/article/updateArticle";
        }

        Provider provider = providerRepository.findById(p)
                .orElseThrow(()-> new IllegalArgumentException("Invalid provider Id:" + p));
        article.setProvider(provider);

        articleRepository.save(article);
        model.addAttribute("articles", articleRepository.findAll());
        return "provider/article/listArticles";
    }
    @GetMapping("show/{id}")
    public String showArticleDetails(@PathVariable("id") long id, Model model) {
        Article article = articleRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("Invalid provider Id:" + id));

        model.addAttribute("article", article);

        return "provider/article/showArticle";
    }


}



