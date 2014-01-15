/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/12/2006
 *
 */
package br.ufrn.sigaa.pesquisa.struts;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.pesquisa.MembroProjetoDiscenteDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.pesquisa.dominio.CotaBolsas;
import br.ufrn.sigaa.pesquisa.dominio.MembroProjetoDiscente;
import br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa;
import br.ufrn.sigaa.pesquisa.form.SubstituicoesBolsistasForm;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Action responsável por gerar relatório de substituições de bolsistas por orientador
 *
 * @author ricardo
 */
public class RelatorioSubstituicoesBolsistasAction extends AbstractCrudAction {

	/**
	 * Popula o formulário de busca do relatório.
	 * Chamado na JSP:
	 * /WEB-INF/jsp/pesquisa/menu/iniciacao.jsp
	 *
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
		SubstituicoesBolsistasForm subForm = (SubstituicoesBolsistasForm) form;

		GenericDAO dao = getGenericDAO(req);

		// Setar valores iniciais
//		subForm.setMininoSubstituicoes(2);
		ParametroHelper helper = ParametroHelper.getInstance();
		int dia = helper.getParametroInt(ConstantesParametro.LIMITE_ALTERACAO_BOLSISTA);

		Calendar c = Calendar.getInstance();
		c.setTime( new Date() );
		if ( c.get(Calendar.DAY_OF_MONTH) < dia ) {
			if (c.get(Calendar.MONTH) == Calendar.JANUARY) {
				c.roll(Calendar.YEAR, -1);
			}
			c.roll(Calendar.MONTH, -1);
		}
		c.set(Calendar.DAY_OF_MONTH, dia);
		c.add(Calendar.DATE, 1);

		subForm.setInicio(Formatador.getInstance().formatarData(c.getTime()));
		
		c.set(Calendar.DAY_OF_MONTH, dia);
		c.roll(Calendar.MONTH, 1);
		if (c.get(Calendar.MONTH) == Calendar.JANUARY) {
			c.roll(Calendar.YEAR, 1);
		}		
		subForm.setFim(Formatador.getInstance().formatarData(c.getTime()));

		popularDadosAuxiliares(req, dao);

		req.setAttribute(mapping.getName(), subForm);
		return mapping.findForward("form");
	}


	/**
	 * Método auxiliar utilizado para popular alguns dados utilizados na emissão do relatório.
	 * 
	 * @param req
	 * @param dao
	 * @throws DAOException
	 */
	private void popularDadosAuxiliares(HttpServletRequest req, GenericDAO dao) throws DAOException {
		req.setAttribute("tiposBolsa", TipoBolsaPesquisa.getTipos());
		req.setAttribute("cotas", dao.findAll(CotaBolsas.class, "descricao", "desc"));
	}


	/**
	 * Busca as informações do relatório e encaminha para a visualização.
	 * /WEB-INF/jsp/pesquisa/PlanoTrabalho/form_substituicoes.jsp
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward gerar(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {

		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);
		SubstituicoesBolsistasForm subForm = (SubstituicoesBolsistasForm) form;

		MembroProjetoDiscenteDao membroDao = getDAO(MembroProjetoDiscenteDao.class, req);

		// Validar dados informados
		subForm.validate(req);
		if (flushErros(req)) {
			return popular(mapping, form, req, res);
		}

		// Preparar dados e realizar a consulta
		ListaMensagens erros = new ListaMensagens();
		Integer tipoBolsa = null;
		Servidor orientador = null;
		Boolean ativos = null;

		for(int filtro : subForm.getFiltros() ){
			switch(filtro) {
			case SubstituicoesBolsistasForm.FILTRO_TIPO_BOLSA:
				tipoBolsa = subForm.getTipoBolsa();
				break;
			case SubstituicoesBolsistasForm.FILTRO_DOCENTE:
				int idOrientador = subForm.getOrientador().getId();
				ValidatorUtil.validateRequiredId(idOrientador, "Orientador", erros);
				orientador = membroDao.findByPrimaryKey(idOrientador, Servidor.class);
				break;
			case SubstituicoesBolsistasForm.FILTRO_ATIVOS_INATIVOS:
				ativos = subForm.getAtivos();
				break;
			}
		}

		if (!erros.isEmpty()) {
			addMensagens(erros.getMensagens(), req);
			popularDadosAuxiliares(req, getGenericDAO(req));
			return mapping.findForward("form");
		}

		Date inicio = Formatador.getInstance().parseDate(subForm.getInicio());
		Date fim = Formatador.getInstance().parseDate(subForm.getFim());

		Collection<MembroProjetoDiscente> relatorio = membroDao.findSubstituicoes(
				inicio, fim,
				subForm.getCota(),
				tipoBolsa,
				orientador,
				ativos,
				subForm.getOrdenacao());

		// Se não forem encontrados resultados, redirecionar para o formulário
		if (relatorio == null || relatorio.isEmpty()) {
			req.setAttribute(mapping.getName(), subForm);
			addMensagemErro("Nenhum resultado encontrado para os critérios informados", req);
			popularDadosAuxiliares(req, membroDao);
			return mapping.findForward("form");
		}

		req.setAttribute("relatorio", relatorio);
		return mapping.findForward("relatorio");
	}

}
