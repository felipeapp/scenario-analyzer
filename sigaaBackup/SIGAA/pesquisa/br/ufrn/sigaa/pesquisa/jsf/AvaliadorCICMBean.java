/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 04/10/2007
 * 
 */
package br.ufrn.sigaa.pesquisa.jsf;


import static br.ufrn.arq.mensagens.MensagensArquitetura.NAO_HA_OBJETO_REMOCAO;
import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.autenticacao.AutenticacaoUtil;
import br.ufrn.arq.seguranca.autenticacao.EmissaoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.SubTipoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.TipoDocumentoAutenticado;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.pesquisa.AvaliadorCICDao;
import br.ufrn.sigaa.arq.dao.pesquisa.CongressoIniciacaoCientificaDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.pesquisa.dominio.AvaliadorCIC;
import br.ufrn.sigaa.pesquisa.dominio.CongressoIniciacaoCientifica;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Controlador para as operações sobre os avaliadores do CIC.
 * 
 * @author Leonardo Campos
 *
 */
@Component("avaliadorCIC")
@Scope("session")
public class AvaliadorCICMBean extends SigaaAbstractController<AvaliadorCIC> {
	
	/**Constante para jsp da lista de certificados */
	private static final String JSP_LISTA_CERTIFICADOS = "/pesquisa/avaliador_cic/lista_certificados.jsf";
	/**Constante para jsp da lista do relatório */
	private static final String JSP_RELATORIO = "/pesquisa/avaliador_cic/relatorio.jsf";
	
	
	
	/** Constante boolean que auxilia a exibição de dados*/
	private boolean verificando = false;
	
	/** Atributo que auxilia na exibição do comprovante*/
	private EmissaoDocumentoAutenticado comprovante;
	/** Filtro da área */
	private boolean filtroArea;
	/** Filtro do tipo usuário */
	private boolean filtroTipo;
	/** Filtro do relatório */
	private boolean filtroRelatorio;
	/** Filtro do status indica que a busca vai utilizar o status para relizara busca. */
	private boolean filtroStatus;
	
	public boolean isFiltroStatus() {
		return filtroStatus;
	}

	public void setFiltroStatus(boolean filtroStatus) {
		this.filtroStatus = filtroStatus;
	}

	/** Coleção dos avaliadores*/
	private Collection<AvaliadorCIC> avaliadoresCIC;
	
	/** Lista de avaliadores*/
	private ArrayList<AvaliadorCIC> lista;
	
	public AvaliadorCICMBean(){
		initObj();
	}

	/**
	 * Responsável pela inicialização do obj com os atributos.
	 */
	private void initObj() {
		filtroArea=false;
		obj = new AvaliadorCIC();
		obj.setCongresso(new CongressoIniciacaoCientifica());
		obj.setDocente(new Servidor());
		obj.setDiscente(new Discente());
		obj.setArea(new AreaConhecimentoCnpq());
		obj.setTipoUsuario(AvaliadorCIC.STATUS_DOCENTE);
	}
	
	@Override
	public String getFormPage() {
		return "/pesquisa/avaliador_cic/form.jsf";
	}
	
	@Override
	public String getListPage() {
		return "/pesquisa/avaliador_cic/lista.jsf";
	}
	
	public String getPresenca() {
		return "/pesquisa/avaliador_cic/marcar_presenca.jsf";
	}
	
	/**
	 * Responsável por checar se o usuário possui o papel necessário para prosseguir com a operação
	 * preparar para o cadastro é direcionar para a tela do formulário.
	 * <br>
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/pesquisa/avaliador_cic/lista.jsp
	 */
	public String preCadastrar() throws ArqException{
		checkRole(SigaaPapeis.GESTOR_PESQUISA);
		prepareMovimento(SigaaListaComando.CADASTRAR_AVALIADOR_CIC);
		initObj();
		setConfirmButton("Cadastrar");
		return forward(getFormPage());
	}
	
