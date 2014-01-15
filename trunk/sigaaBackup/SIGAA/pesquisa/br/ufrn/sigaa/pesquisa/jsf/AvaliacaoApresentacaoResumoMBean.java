/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 07/10/2008
 * 
 */
package br.ufrn.sigaa.pesquisa.jsf;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.pesquisa.AvaliacaoResumoDao;
import br.ufrn.sigaa.arq.dao.pesquisa.CongressoIniciacaoCientificaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ItemAvaliacaoDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ResumoCongressoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.mensagens.MensagensGerais;
import br.ufrn.sigaa.pesquisa.dominio.AvaliacaoApresentacaoResumo;
import br.ufrn.sigaa.pesquisa.dominio.AvaliadorCIC;
import br.ufrn.sigaa.pesquisa.dominio.CongressoIniciacaoCientifica;
import br.ufrn.sigaa.pesquisa.dominio.ItemAvaliacao;
import br.ufrn.sigaa.pesquisa.dominio.NotaItem;
import br.ufrn.sigaa.pesquisa.dominio.OrganizacaoPaineisCIC;
import br.ufrn.sigaa.pesquisa.dominio.ResumoCongresso;
import br.ufrn.sigaa.pesquisa.negocio.MovimentoAvaliacaoResumo;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Controlador para as operações de cadastro e distribuição de avaliações de apresentações
 * de resumos no congresso de iniciação científica
 * 
 * @author Leonardo Campos
 *
 */
@Component("avaliacaoApresentacaoResumoBean") @Scope("session")
public class AvaliacaoApresentacaoResumoMBean extends SigaaAbstractController<AvaliacaoApresentacaoResumo> {
	
	public final String JSP_FORM_DISTRIBUICAO = 			"/pesquisa/avaliacao_apresentacao_resumo/form_distribuicao.jsf";
	public final String JSP_AJUSTES_DISTRIBUICAO = 			"/pesquisa/avaliacao_apresentacao_resumo/ajustes_distribuicao.jsf";
	public final String JSP_FORM_AVALIACAO = 				"/pesquisa/avaliacao_apresentacao_resumo/form_avaliacao.jsf";
	public final String JSP_LISTA_AVALIADOR = 				"/pesquisa/avaliacao_apresentacao_resumo/lista_avaliador.jsf";
	public final String JSP_LISTA_GESTOR = 					"/pesquisa/avaliacao_apresentacao_resumo/lista_gestor.jsf";
	public final String JSP_FORM_RELATORIO_DISTRIBUICAO = 	"/pesquisa/avaliacao_apresentacao_resumo/form_relatorio_distribuicao.jsf";
	public final String JSP_RELATORIO_DISTRIBUICAO = 		"/pesquisa/avaliacao_apresentacao_resumo/relatorio_distribuicao.jsf";
	public final String JSP_FORM_RELATORIO_PONTUACAO = 		"/pesquisa/avaliacao_apresentacao_resumo/form_relatorio_pontuacao.jsf";
	public final String JSP_RELATORIO_PONTUACAO = 			"/pesquisa/avaliacao_apresentacao_resumo/relatorio_pontuacao.jsf";
	public final String JSP_FORM_RELATORIO_AVALIADORES = 	"/pesquisa/avaliador_cic/form_relatorio_avaliadores.jsf";
	public final String JSP_RELATORIO_AVALIADORES = 		"/pesquisa/avaliador_cic/relatorio_avaliadores.jsf";
	public final String JSP_FORM_RELATORIO_FICHAS = 		"/pesquisa/avaliacao_apresentacao_resumo/form_relatorio_fichas.jsf";

	private Boolean pendentes;
	
	private AvaliadorCIC avaliador;
	
	private AreaConhecimentoCnpq area;
	private Unidade unidade;
	
	private List<SelectItem> avaliadoresCombo;
	
	private Collection<AvaliacaoApresentacaoResumo> avaliacoes;
	
	private AvaliacaoApresentacaoResumo avaliacao;
	
	private CongressoIniciacaoCientifica congresso;
	
	private List<HashMap<String,Object>> lista = new ArrayList<HashMap<String,Object>>();
	
	private Collection<NotaItem> notas;
	
