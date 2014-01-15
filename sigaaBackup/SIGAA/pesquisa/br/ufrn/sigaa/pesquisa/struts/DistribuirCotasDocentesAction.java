/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 19/07/2007
 *
 */
package br.ufrn.sigaa.pesquisa.struts;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.sigaa.arq.dao.pesquisa.CotaDocenteDao;
import br.ufrn.sigaa.arq.dao.pesquisa.EditalPesquisaDao;
import br.ufrn.sigaa.arq.dao.prodocente.ClassificacaoRelatorioDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.parametros.dominio.ParametrosPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.CotaDocente;
import br.ufrn.sigaa.pesquisa.dominio.Cotas;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.SiglaUnidadePesquisa;
import br.ufrn.sigaa.pesquisa.form.CotaDocenteForm;
import br.ufrn.sigaa.pesquisa.negocio.CalculosPesquisa;
import br.ufrn.sigaa.pesquisa.negocio.MovimentoDistribuicaoCotasDocentes;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ClassificacaoRelatorio;

/**
 * Action respons�vel pela distribui��o de cotas aos docentes
 *
 * @author Ricardo Wendell
 * @author Leonardo Campos
 */
public class DistribuirCotasDocentesAction extends AbstractCrudAction {

	private static final String VIEW_SELECAO_EDITAL = "selecaoEdital";
	private static final String VIEW_FORM_AJUSTES = "formAjustes";
	private static final String VIEW_RESUMO = "resumo";
	
	// Constantes das opera��es
	private static final String GERAR_DISTRIBUICAO = "1";
	private static final String AJUSTAR_DISTRIBUICAO = "2";

	/**
	 * Iniciar opera��es de distribui��o/ajuste de cotas
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	private ActionForward iniciar(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(SigaaPapeis.ADMINISTRADOR_PESQUISA, req); 
		
		CotaDocenteForm cotaDocenteForm = (CotaDocenteForm) form;

		EditalPesquisaDao editalDao = getDAO(EditalPesquisaDao.class, req);
		Collection<EditalPesquisa> editais = editalDao.findAll(EditalPesquisa.class, "edital.fimSubmissao", "desc");

		if ( editais != null && !editais.isEmpty() ) {
			cotaDocenteForm.getReferenceData().put("editais", editais);
			return mapping.findForward(VIEW_SELECAO_EDITAL);
		} else {
			addMensagemErro("N�o foram encontrados editais de distribui��o de cotas cadastrados no sistema", req);
			return getMappingSubSistema(req, mapping);
		}
	}

	/**
	 * Preparar formul�rio para distribui��o autom�tica de cotas
	 *	Chamado nas JSPs: 
	 *     /WEB-INF/jsp/pesquisa/menu/iniciacao.jsp
	 *     /WEB-INF/jsp/pesquisa/CotaDocente/resumo.jsp
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward iniciarDistribuicao(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		CotaDocenteForm cotaDocenteForm = (CotaDocenteForm) form;
		cotaDocenteForm.setGeracao(true);
		setOperacaoAtiva(req, GERAR_DISTRIBUICAO, null);
		// Buscar classifica��es de relat�rio de pesquisa gerados
		ClassificacaoRelatorioDao classificacaoDao = getDAO(ClassificacaoRelatorioDao.class, req);
		Collection<ClassificacaoRelatorio> classificacoes = classificacaoDao.findClassificacoesRelatorioPesquisa();

		if (classificacoes != null && !classificacoes.isEmpty()) {
			cotaDocenteForm.getReferenceData().put("classificacoes", classificacoes);
		} else {
			addMensagemErro("N�o foram encontrados relatorios de ranking de docentes", req);
			return getMappingSubSistema(req, mapping);
		}

		prepareMovimento(SigaaListaComando.DISTRIBUIR_COTAS_PESQUISA, req);
		return iniciar(mapping, form, req, res);
	}

	/**
	 * Preparar form para ajuste de uma distribui��o
	 *	Chamado na JSP: /WEB-INF/jsp/pesquisa/menu/iniciacao.jsp
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward iniciarAjustes(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		CotaDocenteForm cotaDocenteForm = (CotaDocenteForm) form;
		cotaDocenteForm.setGeracao(false);
		setOperacaoAtiva(req, AJUSTAR_DISTRIBUICAO, null);
		// Buscar centros
		Map<String, Object> referenceData = cotaDocenteForm.getReferenceData();
		referenceData.put( "centros", getGenericDAO(req).findAll(SiglaUnidadePesquisa.class, "unidade.nome", "asc"));

		return iniciar(mapping, form, req, res);
	}

	/**
	 * Buscar edital selecionado e redirecionar para a opera��o de distribui��o
	 * ou ajuste de cotas
	 *	Chamado na JSP: /WEB-INF/jsp/pesquisa/CotaDocente/selecao_edital.jsp
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward selecionarEdital(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
	    if(!checkOperacaoAtiva(req, res, GERAR_DISTRIBUICAO) && !checkOperacaoAtiva(req, res, AJUSTAR_DISTRIBUICAO) ) {
	        addMensagemErro("A opera��o n�o est� mais ativa. Por favor reinicie a opera��o.", req);
            if(flushErros(req))
                return mapping.findForward(getSubSistemaCorrente(req).getForward());
	    }
	     
		CotaDocenteForm cotaDocenteForm = (CotaDocenteForm) form;

		// Buscar dados do edital selecionado
		EditalPesquisaDao editalDao = getDAO(EditalPesquisaDao.class, req);
		EditalPesquisa edital = editalDao.findByPrimaryKey(cotaDocenteForm.getObj().getEdital().getId(), EditalPesquisa.class);

		if ( edital == null ) {
			addMensagemErro("O edital selecionado n�o foi encontrado", req);
			return getMappingSubSistema(req, mapping);
		} else {
			edital.getCota().getDescricao();
			cotaDocenteForm.getObj().setEdital(edital);
		}

		// Gerar distribui��o ou buscar uma distribui��o realizada para realizar ajustes
		if ( cotaDocenteForm.isGeracao() ) {
			return gerarDistribuicao(mapping, form, req, res);
		} else {
			return buscarDistribuicao(mapping, form, req, res);
		}
	}

	/**
	 * Gerar distribui��o de cotas a partir dos crit�rios definidos no edital, do FPPI dos docentes
	 * e da m�dias de notas dos projetos
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	private ActionForward gerarDistribuicao(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		CotaDocenteForm cotaDocenteForm = (CotaDocenteForm) form;

		
		
		/*// Buscar classifica��o selecionada
		ClassificacaoRelatorio classificacao = cotaDocenteForm.getClassificacao();
		classificacao = getGenericDAO(req).findByPrimaryKey(classificacao.getId(), ClassificacaoRelatorio.class);

