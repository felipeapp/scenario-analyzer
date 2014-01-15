/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/10/2006
 *
 */
package br.ufrn.sigaa.pesquisa.struts;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.FacesContextUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.comum.dominio.notificacoes.Destinatario;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.ensino.DocenteExternoDao;
import br.ufrn.sigaa.arq.dao.graduacao.InteressadoBolsaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.CalendarioPesquisaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.CotaDocenteDao;
import br.ufrn.sigaa.arq.dao.pesquisa.MembroProjetoDiscenteDao;
import br.ufrn.sigaa.arq.dao.pesquisa.PlanoTrabalhoDao;
import br.ufrn.sigaa.arq.dao.prodocente.EmissaoRelatorioMediaDao;
import br.ufrn.sigaa.arq.dao.sae.AdesaoCadastroUnicoBolsaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.assistencia.cadunico.dominio.AdesaoCadastroUnicoBolsa;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.graduacao.dominio.InteressadoBolsa;
import br.ufrn.sigaa.ensino.graduacao.dominio.TipoInteressadoBolsa;
import br.ufrn.sigaa.ensino.graduacao.jsf.AtestadoMatriculaMBean;
import br.ufrn.sigaa.ensino.jsf.HistoricoMBean;
import br.ufrn.sigaa.pesquisa.dominio.CalendarioPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.CotaDocente;
import br.ufrn.sigaa.pesquisa.dominio.Cotas;
import br.ufrn.sigaa.pesquisa.dominio.MembroProjetoDiscente;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa;
import br.ufrn.sigaa.pesquisa.form.PlanoTrabalhoForm;
import br.ufrn.sigaa.pesquisa.jsf.EnviarMensagemInteressadosMBean;
import br.ufrn.sigaa.pesquisa.negocio.IndicarBolsistaValidator;
import br.ufrn.sigaa.pesquisa.negocio.MovimentoIndicarBolsista;
import br.ufrn.sigaa.pessoa.dominio.Banco;
import br.ufrn.sigaa.pessoa.dominio.ContaBancaria;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Action para gerenciar a indicação de bolsista em um plano de trabalho
 * 
 * @author Victor Hugo
 *
 */
public class IndicarBolsistaAction extends AbstractCrudAction {

	/** Encaminhamento do Struts para a ação de popular */
	public static final String POPULAR = "popular";

	/** Encaminhamento do Struts para a ação de indicar */
	public static final String INDICAR = "indicar";

	/** Encaminhamento do Struts para a ação de listar */
	public static final String LISTAR = "listar";
	
	/** Encaminhamento do Struts para a ação de listar planos para o gestor de pesquisa */
	public static final String LISTAR_PLANOS_GESTOR = "listar_planos_gestor";
	
	/** Encaminhamento do Struts para a ação de listar bolsistas para o gestor de pesquisa */
	public static final String LISTAR_BOLSISTAS_GESTOR = "listar_bolsistas_gestor";

	/** Encaminhamento do Struts para a ação de finalizar */
	public static final String FINALIZAR = "finalizar";

	/** Encaminhamento do Struts para a ação de resumo */
	public static final String RESUMO = "resumo";
	
	/** Encaminhamento do Struts para a ação de resultado */
	public static final String RESULTADO = "resultado";
	
	/** Constante que armazena a operação ativa. */
	public static final String OPERACAO_ATIVA = "indicarBolsista";


