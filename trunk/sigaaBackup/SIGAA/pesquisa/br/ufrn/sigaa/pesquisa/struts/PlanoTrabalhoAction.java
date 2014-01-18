/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 26/09/2006
 *
 */
package br.ufrn.sigaa.pesquisa.struts;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.RequestUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.notificacoes.Destinatario;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.DocenteExternoDao;
import br.ufrn.sigaa.arq.dao.pesquisa.CalendarioPesquisaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.CotaBolsasDao;
import br.ufrn.sigaa.arq.dao.pesquisa.EditalPesquisaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.PlanoTrabalhoDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoPesquisaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.TipoBolsaPesquisaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.AbstractWizardAction;
import br.ufrn.sigaa.arq.struts.ConstantesCadastro;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.pesquisa.dominio.CalendarioPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.CotaBolsas;
import br.ufrn.sigaa.pesquisa.dominio.Cotas;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.GrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.SiglaUnidadePesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoStatusPlanoTrabalho;
import br.ufrn.sigaa.pesquisa.form.PlanoTrabalhoForm;
import br.ufrn.sigaa.pesquisa.form.TelaCronograma;
import br.ufrn.sigaa.pesquisa.jsf.EnviarMensagemBolsistaMBean;
import br.ufrn.sigaa.pesquisa.negocio.PlanoTrabalhoValidator;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.CronogramaProjeto;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Action responsável pelo controle dos casos de uso referentes a Planos de Trabalho de Pesquisa
 *
 * @author Ricardo Wendell
 * @author Leonardo Campos
 * @author Jean Guerethes
 *
 */
public class PlanoTrabalhoAction extends AbstractWizardAction {

	/** FORM FORWARDS */
	/** Constante da view dos dados gerais */
	public static final String	DADOS_GERAIS	= "dadosGerais";
	/** Constante da view do cronograma */
	public static final String	CRONOGRAMA		= "cronograma";
	/** Constante da view do resumo */
	public static final String	RESUMO			= "resumo";
	/** Constante da view do resumo */
	public static final String	RESUMO_CONSULTOR = "resumoConsultor";
	/** Constante da view da lista / consulta */
	public static final String	LISTA_CONSULTA	= "listaConsulta";

	/** DISPATCH ACTIONS 
	 * <br>
	 * JSP: Não invocado por JSP.
	 * */
	@Override
	public ActionForward navegar(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		PlanoTrabalhoForm planoTrabalhoForm = (PlanoTrabalhoForm) form;
		if ( planoTrabalhoForm.getObj().getOrientador().getId() != 0 ) {
			return super.navegar(mapping, form, request, response);
		} else {
			addMensagem(new MensagemAviso("Esta operação não está mais ativa, por favor reinicie-a!", TipoMensagemUFRN.WARNING), request);
			return mapping.findForward(getSubSistemaCorrente(request).getForward());
		}
	}

	/**
	 * Exerce a função de editar os planos de trabalho
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/PlanoTrabalho/lista_orientador.jsp</li>
	 *	</ul>
	 */
	@Override
	public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		if(!isNotOperacaoAtiva(req, (PlanoTrabalhoForm) form)){
			addMensagemErro("Esta operação está ativa, por favor finalize o plano de trabalho antes de editar outro!", req);
			return mapping.findForward(getSubSistemaCorrente(req).getForward());
		}
		
		PlanoTrabalhoForm planoTrabalhoForm = (PlanoTrabalhoForm) form;
		planoTrabalhoForm.getObj().setId(RequestUtils.getIntParameter(req, "id"));

		GenericDAO dao = getGenericDAO(req);
		PlanoTrabalho plano = dao.findAndFetch(planoTrabalhoForm.getObj().getId(), PlanoTrabalho.class, "projetoPesquisa");
		
		if (plano != null && plano.getProjetoPesquisa() != null && plano.getProjetoPesquisa().getCodigo() != null)
			planoTrabalhoForm.setCodigoProjeto(plano.getProjetoPesquisa().getCodigo());
		
		if (plano != null && plano.getTipoBolsa() != null )
			planoTrabalhoForm.setTipoBolsa( plano.getTipoBolsa().getId() );
		
		if (plano == null) {
			addMensagemErro("O plano de trabalho informado não foi localizado na base de dados.", req);
			return mapping.findForward(getSubSistemaCorrente(req).getForward());
		}
		super.edit(mapping, form, req, res);
		
		if(planoTrabalhoForm == null || planoTrabalhoForm.getCodigoProjeto() == null || 
				plano == null || plano.getProjetoPesquisa() == null || plano.getProjetoPesquisa().getCodigo() == null){
			addMensagemErro("Houve um problema ao carregar os dados do plano de trabalho. Por favor, reinicie a operação.", req);
			return mapping.findForward(getSubSistemaCorrente(req).getForward());
		}
		
		Usuario usuario = (Usuario) getUsuarioLogado(req);
		if(!usuario.isUserInRole(SigaaPapeis.GESTOR_PESQUISA) || !getSubSistemaCorrente(req).equals(SigaaSubsistemas.PESQUISA)){
			if( plano.getStatus() != TipoStatusPlanoTrabalho.CADASTRADO 
			        && plano.getStatus() != TipoStatusPlanoTrabalho.AGUARDANDO_RESUBMISSAO 
			        && plano.getStatus() != TipoStatusPlanoTrabalho.APROVADO_COM_RESTRICOES 
			        && plano.getStatus() != TipoStatusPlanoTrabalho.NAO_APROVADO 
			        && plano.getTipoBolsa().isVinculadoCota() ){
				addMensagemErro("O Plano de Trabalho selecionado não pode mais ser alterado pois já se encontra em avaliação ou em andamento", req);
				return listarPorOrientador(mapping, form, req, res);
			}
		}

		planoTrabalhoForm.getCodigoProjeto().setAno( plano.getProjetoPesquisa().getCodigo().getAno() );
		planoTrabalhoForm.getCodigoProjeto().setPrefixo("P");

		// Inicializar cronogramas
		planoTrabalhoForm.getObj().getCronogramas().iterator();
		planoTrabalhoForm.getObj().getMembrosDiscentes().iterator();
		
		planoTrabalhoForm.setSolicitacaoCota( planoTrabalhoForm.getObj().getTipoBolsa().isVinculadoCota() );
		planoTrabalhoForm.setCadastroVoluntario( TipoBolsaPesquisa.isVoluntario( planoTrabalhoForm.getObj().getTipoBolsa().getId() ));

		prepararPlano(planoTrabalhoForm, req);
		carregarTipoBolsa(mapping, planoTrabalhoForm, req, res);
		