		if ( classificacao == null ) {
			addMensagemErro("O relat�rio de ranking de docentes selecionado n�o foi encontrado", req);
			return iniciarDistribuicao(mapping, form, req, res);
		}

		// Cole��o ordenada de cotas de docentes
        TreeSet<CotaDocente> cotasDocentes = new TreeSet<CotaDocente>();

        // Objetos auxiliares
        EditalPesquisa edital = cotaDocenteForm.getObj().getEdital();

        // DAOs
        CotaDocenteDao cotaDao = getDAO(CotaDocenteDao.class, req);
        ProjetoPesquisaDao projetoDao = getDAO(ProjetoPesquisaDao.class, req);
        PlanoTrabalhoDao planoDao = getDAO(PlanoTrabalhoDao.class, req);

        // Reabrir sess�es
        edital = cotaDao.refresh(edital);

        // Buscar FPPIs dos docentes desta classifica��o
        Collection<EmissaoRelatorio> emissoes = classificacao.getEmissaoRelatorioCollection();
        Map<Integer, Double> notasProjeto = projetoDao.findNotaProjetosByAnoEdital( edital );
        Set<Integer> solicitacoesCota = planoDao.findSolicitacoesByEdital(edital);
        
        if ( solicitacoesCota == null || solicitacoesCota.isEmpty() ) {
            addMensagemErro("N�o h� solicita��es de cotas para o edital selecionado.", req);
            return iniciarDistribuicao(mapping, form, req, res);
        }
        
//        NormalDistribution normal = new NormalDistributionImpl(0, 1);
        