	/**
	 * Popula os atributos de sessão para serem usados em todo o caso de uso.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/portais/docente/menu_docente_testes.jsp</li>
	 *		<li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/menus/portal_docente.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/menu/projeto.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/PlanoTrabalho/finalizar_bolsista.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/PlanoTrabalho/relatorio.jsp</li>
	 *		<li>sigaa.war/menu_pesquisa.jsp</li>
	 *	</ul>
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward popular(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		clearSession(request);
		removeSession(mapping.getName(), request);
		clearSteps(request);
		setOperacaoAtiva(request, OPERACAO_ATIVA, null);

		if (isUserInRole(SigaaPapeis.GESTOR_PESQUISA, request) && getSubSistemaCorrente(request).equals(SigaaSubsistemas.PESQUISA)) {
			return mapping.findForward(getSubSistemaCorrente(request).getForward());
		} else {
			// Verificar se o usuário é um docente da UFRN
			if ( ((Usuario) getUsuarioLogado(request)).getServidor() == null && !isUserInRole(SigaaPapeis.GESTOR_PESQUISA, request) ) {
				addMensagemErro("Você não possui permissão para realizar esta operação", request);
				return cancelar(mapping,form,request,response);
			}

			// Buscar os planos de trabalho passíveis de indicação
			Collection<PlanoTrabalho> planos = getPlanosTrabalho(request);
			addSession( "planos", planos, request );

			// Buscar as cotas ativas para o docente
			CotaDocenteDao cotaDocenteDao = getDAO(CotaDocenteDao.class, request);
			Collection<CotaDocente> cotasDocente = cotaDocenteDao.findByDocenteAndAno( ((Usuario) getUsuarioLogado(request)).getServidor() , getAnoAtual() );
			request.setAttribute("cotasDocente", cotasDocente);

			Map<Integer, Double> mediasDocente = new HashMap<Integer, Double>();
			EmissaoRelatorioMediaDao mediaDao = getDAO(EmissaoRelatorioMediaDao.class, request);
			for ( CotaDocente cota : cotasDocente ) {
				if ( cota.getEmissaoRelatorio() != null ) {
					Double media = mediaDao.findMediaByClassificacaoAndUnidade( cota.getEmissaoRelatorio().getClassificacaoRelatorio(), cota.getDocente().getUnidade() );
					mediasDocente.put(cota.getId(), media);
				}
			}
			request.setAttribute("mediasDocente", mediasDocente);

		}

		prepareMovimento(SigaaListaComando.INDICAR_BOLSISTA,request);
		return mapping.findForward(LISTAR);
	}
	

	/** Recupera os planos de trabalho do docente/docente externo
	 * @param request
	 * @return
	 * @throws ArqException
	 * @throws DAOException
	 */
	private Collection<PlanoTrabalho> getPlanosTrabalho(HttpServletRequest request) throws ArqException, DAOException {
		PlanoTrabalhoDao daoPlano = getDAO(PlanoTrabalhoDao.class, request);
		Collection<PlanoTrabalho> planos;
		if (((Usuario) getUsuarioLogado(request)).getVinculoAtivo().getDocenteExterno() != null)
			planos = daoPlano.findParaIndicacao(0, ((Usuario) getUsuarioLogado(request)).getVinculoAtivo().getDocenteExterno().getId() );
		else
			planos = daoPlano.findParaIndicacao( ( (Usuario) getUsuarioLogado(request)).getServidorAtivo().getId(), 0 );
		return planos;
	}	
	
