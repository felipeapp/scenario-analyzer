/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 21/08/2008
 * 
 */
package br.ufrn.sigaa.pesquisa.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.pesquisa.AvaliacaoResumoDao;
import br.ufrn.sigaa.arq.dao.pesquisa.CongressoIniciacaoCientificaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ResumoCongressoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.mensagens.MensagensGerais;
import br.ufrn.sigaa.pesquisa.dominio.AvaliacaoResumo;
import br.ufrn.sigaa.pesquisa.dominio.AvaliadorCIC;
import br.ufrn.sigaa.pesquisa.dominio.CongressoIniciacaoCientifica;
import br.ufrn.sigaa.pesquisa.dominio.ResumoCongresso;
import br.ufrn.sigaa.pesquisa.negocio.MovimentoAvaliacaoResumo;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Controlador para as operações de cadastro e distribuição de avaliações de resumos do CIC
 * 
 * @author Leonardo Campos
 *
 */
@Component("avaliacaoResumoBean") @Scope("session")
public class AvaliacaoResumoMBean extends SigaaAbstractController<AvaliacaoResumo> {
	
	public final String JSP_DISTRIBUICAO_RESUMOS = "/pesquisa/avaliador_cic/distribuicao_resumos.jsf";
	public final String JSP_LISTA_RESUMOS = "/pesquisa/avaliador_cic/lista_resumos.jsf";
	public final String JSP_AVALIACAO_RESUMO = "/pesquisa/avaliador_cic/avaliacao_resumo.jsf";

	private Boolean pendentes;
	
	private AvaliadorCIC avaliador;
	
	private AreaConhecimentoCnpq area;
	
	private List<SelectItem> avaliadoresCombo;
	
	private Collection<AvaliacaoResumo> avaliacoes;
	
	private AvaliacaoResumo avaliacao;
	
	public AvaliacaoResumoMBean() {
		clear();
	}

	/** Inicializador dos atributos a serem usados */
	private void clear() {
		avaliador = new AvaliadorCIC();
		avaliador.setDocente(new Servidor());
		area = new AreaConhecimentoCnpq();
		avaliadoresCombo = new ArrayList<SelectItem>();
		avaliacoes = new ArrayList<AvaliacaoResumo>();
		pendentes = true;
		avaliacao = new AvaliacaoResumo();
	}
	
	/**
	 * Popula os dados para a distribuição de avaliações de resumo.
	 * <br>
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/pesquisa/menu/iniciacao.jsp
	 * Método invocado a partir do gravar()
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String popularDistribuicao() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_PESQUISA);
		prepareMovimento(SigaaListaComando.DISTRIBUIR_AVALIACAO_RESUMO_CIC);
		clear();
		return forward(JSP_DISTRIBUICAO_RESUMOS);
	}
	
	/**
	 * Carrega os avaliadores para a área de conhecimento selecionada.
	 * <br>
	 * JSP: <ul><li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/avaliacao_apresentacao_resumo/ajustes_distribuicao.jsp</li>
	 *      <li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/avaliacao_apresentacao_resumo/lista_gestor.jsp</li>
	 *      <li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/avaliador_cic/distribuicao_resumos.jsp</li></ul>
	 * @param evt
	 * @throws DAOException
	 */
	public void carregaAvaliadores(ValueChangeEvent evt) throws DAOException{
		Integer idArea = (Integer) evt.getNewValue();
		
		CongressoIniciacaoCientificaDao daoCongresso = getDAO(CongressoIniciacaoCientificaDao.class);
		AvaliacaoResumoDao daoAvaliacao = getDAO(AvaliacaoResumoDao.class);
		
		CongressoIniciacaoCientifica congresso = daoCongresso.findAtivo();
		
		avaliadoresCombo = toSelectItems(daoAvaliacao.findAvaliadoresResumo(congresso.getId(), idArea), "id", "docente.nome");
	}
	
