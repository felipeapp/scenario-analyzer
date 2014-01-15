/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 14/02/2007
 *
 */
package br.ufrn.sigaa.pesquisa.struts;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.pesquisa.MembroProjetoDiscenteDao;
import br.ufrn.sigaa.arq.dao.pesquisa.PlanoTrabalhoDao;
import br.ufrn.sigaa.arq.dao.pesquisa.RelatorioBolsaParcialDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.parametros.dominio.ParametrosPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.CotaBolsas;
import br.ufrn.sigaa.pesquisa.dominio.MembroProjetoDiscente;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.RelatorioBolsaParcial;
import br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa;
import br.ufrn.sigaa.pesquisa.form.RelatorioBolsaParcialForm;
import br.ufrn.sigaa.pesquisa.negocio.RelatorioBolsaParcialValidator;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Action respons�vel pelo controle do envio, consulta e emiss�o de parecer
 * dos relat�rios parciais de bolsa
 *
 * @author Ricardo Wendell
 *
 */
public class RelatorioBolsaParcialAction extends AbstractCrudAction {

	/**
	 * Esse m�todo realiza uma listagem dos planos de bolsa parcial
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward listarPlanos(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		// Validar par�metro do m�dulo de pesquisa se permite o envio de relat�rio parcial
		if( !ParametroHelper.getInstance().getParametroBoolean(ParametrosPesquisa.ENVIO_RELATORIO_PARCIAL) ){
			addMensagemErro("O sistema n�o est� aberto para recebimento de Relat�rios Parciais. Somente os Relat�rios Finais e Resumos para o CIC est�o sendo recebidos.", req);
			return getMappingSubSistema(req, mapping);
		}
		
		// Validar acesso
		verificaPapelDiscente(req);

		DiscenteAdapter discente = ((Usuario) getUsuarioLogado(req)).getDiscenteAtivo();

		// Buscar planos associados ao discente
		PlanoTrabalhoDao planoDao = getDAO(PlanoTrabalhoDao.class, req);
		Collection<PlanoTrabalho> planos = planoDao.findAllEmAndamentoByDiscente(discente);

		if ( planos.isEmpty() ) {
			addMensagemErro("N�o foram encontrados planos de trabalho em andamento designados para voc�. " +
					"Verifique junto ao seu orientador se o cadastro do seu plano foi realizado. Em caso de d�vida, contacte a "+ 
					ParametroHelper.getInstance().getParametro(ParametrosPesquisa.SIGLA_NOME_PRO_REITORIA_PESQUISA)+".", req);
			return mapping.findForward(getSubSistemaCorrente(req).getForward());
		} else {
			req.setAttribute("planos", planos);
			return mapping.findForward("listaPlanos");
		}

	}

	/**
	 * Busca o plano de trabalho do discente (se existir) e prepara
	 * para o envio do relat�rio parcial
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward popularEnvio(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		RelatorioBolsaParcialForm relatorioForm = (RelatorioBolsaParcialForm) form;
		DiscenteAdapter discente = ((Usuario) getUsuarioLogado(req)).getDiscenteAtivo();

		PlanoTrabalhoDao planoDao = getDAO(PlanoTrabalhoDao.class, req);
		// Verificar se o usu�rio � um discente
		if (discente != null) {

			// Verificar se o discente possui um plano de trabalho associado
			PlanoTrabalho plano = planoDao.findByPrimaryKey( relatorioForm.getObj().getPlanoTrabalho().getId() , PlanoTrabalho.class);
			if (plano == null) {
				addMensagemErro("O plano de trabalho informado n�o foi localizado na base de dados.", req);
				return listarPlanos(mapping, form, req, res);
			}

			// Verificar prazo de envio
			RelatorioBolsaParcialValidator.validarPeriodoEnvio(plano, newListaMensagens(req));
			
			if ( flushErros(req) )
				return listarPlanos(mapping, form, req, res);
							
			if (plano.getRelatorioBolsaParcial() != null) {

				// Verifica se o relat�rio j� foi submetido ao orientador para dar parecer
				if (plano.getRelatorioBolsaParcial().isEnviado()) {
					addMensagemErro("N�o � poss�vel modificar o relat�rio pois voce j� submeteu ao parecer do orientador", req);
					return listarPlanos(mapping, form, req, res);
				}
				
				// Verificar se o orientador j� emitiu o parecer
				if (plano.getRelatorioBolsaParcial().getParecerOrientador() != null) {
					addMensagemErro("N�o � poss�vel modificar o relat�rio pois o parecer do orientador j� foi emitido", req);
					return listarPlanos(mapping, form, req, res);
				}

				relatorioForm.setObj(plano.getRelatorioBolsaParcial());
			} else {
				relatorioForm.getObj().setPlanoTrabalho(plano);
			}

			req.setAttribute("fimPeriodo", plano.getTipoBolsa().getFimEnvioRelatorioParcial());
			req.setAttribute(mapping.getName(), relatorioForm);
		}

		return mapping.findForward("formEnvio");
	}
	
	/**
	 * Busca o plano de trabalho do discente (se existir) e prepara
	 * para o envio do relat�rio parcial
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward popularEnvioPropesq(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		RelatorioBolsaParcialForm relatorioForm = (RelatorioBolsaParcialForm) form;

		PlanoTrabalhoDao planoDao = getDAO(PlanoTrabalhoDao.class, req);
		MembroProjetoDiscenteDao dao = getDAO(MembroProjetoDiscenteDao.class, req);
			try {
				// Verificar se o discente possui um plano de trabalho associado
				PlanoTrabalho plano = planoDao.findByPrimaryKey( relatorioForm.getObj().getPlanoTrabalho().getId() , PlanoTrabalho.class);
				if (plano == null) {
					addMensagemErro("O plano de trabalho informado n�o foi localizado na base de dados.", req);
					return mapping.findForward(getSubSistemaCorrente(req).getForward());
				}
				
				MembroProjetoDiscente ultimoDiscentePlano = dao.findUltimoDiscenteFinalizado(plano);
				if(ultimoDiscentePlano == null){
					addMensagemErro("O plano de trabalho informado n�o possui alunos de inicia��o cient�fica associados.", req);
					return mapping.findForward(getSubSistemaCorrente(req).getForward());
				}

				if ( flushErros(req) )
					return mapping.findForward("planos");
								
				if (plano.getRelatorioBolsaParcial() != null) {

					// Verificar se o orientador j� emitiu o parecer
					if (plano.getRelatorioBolsaParcial().getParecerOrientador() != null)
						addWarning("O parecer do orientador j� foi emitido", req);

					relatorioForm.setObj(plano.getRelatorioBolsaParcial());
				} else {
					relatorioForm.getObj().setPlanoTrabalho(plano);
				}

				if (plano.getTipoBolsa().getFimEnvioRelatorioParcial() != null)
					req.setAttribute("fimPeriodo", plano.getTipoBolsa().getFimEnvioRelatorioParcial());
				
				if ( isEmpty( plano.getMembroProjetoDiscente() ) ) {
					 plano.setMembroProjetoDiscente( dao.findUltimoDiscenteFinalizado(plano) );
					 relatorioForm.getObj().getPlanoTrabalho().setMembroProjetoDiscente(plano.getMembroProjetoDiscente());
				}
				
				req.setAttribute(mapping.getName(), relatorioForm);
				
			} finally {
				dao.close();
				planoDao.close();
			}

		return mapping.findForward("formEnvio");
	}
	
	/**
	 * Validar os dados e enviar o relat�rio parcial
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward enviar(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		verificaPapelDiscente(req);
		
		prepareMovimento(SigaaListaComando.ENVIAR_RELATORIO_PARCIAL_BOLSA_PESQUISA, req);
		
		
		RelatorioBolsaParcialForm relatorioForm = (RelatorioBolsaParcialForm) form;
		RelatorioBolsaParcial relatorio =  relatorioForm.getObj();

		MembroProjetoDiscenteDao dao = getDAO(MembroProjetoDiscenteDao.class, req);
		// Gravar relat�rio
		try {
			// se o aluno estiver enviando depois de ser exclu�do do plano de trabalho, vai ser null.
			if (relatorio.getMembroDiscente() == null) {
				if( getSubSistemaAtual(req) == SigaaSubsistemas.PORTAL_DISCENTE.getId() && ((Usuario) getUsuarioLogado(req)).getDiscenteAtivo() != null ){
					Long matricula = ((Usuario) getUsuarioLogado(req)).getDiscenteAtivo().getMatricula();
					relatorio.setMembroDiscente(dao.findByPlanoTrabalho(relatorio.getPlanoTrabalho(), matricula));
				} else { 
					PlanoTrabalho planoBD = dao.findByPrimaryKey(relatorio.getPlanoTrabalho().getId(), PlanoTrabalho.class);
					if(planoBD.getMembroProjetoDiscente() != null)
						relatorio.setMembroDiscente(planoBD.getMembroProjetoDiscente());
					else {
						MembroProjetoDiscente ultimoDiscenteFinalizado = dao.findUltimoDiscenteFinalizado(relatorio.getPlanoTrabalho());
						if(ultimoDiscenteFinalizado != null)
							relatorio.setMembroDiscente( ultimoDiscenteFinalizado );
						else
							relatorio.setMembroDiscente( new MembroProjetoDiscente() );
					}
				}
			}else
				relatorio.setMembroDiscente(relatorio.getMembroDiscente());

			// Validar relat�rio
			ListaMensagens erros = relatorio.validate();
			if (!erros.isEmpty()) {
				addMensagens(erros.getMensagens(), req);
				relatorioForm.getObj().setPlanoTrabalho(getGenericDAO(req).findByPrimaryKey(relatorio.getPlanoTrabalho().getId(), PlanoTrabalho.class));
				req.setAttribute(mapping.getName(), relatorioForm);
				return mapping.findForward("formEnvio");
			}
			
			relatorio.setCodMovimento(SigaaListaComando.ENVIAR_RELATORIO_PARCIAL_BOLSA_PESQUISA);
			relatorio = (RelatorioBolsaParcial) execute(relatorio, req);
			addInformation("Relat�rio Parcial de Bolsa enviado com sucesso!", req);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens().getMensagens(), req);
			relatorioForm.getObj().setPlanoTrabalho(getGenericDAO(req).findByPrimaryKey(relatorio.getPlanoTrabalho().getId(), PlanoTrabalho.class));
			req.setAttribute(mapping.getName(), relatorioForm);
			return mapping.findForward("formEnvio");
		}
		relatorioForm.setObj(relatorio);
		req.setAttribute("relatorio", relatorio);
		return view(mapping, form, req, res);
	}

	/**
	 * Faz uma verifica��o para afim de verificar se o usu�rio logado � discente
	 * 
	 * @param req
	 * @throws SegurancaException
	 * @throws ArqException
	 */
	private void verificaPapelDiscente(HttpServletRequest req) throws SegurancaException, ArqException {
		if(((Usuario) getUsuarioLogado(req)).getDiscenteAtivo() == null)
			checkRole(SigaaPapeis.GESTOR_PESQUISA, req);
	}