	/**
	 * Redireciona para página com o resultado da distribuição da cota.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 *	</ul>
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward verResultadoCota(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// Verificar se o usuário é um docente
		if ( !getAcessoMenu(request).isDocente() && !isUserInRole(SigaaPapeis.GESTOR_PESQUISA, request)) {
			addMensagemErro("Você não possui permissão para realizar esta operação", request);
			return cancelar(mapping,form,request,response);
		}
		
		if(((Usuario) getUsuarioLogado(request)).getVinculoAtivo().getDocenteExterno() != null){
			addMensagemErro("Docentes externos não recebem cotas", request);
			return cancelar(mapping,form,request,response);
		}

		// Buscar as cotas ativas para o docente
		CotaDocenteDao cotaDocenteDao = getDAO(CotaDocenteDao.class, request);
		Collection<CotaDocente> cotasDocente = cotaDocenteDao.findByDocenteAndAno( ((Usuario) getUsuarioLogado(request)).getServidor() , getAnoAtual() );
		request.setAttribute("cotasDocente", cotasDocente);

		Map<Integer, Double> mediasDocente = new HashMap<Integer, Double>();
		EmissaoRelatorioMediaDao mediaDao = getDAO(EmissaoRelatorioMediaDao.class, request);
		for ( CotaDocente cota : cotasDocente ) {
			if ( cota.getEmissaoRelatorio() != null ) {
				Double media = mediaDao.findMediaByClassificacaoAndUnidade( cota.getEmissaoRelatorio().getClassificacaoRelatorio(), cota.getDocente().getUnidade() );
				mediasDocente.put(cota.getId(), media);
			}
		}
		request.setAttribute("mediasDocente", mediasDocente);
		
		return mapping.findForward(RESULTADO);
	}

	/**
	 * Prepara as informações para a indicação do novo bolsista.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/MembroProjetoDiscente/relatorio.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/PlanoTrabalho/lista_bolsistas.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/PlanoTrabalho/relatorio.jsp</li>
	 *	</ul>
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward indicar(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse response)
			throws Exception {
		setOperacaoAtiva(req, OPERACAO_ATIVA, null);
		PlanoTrabalhoDao dao = getDAO(PlanoTrabalhoDao.class, req);

		// Verificar permissões
		if (!getAcessoMenu(req).isDocente()) {
			checkRole(SigaaPapeis.GESTOR_PESQUISA, req);
			prepareMovimento(SigaaListaComando.INDICAR_BOLSISTA,req);
		}

		// Buscar dados do plano
		PlanoTrabalhoForm planoForm = (PlanoTrabalhoForm) form;

		PlanoTrabalho plano = dao.findAndFetch(planoForm.getObj().getId(), PlanoTrabalho.class, "projetoPesquisa", "projetoPesquisa.edital");
		
		if (plano == null) {
			addMensagemErro("O plano de trabalho informado não foi localizado na base de dados.", req);
			return mapping.findForward(getSubSistemaCorrente(req).getForward());
		}
		
		if( plano.getMembroProjetoDiscente() == null || plano.getMembroProjetoDiscente().isInativo() )
			plano.setMembroProjetoDiscente( new MembroProjetoDiscente() );

		planoForm.setPermissaoGestor(isUserInRole(SigaaPapeis.GESTOR_PESQUISA, req) && SigaaSubsistemas.PESQUISA.equals(getSubSistemaCorrente(req)));

		// Preparar formulário para indicação
		if ( plano.getMembroProjetoDiscente().getId() > 0 ) {
			planoForm.setBolsistaAtual( plano.getMembroProjetoDiscente() );
			planoForm.setDataIndicacao( Formatador.getInstance().formatarData(new Date()) );
			if( plano.getMembroProjetoDiscente().getBolsistaAnterior() != null )
				planoForm.setBolsistaAnterior( plano.getMembroProjetoDiscente().getBolsistaAnterior() );
		} else {
			planoForm.setBolsistaAtual(null);
			planoForm.setDataIndicacao( Formatador.getInstance().formatarData(new Date()) );
		}
		
		// Verificar se o plano já possui a cota definida
		if ( plano.getTipoBolsa().getId() == TipoBolsaPesquisa.A_DEFINIR && plano.getCota() != null && plano.getEdital() != null) {

			// Verificar se o prazo para indicações de novos bolsistas está válido
			if (!isUserInRole(SigaaPapeis.GESTOR_PESQUISA, req) && !isDentroPeriodoNovasIndicacoes(req) &&
					getDAO(MembroProjetoDiscenteDao.class, req).findAllByPlanoTrabalho(plano).isEmpty()) {
				
				CalendarioPesquisa cp = getDAO(CalendarioPesquisaDao.class, req).findVigente();
				StringBuilder msg = new StringBuilder("O período para indicações de novos bolsistas não está ");
				if (cp.getInicioIndicacaoBolsista() != null && cp.getFimIndicacaoBolsista() != null)
					msg.append("vigente ( " + Formatador.getInstance().formatarData(cp.getInicioIndicacaoBolsista()) + 
							" até " + Formatador.getInstance().formatarData(cp.getFimIndicacaoBolsista()) + " ).");
				else
					msg.append("definido no calendário.");
				addMensagemErro(msg.toString(), req);
				return popular(mapping, form, req, response);
			}

			carregarCotaBolsa(req, dao, planoForm, plano, true);

		} else {
			planoForm.setTipoBolsa(plano.getTipoBolsa().getId());
			planoForm.setNiveisPermitidos(plano.getTipoBolsa().getNiveisPermitidos());
		}

		plano.setMembroProjetoDiscente(new MembroProjetoDiscente());
		plano.getMembroProjetoDiscente().getDiscente().getPessoa().setContaBancaria( new ContaBancaria() );

		plano.setCodMovimento( SigaaListaComando.INDICAR_BOLSISTA );
		planoForm.setObj(plano);

		addSession("bancos", dao.findAllAtivos(Banco.class, "codigo"), req);
		addSession("tipoConta", planoForm.getAllTiposConta(), req);

		// Limpa os dados, caso tenha requisições anteriores
		planoForm.setDiscentesAdesao(new ArrayList<AdesaoCadastroUnicoBolsa>());
		
		DiscenteDao discenteDao = getDAO(DiscenteDao.class, req);
		
		List<AdesaoCadastroUnicoBolsa> discentesAdesao = new ArrayList<AdesaoCadastroUnicoBolsa>();
		
		List<Discente> discentesCadastroUnico = discenteDao.findAllInteressadoBolsa(TipoInteressadoBolsa.PESQUISA, plano.getId());
		
		if (discentesCadastroUnico != null && !discentesCadastroUnico.isEmpty()) {
			
			AdesaoCadastroUnicoBolsaDao adesaoDao = getDAO(AdesaoCadastroUnicoBolsaDao.class, req);
			
			for (Discente discente : discentesCadastroUnico) {
				AdesaoCadastroUnicoBolsa adesao = adesaoDao.findByDiscente(discente.getId(), 0, 0);
				if (adesao != null){
					adesao.getDiscente().getMatricula();
					adesao.getDiscente().getPessoa().getNome();
					discentesAdesao.add(adesao);
				}
			}
			Collections.sort(discentesAdesao);
			
			planoForm.setDiscentesAdesao(discentesAdesao);
		} else {
			addMessage(req, "Professor, nenhum aluno registrou interesse neste plano de trabalho.", TipoMensagemUFRN.WARNING);
		}
		
		planoForm.setInteressadoBolsa(null);
		
		prepareMovimento(SigaaListaComando.INDICAR_BOLSISTA,req);
		return mapping.findForward(INDICAR);
	}

	/**
	 * Carrega as opções de tipos de bolsa e o dia limite para regularização da bolsa no sistema.
	 * @param req
	 * @param dao
	 * @param planoForm
	 * @param plano
	 * @param indicacao
	 * @throws ArqException
	 * @throws DAOException
	 */
	private void carregarCotaBolsa(HttpServletRequest req, PlanoTrabalhoDao dao, PlanoTrabalhoForm planoForm, PlanoTrabalho plano, boolean indicacao) throws ArqException, DAOException {
		if ( plano.getCota() != null ) {
			CotaDocenteDao cotaDocenteDao = getDAO(CotaDocenteDao.class, req);
			Collection<CotaDocente> cotasRecebidas = cotaDocenteDao.findByDocentePeriodoCota( plano.getOrientador() , plano.getCota()) ;
	
			Map<Integer, String> tiposBolsa = new HashMap<Integer, String>();
			Map<String, Integer> diaLimite = new HashMap<String, Integer>();
	
			// Se o docente tiver recebido cotas, deixar ele selecionar o tipo da bolsa
			if ( cotasRecebidas != null && !cotasRecebidas.isEmpty() ) {
			    Map<Integer, Object[]> tiposBolsaRecebidas = new HashMap<Integer, Object[]>();
	
				for(CotaDocente cotaDocente: cotasRecebidas){
					if(cotaDocente.getQtdCotas() > 0){
						for(Cotas c: cotaDocente.getCotas()){
						    if(c.getQuantidade() > 0) {
						        int id = c.getTipoBolsa().getId();
			                    if(tiposBolsaRecebidas.containsKey(id)) {
						            tiposBolsaRecebidas.put(id, new Object[]{(Integer)tiposBolsaRecebidas.get(id)[0] + c.getQuantidade(), c.getTipoBolsa().getDescricaoResumida()});
						            diaLimite.put( c.getTipoBolsa().getDescricaoResumida(), 
						            			indicacao ? c.getTipoBolsa().getDiaLimiteIndicacao() : c.getTipoBolsa().getDiaLimiteFinalizacao());
			                    } else {
						            tiposBolsaRecebidas.put(id, new Object[]{c.getQuantidade(), c.getTipoBolsa().getDescricaoResumida()});
						            diaLimite.put( c.getTipoBolsa().getDescricaoResumida(), 
						            			indicacao ? c.getTipoBolsa().getDiaLimiteIndicacao() : c.getTipoBolsa().getDiaLimiteFinalizacao());
			                    }
						    }
						}
					}
				}
				
				for(Integer idTipoBolsaRecebida: tiposBolsaRecebidas.keySet()) {
				    // Popular o formulário com as bolsas disponíveis que o docente ainda possuir 
				    if((Integer)tiposBolsaRecebidas.get(idTipoBolsaRecebida)[0] > dao.findByTipoBolsaOrientador(idTipoBolsaRecebida, plano.getOrientador().getId(),
				            plano.getCota().getId()).size())
				        tiposBolsa.put(idTipoBolsaRecebida, (String)tiposBolsaRecebidas.get(idTipoBolsaRecebida)[1]);
				}
			}
			tiposBolsa.put(TipoBolsaPesquisa.VOLUNTARIO, TipoBolsaPesquisa.getDescricao(TipoBolsaPesquisa.VOLUNTARIO));
	
			planoForm.getReferenceData().put("tiposBolsa", tiposBolsa);
			planoForm.getReferenceData().put("diaLimite", diaLimite);
		}
	}
	