	/**
	 * Método responsável por atualizar o avaliador
	 * <br/>
	 * Invodado pela JSP: /sigaa.war/pesquisa/avaliador_cic/lista.jsp 
	 */
	public String atualizar() throws ArqException {

		try {
			int id = getParameterInt("id");
			
			setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
			prepareMovimento(ArqListaComando.ALTERAR);

			GenericDAO dao = new GenericDAOImpl(getSistema(),getSessionRequest());
			obj = dao.findByPrimaryKey(id, AvaliadorCIC.class);
			
			if( ValidatorUtil.isNotEmpty( obj.getDiscente() )  ){
				obj.setDocente( new Servidor() );
				obj.setTipoUsuario(AvaliadorCIC.STATUS_DISCENTE);
			} 
			if( ValidatorUtil.isNotEmpty( obj.getDocente() ) ){
				obj.setDiscente(new Discente());
				obj.setTipoUsuario(AvaliadorCIC.STATUS_DOCENTE);
			}
			
			if(ValidatorUtil.isEmpty( obj.getArea() ) ){
				obj.setArea(new AreaConhecimentoCnpq());
			}
			
			setConfirmButton("Alterar");
		
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
		}

		return forward(getFormPage());
	}
	
	/**
	 * Retorna para listagem;
	 * <br/>
	 * JSP:/sigaa.war/pesquisa/avaliador_cic/form.jsp
	 * @return
	 */
	public String cancelarListar(){
		initObj();
		return forward( getListPage() );
	}
	
	/**
	 * É realizada o cadastro de um novo avaliador do CIC. 
	 * <br>
 	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/portais/docente/menu_docente.jsp
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
		NegocioException {

			checkRole(SigaaPapeis.GESTOR_PESQUISA);
								
			if (getConfirmButton().equalsIgnoreCase("remover")) {
				return remover();
			} else {
				beforeCadastrarAndValidate();
				erros = new ListaMensagens();
				ListaMensagens lista = obj.validate();
		
												
				if (lista != null && !lista.isEmpty()) {
				erros.addAll(lista.getMensagens());
			}
		
			String descDominio = null;
			try {
				descDominio = ReflectionUtils.evalProperty(obj, "descricaoDominio");
			} catch (Exception e) {
			}
		
			if (!hasErrors()) {
				
				
				if(obj.isUsuarioDiscente() ){
					obj.setDocente(null);
				}else if( obj.isUsuarioDocente() ){
					obj.setDiscente(null);
				}
				if( ValidatorUtil.isEmpty( obj.getArea() ) ){
					obj.setArea(null);
				}
		
				MovimentoCadastro mov = new MovimentoCadastro();
				mov.setObjMovimentado(obj);
		
				if (obj.getId() == 0) {
					mov.setCodMovimento(SigaaListaComando.CADASTRAR_AVALIADOR_CIC);
					try {
						execute(mov, (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest());
						if (descDominio != null && !descDominio.equals("")) {
							addMessage(descDominio
									+ "  cadastrado com sucesso!",
									TipoMensagemUFRN.INFORMATION);
						} else {
							addMessage("Operação Realizada com sucesso",
									TipoMensagemUFRN.INFORMATION);
						}
						prepareMovimento(SigaaListaComando.CADASTRAR_AVALIADOR_CIC);
					} catch (Exception e) {
						addMensagemErro(e.getMessage());
						e.printStackTrace();
						initObj();
						return forward(getFormPage());
					}
		
					afterCadastrar();
		
					String forward = forwardCadastrar();
					if (forward == null) {
						return redirectJSF(getCurrentURL());
					} else {
						return redirectJSF(forward);
					}
		
				} else {
					mov.setCodMovimento(ArqListaComando.ALTERAR);
					try {
						
						if( ValidatorUtil.isEmpty(obj.getArea() ) ){
							obj.setArea(null);
						}
						if( ValidatorUtil.isEmpty( obj.getDiscente() ) ){
							obj.setDiscente(null);
						}
						if( ValidatorUtil.isEmpty( obj.getDocente() ) ){
							obj.setDocente(null);
						}
						
						AvaliadorCIC av = execute(mov);
						atualizarColecao(av);
						
						if (descDominio != null && !descDominio.equals("")) {
							addMessage(descDominio + "  alterado com sucesso!",
									TipoMensagemUFRN.INFORMATION);
						} else {
							addMessage("Operação Realizada com sucesso",
									TipoMensagemUFRN.INFORMATION);
						}
						
						initObj();
						setConfirmButton("Cadastrar");
						return forward(getListPage());
						
					} catch (Exception e) {
						addMensagemErro("Erro Inesperado: " + e.getMessage());
						e.printStackTrace();
						return forward(getFormPage());
					}
					
				}
		
			} else {
		
				return null;
			}
		}
	}
	
	
	
	/**
	 * Atualizar colecao de dados
	 * @throws DAOException 
	 */
	private  void atualizarColecao( AvaliadorCIC avalidador ) throws DAOException{
		if( ValidatorUtil.isNotEmpty( lista ) ){
			if( lista.contains( avalidador )){
				int index = lista.indexOf(avalidador);
				lista.remove(index);
				getGenericDAO().initialize(avalidador);
				lista.add(index, avalidador);
			}
		}
	}
	
