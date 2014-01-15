/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Action responsável pelo controle do envio, consulta e emissão de parecer
 * dos relatórios parciais de bolsa
 *
 * @author Ricardo Wendell
 *
 */
public class RelatorioBolsaParcialAction extends AbstractCrudAction {

	/**
	 * Esse método realiza uma listagem dos planos de bolsa parcial
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

		// Validar parâmetro do módulo de pesquisa se permite o envio de relatório parcial
		if( !ParametroHelper.getInstance().getParametroBoolean(ParametrosPesquisa.ENVIO_RELATORIO_PARCIAL) ){
			addMensagemErro("O sistema não está aberto para recebimento de Relatórios Parciais. Somente os Relatórios Finais e Resumos para o CIC estão sendo recebidos.", req);
			return getMappingSubSistema(req, mapping);
		}
		
		// Validar acesso
		verificaPapelDiscente(req);

		DiscenteAdapter discente = ((Usuario) getUsuarioLogado(req)).getDiscenteAtivo();

		// Buscar planos associados ao discente
		PlanoTrabalhoDao planoDao = getDAO(PlanoTrabalhoDao.class, req);
		Collection<PlanoTrabalho> planos = planoDao.findAllEmAndamentoByDiscente(discente);

		if ( planos.isEmpty() ) {
			addMensagemErro("Não foram encontrados planos de trabalho em andamento designados para você. " +
					"Verifique junto ao seu orientador se o cadastro do seu plano foi realizado. Em caso de dúvida, contacte a "+ 
					ParametroHelper.getInstance().getParametro(ParametrosPesquisa.SIGLA_NOME_PRO_REITORIA_PESQUISA)+".", req);
			return mapping.findForward(getSubSistemaCorrente(req).getForward());
		} else {
			req.setAttribute("planos", planos);
			return mapping.findForward("listaPlanos");
		}

	}

	/**
	 * Busca o plano de trabalho do discente (se existir) e prepara
	 * para o envio do relatório parcial
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
		// Verificar se o usuário é um discente
		if (discente != null) {

			// Verificar se o discente possui um plano de trabalho associado
			PlanoTrabalho plano = planoDao.findByPrimaryKey( relatorioForm.getObj().getPlanoTrabalho().getId() , PlanoTrabalho.class);
			if (plano == null) {
				addMensagemErro("O plano de trabalho informado não foi localizado na base de dados.", req);
				return listarPlanos(mapping, form, req, res);
			}

			// Verificar prazo de envio
			RelatorioBolsaParcialValidator.validarPeriodoEnvio(plano, newListaMensagens(req));
			
			if ( flushErros(req) )
				return listarPlanos(mapping, form, req, res);
							
			if (plano.getRelatorioBolsaParcial() != null) {

				// Verifica se o relatório já foi submetido ao orientador para dar parecer
				if (plano.getRelatorioBolsaParcial().isEnviado()) {
					addMensagemErro("Não é possível modificar o relatório pois voce já submeteu ao parecer do orientador", req);
					return listarPlanos(mapping, form, req, res);
				}
				
				// Verificar se o orientador já emitiu o parecer
				if (plano.getRelatorioBolsaParcial().getParecerOrientador() != null) {
					addMensagemErro("Não é possível modificar o relatório pois o parecer do orientador já foi emitido", req);
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
	 * para o envio do relatório parcial
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
					addMensagemErro("O plano de trabalho informado não foi localizado na base de dados.", req);
					return mapping.findForward(getSubSistemaCorrente(req).getForward());
				}
				
				MembroProjetoDiscente ultimoDiscentePlano = dao.findUltimoDiscenteFinalizado(plano);
				if(ultimoDiscentePlano == null){
					addMensagemErro("O plano de trabalho informado não possui alunos de iniciação científica associados.", req);
					return mapping.findForward(getSubSistemaCorrente(req).getForward());
				}

				if ( flushErros(req) )
					return mapping.findForward("planos");
								
				if (plano.getRelatorioBolsaParcial() != null) {

					// Verificar se o orientador já emitiu o parecer
					if (plano.getRelatorioBolsaParcial().getParecerOrientador() != null)
						addWarning("O parecer do orientador já foi emitido", req);

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
	 * Validar os dados e enviar o relatório parcial
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
		// Gravar relatório
		try {
			// se o aluno estiver enviando depois de ser excluído do plano de trabalho, vai ser null.
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

			// Validar relatório
			ListaMensagens erros = relatorio.validate();
			if (!erros.isEmpty()) {
				addMensagens(erros.getMensagens(), req);
				relatorioForm.getObj().setPlanoTrabalho(getGenericDAO(req).findByPrimaryKey(relatorio.getPlanoTrabalho().getId(), PlanoTrabalho.class));
				req.setAttribute(mapping.getName(), relatorioForm);
				return mapping.findForward("formEnvio");
			}
			
			relatorio.setCodMovimento(SigaaListaComando.ENVIAR_RELATORIO_PARCIAL_BOLSA_PESQUISA);
			relatorio = (RelatorioBolsaParcial) execute(relatorio, req);
			addInformation("Relatório Parcial de Bolsa enviado com sucesso!", req);
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
	 * Faz uma verificação para afim de verificar se o usuário logado é discente
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
	 * Método responsável pela gravação do relatório de bolsa parcial
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
		
		// Gravar relatório
		try {
			// se o aluno estiver enviando depois de ser excluído do plano de trabalho, vai ser null.
			if (relatorio.getMembroDiscente() == null) {
				if( getSubSistemaAtual(req) == SigaaSubsistemas.PORTAL_DISCENTE.getId() && ((Usuario) getUsuarioLogado(req)).getDiscenteAtivo() != null ){
					Long matricula = ((Usuario) getUsuarioLogado(req)).getDiscenteAtivo().getMatricula();
					relatorio.setMembroDiscente(dao.findByPlanoTrabalho(relatorio.getPlanoTrabalho(), matricula));
				}else 
				relatorio.setMembroDiscente(relatorio.getPlanoTrabalho().getMembroProjetoDiscente());
			}else
				relatorio.setMembroDiscente(relatorio.getMembroDiscente());

			// Verifica se o relatório já existe no banco e popula o seu Id
			Integer id = dao2.findIdByPlanoAndMembro(relatorio.getPlanoTrabalho().getId(), relatorio.getMembroDiscente().getId());
			if(id != null)
				relatorio.setId(id);
			
			relatorio.setCodMovimento(SigaaListaComando.GRAVAR_RELATORIO_PARCIAL_BOLSA_PESQUISA);
			relatorio = (RelatorioBolsaParcial) execute(relatorio, req);
			addInformation("Relatório Parcial de Bolsa gravado com sucesso!", req);
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
			addMensagemErro("Somente servidores podem acessar esta operação!", req);
			return getMappingSubSistema(req, mapping);
		}

		// Buscar planos de trabalho do docente
		Collection<RelatorioBolsaParcial> relatorios = null;
		RelatorioBolsaParcialDao relatorioDao = getDAO(RelatorioBolsaParcialDao.class, req);
		relatorios = relatorioDao.findByOrientador(servidor.getId(), null);

		if (relatorios == null || relatorios.isEmpty()) {
			addMensagemErro("Você não possui relatórios parciais a emitir parecer", req);
			return getMappingSubSistema(req, mapping);
		} else {
			req.setAttribute("relatorios", relatorios);
		}

		return mapping.findForward("listaOrientandos");
	}

	/**
	 * Buscar os relatórios submetidos por um aluno
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
			addMensagemErro("Somente alunos podem acessar esta operação!", req);
			return getMappingSubSistema(req, mapping);
		}

		// Buscar planos de trabalho do docente
		Collection<RelatorioBolsaParcial> relatorios = null;
		RelatorioBolsaParcialDao relatorioDao = getDAO(RelatorioBolsaParcialDao.class, req);
//		relatorios = relatorioDao.findByDiscente( discente );
		relatorios = relatorioDao.findAllByDiscente( discente );
		

		if (relatorios == null || relatorios.isEmpty()) {
			addMensagemErro("Não foram encontrados relatórios parciais de iniciação científica submetidos por você", req);
			return getMappingSubSistema(req, mapping);
		} else {
			req.setAttribute("relatorios", relatorios);
		}

		return mapping.findForward("listaDiscente");
	}

	/**
	 * Buscar o relatório selecionado e redirecionar para o formulário do parecer
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
			addMensagemErro("É necessário selecionar um relatório para a emissão do parecer", req);
			return listarOrientandos(mapping, form, req, res);
		}

		RelatorioBolsaParcial relatorio = getGenericDAO(req).findByPrimaryKey(idRelatorio, RelatorioBolsaParcial.class);
		if (relatorio == null) {
			addMensagemErro("O relatório informado não foi encontrado", req);
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
			erros.addErro("Informe o parecer do relatório parcial");
		}
		else if(relatorio.getParecerOrientador().trim().length() > 5000) {
			erros.addErro("O parecer deve conter no máximo 5000 caracteres");
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
			addInformation("Parecer de relatório parcial emitido com sucesso!", req);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens().getMensagens(), req);
			relatorioForm.getObj().setPlanoTrabalho(getGenericDAO(req).findByPrimaryKey(relatorio.getPlanoTrabalho().getId(), PlanoTrabalho.class));
			req.setAttribute(mapping.getName(), relatorioForm);
			return mapping.findForward("formParecer");
		}

		return listarOrientandos(mapping, form, req, res);
	}

	/** 
	 * Metódo resposável pela visualização de um bolsa parcial  
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
			addMensagemErro("Relatório Parcial não encontrado!", req);
			return cancelar(mapping, form, req, res);
		} else {
			req.setAttribute("relatorio", relatorio);
		}

		return mapping.findForward("view");
	}

	/**
	 * Consulta do relatório parcial pelo discente
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
			addMensagemErro("Somente discentes podem acessar esta operação!", req);
			return getMappingSubSistema(req, mapping);
		}

		PlanoTrabalhoDao planoDao = getDAO(PlanoTrabalhoDao.class, req);
		PlanoTrabalho plano = planoDao.findUltimoByDiscente(discente.getId(), CalendarUtils.getAnoAtual());

		if (plano == null) {
			addMensagemErro("Você não está associado a um plano de trabalho", req);
			return getMappingSubSistema(req, mapping);
		}

		if (plano.getRelatorioBolsaParcial() == null) {
			addMensagemErro("Você ainda não enviou seu relatório parcial", req);
			return getMappingSubSistema(req, mapping);
		}

		req.setAttribute("relatorio", plano.getRelatorioBolsaParcial());
		return view(mapping, form, req, res);
	}

	/**
	 * Busca de planos de trabalho com relatórios enviados
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
	 * Esse método possibilita a remoção de um parecer sobre um bolsa  
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
			addMensagemErro("É necessário selecionar um relatório", req);
			req.setAttribute("popular", true);
			return relatorio(mapping, form, req, res);
		}

		RelatorioBolsaParcial relatorio = getGenericDAO(req).findByPrimaryKey(idRelatorio, RelatorioBolsaParcial.class);
		if (relatorio == null) {
			addMensagemErro("Relatório Parcial não encontrado!", req);
			return cancelar(mapping, form, req, res);
		} else {
			// Validar remoção do parecer
			if (relatorio.getParecerOrientador() == null || "".equals(relatorio.getParecerOrientador().trim())) {
				addMensagemErro("Este Relatório Parcial não possui parecer.", req);
				return relatorio(mapping, form, req, res);
			}
			try {
				relatorio.setCodMovimento( SigaaListaComando.REMOVER_PARECER_RELATORIO_PARCIAL_BOLSA_PESQUISA );
				execute(relatorio, req);
				addInformation("Parecer de relatório parcial removido com sucesso!", req);
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens().getMensagens(), req);
				relatorio(mapping, form, req, res);
			}

			return relatorio(mapping, form, req, res);
		}
	}

	/**
	 * Esse método realiza a remoção de uma bolsa parcial
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