	/**
	 * Utilizado para indicar quais os níveis de ensino permitidos para uma bolsa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/PlanoTrabalho/indicar_bolsista.jsp</li>
	 *	</ul>
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward tratarTipoBolsa(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		
		// Buscar dados do plano
		PlanoTrabalhoForm planoForm = (PlanoTrabalhoForm) form;
		GenericDAO dao = getGenericDAO(req);
		
		int idTipoBolsa = planoForm.getTipoBolsa();
		
		if(idTipoBolsa > 0)
			planoForm.getObj().setTipoBolsa(dao.findByPrimaryKey(idTipoBolsa, TipoBolsaPesquisa.class));
		
		planoForm.setNiveisPermitidos(planoForm.getObj().getTipoBolsa().getNiveisPermitidos());
		
		return mapping.findForward(INDICAR);
	}

	/**
	 * Verificar se o prazo para indicações de novos bolsistas está válido
	 * 
	 * @param req
	 * @return
	 * @throws ArqException
	 */
	private boolean isDentroPeriodoNovasIndicacoes(HttpServletRequest req) throws ArqException {
		CalendarioPesquisaDao calendarioDao = getDAO(CalendarioPesquisaDao.class, req);
		CalendarioPesquisa calendario = calendarioDao.findVigente();
		if (calendario.getInicioIndicacaoBolsista() == null || calendario.getFimIndicacaoBolsista() == null) {
			return false;
		}
		Date hoje = DateUtils.truncate( new Date(), Calendar.DAY_OF_MONTH );

		return hoje.compareTo( calendario.getInicioIndicacaoBolsista() ) >= 0
			&& hoje.compareTo( calendario.getFimIndicacaoBolsista() ) <= 0;
	}

