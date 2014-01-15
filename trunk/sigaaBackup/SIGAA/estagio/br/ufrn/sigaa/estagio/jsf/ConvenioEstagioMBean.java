/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 28/09/2010
 */
package br.ufrn.sigaa.estagio.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.MunicipioDao;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.estagio.ConvenioEstagioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.estagio.dominio.ConcedenteEstagio;
import br.ufrn.sigaa.estagio.dominio.ConcedenteEstagioPessoa;
import br.ufrn.sigaa.estagio.dominio.ConvenioEstagio;
import br.ufrn.sigaa.estagio.dominio.Estagiario;
import br.ufrn.sigaa.estagio.dominio.StatusConvenioEstagio;
import br.ufrn.sigaa.estagio.dominio.TipoConvenio;
import br.ufrn.sigaa.estagio.dominio.TipoOfertaVaga;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * 
 * Este MBean tem como finalidade de auxiliar nas operações relacionadas ao Convênio de Estágio
 * 
 * @author Arlindo Rodrigues
 *
 */
@Component("convenioEstagioMBean") @Scope("request")
public class ConvenioEstagioMBean extends SigaaAbstractController<ConvenioEstagio> {
	
	/** Lista de Convênios Encontrados na Consulta */
	private Collection<ConvenioEstagio> listaConvenios = new ArrayList<ConvenioEstagio>();
	
	/** Lista de Convênios Pendentes de Análise */
	private Collection<ConvenioEstagio> pendentesAnalise = new ArrayList<ConvenioEstagio>();	
	
	/** Lista de Tipos de Convênio */
	private Collection<TipoConvenio> listaTiposConvenio = new ArrayList<TipoConvenio>();
	
	/** Lista de SelectItem para escolha do município do endereço. */
	private Collection<SelectItem> municipiosEndereco = new ArrayList<SelectItem>(0);	
	
	/** Indica se é cadastro */
	private boolean cadastro;
	
	// Atributos que auxiliam na consulta 
	/** Atributo que auxilia na consulta, indicando se o concedente foi selecionado */
	private boolean filtroStatus;
	/** Atributo que auxilia na consulta, indicando se uma empresa foi selecionada */
	private boolean filtroEmpresa;
	/** Atributo que auxilia na consulta, indicando se o responsável foi selecionado */
	private boolean filtroResponsavel;	
	/** Atributo que auxilia na consulta, indicando se o CPF/CNPJ foi selecionado */
	private boolean filtroCpfCnpj;
	/** Atributo que auxilia na consulta, indicando se o número do convênio foi selecionado */
	private boolean filtroNumConvenio;
	/** Status escolhido na consulta */
	private StatusConvenioEstagio status;
	/** Nome da empresa informado na consulta */
	private String empresa;
	/** Nome do responsável informado na consulta */
	private String responsavel;
	/** CPF/CNPJ do responsável informado na consulta */
	private long cpfCnpj;
	/** Número do Convênio Cadastrado */
	private String numeroConvenio;
	
	/** Pessoa Responsável pelo Concedente de Estágio */
	private ConcedenteEstagioPessoa concedenteEstagioPessoa;
	
	/** Dados pessoais do reitor */
	private Pessoa reitor;
	
	/** Dados pessoais do pró-reitor de graduação. */
	private Pessoa proReitor;
	
	/** Estagiário selecionado */
	private Estagiario estagiario;
		
	/** Comandos possíveis referentes a busca de convênios 
	 * (utilizado ao selecionar o convênio) */ 
	protected enum ComandoConvenioEstagio {
		/** Indica que o usuário analisará um convênio. */ 
		ANALISAR_CONVENIO, 
		/** Indica que o usuário cadastrará uma oferta de estágio. */
		CADASTRO_OFERTA, 
		/** Indica que o usuário cadastrará um estágio avulso. */
		CADASTRO_ESTAGIO_AVULSO;
		
	}
	
	/** Comando atual */
	private ComandoConvenioEstagio comandoConvenio;
	
	/** Indica que é cadastro de oferta de estágio */
	private boolean cadastroOferta;

	/** Indica se pode alterar o nome do concedente. */
	private boolean podeAlterarNomeConcedente;
	
	/** Indica se pode alterar o nome do responsável. */
	private boolean podeAlterarNomeResponsavel;

	/** Atributo que auxilia na consulta, indicando se filtra o resultado por tipo de oferta de vagas */
	private boolean filtrarTipoOfertaVagas;
	
	/** Arquivo digitalizado do termo de convênio constando as assinaturas das partes. */
	private UploadedFile arquivoTermoConvenio;

	/** MBean que está usando este controller para selecionar um convênio de estágio. */
	private SeletorConvenioEstagio mBean;

