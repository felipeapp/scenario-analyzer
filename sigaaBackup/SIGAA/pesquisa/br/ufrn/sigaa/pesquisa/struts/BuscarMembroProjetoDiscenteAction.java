/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/12/2006
 *
 */
package br.ufrn.sigaa.pesquisa.struts;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.pesquisa.MembroProjetoDiscenteDao;
import br.ufrn.sigaa.arq.dao.pesquisa.PlanoTrabalhoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pesquisa.dominio.CotaBolsas;
import br.ufrn.sigaa.pesquisa.dominio.GrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.MembroProjetoDiscente;
import br.ufrn.sigaa.pesquisa.dominio.RelatorioBolsaFinal;
import br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoStatusPlanoTrabalho;
import br.ufrn.sigaa.pesquisa.form.MembroProjetoDiscenteForm;
import br.ufrn.sigaa.pesquisa.negocio.AlterarMembroProjetoDiscenteValidator;
import br.ufrn.sigaa.pesquisa.negocio.MovimentoAlterarProjetoDiscente;
import br.ufrn.sigaa.pesquisa.negocio.MovimentoIndicarBolsista;
import br.ufrn.sigaa.pessoa.dominio.Banco;
import br.ufrn.sigaa.pessoa.dominio.ContaBancaria;

/**
 * Action responsável por realizar buscas por discentes vinculados a planos de trabalho
 * de projetos de pesquisa
 *
 * @author Ricardo Wendell
 *
 */
public class BuscarMembroProjetoDiscenteAction extends AbstractCrudAction {

	/**
	 * Popular a jsp para a busca de discentes
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>sigaa.war/WEB-INF/jsp/pesquisa/menus/iniciacao.jsp</li>
	 * </ul>
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward popular(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);
		MembroProjetoDiscenteForm membroForm = (MembroProjetoDiscenteForm) form;

		req.setAttribute(mapping.getName(), membroForm);
		return mapping.findForward("listar");
	}

	/**
	 * Responsável pela visualização dos Discentes Membros do projeto.
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>sigaa.war/WEB-INF/jsp/pesquisa/MembroProjetoDiscente/relatorio.jsp</li>
	 * </ul>
	 */
	@Override
	public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {

		MembroProjetoDiscenteForm membroForm = (MembroProjetoDiscenteForm) form;

		int idMembroDiscente = getParameterInt(req, "idMembroDiscente");
		if (idMembroDiscente <= 0) {
			if( membroForm.getObj().getId() <= 0 ){
				addMensagemErro("É necessário selecionar um discente", req);
				req.setAttribute("popular", true);
				return relatorio(mapping, form, req, res);
			} else
				idMembroDiscente = membroForm.getObj().getId();
		}

		GenericDAO dao = getGenericDAO(req);
		MembroProjetoDiscente membroDiscente = dao.findByPrimaryKey(idMembroDiscente, MembroProjetoDiscente.class);

		if (membroDiscente != null) {
			UsuarioDao usuarioDao = getDAO(UsuarioDao.class, req);

			if (membroDiscente.getDiscente().getPessoa().getContaBancaria() == null) {
				membroDiscente.getDiscente().getPessoa().setContaBancaria(new ContaBancaria());
			}

//			req.setAttribute("membroDiscente", membroDiscente);
			membroForm.setObj( membroDiscente );

			req.setAttribute(mapping.getName(), membroForm);

			prepareMovimento(SigaaListaComando.ALTERAR_DADOS_BOLSISTA, req);
			req.setAttribute("bancos", dao.findAll(Banco.class, "denominacao", "asc"));
			req.setAttribute("usuarioBolsista", usuarioDao.findByDiscente(membroDiscente.getDiscente()));
		}
		return mapping.findForward("view");
	}

