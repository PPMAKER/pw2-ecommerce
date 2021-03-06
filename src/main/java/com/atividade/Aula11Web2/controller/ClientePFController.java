/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atividade.Aula11Web2.controller;

import com.atividade.Aula11Web2.models.entity.ClientePF;
import com.atividade.Aula11Web2.models.entity.Usuario;
import com.atividade.Aula11Web2.models.repository.ClientePFRepository;
import com.atividade.Aula11Web2.models.repository.RoleRepository;
import com.atividade.Aula11Web2.models.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import javax.transaction.Transactional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Pedro Paulo
 */
@Transactional
@Controller
@RequestMapping("clientePF")
public class ClientePFController {
    @Autowired
    ClientePFRepository repository;
    
    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    RoleRepository roleRepository;


    @GetMapping("/list")
    public ModelAndView listar(
            @RequestParam(value = "nome", required = false) String nome,
            ModelMap model
    ) {
        if (nome!= null && !nome.isBlank()){
            model.addAttribute("clientesPF", repository.findByName(nome));
        } else {
            model.addAttribute("clientesPF", repository.clientesPF());
        }

        return new ModelAndView("/clientePF/list", model);
    }
    
    @GetMapping("/form")
    public ModelAndView form(ClientePF clientePF) {
        return new ModelAndView("/clientePF/form");
    }
    
    @PostMapping("/save")
    public ModelAndView save(@Valid ClientePF clientePF, BindingResult result, ModelMap model) {
        if(result.hasErrors())
            return form(clientePF);
        
        Usuario u = clientePF.getUsuario();
        u.setLogin(clientePF.getCpf());
        u.getRoles().add(roleRepository.role("ROLE_USER"));


        repository.save(clientePF);;
        model.addAttribute("alert_type", "success");
        model.addAttribute("alert_message", "Usuario cadastrado com sucesso");
        return new ModelAndView("/autenticacao/login");
    }
    
    @GetMapping("/remove/{id}")
    public ModelAndView remove(@PathVariable("id") Integer id) {
        repository.remove(id);
        return new ModelAndView("redirect:/clientePF/list");
    }

    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable("id") Integer id, ModelMap model) {
        model.addAttribute("clientePF", repository.clientePF(id));
        return new ModelAndView("/clientePF/form", model);
    }

    @PostMapping("/update")
    public ModelAndView update(@Valid ClientePF clientePF, BindingResult result) {
        if (result.hasErrors()) {
            return form(clientePF);
        }
        repository.update(clientePF);
        return new ModelAndView("redirect:/clientePF/list");
    }
}
