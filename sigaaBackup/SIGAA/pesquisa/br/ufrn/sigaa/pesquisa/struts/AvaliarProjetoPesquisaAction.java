/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 05/10/2006
 *
 */
package br.ufrn.sigaa.pesquisa.struts;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.pesquisa.AvaliacaoProjetoDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ItemAvaliacaoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.AbstractWizardAction;
import br.ufrn.sigaa.arq.struts.ConstantesCadastro;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pesquisa.dominio.AvaliacaoProjeto;
import br.ufrn.sigaa.pesquisa.dominio.Consultor;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.ItemAvaliacao;
import br.ufrn.sigaa.pesquisa.dominio.NotaItem;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.SiglaUnidadePesquisa;
import br.ufrn.sigaa.pesquisa.form.AvaliacaoProjetoForm;
import br.ufrn.sigaa.pesquisa.form.ProjetoPesquisaForm;
import br.ufrn.sigaa.pesquisa.form.TelaCronograma;
import br.ufrn.sigaa.pesquisa.negocio.MovimentoEncerrarAvaliacoesPendentes;

/**
 * Action respons�vel pelo fluxo de avaliar um projeto de pesquisa
 *
 * @author Ricardo Wendell
 *
 */
public class AvaliarProjetoPesquisaAction extends AbstractWizardAction {

	/** FORM FORWARDS */
	public static final String	AVALIACAO		= "avaliacao";
	public static final String	LISTA_AVALIACAO	= "listaAvaliacao"; // Lista das avalia��es para o consultor
	public static final String	RESUMO			= "resumo";
	public static final String	LISTA_PENDENTES = "listaPendentes";

