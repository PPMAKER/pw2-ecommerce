/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atividade.Aula11Web2.models.repository;

import com.atividade.Aula11Web2.models.entity.FormaPagamento;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;
/**
 *
 * @author Pedro Paulo
 */
@Repository
public class FormaPagamentoRepository {

    @PersistenceContext
    private EntityManager em;

    public List<FormaPagamento> formasPagamento() {
        Query query = em.createQuery("from FormaPagamento order by nome");
        return query.getResultList();
    }

    public FormaPagamento formaPagamento(Integer id) {
        return em.find(FormaPagamento.class, id);
    }
}