        // Percorrer emiss�es e popular as cotas com os FPPIs e m�dias de projetos
        System.out.println( "emissoes.. " + emissoes.size() );
        System.out.println( "cotas.. " + edital.getCotas().size() );
        for ( EmissaoRelatorio emissao : emissoes ) {
            Servidor docente = emissao.getServidor();
            if(!solicitacoesCota.contains(docente.getId()))
                continue;

            Double nota = notasProjeto.get(docente.getId());
            nota = nota != null ? nota : 0.0;
//            Double fppi = 0.0;
//            try {
//                fppi = (10.0 * normal.cumulativeProbability(emissao.getFppi()));
//            } catch (MathException e) {
//                e.printStackTrace();
//                System.out.println("Erro ao calcular a probabilidade!");
//            }  

            CotaDocente cotaDocente = new CotaDocente();
            cotaDocente.setEmissaoRelatorio(emissao);
            cotaDocente.setEdital(edital);
            cotaDocente.setDocente(docente);
            cotaDocente.setFppi(emissao.getFppi());
            cotaDocente.setMediaProjetos(nota);
            for(Cotas c: edital.getCotas()){
                Cotas cNova = new Cotas();
                cNova.setTipoBolsa(c.getTipoBolsa());
                cNova.setQuantidade(0);
                cotaDocente.addCotas(cNova);
            }
            cotasDocentes.add(cotaDocente);
        }*/
        
        
        
        
		// Buscar classifica��o selecionada
		ClassificacaoRelatorio classificacao = cotaDocenteForm.getClassificacao();
		
		// Objetos auxiliares
        EditalPesquisa edital = cotaDocenteForm.getObj().getEdital();
        
        String implementacao = ParametroHelper.getInstance().getParametro(ParametrosPesquisa.IMPLEMENTACAO_COMPORTAMENTOS_PESQUISA);
        CalculosPesquisa calculos = ReflectionUtils.newInstance( implementacao );
        
        // Cole��o ordenada de cotas de docentes
        TreeSet<CotaDocente> cotasDocentes = new TreeSet<CotaDocente>();
        cotasDocentes = calculos.criarGradeDistribuicaoCotas(classificacao, edital);

        
        cotaDocenteForm.setCotas( cotasDocentes );
        cotaDocenteForm.getObj().getEdital().setCotasDocentes( cotaDocenteForm.getCotas() );