	/**
	 * Invoca o processador.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/PlanoTrabalho/indicar_bolsista.jsp</li>
	 *	</ul>
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward chamaModelo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		boolean ativa = false;
		PlanoTrabalhoForm planoForm = (PlanoTrabalhoForm) form;
		planoForm.setInteressadoBolsa(null);
		PlanoTrabalhoDao planoDao = new PlanoTrabalhoDao();
		
		if (request.getSession().getAttribute("operacaoAtiva") != null) {
			if (request.getSession().getAttribute("operacaoAtiva").toString()
					.equals(OPERACAO_ATIVA))
				ativa = true;
		}
		
		if(!ativa){
			addMensagemErro("A operação não está mais ativa. Por favor, reinicie os procedimentos.", request);
			return mapping.findForward(getSubSistemaCorrente(request).getForward());
		}

		// Validações feitas na indicação
		ListaMensagens lista = new ListaMensagens();
		IndicarBolsistaValidator.validaIndicacao( planoForm.getObj(), planoForm, lista);
		planoForm.getObj().setEdital(null);

		if (lista.isErrorPresent()) {
			addMensagens(lista.getMensagens(),request);
			return mapping.findForward(INDICAR);
		}
		
		PlanoTrabalho plano = planoForm.getObj();

		try {
			if (planoForm.getTipoBolsa() != 0 ) {
				plano.setTipoBolsa( planoDao.findByPrimaryKey(
						planoForm.getTipoBolsa(), TipoBolsaPesquisa.class) );
			}
		} finally {
			planoDao.close();
		}

		MovimentoIndicarBolsista mov = new MovimentoIndicarBolsista();

		mov.setCodMovimento( SigaaListaComando.INDICAR_BOLSISTA );
		if (plano.getTipoBolsa().getId() == TipoBolsaPesquisa.A_DEFINIR) {
			plano.setTipoBolsa( new TipoBolsaPesquisa(planoForm.getTipoBolsa()) );
		}

		mov.setPlanoTrabalho( plano );
		if ( planoForm.getBolsistaAtual() != null ) {
			// Setando o bolsista anterior
			mov.setBolsistaAnterior( planoForm.getBolsistaAtual() );
			mov.setDataFinalizacao( planoForm.getBolsistaAtual().getDataFim() );
		}
		mov.setDataIndicacao( planoForm.getObj().getMembroProjetoDiscente().getDataInicio() );

		try {
			execute( mov, request );
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens().getMensagens(), request);
			return mapping.findForward(INDICAR);
		}

		request.getSession(false).removeAttribute(mapping.getName());

		addInformation("Bolsista indicado com sucesso.", request);
		addInformation( "<b>ATENÇÃO!!</b> Verifique se os dados do aluno estão completos.<br/>" +
				"Em caso negativo, o aluno deve procurar <b>IMEDIATAMENTE</b> a Coordenação" +
				" do seu Curso para atualizar seus dados pessoais no Sistema Acadêmico, " +
				"<b>sob pena de não ter a bolsa efetivada!</b>", request);
		if (planoForm.getObj().getTipoBolsa().getId() == TipoBolsaPesquisa.PROPESQ) {
			addInformation("Lembre-se: somente quando a bolsa for regularizada pelo Setor de Bolsas este bolsista irá se tornar ativo!", request);
		}

		removeOperacaoAtiva(request);

		return resumo(mapping, planoForm, request, response);
	}

	/**
	 * Método utilizado para a remoção de um bolsista.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *    <li>/sigaa.war/WEB-INF/jsp/pesquisa/MembroProjetoDiscente/relatorio.jsp</li>
	 *	  <li>/sigaa.war/WEB-INF/jsp/pesquisa/PlanoTrabalho/lista_bolsistas.jsp</li>
	 *    <li>/sigaa.war/WEB-INF/jsp/pesquisa/PlanoTrabalho/relatorio.jsp</li>    
	 *  </ul>
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward remover(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		prepareMovimento(SigaaListaComando.REMOVER_BOLSISTA,request);
		PlanoTrabalhoForm planoForm = (PlanoTrabalhoForm) form;
		GenericDAO dao = getGenericDAO(request);
		PlanoTrabalho plano = dao.refresh( planoForm.getObj() );
		
		if (plano == null) {
			addMensagemErro("O plano de trabalho informado não foi localizado na base de dados.", request);
			return mapping.findForward(getSubSistemaCorrente(request).getForward());
		}
		
		planoForm.setObj(plano);
		
		ListaMensagens lista = new ListaMensagens();
		IndicarBolsistaValidator.validaRemocao( planoForm.getObj(), null, lista);

		if (lista.isErrorPresent()) {
			addMensagens(lista.getMensagens(), request);
			if(isUserInRole(SigaaPapeis.GESTOR_PESQUISA, request) && SigaaSubsistemas.PESQUISA.equals(getSubSistemaCorrente(request))){
				request.setAttribute("popular", Boolean.TRUE);
				return mapping.findForward(LISTAR_PLANOS_GESTOR);
			}
			return mapping.findForward(LISTAR);
		}

		planoForm.setBolsistaAtual( plano.getMembroProjetoDiscente() );
		planoForm.setPermissaoGestor(isUserInRole(SigaaPapeis.GESTOR_PESQUISA, request) && SigaaSubsistemas.PESQUISA.equals(getSubSistemaCorrente(request)));
		planoForm.setDataFinalizacao( Formatador.getInstance().formatarData(new Date()) );
		planoForm.getBolsistaAtual().setDataFinalizacao(new Date());
		carregarCotaBolsa(request,  getDAO(PlanoTrabalhoDao.class, request), planoForm, plano, false);

		return mapping.findForward(FINALIZAR);
	}

	/**
	 * Remove um bolsista de plano de trabalho.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/PlanoTrabalho/finalizar_bolsista.jsp</li>
	 *	</ul>
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward removerBolsista(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		prepareMovimento(SigaaListaComando.REMOVER_BOLSISTA,request);
		PlanoTrabalhoForm planoForm = (PlanoTrabalhoForm) form;

		ListaMensagens lista = new ListaMensagens();
		IndicarBolsistaValidator.validaRemocao( planoForm.getObj(), planoForm, lista );
		
		if (lista.isErrorPresent()) {
			addMensagens(lista.getMensagens(),request);
			return mapping.findForward(FINALIZAR);
		}
		
		planoForm.getBolsistaAtual().setMotivoSubstituicao(planoForm.getMotivo());
		
		MovimentoIndicarBolsista mov = new MovimentoIndicarBolsista();
		mov.setCodMovimento( SigaaListaComando.REMOVER_BOLSISTA );
		mov.setPlanoTrabalho( planoForm.getObj() );
		if ( planoForm.getBolsistaAtual() != null ) {
			mov.setBolsistaAnterior( planoForm.getBolsistaAtual() );
			mov.setDataFinalizacao( planoForm.getBolsistaAtual().getDataFim() );
		}

		try {
			execute( mov, request );
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens().getMensagens(), request);
			return mapping.findForward(LISTAR);
		}

		request.getSession(false).removeAttribute("planoTrabalhoForm");

		addInformation("Bolsista finalizado com sucesso!", request);

		if (isUserInRole(SigaaPapeis.GESTOR_PESQUISA, request) && getSubSistemaCorrente(request).equals(SigaaSubsistemas.PESQUISA)) {
			request.setAttribute("popular", true);
			return mapping.findForward(LISTAR_BOLSISTAS_GESTOR);
		} else {
			Collection<PlanoTrabalho> planos = getPlanosTrabalho(request);
			addSession( "planos", planos, request );
			prepareMovimento(SigaaListaComando.INDICAR_BOLSISTA,request);
			
			CotaDocenteDao cotaDocenteDao = getDAO(CotaDocenteDao.class, request);
            Collection<CotaDocente> cotasDocente = cotaDocenteDao.findByDocenteAndAno( ((Usuario) getUsuarioLogado(request)).getServidor() , getAnoAtual() );
            request.setAttribute("cotasDocente", cotasDocente);

            Map<Integer, Double> mediasDocente = new HashMap<Integer, Double>();
            EmissaoRelatorioMediaDao mediaDao = getDAO(EmissaoRelatorioMediaDao.class, request);
            for ( CotaDocente cota : cotasDocente ) {
                if ( cota.getEmissaoRelatorio() != null ) {
                    Double media = mediaDao.findMediaByClassificacaoAndUnidade( cota.getEmissaoRelatorio().getClassificacaoRelatorio(), cota.getDocente().getUnidade() );
                    mediasDocente.put(cota.getId(), media);
                }
            }
            request.setAttribute("mediasDocente", mediasDocente);
		}

		return mapping.findForward(LISTAR);

	}

	/**
	 * Resumo da Indicação/Substituição de Bolsista.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/pesquisa/homologacao_bolsas_sipac/lista.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/MembroProjetoDiscente/relatorio.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/PlanoTrabalho/relatorio_substituicoes.jsp</li>
	 *	</ul>
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward resumo(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse response)
			throws Exception {

		UsuarioDao dao = getDAO(UsuarioDao.class, req);
		DocenteExternoDao docenteExternoDao = getDAO(DocenteExternoDao.class, req);
		MembroProjetoDiscenteDao membroDao = getDAO(MembroProjetoDiscenteDao.class, req);
		PlanoTrabalhoDao planoDao = getDAO(PlanoTrabalhoDao.class, req);
		PlanoTrabalhoForm planoForm = (PlanoTrabalhoForm) form;
		PlanoTrabalho plano = planoDao.filter(null, null, null, null, null, null, null, null, null, planoForm.getObj().getId(), null, false).iterator().next();
		
		MembroProjetoDiscente bolsista = ( plano.getMembroProjetoDiscente() != null ) 
				? dao.findByPrimaryKey(plano.getMembroProjetoDiscente().getId(), MembroProjetoDiscente.class)
				: membroDao.findUltimoDiscenteFinalizado(plano);
		bolsista.getPlanoTrabalho().setProjetoPesquisa(plano.getProjetoPesquisa());
		if(bolsista.getBolsistaAnterior() == null){
			bolsista.setBolsistaAnterior(membroDao.findUltimoDiscenteFinalizado(bolsista));
		}
		
		Usuario orientador = null;
		if(plano.getOrientador() != null)
			orientador = dao.findByServidor(plano.getOrientador());
		else
			orientador = docenteExternoDao.findUsuarioByDocenteExterno(plano.getExterno());

		req.setAttribute("bolsista", bolsista);
		req.setAttribute("orientador", orientador);
		return mapping.findForward(RESUMO);
	}
	
	/**
	 * Histórico do aluno.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/PlanoTrabalho/indicar_bolsista.jsp</li>
	 *	</ul>
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward verHistorico(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse response)
			throws Exception {


		Integer idDiscente = getParameterInt(req, "idAluno");
		
		HistoricoMBean historicoMBean = getMBean("historico", req, response);

		Discente discente = getGenericDAO(req).findByPrimaryKey(idDiscente, Discente.class);
		historicoMBean.setDiscente(discente);
		historicoMBean.selecionaDiscente();
		return null;
	}
	
	/**
	 * Exibe as qualificações do aluno selecionado.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/PlanoTrabalho/indicar_bolsista.jsp</li>
	 *	</ul>
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward verQualificacao(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse response)
			throws Exception {


		Integer idDiscente = getParameterInt(req, "idAluno");
		Integer idPlanoTrabalho = getParameterInt(req, "idPlanoTrabalho");
		
		InteressadoBolsaDao dao = getDAO(InteressadoBolsaDao.class, req);
		
		InteressadoBolsa interessadoBolsa = dao.findByDiscente(idPlanoTrabalho, TipoInteressadoBolsa.PESQUISA.getId(), idDiscente);
		
		PlanoTrabalhoForm planoForm = (PlanoTrabalhoForm) form;
		
		planoForm.setInteressadoBolsa(interessadoBolsa);
		
		return mapping.findForward(INDICAR);
	}	
	
	/**
	 * Mostra o atestado de matrícula do discente selecionado.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/PlanoTrabalho/indicar_bolsista.jsp</li>
	 *	</ul>
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward verAtestado(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse response)
			throws Exception {


		Integer idDiscente = getParameterInt(req, "idAluno");
		
		AtestadoMatriculaMBean atestado = getMBean("atestadoMatricula", req, response);

		Discente discente = getGenericDAO(req).findByPrimaryKey(idDiscente, Discente.class);
		
		atestado.setDiscente(discente);
		atestado.selecionaDiscente();
		
		
		FacesContextUtils.getFacesContext(req, response).responseComplete();

		return null;
	}
	
	
	/**
	 * Chama O MBean responsável por enviar email para os interessados
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/PlanoTrabalho/indicar_bolsista.jsp</li>
	 *	</ul>
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param response
	 * @return
	 * @throws Exception
	 */	
	public ActionForward enviarEmail(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse response)
			throws Exception {
		
		setOperacaoAtiva(req, "enviarMensagem");
		
		Integer idPlanoTrabalho = getParameterInt(req, "idPlanoTrabalho");
		
		EnviarMensagemInteressadosMBean not = getMBean("enviarMsgInteressados", req, response);
			
		InteressadoBolsaDao dao = getDAO(InteressadoBolsaDao.class, req);
		
 		String remetente =  getUsuarioLogado(req).getNome();
 		
		try{
			List<Map<String, Object>> interessados = dao.findEmailInteressadosByOportunidade(idPlanoTrabalho);			
			
			if (interessados.size() > 0){
				ArrayList<Destinatario> destinatarios = new ArrayList<Destinatario>();
				for (Map<String, Object> lista : interessados) {
					
					String nome = String.valueOf( lista.get("nome") );
					String email = String.valueOf( lista.get("email") );
					int idUsuario = Integer.parseInt(""+ lista.get("id_usuario") );
					
					Destinatario destinatario = new Destinatario(nome, email);
					destinatario.setIdusuario(idUsuario);
					
					destinatarios.add(destinatario);
				}			
				
				req.getSession().setAttribute("idPlanoTrabalho", idPlanoTrabalho);
				req.getSession().setAttribute("alunosInteressados", interessados);
				req.getSession().setAttribute("destinatarios", destinatarios);
				
				req.getSession().setAttribute("remetente", remetente);
				not.iniciar();
				return null;
			} else {
				addMensagemErro("Não existe(m) Interessado(s) Cadastrado(s)!", req);
				return mapping.findForward(LISTAR);
			}
		} finally {
			if (dao != null)
				dao.close();
		}			
	}	
}