	/**
	 * Carrega as avaliações do avaliador selecionado + as avaliações pendentes de acordo com o
	 * campo marcado no formulário.
	 * 
	 * 
	 * JSP: <ul><li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/avaliacao_apresentacao_resumo/ajustes_distribuicao.jsp</li>
	 *      <li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/avaliacao_apresentacao_resumo/lista_gestor.jsp</li>
	 *      <li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/avaliador_cic/distribuicao_resumos.jsp</li></ul>
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void carregaAvaliacoes(ValueChangeEvent evt) throws DAOException{
		Integer idAvaliador = (Integer) evt.getNewValue();
		avaliacoes = new ArrayList<AvaliacaoResumo>();		
		
		if(idAvaliador > 0){
			CongressoIniciacaoCientificaDao daoCongresso = getDAO(CongressoIniciacaoCientificaDao.class);
			AvaliacaoResumoDao daoAvaliacao = getDAO(AvaliacaoResumoDao.class);
			ResumoCongressoDao daoResumo = getDAO(ResumoCongressoDao.class);
			
			CongressoIniciacaoCientifica congresso = daoCongresso.findAtivo();
			
			if(pendentes){
				avaliacoes = daoAvaliacao.findAvaliacoesByAvaliador(idAvaliador);
				for(AvaliacaoResumo a: avaliacoes)
					a.setSelecionado(true);
				Collection<ResumoCongresso> resumos = daoResumo.filter(congresso.getId(), area.getId(), null, null, null, ResumoCongresso.SUBMETIDO, null, null, Boolean.TRUE, null);
				for(ResumoCongresso r: resumos){
					AvaliacaoResumo av = new AvaliacaoResumo();
					av.setAvaliador(avaliador);
					av.setResumo(r);
					avaliacoes.add(av);
				}
			}else{
				avaliacoes = daoAvaliacao.findAvaliacoesByAvaliador(idAvaliador);
				for(AvaliacaoResumo a: avaliacoes)
					a.setSelecionado(true);
			}
		}
	}
	
	/**
	 * Persiste a distribuição das avaliações dos resumos pra o avaliador no banco
	 * <br>
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/pesquisa/avaliador_cic/distribuicao_resumos.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String gravar() throws ArqException {
		
		if(avaliador.getId() == 0){
			addMensagemErro("Selecione um Avaliador.");
			return null;
		}
				
		MovimentoAvaliacaoResumo mov = new MovimentoAvaliacaoResumo();
		mov.setCodMovimento(SigaaListaComando.DISTRIBUIR_AVALIACAO_RESUMO_CIC);
		mov.setAvaliacoes(avaliacoes);
		
		try {
			executeWithoutClosingSession(mov);
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		}
		addMensagemInformation("Distribuição de avaliações de resumos realizada com sucesso!");
		
		return popularDistribuicao();
	}
	
	/**
	 * Lista as avaliações de resumo para o avaliador logado.
	 * <br>
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/portais/docente/menu_docente.jsp
	 * E pelo método AvaliarResumo()
	 * @return
	 * @throws ArqException 
	 */
	public String listarResumos() throws ArqException {
		CongressoIniciacaoCientificaDao daoCongresso = getDAO(CongressoIniciacaoCientificaDao.class);
		AvaliacaoResumoDao dao = getDAO(AvaliacaoResumoDao.class);
			
		Collection<AvaliadorCIC> avaliadores = null;
		CongressoIniciacaoCientifica congresso = daoCongresso.findAtivo();
		if(getUsuarioLogado().getServidorAtivo() != null &&  getSubSistema().equals(SigaaSubsistemas.PORTAL_DOCENTE )){
			avaliadores = dao.findAvaliadorResumoByServidor(congresso.getId(), getUsuarioLogado().getServidor().getId());
		} else if(getUsuarioLogado().getDiscenteAtivo() != null &&  getSubSistema().equals(SigaaSubsistemas.PORTAL_DISCENTE )) {
			avaliadores = dao.findAvaliadorResumoByDiscente(congresso.getId(), getDiscenteUsuario().getId());
		} else {
			addMensagem(MensagensGerais.APENAS_DOCENTE_QUADRO_EFETIVO, "UFRN");
			return null;
		}
		
		if(ValidatorUtil.isEmpty(avaliadores)){
			addMensagemErro("Você não foi relacionado como Avaliador de Resumo para o Congresso de Iniciação Científica atual.");
			return null;
		}
		
		Collection<Integer> idsAvaliadores = new ArrayList<Integer>();
		
		for(AvaliadorCIC ava: avaliadores){
			idsAvaliadores.add(ava.getId());
		}
		
		avaliacoes = dao.findAvaliacoesByAvaliador(idsAvaliadores);
		if(avaliacoes == null || avaliacoes.isEmpty()){
			addMensagemWarning("Não há mais avaliações de resumo destinadas a você.");
			return cancelar();
		}
		
		return forward(JSP_LISTA_RESUMOS);
	}
	
