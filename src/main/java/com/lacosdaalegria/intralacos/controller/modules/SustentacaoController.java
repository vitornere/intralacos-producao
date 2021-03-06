package com.lacosdaalegria.intralacos.controller.modules;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lacosdaalegria.intralacos.model.usuario.Voluntario;
import com.lacosdaalegria.intralacos.service.modules.SustentacaoService;
import com.lacosdaalegria.intralacos.service.modules.VoluntarioService;

@Controller
public class SustentacaoController {
	
	@Autowired
	private SustentacaoService service;
	@Autowired
	private VoluntarioService vService;
	
	/*
	 * ======================================================================================
	 * ======================== Controle de Equipe Sustentação ==============================
	 * ======================================================================================
	 */
	 
	@GetMapping("/admin/controle/analistas")
	public String controleAnalistas(Model model) {
		model.addAttribute("analistas", service.allAnalistas());
		return "admin/sustentacao/controle";
	}
	
	@PostMapping("/admin/add/analista")
	public String addAnalista(String email) {
		service.addAnalista(email);
		return "redirect:/admin/controle/analistas";
	}
	
	@PostMapping("/admin/remove/analista")
	public String addAnalista(Voluntario voluntario) {
		service.removeAnalista(voluntario);
		return "redirect:/admin/controle/analistas";
	}
	 
	 
	 /*
	 * ======================================================================================
	 * ============================== Equipe Sustentação ====================================
	 * ======================================================================================
	 */
	
	@GetMapping("/sustentacao/page")
	public String sustentacao(Model model) {
		model.addAttribute("voluntario", new Voluntario());
		return "admin/sustentacao/page";
	}
	
	@PostMapping("/sustentacao/search")
	public String search(String campo, String valor, Model model) {
		Voluntario voluntario = vService.searchSustentacao(campo, valor);
		if (voluntario == null) {
			model.addAttribute("voluntario",  new Voluntario());
			model.addAttribute("errorMessage", "Não foi localizado nenhum voluntário com essa informação");
		} else  {
			model.addAttribute("voluntario",  voluntario);
		}
		
		return "admin/sustentacao/page";
	}
	
	@PostMapping("/sustentacao/atualizar/voluntario")
	public ModelAndView updateInfo(@Valid Voluntario voluntario, BindingResult result) {
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("admin/sustentacao/page");
		vService.duplicidadeInfo(voluntario, result);

    	if(hasNoErroUpdate(result)) {
    		vService.updateUserSustentacao(voluntario);
    		modelAndView.addObject("voluntario", voluntario);
            modelAndView.addObject("successMessage", "Informações atualizadas com sucesso!");
    	}
    	return modelAndView;
	}
	
	//Metodo que valida result para atualização, deve ser transferido para camada de serviço
	private boolean hasNoErroUpdate(BindingResult result) {		
		for(ObjectError e : result.getAllErrors()) {
			if(!e.getCodes()[0].contains("senha") && !e.getCodes()[0].contains("preferencia")
					&&!e.getCodes()[0].contains("whatsapp")&&!e.getCodes()[0].contains("regiao")
					&&!e.getCodes()[0].contains("ddd")&&!e.getCodes()[0].contains("nascimento"))
				return false;
		}
		return true;
	}

}
