package com.sip.ams.repositories;

import com.sip.ams.entities.Article;
import com.sip.ams.entities.Provider;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProviderRepository extends CrudRepository<Provider,Long> {
    @Query("select a FROM Article a WHERE a.provider.id = ?1")
    List<Article> findArticlesByProvider(long id);

}
