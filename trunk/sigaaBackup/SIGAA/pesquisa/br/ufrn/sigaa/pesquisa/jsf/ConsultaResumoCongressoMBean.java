/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 26/10/2012 
 */
package br.ufrn.sigaa.pesquisa.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.pesquisa.CongressoIniciacaoCientificaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ResumoCongressoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.pesquisa.dominio.CongressoIniciacaoCientifica;
import br.ufrn.sigaa.pesquisa.dominio.ResumoCongresso;

/**
 * Controlador responsável por consultar informações de resumos submetidos
 * para congressos de iniciação científica.
 * 
 * @author Leonardo Campos
 *
 */
@Component("consultaResumoCongressoMBean") @Scope("session")
public class ConsultaResumoCongressoMBean extends SigaaAbstractController<ResumoCongresso> {

	private boolean filtroCongresso;
	
	private boolean filtroArea;
	
	private Integer buscaCongresso;
	
	private String buscaArea;
	
	private Collection<SelectItem> congressosCombo;
	
	public ConsultaResumoCongressoMBean() {
		clear();
	}
	
	private void clear() {
		filtroCongresso = true;
		filtroArea = false;
		buscaCongresso = 0;
		buscaArea = "";
		congressosCombo = null;
	}



	public String iniciar() throws DAOException {
		clear();
		CongressoIniciacaoCientifica congressoAtual = getDAO(CongressoIniciacaoCientificaDao.class).findAtivo();
		buscaCongresso = congressoAtual.getId();
		resultadosBusca = getDAO(ResumoCongressoDao.class).filter(congressoAtual.getId(), null, null, null, null, null, null, null, null, null);
		return telaBusca();
	}
	
	public String buscar() throws DAOException {
		
		Integer idCongresso = null;
		String codArea = null;
		
		if(filtroCongresso) {
			ValidatorUtil.validateRequiredId(buscaCongresso, "Congresso", erros);
			idCongresso = buscaCongresso;
		}
		
		if(filtroArea) {
			ValidatorUtil.validateRequired(buscaArea, "Área", erros);
			codArea = buscaArea;
		}
		
		if(hasErrors()){
			addMensagens(erros);
			return telaBusca();
		}
		
		resultadosBusca = getDAO(ResumoCongressoDao.class).filter(idCongresso, null, null, null, null, null, codArea, null, null, null);
		
		return telaBusca();
	}
	
	public String view() throws DAOException {
		obj = getGenericDAO().findByPrimaryKey(getParameterInt("id"), ResumoCongresso.class);
		return telaView();
	}
	
	private String telaView() {
		return forward("/pesquisa/ResumoCongresso/view.jsp");
	}

	public String telaBusca(){
		return forward("/pesquisa/ResumoCongresso/form_busca.jsp");
	}
	
	public Collection<SelectItem> getCongressosCombo() throws DAOException {
		if(congressosCombo == null)
			congressosCombo = toSelectItems(getGenericDAO().findAll(CongressoIniciacaoCientifica.class, "ano", "desc"), "id", "descricao");
		return congressosCombo;
	}

	public boolean isFiltroCongresso() {
		return filtroCongresso;
	}

	public void setFiltroCongresso(boolean filtroCongresso) {
		this.filtroCongresso = filtroCongresso;
	}

	public boolean isFiltroArea() {
		return filtroArea;
	}

	public void setFiltroArea(boolean filtroArea) {
		this.filtroArea = filtroArea;
	}

	public Integer getBuscaCongresso() {
		return buscaCongresso;
	}

	public void setBuscaCongresso(Integer buscaCongresso) {
		this.buscaCongresso = buscaCongresso;
	}

	public String getBuscaArea() {
		return buscaArea;
	}

	public void setBuscaArea(String buscaArea) {
		this.buscaArea = buscaArea;
	}

	public void setCongressosCombo(Collection<SelectItem> congressosCombo) {
		this.congressosCombo = congressosCombo;
	}
}
