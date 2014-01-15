///*
// * Universidade Federal do Rio Grande do Norte
// * Superintendência de Informática - UFRN
// * Diretoria de Sistemas
// *
// * Created on 06/10/2006
// *
// */
//package br.ufrn.sigaa.pesquisa.struts;
//
//import java.util.Collection;
//import java.util.Date;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.struts.action.ActionForm;
//import org.apache.struts.action.ActionForward;
//import org.apache.struts.action.ActionMapping;
//import org.apache.struts.upload.FormFile;
//
//import br.ufrn.arq.dao.GenericDAO;
//import br.ufrn.arq.dominio.MovimentoCadastro;
//import br.ufrn.arq.erros.NegocioException;
//import br.ufrn.arq.negocio.ArqListaComando;
//import br.ufrn.arq.negocio.validacao.ListaMensagens;
//import br.ufrn.arq.negocio.validacao.MensagemAviso;
//import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
//import br.ufrn.arq.seguranca.SigaaPapeis;
//import br.ufrn.arq.util.RequestUtils;
//import br.ufrn.sigaa.arq.dao.pesquisa.EditalPesquisaDao;
//import br.ufrn.sigaa.arq.dao.projetos.EditalDao;
//import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
//import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
//import br.ufrn.sigaa.arq.struts.ConstantesCadastro;
//import br.ufrn.sigaa.dominio.Usuario;
//import br.ufrn.sigaa.pesquisa.dominio.CategoriaProjetoPesquisa;
//import br.ufrn.sigaa.pesquisa.dominio.CotaBolsas;
//import br.ufrn.sigaa.pesquisa.dominio.Cotas;
//import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
//import br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa;
//import br.ufrn.sigaa.pesquisa.form.EditalPesquisaForm;
//import br.ufrn.sigaa.pesquisa.negocio.EditalPesquisaValidator;
//import br.ufrn.sigaa.pesquisa.negocio.MovimentoEditalPesquisa;
//import br.ufrn.sigaa.projetos.dominio.Edital;
//
///**
// * Action responsável pelo controle das operações referentes à publicação de editais
// * relacionados ao subsistema de pesquisa
// * 
// * @author ricardo
// *
// */
//public class PublicarEditalPesquisaAction extends AbstractCrudAction {
//
//	/**
//	 * Método responsável por editar um publicação do edital de pesquisa
//	 */
//	@Override
//	public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
//		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);
//		
//		EditalPesquisaForm editalForm = (EditalPesquisaForm) form;
//		if( req.getParameter("popular") != null )
//		    editalForm = new EditalPesquisaForm();
//		EditalPesquisaDao editalDao = getDAO(EditalPesquisaDao.class, req);
//		
//		if( req.getParameter("id") != null ){
//			editalForm.setObj( editalDao.findByPrimaryKey( Integer.parseInt( req.getParameter("id") ), EditalPesquisa.class) );
//			if( req.getAttribute("remove") == null && req.getSession()
//	                .getAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION) == null)
//				addMensagem(new MensagemAviso("Ao alterar o Edital, você pode submeter um novo arquivo <br>" +
//										"ou deixar o arquivo que foi submetido anteriormente.", TipoMensagemUFRN.WARNING), req);
//		}
//		
//		req.setAttribute("cotas", editalDao.findAll(CotaBolsas.class));
//		req.setAttribute("categorias", editalDao.findAllAtivos(CategoriaProjetoPesquisa.class, "ordem"));
//		req.setAttribute("editais", editalDao.findAllAtivos());
//		req.setAttribute("editaisAssociados", getDAO(EditalDao.class, req).findAllAssociados());
//		req.setAttribute("tiposBolsa", editalDao.findAll(TipoBolsaPesquisa.class, "descricao", "asc"));
//		
//		// Definir cota como a cota padrão atual
//		Collection<CotaBolsas> cotas = editalDao.findByExactField(CotaBolsas.class, "cotaAtual", true);
//		if (editalForm.getObj().getId() == 0 && cotas != null && !cotas.isEmpty())
//			editalForm.getObj().setCota(cotas.iterator().next());
//		
//		if( req.getAttribute("remove") == null )
//			prepareMovimento(SigaaListaComando.PUBLICAR_EDITAL_PESQUISA, req);
//		
//		editalForm.setEditalAssociado(editalForm.getObj().isAssociado());	
//		req.getSession().setAttribute(mapping.getName(), editalForm);
//		return mapping.findForward(FORM);
//	}
//	
//	/**
//	 * Método com a função de trocar o edital associado
//	 * 
//	 * @param mapping
//	 * @param form
//	 * @param req
//	 * @param res
//	 * @return
//	 * @throws Exception
//	 */
//	public ActionForward changeEditalAssociado(ActionMapping mapping, ActionForm form,
//			HttpServletRequest req, HttpServletResponse res)
//			throws Exception {
//		EditalPesquisaForm editalForm = (EditalPesquisaForm) form;
//		
//		if(editalForm.getObj().getEdital() != null && editalForm.getObj().getEdital().getId() > 0)
//			editalForm.getObj().setEdital(getGenericDAO(req).findByPrimaryKey(editalForm.getObj().getEdital().getId(), Edital.class));
//		else
//			editalForm.getObj().setEdital(new Edital());
//		
//		return edit(mapping, form, req, res);
//	}
//	
//	/**
//	 * Método responsável pela publicação de um edital de pesquisa
//	 */
//	@Override
//	public ActionForward persist(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
//		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);
//		
//		EditalPesquisaForm editalForm = (EditalPesquisaForm) form;
//		
//		if(editalForm.getObj().getEdital().getId() < 0)
//			editalForm.getObj().getEdital().setId(0);
//		
//		// Validar dados
//		ListaMensagens lista = new ListaMensagens();
//		EditalPesquisaValidator.validaEdital(editalForm, editalForm.getObj(), lista);
//		if (lista.isErrorPresent()) {
//			addMensagens(lista.getMensagens(), req);
//			return edit(mapping, form, req, res);
//		}
//		
//		if(!editalForm.isEditalAssociado()){
//			editalForm.getObj().setDataCadastro(new Date());
//			editalForm.getObj().setUsuario((Usuario) getUsuarioLogado(req));
//		}
//		
//		// Popular movimento
//		MovimentoEditalPesquisa movEdital = new MovimentoEditalPesquisa();
//		
//		movEdital.setCodMovimento(SigaaListaComando.PUBLICAR_EDITAL_PESQUISA);
//		movEdital.setEditalPesquisa(editalForm.getObj());
//		
//		if(editalForm.getArquivoEdital() != null){
//			FormFile arquivoEdital = editalForm.getArquivoEdital();
//			if (arquivoEdital.getFileSize() != 0) {
//				movEdital.setNome(arquivoEdital.getFileName());
//				movEdital.setContentType(arquivoEdital.getContentType());
//				movEdital.setDadosArquivo(arquivoEdital.getFileData());
//			}
//		}
//		
//		execute(movEdital, req);
//		
//		addInformation("Edital publicado com sucesso!", req);
//		
//		req.setAttribute("objeto", editalForm.getObj());
//		removeSession(mapping.getName(), req);
//		return mapping.findForward(VIEW);
//	}
//	
//	/**
//	 * Método responsável pela edição de um edital de pesquisa
//	 */
//	@Override
//	public ActionForward remove(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
//		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);
//		
//		if (getForm(form).isConfirm()) {
//			GenericDAO dao = getGenericDAO(req);
//			EditalPesquisa edital = dao.findByPrimaryKey(RequestUtils.getIntParameter(req, "id"), EditalPesquisa.class);
//
//			MovimentoEditalPesquisa movEdital = new MovimentoEditalPesquisa();
//			movEdital.setCodMovimento(SigaaListaComando.REMOVER_EDITAL_PESQUISA);
//			movEdital.setEditalPesquisa(edital);
//
//			try {
//				execute(movEdital, req);
//			} catch (NegocioException e) {
//				addMensagens(e.getListaMensagens().getMensagens(), req);
//				req.setAttribute(mapping.getName(), form);
//				return view(mapping, form, req, res);
//			}
//
//			addInformation("Remoção realizada com sucesso!", req);
//			return mapping.findForward(getSubSistemaCorrente(req).getForward());
//		} else {
//			req.setAttribute("remove", true);
//			prepareMovimento(SigaaListaComando.REMOVER_EDITAL_PESQUISA, req);
//			return view(mapping, form, req, res);
//		}
//	}
//
//	/**
//	 * Adiciona uma quantidade de cotas do tipo informado para serem distribuídas através do edital.
//	 * 
//	 * @param mapping
//	 * @param form
//	 * @param request
//	 * @param response
//	 * @return
//	 * @throws Exception
//	 */
//	public ActionForward adicionarCotas(ActionMapping mapping, ActionForm form,
//			HttpServletRequest request, HttpServletResponse response)
//			throws Exception {
//		checkRole(SigaaPapeis.GESTOR_PESQUISA, request);
//		
//		EditalPesquisaForm editalForm = (EditalPesquisaForm) form;
//		TipoBolsaPesquisa tipoBolsa = editalForm.getTipoBolsa();
//		
//		// Validar dados
//		ListaMensagens lista = new ListaMensagens();
//		EditalPesquisaValidator.validaAdicionaCota(editalForm, lista);
//		if (lista.isErrorPresent()) {
//			return edit(mapping, form, request, response);
//		}
//		
//		tipoBolsa = getGenericDAO(request).findByPrimaryKey(tipoBolsa.getId(), TipoBolsaPesquisa.class);
//		Cotas cota = new Cotas();
//		cota.setTipoBolsa(tipoBolsa);
//		cota.setQuantidade(editalForm.getQuantidade());
//		editalForm.getObj().addCotas(cota);
//		
//		
//		return edit(mapping, form, request, response);
//	}
//	/**
//	 * Remove uma quantidade de cotas do edital.
//	 * 
//	 * @param mapping
//	 * @param form
//	 * @param request
//	 * @param response
//	 * @return
//	 * @throws Exception
//	 */
//	public ActionForward removerCotas(ActionMapping mapping, ActionForm form,
//			HttpServletRequest request, HttpServletResponse response)
//			throws Exception {
//		checkRole(SigaaPapeis.GESTOR_PESQUISA, request);
//		
//		EditalPesquisaForm editalForm = (EditalPesquisaForm) form;
//		
//		int id = RequestUtils.getIntParameter(request, "idCotas");
//
//		if(id > 0){
//			Cotas c = new Cotas();
//			c.setId(id);
//			
//			prepareMovimento(ArqListaComando.REMOVER, request);
//			
//			MovimentoCadastro mov = new MovimentoCadastro();
//			mov.setCodMovimento(ArqListaComando.REMOVER);
//			mov.setObjMovimentado(c);
//			
//			executeWithoutClosingSession(mov, request);
//		}
//		int pos = RequestUtils.getIntParameter(request, "pos");
//		removePorPosicao(editalForm.getObj().getCotas(), pos);
//		
//		return edit(mapping, form, request, response);
//	}
//	
//	/**
//	 * Método que possibilita a visualização de um edital de pesquisa
//	 */
//	@Override
//	public ActionForward view(ActionMapping mapping, ActionForm form,
//			HttpServletRequest req, HttpServletResponse res) throws Exception {
//		GenericDAO dao = getGenericDAO(req);
//
//		getForm(form).checkRole(req);
//
//		int id = RequestUtils.getIntParameter(req, "id");
//
//		EditalPesquisa obj = dao.findByPrimaryKey(id, EditalPesquisa.class);
//		obj.getCotas().iterator();
//		req.setAttribute(ConstantesCadastro.ATRIBUTO_VISUALIZACAO, obj);
//		req.setAttribute("visualizacao", Boolean.TRUE);
//
//		return mapping.findForward(VIEW);
//	}
//}
