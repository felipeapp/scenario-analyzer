package br.ufrn.sigaa.mobile.touch.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoPesquisaDao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.mobile.commons.SigaaTouchAbstractController;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;

@Component("buscaPesquisaTouch") @Scope("request")
public class BuscaPesquisaTouchMBean extends SigaaTouchAbstractController<ProjetoPesquisa> {
	
	private Collection<ProjetoPesquisa> projetos;
	
	private String tituloBusca;
	
	private Unidade centroBusca;
	
	private Unidade unidadeBusca;
	
	private Integer anoBusca;
	
	public BuscaPesquisaTouchMBean(){
		init();
	}
	
	private void init() {
		obj = new ProjetoPesquisa();
		
		centroBusca = new Unidade();
		unidadeBusca = new Unidade();
	}

	public String iniciarBusca() {
		return forward("/mobile/touch/public/busca_pesquisa.jsf");
	}
	
	public String buscarProjetos() throws DAOException {
		if (isEmpty(tituloBusca) && isEmpty(centroBusca) 
				&& isEmpty(unidadeBusca) && isEmpty(anoBusca)) {
			addMensagemErro("Informe um dos parâmetros definidos para a busca.");
			return null;
		}
		
		ProjetoPesquisaDao projetoDao = getDAO(ProjetoPesquisaDao.class);

		try {
			String titulo = isEmpty(tituloBusca) ? null : tituloBusca;
			Integer centro = isEmpty(centroBusca) ? null : centroBusca.getId();
			Integer unidade = isEmpty(unidadeBusca) ? null : unidadeBusca.getId();
			Integer ano = isEmpty(anoBusca) ? null : anoBusca;
			
			projetos = projetoDao.filter(null, null, ano, null, null, null, centro, unidade, titulo, null, null, null, null, null, null, false);
			
			if(isEmpty(projetos)) {
				addMensagemErro("Nenhum projeto de pesquisa encontrado com os parâmetros utilizados.");
				return null;
			}
		}
		catch(LimiteResultadosException e) {
			addMensagemErro(e.getMessage());
			return null;
		}

		return forward("/mobile/touch/public/lista_pesquisa.jsf");
	}

	public Collection<ProjetoPesquisa> getProjetos() {
		return projetos;
	}

	public void setProjetos(Collection<ProjetoPesquisa> projetos) {
		this.projetos = projetos;
	}

	public String getTituloBusca() {
		return tituloBusca;
	}

	public void setTituloBusca(String tituloBusca) {
		this.tituloBusca = tituloBusca;
	}

	public Unidade getCentroBusca() {
		return centroBusca;
	}

	public void setCentroBusca(Unidade centroBusca) {
		this.centroBusca = centroBusca;
	}

	public Unidade getUnidadeBusca() {
		return unidadeBusca;
	}

	public void setUnidadeBusca(Unidade unidadeBusca) {
		this.unidadeBusca = unidadeBusca;
	}

	public Integer getAnoBusca() {
		return anoBusca;
	}

	public void setAnoBusca(Integer anoBusca) {
		this.anoBusca = anoBusca;
	}

}