	/** Possíveis status de convênio de estágio que poderão ser selecionados, quando este controller estiver no {@link #modoSeletor modo seletor} .*/
	private Integer[] statusConvenioEstagio;

	/**
	 * Indica que este controller está no modo seletor, isto é, que passará o
	 * fluxo de execução para o {@link #mBean} setando o convênio de estágio
	 * selecioando pelo usuário.
	 */
	private boolean modoSeletor;

	/** Indica que o usuário está fazendo análise de convênios. */
	private boolean analiseConvenios;
	
	/**
	 * Inicializa os Objetos
	 */
	private void initObj() {
		if (obj == null)
			obj = new ConvenioEstagio();
		
		if (obj.getConcedente() == null)
			obj.setConcedente(new ConcedenteEstagio());
		
		if (obj.getConcedente().getPessoa() == null)
			obj.getConcedente().setPessoa(new Pessoa());
		
		if (concedenteEstagioPessoa == null)
			concedenteEstagioPessoa = new ConcedenteEstagioPessoa();
		
		if (concedenteEstagioPessoa.getPessoa() == null)
			concedenteEstagioPessoa.setPessoa(new Pessoa());
		concedenteEstagioPessoa.getPessoa().prepararDados();
		
		if (obj.getTipoConvenio() == null)
			obj.setTipoConvenio(new TipoConvenio());
		
		obj.setStatus(new StatusConvenioEstagio(StatusConvenioEstagio.SUBMETIDO, "SUBMETIDO"));
		
		cadastro = false;
		filtroStatus = false;
		podeAlterarNomeConcedente = true;
		podeAlterarNomeResponsavel = true;
		status = new StatusConvenioEstagio();
		modoSeletor = false;
		analiseConvenios = true;
	}
	
	/** Construtor Padrão */
	public ConvenioEstagioMBean() {
		initObj();
	}
	
