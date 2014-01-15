/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 22/06/2007
 *
 */
package br.ufrn.sigaa.pesquisa.struts;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.pesquisa.MembroProjetoDiscenteDao;
import br.ufrn.sigaa.arq.dao.pesquisa.PlanoTrabalhoDao;
import br.ufrn.sigaa.arq.dao.pesquisa.RelatorioBolsaFinalDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.parametros.dominio.ParametrosPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.CotaBolsas;
import br.ufrn.sigaa.pesquisa.dominio.MembroProjetoDiscente;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.RelatorioBolsaFinal;
import br.ufrn.sigaa.pesquisa.dominio.SiglaUnidadePesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa;
import br.ufrn.sigaa.pesquisa.form.RelatorioBolsaFinalForm;
import br.ufrn.sigaa.pesquisa.negocio.RelatorioBolsaFinalValidator;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Action responsável pelo controle do envio, consulta e emissão de parecer
 * dos relatórios finais de bolsa
 *
 * @author Ricardo Wendell
 *
 */
public class RelatorioBolsaFinalAction extends AbstractCrudAction {

    /**
     * Lista os planos de trabalho do aluno que podem ter relatórios finais enviados ou a ser enviados.
     * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/portais/discente/menu_discente.jsp</li>
	 *	</ul>
	 *
     */
	public ActionForward listarPlanos(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		// Validar acesso
		if ( !getAcessoMenu(req).isDiscente() && !getAcessoMenu(req).isDiscenteMedio() ) {
			addMensagemErro("Somente discentes podem enviar relatórios finais de bolsa. Caso você seja um aluno, contacte os administradores do " +
					RepositorioDadosInstitucionais.get("siglaSigaa") + " através da opção 'Abrir Chamado', localizada no cabecalho do sistema.", req);
			return getMappingSubSistema(req, mapping);
		}

		DiscenteAdapter discente = ((Usuario) getUsuarioLogado(req)).getDiscenteAtivo();

		// Buscar planos associados ao discente
		PlanoTrabalhoDao planoDao = getDAO(PlanoTrabalhoDao.class, req);
		Collection<PlanoTrabalho> planos = planoDao.findAllEmAndamentoByDiscente(discente);
		
		Collection<PlanoTrabalho> planosQueParticipou = planoDao.findAllPlanosQueParticipou(discente);
		
		MembroProjetoDiscenteDao mpdDao = getDAO(MembroProjetoDiscenteDao.class, req);

		for (PlanoTrabalho pt : planosQueParticipou) {
			MembroProjetoDiscente ultimoDiscente = mpdDao.findUltimoDiscenteFinalizado(pt);
			if (ultimoDiscente.getDiscente().getId() == discente.getId()) 
				planos.add(pt);
		}
		
		Collection<PlanoTrabalho> planosQueNaoPrecisaRelatorio = new HashSet<PlanoTrabalho>(0);
		
		for( PlanoTrabalho p: planos){
			if(!p.getTipoBolsa().isNecessitaRelatorio())
				planosQueNaoPrecisaRelatorio.add(p);			
		}
		
		if ( planos.isEmpty() ) {
			addMensagemErro("Não foram encontrados planos de trabalho em andamento designados para você. " +
					"Verifique junto ao seu orientador se o cadastro do seu plano foi realizado. Em caso de dúvida, contacte a "+ 
					ParametroHelper.getInstance().getParametro(ParametrosPesquisa.SIGLA_NOME_PRO_REITORIA_PESQUISA)+".", req);
			return mapping.findForward(getSubSistemaCorrente(req).getForward());
		}else if(planos.size() == planosQueNaoPrecisaRelatorio.size()) {
			addMensagemErro("A(s) modalidade(s) de bolsa associada(s) ao seu(s) plano(s) não requer(em) a submissão de relatório final.", req);
			return mapping.findForward(getSubSistemaCorrente(req).getForward());
		}else if(planos.size() != planosQueNaoPrecisaRelatorio.size() && planosQueNaoPrecisaRelatorio.size() > 0) {
			addMensagem(new MensagemAviso("Somente os planos de trabalho que requerem a submissão de relatório são listados abaixo.", TipoMensagemUFRN.WARNING), req);
			planos.removeAll(planosQueNaoPrecisaRelatorio);
			req.setAttribute("planos", planos);
			return mapping.findForward("listaPlanos");
		}else {
			req.setAttribute("planos", planos);
			return mapping.findForward("listaPlanos");
		}

	}