		if (getUltimoComando(req).equals(ArqListaComando.REMOVER)) {
			return mapping.findForward(RESUMO);
		} else {
			planoTrabalhoForm.getObj().setExterno(new DocenteExterno());
			planoTrabalhoForm.getObj().setCodMovimento(SigaaListaComando.ALTERAR_PLANO_TRABALHO);
			prepareMovimento(SigaaListaComando.ALTERAR_PLANO_TRABALHO, req);
			return mapping.findForward(DADOS_GERAIS);
		}
	}

	/**
	 * Preenche os dados para iniciar o caso de uso a partir do Portal Docente,
	 * quando um professor deseja concorrer a uma cota de bolsa.
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 *	</ul>
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward iniciarSolicitacaoCota(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
					throws Exception {
		PlanoTrabalhoForm planoForm = (PlanoTrabalhoForm) form;
		planoForm.clear();
		Map<String, Object> referenceData = planoForm.getReferenceData();
		Collection<EditalPesquisa> editaisAbertos = getDAO(EditalPesquisaDao.class, req).findAllAbertosTipo(true);
		referenceData.put("editaisAbertos", editaisAbertos);
		
		if( editaisAbertos.isEmpty() ){
			addMensagemErro("Não há editais de pesquisa com período de submissão aberto para solicitação de cotas.", req);
			return mapping.findForward(getSubSistemaCorrente(req).getForward());
		}
		
		return mapping.findForward("escolhaEdital");
	}
	
	public ActionForward selecionarEdital(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		
		if(((Usuario) getUsuarioLogado(req)).getVinculoServidor() == null && ((Usuario) getUsuarioLogado(req)).getDocenteExterno() == null ) {
			addMensagemErro("Somente docentes servidores da instituição podem concorrer ao edital.", req);
			return mapping.findForward(getSubSistemaCorrente(req).getForward());
		}
		
		PlanoTrabalhoForm planoForm = (PlanoTrabalhoForm) form;
		planoForm.clear();
		int idEdital = getParameterInt(req, "idEdital");

		ProjetoPesquisaDao projetoDao = getDAO(ProjetoPesquisaDao.class, req);
		DocenteExternoDao dao = getDAO(DocenteExternoDao.class, req);
		EditalPesquisaDao editalDao =  getDAO(EditalPesquisaDao.class, req);
		
		EditalPesquisa edital = projetoDao.findAndFetch(idEdital, EditalPesquisa.class, "cota", "cotas");
		if(ValidatorUtil.isEmpty(edital))
			edital = editalDao.findByIdEdital(idEdital);
		
		Servidor servidor = ((Usuario) getUsuarioLogado(req)).getServidor();
		Collection<DocenteExterno> docenteExterno = null;
		if (servidor == null)
			docenteExterno = dao.findByAtivosValido(((Usuario) getUsuarioLogado(req)).getDocenteExterno().getPessoa());
		
		if (servidor != null || (docenteExterno != null && docenteExterno.size() > 0)) {
			Collection<ProjetoPesquisa> findParaSolicitacaoCota = projetoDao.findParaSolicitacaoCota(servidor, docenteExterno, edital);
			if(!ValidatorUtil.isEmpty(findParaSolicitacaoCota))
				req.setAttribute("lista", findParaSolicitacaoCota);
			else {
				addMensagemErro("Você não coordena projetos passíveis de solicitação de cotas para o edital selecionado." +
						"<br/> Cadastre um novo projeto de pesquisa, ou efetue a renovação de um projeto atual.", req);
				return mapping.findForward(getSubSistemaCorrente(req).getForward()); 
			}
		}
		
		edital.getCotas().iterator();
		
		Collection<TipoBolsaPesquisa> bolsas = new ArrayList<TipoBolsaPesquisa>(); 
		if ( !edital.getCotas().isEmpty() ) {
			bolsas = new ArrayList<TipoBolsaPesquisa>();
			for (Cotas cota : edital.getCotas())
				bolsas.add(cota.getTipoBolsa());
		}
		
		planoForm.getReferenceData().put("tipoBolsas", bolsas);
		planoForm.getObj().setEdital(edital);
		planoForm.getObj().setCota(edital.getCota());
		
		return mapping.findForward("escolhaProjeto");
	}
	
	/**
	 * Preenche os dados para iniciar o caso de uso a partir do Portal Docente,
	 * quando um professor deseja cadastrar um plano de trabalho voluntário.
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 *	</ul>
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward iniciarPlanoVoluntario(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
					throws Exception {
		PlanoTrabalhoForm planoForm = (PlanoTrabalhoForm) form;
		Map<String, Object> referenceData = planoForm.getReferenceData();
		Collection<CotaBolsas> cotasVoluntarioAbertas = getDAO(CotaBolsasDao.class, req).cotaBolsasVoluntariaAberta();
		referenceData.put("cotas", cotasVoluntarioAbertas);
		
		if( cotasVoluntarioAbertas.isEmpty() ){
			addMensagemErro("Não há cotas com período de submissão aberto para planos voluntários.", req);
			return mapping.findForward(getSubSistemaCorrente(req).getForward());
		}
		
		return mapping.findForward("escolhaCota");
	}
	
	/**
	 * Preenche os dados para iniciar o caso de uso a partir do Portal Docente,
	 * quando um professor deseja cadastrar um plano de trabalho sem vínculo com uma cota de bolsas
	 * controlada pela instituição.
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 *	</ul>
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward iniciarPlanoSemCota(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		ProjetoPesquisaDao projetoDao = getDAO(ProjetoPesquisaDao.class, req);
		DocenteExternoDao dao = getDAO(DocenteExternoDao.class, req);
		
		Servidor servidor = ((Usuario) getUsuarioLogado(req)).getServidor();
		Collection<DocenteExterno> docenteExterno = null;
		if (servidor == null)
			docenteExterno = dao.findByAtivosValido(((Usuario) getUsuarioLogado(req)).getDocenteExterno().getPessoa());
		
		if (servidor != null || (docenteExterno != null && docenteExterno.size() > 0)) {
			Collection<ProjetoPesquisa> findParaSemCota = projetoDao.findParaSemCota(servidor, docenteExterno);
			if(!ValidatorUtil.isEmpty(findParaSemCota))
				req.setAttribute("lista", findParaSemCota);
			else {
				addMensagemErro("Você não possui projetos de pesquisa em execução sob sua coordenação." +
						"<br/> Cadastre um novo projeto de pesquisa, ou efetue a renovação de um projeto atual.", req);
				return mapping.findForward(getSubSistemaCorrente(req).getForward()); 
			}
		}
		
		return mapping.findForward("escolhaProjetoSem");
	}
	
	public ActionForward selecionarCota(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		PlanoTrabalhoForm planoForm = (PlanoTrabalhoForm) form;
		planoForm.clear();
		int idCota = getParameterInt(req, "idCota");

		ProjetoPesquisaDao projetoDao = getDAO(ProjetoPesquisaDao.class, req);
		DocenteExternoDao dao = getDAO(DocenteExternoDao.class, req);
		
		CotaBolsas cota = projetoDao.findAndFetch(idCota, CotaBolsas.class);
		
		Servidor servidor = ((Usuario) getUsuarioLogado(req)).getServidor();
		Collection<DocenteExterno> docenteExterno = null;
		if (servidor == null)
			docenteExterno = dao.findByAtivosValido(((Usuario) getUsuarioLogado(req)).getDocenteExterno().getPessoa());
		
		if (servidor != null || (docenteExterno != null && docenteExterno.size() > 0)) {
			Collection<ProjetoPesquisa> findParaVoluntario = projetoDao.findParaVoluntario(servidor, docenteExterno, cota);
			if(!ValidatorUtil.isEmpty(findParaVoluntario))
				req.setAttribute("lista", findParaVoluntario);
			else {
				addMensagemErro("Você não coordena projetos passíveis de cadastro de planos voluntários para a cota selecionada." +
						"<br/> Cadastre um novo projeto de pesquisa, ou efetue a renovação de um projeto atual.", req);
				return mapping.findForward(getSubSistemaCorrente(req).getForward()); 
			}
		}
		
		planoForm.getObj().setCota(cota);
		
		return mapping.findForward("escolhaProjetoVol");
	}

	/**
	 * Utilizado para filtrar as opções de cadastro de bolsas com ou sem cota.<br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/PlanoTrabalho/dados_gerais.jsp</li>
	 *	</ul>
	 * 
	 */
	public ActionForward tratarTipoBolsa(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
					throws Exception {
		PlanoTrabalhoForm planoTrabalhoForm = (PlanoTrabalhoForm) form;
		GenericDAO dao = getGenericDAO(req);
		
		int tipoBolsa = Integer.parseInt( req.getParameter("idTipoBolsa") );
		
		if(tipoBolsa > 0){
			planoTrabalhoForm.getObj().setTipoBolsa(dao.findByPrimaryKey(tipoBolsa, TipoBolsaPesquisa.class));
			planoTrabalhoForm.setSolicitacaoCota( planoTrabalhoForm.getObj().getTipoBolsa().isVinculadoCota() );
		}
		
		if ( planoTrabalhoForm.getReferenceData().get("membrosProjeto") == null ) {
			addMensagemErro("Você usou o botão voltar do navegador, reinicie os procedimentos e tente novamente.", req);
			return mapping.findForward(getSubSistemaCorrente(req).getForward());
		}
		
		if(planoTrabalhoForm.isSolicitacaoCota()){
			// Se é o cadastro de um novo plano
			if (planoTrabalhoForm.getObj().getId() == 0) {
				planoTrabalhoForm.getObj().setCota(new CotaBolsas());
				if(planoTrabalhoForm.getObj().getEdital() == null)
					planoTrabalhoForm.getObj().setEdital(new EditalPesquisa());
				// Se é a alteração de um plano existente
			}else if(planoTrabalhoForm.getObj().getId() > 0){
				PlanoTrabalho planoBD = dao.findByPrimaryKey(planoTrabalhoForm.getObj().getId(), PlanoTrabalho.class);
				
				// verifica se o plano possuía cota associada
				if( planoBD.getCota() != null)
					// seta a cota à qual ele estava associado
					planoTrabalhoForm.getObj().setCota(dao.findByPrimaryKey(planoBD.getCota().getId(), CotaBolsas.class));
				else
					// caso contrário inicializa o atributo
					planoTrabalhoForm.getObj().setCota(new CotaBolsas());
				
				if( planoBD.getEdital() != null ) {
					planoTrabalhoForm.getObj().setEdital(dao.findByPrimaryKey(planoBD.getEdital().getId(), EditalPesquisa.class));
					planoTrabalhoForm.getObj().getEdital().getCotas().iterator();
					Collection<TipoBolsaPesquisa> bolsas = new ArrayList<TipoBolsaPesquisa>(); 
					if ( !planoTrabalhoForm.getObj().getEdital().getCotas().isEmpty() ) {
						bolsas = new ArrayList<TipoBolsaPesquisa>();
						for (Cotas cota : planoTrabalhoForm.getObj().getEdital().getCotas())
							bolsas.add(cota.getTipoBolsa());
					}
					
					planoTrabalhoForm.getReferenceData().put("tipoBolsas", bolsas);
				} else {
					planoTrabalhoForm.getObj().setEdital(new EditalPesquisa());
					planoTrabalhoForm.getReferenceData().put("tipoBolsas", new ArrayList<TipoBolsaPesquisa>());
				}
				
			}
		} else {
			planoTrabalhoForm.getObj().setCota(null);
			planoTrabalhoForm.getObj().setEdital(null);
		}
		
		planoTrabalhoForm.getOutGroupStrutsGestorBolsa(tipoBolsa);
		
		return mapping.findForward(DADOS_GERAIS);
	}
	
	/**
	 * Utilizado para filtrar as opções de cadastro de bolsas com ou sem cota.<br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/PlanoTrabalho/dados_gerais.jsp</li>
	 *	</ul>
	 * 
	 */
	public ActionForward tratarEditalPesquisa(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		PlanoTrabalhoForm planoTrabalhoForm = (PlanoTrabalhoForm) form;
		GenericDAO dao = getGenericDAO(req);

		if ( planoTrabalhoForm.getReferenceData().get("membrosProjeto") == null ) {
			addMensagemErro("Você usou o botão voltar do navegador, reinicie os procedimentos e tente novamente.", req);
			return mapping.findForward(getSubSistemaCorrente(req).getForward());
		}
		
		planoTrabalhoForm.getObj().setEdital(dao.findByPrimaryKey(planoTrabalhoForm.getObj().getEdital().getId(), EditalPesquisa.class));
		planoTrabalhoForm.getObj().getEdital().getCotas().iterator();
		planoTrabalhoForm.getObj().getEdital().getCota().getDescricao();
		planoTrabalhoForm.getObj().setCota(planoTrabalhoForm.getObj().getEdital().getCota());
		
		Collection<TipoBolsaPesquisa> bolsas = new ArrayList<TipoBolsaPesquisa>(); 
		if ( !planoTrabalhoForm.getObj().getEdital().getCotas().isEmpty() ) {
			bolsas = new ArrayList<TipoBolsaPesquisa>();
			for (Cotas cota : planoTrabalhoForm.getObj().getEdital().getCotas())
				bolsas.add(cota.getTipoBolsa());
		}
		
		planoTrabalhoForm.getReferenceData().put("tipoBolsas", bolsas);

		return mapping.findForward(DADOS_GERAIS);
	}

	
	
	/**
	 * Busca os dados do projeto de pesquisa selecionado
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/lista_projetos_coordenador.jsp</li>
	 *	</ul>
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ActionForward selecionarProjeto(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		PlanoTrabalhoForm planoForm = (PlanoTrabalhoForm) form;
		int idProjeto = getParameterInt(req, "idProjeto");

		CotaBolsasDao dao = getDAO(CotaBolsasDao.class, req);
		ProjetoPesquisa projeto = dao.findByPrimaryKey(idProjeto, ProjetoPesquisa.class);

		Usuario usuario = (Usuario) ((Usuario) getUsuarioLogado(req) != null ? 
				(Usuario) getUsuarioLogado(req) 
				: ((Usuario) getUsuarioLogado(req)).getDocenteExterno());
		
		planoForm.setExterno(usuario.getVinculoAtivo().isVinculoDocenteExterno());
		
		// Buscar projeto
		if (projeto == null) {
			addMensagemErro("Projeto de pesquisa não encontrado!", req);
			return mapping.findForward(getSubSistemaCorrente(req).getForward());
		}
		else if (!usuario.isUserInRole(SigaaPapeis.GESTOR_PESQUISA) || !getSubSistemaCorrente(req).equals(SigaaSubsistemas.PESQUISA)){
			// Verificar se o o projeto possui coordenador
			Servidor coordenador = projeto.getCoordenador();
			if (coordenador != null) {
				// Verificar se o usuário é o coordenador do projeto
				if (!coordenador.equals(usuario.getServidor()) && 
						coordenador.getPessoa().getId() != usuario.getDocenteExterno().getPessoa().getId()) {
					addMensagemErro("Somente coordenadores de projeto podem realizar o cadastro de planos de trabalho", req);
					return getMappingSubSistema(req, mapping);
				}
			} else {
				addMensagemErro("Não foi designado um coordenador para o projeto de pesquisa selecionado. " +
						"Contacte a Pró-Reitoria de Pesquisa para regularizar o projeto.", req);
				return getMappingSubSistema(req, mapping);
			}
			boolean erro = false;
			// Verificar se é uma solicitação de cota ou registro de plano de trabalho
			if (planoForm.isSolicitacaoCota()) {
								
				Map<String, Object> referenceData = planoForm.getReferenceData();
				Collection<EditalPesquisa> editaisAbertos = getDAO(EditalPesquisaDao.class, req).findAllAbertosTipo(true);
				referenceData.put("editaisAbertos", editaisAbertos);
				if(referenceData.get("tipoBolsas") == null)
					referenceData.put("tipoBolsas", new ArrayList<TipoBolsaPesquisa>());
				
				if( editaisAbertos.isEmpty() && !planoForm.isCadastroVoluntario() ){
					addMensagemErro("Não há editais de pesquisa com período de submissão aberto.", req);
					erro = true;
				}
				
				// Um Docente Externo só pode ter um trabalho Voluntário				
				if (planoForm.isCadastroVoluntario() && usuario.getVinculoAtivo().isVinculoDocenteExterno() && editaisAbertos.size() >= 1) {
					int count=0;
					for (EditalPesquisa ep : editaisAbertos) {
						if (ep.getUsuario().getId() == usuario.getId()) {
							count++;
						} 
					}
					if (count > 0) {
						addMensagemErro("Docente Externo só pode cadastrar 1 plano de trabalho voluntário.", req);
						erro = true;
					}
				}
				
			} else {
				if (projeto.getSituacaoProjeto().getId() != TipoSituacaoProjeto.EM_ANDAMENTO) {
					addMensagemErro("O registro de planos de trabalho com bolsas de fluxo contínuo só pode ser feito para projetos com status EM EXECUÇÃO.", req);
					erro = true;
				}
			}
			if(erro) return getMappingSubSistema(req, mapping);
		}

		planoForm.getObj().setProjetoPesquisa(projeto);
		prepararPlano(planoForm, req);
		
		if ( planoForm.isCadastroVoluntario() )
			planoForm.getObj().setCota( dao.cotaBolsasVoluntariaAberta().iterator().next() );

		if (((Collection<MembroProjeto>) planoForm.getReferenceData().get("membrosProjetoExterno")).isEmpty()) {
			planoForm.getObj().getExterno().getId();
		}
		
		if (((Collection<MembroProjeto>) planoForm.getReferenceData().get("membrosProjetoExterno")).isEmpty() &&
				((Collection<MembroProjeto>) planoForm.getReferenceData().get("membrosProjeto")).isEmpty()) {
			addMensagemErro("Não foram definidos membros nesse projeto que possam orientar planos de trabalho.", req);
			return getMappingSubSistema(req, mapping);
		}
		return mapping.findForward(DADOS_GERAIS);
	}
	
	/**
	 * Preparar para o cadastro/alteração do plano de trabalho
	 *
	 * @param planoTrabalhoForm
	 * @param req
	 * @throws ArqException
	 * @throws NegocioException
	 * @throws RemoteException
	 */
	private void prepararPlano(PlanoTrabalhoForm planoTrabalhoForm, HttpServletRequest req) throws ArqException, RemoteException, NegocioException {
	    TipoBolsaPesquisaDao dao = getDAO(TipoBolsaPesquisaDao.class, req);
		ProjetoPesquisa projeto = planoTrabalhoForm.getObj().getProjetoPesquisa();
		CotaBolsasDao cotaBolsaDao = getDAO(CotaBolsasDao.class, req);

		try {
			
			if(planoTrabalhoForm.isSolicitacaoCota()){
				//Definindo edital pra evitar exceção
				if(planoTrabalhoForm.getObj().getEdital() == null)
					planoTrabalhoForm.getObj().setEdital(new EditalPesquisa());
			} else {
				planoTrabalhoForm.getObj().setCota(null);
			}
	
			// Definindo o atributo da permissão do gestor
			planoTrabalhoForm.setPermissaoGestor(isUserInRole(SigaaPapeis.GESTOR_PESQUISA, req) && SigaaSubsistemas.PESQUISA.equals(getSubSistemaCorrente(req)));
	
			if (planoTrabalhoForm.isPermissaoGestor()) {
				planoTrabalhoForm.getObj().setProjetoPesquisa(projeto);
				planoTrabalhoForm.getReferenceData().put("cotas", dao.findAll(CotaBolsas.class, "descricao", "desc"));
				planoTrabalhoForm.getReferenceData().put("tiposStatus", TipoStatusPlanoTrabalho.getTipos());
				planoTrabalhoForm.getReferenceData().put("editais", dao.findAll(EditalPesquisa.class));
	
				if (planoTrabalhoForm.getObj().getStatus() <= 0) {
					planoTrabalhoForm.getObj().setStatus(TipoStatusPlanoTrabalho.EM_ANDAMENTO);
				}
				
				if ( ValidatorUtil.isEmpty( planoTrabalhoForm.getObj().getCota() ) )
					planoTrabalhoForm.getObj().setCota(new CotaBolsas());
			
			} else if (!planoTrabalhoForm.isPermissaoGestor() && planoTrabalhoForm.isCadastroVoluntario()) {
				planoTrabalhoForm.getReferenceData().put("cotas", cotaBolsaDao.cotaBolsasVoluntariaAberta());
			}
	
			planoTrabalhoForm.getReferenceData().put("tiposBolsa", dao.findAllAtivos(TipoBolsaPesquisa.class, "descricao"));
			planoTrabalhoForm.getReferenceData().put("tiposBolsaVoluntarios", TipoBolsaPesquisa.getTiposVoluntarios());
			planoTrabalhoForm.getReferenceData().put("tiposBolsaFluxoContinuo", dao.findBolsasFluxoContinuo());
			planoTrabalhoForm.getReferenceData().put("membrosProjeto", projeto.getMembrosDocentes());
			planoTrabalhoForm.getReferenceData().put("membrosProjetoExterno", projeto.getMembrosExterno());
	
			prepareMovimento(SigaaListaComando.CADASTRAR_PLANO_TRABALHO, req);
	
			definirPassos(req);

		} finally {
			dao.close();
			cotaBolsaDao.close();
		}
	}

	/**
	 * Submete os dados da primeira tela do cadastro para validação e encaminha para a tela seguinte.<br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/PlanoTrabalho/dados_gerais.jsp</li>
	 *	</ul>
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward submeterDadosGerais(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		if(req.getSession().getAttribute("steps") == null){
			addMensagemErro("A operação não está mais ativa. Por favor reinicie a operação.", req);
			if(flushErros(req))
				return mapping.findForward(getSubSistemaCorrente(req).getForward());
		}
		
		Integer idTipoBolsa = Integer.parseInt( req.getParameter("idTipoBolsa") );
		
		PlanoTrabalhoForm planoTrabalhoForm = (PlanoTrabalhoForm) form;
		PlanoTrabalho plano = planoTrabalhoForm.getObj();

		if ( !planoTrabalhoForm.isSolicitacaoCota() || planoTrabalhoForm.isCadastroVoluntario() )
			plano.setBolsaDesejada( idTipoBolsa );
		
		if ( req.getParameter("idStatusBolsa") != null ) {
			plano.setStatus( Integer.parseInt( req.getParameter("idStatusBolsa") ) );
		}

		GenericDAO dao = getGenericDAO(req);

		// Buscar dados do tipo de bolsa
		if (idTipoBolsa != null && idTipoBolsa != 0) {
			plano.setTipoBolsa(dao.findByPrimaryKey(idTipoBolsa, TipoBolsaPesquisa.class));
			planoTrabalhoForm.setTipoBolsa(idTipoBolsa);
		} else 
			plano.setTipoBolsa(dao.refresh(plano.getTipoBolsa()));
		
		if (plano.getProjetoPesquisa() == null) {
			addMensagemErro("É necessário iniciar esta operação a partir da escolha de um projeto de pesquisa", req);
			return getMappingSubSistema(req, mapping);
		}

		ListaMensagens lista = new ListaMensagens();
		// Validar dados gerais do plano de trabalho
		PlanoTrabalhoValidator.validarDadosGerais(planoTrabalhoForm.getObj(), planoTrabalhoForm, isUserInRole(SigaaPapeis.GESTOR_PESQUISA, req), lista);

		if ( lista.isErrorPresent() ) {
			addMensagens(lista.getMensagens(), req);
			return mapping.findForward(DADOS_GERAIS);
		}

		if(planoTrabalhoForm.isSolicitacaoCota()){
			// Popular dados do edital selecionado
			if(plano.getEdital() != null && plano.getEdital().getId() > 0)
				plano.setEdital(dao.findByPrimaryKey(plano.getEdital().getId(), EditalPesquisa.class));
			// Definir a cota do plano como a cota do edital selecionado
			if (plano.getEdital() != null && planoTrabalhoForm.getObj().getId() == 0 && plano.getEdital().getCotas() != null 
					&& !plano.getEdital().getCotas().isEmpty()) {
				planoTrabalhoForm.getObj().setCota(plano.getEdital().getCota());
			}

			// Buscar dados da cota selecionada
			CotaBolsas cota;
			if (planoTrabalhoForm.isCadastroVoluntario()) {
				cota = dao.findByPrimaryKey(plano.getCota().getId(), CotaBolsas.class);
				plano.setEdital(null);
			}
			else
				cota = dao.findByPrimaryKey(plano.getEdital().getCota().getId(), CotaBolsas.class);
			
			plano.setCota(cota);
			plano.setDataInicio(cota.getInicio());
			plano.setDataFim(cota.getFim());
		}else {
			plano.setCota(null);
			plano.setEdital(null);
		}
		
		// Buscar dados do Orientador
		if (plano.getOrientador() != null && plano.getOrientador().getId() != 0) {
			plano.setOrientador(dao.findByPrimaryKey(plano.getOrientador().getId(), Servidor.class));
			plano.setExterno(new DocenteExterno());
		}
		
		if (plano.getExterno() != null && plano.getExterno().getId() != 0) { 
			plano.setExterno(dao.findByPrimaryKey(plano.getExterno().getId(), DocenteExterno.class));
			plano.setOrientador(new Servidor());
		}
		
		if ( plano.getOrientador().getId() == 0 && plano.getExterno().getId() == 0) {
			addMensagemErro("É necessário informar pelo menos um orientador.", req);
			addMensagens(lista.getMensagens(), req);
			return mapping.findForward(DADOS_GERAIS);
		}

		// Validar parâmetros do sistema de pesquisa para limites de cotas
		PlanoTrabalhoValidator.validarRestricoes(planoTrabalhoForm.getObj(), (Usuario) getUsuarioLogado(req), lista);
		addMensagens(lista.getMensagens(), req);
		
		if ( planoTrabalhoForm.isPermissaoGestor() ) {

			if ( req.getParameter("alterarProjeto") != null ) {
				planoTrabalhoForm.setAlterarProjeto(true);

				ProjetoPesquisaDao projetoDao = getDAO(ProjetoPesquisaDao.class, req);
				ProjetoPesquisa novoProjeto = projetoDao.findByCodigo(planoTrabalhoForm.getCodigoProjeto());

				if ( novoProjeto == null ) {
					addMensagemErro("O projeto com o código informado não foi encontrado", req);
				} else if ( !novoProjeto.isMembroProjeto(plano.getOrientador()) ) {
					addMensagemErro("O orientador deste plano de trabalho não pertence ao projeto informado", req);
				} else {
					plano.setProjetoPesquisa(novoProjeto);
				}

			} else {
				planoTrabalhoForm.setAlterarProjeto(false);
			}

		}

		if ( lista.isErrorPresent() ) {
			if(plano.getTipoBolsa() == null) plano.setTipoBolsa(new TipoBolsaPesquisa(-1));
				if (plano.getExterno() == null) 
					plano.setExterno(new DocenteExterno());
			return mapping.findForward(DADOS_GERAIS);
		}

		// Inicializar tela do cronograma
		TelaCronograma cronograma = new TelaCronograma(
				plano.getDataInicio(),
				plano.getDataFim(),
				plano.getCronogramas()
			);
		planoTrabalhoForm.setTelaCronograma(cronograma);



		setStep(req, 2);
		return mapping.findForward(CRONOGRAMA);
	}

	/**
	 * Submete os dados da segunda tela do cadastro para validação e encaminha para a tela de resumo.<br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/PlanoTrabalho/cronograma.jsp</li>
	 *	</ul>
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward submeterCronograma(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		if(req.getSession().getAttribute("steps") == null){
			addMensagemErro("A operação não está mais ativa. Por favor reinicie a operação.", req);
			return mapping.findForward(getSubSistemaCorrente(req).getForward());
		}
		PlanoTrabalhoForm planoTrabalhoForm = (PlanoTrabalhoForm) form;

		if (planoTrabalhoForm.getObj().getProjetoPesquisa() == null) {
			addMensagemErro("É necessário iniciar esta operação a partir da escolha de um projeto de pesquisa", req);
			return getMappingSubSistema(req, mapping);
		}

		// Obter objetos cronogramas a partir dos dados do formulário
		planoTrabalhoForm.getTelaCronograma().definirCronograma(req);

		// Obter atividades do cronograma a partir do formulário
		List<CronogramaProjeto> cronogramas = planoTrabalhoForm.getTelaCronograma().getCronogramas();
		for (CronogramaProjeto cronograma : cronogramas) {
			cronograma.setPlanoTrabalho(planoTrabalhoForm.getObj());
		}
		planoTrabalhoForm.getObj().setCronogramas(cronogramas);

		// Validar cronograma do plano de trabalho
		ListaMensagens lista = new ListaMensagens();
		PlanoTrabalhoValidator.validarCronograma(planoTrabalhoForm.getObj(), lista);

		if (lista.isErrorPresent()) {
			addMensagens(lista.getMensagens(), req);
			return mapping.findForward(CRONOGRAMA);
		}

		setStep(req, 3);
		return mapping.findForward(RESUMO);
	}

	/**
	 * Remove o plano de trabalho escolhido previamente.<br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/PlanoTrabalho/resumo.jsp</li>
	 *	</ul>
	 */
	@Override
	public ActionForward remove(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		PlanoTrabalhoForm planoTrabalhoForm = (PlanoTrabalhoForm) form;
		planoTrabalhoForm.setConfirm(false);

		planoTrabalhoForm.getObj().setId(RequestUtils.getIntParameter(req, "id"));
		
		GenericDAO dao = getGenericDAO(req);
		PlanoTrabalho plano = dao.findAndFetch(planoTrabalhoForm.getObj().getId(), PlanoTrabalho.class, "projetoPesquisa");
		if (plano != null && plano.getProjetoPesquisa() != null && plano.getProjetoPesquisa().getCodigo() != null)
			planoTrabalhoForm.setCodigoProjeto(plano.getProjetoPesquisa().getCodigo());
		
		if (plano == null) {
			addMensagemErro("O plano de trabalho informado não foi localizado na base de dados.", req);
			return mapping.findForward(getSubSistemaCorrente(req).getForward());
		}
		
		req.setAttribute("desativar", "true");
		super.remove(mapping, form, req, res);

		// Inicializar tela do cronograma
		TelaCronograma cronograma = new TelaCronograma(
				planoTrabalhoForm.getObj().getDataInicio(),
				planoTrabalhoForm.getObj().getDataFim(),
				planoTrabalhoForm.getObj().getCronogramas()
			);
		planoTrabalhoForm.setTelaCronograma(cronograma);

		planoTrabalhoForm.getObj().setCodMovimento(SigaaListaComando.REMOVER_PLANO_TRABALHO);
		prepareMovimento(SigaaListaComando.REMOVER_PLANO_TRABALHO, req);

		return mapping.findForward(RESUMO);
	}

	/**
	 * Verifica se a operação ainda se encontra ativa.
	 * retorna verdadeiro caso a operação não esteja mais ativa. 
	 * utilizado para evitar problemas quando o usuário volta pelo navegador.
	 * 
	 * @param request
	 * @param planoForm
	 * @return
	 */
	private boolean isNotOperacaoAtiva(HttpServletRequest request,
			PlanoTrabalhoForm planoForm) {
		return request.getSession().getAttribute("steps") == null && 
				(planoForm == null || planoForm.getObj() == null || planoForm.getObj().getCodMovimento() == null ||
						!planoForm.getObj().getCodMovimento().equals(SigaaListaComando.REMOVER_PLANO_TRABALHO));
	}
	
	/**
	 * Finaliza o caso de uso, chamando o processador para executar a operação desejada pelo usuário.<br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/PlanoTrabalho/resumo.jsp</li>
	 *	</ul>
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward finalizar(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		PlanoTrabalhoForm planoTrabalhoForm = (PlanoTrabalhoForm) form;
				
		if( isNotOperacaoAtiva(req, planoTrabalhoForm) ){
			addMensagemErro("A operação não está mais ativa. Por favor reinicie a operação.", req);
			return mapping.findForward(getSubSistemaCorrente(req).getForward());
		}
		if (planoTrabalhoForm.getObj().getOrientador().getId() == 0) 
			planoTrabalhoForm.getObj().setOrientador(null);
		else
			planoTrabalhoForm.getObj().setExterno(null);
		
		try {
			getGenericDAO(req).detach(planoTrabalhoForm.getObj());
			execute(planoTrabalhoForm.getObj(), req);
		} catch (NegocioException e) {
			if (planoTrabalhoForm.getObj().getCodMovimento().equals(SigaaListaComando.REMOVER_PLANO_TRABALHO)) {
				req.setAttribute("remove", true);
			}

			addMensagens(e.getListaMensagens().getMensagens(), req);
			return mapping.findForward(RESUMO);
		}

		removeSession(mapping.getName(), req);
		req.setAttribute("formPlanoTrabalho", planoTrabalhoForm);
		req.setAttribute("comprovante", true);

		if (planoTrabalhoForm.getObj().getCodMovimento().equals(SigaaListaComando.CADASTRAR_PLANO_TRABALHO)) {
			addInformation("Plano de Trabalho cadastrado com sucesso!", req);
			if (!planoTrabalhoForm.isSolicitacaoCota())
				addMensagem(new MensagemAviso("Atenção! Não esqueça de indicar o bolsista para o plano de trabalho registrado.", TipoMensagemUFRN.WARNING), req);
			clearSteps(req);
		} else if (planoTrabalhoForm.getObj().getCodMovimento().equals(SigaaListaComando.REMOVER_PLANO_TRABALHO)) {
			req.setAttribute("popular", true);
			addInformation("Plano de Trabalho removido com sucesso!", req);
			return getMappingSubSistema(req, mapping);
		} else {
			addInformation("Plano de Trabalho alterado com sucesso!", req);
			clearSteps(req);
		}
		return getMappingSubSistema(req, mapping);
	}

	/**
	 * Listar os planos de trabalho de um discente ou docente.<br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 *	</ul>
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward listarPorOrientador(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		PlanoTrabalhoDao planoTrabalhoDao = getDAO(PlanoTrabalhoDao.class, req);
		Collection<PlanoTrabalho> planos = new ArrayList<PlanoTrabalho>();
		
		Pessoa pessoa = ((Usuario) getUsuarioLogado(req)).getPessoa();
		 
		planos.addAll( planoTrabalhoDao.findByOrientador(pessoa.getId()) );
		
		req.setAttribute("periodoSubmissao", verificaPeriodoSubmissao(req));
		req.setAttribute("planos", planos);
		return mapping.findForward("listaOrientador");
	}
	
	/**
	 * Método auxiliar para verificar no calendário se está vigente o período de submissão de projetos e planos de trabalho. 
	 * @param req
	 * @return
	 * @throws ArqException
	 */
	private boolean verificaPeriodoSubmissao(HttpServletRequest req) throws ArqException{
		CalendarioPesquisaDao calendarioDao = getDAO(CalendarioPesquisaDao.class, req);
		CalendarioPesquisa cal = calendarioDao.findVigente();
		Date hoje = new Date();
		return !hoje.before(cal.getInicioEnvioProjetos()) && !hoje.after(cal.getFimEnvioProjetos());
	}

	/**
	 * Listar os planos de trabalho de um discente ou docente.<br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/portais/discente/menu_discente.jsp</li>
	 *	</ul>
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward listarPorDiscente(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		PlanoTrabalhoDao planoTrabalhoDao = getDAO(PlanoTrabalhoDao.class, req);
		Collection<PlanoTrabalho> planos = null;

		DiscenteAdapter discente = ((Usuario) getUsuarioLogado(req)).getDiscenteAtivo();
		if (discente != null) {
			planos = planoTrabalhoDao.findAllByDiscente(discente);
		}

		req.setAttribute("planos", planos);
		return mapping.findForward("listaDiscente");
	}

	/**
	 * Lista todos os planos de trabalho 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/menu/projeto.jsp</li>
	 *		<li>sigaa.war/menu_pesquisa</li>
	 *	</ul>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		super.list(mapping, form, req, res);

		Collection lista = (Collection) req.getAttribute(ConstantesCadastro.ATRIBUTO_LISTAGEM);

		if (req.getParameter("relatorio") != null && lista != null && !lista.isEmpty() ) {
			req.setAttribute("relatorio", true);
		}

		return mapping.findForward(LISTAR);

	}

	/**
	 * Efetua a busca por planos de trabalho segundo os diversos filtros informados.<br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/PlanoTrabalho/relatorio.jsp</li>
	 *	</ul>
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward buscar(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {

		PlanoTrabalhoForm pForm = (PlanoTrabalhoForm) form;
		PlanoTrabalhoDao dao = getDAO(PlanoTrabalhoDao.class, req);
		pForm.setConfirm(false);

		req.setAttribute("gruposPesquisa", dao.findAllProjection( GrupoPesquisa.class, "nome", "asc", new String[]{"id", "nome"} ) );
		req.setAttribute("centros", dao.findAll(SiglaUnidadePesquisa.class, "unidade.nome", "asc") );
		req.setAttribute("cursos", dao.findAllProjection(Curso.class,"nome","asc",new String[]{"id", "nome", "unidade"}));
		
		//Adicionando todos os tipo de modalidades ao combo de busca.
		Collection<TipoBolsaPesquisa> modalidades = dao.findByExactField(TipoBolsaPesquisa.class, "ativo", true);
		Map<Integer, String> tipos = new TreeMap<Integer, String>();
		for (TipoBolsaPesquisa ti : modalidades) {
			tipos.put(ti.getId(), ti.getDescricao());
		}
		
		req.setAttribute("modalidades", tipos);
		req.setAttribute("cotas", dao.findAll(CotaBolsas.class, "descricao", "asc"));
		req.setAttribute("editais", dao.findAll(EditalPesquisa.class, "edital.dataCadastro", "desc"));
		req.setAttribute("status", TipoStatusPlanoTrabalho.getTipos());

		if (req.getParameter("popular") != null || req.getAttribute("popular") != null) {
			return mapping.findForward("relatorio");
		}

		Collection<PlanoTrabalho> lista;
		Integer idGrupoPesquisa = null;
		Integer idCentro = null;
		Integer idUnidade = null;
		Integer idAluno = null;
		Integer idOrientador = null;
		Integer idCota = null;
		Integer idEdital = null;
		Integer idModalidade = null;
		Integer idStatus = null;
		Integer idPessoa = null;

		ListaMensagens erros = new ListaMensagens();

		if (pForm.getFiltros().length > 0) {
			for(int filtro : pForm.getFiltros() ){
				switch(filtro) {
				case TipoFiltroPlanoTrabalho.GRUPO_PESQUISA:
					idGrupoPesquisa = pForm.getGrupoPesquisa();
					ValidatorUtil.validateRequiredId(idGrupoPesquisa, "Grupo de Pesquisa", erros);
					break;
				case TipoFiltroPlanoTrabalho.CENTRO:
					idCentro = pForm.getUnidade().getGestora().getId();
					ValidatorUtil.validateRequiredId(idCentro, "Centro", erros);
					break;
				case TipoFiltroPlanoTrabalho.DEPARTAMENTO:
					idUnidade = pForm.getUnidade().getId();
					ValidatorUtil.validateRequiredId(idUnidade, "Departamento", erros);
					break;
				case TipoFiltroPlanoTrabalho.NOME:
					idAluno = pForm.getObj().getMembroProjetoDiscente().getDiscente().getId();
					ValidatorUtil.validateRequiredId(idAluno, "Aluno", erros);
					break;
				case TipoFiltroPlanoTrabalho.ORIENTADOR:
					idOrientador = pForm.getObj().getOrientador().getId();
					ValidatorUtil.validateRequiredId(idOrientador, "Orientador", erros);
					if ("externo".equalsIgnoreCase(req.getParameter("tipoAjaxDocente"))) {
						idPessoa = getDAO(DocenteExternoDao.class, req).findIdPessoaByDocenteExterno(idOrientador);
						idOrientador = null;
					}
					break;
				case TipoFiltroPlanoTrabalho.COTA:
					idCota = pForm.getObj().getCota().getId();
					ValidatorUtil.validateRequiredId(idCota, "Cota", erros);
					break;
				case TipoFiltroPlanoTrabalho.EDITAL:
					idEdital = pForm.getObj().getEdital().getId();
					ValidatorUtil.validateRequiredId(idEdital, "Edital", erros);
					break;
				case TipoFiltroPlanoTrabalho.MODALIDADE_BOLSA:
					idModalidade = pForm.getObj().getTipoBolsa().getId();
					ValidatorUtil.validateRequiredId(idModalidade, "Modalidade", erros);
					break;
				case TipoFiltroPlanoTrabalho.STATUS_PLANO:
					idStatus = pForm.getObj().getStatus();
					ValidatorUtil.validateRequiredId(idStatus, "Status", erros);
					break;
				}
			}
		} else {
			erros.addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
		}

		try {
			if (erros.isEmpty()) {
				lista = dao.filter(
						idGrupoPesquisa,
						idCentro,
						idUnidade,
						idAluno,
						idOrientador,
						idCota,
						idEdital,
						idModalidade,
						idStatus,
						null,
						idPessoa, true);
				
				req.setAttribute("lista", lista);
				
				if ( lista.isEmpty() ) {
					addMensagemErro("Nenhum relatório foi encontrado de acordo com os critérios de busca informados.", req);
				}
				
			} else {
				addMensagens(erros.getMensagens(), req);
			}

			} catch (LimiteResultadosException lre) {
				addMensagemErro(lre.getMessage(), req);
				lre.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		return mapping.findForward("relatorio");
	}

	public ActionForward buscarConsultor(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		buscar(mapping, form, req, res);
		setSubSistemaAtual(req, getSubSistemaCorrente(req));
		return mapping.findForward(RESUMO_CONSULTOR);
	}
	
	/**
	 * Serve pra visualizar o plano de trabalho escolhido
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/AvaliacaoProjeto/avaliacao.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/PlanoTrabalho/avaliacao.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/PlanoTrabalho/lista_discente.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/PlanoTrabalho/lista_orientador.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ResumoCongresso/seleciona_plano_trabalho.jsp</li>
	 *	</ul>
	 */
	@Override
	public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		PlanoTrabalhoForm planoTrabalhoForm = (PlanoTrabalhoForm) form;

		// Buscar dados do plano de trabalho
		PlanoTrabalhoForm planoForm = new PlanoTrabalhoForm();

		planoForm.setObj(getGenericDAO(req).findAndFetch(planoTrabalhoForm.getObj().getId(), PlanoTrabalho.class, "projetoPesquisa"));
		
		if (planoTrabalhoForm.getObjAsPersistDB() == null || getForm(form).formBackingObject(req) == null) {
			return mapping.findForward(getSubSistemaCorrente(req).getForward());
		}

		if(planoForm.getObj() == null) {
		    addMensagemErro("Plano de trabalho não localizado.", req);
		    return getMappingSubSistema(req, res, mapping);
		}

		// Inicializar tela do cronograma
		TelaCronograma cronograma = new TelaCronograma(
				planoForm.getObj().getDataInicio(),
				planoForm.getObj().getDataFim(),
				planoForm.getObj().getCronogramas()
			);
		planoForm.setTelaCronograma(cronograma);

		if ( !isEmpty( planoForm.getObj().getBolsaDesejada() ) ) {
			TipoBolsaPesquisa tbp = getGenericDAO(req).findByPrimaryKey(planoForm.getObj().getBolsaDesejada(), TipoBolsaPesquisa.class);
			planoForm.setBolsaDesejada( !isEmpty(tbp) ? tbp.getDescricaoResumida() : "Não Informada");
		} else {
			planoForm.setBolsaDesejada("Não Informada");
		}
		
		req.setAttribute("formPlanoTrabalho", planoForm);
		req.setAttribute("comprovante", true);
		return mapping.findForward(RESUMO);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection getCustomList() {
		return null;
	}

	/**
	 * Grava os dados de um plano de trabalho a partir da primeira tela do cadastro.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/PlanoTrabalho/dados_gerais.jsp</li>
	 *	</ul>
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward gravarDadosGerais(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {

		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);

		prepareMovimento(SigaaListaComando.GRAVAR_PLANO_TRABALHO, req);

		PlanoTrabalhoForm planoTrabalhoForm = (PlanoTrabalhoForm) form;
		PlanoTrabalho plano = planoTrabalhoForm.getObj();
		plano.setCodMovimento(SigaaListaComando.GRAVAR_PLANO_TRABALHO);

		if (plano.getProjetoPesquisa() == null) {
			addMensagemErro("É necessário iniciar esta operação a partir da escolha de um projeto de pesquisa", req);
			return getMappingSubSistema(req, mapping);
		}

		GenericDAO dao = getGenericDAO(req);

		if ( req.getParameter("idStatusBolsa") != null ) {
			planoTrabalhoForm.getObj().setStatus( Integer.parseInt( req.getParameter("idStatusBolsa")) );
		}
		
		ListaMensagens lista = new ListaMensagens();
		// Validar dados gerais do plano de trabalho
		PlanoTrabalhoValidator.validarDadosGerais(planoTrabalhoForm.getObj(), planoTrabalhoForm, isUserInRole(SigaaPapeis.GESTOR_PESQUISA, req), lista);
		// Validar numero de bolsistas definidos pela cota
//		PlanoTrabalhoValidator.validarNumeroDeBolsistas(planoTrabalhoForm.getObj(), lista);
		
		if(planoTrabalhoForm.isSolicitacaoCota()){
			// Buscar dados da cota selecionada
			plano.setCota(dao.findByPrimaryKey(plano.getCota().getId(), CotaBolsas.class));
			// Popular dados do edital selecionado
			if(plano.getEdital() != null && plano.getEdital().getId() > 0)
				plano.setEdital(dao.findByPrimaryKey(plano.getEdital().getId(), EditalPesquisa.class));
			else if(plano.getTipoBolsa().getId() == TipoBolsaPesquisa.VOLUNTARIO)
				plano.setEdital(null);
		}
		
		// Buscar dados do Orientador
		plano.setOrientador(dao.findByPrimaryKey(plano.getOrientador().getId(), Servidor.class));

		// Validar parâmetros do sistema de pesquisa para limites de cotas
		PlanoTrabalhoValidator.validarRestricoes(planoTrabalhoForm.getObj(), (Usuario) getUsuarioLogado(req), lista);
		addMensagens(lista.getMensagens(), req);
		
		planoTrabalhoForm.getReferenceData().clear();
		
		if ( isUserInRole(SigaaPapeis.GESTOR_PESQUISA, req) ) {

			if ( req.getParameter("alterarProjeto") != null ) {
				planoTrabalhoForm.setAlterarProjeto(true);

				ProjetoPesquisaDao projetoDao = getDAO(ProjetoPesquisaDao.class, req);
				ProjetoPesquisa novoProjeto = projetoDao.findByCodigo(planoTrabalhoForm.getCodigoProjeto());

				if ( novoProjeto == null ) {
					addMensagemErro("O projeto com o código informado não foi encontrado", req);
				} else if ( !novoProjeto.isMembroProjeto(plano.getOrientador()) ) {
					addMensagemErro("O orientador deste plano de trabalho não pertence ao projeto informado", req);
				} else {
					plano.setProjetoPesquisa(novoProjeto);
				}

			} else {
				planoTrabalhoForm.setAlterarProjeto(false);
			}

		}

		if ( lista.isErrorPresent() ) {
			return mapping.findForward(DADOS_GERAIS);
		}

		// Inicializar tela do cronograma
		TelaCronograma telaCronograma = new TelaCronograma(
				plano.getDataInicio(),
				plano.getDataFim(),
				plano.getCronogramas()
			);
		planoTrabalhoForm.setTelaCronograma(telaCronograma);
		
		// Obter objetos cronogramas a partir dos dados do formulário
		planoTrabalhoForm.getTelaCronograma().definirCronograma(req);
		
		// Obter atividades do cronograma a partir do formulário
		List<CronogramaProjeto> cronogramas = planoTrabalhoForm.getTelaCronograma().getCronogramas();
		for (CronogramaProjeto cronograma : cronogramas) {
			cronograma.setPlanoTrabalho(planoTrabalhoForm.getObj());
		}
		planoTrabalhoForm.getObj().setCronogramas(cronogramas);
		
		if (planoTrabalhoForm.getObj().getExterno().getId() == 0)
			 planoTrabalhoForm.getObj().setExterno(null);
		
		try {
			getGenericDAO(req).detach(planoTrabalhoForm.getObj());
			execute(planoTrabalhoForm.getObj(), req);
		} catch (NegocioException e) {
			return mapping.findForward(DADOS_GERAIS);
		}

		clearSession(req);
		removeSession(mapping.getName(), req);
		clearSteps(req);
		addInformation("Plano de Trabalho gravado com sucesso!", req);

		return getMappingSubSistema(req, mapping);
	}

	/**
	 * Método auxiliar utilizado para definir os passos do wizard.
	 * @param req
	 */
	private void definirPassos(HttpServletRequest req) {
		clearSteps(req);
		addStep(req, "Plano de Trabalho: Dados Gerais", "/pesquisa/planoTrabalho/wizard", DADOS_GERAIS);
		addStep(req, "Cronograma", "/pesquisa/planoTrabalho/wizard", CRONOGRAMA);
		addStep(req, "Resumo", "/pesquisa/planoTrabalho/wizard", RESUMO);
		setStep(req, 1);
	}
	
	
	/**
	 * Invoca o MBean responsável por enviar email/mensagem para um usuário
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/PlanoTrabalho/lista_orientador.jsp</li>
	 *	</ul>
	 * @param mapping
	 * @param form
	 * @param req
	 * @param response
	 * @return
	 * @throws Exception
	 */	
	public ActionForward enviarMensagemBolsista(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse response)
			throws Exception {
		
		setOperacaoAtiva(req, "enviarMensagemBolsista");
				
		Integer idDiscente = getParameterInt(req, "idDiscente");
		if (idDiscente != null && idDiscente != 0) {
			
			Discente bolsista = getGenericDAO(req).findByPrimaryKey(idDiscente, Discente.class);
			Usuario usuario = new Usuario();
				
			if(bolsista != null)
				usuario = getDAO(DiscenteDao.class, req).findByUsuario(bolsista.getPessoa().getId());
			else{
				addMensagemErro("A mensagem não pôde ser enviada pois não há bolsista ativo vinculado ao plano de trabalho.", req);
				return listarPorOrientador(mapping, form, req, response);
			}
			
			String remetente =  getUsuarioLogado(req).getNome();
			
			if (idDiscente > 0){
				ArrayList<Destinatario> destinatarios = new ArrayList<Destinatario>();
	
				String nome = bolsista.getPessoa().getNome();
				String email = bolsista.getPessoa().getEmail();
				int idUsuario = usuario.getId() ;
	
				Destinatario destinatario = new Destinatario(nome, email);
				destinatario.setIdusuario(idUsuario);
				destinatarios.add(destinatario);
				
				EnviarMensagemBolsistaMBean not = getMBean("enviarMsgBolsista", req, response);
				not.setDestinatarios(destinatarios);
				not.setRemetente(remetente);
				not.setTitulo("Enviar mensagem aos Orientandos");
				
				req.getSession().setAttribute("destinatarios", destinatarios);
				req.getSession().setAttribute("remetente", remetente);
				
				not.iniciar();
			}
			
			return null;
		}
		else {
			addMensagemErro("Bolsista não localizado.", req);
			return listarPorOrientador(mapping, form, req, response);
		}

	}

	public ActionForward carregarTipoBolsa(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws DAOException, ArqException {
		GenericDAO dao = getGenericDAO(req);
		try {
			PlanoTrabalhoForm planoForm = (PlanoTrabalhoForm) form;
			if ( !isEmpty( planoForm.getObj().getEdital() ) ) {
				EditalPesquisa editalPesquisa = dao.findAndFetch(planoForm.getObj().getEdital().getId(), EditalPesquisa.class, "cota", "cotas");
				planoForm.getObj().setCota( editalPesquisa.getCota() );
				
				Collection<TipoBolsaPesquisa> bolsas = new ArrayList<TipoBolsaPesquisa>(); 
				if ( !editalPesquisa.getCotas().isEmpty() ) {
					bolsas = new ArrayList<TipoBolsaPesquisa>();
					for (Cotas cota : editalPesquisa.getCotas())
						bolsas.add(cota.getTipoBolsa());
				}
				
				planoForm.getReferenceData().put("tipoBolsas", bolsas);
			}
		} finally {
			dao.close();
		}
		
		return mapping.findForward(DADOS_GERAIS);
	}

}