	@Override
	protected void afterCadastrar() throws ArqException {
		super.afterCadastrar();
		initObj();
	}
	
	/**
	 * Redireciona para a página da listagem.
	 * <br>
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/pesquisa/menu/iniciacao.jsp
	 */
	@Override
	public String listar() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_PESQUISA);
		initObj();
		lista=new ArrayList<AvaliadorCIC>();
		obj.setCongresso(getDAO(CongressoIniciacaoCientificaDao.class).findAtivo());
		return forward(getListPage());
	}
	
	/**
	 * Método busca e preenche o select item para o combo com todos os congresso de iniciação cientifica.
	 * <br/>
	 * JSP: /sigaa.war/pesquisa/avaliador_cic/form.jsp
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllCongressosCombo() throws DAOException{
		return toSelectItems(getGenericDAO().findAll(CongressoIniciacaoCientifica.class), "id", "descricao");
	}
	
	/**
	 * Realizar a busca com os parâmetros informados. Pelo usuário;
	 * <br>
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/pesquisa/avaliador_cic/lista.jsp
	 */
	@Override
	public String buscar() throws Exception {
		CongressoIniciacaoCientificaDao dao = getDAO(CongressoIniciacaoCientificaDao.class);
		
		AreaConhecimentoCnpq area = null;
		Boolean avaliadorResumo = null;
		Boolean avaliadorApresentacao = null;
		
		erros = new ListaMensagens();
		
		if(filtroArea){
			area = obj.getArea();
			ValidatorUtil.validateRequiredId(area.getId(), "Área de Conhecimento", erros);
		}
		avaliadorResumo = obj.isAvaliadorResumo();
		avaliadorApresentacao = obj.isAvaliadorApresentacao();
		
		
		if( ValidatorUtil.isEmpty( obj.getTipoUsuario() ) ){
			erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO,"Tipo do Usuário");
		}
		
		if(!erros.isEmpty()){
			addMensagens(erros);
			return forward(getListPage());
		} else {
			boolean buscarDocente=false;
			boolean buscarDiscente=false;
			
			if(  obj.isUsuarioDiscente() ){
				buscarDiscente=true;
			}
			if( obj.isUsuarioDocente() ){
				buscarDocente=true;
			}
			lista = (ArrayList<AvaliadorCIC>) dao.findByCongresso(obj.getCongresso(), avaliadorResumo, avaliadorApresentacao, area,buscarDiscente , buscarDocente);
		}
		
		if(!filtroRelatorio){
			if(	ValidatorUtil.isEmpty( lista ) ){
				addMensagem( MensagensArquitetura.BUSCA_SEM_RESULTADOS );
			}
			return forward(getListPage());
		} else {
			obj.setCongresso( dao.refresh(obj.getCongresso()) );
			if(filtroArea)
				obj.setArea( dao.refresh(obj.getArea()) );
			return forward(JSP_RELATORIO);
		}
	}

	/**
	 * É realizada uma busca para verificar se o usuário possui certificado cadastrado, caso possua será
	 * exibido para o mesmo a opção referente emissão do certificado.  
	 * <br>
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/portais/docente/menu_docente.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public String listarCertificados() throws SegurancaException, DAOException{
		checkDocenteRole();
		
		Collection lista = getGenericDAO().findByExactField(AvaliadorCIC.class, "docente.id", getUsuarioLogado().getServidor().getId(), "asc", "congresso.ano");
	
		if(lista.isEmpty()){
			addMensagemErro("Você não possui certificados de avaliador disponíveis.");
			return null;
		}
		setResultadosBusca(lista);
		return forward(JSP_LISTA_CERTIFICADOS);
	}
	
	/**
	 * Geração do certificado para os avaliadores que participaram do CIC.
	 * <br>
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/pesquisa/avaliador_cic/lista_certificados.jsp
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String emitirCertificado() throws Exception {

		if(obj == null || obj.getId() <= 0){
			obj = getGenericDAO().findByPrimaryKey(getParameterInt("idAvaliador"), AvaliadorCIC.class);
			obj.getCongresso().getAno();
			obj.getCongresso().getEdicao();
			obj.getCongresso().getInicio();
			obj.getCongresso().getFim();
			obj.getDocente().getPessoa().getNome();
		}
		
		ArrayList<AvaliadorCIC> lista = new ArrayList<AvaliadorCIC>();
		lista.add(obj);
		
		HashMap parametros = new HashMap();
		
		// gerando código de autenticação...
		if (!verificando) {

			comprovante = geraEmissao(
					TipoDocumentoAutenticado.CERTIFICADO,
					((Integer) obj.getId()).toString(),
					gerarSementeCertificado(), null, SubTipoDocumentoAutenticado.CERTIFICADO_PARTICIPANTE_AVALIADOR_CIC, true);
		}
		
		parametros.put("codigoSeguranca", comprovante.getCodigoSeguranca());
		parametros.put("siteVerificacao", ParametroHelper.getInstance().getParametro(ConstantesParametro.ENDERECO_AUTENTICIDADE));
		parametros.put("data_certificado", Formatador.getInstance().formatarDataDiaMesAno( comprovante.getDataEmissao() ) );
		parametros.put("numero_documento", comprovante.getNumeroDocumento());

		// Gerar certificado
	    JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReportSIGAA("certificado_avaliador_cic.jasper"),
	    		parametros,
	    		new JRBeanCollectionDataSource(lista));

	    getCurrentResponse().setContentType("application/pdf");
	    getCurrentResponse().addHeader("Content-Disposition", "attachment; filename=certificado_avaliador.pdf");
	    JasperExportManager.exportReportToPdfStream(prt,getCurrentResponse().getOutputStream());
	    
	    FacesContext.getCurrentInstance().responseComplete();
	    
	    initObj();
	    return null;
	}

	/**
	 * Gerar semente para validação do documento
	 * <br>
	 * Esse método é chamado pelos seguintes métodos: emitirCertificado() e validaDigest()
	 * 
	 * @return
	 */
	private String gerarSementeCertificado() {
		StringBuilder builder = new StringBuilder();

		builder.append(obj.getId());
		builder.append(obj.getDocente().getId());
		builder.append(obj.getCongresso().getId());
		builder.append(TipoDocumentoAutenticado.CERTIFICADO);

		return builder.toString();
	}
	
	/**
	 * Serve para validar o certificado informando os dados necessários para a realização
	 * de tal evento;
	 * <br>
	 * JSP: /sigaa.war/public/autenticidade/tipo_documento.jsp
	 * 
	 * @param comprovante
	 * @param req
	 * @param res
	 */
	public void exibir(EmissaoDocumentoAutenticado comprovante,
			HttpServletRequest req, HttpServletResponse res) {
		try {
			this.comprovante = comprovante;
			verificando = true;
			obj = getGenericDAO().findByPrimaryKey(Integer.parseInt(comprovante.getIdentificador()), AvaliadorCIC.class);
			emitirCertificado();
		} catch (Exception e) {
			addMensagemErroPadrao();
			notifyError(e);
		}
	}

	/**
	 * Validação das emissões de certificado.  
	 * <br>
	 * JSP: /sigaa.war/public/autenticidade/tipo_documento.jsp
	 * 
	 * @param comprovante
	 * @return
	 */
	public boolean validaDigest(EmissaoDocumentoAutenticado comprovante) {		
		try {
			obj = getGenericDAO().findByPrimaryKey(Integer.parseInt(comprovante.getIdentificador()), AvaliadorCIC.class);
			String codigoVerificacao = AutenticacaoUtil.geraCodigoValidacao(comprovante, gerarSementeCertificado());
			if (codigoVerificacao.equals(comprovante.getCodigoSeguranca()))
				return true;
		} catch (Exception e) {
			addMensagemErroPadrao();
			notifyError(e);
		}
		return false;
	}
	
	/**
	 * Realiza uma checagem no papel do usuário, para verificar se o mesmo possui permissão para
	 * realizar a operação e direcioná-lo para a tela do formulário.
	 * <br>
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/portais/docente/menu_docente.jsp
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String iniciarMarcacaoPresenca() throws SegurancaException, DAOException {
		checkRole(SigaaPapeis.GESTOR_PESQUISA);
		initObj();
		avaliadoresCIC=new ArrayList<AvaliadorCIC>();
		obj.setCongresso(getDAO(CongressoIniciacaoCientificaDao.class).findAtivo());
		return forward(getPresenca());
	}
	
	/**
	 * Realiza a busca com os parâmetros passados no formulário.
	 * <br>
	 * JSP:  /SIGAA/app/sigaa.ear/sigaa.war/pesquisa/avaliador_cic/marcar_presenca.jsp
	 *  
	 * @return
	 * @throws DAOException
	 */
	public String buscarAvaliador() throws DAOException{
		
		CongressoIniciacaoCientificaDao dao = getDAO(CongressoIniciacaoCientificaDao.class);
		
		AreaConhecimentoCnpq area = null;
		Boolean avaliadorResumo = null;
		Boolean avaliadorApresentacao = null;
		
		erros = new ListaMensagens();
		
		if(filtroArea){
			area = obj.getArea();
			ValidatorUtil.validateRequiredId(area.getId(), "Área de Conhecimento", erros);
		}
		if(obj.isAvaliadorResumo()){
			avaliadorResumo = obj.isAvaliadorResumo();
		}
		if( obj.isAvaliadorApresentacao()){
			avaliadorApresentacao = obj.isAvaliadorApresentacao();
		}
		
		if(!erros.isEmpty()){
			addMensagens(erros);
			return forward(getPresenca());
		} else {
			avaliadoresCIC = dao.findByCongresso(obj.getCongresso(), avaliadorResumo, avaliadorApresentacao, area,false,false);
		}
		return forward(getPresenca());
	}
	
	/**
	 * Realiza um update no campo presença dos avaliadores selecionados para terem a sua
	 * presença modificada.
	 * <br>
	 * JSP:  /SIGAA/app/sigaa.ear/sigaa.war/pesquisa/avaliador_cic/marcar_presenca.jsp
	 * @return
	 * @throws DAOException
	 */
	public String cadastrarPresenca() throws DAOException {
		AvaliadorCICDao dao = getDAO(AvaliadorCICDao.class);
		dao.atualizacaoPresenca(avaliadoresCIC, obj.getCongresso());
		addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO, "Presença dos Avaliadores ");
		
		return forward(getPresenca());
	}
	
	
	/**
	 * Método chamado para remover uma entidade
	 * <br/>
	 * JSP: /sigaa.war/pesquisa/avaliador_cic/lista.jsp
	 * @return
	 * @throws ArqException
	 */
	public String remover() throws ArqException {
		int id = getParameterInt("id", 0 );
		AvaliadorCICDao dao = getDAO(AvaliadorCICDao.class);
		MovimentoCadastro mov = new MovimentoCadastro(obj, ArqListaComando.REMOVER);
		prepareMovimento(ArqListaComando.REMOVER);
		AvaliadorCIC avalidador = dao.findByPrimaryKey(id, AvaliadorCIC.class );
		
		if ( ValidatorUtil.isEmpty( avalidador ) ) {
			addMensagem(NAO_HA_OBJETO_REMOCAO);
			return null;
		} else {
			try {
				mov.setObjMovimentado(avalidador);
				execute(mov);
				addMensagem(OPERACAO_SUCESSO);
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				return redirect(getListPage());
			} 

		}
		lista.remove(avalidador);
		return forward( getListPage());
	}
	
	/**
	 * Retorna os select item para o radio da escolha do tipo de avaliador.
	 * </br>
	 * Invocado pela JSP:
	 * 
	 * @return
	 */
	public Collection<SelectItem> getComboStatusPessoa(){
		Collection<SelectItem> itens = new ArrayList<SelectItem>();
		SelectItem item1= new SelectItem(AvaliadorCIC.STATUS_DOCENTE,"DOCENTE");
		SelectItem item2= new SelectItem(AvaliadorCIC.STATUS_DISCENTE,"DISCENTE");
		itens.add(item1);
		itens.add(item2);
		return itens;
	}
	
	public boolean isVerificando() {
		return verificando;
	}

	public void setVerificando(boolean verificando) {
		this.verificando = verificando;
	}

	public EmissaoDocumentoAutenticado getComprovante() {
		return comprovante;
	}

	public void setComprovante(EmissaoDocumentoAutenticado comprovante) {
		this.comprovante = comprovante;
	}

	public boolean isFiltroArea() {
		return filtroArea;
	}

	public void setFiltroArea(boolean filtroArea) {
		this.filtroArea = filtroArea;
	}

	public boolean isFiltroTipo() {
		return filtroTipo;
	}

	public void setFiltroTipo(boolean filtroTipo) {
		this.filtroTipo = filtroTipo;
	}

	public boolean isFiltroRelatorio() {
		return filtroRelatorio;
	}

	public void setFiltroRelatorio(boolean filtroRelatorio) {
		this.filtroRelatorio = filtroRelatorio;
	}

	public Collection<AvaliadorCIC> getAvaliadoresCIC() {
		return avaliadoresCIC;
	}

	public void setAvaliadoresCIC(Collection<AvaliadorCIC> avaliadoresCIC) {
		this.avaliadoresCIC = avaliadoresCIC;
	}

	/**
	 * Autocomplete para nome do avaliador.
	 * </br>
	 * JSP:/sigaa.war/pesquisa/avaliacao_apresentacao_resumo/lista_gestor.jsp
	 * @param event
	 * @return
	 * @throws DAOException
	 */
	public List<AvaliadorCIC> autoComplete(Object event) throws DAOException {
		String nome = event.toString();
		CongressoIniciacaoCientificaDao dao = getDAO(CongressoIniciacaoCientificaDao.class);
		return getDAO(AvaliadorCICDao.class).findByNome(nome, dao.findAtivo());
	}

	public ArrayList<AvaliadorCIC> getLista() {
		return lista;
	}

	public void setLista(ArrayList<AvaliadorCIC> lista) {
		this.lista = lista;
	}


}