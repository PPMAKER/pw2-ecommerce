    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atividade.Aula11Web2.controller;

import com.atividade.Aula11Web2.models.entity.Cidade;
import com.atividade.Aula11Web2.models.entity.ClientePF;
import com.atividade.Aula11Web2.models.entity.Endereco;
import com.atividade.Aula11Web2.models.entity.Estado;
import com.atividade.Aula11Web2.models.entity.FormaPagamento;
import com.atividade.Aula11Web2.models.entity.ItemVenda;
import com.atividade.Aula11Web2.models.entity.Produto;
import com.atividade.Aula11Web2.models.entity.Venda;
import com.atividade.Aula11Web2.models.repository.CidadeRepository;
import com.atividade.Aula11Web2.models.repository.ClientePFRepository;
import com.atividade.Aula11Web2.models.repository.EnderecoRepository;
import com.atividade.Aula11Web2.models.repository.EstadoRepository;
import com.atividade.Aula11Web2.models.repository.FormaPagamentoRepository;
import com.atividade.Aula11Web2.models.repository.ProdutoRepository;
import com.atividade.Aula11Web2.models.repository.VendaRepository;
import java.time.LocalDate;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author Pedro Paulo
 */
@Scope("request")
@Transactional
@Controller
@RequestMapping("venda")
public class VendaController {

    @Autowired
    VendaRepository repository;

    @Autowired
    ProdutoRepository repositoryP;

    @Autowired
    ClientePFRepository repositoryCientePF;
    
    @Autowired
    EnderecoRepository enderecoRepository;

    @Autowired
    CidadeRepository cidadeRepository;

    @Autowired
        EstadoRepository estadoRepository;
    
    @Autowired
    FormaPagamentoRepository formaPagamentoRepository;

    @Autowired
    Venda venda;

     @GetMapping("/list")
    public ModelAndView listar(
            @RequestParam(value = "cliente_id", required = false) Integer cliente_id,
            @RequestParam(value = "dataStart", required = false) LocalDate dataStart,
            @RequestParam(value = "dataStop", required = false) LocalDate dataStop,
            ModelMap model
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<ClientePF> clientespf = repositoryCientePF.findByCpf(authentication.getName());
        if (!clientespf.isEmpty()) {
            model.addAttribute("vendas", repository.findByClienteId(clientespf.get(0).getId()));
        } else if (cliente_id != null && cliente_id > 0) {
            model.addAttribute("vendas", repository.findByClienteId(cliente_id));
        } else if (dataStart != null && dataStop != null) {
            model.addAttribute("vendas", repository.findByDate(dataStart, dataStop));
        } else {
            model.addAttribute("vendas", repository.vendas());
        }
        return new ModelAndView("/venda/list", model);
    }

    @PostMapping("/add-item")
    public ModelAndView addItem(@Valid ItemVenda itemVenda, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", result.getAllErrors().get(0).getDefaultMessage());
            redirectAttributes.addFlashAttribute("produto_id_error", itemVenda.getProduto().getId());
            return new ModelAndView("redirect:/produto/list");
        }
        Produto produto = repositoryP.produto(itemVenda.getProduto().getId());

        itemVenda.setProduto(produto);
        itemVenda.setVenda(venda);
        venda.getItensVenda().add(itemVenda);

        redirectAttributes.addFlashAttribute("message", produto.getDescricao() + " adicionado ao carrinho!");
        redirectAttributes.addFlashAttribute("showAlert", true);
        return new ModelAndView("redirect:/produto/list");
    }

    @GetMapping("/del-item/{id}")
    public ModelAndView delItem(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        venda.getItensVenda().remove(id);
        redirectAttributes.addFlashAttribute("alert_type", "warning");
        redirectAttributes.addFlashAttribute("alert_message", "Produto removido do carrinho!");
        return new ModelAndView("redirect:/venda/carrinho");
    }

    @GetMapping("/carrinho")
    public String form() {
        return "/venda/cart";
    }

    @GetMapping("/checkout")
    public ModelAndView checkout(Venda venda, ModelMap model, RedirectAttributes redirectAttributes) {
        if (this.venda.getItensVenda().isEmpty()) {
            redirectAttributes.addFlashAttribute("error_cart", "Carrinho vazio");
            return new ModelAndView("redirect:/venda/carrinho");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ClientePF clientepf = repositoryCientePF.findByCpf(authentication.getName()).get(0);
        List<Estado> estados = estadoRepository.estados();
        List<Cidade> cidades = cidadeRepository.cidades();
        List<Endereco> enderecos = clientepf.getEnderecos();
        List<FormaPagamento> formasPagamento = formaPagamentoRepository.formasPagamento();

        model.addAttribute("estados", estados);
        model.addAttribute("cidades", cidades);
        model.addAttribute("enderecos", enderecos);
        model.addAttribute("formasPagamento", formasPagamento);

        return new ModelAndView("/venda/finalizar", model);
    }

    @PostMapping("/save")
    public ModelAndView save(@Valid Venda venda, BindingResult result, RedirectAttributes redirectAttributes) {
        Endereco endereco = venda.getEndereco();
        if (result.hasErrors() && endereco.getId() == 0) {
            return checkout(venda, new ModelMap(), redirectAttributes);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ClientePF clientepf = repositoryCientePF.findByCpf(authentication.getName()).get(0);

        if (endereco.getId() == 0) {
            endereco.setCliente(clientepf);
        } else {
            endereco.cleanFields();
            endereco = enderecoRepository.endereco(endereco.getId());
        }

        System.out.println(endereco.toString());

        this.venda.setId(null);
        this.venda.setEndereco(endereco);
        this.venda.setFormaPagamento(venda.getFormaPagamento());
        this.venda.setData(LocalDate.now());
        this.venda.setClientePF(clientepf);
        repository.save(this.venda);
        this.venda.getItensVenda().clear();

        redirectAttributes.addFlashAttribute("alert_type", "success");
        redirectAttributes.addFlashAttribute("alert_message", "Venda armazenada com sucesso");
        return new ModelAndView("redirect:/venda/list");
    }

    @GetMapping("/detail/{id}")
    public ModelAndView detail(@PathVariable("id") Integer id, ModelMap model) {
        model.addAttribute("venda", repository.venda(id));
        return new ModelAndView("/venda/detail", model);
    }

}
