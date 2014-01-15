/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 06/10/2006
 *
 */
package br.ufrn.sigaa.pesquisa.struts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoPesquisaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.SiglaUnidadePesquisa;
import br.ufrn.sigaa.pesquisa.form.AnalisarAvaliacoesForm;
import br.ufrn.sigaa.pesquisa.negocio.AnalisadorAvaliacoesProjeto;
import br.ufrn.sigaa.pesquisa.negocio.AnalisarAvaliacoesValidator;
import br.ufrn.sigaa.pesquisa.negocio.MovimentoAnalisarAvaliacoes;

/**
 * Action responsável pela análise das avaliações dos projetos de pesquisa
 * realizadas pelos consultores externos.
 * 
 * @author Victor Hugo
 *
 */
public class AnalisarAvaliacoesAction extends AbstractCrudAction {

	/** FORM FORWARDS */
	public static final String	LISTAR			= "listar";
	public static final String	RELATORIO		= "relatorio";
	public static final String	ANALISAR		= "analisar";
	public static final String	RESUMO			= "resumo";


	/**
	 * Método responsável pela listagem das avaliações.
	 * JSPs:
	 *     /WEB-INF/jsp/pesquisa/menu/projetos.jsp 
	 *     /WEB-INF/jsp/pesquisa/AnalisarAvaliacoes/lista.jsp
	 */
	@SuppressWarnings("unchecked")
	public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);

		AnalisarAvaliacoesForm analiseForm = (AnalisarAvaliacoesForm) form;
		ProjetoPesquisaDao daoProjeto = getDAO( ProjetoPesquisaDao.class, req );

		boolean relatorio = false;
		
		if ( req.getParameter("popular") != null ) {
			clearForm(req, mapping.getName());
			Map<String, Object> referenceData = analiseForm.getReferenceData();
			referenceData.put( "centros", daoProjeto.findAll(SiglaUnidadePesquisa.class, "unidade.nome", "asc"));
			referenceData.put( "editaisPesquisa", daoProjeto.findAll(EditalPesquisa.class, "edital.dataCadastro", "desc"));
		} else {

			EditalPesquisa edital = null;
			Unidade unidadeAcademica = null;
			Integer minimoAvaliacoes = null;

			// Definição dos filtros e validações
			ListaMensagens erros = new ListaMensagens();
			for(int filtro : analiseForm.getFiltros() ){
				switch(filtro) {
					case AnalisarAvaliacoesForm.EDITAL:
						edital = analiseForm.getEdital();
						break;
					case AnalisarAvaliacoesForm.UNIDADE_ACADEMICA:
						unidadeAcademica = daoProjeto.findByPrimaryKey(analiseForm.getUnidadeAcademica().getId(), Unidade.class);
						break;
					case AnalisarAvaliacoesForm.MINIMO_AVALIACOES:
						minimoAvaliacoes = analiseForm.getMinimoAvaliacoes();
						ValidatorUtil.validateMinValue(analiseForm.getMinimoAvaliacoes(), 0, "Quantidade mínima de avaliações", erros);
						break;
					case AnalisarAvaliacoesForm.FORMATO_RELATORIO:
						relatorio = true;
						break;
				}
			}
			Collection<ProjetoPesquisa> projetos = new ArrayList<ProjetoPesquisa>();
			if (erros.isEmpty()) {
				// Buscar projetos
				projetos = daoProjeto.findParaAnalise(unidadeAcademica, minimoAvaliacoes, edital);
			} else {
				addMensagens(erros.getMensagens(), req);
			}

			// Analisar avaliações e definir sugestão
			analiseForm.setAnalise( AnalisadorAvaliacoesProjeto.sugerirAvaliacao(projetos) );

			analiseForm.setFiltros(new int[] {});
		}

		if (!relatorio) {
			prepareMovimento(SigaaListaComando.ANALISAR_AVALIACOES, req);
			return mapping.findForward(LISTAR);
		} else {
			return mapping.findForward(RELATORIO);
		}
	}

	/**
	 * Verificar análises realizadas e atualizar os projetos
	 * JSP: /WEB-INF/jsp/pesquisa/AnalisarAvaliacoes/lista.jsp
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public ActionForward analisar(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);

		AnalisarAvaliacoesForm analiseForm = (AnalisarAvaliacoesForm) form;
		AnalisarAvaliacoesValidator.validaAnalises( analiseForm.getAnalise(), newListMensagens(req) );

		if (flushErros(req)) {
			return mapping.findForward(LISTAR);
		}

		// Extrair resultado da análise
		Set<?> keys = req.getParameterMap().keySet();
		Collection<String> resultados = new ArrayList<String>();
		ProjetoPesquisaDao daoProjeto = getDAO( ProjetoPesquisaDao.class, req );

		for( Object o : keys ){
			if( o.toString().startsWith("analise_") )
				resultados.add( o.toString() );
		}

		analiseForm.getProjetosAprovados( ).clear();
		analiseForm.getProjetosReprovados( ).clear();
		analiseForm.getProjetosIndefinidos( ).clear();
		for( String parametro : resultados ){

			int id = Integer.parseInt( parametro.substring(8) ); //posição da string onde começa o código do projeto analise_#codigo (# = 8ª posiçao)

			String decisao = req.getParameter( parametro );
			ProjetoPesquisa projeto = daoProjeto.findParaAnaliseById( id );

			if( decisao.equalsIgnoreCase( "true" ) ){
				projeto.setAprovado( true );
				analiseForm.getProjetosAprovados().add( projeto );
			}
			else if( decisao.equalsIgnoreCase( "false" ) ){
				projeto.setAprovado( false );
				analiseForm.getProjetosReprovados().add( projeto );
			}
			else{
				projeto.setAprovado( null );
				analiseForm.getProjetosIndefinidos().add( projeto );
			}


		}

		return mapping.findForward(RESUMO);
	}

	/**
	 * Finaliza a operação chamando o processador.
	 * JSP: /WEB-INF/jsp/pesquisa/AnalisarAvaliacoes/resumo.jsp
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward finalizar(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);

		AnalisarAvaliacoesForm analiseForm = (AnalisarAvaliacoesForm) form;
		Collection<ProjetoPesquisa> projetos = new ArrayList<ProjetoPesquisa>();
		projetos.addAll( analiseForm.getProjetosAprovados() );
		projetos.addAll( analiseForm.getProjetosReprovados() );

		MovimentoAnalisarAvaliacoes mov = new MovimentoAnalisarAvaliacoes();
		mov.setProjetos( projetos );
		mov.setCodMovimento( SigaaListaComando.ANALISAR_AVALIACOES );

		try {
			execute(mov, req);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens().getMensagens(), req);
			return mapping.findForward(RESUMO);
		}

		addInformation("Análise de projetos realizada com sucesso!", req);
		return cancel(mapping, form, req, res);

	}

	/**
	 * Cancela a ação e sai do caso de uso
	 * JSPs:
	 *     /WEB-INF/jsp/pesquisa/AnalisarAvaliacoes/lista.jsp
	 *     /WEB-INF/jsp/pesquisa/AnalisarAvaliacoes/resumo.jsp
	 *
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward cancelar(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		removeSession("formAnalisarAvaliacoes", request);

		return super.cancelar(mapping, form, request, response);

	}

}
