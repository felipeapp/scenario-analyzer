/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 10/10/2012 
 */
package br.ufrn.sigaa.pesquisa.jsf;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoApoioGrupoPesquisaDao;
import br.ufrn.sigaa.arq.dao.projetos.AvaliacaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.ElementoDespesa;
import br.ufrn.sigaa.extensao.dominio.ResumoOrcamentoDetalhado;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoApoioGrupoPesquisa;
import br.ufrn.sigaa.projetos.dominio.Avaliacao;
import br.ufrn.sigaa.projetos.dominio.OrcamentoDetalhado;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoAvaliacao;
import br.ufrn.sigaa.projetos.negocio.AvaliacaoProjetosFactory;

/**
 * @author Leonardo
 *
 */
@Component("avaliacaoProjetoApoioGrupoPesquisaMBean")
@Scope("session")
public class AvaliacaoProjetoApoioGrupoPesquisaMBean extends
		SigaaAbstractController<Avaliacao> {

	private ProjetoApoioGrupoPesquisa projeto;
	
	private Collection<ProjetoApoioGrupoPesquisa> projetos;
	
	private Map<ElementoDespesa, ResumoOrcamentoDetalhado> tabelaOrcamentaria;
	
	private Collection<Avaliacao> avaliacoes;
	
	public AvaliacaoProjetoApoioGrupoPesquisaMBean() {
		obj = new Avaliacao();
		tabelaOrcamentaria = new HashMap<ElementoDespesa, ResumoOrcamentoDetalhado>();
	}	
	
	public String listarProjetos() throws DAOException {
		ProjetoApoioGrupoPesquisaDao dao = getDAO(ProjetoApoioGrupoPesquisaDao.class);
		
		projetos = dao.projetosParaAvaliar();
		
		return forward("/pesquisa/ProjetoApoioGrupoPesquisa/lista_avaliacao.jsp");
	}

	public String iniciarCriarAvaliacao() throws ArqException{
		prepareMovimento(SigaaListaComando.AVALIAR_PROJETO);
		
		ProjetoApoioGrupoPesquisaDao dao = getDAO(ProjetoApoioGrupoPesquisaDao.class);
		obj = new Avaliacao();
		tabelaOrcamentaria = new HashMap<ElementoDespesa, ResumoOrcamentoDetalhado>();
		projeto = dao.carregarProjetoApoio(getParameterInt("id"));
		obj.setProjeto(projeto.getProjeto());
		obj.setAvaliador(getUsuarioLogado());
		obj.setDistribuicao(dao.distribuicaoMaisRecente());
		obj.getDistribuicao().getModeloAvaliacao().getTipoAvaliacao().getId();
		obj.setSituacao(new TipoSituacaoAvaliacao(TipoSituacaoAvaliacao.PENDENTE));
		
		projeto.getProjeto().getOrcamento().addAll(
				dao.findByExactField(OrcamentoDetalhado.class, "projeto.id", projeto.getProjeto().getId()) );
		
		recalculaTabelaOrcamentaria(projeto.getProjeto().getOrcamento());
		
		AvaliacaoDao avaDao = getDAO(AvaliacaoDao.class);
		avaliacoes = avaDao.findByProjeto(projeto.getProjeto());
		
		return forward("/pesquisa/ProjetoApoioGrupoPesquisa/form_avaliacao.jsp");
	}
	
	private void recalculaTabelaOrcamentaria(Collection<OrcamentoDetalhado> orcamentos) {
		tabelaOrcamentaria.clear();

		for (OrcamentoDetalhado orca : orcamentos) {
			ResumoOrcamentoDetalhado resumo = tabelaOrcamentaria.get(orca
					.getElementoDespesa());
			if (resumo == null) {
				resumo = new ResumoOrcamentoDetalhado();
			}
			resumo.getOrcamentos().add(orca);
			tabelaOrcamentaria.put(orca.getElementoDespesa(), resumo);
		}
	}
	
	public String avaliar() throws ArqException {
		obj.calcularMedia();
		obj.setDataAvaliacao(new Date());
		ValidatorUtil.validateRequired(obj.getParecer(), "Parecer", erros);
		ValidatorUtil.validateMaxLength(obj.getParecer(), 2000, "Parecer", erros);
		
		if(hasErrors()){
			return null;
		}
		
		try {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.AVALIAR_PROJETO);
			mov.setObjMovimentado(obj);
			mov.setObjAuxiliar(AvaliacaoProjetosFactory.getInstance().getEstrategia(obj.getProjeto()));
			execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			return listarProjetos();
		} catch(NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
		return forward("/pesquisa/ProjetoApoioGrupoPesquisa/form_avaliacao.jsp");
	}
	
	public Collection<ProjetoApoioGrupoPesquisa> getProjetos() {
		return projetos;
	}

	public void setProjetos(Collection<ProjetoApoioGrupoPesquisa> projetos) {
		this.projetos = projetos;
	}

	public ProjetoApoioGrupoPesquisa getProjeto() {
		return projeto;
	}

	public void setProjeto(ProjetoApoioGrupoPesquisa projeto) {
		this.projeto = projeto;
	}

	public Map<ElementoDespesa, ResumoOrcamentoDetalhado> getTabelaOrcamentaria() {
		return tabelaOrcamentaria;
	}

	public void setTabelaOrcamentaria(
			Map<ElementoDespesa, ResumoOrcamentoDetalhado> tabelaOrcamentaria) {
		this.tabelaOrcamentaria = tabelaOrcamentaria;
	}

	public Collection<Avaliacao> getAvaliacoes() {
		return avaliacoes;
	}

	public void setAvaliacoes(Collection<Avaliacao> avaliacoes) {
		this.avaliacoes = avaliacoes;
	}
}
