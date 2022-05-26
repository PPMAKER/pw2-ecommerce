/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atividade.Aula11Web2.models.repository;

import com.atividade.Aula11Web2.models.entity.Role;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;
/**
 *
 * @author Pedro Paulo
 */
@Repository
public class RoleRepository {
    @PersistenceContext
    private EntityManager em;
    
    public Role role(String nome){
        String hql = "from Role where nome=:nome";
        Query query = em.createQuery(hql, Role.class);
        query.setParameter("nome", nome);
        return (Role) query.getSingleResult();
    }
}