	/**
	 * Responsável pela alteração Dicentes Membros do projeto.
	 * 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>sigaa.war/WEB-INF/jsp/pesquisa/MembroProjetoDiscente/view.jsp</li>
	 * </ul>
	 * 
	 */
	@Override
	public ActionForward persist(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {

		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);
		MembroProjetoDiscenteForm membroForm = (MembroProjetoDiscenteForm) form;
		ListaMensagens lista = new ListaMensagens();
		AlterarMembroProjetoDiscenteValidator.validaAlteracao(membroForm.getObj(), membroForm, lista);

		if (!lista.isErrorPresent()) {

			MovimentoAlterarProjetoDiscente movAlterar = new MovimentoAlterarProjetoDiscente();
			movAlterar.setMembroDiscente( membroForm.getObj() );
			movAlterar.setCodMovimento( SigaaListaComando.ALTERAR_DADOS_BOLSISTA );

			try {
				execute(movAlterar, req);
			} catch (NegocioException e) {
				req.setAttribute(mapping.getName(), form);
				return view(mapping, form, req, res);
			}
			addMensagem(req, MensagensArquitetura.OPERACAO_SUCESSO);
			req.setAttribute("popular", true);
			return relatorio(mapping, membroForm, req, res);
		} else {
			addMensagens(lista.getMensagens(), req);
			req.setAttribute(mapping.getName(), form);
			return view(mapping, form, req, res);
		}
	}