	/**
	 * Inicia o Cadastro de Convênio de Estágio
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String iniciar() throws ArqException{
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.GESTOR_CONVENIOS_ESTAGIO_PROPLAN, SigaaPapeis.COORDENADOR_ESTAGIOS);	
		prepareMovimento(SigaaListaComando.CADASTRAR_CONVENIO_ESTAGIO);
		initObj();		
		cadastro = true;
		
		ConvenioEstagioDao dao = getDAO(ConvenioEstagioDao.class);
		listaTiposConvenio = dao.findAllTipoConvenioSolicitacao();
		if (ValidatorUtil.isEmpty(listaTiposConvenio)){
			addMensagemErro("Nenhum Tipo de Convênio Cadastrado");
			return null;
		}
		
		if (obj.getConcedente().getPessoa().getEnderecoContato() == null
				|| obj.getConcedente().getPessoa().getEnderecoContato().getId() == 0)
			carregarMunicipiosEndereco(UnidadeFederativa.ID_UF_PADRAO);
		else
			carregarMunicipiosEndereco(obj.getConcedente().getPessoa().
				getEnderecoContato().getUnidadeFederativa().getId());
		return telaForm();
	}
	
	/**
	 * Alterar o Cadastro de Convênio de Estágio
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/convenio_estagio/include/_lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@Override
	public String atualizar() throws ArqException {
		obj = new ConvenioEstagio();
		concedenteEstagioPessoa = new ConcedenteEstagioPessoa();
		iniciar();
		int id = getParameterInt("id", 0);
		GenericDAO dao = getGenericDAO();
		obj = dao.findByPrimaryKey(id, ConvenioEstagio.class);
		if (obj != null) {
			analiseConvenios = false;
			concedenteEstagioPessoa = obj.getConcedente().getResponsavel();
			concedenteEstagioPessoa.getPessoa().prepararDados();
			status = obj.getStatus();
			obj.getConcedente().getPessoa().prepararDados();
			setConfirmButton("Alterar");
			podeAlterarNomeResponsavel = true;
			podeAlterarNomeConcedente = true;
			prepareMovimento(SigaaListaComando.ATUALIZAR_CONVENIO_ESTAGIO);
			return forward(getFormPage());
		} else {
			obj = new ConvenioEstagio();
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return null;
		}
	}
	
	/**
	 * Iniciar Análise do Convênio de Estágio
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/modulo/geral.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String iniciarAnalise() throws SegurancaException, HibernateException, DAOException{
		checkRole(SigaaPapeis.GESTOR_CONVENIOS_ESTAGIO_PROPLAN);		
		pendentesAnalise = getConveniosPendentesAnalise(null);
		if (isEmpty(pendentesAnalise)){
			addMensagemErro("Não há Solicitações de Convênios Pendentes.");
			return null;
		}
		listaConvenios = null;
		comandoConvenio = ComandoConvenioEstagio.ANALISAR_CONVENIO;
		obj = new ConvenioEstagio();
		concedenteEstagioPessoa = new ConcedenteEstagioPessoa();
		initObj();
		analiseConvenios = true;
		return telaConsulta();		
	}	
	
	/**
	 * Inicia o formulário de Análise de Convênio de Estágio
	 * Método chamado pela seguinte JSP:
	 * <br/><br/>
	 * <ul>
	 *   <li>/sigaa.war/estagio/convenio_estagio/include/_lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String analisar() throws ArqException{
		populateObj();
		if (ValidatorUtil.isEmpty(obj)){
			addMensagemErro("Objeto não selecionado");
			return null;
		}			
		if (obj.isAgenteInterno()){
			if (ValidatorUtil.isEmpty(obj.getConcedente().getUnidade()))
				obj.getConcedente().setUnidade(new Unidade());
		}
		status = obj.getStatus();
		concedenteEstagioPessoa = obj.getConcedente().getResponsavel();
		prepareMovimento(SigaaListaComando.ANALISAR_CONVENIO_ESTAGIO);
		return forward("/estagio/convenio_estagio/form_analise.jsp");
	}
	
	/**
	 * Seleciona a convênio estágio e prossegue com a ação chamada
	 * <br/><br/>
	 * <ul>
	 *   <li>/sigaa.war/estagio/convenio_estagio/include/_lista.jsp</li>
	 * </ul> 
	 * @return
	 * @throws ArqException
	 */
	public String selecionarConvenio() throws ArqException {
		if (comandoConvenio != null){
			
			if (obj.isAprovado() && comandoConvenio.equals(ComandoConvenioEstagio.CADASTRO_OFERTA)){				
				OfertaEstagioMBean bean = getMBean("ofertaEstagioMBean");
				bean.setConcedente(obj.getConcedente());
				return bean.iniciarCadastro();
			} else if (obj.isAprovado() && comandoConvenio.equals(ComandoConvenioEstagio.CADASTRO_ESTAGIO_AVULSO)){
				estagiario.setConcedente(obj.getConcedente());
				EstagioMBean bean = getMBean("estagioMBean");
				bean.setObj(estagiario);
				return bean.carregarCadastroAvulso();		
			} else if (isPermiteAnalisarConvenio()){
				return analisar();
			}
			
		}
		populateObj();
		if (modoSeletor) {
			erros = mBean.validaConvenioSelecionado(obj);
			if (hasErrors()) return null;
			else return mBean.selecionaConvenioEstagio(obj);
		}
		concedenteEstagioPessoa = obj.getConcedente().getResponsavel();
		return forward(getViewPage());
	}
	