	/**
	 * Popular os dados para a avaliação
	 * <br>
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/pesquisa/avaliador_cic/lista_resumos.jsp
	 * @return
	 * @throws ArqException 
	 */
	public String popularAvaliacao() throws ArqException {
		avaliacao = new AvaliacaoResumo();
		int id = getParameterInt("id", 0);
		
		if(id > 0) {
			avaliacao = getGenericDAO().findByPrimaryKey(id, AvaliacaoResumo.class);
		} else {
			addMensagemErro("Selecione um resumo para avaliar.");
			return null;
		}
		
		prepareMovimento(SigaaListaComando.AVALIAR_RESUMO_CIC);
		return forward(JSP_AVALIACAO_RESUMO);
	}
	
	/**
	 * Esse método consiste na avaliação do resumo, dar o parecer sobre o resumo.
	 * <br>
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/pesquisa/avaliador_cic/avaliacao_resumo.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String avaliarResumo() throws ArqException {
		
		if(avaliacao.getParecer() == null || avaliacao.getParecer().trim().equals("")){
			addMensagemErro("Informe seu parecer sobre o resumo.");
			return null;
		}
		
		MovimentoAvaliacaoResumo mov = new MovimentoAvaliacaoResumo();
		mov.setCodMovimento(SigaaListaComando.AVALIAR_RESUMO_CIC);
		mov.getAvaliacoes().add(avaliacao);
		
		try {
			executeWithoutClosingSession(mov);
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		}
		
		addMensagemInformation("Resumo de código "+ avaliacao.getResumo().getCodigo()+ " avaliado com sucesso!");
		
		return listarResumos();
	}
	
	public AvaliadorCIC getAvaliador() {
		return avaliador;
	}

	public void setAvaliador(AvaliadorCIC avaliador) {
		this.avaliador = avaliador;
	}

	public AreaConhecimentoCnpq getArea() {
		return area;
	}

	public void setArea(AreaConhecimentoCnpq area) {
		this.area = area;
	}

	public List<SelectItem> getAvaliadoresCombo() {
		return avaliadoresCombo;
	}

	public void setAvaliadoresCombo(List<SelectItem> avaliadoresCombo) {
		this.avaliadoresCombo = avaliadoresCombo;
	}
	
	@Override
	public String cancelar() {
		clear();
		return super.cancelar();
	}

	public Boolean getPendentes() {
		return pendentes;
	}

	public void setPendentes(Boolean pendentes) {
		this.pendentes = pendentes;
	}

	public Collection<AvaliacaoResumo> getAvaliacoes() {
		return avaliacoes;
	}

	public void setAvaliacoes(Collection<AvaliacaoResumo> avaliacoes) {
		this.avaliacoes = avaliacoes;
	}

	public AvaliacaoResumo getAvaliacao() {
		return avaliacao;
	}

	public void setAvaliacao(AvaliacaoResumo avaliacao) {
		this.avaliacao = avaliacao;
	}
}
