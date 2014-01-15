package br.ufrn.sigaa.mobile.touch.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.sigaa.arq.dao.extensao.AtividadeExtensaoDao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.mobile.commons.SigaaTouchAbstractController;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

@Component("buscaExtensaoTouch") @Scope("request")
public class BuscaExtensaoTouchMBean extends SigaaTouchAbstractController<AtividadeExtensao> {
	
	private Collection<AtividadeExtensao> atividades;
	
	private String tituloBusca;
	
	private TipoAtividadeExtensao tipoAtividadeBusca;
	
	private Unidade unidadeBusca;
	
	private Integer anoBusca;
	
	public BuscaExtensaoTouchMBean(){
		init();
	}
	
	private void init() {
		obj = new AtividadeExtensao();
		
		tipoAtividadeBusca = new TipoAtividadeExtensao();
		unidadeBusca = new Unidade();
	}

	public String iniciarBusca() {
		return forward("/mobile/touch/public/busca_extensao.jsf");
	}
	
	public String buscarAtividades() throws DAOException {
		if (isEmpty(tituloBusca) && isEmpty(tipoAtividadeBusca) 
				&& isEmpty(unidadeBusca) && isEmpty(anoBusca)) {
			addMensagemErro("Informe um dos parâmetros definidos para a busca.");
			return null;
		}
		
		AtividadeExtensaoDao dao = null;
		dao = getDAO(AtividadeExtensaoDao.class);

		try {
			Integer[] idTipoAtividade = new Integer[1];
			idTipoAtividade[0] = isEmpty(tipoAtividadeBusca) ? null : tipoAtividadeBusca.getId();
		
			Integer[] idSituacaoAtividade = new Integer[3];
			idSituacaoAtividade[0] = TipoSituacaoProjeto.EXTENSAO_EM_EXECUCAO;
			idSituacaoAtividade[1] = TipoSituacaoProjeto.EXTENSAO_CONCLUIDO;
			idSituacaoAtividade[2] = TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO;
			
			String titulo = isEmpty(tituloBusca) ? null : tituloBusca;
			Integer unidade = isEmpty(unidadeBusca) ? null : unidadeBusca.getId();
			Integer ano = isEmpty(anoBusca) ? null : anoBusca;
			
			atividades = dao.filter(null, null, null, null, null, null, null, titulo,
				idTipoAtividade, idSituacaoAtividade,
				unidade, null, null, null, null, null, ano,
				null, null, null, null, null, null, false, null, null, null, null, null, isExtensao());
			
			if(isEmpty(atividades)) {
				addMensagemErro("Nenhum projeto de extensão encontrado com os parâmetros utilizados.");
				return null;
			}
		}
		catch(LimiteResultadosException e) {
			addMensagemErro(e.getMessage());
			return null;
		}

		return forward("/mobile/touch/public/lista_extensao.jsf");
	}

	public Collection<AtividadeExtensao> getAtividades() {
		return atividades;
	}

	public void setAtividades(Collection<AtividadeExtensao> atividades) {
		this.atividades = atividades;
	}

	public String getTituloBusca() {
		return tituloBusca;
	}

	public void setTituloBusca(String tituloBusca) {
		this.tituloBusca = tituloBusca;
	}

	public TipoAtividadeExtensao getTipoAtividadeBusca() {
		return tipoAtividadeBusca;
	}

	public void setTipoAtividadeBusca(TipoAtividadeExtensao tipoAtividadeBusca) {
		this.tipoAtividadeBusca = tipoAtividadeBusca;
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