	/**
	 * Inicia a Consulta de Convênios de Estágios
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String iniciarConsulta() throws SegurancaException, HibernateException, DAOException {
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO, 
				SigaaPapeis.GESTOR_CONVENIOS_ESTAGIO_PROPLAN, SigaaPapeis.PORTAL_CONCEDENTE_ESTAGIO, SigaaPapeis.COORDENADOR_ESTAGIOS);
				
		pendentesAnalise = new ArrayList<ConvenioEstagio>();
		/* Busca todos os Convênios referente ao Usuário do Coord. 
		 * logado que estão com status SUBMETIDO (Pendente de Análise) */
		if (isPortalCoordenadorGraduacao())
			pendentesAnalise = getConveniosPendentesAnalise(getUsuarioLogado());
		else if (isPortalConcedenteEstagio()){
			responsavel = getUsuarioLogado().getPessoa().getCpf_cnpjString();
			status.setId(StatusConvenioEstagio.APROVADO);
			return buscar();						
		}
		return telaConsulta();
	}
	
	/**
	 * Inicia a consulta a partir de um comando
	 * <br/><br/>
	 * Método não chamado por JSP's.
	 * @param comando
	 * @return
	 */
	public String iniciarConsulta(ComandoConvenioEstagio comando){
		this.comandoConvenio = comando; 
		return telaConsulta();
	}
	
	/**
	 * Retorna os Convênios de Estágio Pendentes de Análise
	 * @param usuario
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	private Collection<ConvenioEstagio> getConveniosPendentesAnalise(Usuario usuario) throws HibernateException, DAOException {
		ConvenioEstagioDao dao = getDAO(ConvenioEstagioDao.class);
		Collection<StatusConvenioEstagio> status = new LinkedList<StatusConvenioEstagio>();
		status.add(new StatusConvenioEstagio(StatusConvenioEstagio.SUBMETIDO, "SUBMETIDO"));
		status.add(new StatusConvenioEstagio(StatusConvenioEstagio.EM_ANALISE, "EM ANÁLISE"));
		return dao.findConvenioGeral(null, null, usuario, status, false, null, null, null);
	}
	
	/**
	 * Busca Convênios de Estágios Cadastrados conforme os parâmetros informados.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/convenio_estagio/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String buscar() throws HibernateException, DAOException{
		pendentesAnalise = new LinkedList<ConvenioEstagio>();
		if (modoSeletor)
			filtroStatus = true;
		
		if (isPortalConcedenteEstagio()){
			responsavel = getUsuarioLogado().getPessoa().getCpf_cnpjString();
			status.setId(StatusConvenioEstagio.APROVADO);
			filtroStatus = filtroResponsavel = true;
		}
		
		if (!filtroEmpresa && !filtroResponsavel && !filtroStatus && !isPortalConcedenteEstagio() && !filtroNumConvenio && !filtroCpfCnpj){
			addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
		}
		
		if (filtroEmpresa && ValidatorUtil.isEmpty(empresa)){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Concedente");
		}
		
		if (filtroCpfCnpj && ValidatorUtil.isEmpty(cpfCnpj)){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "CPF/CNPJ");
		}
		
		if (filtroResponsavel && ValidatorUtil.isEmpty(responsavel)){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Responsável");
		}
		
		if (filtroNumConvenio && ValidatorUtil.isEmpty(numeroConvenio)){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Número do Convênio");
		}
		
		if (hasErrors())
			return null;
		if (comandoConvenio == ComandoConvenioEstagio.ANALISAR_CONVENIO) {
			pendentesAnalise = getConveniosPendentesAnalise(null);
		} 
		
		filtrarConsulta();

		return telaConsulta();
	}
	
	/**
	 * Atualiza a Consulta forme os parâmetros passados na busca.
	 * @throws HibernateException
	 * @throws DAOException
	 */
	private void filtrarConsulta() throws HibernateException, DAOException{
		ConvenioEstagioDao dao = getDAO(ConvenioEstagioDao.class);
		Collection<Integer> tipoOfertaVagas = new LinkedList<Integer>();
		if (filtrarTipoOfertaVagas) {
			if (isPortalCoordenadorGraduacao()) {
				tipoOfertaVagas.add(TipoOfertaVaga.OFERTADO_PELA_COORDENACAO.ordinal());
			} else if (isPortalConcedenteEstagio()) {
				tipoOfertaVagas.add(TipoOfertaVaga.OFERTADO_POR_EMPRESA.ordinal());
			}
			tipoOfertaVagas.add(TipoOfertaVaga.OFERTADO_POR_EMPRESA_COORDENACAO.ordinal());
		}
		listaConvenios = dao.findConvenioGeral(
					(filtroEmpresa ? empresa : null), 
					(filtroResponsavel || isPortalConcedenteEstagio() ? responsavel : null),
					null, 
					(filtroStatus || isPortalConcedenteEstagio() || isCadastroOferta() ? status : null), 
					(comandoConvenio != null && comandoConvenio.equals(ComandoConvenioEstagio.CADASTRO_ESTAGIO_AVULSO)),
					tipoOfertaVagas,
					(filtroCpfCnpj ? cpfCnpj : null),
					(filtroNumConvenio ? numeroConvenio : null));
		if (!isEmpty(listaConvenios)) {
			Iterator<ConvenioEstagio> iterator = listaConvenios.iterator();
			while (iterator.hasNext()) {
				if (pendentesAnalise.contains(iterator.next()))
					iterator.remove();
			}
		}
	}
	
	/**
	 * Redireciona para o Formulário de Cadastro
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/convenio_estagio/view.jsp</li>
	 * </ul>
	 * @return
	 */
	public String telaForm(){
		return forward(getFormPage());
	}
	
	/**
	 * Redireciona para a tela de Consulta.
	 * <br/><br/>
	 * Método não chamado por JSP.
	 * @return
	 */
	public String telaConsulta(){
		return forward(getListPage());
	}
	
	/**
	 * Volta para o Form de origem.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/convenio_estagio/view.jsp</li>
	 * </ul>
	 * @return
	 */	
	public String voltar(){
		if (isCadastro())
			return telaForm();
		return telaConsulta(); 
	}
	
	/**
	 * Visualiza os Dados do Convênio de Estágio e valida os dados do cadastro
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/convenio_estagio/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String proximoPasso() throws DAOException{
		int idTipoConvenio = getParameterInt("tipoConvenio", 0);
		obj.getTipoConvenio().setId(idTipoConvenio);
		
		if (!validaDados())
			return null;
				
		carregaObjetos();
		
		if (isConvenioEstagio())
			return forward("/estagio/convenio_estagio/form_analise.jsp");
		else
			return forward(getViewPage());
	}
	
	
	/**
	 * Carrega os Objetos dos dados Pessoais do Concedente de Estágio
	 * @throws DAOException
	 */
	private void carregaObjetos() throws DAOException{
		obj.setTipoConvenio(getGenericDAO().refresh(obj.getTipoConvenio()));
		
		obj.getConcedente().getPessoa().getEnderecoContato().setMunicipio(
				getGenericDAO().refresh(obj.getConcedente().getPessoa().getEnderecoContato().getMunicipio()));
		
		obj.getConcedente().getPessoa().getEnderecoContato().setUnidadeFederativa(
				getGenericDAO().refresh(obj.getConcedente().getPessoa().getEnderecoContato().getUnidadeFederativa()));
			
		obj.getConcedente().getPessoa().setUnidadeFederativa(obj.getConcedente().getPessoa().getEnderecoContato().getUnidadeFederativa());
		obj.getConcedente().getPessoa().setMunicipio(obj.getConcedente().getPessoa().getEnderecoContato().getMunicipio());
		if (obj.isAgenteInterno() && isEmpty(obj.getConcedente().getUnidade()))
			obj.getConcedente().setUnidade(new Unidade());
	}
	
	/**
	 * Valida os dados de cadastro do Convênio de Estágio
	 * @return
	 */
	private boolean validaDados() {
		erros = new ListaMensagens();
		erros.addAll(obj.validate().getMensagens());
		
		erros.addAll(concedenteEstagioPessoa.validate().getMensagens());

		if (hasErrors())
			return false;
		
		return true;
	}
	
	/**
	 * Visualiza os dados do Convênio de Estágio
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/convenio_estagio/include/_lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String view() throws ArqException{
		if (ValidatorUtil.isEmpty(obj)){
			addMensagemErro("Objeto não selecionado");
			return null;
		}		
		populateObj();
		concedenteEstagioPessoa = obj.getConcedente().getResponsavel();
		obj.setConcedente(getGenericDAO().refresh(obj.getConcedente()));
		try {
			if (obj.getIdArquivoTermoConvenio() != null) {
				String nomeArquivo = EnvioArquivoHelper.recuperaNomeArquivo(obj.getIdArquivoTermoConvenio());
				getCurrentResponse().setContentType("application/pdf");
				getCurrentResponse().addHeader("Content-Disposition", "attachment; filename=" + nomeArquivo);
				ServletOutputStream os = getCurrentResponse().getOutputStream();
				EnvioArquivoHelper.recuperaArquivo(os, obj.getIdArquivoTermoConvenio());
				if (FacesContext.getCurrentInstance() != null)
					FacesContext.getCurrentInstance().responseComplete();
				return null;
			} else {
				Integer idPessoa =  ParametroHelper.getInstance().getParametroInt( ConstantesParametroGeral.GESTOR_INSTITUICAO );
				reitor = getGenericDAO().findByPrimaryKey(idPessoa, Pessoa.class);
				idPessoa =  ParametroHelper.getInstance().getParametroInt( ParametrosGraduacao.ID_PESSOA_DIRETOR_DAE );
				proReitor = getGenericDAO().findByPrimaryKey(idPessoa, Pessoa.class);
				return forward("/estagio/convenio_estagio/termo_convenio.jsp");
			}
		} catch (IOException e) {
			throw new ArqException(e);
		}
	}
	
	/**
	 * Efetua o Cadastro do Convênio de Estágio
	 * <br/><br/>
	 * Método Chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/estagio/convenio_estagio/include/form_analise.jsp</li>
	 * 	 <li>/sigaa.war/estagio/convenio_estagio/view.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		
		validarConvenio();
		
		if (hasErrors())
			return null;		
		
		try {								
			// Prepara o movimento, setando o objeto
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setCodMovimento(getUltimoComando());
			mov.setObjAuxiliar(arquivoTermoConvenio);
			mov.setRegistroEntrada(getRegistroEntrada());
			
			 if (getUltimoComando().equals(SigaaListaComando.CADASTRAR_CONVENIO_ESTAGIO)
			 ||getUltimoComando().equals(SigaaListaComando.ATUALIZAR_CONVENIO_ESTAGIO))
					mov.setObjAuxiliar(concedenteEstagioPessoa);			
			
			execute(mov);
			
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO, TipoMensagemUFRN.INFORMATION);		
			
			pendentesAnalise = getConveniosPendentesAnalise(isConvenioEstagio() ? null : getUsuarioLogado());
			if (analiseConvenios){
				if (!isEmpty(pendentesAnalise)) {
					if (isConvenioEstagio()) { 
						return iniciarAnalise();
					} else {
						return cancelar();
					}
				} else {
					filtrarConsulta();
					return iniciarConsulta();
				}
			} else if (getUltimoComando().equals(SigaaListaComando.ATUALIZAR_CONVENIO_ESTAGIO)){
				filtrarConsulta();
				return iniciarConsulta();
			} else
				return cancelar();
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return telaForm();
		}
	}

	/**
	 * Valida o Convênio de Estágio
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	private void validarConvenio() throws HibernateException, DAOException {
		validateRequired(obj.getStatus(), "Situação", erros);
		if (obj.isAprovado()){
			if (ValidatorUtil.isEmpty(obj.getNumeroConvenio()))
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Número do Convênio");
		} else if (obj.isRecusado()){
			if (ValidatorUtil.isEmpty(obj.getMotivoAnalise()))
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Motivo da Análise");				
		}
		ConvenioEstagioDao dao = getDAO(ConvenioEstagioDao.class);
		if (obj.getId() == 0 &&
				dao.findConvenioAtivobyConcedenteTipo(obj.getConcedente().getPessoa(), obj.getTipoConvenio().getId()).size() > 0)
			addMensagemErro("O Concedente informado já possui uma Solicitação de Convênio Ativa para o tipo de Convênio informado");
		if (arquivoTermoConvenio != null) {
			if (!arquivoTermoConvenio.getContentType().toLowerCase().contains("pdf"))
				addMensagemErro("O arquivo enviado não está no formato PDF.");
		}
	}
	
	/**
	 * Busca a Empresa pelo CNPJ
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/convenio_estagio/form.jsp</li>
	 * </ul>
	 * @param e
	 * @throws DAOException
	 */
	public void buscarCNPJ(ActionEvent e) throws DAOException{
		Long cnpj = StringUtils.extractLong(String.valueOf(obj.getConcedente().getPessoa().getCpf_cnpj()));
		PessoaDao dao = null;
		if (cnpj != null && cnpj > 0){
			dao = getDAO(PessoaDao.class);
			Pessoa p = dao.findByCpf(cnpj, false);
			if (p != null){
				p.prepararDados();
				obj.getConcedente().setPessoa(p);
				podeAlterarNomeConcedente = true;
			}
		} else {
			obj.getConcedente().setPessoa(new Pessoa());
			podeAlterarNomeConcedente = true;
		}
		obj.getConcedente().getPessoa().prepararDados();
	}
	
	/**
	 * Busca o Responsável pelo CPF
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/convenio_estagio/form.jsp</li>
	 * </ul>
	 * @param e
	 * @throws DAOException
	 */
	public void buscarCPF(ActionEvent e) throws DAOException{
		Long cpf = StringUtils.extractLong(String.valueOf(concedenteEstagioPessoa.getPessoa().getCpf_cnpj()));
		PessoaDao dao = null;
		podeAlterarNomeResponsavel = true;
		if (cpf != null && cpf > 0){
			dao = getDAO(PessoaDao.class);
			Pessoa p = dao.findByCpf(cpf, false);
			if (p != null){
				concedenteEstagioPessoa.setPessoa(p);
				concedenteEstagioPessoa.getPessoa().prepararDados();
			}
		} else {
			concedenteEstagioPessoa.setPessoa(new Pessoa());
		}			
	}		
	
	/**
	 * Retorna os Tipos de Convênio em formato de Combo
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/convenio_estagio/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getTipoConvenioCombo() throws DAOException{
		return toSelectItems(getGenericDAO().findAll(TipoConvenio.class), "id", "descricao");		
	}
	
	/** Listener responsável por carregar a lista de municípios de naturalidade ou endereço, caso o valor
	 * da UF seja alterado no formulário.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/geral/pessoa/dados_pessoais.jsp</li>
	 *	</ul>
	 * @param e
	 * @throws DAOException
	 */
	public void carregarMunicipios(ValueChangeEvent e) throws DAOException {
		String selectId = e.getComponent().getId();
		if (selectId != null && e.getNewValue() != null) {
			Integer ufId = (Integer) e.getNewValue();
			carregarMunicipiosEndereco(ufId);
		}
	}
	
	/**  
	 * Carrega a lista de municípios de endereço de uma determinada UF.
	 * <br><br>
	 * Método não invocado por JSP
	 * @param idUf
	 * @throws DAOException
	 */
	private void carregarMunicipiosEndereco(Integer idUf) throws DAOException {
		if ( idUf == null || idUf == 0) 
			idUf = obj.getConcedente().getPessoa().getEnderecoContato().getUnidadeFederativa().getId() == 0
			? UnidadeFederativa.ID_UF_PADRAO : obj.getConcedente().getPessoa().getEnderecoContato().getUnidadeFederativa().getId();

		MunicipioDao dao = getDAO(MunicipioDao.class);
		UnidadeFederativa uf = dao.findByPrimaryKey(idUf, UnidadeFederativa.class);
		Collection<Municipio> municipios = dao.findByUF(idUf);
		obj.getConcedente().getPessoa().getEnderecoContato().setUnidadeFederativa(uf);
		municipiosEndereco = new ArrayList<SelectItem>(0);
		municipiosEndereco.add(new SelectItem(uf.getCapital().getId(), uf.getCapital().getNome()));
		municipiosEndereco.addAll(toSelectItems(municipios, "id", "nome"));
		if (!isEmpty(obj.getConcedente().getPessoa().getEnderecoContato().getMunicipio()))
			obj.getConcedente().getPessoa().getEnderecoContato().setMunicipio(uf.getCapital());			
	}
	
	/**
	 * Verifica se o usuário pode analisar a solicitação de convênio de estágio
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/convenio_estagio/include/_lista.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isPermiteAnalisarConvenio(){
		return isUserInRole(SigaaPapeis.GESTOR_CONVENIOS_ESTAGIO_PROPLAN); 
	}
	
	/** Retorna uma coleção de select itens de tipos de ofertas de vaga.
	 * @return
	 */
	public Collection<SelectItem> getTipoOfertaVagaCombo(){
		Collection<SelectItem> lista = new LinkedList<SelectItem>();
		for (TipoOfertaVaga tipo : TipoOfertaVaga.values())
			lista.add(new SelectItem(tipo.name(), tipo.toString()));
		return lista;
	}
	
	@Override
	public String getFormPage() {
		return "/estagio/convenio_estagio/form.jsf";
	}
	
	@Override
	public String getViewPage() {
		return "/estagio/convenio_estagio/view.jsf";
	}
	
	@Override
	public String getListPage() {
		return "/estagio/convenio_estagio/lista.jsf";
	}

	public boolean isCadastro() {
		return cadastro;
	}

	public void setCadastro(boolean cadastro) {
		this.cadastro = cadastro;
	}

	public Collection<ConvenioEstagio> getListaConvenios() {
		return listaConvenios;
	}

	public void setListaConvenios(Collection<ConvenioEstagio> listaConvenios) {
		this.listaConvenios = listaConvenios;
	}

	public StatusConvenioEstagio getStatus() {
		return status;
	}

	public void setStatus(StatusConvenioEstagio status) {
		this.status = status;
	}

	public Collection<ConvenioEstagio> getPendentesAnalise() {
		return pendentesAnalise;
	}

	public void setPendentesAnalise(Collection<ConvenioEstagio> pendentesAnalise) {
		this.pendentesAnalise = pendentesAnalise;
	}

	public boolean isFiltroStatus() {
		return filtroStatus;
	}

	public void setFiltroStatus(boolean filtroStatus) {
		this.filtroStatus = filtroStatus;
	}

	public boolean isFiltroEmpresa() {
		return filtroEmpresa;
	}

	public void setFiltroEmpresa(boolean filtroEmpresa) {
		this.filtroEmpresa = filtroEmpresa;
	}

	public boolean isFiltroResponsavel() {
		return filtroResponsavel;
	}

	public void setFiltroResponsavel(boolean filtroResponsavel) {
		this.filtroResponsavel = filtroResponsavel;
	}

	public String getEmpresa() {
		return empresa;
	}

	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}

	public String getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}

	public Collection<TipoConvenio> getListaTiposConvenio() {
		return listaTiposConvenio;
	}

	public void setListaTiposConvenio(Collection<TipoConvenio> listaTiposConvenio) {
		this.listaTiposConvenio = listaTiposConvenio;
	}

	public ConcedenteEstagioPessoa getConcedenteEstagioPessoa() {
		return concedenteEstagioPessoa;
	}

	public void setConcedenteEstagioPessoa(
			ConcedenteEstagioPessoa concedenteEstagioPessoa) {
		this.concedenteEstagioPessoa = concedenteEstagioPessoa;
	}

	public boolean isCadastroOferta() {
		return cadastroOferta;
	}

	public void setCadastroOferta(boolean cadastroOferta) {
		this.cadastroOferta = cadastroOferta;
	}

	public Collection<SelectItem> getMunicipiosEndereco() {
		return municipiosEndereco;
	}

	public void setMunicipiosEndereco(Collection<SelectItem> municipiosEndereco) {
		this.municipiosEndereco = municipiosEndereco;
	}

	public ComandoConvenioEstagio getComandoConvenio() {
		return comandoConvenio;
	}

	public void setComandoConvenio(ComandoConvenioEstagio comandoConvenio) {
		this.comandoConvenio = comandoConvenio;
	}

	public Estagiario getEstagiario() {
		return estagiario;
	}

	public void setEstagiario(Estagiario estagiario) {
		this.estagiario = estagiario;
	}

	public Pessoa getReitor() {
		return reitor;
	}

	public void setReitor(Pessoa reitor) {
		this.reitor = reitor;
	}

	public boolean isPodeAlterarNomeResponsavel() {
		return podeAlterarNomeResponsavel;
	}

	public void setPodeAlterarNomeResponsavel(boolean podeAlterarNomeResponsavel) {
		this.podeAlterarNomeResponsavel = podeAlterarNomeResponsavel;
	}

	public boolean isPodeAlterarNomeConcedente() {
		return podeAlterarNomeConcedente;
	}

	public void setPodeAlterarNomeConcedente(boolean podeAlterarNomeConcedente) {
		this.podeAlterarNomeConcedente = podeAlterarNomeConcedente;
	}

	public UploadedFile getArquivoTermoConvenio() {
		return arquivoTermoConvenio;
	}

	public void setArquivoTermoConvenio(UploadedFile arquivoTermoConvenio) {
		this.arquivoTermoConvenio = arquivoTermoConvenio;
	}

	/** Inicia a seleção de estágio.
	 * <br/> Método não invocado por JSP's
	 * @param mBean
	 * @param statusConvenioEstagio
	 * @return
	 * @throws DAOException 
	 */
	public String iniciarSeletorEstagio(SeletorConvenioEstagio mBean, Integer[] statusConvenioEstagio) throws DAOException {
		this.mBean = mBean;
		this.statusConvenioEstagio = statusConvenioEstagio;
		if (statusConvenioEstagio != null && statusConvenioEstagio.length == 1)
			status = getGenericDAO().findByPrimaryKey(statusConvenioEstagio[0], StatusConvenioEstagio.class);
		this.modoSeletor = true;
		this.filtroStatus = true;
		return telaConsulta();
	}
	
	/** Retorna uma lista de selecitems de status de convênios de acordo com o caso de uso.
	 * @return
	 * @throws ArqException
	 */
	public Collection<SelectItem> getStatusConvenioEstagioPreDefinidosCombo() throws ArqException {
		StatusConvenioEstagioMBean mBean = getMBean("statusConvenioEstagioMBean");
		if (isEmpty(statusConvenioEstagio)) {
			return mBean.getAllCombo();
		} else {
			Collection<StatusConvenioEstagio> situacoes = new LinkedList<StatusConvenioEstagio>();
			Collection<StatusConvenioEstagio> allStatus = mBean.getAll();
			for (Integer id : statusConvenioEstagio)
				for (StatusConvenioEstagio status : allStatus) 
					if (status.getId() == id)
						situacoes.add(status);
			return toSelectItems(situacoes, "id", "descricao");
		}
	}

	public boolean isModoSeletor() {
		return modoSeletor;
	}

	public void setModoSeletor(boolean modoSeletor) {
		this.modoSeletor = modoSeletor;
	}

	public SeletorConvenioEstagio getMBean() {
		return mBean;
	}

	public void setMBean(SeletorConvenioEstagio mBean) {
		this.mBean = mBean;
	}

	public boolean isAnaliseConvenios() {
		return analiseConvenios;
	}

	public void setAnaliseConvenios(boolean analiseConvenios) {
		this.analiseConvenios = analiseConvenios;
	}

	public Pessoa getProReitor() {
		return proReitor;
	}

	public void setProReitor(Pessoa proReitor) {
		this.proReitor = proReitor;
	}

	public boolean isFiltroCpfCnpj() {
		return filtroCpfCnpj;
	}

	public void setFiltroCpfCnpj(boolean filtroCpfCnpj) {
		this.filtroCpfCnpj = filtroCpfCnpj;
	}

	public boolean isFiltroNumConvenio() {
		return filtroNumConvenio;
	}

	public void setFiltroNumConvenio(boolean filtroNumConvenio) {
		this.filtroNumConvenio = filtroNumConvenio;
	}

	public long getCpfCnpj() {
		return cpfCnpj;
	}

	public void setCpfCnpj(long cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
	}

	public String getNumeroConvenio() {
		return numeroConvenio;
	}

	public void setNumeroConvenio(String numeroConvenio) {
		this.numeroConvenio = numeroConvenio;
	}
}