	/**
	 * Busca o plano de trabalho do discente (se existir) e prepara
	 * para o envio do relatório final
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/RelatorioBolsaFinal/lista_planos.jsp</li>
	 *	</ul>
	 */
	public ActionForward popularEnvio(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		RelatorioBolsaFinalForm relatorioForm = (RelatorioBolsaFinalForm) form;
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
			
			// Verificar o prazo de envio
			RelatorioBolsaFinalValidator.validarPeriodoEnvio(plano, newListaMensagens(req));
			
			if ( flushErros(req) )
				return listarPlanos(mapping, form, req, res);
			
			if (plano.getRelatorioBolsaFinal() != null) {

				// Verifica se o relatório já foi submetido ao orientador para dar parecer
				if (plano.getRelatorioBolsaFinal().isEnviado()) {
					addMensagemErro("Não é possível modificar o relatório pois voce já submeteu ao parecer do orientador", req);
					return listarPlanos(mapping, form, req, res);
				}					
				
				// Verificar se o orientador já emitiu o parecer
				if (plano.getRelatorioBolsaFinal().getParecerOrientador() != null) {
					addMensagemErro("Não é possível modificar o relatório pois o parecer do orientador já foi emitido", req);
					return listarPlanos(mapping, form, req, res);
				}

				relatorioForm.setObj(plano.getRelatorioBolsaFinal());
			} else {
				relatorioForm.getObj().setPlanoTrabalho(plano);
                if(plano.getMembroProjetoDiscente() != null)
                    relatorioForm.getObj().setMembroDiscente(plano.getMembroProjetoDiscente());
                else
                    relatorioForm.getObj().setMembroDiscente(plano.getMembrosDiscentes().iterator().next());
			}
			req.setAttribute("fimPeriodo", plano.getTipoBolsa().getFimEnvioRelatorioFinal());
			req.setAttribute(mapping.getName(), relatorioForm);
		}