        return mapping.findForward(VIEW_RESUMO);
	}

	/**
	 * Chama o processador para persistir a distribui��o de cotas no banco.
	 * Chamado na JSP: /WEB-INF/jsp/pesquisa/CotaDocente/resumo.jsp
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward persistirDistribuicao(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
	    if(!checkOperacaoAtiva(req, res, GERAR_DISTRIBUICAO) ) {
            addMensagemErro("A opera��o n�o est� mais ativa. Por favor reinicie a opera��o.", req);
            if(flushErros(req))
                return mapping.findForward(getSubSistemaCorrente(req).getForward());
        }
	    CotaDocenteForm cotaDocenteForm = (CotaDocenteForm) form;
	    
	    CotaDocenteDao cotaDocenteDao = getDAO(CotaDocenteDao.class, req);
        if ( cotaDocenteDao.existsDistribuicao( cotaDocenteForm.getObj().getEdital() ) ) {
            addMensagemErro("J� foi gerada uma distribui��o de cotas para o edital selecionado: " +
                    " '" + cotaDocenteForm.getObj().getEdital().getDescricao() + "'. " +
                    "Caso necess�rio, utilize a opera��o de realizar ajustes para efetuar altera��es.", req);
            return iniciarDistribuicao(mapping, form, req, res);
        }
	    
	    // Preparar movimento
	    MovimentoDistribuicaoCotasDocentes movDistribuicao = new MovimentoDistribuicaoCotasDocentes();
	    movDistribuicao.setEdital( cotaDocenteForm.getObj().getEdital() );
	    movDistribuicao.setCotasDocentes(cotaDocenteForm.getCotas());
	    movDistribuicao.setCodMovimento(SigaaListaComando.DISTRIBUIR_COTAS_PESQUISA);
	    
	    try {
	        execute(movDistribuicao, req);
	    } catch (NegocioException e) {
	        addMensagens(e.getListaMensagens().getMensagens(), req);
	        return mapping.findForward(VIEW_RESUMO);
	    }
	    addInformation("Distribui��o gerada com sucesso!", req);
	    removeOperacaoAtiva(req);
	    return mapping.findForward(getSubSistemaCorrente(req).getForward());
	}
	
	/**
	 * Busca os dados da distribui��o de cotas e encaminha para a tela de ajustes.
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	private ActionForward buscarDistribuicao(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		CotaDocenteForm cotaDocenteForm = (CotaDocenteForm) form;

		CotaDocenteDao cotaDao = getDAO(CotaDocenteDao.class, req);

		// Buscar e ordenar as cotas
		Collection<CotaDocente> cotas = cotaDao.findByEditalPesquisa( cotaDocenteForm.getObj().getEdital(), cotaDocenteForm.getCentro().getId() );

		Comparator<CotaDocente> comparator = null;
		if (cotaDocenteForm.getOrdenacao() == null || cotaDocenteForm.getOrdenacao().equals("ifc")) {
			comparator = new Comparator<CotaDocente>() {
				public int compare(CotaDocente o1, CotaDocente o2) {
					int comparacao = new Double(o2.getIfc()).compareTo(o1.getIfc());

					if(comparacao == 0) {
						comparacao = new Double(o2.getMediaProjetos()).compareTo(o1.getMediaProjetos());
					}
					if(comparacao == 0) {
						comparacao = o1.getDocente().getNome().compareTo(o2.getDocente().getNome());
					}

					return comparacao ;
				}
			};
		} else {
			comparator = new Comparator<CotaDocente>() {
				public int compare(CotaDocente o1, CotaDocente o2) {
					return o1.getDocente().getNome().compareTo(o2.getDocente().getNome());
				}
			};
		}

		TreeSet<CotaDocente> cotasOrdenadas = new TreeSet<CotaDocente>(comparator);
		cotasOrdenadas.addAll(cotas);

		// Preparar HashMaps com as cotas dos docentes para altera��o
		for (CotaDocente cota : cotasOrdenadas) {
			for(Cotas c: cota.getCotas()){
				cotaDocenteForm.setCota(String.valueOf(c.getTipoBolsa().getId()) + String.valueOf(cota.getDocente().getId()), c.getQuantidade());
			}
		}

		if ( cotaDocenteForm.getCentro().getId() != -1 ) {
			cotaDocenteForm.setCentro( cotaDao.findByPrimaryKey(cotaDocenteForm.getCentro().getId(), Unidade.class) );
		}

		cotaDocenteForm.setCotas(cotasOrdenadas);

		prepareMovimento(SigaaListaComando.AJUSTAR_COTAS_PESQUISA, req);
		return mapping.findForward(VIEW_FORM_AJUSTES);
	}

	/**
	 * Persiste no banco os ajustes realizados na distribui��o de cotas.
	 * Chamado na JSP: /WEB-INF/jsp/pesquisa/CotaDocente/form_ajustes.jsp
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward gravarAjustes(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
	    if( !checkOperacaoAtiva(req, res, AJUSTAR_DISTRIBUICAO) ) {
            addMensagemErro("A opera��o n�o est� mais ativa. Por favor reinicie a opera��o.", req);
            if(flushErros(req))
                return mapping.findForward(getSubSistemaCorrente(req).getForward());
        }
	    CotaDocenteForm cotaDocenteForm = (CotaDocenteForm) form;

		// Preparar dados do formul�rio
		for (CotaDocente cota : cotaDocenteForm.getCotas()) {
			for(Cotas c: cota.getCotas()){
				String quant = (String) cotaDocenteForm.getCota( String.valueOf(c.getTipoBolsa().getId()) + String.valueOf(cota.getDocente().getId()) );
				quant = quant != null && !quant.trim().equals("") ? quant : "0";
				c.setQuantidade( Integer.parseInt( quant ) );
			}
		}

		// Preparar movimento
		MovimentoDistribuicaoCotasDocentes movDistribuicao = new MovimentoDistribuicaoCotasDocentes();
		movDistribuicao.setEdital( cotaDocenteForm.getObj().getEdital() );
		movDistribuicao.setCotasDocentes(cotaDocenteForm.getCotas());
		movDistribuicao.setCodMovimento(SigaaListaComando.AJUSTAR_COTAS_PESQUISA);

		try {
			execute(movDistribuicao, req);
			addInformation("Ajustes realizados com sucesso.", req);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens().getMensagens(), req);
			return mapping.findForward(VIEW_FORM_AJUSTES);
		}
		removeOperacaoAtiva(req);
		return resumo(mapping, form, req, res);
	}

	/**
	 * Adicionar um docente que ainda n�o est� na lista � distribui��o.
	 * Chamado na JSP: /WEB-INF/jsp/pesquisa/CotaDocente/form_ajustes.jsp
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward adicionarDocente(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
	    if(!checkOperacaoAtiva(req, res, GERAR_DISTRIBUICAO) && !checkOperacaoAtiva(req, res, AJUSTAR_DISTRIBUICAO) ) {
            addMensagemErro("A opera��o n�o est� mais ativa. Por favor reinicie a opera��o.", req);
            if(flushErros(req))
                return mapping.findForward(getSubSistemaCorrente(req).getForward());
        }
		CotaDocenteForm cotaDocenteForm = (CotaDocenteForm) form;
		CotaDocente cotaDocente = cotaDocenteForm.getObj();

		// Buscar dados do servidor informado
		GenericDAO dao = getGenericDAO(req);
		Servidor servidor = dao.findByPrimaryKey( cotaDocente.getDocente().getId() , Servidor.class);

		if (servidor == null) {
			addMensagemErro("Selecione um servidor para adicionar � esta distribui��o", req);
		} else {
			CotaDocente cota = new CotaDocente();

			cota.setDocente( servidor );
			cota.setEdital( cotaDocente.getEdital()  );

			// Preparar movimento
			MovimentoDistribuicaoCotasDocentes movDistribuicao = new MovimentoDistribuicaoCotasDocentes();
			movDistribuicao.setCotaDocente( cota );
			movDistribuicao.setCodMovimento(SigaaListaComando.ADICIONAR_COTA_DOCENTE);

			try {
				prepareMovimento(SigaaListaComando.ADICIONAR_COTA_DOCENTE, req);
				execute(movDistribuicao, req);

				cotaDocenteForm.getCotas().add( cota );

				for(Cotas c: cotaDocente.getEdital().getCotas()){
					cotaDocenteForm.setCota(String.valueOf(c.getTipoBolsa().getId()) + String.valueOf(cota.getDocente().getId()), 0);
				}
				
				addInformation("Docente adicionado com sucesso � lista", req);
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens().getMensagens(), req);
				return mapping.findForward(VIEW_FORM_AJUSTES);
			}

		}

		return mapping.findForward(VIEW_FORM_AJUSTES);
	}

	/**
	 * Buscar a distribui��o atual de um determinado edital
	 * N�o � chamado em JSP alguma, apenas por outros m�todos para encaminhar � tela
	 * de resumo da distribui��o. 
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward resumo(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		CotaDocenteForm cotaDocenteForm = (CotaDocenteForm) form;

		CotaDocenteDao cotaDao = getDAO(CotaDocenteDao.class, req);

		// Buscar e ordenar as cotas
		Collection<CotaDocente> cotas = cotaDao.findByEditalPesquisa( cotaDocenteForm.getObj().getEdital(), null);
		cotaDocenteForm.setCotas(new TreeSet<CotaDocente>(cotas));
		cotaDocenteForm.getObj().getEdital().setCotasDocentes( cotaDocenteForm.getCotas() );

		return mapping.findForward(VIEW_RESUMO);
	}

	/**
	 * Remove a opera��o ativa e sai do caso de uso.
	 * Chamado nas JSPs: 
	 *     /WEB-INF/jsp/pesquisa/CotaDocente/selecao_edital.jsp
	 *     /WEB-INF/jsp/pesquisa/CotaDocente/form_ajustes.jsp
	 *     /WEB-INF/jsp/pesquisa/CotaDocente/resumo.jsp
	 */
	@Override
	public ActionForward cancelar(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response)
	        throws Exception {
	    removeOperacaoAtiva(request);
	    return super.cancelar(mapping, form, request, response);
	}
	
	/**
	 * Verifica se a opera��o se encontra ativa ou n�o.
	 */
	@Override
	public boolean checkOperacaoAtiva(HttpServletRequest req,
	        HttpServletResponse res, String operacao) throws ServletException,
	        IOException {
	    boolean ativa = false;

        if (req.getSession().getAttribute("operacaoAtiva") != null) {
            if (req.getSession().getAttribute("operacaoAtiva").toString()
                    .equals(operacao))
                ativa = true;
        }
        return ativa;
	}
}