	/**
	 * M�todo respons�vel pela grava��o do relat�rio de bolsa parcial
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward gravar(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		verificaPapelDiscente(req);
		prepareMovimento(SigaaListaComando.GRAVAR_RELATORIO_PARCIAL_BOLSA_PESQUISA, req);
		
		RelatorioBolsaParcialForm relatorioForm = (RelatorioBolsaParcialForm) form;
		RelatorioBolsaParcial relatorio =  relatorioForm.getObj();
		
		MembroProjetoDiscenteDao dao = getDAO(MembroProjetoDiscenteDao.class, req);
		RelatorioBolsaParcialDao dao2 = getDAO(RelatorioBolsaParcialDao.class, req);
		
		// Gravar relat�rio
		try {
			// se o aluno estiver enviando depois de ser exclu�do do plano de trabalho, vai ser null.
			if (relatorio.getMembroDiscente() == null) {
				if( getSubSistemaAtual(req) == SigaaSubsistemas.PORTAL_DISCENTE.getId() && ((Usuario) getUsuarioLogado(req)).getDiscenteAtivo() != null ){
					Long matricula = ((Usuario) getUsuarioLogado(req)).getDiscenteAtivo().getMatricula();
					relatorio.setMembroDiscente(dao.findByPlanoTrabalho(relatorio.getPlanoTrabalho(), matricula));
				}else 
				relatorio.setMembroDiscente(relatorio.getPlanoTrabalho().getMembroProjetoDiscente());
			}else
				relatorio.setMembroDiscente(relatorio.getMembroDiscente());

			// Verifica se o relat�rio j� existe no banco e popula o seu Id
			Integer id = dao2.findIdByPlanoAndMembro(relatorio.getPlanoTrabalho().getId(), relatorio.getMembroDiscente().getId());
			if(id != null)
				relatorio.setId(id);
			
			relatorio.setCodMovimento(SigaaListaComando.GRAVAR_RELATORIO_PARCIAL_BOLSA_PESQUISA);
			relatorio = (RelatorioBolsaParcial) execute(relatorio, req);
			addInformation("Relat�rio Parcial de Bolsa gravado com sucesso!", req);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens().getMensagens(), req);
			relatorioForm.getObj().setPlanoTrabalho(getGenericDAO(req).findByPrimaryKey(relatorio.getPlanoTrabalho().getId(), PlanoTrabalho.class));
			req.setAttribute(mapping.getName(), relatorioForm);
			req.setAttribute("fimPeriodo", relatorioForm.getObj().getPlanoTrabalho().getTipoBolsa().getFimEnvioRelatorioParcial());
			return mapping.findForward("formEnvio");
		}
		relatorioForm.setObj(relatorio);
		req.setAttribute("relatorio", relatorio);
		return view(mapping, form, req, res);
	}
	
	
	
	/**
	 * Listar os orientados (planos de trabalho) do docente logado, caso este possua
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward listarOrientandos(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		Usuario usuario = (Usuario) getUsuarioLogado(req);
		Servidor servidor = usuario.getServidor();

		if (servidor == null) {
			addMensagemErro("Somente servidores podem acessar esta opera��o!", req);
			return getMappingSubSistema(req, mapping);
		}

		// Buscar planos de trabalho do docente
		Collection<RelatorioBolsaParcial> relatorios = null;
		RelatorioBolsaParcialDao relatorioDao = getDAO(RelatorioBolsaParcialDao.class, req);
		relatorios = relatorioDao.findByOrientador(servidor.getId(), null);

		if (relatorios == null || relatorios.isEmpty()) {
			addMensagemErro("Voc� n�o possui relat�rios parciais a emitir parecer", req);
			return getMappingSubSistema(req, mapping);
		} else {
			req.setAttribute("relatorios", relatorios);
		}

		return mapping.findForward("listaOrientandos");
	}

	/**
	 * Buscar os relat�rios submetidos por um aluno
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward listarRelatorios(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		Usuario usuario = (Usuario) getUsuarioLogado(req);
		DiscenteAdapter discente = usuario.getDiscenteAtivo();

		if (discente == null) {
			addMensagemErro("Somente alunos podem acessar esta opera��o!", req);
			return getMappingSubSistema(req, mapping);
		}

		// Buscar planos de trabalho do docente
		Collection<RelatorioBolsaParcial> relatorios = null;
		RelatorioBolsaParcialDao relatorioDao = getDAO(RelatorioBolsaParcialDao.class, req);
//		relatorios = relatorioDao.findByDiscente( discente );
		relatorios = relatorioDao.findAllByDiscente( discente );
		

		if (relatorios == null || relatorios.isEmpty()) {
			addMensagemErro("N�o foram encontrados relat�rios parciais de inicia��o cient�fica submetidos por voc�", req);
			return getMappingSubSistema(req, mapping);
		} else {
			req.setAttribute("relatorios", relatorios);
		}

		return mapping.findForward("listaDiscente");
	}

	/**
	 * Buscar o relat�rio selecionado e redirecionar para o formul�rio do parecer
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward selecionarBolsista(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		RelatorioBolsaParcialForm relatorioForm = (RelatorioBolsaParcialForm) form;

		int idRelatorio = getParameterInt(req,"idRelatorio");
		if (idRelatorio == 0) {
			addMensagemErro("� necess�rio selecionar um relat�rio para a emiss�o do parecer", req);
			return listarOrientandos(mapping, form, req, res);
		}

		RelatorioBolsaParcial relatorio = getGenericDAO(req).findByPrimaryKey(idRelatorio, RelatorioBolsaParcial.class);
		if (relatorio == null) {
			addMensagemErro("O relat�rio informado n�o foi encontrado", req);
			return listarOrientandos(mapping, form, req, res);
		}

		prepareMovimento(SigaaListaComando.PARECER_RELATORIO_PARCIAL_BOLSA_PESQUISA, req);

		relatorioForm.setObj(relatorio);
		req.setAttribute(mapping.getName(), relatorioForm);
		return mapping.findForward("formParecer");
	}


	/**
	 * Persistir o parecer emitido pelo orientador
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward emitirParecer(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		RelatorioBolsaParcialForm relatorioForm = (RelatorioBolsaParcialForm) form;
		RelatorioBolsaParcial relatorio = relatorioForm.getObj();

		// Validar parecer
		ListaMensagens erros = new ListaMensagens();
		if (relatorio.getParecerOrientador() == null || "".equals(relatorio.getParecerOrientador().trim())) {
			erros.addErro("Informe o parecer do relat�rio parcial");
		}
		else if(relatorio.getParecerOrientador().trim().length() > 5000) {
			erros.addErro("O parecer deve conter no m�ximo 5000 caracteres");
		}

		if (!erros.isEmpty()) {
			addMensagens(erros.getMensagens(), req);
			relatorioForm.setObj(getGenericDAO(req).findByPrimaryKey(relatorio.getId(), RelatorioBolsaParcial.class));
			relatorioForm.getObj().setParecerOrientador(relatorio.getParecerOrientador());
			req.setAttribute(mapping.getName(), relatorioForm);
			return mapping.findForward("formParecer");
		}

		// Persistir parecer
		try {
			relatorio.setCodMovimento(SigaaListaComando.PARECER_RELATORIO_PARCIAL_BOLSA_PESQUISA);
			relatorio = (RelatorioBolsaParcial) execute(relatorio, req);
			addInformation("Parecer de relat�rio parcial emitido com sucesso!", req);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens().getMensagens(), req);
			relatorioForm.getObj().setPlanoTrabalho(getGenericDAO(req).findByPrimaryKey(relatorio.getPlanoTrabalho().getId(), PlanoTrabalho.class));
			req.setAttribute(mapping.getName(), relatorioForm);
			return mapping.findForward("formParecer");
		}

		return listarOrientandos(mapping, form, req, res);
	}

	/** 
	 * Met�do respos�vel pela visualiza��o de um bolsa parcial  
	 */
	@Override
	public ActionForward view(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		int idRelatorio = 0;
		RelatorioBolsaParcial relatorio = (RelatorioBolsaParcial) req.getAttribute("relatorio");
		if (relatorio == null) {
			idRelatorio = getParameterInt(req, "idRelatorio");
		} else {
			idRelatorio = relatorio.getId();
		}

		relatorio = getDAO(RelatorioBolsaParcialDao.class, req).findOtimizadoParaView(idRelatorio);

		if (relatorio == null) {
			addMensagemErro("Relat�rio Parcial n�o encontrado!", req);
			return cancelar(mapping, form, req, res);
		} else {
			req.setAttribute("relatorio", relatorio);
		}

		return mapping.findForward("view");
	}