	private Integer numeroAvaliacoes;
	
	private boolean filtroAvaliador;
	private boolean filtroAluno;
	private boolean filtroOrientador;
	private boolean filtroCodResumo;
	private boolean filtroNumPainel;
	
	private String nomeAvaliador;
	private Integer idAvaliador;
	private String nomeAluno;
	private Integer idAluno;
	private String nomeOrientador;
	private Integer idOrientador;
	private String codResumo;
	private Integer numPainel;
	
	public AvaliacaoApresentacaoResumoMBean() {
		clear();
	}

	/** Inicialização dos métodos que serão utilizados no decorrer do caso de uso */
	private void clear() {
		avaliador = new AvaliadorCIC();
		avaliador.setDocente(new Servidor());
		area = new AreaConhecimentoCnpq();
		unidade = new Unidade();
		avaliadoresCombo = new ArrayList<SelectItem>();
		avaliacoes = new ArrayList<AvaliacaoApresentacaoResumo>();
		pendentes = true;
		avaliacao = new AvaliacaoApresentacaoResumo();
		congresso = new CongressoIniciacaoCientifica();
	}
	
	/**
	 * Popula as informações necessárias e encaminha para o formulário de geração da distribuição de avaliações de apresentação de resumos
	 * <br>
	 * JSP: <ul>
	 * 			<li>/WEB-INF/jsp/pesquisa/menu/iniciacao.jsp</li>
	 * 		</ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarDistribuicaoAvaliacoesApresentacaoResumo() throws ArqException {
		checkRole(SigaaPapeis.ADMINISTRADOR_PESQUISA);
		CongressoIniciacaoCientificaDao daoCongresso = getDAO(CongressoIniciacaoCientificaDao.class);
		AvaliacaoResumoDao daoAvaliacao = getDAO(AvaliacaoResumoDao.class);
		
		CongressoIniciacaoCientifica congresso = daoCongresso.findAtivo();
		
		avaliacoes = daoAvaliacao.findAvaliacoesApresentacaoResumoByCongresso(congresso.getId());
		
		if(avaliacoes != null && !avaliacoes.isEmpty())
			addMensagemWarning("Já foi gerada uma distribuição de avaliações de trabalhos para o congresso atual ("+ congresso.getDescricaoResumida() +
					").<br/> Caso confirme esta operação, a distribuição anterior será descartada e uma nova distribuição será gerada.");
		
		setOperacaoAtiva(SigaaListaComando.DISTRIBUIR_AVALIACAO_APRESENTACAO_RESUMO_CIC.getId());
		prepareMovimento(SigaaListaComando.DISTRIBUIR_AVALIACAO_APRESENTACAO_RESUMO_CIC);
		return forward(JSP_FORM_DISTRIBUICAO);
	}
	
	/**
	 * Realiza a distribuição de avaliações de apresentações de resumos do cic.
	 * <br>
	 * JSP: <ul>
	 * 			<li>/pesquisa/avaliacao_apresentacao_resumo/form_distribuicao.jsp</li>
	 * 		</ul>
	 * @return
	 * @throws ArqException
	 */
	public String distribuirAvaliacoesApresentacaoResumo() throws ArqException {
		checkRole(SigaaPapeis.ADMINISTRADOR_PESQUISA);
		if(!checkOperacaoAtiva(SigaaListaComando.DISTRIBUIR_AVALIACAO_APRESENTACAO_RESUMO_CIC.getId())){
			return cancelar();
		}
		
		ValidatorUtil.validateRequired(numeroAvaliacoes, "Número de avaliações por trabalho", erros);
		if(hasErrors()){
			return null;
		}
		
		MovimentoAvaliacaoResumo mov = new MovimentoAvaliacaoResumo();
		mov.setCodMovimento(SigaaListaComando.DISTRIBUIR_AVALIACAO_APRESENTACAO_RESUMO_CIC);
		mov.setNumeroAvaliacoes(numeroAvaliacoes);
		mov.setAvaliacoesApresentacao(avaliacoes);
		
		try {
			mov = (MovimentoAvaliacaoResumo) execute(mov);
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		}
		
		addMensagemInformation("Distribuição de avaliações de trabalhos realizada com sucesso!" +
				"<br/> No total, "+ mov.getAvaliacoesApresentacao().size() +" avaliações foram distribuídas.");
		
		return cancelar();
	}
	