	/**
	 * Buscar os discentes associados a planos de trabalho de acordo com os critérios de busca.
	 * 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>sigaa.war/WEB-INF/jsp/pesquisa/menus/iniciacao.jsp</li>
	 * </ul>
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward listar(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);

		MembroProjetoDiscenteDao membroDao = getDAO(MembroProjetoDiscenteDao.class, req);
		MembroProjetoDiscenteForm membroForm = (MembroProjetoDiscenteForm) form;

		// Buscar discentes de acordo com o critério escolhido
		Collection<MembroProjetoDiscente> membros = null;
		switch (membroForm.getTipoBusca()) {
			case MembroProjetoDiscenteForm.BUSCA_MATRICULA:
				membros = membroDao.findByMatricula(membroForm.getObj().getDiscente().getMatricula(), false);
				break;

			case MembroProjetoDiscenteForm.BUSCA_DISCENTE:
				membros = membroDao.findByDiscente(membroForm.getObj().getDiscente(), false);
				break;

			case MembroProjetoDiscenteForm.BUSCA_ORIENTADOR:
				membros = membroDao.findByOrientador(membroForm.getObj().getPlanoTrabalho().getOrientador().getId(), false);
				break;
			case MembroProjetoDiscenteForm.BUSCA_TODOS_ATIVOS:
				membros = membroDao.findAtivos();
				break;
		}


		req.setAttribute("lista", membros);
		req.setAttribute(mapping.getName(), membroForm);
		return mapping.findForward("listar");
	}

	/**
	 * Buscar os resumos dos relatórios submetidos ao congresso de iniciação científica
	 * 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>sigaa.war/WEB-INF/jsp/pesquisa/MembroProjetoDiscente/resumos_congresso.jsp</li>
	 * </ul>
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward resumosCongresso(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);
		MembroProjetoDiscenteForm membroForm = (MembroProjetoDiscenteForm) form;

		if (req.getParameter("popular") != null) {
			membroForm.getObj().getPlanoTrabalho().getProjetoPesquisa().setAno(CalendarUtils.getAnoAtual());
			req.setAttribute(mapping.getName(), membroForm);
			return mapping.findForward("formResumosCongresso");
		}

		PlanoTrabalhoDao planoDao = getDAO(PlanoTrabalhoDao.class, req);
		Collection<RelatorioBolsaFinal> relatorios = planoDao.findResumosCongresso(membroForm.getObj().getPlanoTrabalho().getProjetoPesquisa().getAno());

		if (relatorios != null && !relatorios.isEmpty()) {
			req.setAttribute("relatorios", relatorios);
		} else {
			addInformation("Nenhum resumo encontrado", req);
			return mapping.findForward("formResumosCongresso");
		}

		return mapping.findForward("resumosCongresso");
	}


	/**
	 * Relatório de alunos vinculados a projetos de pesquisa
	 *
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>sigaa.war/WEB-INF/jsp/pesquisa/MembroProjetoDiscente/relatorio.jsp</li>
	 * </ul>
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward relatorio(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);

		MembroProjetoDiscenteDao membroDao = getDAO(MembroProjetoDiscenteDao.class, req);
		CursoDao cursoDao = getDAO(CursoDao.class, req);
		MembroProjetoDiscenteForm membroForm = (MembroProjetoDiscenteForm) form;

		req.setAttribute("gruposPesquisa", membroDao.findAllProjection( GrupoPesquisa.class, "nome", "asc", new String[]{"id", "nome"} ) );
		req.setAttribute("centros", membroDao.findByExactField(Unidade.class, "tipoAcademica", TipoUnidadeAcademica.CENTRO, "asc", "nome"));
		req.setAttribute("cursos", cursoDao.findByNivel(NivelEnsino.GRADUACAO, true, false));
		req.setAttribute("modalidades", TipoBolsaPesquisa.getTipos());
		req.setAttribute("tiposStatus", TipoStatusPlanoTrabalho.getTipos());
		req.setAttribute("cotas", membroDao.findAllProjection( CotaBolsas.class, "descricao", "asc", new String[]{"id", "descricao"} ) );

		if (req.getParameter("popular") != null || req.getAttribute("popular") != null) {
			return mapping.findForward("relatorio");
		}

		Collection<MembroProjetoDiscente> lista = null;
		Integer idGrupoPesquisa = null;
		Integer idCentro = null;
		Integer idUnidade = null;
		Integer idAluno = null;
		Integer idOrientador = null;
		Integer idCurso = null;
		Integer modalidade = null;
		Character sexo = null;
		Integer status = null;
		Integer idCota = null;
		boolean somenteAtivos = false;
		boolean formatoRelatorio = false;

		ListaMensagens erros = new ListaMensagens();


		// Definição dos filtros e validações
		for(int filtro : membroForm.getFiltros() ){
			switch(filtro) {
			case TipoFiltroBolsista.GRUPO_PESQUISA:
				idGrupoPesquisa = membroForm.getObj().getPlanoTrabalho().getProjetoPesquisa().getLinhaPesquisa().getGrupoPesquisa().getId();
				ValidatorUtil.validateRequiredId(idGrupoPesquisa, "Grupo de Pesquisa", erros);
				break;
			case TipoFiltroBolsista.CENTRO:
				idCentro = membroForm.getCentro().getId();
				ValidatorUtil.validateRequiredId(idCentro, "Centro", erros);
				break;
			case TipoFiltroBolsista.DEPARTAMENTO:
				idUnidade = membroForm.getUnidade().getId();
				ValidatorUtil.validateRequiredId(idUnidade, "Unidade", erros);
				break;
			case TipoFiltroBolsista.NOME:
				idAluno = membroForm.getObj().getDiscente().getId();
				ValidatorUtil.validateRequiredId(idAluno, "Aluno", erros);
				break;
			case TipoFiltroBolsista.ORIENTADOR:
				idOrientador = membroForm.getMembroProjetoServidor().getServidor().getId();
				ValidatorUtil.validateRequiredId(idOrientador, "Orientador", erros);
				break;
			case TipoFiltroBolsista.MODALIDADE_BOLSA:
				modalidade = membroForm.getObj().getTipoBolsa().getId();
				ValidatorUtil.validateRequiredId(modalidade, "Modalidade da Bolsa", erros);
				break;
			case TipoFiltroBolsista.CURSO:
				idCurso = membroForm.getObj().getDiscente().getCurso().getId();
				ValidatorUtil.validateRequiredId(idCurso, "Curso", erros);
				break;
			case TipoFiltroBolsista.SEXO:
				sexo = membroForm.getObj().getDiscente().getPessoa().getSexo();
				break;
			case TipoFiltroBolsista.STATUS_RELATORIO:
				status = membroForm.getObj().getPlanoTrabalho().getStatus();
				break;
			case TipoFiltroBolsista.COTA:
				idCota = membroForm.getIdCota();
				ValidatorUtil.validateRequiredId(idCota, "Cota", erros);
				break;
			case TipoFiltroBolsista.SOMENTE_ATIVOS:
				somenteAtivos = true;
				break;
			case TipoFiltroBolsista.FORMATO_RELATORIO:
				formatoRelatorio = true;
				break;
			}
		}

		if(idGrupoPesquisa == null && idCentro == null && idUnidade == null && idAluno == null && idOrientador == null && idCurso == null && modalidade == null && status == null && sexo == null && idCota == null && !somenteAtivos){
			addMensagem(new MensagemAviso("Selecione um filtro de busca.", TipoMensagemUFRN.ERROR), req);
			return mapping.findForward("relatorio");
		}

		if (erros.isEmpty()) {
				lista = membroDao.filter(
						idGrupoPesquisa,
						idCentro,
						idUnidade,
						idAluno,
						idOrientador,
						modalidade,
						idCurso,
						sexo,
						status,
						idCota,
						somenteAtivos);
			req.setAttribute("lista", lista);

		} else {
			addMensagens(erros.getMensagens(), req);
		}

		if (formatoRelatorio) {
			return mapping.findForward("formatoImpressao");
		} else {
			return mapping.findForward("relatorio");
		}
	}
	
	/**
	 * Inativa o registro do bolsista no banco.
	 * 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>sigaa.war/WEB-INF/jsp/pesquisa/MembroProjetoDiscente/relatorio.jsp</li>
	 * </ul>
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward inativar(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {

		MembroProjetoDiscenteForm membroForm = (MembroProjetoDiscenteForm) form;

		int idMembroDiscente = getParameterInt(req, "idMembroDiscente");
		if (idMembroDiscente <= 0) {
			if( membroForm.getObj().getId() <= 0 ){
				addMensagemErro("É necessário selecionar um discente", req);
				req.setAttribute("popular", true);
				return relatorio(mapping, form, req, res);
			} else
				idMembroDiscente = membroForm.getObj().getId();
		}

		prepareMovimento(SigaaListaComando.INATIVAR_BOLSISTA, req);
		
		GenericDAO dao = getGenericDAO(req);
		MembroProjetoDiscente membroDiscente = dao.findByPrimaryKey(idMembroDiscente, MembroProjetoDiscente.class);

		MovimentoIndicarBolsista mov = new MovimentoIndicarBolsista();
		mov.setBolsistaAnterior(membroDiscente);
		mov.setCodMovimento(SigaaListaComando.INATIVAR_BOLSISTA);

		try {
			execute(mov, req);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens().getMensagens(), req);
			req.setAttribute("popular", true);
			return relatorio(mapping, form, req, res);
		}

		addMessage(req, "Remoção realizada com sucesso!", TipoMensagemUFRN.INFORMATION);

		// Limpar formulário, caso ele esteja em sessão
		req.getSession(false).removeAttribute(mapping.getName());

		return relatorio(mapping, membroForm, req, res);
	}

	/**
	 * Decide pra qual tela voltar da view de consulta de dados do bolsista de acordo com o perfil do usuário.
	 * 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>sigaa.war/WEB-INF/jsp/pesquisa/MembroProjetoDiscente/relatorio.jsp</li>
	 * </ul>
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward voltar(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (isUserInRole(SigaaPapeis.GESTOR_PESQUISA, req) && getSubSistemaCorrente(req).equals(SigaaSubsistemas.PESQUISA)) {
			return mapping.findForward("relatorio");
		} else {
			return mapping.findForward("lista_orientador");
		}
	}
}