	/**
	 * Consulta do relat�rio parcial pelo discente
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward viewDiscente(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		Usuario usuario = (Usuario) getUsuarioLogado(req);
		DiscenteAdapter discente = usuario.getDiscenteAtivo();

		if (discente == null) {
			addMensagemErro("Somente discentes podem acessar esta opera��o!", req);
			return getMappingSubSistema(req, mapping);
		}

		PlanoTrabalhoDao planoDao = getDAO(PlanoTrabalhoDao.class, req);
		PlanoTrabalho plano = planoDao.findUltimoByDiscente(discente.getId(), CalendarUtils.getAnoAtual());

		if (plano == null) {
			addMensagemErro("Voc� n�o est� associado a um plano de trabalho", req);
			return getMappingSubSistema(req, mapping);
		}

		if (plano.getRelatorioBolsaParcial() == null) {
			addMensagemErro("Voc� ainda n�o enviou seu relat�rio parcial", req);
			return getMappingSubSistema(req, mapping);
		}

		req.setAttribute("relatorio", plano.getRelatorioBolsaParcial());
		return view(mapping, form, req, res);
	}

	/**
	 * Busca de planos de trabalho com relat�rios enviados
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward relatorio(ActionMapping mapping, ActionForm form, 
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);

		RelatorioBolsaParcialForm relForm = (RelatorioBolsaParcialForm) form;
		RelatorioBolsaParcialDao dao = getDAO(RelatorioBolsaParcialDao.class, req);
		Collection<RelatorioBolsaParcial> relatorios = null;
		int count = 0;

		Integer centro = null;
		Integer unidade = null;
		Integer modalidade = null;
		Integer orientador = null;
		Integer cota = null;
		Integer discente = null;
		
		if (relForm.isBuscar()) {
			if (relForm.isFiltroCentro()) 
				centro = relForm.getCentro();
			if (relForm.isFiltroDepartamento()) 
				unidade = relForm.getUnidade().getId();
			if (relForm.isFiltroModalidade()) 
				modalidade = relForm.getModalidade();
			if (relForm.isFiltroOrientador())
				orientador = relForm.getOrientador().getId();
			if (relForm.isFiltroCota()) 
				cota = relForm.getCota();
			if (relForm.isFiltroAluno()) 
				discente = relForm.getDiscente().getId();
			
			relatorios = dao.findByRelatoriosBolsaParcial(centro, unidade, modalidade, orientador, cota, discente, relForm.isParecer());
		}

			
		req.setAttribute("centros", dao.findByExactField(Unidade.class, "tipoAcademica", TipoUnidadeAcademica.CENTRO, "asc", "nome"));
		req.setAttribute("modalidades", TipoBolsaPesquisa.getTipos());
		req.setAttribute("cotas", dao.findAll(CotaBolsas.class, "descricao", "asc"));

		req.setAttribute("relatorios", relatorios);
		req.setAttribute("count", count);

		prepareMovimento(SigaaListaComando.REMOVER_PARECER_RELATORIO_PARCIAL_BOLSA_PESQUISA, req);
		return mapping.findForward("relatorio");
	}

	/**
	 * Esse m�todo possibilita a remo��o de um parecer sobre um bolsa  
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward removerParecer(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {

		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);

		int idRelatorio = getParameterInt(req, "idRelatorio");
		if( idRelatorio <= 0 ){
			addMensagemErro("� necess�rio selecionar um relat�rio", req);
			req.setAttribute("popular", true);
			return relatorio(mapping, form, req, res);
		}

		RelatorioBolsaParcial relatorio = getGenericDAO(req).findByPrimaryKey(idRelatorio, RelatorioBolsaParcial.class);
		if (relatorio == null) {
			addMensagemErro("Relat�rio Parcial n�o encontrado!", req);
			return cancelar(mapping, form, req, res);
		} else {
			// Validar remo��o do parecer
			if (relatorio.getParecerOrientador() == null || "".equals(relatorio.getParecerOrientador().trim())) {
				addMensagemErro("Este Relat�rio Parcial n�o possui parecer.", req);
				return relatorio(mapping, form, req, res);
			}
			try {
				relatorio.setCodMovimento( SigaaListaComando.REMOVER_PARECER_RELATORIO_PARCIAL_BOLSA_PESQUISA );
				execute(relatorio, req);
				addInformation("Parecer de relat�rio parcial removido com sucesso!", req);
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens().getMensagens(), req);
				relatorio(mapping, form, req, res);
			}

			return relatorio(mapping, form, req, res);
		}
	}

	/**
	 * Esse m�todo realiza a remo��o de uma bolsa parcial
	 */
	@Override
	public ActionForward remove(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);

		getForm(form).setConfirm(true);
		prepareMovimento(ArqListaComando.DESATIVAR, req);
		req.setAttribute("desativar", "true");
		super.remove(mapping, form, req, res);
		return relatorio(mapping, form, req, res);
	}

}