	/**
	 * M�todo respons�vel pela listagem das avalia��es dos projetos de pesquisa
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward listaAvaliacao(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		AvaliacaoProjetoDao avaliacaoDao = getDAO(AvaliacaoProjetoDao.class, req);
		AvaliacaoProjetoForm avaliacaoForm = (AvaliacaoProjetoForm) form;

		int idConsultor = 0;
		if (req.getParameter("idConsultor") == null) {
			idConsultor = avaliacaoForm.getObj().getConsultor().getId();
		} else {
			idConsultor = Integer.parseInt(req.getParameter("idConsultor"));
		}

		Collection<AvaliacaoProjeto> lista = avaliacaoDao.findByConsultor(idConsultor, false);
		req.setAttribute(ConstantesCadastro.ATRIBUTO_LISTAGEM, lista);
		return mapping.findForward(LISTA_AVALIACAO);
	}

	/**
	 * M�todo respons�vel pela listagem dos projetos de pesquisa pendentes
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward listarPendentes(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		AvaliacaoProjetoForm avaliacaoForm = (AvaliacaoProjetoForm) form;
		AvaliacaoProjetoDao dao = getDAO(AvaliacaoProjetoDao.class, req);
		
		Collection<AvaliacaoProjeto> lista = dao.findBySituacao(AvaliacaoProjeto.AGUARDANDO_AVALIACAO);
		if ( lista.isEmpty() ) {
			addMensagem(new MensagemAviso("N�o foram encontradas avalia��es pendentes.", TipoMensagemUFRN.WARNING), req);
			return getMappingSubSistema(req, mapping);
		} else {
			req.setAttribute("lista", lista);
			avaliacaoForm.setAvaliacoesPendentes(lista);
			prepareMovimento(SigaaListaComando.ENCERRAR_AVALIACOES_PENDENTES, req);
		}
		
		return mapping.findForward(LISTA_PENDENTES);
	}
	
	/**
	 * M�todo respons�vel pelo encerramento das avalia��es
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward encerrarAvaliacoes(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		AvaliacaoProjetoForm avaliacaoForm = (AvaliacaoProjetoForm) form;
		
		if(avaliacaoForm.getAvaliacoesPendentes() != null && !avaliacaoForm.getAvaliacoesPendentes().isEmpty()){
		
			MovimentoEncerrarAvaliacoesPendentes mov = new MovimentoEncerrarAvaliacoesPendentes();
			mov.setCodMovimento(SigaaListaComando.ENCERRAR_AVALIACOES_PENDENTES);
			mov.setAvaliacoesPendentes(avaliacaoForm.getAvaliacoesPendentes());
			
			try {
				execute(mov, request);
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens().getMensagens(), request);
				return getMappingSubSistema(request, mapping);
			}
	
			addInformation(avaliacaoForm.getAvaliacoesPendentes().size() + " avalia��es encerradas com sucesso!", request);
			clearSession(request);
			removeSession(mapping.getName(), request);
			return getMappingSubSistema(request, mapping);
		}
		addMensagemErro("N�o h� avalia��es pendentes para serem encerradas.", request);
		return mapping.findForward(LISTA_PENDENTES);
	}
	
	/**
	 * M�todo respons�vel pela listagem dos projetos de pesquisa
	 */
	@Override
	public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);
		AvaliacaoProjetoForm avaliacaoForm = (AvaliacaoProjetoForm) form;
		AvaliacaoProjetoDao dao = getDAO(AvaliacaoProjetoDao.class, req);
		AvaliacaoProjeto avaliacao = avaliacaoForm.getObj();

		// Popular dados do formul�rio de busca
		Map<String, Object> reference = avaliacaoForm.getReferenceData();
		reference.put("status", AvaliacaoProjeto.getTiposSituacao() );
		reference.put("tipos", AvaliacaoProjeto.getTiposDistribuicao() );
		reference.put("centros", dao.findAll(SiglaUnidadePesquisa.class, "unidade.nome", "asc"));
		reference.put("editaisPesquisa", dao.findAll(EditalPesquisa.class, "edital.dataCadastro", "desc"));

		// Preparar dados para exibi��o
		if (req.getParameter("popular") != null || req.getAttribute("popular") != null) {
			clearForm(req, mapping.getName());
			return mapping.findForward(LISTAR);
		}

		// Analisando filtros selecionados
		Consultor consultor = null;
		Unidade centro = null;
		Integer tipoDistribuicao = null;
		Integer status = null;
		EditalPesquisa edital = null;

		if (avaliacaoForm.getFiltros() != null) {
			for(int filtro : avaliacaoForm.getFiltros() ){
				switch(filtro) {
					case AvaliacaoProjetoForm.CONSULTOR:
						consultor = avaliacao.getConsultor();
						break;
					case AvaliacaoProjetoForm.CENTRO:
						centro = avaliacao.getProjetoPesquisa().getCentro();
						break;
					case AvaliacaoProjetoForm.TIPO_DISTRIBUICAO:
						tipoDistribuicao = avaliacao.getTipoDistribuicao();
						break;
					case AvaliacaoProjetoForm.SITUACAO:
						status = avaliacao.getSituacao();
						break;
					case AvaliacaoProjetoForm.EDITAL:
						edital = avaliacao.getProjetoPesquisa().getEdital();
						break;
				}
			}
		}

		Collection<AvaliacaoProjeto> lista = dao.filter( avaliacaoForm.getAno(), status, consultor, centro, tipoDistribuicao, edital);
		if ( lista.isEmpty() ) {
			addMensagem(new MensagemAviso("N�o foram encontradas avalia��es de acordo os crit�rios de busca informados", TipoMensagemUFRN.WARNING), req);
		} else {
			req.setAttribute("lista", lista);
		}

		return mapping.findForward(LISTAR);
	}

	/**
	 * Popular dados para avaliar o projeto
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward popular(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		AvaliacaoProjetoForm avaliacaoForm = (AvaliacaoProjetoForm) form;
		AvaliacaoProjeto avaliacao = avaliacaoForm.getObj();

		ItemAvaliacaoDao dao = getDAO(ItemAvaliacaoDao.class, req);
		// Avalia��o pr�-cadastrada
		if ( req.getParameter("idProjeto") == null ) {
			avaliacao = dao.findByPrimaryKey(avaliacaoForm.getObj().getId(), AvaliacaoProjeto.class);
			avaliacao.getConsultor().getNome(); // Inicializar dados do consultor
		}

		// Montar formul�rio de avalia��o
		if ( avaliacao.getNotasItens().isEmpty() ) {
			Collection<ItemAvaliacao> itens = dao.findByTipo(ItemAvaliacao.AVALIACAO_PROJETO);

			for (ItemAvaliacao item: itens) {
				NotaItem notaItem = new NotaItem();
				notaItem.setItemAvaliacao(item);
				notaItem.setAvaliacaoProjeto(avaliacao);
				notaItem.setNota(0.0);

				avaliacao.getNotasItens().add(notaItem);
			}
		}

		// Ordenar itens
		List<NotaItem> notas = toArrayList( avaliacao.getNotasItens() );
		Collections.sort(notas);
		avaliacao.setNotasItens(notas);

		avaliacaoForm.setObj(avaliacao);
		avaliacaoForm.referenceData(req);

		// Popular cronograma
		ProjetoPesquisa projeto = dao.refresh(avaliacao.getProjetoPesquisa());
		projeto.getCronogramas().iterator();
		projeto.getPlanosTrabalho().iterator();

		ProjetoPesquisaForm projetoForm = new ProjetoPesquisaForm();
		projetoForm.setObj(projeto);

		TelaCronograma cronograma = new TelaCronograma(
				projeto.getDataInicio(),
				projeto.getDataFim(),
				projeto.getCronogramas()
			);
		projetoForm.setTelaCronograma(cronograma);
		req.getSession().setAttribute("projetoPesquisaForm", projetoForm);


		// Preparar movimento
		avaliacaoForm.getObj().setCodMovimento(SigaaListaComando.AVALIAR_PROJETO_PESQUISA);
		prepareMovimento(SigaaListaComando.AVALIAR_PROJETO_PESQUISA, req);

		return mapping.findForward(AVALIACAO);
	}

	/**
	 * Popular avalia��o a partir de um projeto.
	 * Utilizado para os casos de consultoria especial
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward popularProjeto(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		AvaliacaoProjetoForm avaliacaoForm = (AvaliacaoProjetoForm) form;
		GenericDAO dao = getGenericDAO(req);

		// Buscar projeto selecionado
		int idProjeto = getParameterInt(req, "idProjeto");
		ProjetoPesquisa projeto = dao.findByPrimaryKey(idProjeto, ProjetoPesquisa.class);
		if (projeto == null) {
			addMensagemErro(" O projeto selecionado n�o � v�lido ", req);
			return listaAvaliacao(mapping, form, req, res);
		}

		//Inicializar tela do cronograma
		ProjetoPesquisaForm projetoForm = new ProjetoPesquisaForm();
		projetoForm.setObj(projeto);
		TelaCronograma cronograma = new TelaCronograma(
				projetoForm.getObj().getDataInicio(),
				projetoForm.getObj().getDataFim(),
				projetoForm.getObj().getCronogramas()
			);
		projetoForm.setTelaCronograma(cronograma);
		req.getSession(false).setAttribute("projetoPesquisaForm", projetoForm);

		// Criar uma nova avalia��o
		AvaliacaoProjeto avaliacao = new AvaliacaoProjeto();
		avaliacao.setProjetoPesquisa( projeto );
		avaliacao.setConsultor( ((Usuario)getUsuarioLogado(req)).getConsultor() );
		avaliacao.setDataDistribuicao( new Date() );

		avaliacaoForm.setObj( avaliacao );
		return popular(mapping, form, req, res);
	}
	
	
	/**
	 * Popular avalia��o ap�s retornar da avalia��o de um plano de trabalho.
	 * Utilizado quando o consultor est� na avalia��o de projetos, vai para a 
	 * avalia��o de um plano de trabalho vinculado ao projeto e precisa voltar
	 * direto para a avalia��o do mesmo projeto do in�cio.
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward popularVoltaProjeto(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		AvaliacaoProjetoForm avaliacaoForm = (AvaliacaoProjetoForm) form;
		int idAvaliacaoProjeto = getParameterInt(req, "fromAvaliacaoProjeto");
		avaliacaoForm.getObj().setId(idAvaliacaoProjeto);
		return popular(mapping, form, req, res);
	}

	/**
	 * Recuperar os dados da avalia��o e preparar para a confirma��o
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward avaliar(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		AvaliacaoProjetoForm avaliacaoForm = (AvaliacaoProjetoForm) form;
		AvaliacaoProjeto avaliacao = avaliacaoForm.getObj();

		// Lista de erros
		ListaMensagens lista = new ListaMensagens();

		// Popular itens da cole��o de notas
		Iterator<NotaItem> it = avaliacao.getNotasItens().iterator();
		for (int i = 0; i < avaliacaoForm.getNotas().length; i++) {
			NotaItem item = it.next();
			Double nota = 0.0;
			try {
				nota = Formatador.getInstance().parseValor(avaliacaoForm.getNotas()[i]);
			} catch (NumberFormatException e) {
			}

			ValidatorUtil.validateRange(nota, 0.0 , 10.0, item.getItemAvaliacao().getDescricao(), lista );

			avaliacaoForm.getNotas()[i] = Formatador.getInstance().formatarDecimal1(nota);
			item.setNota( nota );
		}

		if (avaliacao.hasPlanoPendenteAvaliacao()) {
			lista.addErro("� necess�rio avaliar todos os planos de trabalho do projeto de pesquisa para prosseguir com a avalia��o do projeto.");
		}

		// Calcular a m�dia
		avaliacao.calcularMedia();
		
		// Verificar erros
		if (lista.isErrorPresent()) {
			addMensagens(lista.getMensagens(), req);
			return mapping.findForward(AVALIACAO);
		} else {
			return mapping.findForward(RESUMO);
		}

	}

	/**
	 * M�todo respons�vel pela finaliza��o de um projeto de pesquisa
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward finalizar(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {

		AvaliacaoProjetoForm avaliacaoForm = (AvaliacaoProjetoForm) form;
		AvaliacaoProjeto avaliacao = avaliacaoForm.getObj();

		if (avaliacaoForm.getObj().getObservacoes().equals("") || avaliacao.getObservacoes().equals("")) {
			addMensagemErro("Parecer: Campo Obrigat�rio n�o informado.", req);
			return avaliar(mapping, avaliacaoForm, req, res);
		}

		
		try {
			getGenericDAO(req).detach(avaliacao);
			execute(avaliacao, req);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens().getMensagens(), req);
			return mapping.findForward(RESUMO);
		}

		addInformation("Avalia��o do projeto " + avaliacao.getProjetoPesquisa().getCodigo() + " realizada com sucesso!", req);

		if (getSubSistemaCorrente(req).equals(SigaaSubsistemas.PORTAL_CONSULTOR)) {
			return mapping.findForward(SigaaSubsistemas.PORTAL_CONSULTOR.getForward());
		} else {
			return listaAvaliacao(mapping, form, req, res);
		}

	}

	/**
	 * Buscar todas as avalia��es de um projeto de pesquisa e mostrar um quadro resumo
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward resumoAvaliacoes(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {

		AvaliacaoProjetoForm avaliacaoForm = (AvaliacaoProjetoForm) form;
		AvaliacaoProjeto avaliacao = avaliacaoForm.getObj();

		// Buscar projeto de pesquisa
		ProjetoPesquisa projeto = getGenericDAO(req).findByPrimaryKey(avaliacao.getProjetoPesquisa().getId(), ProjetoPesquisa.class);

		// Calcular a m�dia de notas do projeto
		projeto.calcularMedia();

		req.setAttribute("projeto", projeto);
		return mapping.findForward("resumoAvaliacoes");
	}

	/**
	 * M�todo respons�vel pela visualiza��o de um projeto de pesquisa
	 */
	@Override
	public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		AvaliacaoProjetoForm avaliacaoForm = (AvaliacaoProjetoForm) form;

		// Popular avalia��o
		avaliacaoForm.setObj( getGenericDAO(req).findByPrimaryKey(avaliacaoForm.getObj().getId(), AvaliacaoProjeto.class) );
		avaliacaoForm.getObj().getConsultor().getNome();

		req.setAttribute("comprovante", true);
		return mapping.findForward(RESUMO);
	}

	/**
	 * M�todo respons�vel pelo cancelamento de um projeto de pesquisa
	 */
	@Override
	public ActionForward cancelar(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		clearSession(request);
		removeSession(mapping.getName(), request);
		clearSteps(request);
		return getMappingSubSistema(request, mapping);
	}

}