		return mapping.findForward("formEnvio");
	}

	/**
	 * Busca o plano de trabalho do discente e prepara
     * para o envio do relatório final feito pelo gestor do módulo de pesquisa.
     * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/RelatorioBolsaFinal/relatorio.jsp</li>
	 *	</ul>
	 *
	 */
	public ActionForward popularEnvioPropesq(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);
		
		RelatorioBolsaFinalForm relatorioForm = (RelatorioBolsaFinalForm) form;
		PlanoTrabalhoDao planoDao = getDAO(PlanoTrabalhoDao.class, req);
		MembroProjetoDiscenteDao membroProjetoDao = getDAO(MembroProjetoDiscenteDao.class, req);

		try {
		
			// Verificar se o discente possui um plano de trabalho associado
			PlanoTrabalho plano = planoDao.findByPrimaryKey( relatorioForm.getObj().getPlanoTrabalho().getId() , PlanoTrabalho.class);
			if (plano == null) {
				addMensagemErro("O plano de trabalho informado não foi localizado na base de dados.", req);
				return mapping.findForward(getSubSistemaCorrente(req).getForward());
			}
			
			if (plano != null) {
				prepareMovimento(SigaaListaComando.ENVIAR_RELATORIO_FINAL_BOLSA_PESQUISA, req);

				if (plano.getRelatorioBolsaFinal() != null) {

					// Verificar se o orientador já emitiu o parecer
					if (plano.getRelatorioBolsaFinal().getParecerOrientador() != null) {
						addMensagemErro("Não é possível modificar o relatório pois o parecer do orientador já foi emitido", req);
						return mapping.findForward(getSubSistemaCorrente(req).getForward());
					}

					relatorioForm.setObj(plano.getRelatorioBolsaFinal());
				} else {
					relatorioForm.getObj().setPlanoTrabalho(plano);
	                if(plano.getMembroProjetoDiscente() != null)
	                	relatorioForm.getObj().setMembroDiscente(plano.getMembroProjetoDiscente());
	                else {
	                	if(!isEmpty(plano.getMembrosDiscentes()))
	                		relatorioForm.getObj().setMembroDiscente(membroProjetoDao.findUltimoDiscenteFinalizado(plano));
	                	else {
	                		addMensagemErro("Não é possível enviar o relatório pois nenhum discente foi indicado para o plano de trabalho.", req);
	    					return mapping.findForward(getSubSistemaCorrente(req).getForward());
	                	}
	                }
				}
				req.setAttribute(mapping.getName(), relatorioForm);
			}
		
		} finally {
			planoDao.close();
			membroProjetoDao.close();
		}

		return mapping.findForward("formEnvio");
	}

	/**
	 * Validar os dados e enviar o relatório final
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/RelatorioBolsaFinal/form_envio.jsp</li>
	 *	</ul>
	 *
	 */
	public ActionForward enviar(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		prepareMovimento(SigaaListaComando.ENVIAR_RELATORIO_FINAL_BOLSA_PESQUISA, req);
		
		RelatorioBolsaFinalForm relatorioForm = (RelatorioBolsaFinalForm) form;
		RelatorioBolsaFinal relatorio =  relatorioForm.getObj();

		// Validar relatório
		ListaMensagens erros = relatorio.validate();
		if (!erros.isEmpty()) {
			addMensagens(erros.getMensagens(), req);
			relatorioForm.getObj().setPlanoTrabalho(getGenericDAO(req).findByPrimaryKey(relatorio.getPlanoTrabalho().getId(), PlanoTrabalho.class));
			relatorioForm.getObj().setMembroDiscente(relatorioForm.getObj().getPlanoTrabalho().getMembroProjetoDiscente());
			req.setAttribute(mapping.getName(), relatorioForm);
			return mapping.findForward("formEnvio");
		}

		MembroProjetoDiscenteDao dao = getDAO(MembroProjetoDiscenteDao.class, req);
		
		// Gravar relatório
		try {
			// se o aluno estiver enviando depois de ser excluído do plano de trabalho, vai ser null.
			if (relatorio.getMembroDiscente() == null) {
				if( getSubSistemaAtual(req) == SigaaSubsistemas.PORTAL_DISCENTE.getId() && ((Usuario) getUsuarioLogado(req)).getDiscenteAtivo() != null ){
					Long matricula = ((Usuario) getUsuarioLogado(req)).getDiscenteAtivo().getMatricula();
					relatorio.setMembroDiscente(dao.findByPlanoTrabalho(relatorio.getPlanoTrabalho(), matricula));
				}
				else {
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
			}
			else 
				relatorio.setMembroDiscente(relatorio.getMembroDiscente());
			
			
			relatorio.setCodMovimento(SigaaListaComando.ENVIAR_RELATORIO_FINAL_BOLSA_PESQUISA);
			relatorio = (RelatorioBolsaFinal) execute(relatorio, req);
			addInformation("Relatório Final de Bolsa enviado com sucesso!", req);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens().getMensagens(), req);
			relatorioForm.getObj().setPlanoTrabalho(getGenericDAO(req).findByPrimaryKey(relatorio.getPlanoTrabalho().getId(), PlanoTrabalho.class));
			relatorioForm.getObj().setMembroDiscente(relatorioForm.getObj().getPlanoTrabalho().getMembroProjetoDiscente());
			req.setAttribute(mapping.getName(), relatorioForm);
			return mapping.findForward("formEnvio");
		}

		req.setAttribute("comprovante", true);
		req.setAttribute("relatorio", relatorio);
		return view(mapping, form, req, res);
	}

	/**
	 * Validar os dados e gravar o relatório parcial
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/RelatorioBolsaFinal/form_envio.jsp</li>
	 *	</ul>
	 *
	 */
	public ActionForward gravar(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		prepareMovimento(SigaaListaComando.GRAVAR_RELATORIO_FINAL_BOLSA_PESQUISA, req);
		
		RelatorioBolsaFinalForm relatorioForm = (RelatorioBolsaFinalForm) form;
		RelatorioBolsaFinal relatorio =  relatorioForm.getObj();
		
		MembroProjetoDiscenteDao dao = getDAO(MembroProjetoDiscenteDao.class, req);
		
		// Gravar relatório
		try {
			// se o aluno estiver enviando depois de ser excluído do plano de trabalho, vai ser null.
			if (relatorio.getMembroDiscente() == null) {
				if( getSubSistemaAtual(req) == SigaaSubsistemas.PORTAL_DISCENTE.getId() && ((Usuario) getUsuarioLogado(req)).getDiscenteAtivo() != null ){
					Long matricula = ((Usuario) getUsuarioLogado(req)).getDiscenteAtivo().getMatricula();
					relatorio.setMembroDiscente(dao.findByPlanoTrabalho(relatorio.getPlanoTrabalho(), matricula));
				}
				else
					relatorio.setMembroDiscente(null);
			}
			else 
				relatorio.setMembroDiscente(relatorio.getMembroDiscente());
			
			relatorio.setCodMovimento(SigaaListaComando.GRAVAR_RELATORIO_FINAL_BOLSA_PESQUISA);
			relatorio = (RelatorioBolsaFinal) execute(relatorio, req);
			addInformation("Relatório Final de Bolsa gravado com sucesso!", req);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens().getMensagens(), req);
			relatorioForm.getObj().setPlanoTrabalho(getGenericDAO(req).findByPrimaryKey(relatorio.getPlanoTrabalho().getId(), PlanoTrabalho.class));
			req.setAttribute(mapping.getName(), relatorioForm);
			return mapping.findForward("formEnvio");
		}

		req.setAttribute("relatorio", relatorio);
		return view(mapping, form, req, res);
	}	
	
	/**
	 * Listar relatórios submetidos pelos orientandos do docente logado, caso este possua algum.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 *	</ul>
	 * 
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
		Collection<RelatorioBolsaFinal> relatorios = null;
		RelatorioBolsaFinalDao relatorioDao = getDAO(RelatorioBolsaFinalDao.class, req);
		relatorios = relatorioDao.findByOrientadorAndStatus(servidor.getId(), null);

		if (relatorios == null || relatorios.isEmpty()) {
			addMensagemErro("Você não possui relatórios finais a emitir parecer", req);
			return getMappingSubSistema(req, mapping);
		} else {
			req.setAttribute("relatorios", relatorios);
		}

		return mapping.findForward("listaOrientandos");
	}

	/**
	 * Buscar os relatórios submetidos por um aluno
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/portais/discente/menu_docente.jsp</li>
	 *	</ul>
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
		Collection<RelatorioBolsaFinal> relatorios = null;
		RelatorioBolsaFinalDao relatorioDao = getDAO(RelatorioBolsaFinalDao.class, req);
		relatorios = relatorioDao.findAllByDiscente( discente );

		if (relatorios == null || relatorios.isEmpty()) {
			addMensagemErro("Não foram encontrados relatórios finais de iniciação científica submetidos por você", req);
			return getMappingSubSistema(req, mapping);
		} else {
			req.setAttribute("relatorios", relatorios);
		}

		return mapping.findForward("listaDiscente");
	}

	/**
	 * Buscar o relatório selecionado e redirecionar para o formulário do parecer
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/RelatorioBolsaFinal/lista_orientandos.jsp</li>
	 *	</ul>
	 *
	 */
	public ActionForward selecionarBolsista(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		RelatorioBolsaFinalForm relatorioForm = (RelatorioBolsaFinalForm) form;

		int idRelatorio = getParameterInt(req,"idRelatorio");
		if (idRelatorio == 0) {
			addMensagemErro("É necessário selecionar um relatório para a emissão do parecer", req);
			return listarOrientandos(mapping, form, req, res);
		}

		RelatorioBolsaFinal relatorio = getGenericDAO(req).findByPrimaryKey(idRelatorio, RelatorioBolsaFinal.class);
		if (relatorio == null) {
			addMensagemErro("O relatório informado não foi encontrado", req);
			return listarOrientandos(mapping, form, req, res);
		}

		prepareMovimento(SigaaListaComando.PARECER_RELATORIO_FINAL_BOLSA_PESQUISA, req);

		relatorioForm.setObj(relatorio);
		req.setAttribute(mapping.getName(), relatorioForm);
		return mapping.findForward("formParecer");
	}


	/**
	 * Persistir o parecer emitido pelo orientador/gestor do módulo de pesquisa
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/RelatorioBolsaFinal/form_parecer.jsp</li>
	 *	</ul>
	 *
	 */
	public ActionForward emitirParecer(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		RelatorioBolsaFinalForm relatorioForm = (RelatorioBolsaFinalForm) form;
		RelatorioBolsaFinal relatorio = relatorioForm.getObj();

		// Validar parecer
		ListaMensagens erros = new ListaMensagens();
		if (relatorio.getParecerOrientador() == null || "".equals(relatorio.getParecerOrientador().trim())) {
			erros.addErro("Informe o parecer do relatório final");
		}
		else if(relatorio.getParecerOrientador().trim().length() > 10000) {
			erros.addErro("O parecer deve conter no máximo 10000 caracteres");
		}

		if (!erros.isEmpty()) {
			addMensagens(erros.getMensagens(), req);
			relatorioForm.setObj(getGenericDAO(req).findByPrimaryKey(relatorio.getId(), RelatorioBolsaFinal.class));
			relatorioForm.getObj().setParecerOrientador(relatorio.getParecerOrientador());
			req.setAttribute(mapping.getName(), relatorioForm);
			return mapping.findForward("formParecer");
		}

		// Persistir parecer
		try {
			relatorio.setCodMovimento(SigaaListaComando.PARECER_RELATORIO_FINAL_BOLSA_PESQUISA);
			relatorio = (RelatorioBolsaFinal) execute(relatorio, req);
			addInformation("Parecer de relatório final emitido com sucesso!", req);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens().getMensagens(), req);
			relatorioForm.getObj().setPlanoTrabalho(getGenericDAO(req).findByPrimaryKey(relatorio.getPlanoTrabalho().getId(), PlanoTrabalho.class));
			req.setAttribute(mapping.getName(), relatorioForm);
			return mapping.findForward("formParecer");
		}

		if(isUserInRole(SigaaPapeis.GESTOR_PESQUISA, req) && SigaaSubsistemas.PESQUISA.equals(getSubSistemaCorrente(req)))
		    return relatorio(mapping, relatorioForm, req, res);
		return listarOrientandos(mapping, form, req, res);
	}

	/**
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/RelatorioBolsaFinal/lista_orientandos.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/RelatorioBolsaFinal/lista_planos.jsp</li>
	 *	</ul>
	 *
	 */
	@Override
	public ActionForward view(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		int idRelatorio = 0;
		RelatorioBolsaFinal relatorio = (RelatorioBolsaFinal) req.getAttribute("relatorio");
		if (relatorio == null) {
			idRelatorio = getParameterInt(req, "idRelatorio");
		} else {
			idRelatorio = relatorio.getId();
		}

		relatorio = getDAO(RelatorioBolsaFinalDao.class, req).findOtimizadoParaView(idRelatorio);

		if (relatorio == null) {
			addMensagemErro("Relatório Final não encontrado!", req);
			return cancelar(mapping, form, req, res);
		} else {
			req.setAttribute("relatorio", relatorio);
		}

		return mapping.findForward("view");
	}

	/**
	 * Consulta do relatório Final pelo discente
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/RelatorioBolsaFinal/lista_discente.jsp</li>
	 *	</ul>
	 *
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
		PlanoTrabalho plano = planoDao.findUltimoByDiscente( discente.getId(), CalendarUtils.getAnoAtual() );

		if (plano == null) {
			addMensagemErro("Você não está associado a um plano de trabalho", req);
			return getMappingSubSistema(req, mapping);
		}

		if (plano.getRelatorioBolsaFinal() == null) {
			addMensagemErro("Você ainda não enviou seu relatório Final", req);
			return getMappingSubSistema(req, mapping);
		}

		req.setAttribute("relatorio", plano.getRelatorioBolsaFinal());
		return view(mapping, form, req, res);
	}

	/**
	 * Busca de planos de trabalho com relatórios enviados
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/menu/iniciacao.jsp</li>
	 *	</ul>
	 *
	 */
	public ActionForward relatorio(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		int[] papeis = {SigaaPapeis.PORTAL_PLANEJAMENTO,SigaaPapeis.GESTOR_PESQUISA};
		checkRole(papeis, req);

		RelatorioBolsaFinalForm relForm = (RelatorioBolsaFinalForm) form;

		RelatorioBolsaFinalDao dao = getDAO(RelatorioBolsaFinalDao.class, req);
		Collection<RelatorioBolsaFinal> relatorios = null;

		// Popular dados auxiliares do formulário
		req.setAttribute("centros", dao.findAll(SiglaUnidadePesquisa.class, "unidade.nome", "asc"));
		req.setAttribute("modalidades", TipoBolsaPesquisa.getTipos());
		req.setAttribute("cotas", dao.findAll(CotaBolsas.class, "descricao", "desc"));

		if (!relForm.isBuscar()) {
			return mapping.findForward("relatorio");
		}

		// Verificar filtros selecionados
		Unidade centro = null;
		Unidade departamento = null;
		Discente discente = null;
		Integer modalidade = null;
		Servidor orientador = null;
		CotaBolsas cota = null;
		Boolean parecer = null;
		Boolean submetido = null;

		ListaMensagens erros = new ListaMensagens();
		for(int filtro : relForm.getFiltros() ){
			switch(filtro) {
				case RelatorioBolsaFinalForm.BUSCA_CENTRO:
					ValidatorUtil.validateRequired(relForm.getCentro(), "Centro", erros);
					centro = relForm.getCentro();
					break;
				case RelatorioBolsaFinalForm.BUSCA_DEPARTAMENTO:
					ValidatorUtil.validateRequired(relForm.getUnidade(), "Departamento", erros);
					departamento = relForm.getUnidade();
					break;
				case RelatorioBolsaFinalForm.BUSCA_ALUNO:
					ValidatorUtil.validateRequired(relForm.getDiscente(), "Discente", erros);
					discente = relForm.getDiscente();
					break;
				case RelatorioBolsaFinalForm.BUSCA_ORIENTADOR:
					ValidatorUtil.validateRequired(relForm.getOrientador(), "Orientador", erros);
					orientador = relForm.getOrientador();
					break;
				case RelatorioBolsaFinalForm.BUSCA_MODALIDADE:
					ValidatorUtil.validateRequired(relForm.getModalidade(), "Modalidade de Bolsa", erros);
					modalidade = relForm.getModalidade();
					break;
				case RelatorioBolsaFinalForm.BUSCA_COTA:
					ValidatorUtil.validateRequired(relForm.getCota(), "Cota", erros);
					cota = relForm.getCota();
					break;
				case RelatorioBolsaFinalForm.BUSCA_PARECER:
					parecer = relForm.getParecer();
					break;
				case RelatorioBolsaFinalForm.BUSCA_SUBMETIDO:
					submetido = relForm.getSubmetido();
					break;
			}
		}

		if (erros.isEmpty()) {
			relatorios = dao.filter(
					centro,
					departamento,
					discente,
					orientador,
					modalidade,
					cota,
					parecer,
					submetido);

			if ( relatorios.isEmpty() ) {
				addMensagemErro("Nenhum relatório foi encontrado de acordo com os critérios de busca informados.", req);
			}
			
			req.setAttribute("relatorios", relatorios);
			prepareMovimento(SigaaListaComando.REMOVER_PARECER_RELATORIO_FINAL_BOLSA_PESQUISA, req);
		} else {
			addMensagens(erros.getMensagens(), req);
		}

		
		return mapping.findForward("relatorio");
	}

	/**
	 * Remoção do parecer emitido pelo orientador
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/RelatorioBolsaFinal/relatorio.jsp</li>
	 *	</ul>
	 *
	 */
	public ActionForward removerParecer(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {

		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);

		int idRelatorio = getParameterInt(req, "idRelatorio");
		if( idRelatorio <= 0 ){
			addMensagemErro("É necessário selecionar um relatório", req);
			req.setAttribute("popular", true);
			return relatorio(mapping, form, req, res);
		}

		RelatorioBolsaFinal relatorio = getGenericDAO(req).findByPrimaryKey(idRelatorio, RelatorioBolsaFinal.class);
		if (relatorio == null) {
			addMensagemErro("Relatório final não encontrado!", req);
			return cancelar(mapping, form, req, res);
		} else {
			// Validar remoção do parecer
			if (relatorio.getParecerOrientador() == null || "".equals(relatorio.getParecerOrientador().trim())) {
				addMensagemErro("Este Relatório final não possui parecer.", req);
				return relatorio(mapping, form, req, res);
			}
			try {
				relatorio.setCodMovimento( SigaaListaComando.REMOVER_PARECER_RELATORIO_FINAL_BOLSA_PESQUISA );
				execute(relatorio, req);
				addInformation("Parecer de relatório final removido com sucesso!", req);
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens().getMensagens(), req);
				relatorio(mapping, form, req, res);
			}

			return relatorio(mapping, form, req, res);
		}
	}
	/**
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/RelatorioBolsaFinal/relatorio.jsp</li>
	 *	</ul>
	 */
	@Override
	public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);
		
		RelatorioBolsaFinalForm relatorioForm = (RelatorioBolsaFinalForm) form;
		RelatorioBolsaFinal relatorio = relatorioForm.getObj();
		PlanoTrabalhoDao planoDao = getDAO(PlanoTrabalhoDao.class, req);

		// Verificar se o discente possui um plano de trabalho associado
		relatorio = planoDao.findByPrimaryKey( relatorioForm.getObj().getId() , RelatorioBolsaFinal.class);

		if (relatorio != null) {
			prepareMovimento(SigaaListaComando.ENVIAR_RELATORIO_FINAL_BOLSA_PESQUISA, req);

			// Verificar se o orientador já emitiu o parecer
			if (relatorio.getParecerOrientador() != null && relatorio.getDataParecer() != null) {
				addMensagemErro("Não é possível modificar o relatório pois o parecer do orientador já foi emitido", req);
				return mapping.findForward(getSubSistemaCorrente(req).getForward());
			}

			relatorioForm.setObj(relatorio);
		}
		req.setAttribute(mapping.getName(), relatorioForm);

		return mapping.findForward("formEnvio");
	}

	/**
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/RelatorioBolsaFinal/relatorio.jsp</li>
	 *	</ul>
	 */
	@Override
	public ActionForward remove(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);
		getForm(form).setConfirm(true);
		RelatorioBolsaFinalForm relatorioForm = (RelatorioBolsaFinalForm) form;
		getGenericDAO(req).updateFields(RelatorioBolsaFinal.class, relatorioForm.getObj().getId(), 
				new String [] {"ativo", "enviado", "parecerOrientador", "dataParecer"}, 
				new Object [] {false, false, null, null});
		return relatorio(mapping, form, req, res);
	}

}
