/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 11/10/2012 
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
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoApoioNovosPesquisadoresDao;
import br.ufrn.sigaa.arq.dao.projetos.AvaliacaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.ElementoDespesa;
import br.ufrn.sigaa.extensao.dominio.ResumoOrcamentoDetalhado;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoApoioNovosPesquisadores;
import br.ufrn.sigaa.pesquisa.form.TelaCronograma;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.Avaliacao;
import br.ufrn.sigaa.projetos.dominio.OrcamentoDetalhado;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoAvaliacao;
import br.ufrn.sigaa.projetos.negocio.AvaliacaoProjetosFactory;
import br.ufrn.sigaa.projetos.negocio.CronogramaProjetoHelper;

/**
 * @author Leonardo
 *
 */
@Component("avaliacaoProjetoApoioNovosPesquisadoresMBean")
@Scope("session")
public class AvaliacaoProjetoApoioNovosPesquisadoresMBean extends
		SigaaAbstractController<Avaliacao> {

	private ProjetoApoioNovosPesquisadores projeto;
	
	private Collection<ProjetoApoioNovosPesquisadores> projetos;
	
	private Map<ElementoDespesa, ResumoOrcamentoDetalhado> tabelaOrcamentaria;
	
	private TelaCronograma telaCronograma = new TelaCronograma();
	
	private Collection<Avaliacao> avaliacoes;
	
	public AvaliacaoProjetoApoioNovosPesquisadoresMBean() {
		obj = new Avaliacao();
		tabelaOrcamentaria = new HashMap<ElementoDespesa, ResumoOrcamentoDetalhado>();
		telaCronograma = new TelaCronograma();
	}
	
	public String listarProjetos() throws DAOException {
		ProjetoApoioNovosPesquisadoresDao dao = getDAO(ProjetoApoioNovosPesquisadoresDao.class);
		
		projetos = dao.projetosParaAvaliar();
		
		return forward("/pesquisa/ProjetoApoioNovosPesquisadores/lista_avaliacao.jsp");
	}

	public String iniciarCriarAvaliacao() throws ArqException{
		prepareMovimento(SigaaListaComando.AVALIAR_PROJETO);
		
		ProjetoApoioNovosPesquisadoresDao dao = getDAO(ProjetoApoioNovosPesquisadoresDao.class);
		obj = new Avaliacao();
		tabelaOrcamentaria = new HashMap<ElementoDespesa, ResumoOrcamentoDetalhado>();
		telaCronograma = new TelaCronograma();
		
		projeto = dao.carregarProjetoApoio(getParameterInt("id"));
		projeto.setCoordenador(dao.findByPrimaryKey(projeto.getCoordenador().getId(), Servidor.class));
		projeto.setEditalPesquisa(dao.findByPrimaryKey(projeto.getEditalPesquisa().getId(), EditalPesquisa.class));
		
		obj.setProjeto(projeto.getProjeto());
		obj.setAvaliador(getUsuarioLogado());
		obj.setDistribuicao(dao.distribuicaoMaisRecente());
		obj.getDistribuicao().getModeloAvaliacao().getTipoAvaliacao().getId();
		obj.setSituacao(new TipoSituacaoAvaliacao(TipoSituacaoAvaliacao.PENDENTE));
		
		projeto.getProjeto().getOrcamento().addAll(
				dao.findByExactField(OrcamentoDetalhado.class, "projeto.id", projeto.getProjeto().getId()) );
		
		recalculaTabelaOrcamentaria(projeto.getProjeto().getOrcamento());
		
		setTelaCronograma(CronogramaProjetoHelper.carregarTelaCronogramaProjetoEditalPesquisa(getGenericDAO(), projeto.getProjeto(), projeto.getEditalPesquisa()));
		
		AvaliacaoDao avaDao = getDAO(AvaliacaoDao.class);
		avaliacoes = avaDao.findByProjeto(projeto.getProjeto());
		
		return forward("/pesquisa/ProjetoApoioNovosPesquisadores/form_avaliacao.jsp");
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
		return forward("/pesquisa/ProjetoApoioNovosPesquisadores/form_avaliacao.jsp");
	}
	
	public Collection<ProjetoApoioNovosPesquisadores> getProjetos() {
		return projetos;
	}

	public void setProjetos(Collection<ProjetoApoioNovosPesquisadores> projetos) {
		this.projetos = projetos;
	}

	public ProjetoApoioNovosPesquisadores getProjeto() {
		return projeto;
	}

	public void setProjeto(ProjetoApoioNovosPesquisadores projeto) {
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

	public TelaCronograma getTelaCronograma() {
		return telaCronograma;
	}

	public void setTelaCronograma(TelaCronograma telaCronograma) {
		this.telaCronograma = telaCronograma;
	}
}