	/**
	 * Popula os dados para ajustar a distribuição de avaliações de apresentações de resumos do CIC.
	 * <br>
	 * JSP: <ul>
	 * 			<li>/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/pesquisa/menu/iniciacao.jsp</li>
	 * 		</ul>
	 * Acessado a partir do método gravar()
	 * @return
	 * @throws ArqException
	 */
	public String popularAjustesDistribuicao() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_PESQUISA);
		prepareMovimento(SigaaListaComando.AJUSTAR_DISTRIBUICAO_AVALIACAO_APRESENTACAO_RESUMO_CIC);
		clear();
		return forward(JSP_AJUSTES_DISTRIBUICAO);
	}
	
	/**
	 * Carrega os avaliadores para o centro selecionado.
	 * <br>
	 * JSP: <ul>
	 * 			<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/avaliacao_apresentacao_resumo/ajustes_distribuicao.jsp</li>
	 *      	<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/avaliacao_apresentacao_resumo/lista_gestor.jsp</li>
	 *      </ul>
	 *      
	 * @param evt
	 * @throws DAOException
	 */
	public void carregaAvaliadores(ValueChangeEvent evt) throws DAOException{
		Integer idCentro = (Integer) evt.getNewValue();
		
		CongressoIniciacaoCientificaDao daoCongresso = getDAO(CongressoIniciacaoCientificaDao.class);
		AvaliacaoResumoDao daoAvaliacao = getDAO(AvaliacaoResumoDao.class);
		
		CongressoIniciacaoCientifica congresso = daoCongresso.findAtivo();
		
		avaliadoresCombo = toSelectItems(daoAvaliacao.findAvaliadoresApresentacaoResumoByCentro(congresso.getId(), idCentro), "id", "docente.nome");
	}
	
	/**
	 * Utilizado apenas pra ser chamado da view
	 * <br> 
	 * JSP: <ul>
	 * 			<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/avaliacao_apresentacao_resumo/ajustes_distribuicao.jsp</li>
	 * 			<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/avaliacao_apresentacao_resumo/lista_gestor.jsp</li>
	 * 		</ul>
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void carregaAvaliacoes(ValueChangeEvent evt) throws DAOException{
		Integer idAvaliador = (Integer) evt.getNewValue();
		avaliacoes = new ArrayList<AvaliacaoApresentacaoResumo>();		
		
		if(idAvaliador > 0){
			carregarAvaliacoesByAvaliador(idAvaliador);
		}
	}

	/**
	 * Carrega as avaliações do avaliador selecionado + as avaliações pendentes de acordo com o
	 * campo marcado no formulário.
	 * <br>
	 * 	<ul>
	 * 		<li>Acessador apartir do metodo carregaAvaliacoes() e listarResumosGestor()</li>
	 *  </ul>
	 * @param idAvaliador
	 * @throws DAOException
	 */
	private void carregarAvaliacoesByAvaliador(Integer idAvaliador)
			throws DAOException {
		CongressoIniciacaoCientificaDao daoCongresso = getDAO(CongressoIniciacaoCientificaDao.class);
		AvaliacaoResumoDao daoAvaliacao = getDAO(AvaliacaoResumoDao.class);
		ResumoCongressoDao daoResumo = getDAO(ResumoCongressoDao.class);
		
		CongressoIniciacaoCientifica congresso = daoCongresso.findAtivo();
		
		if(pendentes){
			avaliacoes = daoAvaliacao.findAvaliacoesApresentacaoResumoByAvaliador(idAvaliador);
			for(AvaliacaoApresentacaoResumo a: avaliacoes)
				a.setSelecionado(true);
			
			OrganizacaoPaineisCIC organizacao = daoCongresso.findOrganizacaoMaisRecente(congresso);
			organizacao.getDiasApresentacao().size();
			
			Collection<ResumoCongresso> resumos = daoResumo.filter(congresso.getId(), null, null, null, null, ResumoCongresso.APROVADO, null, null, null, Boolean.TRUE, organizacao.getIdsUnidadesMesmoDia(unidade));

//			if(unidade.getId() != Unidade.UNIDADE_DIREITO_GLOBAL)
//				resumos.addAll(daoResumo.filter(congresso.getId(), null, new Unidade(Unidade.UNIDADE_DIREITO_GLOBAL), null, null, ResumoCongresso.APROVADO, null, null, null, Boolean.TRUE));
//			else
//				resumos.addAll(daoResumo.filter(congresso.getId(), null, null, null, null, ResumoCongresso.APROVADO, null, null, null, Boolean.TRUE));
			
			for(ResumoCongresso r: resumos){
				AvaliacaoApresentacaoResumo av = new AvaliacaoApresentacaoResumo();
				av.setAvaliador(avaliador);
				av.setResumo(r);
				avaliacoes.add(av);
			}
		}else{
			avaliacoes = daoAvaliacao.findAvaliacoesApresentacaoResumoByAvaliador(idAvaliador);
			for(AvaliacaoApresentacaoResumo a: avaliacoes)
				a.setSelecionado(true);
		}
	}
	
	/**
	 * Persiste no banco a distribuição das avaliações dos resumos pra o avaliador
	 * <br>
	 * JSP: <ul>
	 * 		  <li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/avaliacao_apresentacao_resumo/ajustes_distribuicao.jsp</li>
	 * 		</ul>
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
		mov.setCodMovimento(SigaaListaComando.AJUSTAR_DISTRIBUICAO_AVALIACAO_APRESENTACAO_RESUMO_CIC);
		mov.setAvaliacoesApresentacao(avaliacoes);
		
		try {
			executeWithoutClosingSession(mov);
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		}
		
		addMensagemInformation("Ajuste na distribuição de avaliações de apresentação de resumos realizada com sucesso!");
		
		return popularAjustesDistribuicao();
	}

	/**
	 * Lista as avaliações de apresentações de resumo para o gestor de pesquisa.
	 * <br>
	 * JSP: <ul> 
	 * 			<li> /SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/pesquisa/menu/iniciacao.jsp </li>
	 * 		</ul> 
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String listarResumosGestor() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_PESQUISA);
		prepareMovimento(SigaaListaComando.AVALIAR_APRESENTACAO_RESUMO_CIC);
		pendentes = false;
		if(avaliador != null && avaliador.getId() > 0)
			carregarAvaliacoesByAvaliador( avaliador.getId() );
		return forward(JSP_LISTA_GESTOR);
	}
	
	/**
	 * Lista as avaliações de apresentações de resumo para o avaliador logado.
	 * <br>
	 * JSP: <ul>
	 * 			<li> /SIGAA/app/sigaa.ear/sigaa.war/portais/docente/menu_docente.jsp </li>
	 * 		</ul>
	 * @return
	 * @throws ArqException 
	 */
	public String listarResumosAvaliador() throws ArqException {
		CongressoIniciacaoCientificaDao daoCongresso = getDAO(CongressoIniciacaoCientificaDao.class);
		AvaliacaoResumoDao dao = getDAO(AvaliacaoResumoDao.class);
			
		Collection<AvaliadorCIC> avaliadores = null;
		if(getUsuarioLogado().getServidor() != null){
			congresso = daoCongresso.findAtivo();
			
			avaliadores = dao.findAvaliadorApresentacaoResumoByServidor(congresso.getId(), getUsuarioLogado().getServidor().getId());
			if(avaliadores == null || avaliadores.isEmpty()){
				addMensagemErro("Você não foi relacionado como Avaliador de Apresentação de Trabalho para o Congresso de Iniciação Científica atual.");
				return null;
			}

		} else {
			addMensagem(MensagensGerais.APENAS_DOCENTE_QUADRO_EFETIVO, "UFRN");
			return null;
		}
		
		Collection<Integer> idsAvaliadores = new ArrayList<Integer>();
		
		for(AvaliadorCIC ava: avaliadores){
			idsAvaliadores.add(ava.getId());
		}
		
		avaliacoes = dao.findAvaliacoesByAvaliadorApresentacao(idsAvaliadores);
		if(avaliacoes == null || avaliacoes.isEmpty()){
			addMensagemErro("Não há mais avaliações de apresentações de resumo destinadas a você.");
			return null;
		}
		
		return forward(JSP_LISTA_AVALIADOR);
	}
	
	/**
	 * redireciona para a operação desenvolvida em struts
	 * @param evt
	 */
	public void redirecionar(ActionEvent evt){
		String id = getParameter("id");
		String url = getParameter("url");
		redirect(url+id);
	}

	/**
	 * Popular os dados para a avaliação
	 * <br>
	 * JSP: <ul>
	 * 			<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/avaliacao_apresentacao_resumo/lista_avaliador.jsp</li>
	 * 			<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/avaliacao_apresentacao_resumo/lista_gestor.jsp</li>
	 * 		</ul>
	 * @return
	 * @throws ArqException 
	 */
	public String popularAvaliacao() throws ArqException {
		
		if(!isUserInRole(SigaaPapeis.GESTOR_PESQUISA)){
			CongressoIniciacaoCientificaDao daoCongresso = getDAO(CongressoIniciacaoCientificaDao.class);
			CongressoIniciacaoCientifica congresso = daoCongresso.findAtivo();
			// verifica se o congresso já iniciou
			if( congresso.getInicio().after(new Date()) ){
				addMensagemErro("A avaliação só pode ser realizada durante o período do Congresso.");
				return null;
			}
		}
		
		avaliacao = new AvaliacaoApresentacaoResumo();
		int id = getParameterInt("id", 0);
		
		if(id > 0) {
			avaliacao = getGenericDAO().findByPrimaryKey(id, AvaliacaoApresentacaoResumo.class);
		} else {
			addMensagemErro("Selecione uma apresentação de resumo para avaliar.");
			return null;
		}
		
		ItemAvaliacaoDao dao = getDAO(ItemAvaliacaoDao.class);
		
		// Montar formulário de avaliação
		if ( avaliacao.getNotasItens().isEmpty() ) {
			Collection<ItemAvaliacao> itens = dao.findByTipo(ItemAvaliacao.AVALIACAO_RESUMO);

			for (ItemAvaliacao item: itens) {
				NotaItem notaItem = new NotaItem();
				notaItem.setItemAvaliacao(item);
				notaItem.setAvaliacaoApresentacaoResumo(avaliacao);
				notaItem.setNota(5.0);

				avaliacao.getNotasItens().add(notaItem);
			}
		}

		// Ordenar itens
		List<NotaItem> notas = CollectionUtils.toArrayList( avaliacao.getNotasItens() );
		Collections.sort(notas);
		avaliacao.setNotasItens(notas);
		
		prepareMovimento(SigaaListaComando.AVALIAR_APRESENTACAO_RESUMO_CIC);
		return forward(JSP_FORM_AVALIACAO);
	}
	
	/**
	 * Chama o processador para persistir as notas da avaliação
	 * <br>
	 * 
	 * JSP: <ul>
	 * 			<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/avaliacao_apresentacao_resumo/lista_gestor.jsp</li>
	 * 		</ul>
	 * @return
	 * @throws ArqException
	 */
	public String avaliarResumo() throws ArqException {

		for (NotaItem lista : avaliacao.getNotasItens()) {
			if (lista.getNota() == null) {
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, lista.getItemAvaliacao().getDescricao());
			}
		}

		if (hasErrors())
			return null;
		
		MovimentoAvaliacaoResumo mov = new MovimentoAvaliacaoResumo();
		mov.setCodMovimento(SigaaListaComando.AVALIAR_APRESENTACAO_RESUMO_CIC);
		mov.getAvaliacoesApresentacao().add(avaliacao);
		
		try {
			executeWithoutClosingSession(mov);
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		}
		
		addMensagemInformation("Avaliação realizada com sucesso!");
		
		if(isUserInRole(SigaaPapeis.GESTOR_PESQUISA) && getSubSistema().equals(SigaaSubsistemas.PESQUISA))
			return listarResumosGestor();
		return listarResumosAvaliador();
	}

	/**
	 * Popula o formulário com os dados para emissão do relatório de distribuição de avaliações
	 * <br>
	 * JSP: <ul>
	 * 			<li>/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/pesquisa/menu/iniciacao.jsp</li>
	 * 		</ul>
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String popularRelatorioDistribuicao() throws SegurancaException, DAOException {
		clear();
		checkRole(SigaaPapeis.GESTOR_PESQUISA);
		CongressoIniciacaoCientificaDao daoCongresso = getDAO(CongressoIniciacaoCientificaDao.class);
		congresso = daoCongresso.findAtivo();
		return forward(JSP_FORM_RELATORIO_DISTRIBUICAO);
	}

	/**
	 * Emite o relatório de distribuição de avaliações
	 * <br>
	 * 
	 * JSP: <ul>
	 *			<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/avaliacao_apresentacao_resumo/form_relatorio_distribuicao.jsp</li> 		
	 * 		</ul>
	 * @return
	 * @throws ArqException
	 */
	public String relatorioDistribuicao() throws ArqException {
		AvaliacaoResumoDao dao = getDAO(AvaliacaoResumoDao.class);
		congresso = dao.findByPrimaryKey(congresso.getId(), CongressoIniciacaoCientifica.class);
		if(unidade != null && unidade.getId() > 0)
			unidade = dao.findByPrimaryKey(unidade.getId(), Unidade.class);
		lista = dao.findRelatorioDistribuicaoAvaliacaoApresentacao(congresso.getId(), unidade.getId());
		return forward(JSP_RELATORIO_DISTRIBUICAO);
	}

	/**
	 * Popula o formulário com os dados para emissão do relatório com a pontuação dos trabalhos
	 * <br>
	 * 
	 * JSP: <ul>
	 * 			<li>/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/pesquisa/menu/iniciacao.jsp</li>
	 * 			<li>/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/pesquisa/menu/relatorio.jsp</li>
	 * 		</ul>
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String popularRelatorioPontuacao() throws SegurancaException, DAOException {
		clear();
		checkRole(SigaaPapeis.GESTOR_PESQUISA);
		CongressoIniciacaoCientificaDao daoCongresso = getDAO(CongressoIniciacaoCientificaDao.class);
		congresso = daoCongresso.findAtivo();
		return forward(JSP_FORM_RELATORIO_PONTUACAO);
	}

	/**
	 * Emite o relatório com a pontuação dos trabalhos
	 * <br>
	 * 
	 * JSP: <ul>
	 * 			<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/avaliacao_apresentacao_resumo/form_relatorio_pontuacao.jsp</li>
	 * 		</ul>
	 * @return
	 * @throws ArqException
	 */
	public String relatorioPontuacao() throws ArqException {
		AvaliacaoResumoDao dao = getDAO(AvaliacaoResumoDao.class);
		congresso = dao.findByPrimaryKey(congresso.getId(), CongressoIniciacaoCientifica.class);
		if(unidade != null && unidade.getId() > 0)
			unidade = dao.findByPrimaryKey(unidade.getId(), Unidade.class);
		lista = dao.findRelatorioPontuacaoTrabalhos(congresso.getId(), unidade.getId());
		return forward(JSP_RELATORIO_PONTUACAO);
	}

	/**
	 * Popula os dados do formulário para emissão do relatório de avaliadores
	 * <br>
	 * JSP: <ul>
	 * 			<li>/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/pesquisa/menu/iniciacao.jsp</li>
	 * 			<li>/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/pesquisa/menu/relatorio.jsp</li>
	 * 		</ul>
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String popularRelatorioAvaliadores() throws SegurancaException, DAOException {
		clear();
		checkRole(SigaaPapeis.GESTOR_PESQUISA);
		CongressoIniciacaoCientificaDao daoCongresso = getDAO(CongressoIniciacaoCientificaDao.class);
		congresso = daoCongresso.findAtivo();
		return forward(JSP_FORM_RELATORIO_AVALIADORES);
	}

	/**
	 * Emite o relatório de avaliadores
	 * <br>
	 * JSP: <ul>
	 * 			<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/avaliador_cic/form_relatorio_avaliadores.jsp</li>
	 * 		</ul>
	 * @return
	 * @throws ArqException
	 */
	public String relatorioAvaliadores() throws ArqException {
		AvaliacaoResumoDao dao = getDAO(AvaliacaoResumoDao.class);
		congresso = dao.findByPrimaryKey(congresso.getId(), CongressoIniciacaoCientifica.class);
		if(unidade != null && unidade.getId() > 0)
			unidade = dao.findByPrimaryKey(unidade.getId(), Unidade.class);
		lista = dao.findRelatorioAvaliadoresApresentacao(congresso.getId(), unidade.getId());
		return forward(JSP_RELATORIO_AVALIADORES);
	}

	/**
	 * Popula o formulário para emissão do relatório com as fichas de avaliação de resumos
  	 * <br>
   	 * JSP: <ul>
	 * 		  <li>/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/pesquisa/menu/iniciacao.jsp</li>
	 *      </ul>
	 * @return
	 */
	public String popularRelatorioFichasAvaliacao() throws ArqException {
		clear();
		checkRole(SigaaPapeis.GESTOR_PESQUISA);
		CongressoIniciacaoCientificaDao daoCongresso = getDAO(CongressoIniciacaoCientificaDao.class);
		congresso = daoCongresso.findAtivo();
		return forward(JSP_FORM_RELATORIO_FICHAS);
	}

	/**
     * Realiza a geração do relatório, de acordo com os critérios selecionados
   	 * <br>
   	 * JSP: <ul>
	 * 		  <li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/avaliacao_apresentacao_resumo/form_relatorio_fichas.jsp</li>
	 *      </ul>
     * @return
     * @throws DAOException
     */
    public String relatorioFichasAvaliacao() throws DAOException {
    	int servidor = avaliador.getDocente().getId() > 0 ? avaliador.getDocente().getId() : -1;
        Connection con = null;
        try {
            // Popular parâmetros do relatório
            HashMap<String, Object> parametros = new HashMap<String, Object>();
            parametros.put("congresso", congresso.getId() );
            parametros.put("unidade", unidade.getId() );
            parametros.put("servidor",  servidor);
            parametros.put("subSistema", getSubSistema().getNome());
	        parametros.put("subSistemaLink", getSubSistema().getLink());

            // Preencher relatório
            con = Database.getInstance().getSigaaConnection();
            JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReportSIGAA("trf9932_FichaAvaliacao.jasper"), parametros, con );

            // Exportar relatório de acordo com o formato escolhido
            String nomeArquivo = "FichasAvaliacaoCIC.pdf";
            JasperReportsUtil.exportar(prt, nomeArquivo, getCurrentRequest(), getCurrentResponse(), "pdf");
            FacesContext.getCurrentInstance().responseComplete();

        } catch (Exception e) {
            e.printStackTrace();
            notifyError(e);
            addMensagemErro("Ocorreu um erro durante a geração deste relatório. Por favor, contacte o suporte através do \"Abrir Chamado\"");
            return null;
        } finally {
            Database.getInstance().close(con);
        }

        return null;
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

	public Collection<AvaliacaoApresentacaoResumo> getAvaliacoes() {
		return avaliacoes;
	}

	public void setAvaliacoes(Collection<AvaliacaoApresentacaoResumo> avaliacoes) {
		this.avaliacoes = avaliacoes;
	}

	public AvaliacaoApresentacaoResumo getAvaliacao() {
		return avaliacao;
	}

	public void setAvaliacao(AvaliacaoApresentacaoResumo avaliacao) {
		this.avaliacao = avaliacao;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public CongressoIniciacaoCientifica getCongresso() {
		return congresso;
	}

	public void setCongresso(CongressoIniciacaoCientifica congresso) {
		this.congresso = congresso;
	}

	public List<HashMap<String, Object>> getLista() {
		return lista;
	}

	public void setLista(List<HashMap<String, Object>> lista) {
		this.lista = lista;
	}
	
	public int getNumeroRegistrosEncontrados() {
		if(lista!=null)
			return lista.size();
		else
			return 0;
	}

	public Collection<NotaItem> getNotas() {
		return notas;
	}

	public void setNotas(Collection<NotaItem> notas) {
		this.notas = notas;
	}

	public Integer getNumeroAvaliacoes() {
		return numeroAvaliacoes;
	}

	public void setNumeroAvaliacoes(Integer numeroAvaliacoes) {
		this.numeroAvaliacoes = numeroAvaliacoes;
	}

	/**
	 * Verifica se está no período do congresso
	 * @return
	 * @throws DAOException 
	 */
	public boolean isPeriodoCongresso() throws DAOException{
		if(congresso == null || congresso.getAno() != CalendarUtils.getAnoAtual())
			congresso = getDAO(CongressoIniciacaoCientificaDao.class).findAtivo();
		Date agora = new Date();
		Date congressoDataFim = CalendarUtils.adicionaDias(congresso.getFim(), 1);
		return agora.getTime() >= congresso.getInicio().getTime() && agora.getTime() <= congressoDataFim.getTime();
	}

	public boolean isFiltroAvaliador() {
		return filtroAvaliador;
	}

	public void setFiltroAvaliador(boolean filtroAvaliador) {
		this.filtroAvaliador = filtroAvaliador;
	}

	public boolean isFiltroAluno() {
		return filtroAluno;
	}

	public void setFiltroAluno(boolean filtroAluno) {
		this.filtroAluno = filtroAluno;
	}

	public boolean isFiltroOrientador() {
		return filtroOrientador;
	}

	public void setFiltroOrientador(boolean filtroOrientador) {
		this.filtroOrientador = filtroOrientador;
	}

	public boolean isFiltroCodResumo() {
		return filtroCodResumo;
	}

	public void setFiltroCodResumo(boolean filtroCodResumo) {
		this.filtroCodResumo = filtroCodResumo;
	}

	public boolean isFiltroNumPainel() {
		return filtroNumPainel;
	}

	public void setFiltroNumPainel(boolean filtroNumPainel) {
		this.filtroNumPainel = filtroNumPainel;
	}

	public Integer getIdAvaliador() {
		return idAvaliador;
	}

	public void setIdAvaliador(Integer idAvaliador) {
		this.idAvaliador = idAvaliador;
	}

	public Integer getIdAluno() {
		return idAluno;
	}

	public void setIdAluno(Integer idAluno) {
		this.idAluno = idAluno;
	}

	public Integer getIdOrientador() {
		return idOrientador;
	}

	public void setIdOrientador(Integer idOrientador) {
		this.idOrientador = idOrientador;
	}

	public String getCodResumo() {
		return codResumo;
	}

	public void setCodResumo(String codResumo) {
		this.codResumo = codResumo;
	}

	public Integer getNumPainel() {
		return numPainel;
	}

	public void setNumPainel(Integer numPainel) {
		this.numPainel = numPainel;
	}

	public String getNomeAvaliador() {
		return nomeAvaliador;
	}

	public void setNomeAvaliador(String nomeAvaliador) {
		this.nomeAvaliador = nomeAvaliador;
	}

	public String getNomeAluno() {
		return nomeAluno;
	}

	public void setNomeAluno(String nomeAluno) {
		this.nomeAluno = nomeAluno;
	}

	public String getNomeOrientador() {
		return nomeOrientador;
	}

	public void setNomeOrientador(String nomeOrientador) {
		this.nomeOrientador = nomeOrientador;
	}

	public String buscarAvaliacoes() throws DAOException {
		
		if(filtroAvaliador)
			ValidatorUtil.validateRequired(idAvaliador, "Avaliador", erros);
		if(filtroAluno)
			ValidatorUtil.validateRequired(nomeAluno, "Aluno", erros);
		if(filtroOrientador)
			ValidatorUtil.validateRequired(idOrientador, "Orientador", erros);
		if(filtroCodResumo)
			ValidatorUtil.validateRequired(codResumo, "Código do Resumo", erros);
		if(filtroNumPainel)
			ValidatorUtil.validateRequired(numPainel, "Número do Painel", erros);
		
		if(hasErrors()){
			addMensagens(erros);
			return null;
		}
		
		AvaliacaoResumoDao dao = getDAO(AvaliacaoResumoDao.class);
		avaliacoes = dao.filter(getDAO(CongressoIniciacaoCientificaDao.class).findAtivo(), idAvaliador, nomeAluno, idOrientador, codResumo, numPainel);
		return null;
	}
}