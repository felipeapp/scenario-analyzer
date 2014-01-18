/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 20/10/2010
 */

package br.ufrn.sigaa.estagio.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateMinValue;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
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
import br.ufrn.arq.seguranca.autenticacao.AutValidator;
import br.ufrn.arq.seguranca.autenticacao.AutenticacaoUtil;
import br.ufrn.arq.seguranca.autenticacao.EmissaoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.SubTipoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.TipoDocumentoAutenticado;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidadorCPFCNPJ;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.estagio.EstagiarioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoAtividade;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.estagio.dominio.ConcedenteEstagio;
import br.ufrn.sigaa.estagio.dominio.ConvenioEstagio;
import br.ufrn.sigaa.estagio.dominio.Estagiario;
import br.ufrn.sigaa.estagio.dominio.HorarioEstagio;
import br.ufrn.sigaa.estagio.dominio.InteresseOferta;
import br.ufrn.sigaa.estagio.dominio.ParametrosEstagio;
import br.ufrn.sigaa.estagio.dominio.StatusConvenioEstagio;
import br.ufrn.sigaa.estagio.dominio.StatusEstagio;
import br.ufrn.sigaa.estagio.dominio.TipoConvenio;
import br.ufrn.sigaa.estagio.dominio.TipoEstagio;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Este MBean tem como finalidade de auxiliar nas opera��es relacionadas ao Cadastro de Est�gio
 * 
 * @author Arlindo Rodrigues
 *
 */
@Component("estagioMBean") @Scope("session")
public class EstagioMBean extends SigaaAbstractController<Estagiario> implements AutValidator, OperadorDiscente, SeletorConvenioEstagio {
	
	/** MBean de Busca de Est�gio que auxilia no redirecionamento */
	@Autowired
	private BuscaEstagioMBean buscaEstagioMBean;
	
	/** MBean de Busca de Conv�nio de Est�gio que auxilia no redirecionamento */
	@Autowired
	private ConvenioEstagioMBean convenioEstagioMBean;
	
	/** Interesse do Discente que ser� Estagi�rio */
	private InteresseOferta interesseOferta;
	
	/** Indica se � cadastro ou altera��o */
	private boolean cadastro;
	
	/** Coordenador do Curso do Estagi�rio Selecionado */
	private Servidor coordenador;
	
	/** Dura��o do Est�gio anos/meses */
	private long duracaoEstagio;
	
	/** Status Referente ao Parecer do Est�gio */
	private StatusEstagio statusParecer;
	/** Observa��es Referente ao Parecer do Est�gio */
	private String obsParecer;
	
	/** Guarda o c�digo que vai autenticar o documento do SIGAA. */
	private String codigoSeguranca;
	
	/** Guarda o c�digo de infica��o do Documento. */
	private String identificadorDocumento;	
	
	/** Mant�m uma c�pia do comprovante para evitar gerar mais de uma vez se o usu�rio ficar atualizando a p�gina. */
	private EmissaoDocumentoAutenticado comprovante;		
	
	/** Discente selecionado */
	private DiscenteAdapter discente;
	
	/** Documento que ser� emitido */
	private int tipoDocumento;	

	/** Hor�rios do est�gio do discente no per�odo matutino. */
	private Map<String,HorarioEstagio> horarioMatutino;
	
	/** Hor�rios do est�gio do discente no per�odo vespertino. */
	private Map<String,HorarioEstagio> horarioVespertino;
	
	/** Hor�rios do est�gio do discente no per�odo noturno. */
	private Map<String,HorarioEstagio> horarioNoturno;

	/** Tipos de est�gio selecion�veis pelo usu�rio. */
	private List<SelectItem> tipoEstagioCombo;

	/** Indica se o usu�rio pode alterar a empresa. */
	private boolean podeAlterarEmpresaEstagio;
	
	/**
	 * Inicializa os Objetos  
	 */
	private void initObj(){
		if (isEmpty(obj))
			obj = new Estagiario();
		
		if (isEmpty(obj.getDiscente()))
			obj.setDiscente(new Discente());
		
		if (isEmpty(obj.getTipoEstagio()))
			obj.setTipoEstagio(new TipoEstagio());
		
		if (isEmpty(obj.getInteresseOferta()))
			obj.setInteresseOferta(new InteresseOferta());
		
		if (isEmpty(obj.getOrientador()))
			obj.setOrientador(new Servidor());
		
		if (isEmpty(obj.getSupervisor()))
			obj.setSupervisor(new Pessoa());
		
		if (isEmpty(obj.getEmpresaEstagio()))
			obj.setEmpresaEstagio(new Pessoa());
		
		cadastro = false;		
		statusParecer = new StatusEstagio();
		
		// hor�rios dos est�gios
		horarioMatutino = new TreeMap<String, HorarioEstagio>();
		horarioVespertino = new TreeMap<String, HorarioEstagio>();
		horarioNoturno = new TreeMap<String, HorarioEstagio>();
		Calendar cal = Calendar.getInstance();
		for (HorarioEstagio horario : obj.getHorariosEstagio()) {
			cal.setTime(horario.getHoraInicio());
			if (cal.get(Calendar.HOUR_OF_DAY) < 12)
				horarioMatutino.put(String.valueOf(horario.getDia()), horario);
			else if (cal.get(Calendar.HOUR_OF_DAY) < 18)
				horarioVespertino.put(String.valueOf(horario.getDia()), horario);
			else
				horarioNoturno.put(String.valueOf(horario.getDia()), horario);
		}
		// hor�rios que n�o foram definidos
		for(char dia = '2'; dia <= '7'; dia++){
			if (!horarioMatutino.containsKey(String.valueOf(dia)))
				horarioMatutino.put(String.valueOf(dia), new HorarioEstagio(0, dia));
			if (!horarioVespertino.containsKey(String.valueOf(dia)))
				horarioVespertino.put(String.valueOf(dia), new HorarioEstagio(0, dia));
			if (!horarioNoturno.containsKey(String.valueOf(dia)))
				horarioNoturno.put(String.valueOf(dia), new HorarioEstagio(0, dia));
		}
	}
	
	/** Construtor Padr�o */
	public EstagioMBean() {
	    initObj();
	}
	
	/**
	 * Redireciona para o formul�rio de cadastro
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/estagio/view.jsp</li>
	 * </ul>
	 * @return
	 */
	public String telaForm(){
		if (isCadastro())
			return forward(getFormPage());
		else
			return forward("/estagio/estagio/lista.jsp");
	}
	
	/**
	 * Valida os dados preenchidos no cadastro e redireciona para a 
	 * Visualiza��o e confirma��o do cadastro. 
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/estagio/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String proximoPasso() throws DAOException{
		
		obj.setDiscente(getGenericDAO().refresh( discente ));
		
		if (!validaEstagio())
			return null;
		
		obj.setTipoEstagio(getGenericDAO().refresh(obj.getTipoEstagio()));
		
    	statusParecer = getGenericDAO().refresh(statusParecer);
    	
    	obj.setStatus(statusParecer);
    	
    	// define os hor�rios do est�gio
    	obj.getHorariosEstagio().clear();
    	for (HorarioEstagio horario : horarioMatutino.values())
    		if (!horario.validate().isErrorPresent())
    			obj.addHorarioEstagio(horario);
    	for (HorarioEstagio horario : horarioVespertino.values())
    		if (!horario.validate().isErrorPresent())
    			obj.addHorarioEstagio(horario);
    	for (HorarioEstagio horario : horarioNoturno.values())
    		if (!horario.validate().isErrorPresent())
    			obj.addHorarioEstagio(horario);
    	Collections.sort(obj.getHorariosEstagio());
		return forward(getViewPage());
	}
	
	/**
	 * Visualiza os dados do Est�gio selecionado
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/estagio/include/_lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String view() throws DAOException{
		populateObj();
		return forward(getViewPage());		
	}
	
	/**
	 * Inicia a altera��o do est�gio
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/estagio/include/_lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String alterar() throws ArqException{
		populateObj();
		prepareMovimento(SigaaListaComando.ALTERAR_ESTAGIO);		
		cadastro = true;
		statusParecer = new StatusEstagio( obj.getStatus().getId() );
		discente = obj.getDiscente();
		if (obj.getTipoEstagio() == null)
			obj.setTipoEstagio(new TipoEstagio());
		if (obj.getOrientador() == null)
			obj.setOrientador(new Servidor());
		else
			obj.getOrientador().getNome();
		if (obj.getInteresseOferta() != null && obj.getInteresseOferta().getOferta() != null)
			obj.getInteresseOferta().getOferta().getStatus().getDescricao();
		return telaForm();		
	}
	
	
	/** Inicia o cadastro de est�gio avulso, sem ter oferta e indica��o<br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCadastroAvulso() throws ArqException{
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.GESTOR_CONVENIOS_ESTAGIO_PROPLAN, SigaaPapeis.COORDENADOR_ESTAGIOS);
		initObj();
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");		
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.CADASTRO_ESTAGIO_AVULSO);
		buscaDiscenteMBean.setNivelEnsinoEspecifico(NivelEnsino.GRADUACAO);
		return buscaDiscenteMBean.popular();			
	}

	/**
	 * Inicializa o form de cadastro de est�gio avulso
	 * <br/><br/>
	 * M�todo n�o chamado por JSP's.
	 * @return
	 * @throws ArqException 
	 */
	public String carregarCadastroAvulso() throws ArqException{
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.GESTOR_CONVENIOS_ESTAGIO_PROPLAN);
		
		prepareMovimento(SigaaListaComando.CADASTRAR_ESTAGIO_AVULSO);		
		statusParecer = new StatusEstagio(StatusEstagio.APROVADO);
		cadastro = true;				
		discente = obj.getDiscente();
		
		return telaForm();
	}
	
	/**
	 * Inicia a altera��o do est�gio
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/estagio/include/_lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String efetivarEstagio() throws ArqException{
		
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.GESTOR_CONVENIOS_ESTAGIO_PROPLAN);
		
		populateObj();
		initObj();

		// Evitar lazy..
		obj.getOrientador().getPessoa();
		discente = obj.getDiscente();
		if (obj.getInteresseOferta().getOferta().getInteresses() != null)
			obj.getInteresseOferta().getOferta().getInteresses().iterator();
		if (obj.getInteresseOferta().getOferta().getCursosOfertados() != null)
			obj.getInteresseOferta().getOferta().getCursosOfertados().iterator();
		obj.getInteresseOferta().getOferta().getConcedente().getPessoa().getNome();
		
		if (!discente.getCurso().equals(getCursoAtualCoordenacao())) {
			addMensagemErro("O discente n�o � do curso atualmente coordenado.");
			return null;
		}
		
		prepareMovimento(SigaaListaComando.ANALISAR_ESTAGIO);
		
		cadastro = true;
		tipoEstagioCombo = null;
		
		if (!obj.getConcedente().getConvenioEstagio().isAgenteIntegrador())
			obj.setEmpresaEstagio(obj.getConcedente().getPessoa());
		else
			obj.setEmpresaEstagio(new Pessoa());
		
		return telaForm();		
	}	
	
	/**
	 * Cadastra o est�gio
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/estagio/view.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		try {					
			if (getUltimoComando().equals(SigaaListaComando.CADASTRAR_ESTAGIO_AVULSO) ||
					getUltimoComando().equals(SigaaListaComando.ANALISAR_ESTAGIO) ||
					getUltimoComando().equals(SigaaListaComando.SOLICITAR_CANCELAMENTO_ESTAGIO) || 
					getUltimoComando().equals(SigaaListaComando.CANCELAR_ESTAGIO)){
				
				if ((getUltimoComando().equals(SigaaListaComando.SOLICITAR_CANCELAMENTO_ESTAGIO) || 
						getUltimoComando().equals(SigaaListaComando.CANCELAR_ESTAGIO)) &&
						isEmpty(obj.getMotivoCancelamento())){
					addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Motivo do Cancelamento");
				}
			}
			if (!confirmaSenha())
				return null;
			// caso altere o status do est�gio para cancelado, sem alterar a data.
			if (obj.isCancelado() && obj.getDataCancelamento() == null){
				obj.setDataCancelamento(new Date());
			}
			// Prepara o movimento, setando o objeto
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setCodMovimento(getUltimoComando());
			execute(mov);
			
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO, TipoMensagemUFRN.INFORMATION);	
						
			if (getUltimoComando().equals(SigaaListaComando.CADASTRAR_ESTAGIO_AVULSO) ||
					getUltimoComando().equals(SigaaListaComando.ANALISAR_ESTAGIO)){
				buscaEstagioMBean.carregaEstagiosPendentes();				
			}
			return buscaEstagioMBean.filtrar();			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return telaForm();
		}				
	}	
	
	/**
	 * Carrega os dados para exibi��o dos Relat�rios
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/estagio/include/_lista.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 */
	private void carregarRelatorios() throws ArqException{			
		coordenador = getCoordenadorCurso(obj.getDiscente().getCurso());
		
		duracaoEstagio = CalendarUtils.calculaQuantidadeMesesEntreDatasIntervaloFechado(obj.getDataInicio(), obj.getDataFim());
				
		obj = getGenericDAO().refresh(obj);	
		
		/* *************************************************************************************
		 *  Seguran�a inserida para caso o usu�rio tente acessar a p�gina do documento diretamente.
		 * *************************************************************************************/
		getCurrentRequest().setAttribute("liberaEmissao", true);	
		getCurrentRequest().setAttribute("estagioMBean", this);		
	}
	
	/**
	 * Iniciar o Cancelamento do Est�gio
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/estagio/include/_lista.jsp</li>
	 * </ul> 
	 * @return
	 * @throws ArqException
	 */
	public String cancelarEstagio() throws ArqException {
		populateObj();
		if (isPortalCoordenadorGraduacao())
			prepareMovimento(SigaaListaComando.CANCELAR_ESTAGIO);
		else 
			prepareMovimento(SigaaListaComando.SOLICITAR_CANCELAMENTO_ESTAGIO);		
		return forward("/estagio/estagio/form_cancelamento.jsp");
	}
	
	/**
	 * Exibe o Termo de Compromisso.
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/estagio/include/_lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws Exception 
	 */
	public String exibirTermoCompromisso() throws Exception{
		populateObj();

		tipoDocumento = SubTipoDocumentoAutenticado.TERMO_COMPROMISSO_ESTAGIO;		
		codigoSeguranca = null;
		identificadorDocumento = String.valueOf(obj.getId() + obj.getDiscente().getMatricula());
		
		gerarCodigoSeguranca();
		
		carregarRelatorios();	

		return forward(getRelatorioTermoCompromisso());
	}
	
	/**
	 * Busca o Supervisor pelo CPF
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/estagio/form.jsp</li>
	 * </ul>
	 * @param e
	 * @throws DAOException
	 */
	public void buscarCPF(ActionEvent e) throws DAOException{
		Long cpf = StringUtils.extractLong(String.valueOf(obj.getSupervisor().getCpf_cnpj()));
		PessoaDao dao = null;
		try {
			if (cpf != null && cpf > 0){
				dao = getDAO(PessoaDao.class);
				obj.setSupervisor( dao.findByCpf(cpf) );
				if (ValidatorUtil.isEmpty( obj.getSupervisor() ))
					obj.setSupervisor( new Pessoa() );
			}
		} finally {
			if (dao != null)
				dao.close();
		}		
	}	
	
	
	/**
	 * Exibe a Rescis�o do Termo de Compromisso.
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/estagio/include/_lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws Exception 
	 */
	public String exibirRescisaoEstagio() throws Exception{	
		populateObj();
		tipoDocumento = SubTipoDocumentoAutenticado.TERMO_RESCISAO_ESTAGIO;
		
		codigoSeguranca = null;
		identificadorDocumento = String.valueOf(obj.getId() + obj.getDiscente().getMatricula());
		
		gerarCodigoSeguranca();		
		
		carregarRelatorios();	
		
		return forward(getRelatorioRescisao());
	}	

	/**
	 * Retorna o Coordenador do Curso do Discente Estagi�rio.
	 * @return
	 * @throws DAOException
	 */
	private Servidor getCoordenadorCurso(Curso curso) throws DAOException{
		CoordenacaoCursoDao dao = getDAO(CoordenacaoCursoDao.class);
		try {
			CoordenacaoCurso coordenacao = dao.findAtivoByData(new Date(), curso);
			if (!isEmpty(coordenacao))
				return coordenacao.getServidor();				
			return null;
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	/**
	 * Cancela a opera��o
	 * <br/><br/>
	 * M�todo Chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/estagio/estagio/form.jsp</li>
	 *   <li>/sigaa.war/estagio/estagio/view.jsp</li>
	 * </ul>
	 */
	@Override
	public String cancelar() {
		if (getUltimoComando().equals(SigaaListaComando.ALTERAR_ESTAGIO)){
			return forward(buscaEstagioMBean.getListPage());
		}		
		return super.cancelar();
	}
	
	/**
	 * Valida os dados informados no cadastro
	 * @return
	 * @throws SegurancaException 
	 * @throws DAOException 
	 */
	private boolean validaEstagio() throws DAOException {
		erros = new ListaMensagens();
		if (getUltimoComando().equals(SigaaListaComando.CADASTRAR_ESTAGIO_AVULSO) || 
				getUltimoComando().equals(SigaaListaComando.ANALISAR_ESTAGIO)){
			if (statusParecer.getId() == StatusEstagio.APROVADO){
				erros.addAll(obj.validate().getMensagens());											
			} 
		}
		if (statusParecer.getId() == StatusEstagio.APROVADO) {
			if (ValidatorUtil.isEmpty(obj.getSupervisor()))
				erros.addErro("� necess�rio informar o Supervisor do Est�gio.");
			
			if (obj.getSupervisor().getCpf_cnpj() == null || !ValidadorCPFCNPJ.getInstance().validaCpfCNPJ(obj.getSupervisor().getCpf_cnpj()))
				erros.addErro("CPF do Supervisor Inv�lido.");
			
			if (ValidatorUtil.isEmpty(obj.getSupervisor().getNome()))
				erros.addErro("Informe o nome do Supervisor.");
	
			if (ValidatorUtil.isEmpty(obj.getSupervisor().getEmail()) || !EmailValidator.getInstance().isValid(obj.getSupervisor().getEmail()))
				erros.addErro("Informe um E-mail V�lido para o Supervisor.");			
			
			if (ValidatorUtil.isEmpty(obj.getDescricaoAtividades()))
				erros.addErro("Informe a Descri��o das Atividades.");			

			Integer chMaxTeoriaPratica = ParametroHelper.getInstance().getParametroInt(ParametrosEstagio.CARGA_HORARIA_MAX_ESTAGIO_TEORIA_PRATICA);
			Integer chMax = ParametroHelper.getInstance().getParametroInt(ParametrosEstagio.CARGA_HORARIA_MAX_ESTAGIO);

			if (!isEmpty(obj.getCargaHorariaSemanal())){
				if (obj.isAlternaTeoriaPratica()){
					if (obj.getCargaHorariaSemanal() > chMaxTeoriaPratica)
						erros.addErro("A Carga Hor�ria n�o pode ser maior que "+chMaxTeoriaPratica+"h semanais");
				} else if (obj.getCargaHorariaSemanal() > chMax)
					erros.addErro("A Carga Hor�ria n�o pode ser maior que "+chMax+"h semanais, pois n�o alterna entre Teoria e Pr�tica ");					
			} else {
				validateMinValue(obj.getCargaHorariaSemanal(), 1, "Carga Hor�ria Semanal", erros);
			}
			
			Integer periodoMax = ParametroHelper.getInstance().getParametroInt(ParametrosEstagio.PERIODO_MAX_ESTAGIO);
			Integer periodoMin = ParametroHelper.getInstance().getParametroInt(ParametrosEstagio.PERIODO_MIN_ESTAGIO);
			
			if (isEmpty(obj.getDiscente().getPessoa().getTipoNecessidadeEspecial()) && !obj.isVigente()){
				erros.addErro("A Vig�ncia m�xima do Est�gio � de "+periodoMax+" Ano(s)");
			} else if (CalendarUtils.calculaQuantidadeMesesEntreDatasIntervaloFechado(obj.getDataInicio(), obj.getDataFim()) < periodoMin)
				erros.addErro("A Vig�ncia m�nima do Est�gio � de "+periodoMin+" meses");
				
			// valida os hor�rios
			validaHorarioEstagio();
			// verifica se o discente est� matriculado.
			verificaMatriculaAtiva();
		} else {
			validateRequired(obj.getObsParecerCoordenador(), "Motivo do Parecer", erros);
		}
		
		if (hasErrors())
			return false;
		
		return true;
	}

	/**
	 * Valida o hor�rio de est�gio.
	 */
	private void validaHorarioEstagio() {
		Calendar cal = Calendar.getInstance();
		long chTotal = 0;
		for (HorarioEstagio horario : horarioMatutino.values()) {
			if (horario.getHoraInicio() == null) continue;
			cal.setTime(horario.getHoraInicio());
			if (cal.get(Calendar.HOUR_OF_DAY) >= 12)
				erros.addErro("O hor�rio matutino da " + horario.getDescricaoDia() + " n�o inicia antes das 12h.");
			else if (horario.getHoraFim() == null)
				addMensagemErro("O hor�rio matutino de sa�da da " + horario.getDescricaoDia() + " n�o est� definido.");
			else if (!horario.getHoraInicio().before(horario.getHoraFim()))
				addMensagemErro("O hor�rio matutino de entrada e sa�da da " + horario.getDescricaoDia() + " est� definido incorretamente.");
			else
				chTotal += horario.getHoraFim().getTime() - horario.getHoraInicio().getTime();
		}
		for (HorarioEstagio horario : horarioVespertino.values()) {
			if (horario.getHoraInicio() == null) continue;
			cal.setTime(horario.getHoraInicio());
			if (cal.get(Calendar.HOUR_OF_DAY) < 12 || cal.get(Calendar.HOUR_OF_DAY) >= 18)
				addMensagemErro("O hor�rio vespertino da " + horario.getDescricaoDia() + " n�o est� definido entre 12h e 18h.");
			else if (horario.getHoraFim() == null)
				addMensagemErro("O hor�rio vespertino de sa�da da " + horario.getDescricaoDia() + " n�o est� definido.");
			else if (!horario.getHoraInicio().before(horario.getHoraFim()))
				addMensagemErro("O hor�rio vespertino de entrada e sa�da da " + horario.getDescricaoDia() + " est� definido incorretamente.");
			else
				chTotal += horario.getHoraFim().getTime() - horario.getHoraInicio().getTime();
		}
		for (HorarioEstagio horario : horarioNoturno.values()) {
			if (horario.getHoraInicio() == null) continue;
			cal.setTime(horario.getHoraInicio());
			if (cal.get(Calendar.HOUR_OF_DAY) < 18)
				addMensagemErro("O hor�rio noturno da " + horario.getDescricaoDia() + " n�o inicia ap�s as 18h.");
			else if (horario.getHoraFim() == null)
				addMensagemErro("O hor�rio noturno de sa�da da " + horario.getDescricaoDia() + " n�o est� definido.");
			else if (!horario.getHoraInicio().before(horario.getHoraFim()))
				addMensagemErro("O hor�rio noturno de entrada e sa�da da " + horario.getDescricaoDia() + " est� definido incorretamente.");
			else
				chTotal += horario.getHoraFim().getTime() - horario.getHoraInicio().getTime();
		}
		long hora = (chTotal / 1000 / 60 / 60);
		long min = (chTotal - hora * 3600 * 1000)/1000/60;
		if (hora > obj.getCargaHorariaSemanal())
			erros.addErro(
					String.format("O hor�rio informado possui carga hor�ria (%02d:%02dh) maior que a carga hor�ria semanal informada (%dh)",
							hora, min, obj.getCargaHorariaSemanal()));
	}
	
	
	/**
	 * Retorna os Tipos de Est�gio em formato de Combo
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/estagio/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getTipoEstagioCombo() throws DAOException {
		if (tipoEstagioCombo == null) {
			GenericDAO dao = getGenericDAO();
			if (!isEmpty(obj.getConcedente())) {
				tipoEstagioCombo = new LinkedList<SelectItem>();
				TipoConvenio tipo = obj.getConcedente().getConvenioEstagio().getTipoConvenio();
				TipoEstagio tipoEstagio;
				if (tipo.getId() == TipoConvenio.ESTAGIO_CURRICULAR_OBRIGATORIO) {
					tipoEstagio = dao.findByPrimaryKey(TipoEstagio.ESTAGIO_CURRICULAR_OBRIGATORIO, TipoEstagio.class);
					tipoEstagioCombo.add(new SelectItem(tipoEstagio.getId(), tipoEstagio.getDescricao()));
				} else if (tipo.getId() == TipoConvenio.ESTAGIO_CURRICULAR_NAO_OBRIGATORIO) {
					tipoEstagio = dao.findByPrimaryKey(TipoEstagio.ESTAGIO_CURRICULAR_NAO_OBRIGATORIO, TipoEstagio.class);
					tipoEstagioCombo.add(new SelectItem(tipoEstagio.getId(), tipoEstagio.getDescricao()));
				} else {
					tipoEstagioCombo = toSelectItems(dao.findAll(TipoEstagio.class), "id", "descricao");
				}
			} else {
				tipoEstagioCombo = toSelectItems(dao.findAll(TipoEstagio.class), "id", "descricao");
			}
		}
		return tipoEstagioCombo;
	}
	
	/**
	 * Retorna os Status de Est�gio que permite parecer em formato de Combo
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/estagio/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getSituacoesAnaliseCombo() throws DAOException{
		EstagiarioDao dao = getDAO(EstagiarioDao.class);
		Collection<Integer> status = new LinkedList<Integer>();
		status.add(obj.getStatus().getId());
		try {
			switch(obj.getStatus().getId()) {
				case StatusEstagio.APROVADO  : status.add(StatusEstagio.CANCELADO); status.add(StatusEstagio.CONCLUIDO); break;
				case StatusEstagio.CANCELADO : break;
				case StatusEstagio.CONCLUIDO : break;
				case StatusEstagio.NAO_COMPATIVEL : break;
				case StatusEstagio.EM_ANALISE : status.add(StatusEstagio.APROVADO); status.add(StatusEstagio.NAO_COMPATIVEL); break;
				case StatusEstagio.SOLICITADO_CANCELAMENTO : status.add(StatusEstagio.APROVADO); status.add(StatusEstagio.CANCELADO); status.add(StatusEstagio.NAO_COMPATIVEL); break;
			}
			Collection<StatusEstagio> todos = dao.findAllStatusParecer();
			Iterator<StatusEstagio> iterator = todos.iterator();
			while (iterator.hasNext()) {
				if (!status.contains(iterator.next().getId()))
					iterator.remove();
			}
			return toSelectItems(todos, "id", "descricao");
		} finally {
			if (dao != null)
				dao.close();
		}
	}		
	
	/**
	 * Carrega o Status Escolhido no Parecer
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/estagio/form.jsp</li>
	 * </ul>
	 * @param e
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public void carregarStatus(ValueChangeEvent e) throws DAOException, SegurancaException {
		if((Integer)e.getNewValue() != 0) 
			statusParecer.setId( (Integer)e.getNewValue() );
	}	
	
	/**
	 * Verifica se o discente est� matriculado em alguma disciplina/atividade.
	 * <br/> M�todo n�o invocado por JSP's
	 * @param e
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public void verificaMatriculaAtiva() throws DAOException {
		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class);
		CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(discente);
		int count = dao.countMatriculasByDiscente(discente, cal.getAno(), cal.getPeriodo(), SituacaoMatricula.MATRICULADO);
		if (count == 0)
			addMensagemErro("O discente n�o est� matriculado em disciplina no ano-per�odo atual.");
		if (obj.getTipoEstagio().getId() == TipoEstagio.ESTAGIO_CURRICULAR_OBRIGATORIO) {
			Collection<MatriculaComponente> matriculas = dao.findAtividades(obj.getDiscente(),
					new TipoAtividade(TipoAtividade.ESTAGIO), SituacaoMatricula.MATRICULADO);
			if (isEmpty(matriculas)) {
				boolean matriculado = false;
				for (MatriculaComponente mc : matriculas)
					if (mc.getAno() == cal.getAno() && mc.getPeriodo() == cal.getPeriodo())
						matriculado = true;
				if (!matriculado)
					addMensagemWarning("O discente n�o est� matriculado em uma atividade de est�gio.");
			} 
		}
	}	
	
	/**
	 * Indica se o Parecer foi favor�vel
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/estagio/form.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isParecerAprovado(){		
		if (!isEmpty(statusParecer))
			return statusParecer.getId() == StatusEstagio.APROVADO;
		
		if (!isEmpty(obj.getStatus()))
			return obj.getStatus().getId() == StatusEstagio.APROVADO;
		
		return false;
	}	
	
	@Override
	public String getFormPage() {
		return "/estagio/estagio/form.jsp";
	}
	
	@Override
	public String getViewPage() {
		return "/estagio/estagio/view.jsp";
	}
	
	public String getRelatorioRescisao(){
		return "/estagio/estagio/termos/rescisao_estagio.jsp";		
	}
	
	public String getRelatorioTermoCompromisso(){
		return "/estagio/estagio/termos/termo_compromisso.jsp";
	}

	public InteresseOferta getInteresseOferta() {
		return interesseOferta;
	}

	public void setInteresseOferta(InteresseOferta interesseOferta) {
		this.interesseOferta = interesseOferta;
	}

	public boolean isCadastro() {
		return cadastro;
	}

	public void setCadastro(boolean cadastro) {
		this.cadastro = cadastro;
	}

	public Servidor getCoordenador() {
		return coordenador;
	}

	public void setCoordenador(Servidor coordenador) {
		this.coordenador = coordenador;
	}

	public long getDuracaoEstagio() {
		return duracaoEstagio;
	}

	public void setDuracaoEstagio(long duracaoEstagio) {
		this.duracaoEstagio = duracaoEstagio;
	}

	public StatusEstagio getStatusParecer() {
		return statusParecer;
	}

	public void setStatusParecer(StatusEstagio statusParecer) {
		this.statusParecer = statusParecer;
	}

	public String getObsParecer() {
		return obsParecer;
	}

	public void setObsParecer(String obsParecer) {
		this.obsParecer = obsParecer;
	}

	/**
	 * Exibe o relat�rio que est� sendo validado.
	 * <br/><br/>
	 * M�todo n�o chamado por JSP.
	 */
	@Override
	public void exibir(EmissaoDocumentoAutenticado comprovante,
			HttpServletRequest req, HttpServletResponse res) {
		try {
			if (ValidatorUtil.isEmpty(comprovante.getDadosAuxiliares()))
				return;
			
			obj = getGenericDAO().findByPrimaryKey(Integer.parseInt(comprovante.getDadosAuxiliares()), Estagiario.class);			
			
			if (!isEmpty(obj)){						
				/*  Pega o c�digo de seguran�a do comprovante para reexibir ao usu�rio */
				codigoSeguranca = comprovante.getCodigoSeguranca();				
				/* *************************************************************************************
				 *   IMPORTANTE TEM QUE REDIRECIONAR PARA A P�GINA DO RELAT�RIO CONFORME O SEU SUBTIPO
				 * *************************************************************************************/
				
				carregarRelatorios();
				
				switch (comprovante.getSubTipoDocumento()) {
				case SubTipoDocumentoAutenticado.TERMO_COMPROMISSO_ESTAGIO:
					getCurrentRequest().getRequestDispatcher(getRelatorioTermoCompromisso()).forward(getCurrentRequest(), getCurrentResponse());					
					break;
				case SubTipoDocumentoAutenticado.TERMO_RESCISAO_ESTAGIO:
					getCurrentRequest().getRequestDispatcher(getRelatorioRescisao()).forward(getCurrentRequest(), getCurrentResponse());
					break;
				}				
			}
		}catch (Exception  e) {
			e.printStackTrace();
			tratamentoErroPadrao(e);			
		}
	}

	/**
	 * Realiza a valida��o do relat�rio
	 * <br/><br/>
	 * M�todo n�o chamado por JSP.
	 */
	@Override
	public boolean validaDigest(EmissaoDocumentoAutenticado comprovante) {		
		try {						
			if (ValidatorUtil.isEmpty(comprovante.getDadosAuxiliares()))
				return false;
			
			obj = getGenericDAO().findByPrimaryKey(Integer.parseInt(comprovante.getDadosAuxiliares()), Estagiario.class);
		
			if (isEmpty(obj))
				return false;
			
			String codigoVerificacao = AutenticacaoUtil.geraCodigoValidacao(comprovante, getSementeDocumento());

			if (codigoVerificacao.equals(comprovante.getCodigoSeguranca()))
				return true;							
			
		}catch (DAOException daoExt) {
			daoExt.printStackTrace();
			return false;
		} catch (ArqException ae) {
			ae.printStackTrace();
			return false;
		}			
		return false;
	}
	
	/**
	 *   Gera o C�digo de Seguran�a para os Documentos do Est�gio
	 */
	private void gerarCodigoSeguranca() throws ArqException{		
		// S� gera outro c�digo de seguran�a e, consequentemente, outro documento se ele j�
		// n�o foi emitido antes, para evitar gera��es desnecess�rias de c�digo de seguran�a.		
		if (codigoSeguranca == null  ){			
			try {										
				comprovante = geraEmissao(
						TipoDocumentoAutenticado.DOCUMENTOS_DE_ESTAGIO,
						String.valueOf( identificadorDocumento ),  // identificador
						
						getSementeDocumento(),
						
						String.valueOf( obj.getId() ), // informa��es complementares - id do est�gio
						tipoDocumento,
						false
				);		
				codigoSeguranca = comprovante != null ? comprovante.getCodigoSeguranca() : null;				
			} catch (NegocioException e){
				addMensagens(e.getListaMensagens());
			}
		}
	}	
	
	/**
	 * Verifica se o usu�rio pode cadastrar um estagi�rio
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/interesse_oferta/view.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isPermiteAnalisarEstagio(){
		return isPortalCoordenadorGraduacao() && !isEmpty(getCursoAtualCoordenacao()); 
	}		
	
	/**
	 * Retorna a semente do documento.
	 * @return
	 */
	private String getSementeDocumento(){
		return (obj.getConcedente().getConvenioEstagio().getNumeroConvenio()+
			((obj.getTipoEstagio() != null ? obj.getTipoEstagio().getId() : 0 )+
			(obj.getDataInicio() != null ? obj.getDataInicio().getTime() : 0))*1000)+
			(obj.getDataFim() != null ? obj.getDataFim().getTime() : 0); // semente
	}

	public String getCodigoSeguranca() {
		return codigoSeguranca;
	}

	public void setCodigoSeguranca(String codigoSeguranca) {
		this.codigoSeguranca = codigoSeguranca;
	}

	public int getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(int tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public EmissaoDocumentoAutenticado getComprovante() {
		return comprovante;
	}

	public void setComprovante(EmissaoDocumentoAutenticado comprovante) {
		this.comprovante = comprovante;
	}

	public String getIdentificadorDocumento() {
		return identificadorDocumento;
	}

	public void setIdentificadorDocumento(String identificadorDocumento) {
		this.identificadorDocumento = identificadorDocumento;
	}

	/** 
	 * Chamado a partir do BuscaDiscenteMBean
	 * M�todo n�o invocado por JSP.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#selecionaDiscente()
	 */	
	@Override
	public String selecionaDiscente() throws ArqException {
		if (ValidatorUtil.isEmpty(discente)){
			addMensagemErro("N�o foi poss�vel carregar dados do discente selecionado.");
			return null;				
		}
			
		obj.setDiscente(discente);	
		
		convenioEstagioMBean.setEstagiario(obj);
		Integer[] statusConvenioEstagio = { StatusConvenioEstagio.APROVADO};
		return convenioEstagioMBean.iniciarSeletorEstagio(this, statusConvenioEstagio );
	}
	
	/**
	 * Busca o local de est�gio pelo CPF/CNPJ
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/estagio/form.jsp</li>
	 * </ul>
	 * @param e
	 * @throws DAOException
	 */
	public void buscarEmpresaEstagio(ActionEvent e) throws DAOException{
		Long cnpj = StringUtils.extractLong(String.valueOf(obj.getEmpresaEstagio().getCpf_cnpj()));
		PessoaDao dao = null;
		podeAlterarEmpresaEstagio = true;
		if (ValidadorCPFCNPJ.getInstance().validaCpfCNPJ(cnpj)){
			dao = getDAO(PessoaDao.class);
			Pessoa p = dao.findByCpf(cnpj, false);
			if (p != null){
				obj.setEmpresaEstagio(p);
				podeAlterarEmpresaEstagio = false;
			} else {
				if (ValidadorCPFCNPJ.getInstance().validaCPF(String.valueOf(cnpj)))
					obj.getEmpresaEstagio().setTipo(Pessoa.PESSOA_FISICA);
				else
					obj.getEmpresaEstagio().setTipo(Pessoa.PESSOA_JURIDICA);
			}
		} else {
			podeAlterarEmpresaEstagio = true;
		}
		obj.getEmpresaEstagio().prepararDados();
	}
	
	/**
	 * Verifica se a��o atual � cadastro avulso
	 * <br/><br/>
	 * M�todo Chamado pelas seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/interesse_oferta/form.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isCadastroAvulso(){		
		return getUltimoComando() != null && getUltimoComando().equals(SigaaListaComando.CADASTRAR_ESTAGIO_AVULSO); 
	}

	/**
	 * Seta o discente selecionado na busca por discente.
	 * <br /><br />
	 * M�todo n�o invocado por JSP.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#setDiscente(br.ufrn.sigaa.pessoa.dominio.Discente)
	 */	
	@Override
	public void setDiscente(DiscenteAdapter discente) throws ArqException {		
		this.discente = discente;
	}

	public DiscenteAdapter getDiscente() {
		return discente;
	}

	public Map<String, HorarioEstagio> getPrimeiroTurno() {
		return horarioMatutino;
	}

	public void setPrimeiroTurno(Map<String, HorarioEstagio> primeiroTurno) {
		this.horarioMatutino = primeiroTurno;
	}

	public Map<String, HorarioEstagio> getSegundoTurno() {
		return horarioVespertino;
	}

	public void setSegundoTurno(Map<String, HorarioEstagio> segundoTurno) {
		this.horarioVespertino = segundoTurno;
	}

	public Map<String, HorarioEstagio> getHorarioMatutino() {
		return horarioMatutino;
	}

	public void setHorarioMatutino(Map<String, HorarioEstagio> horarioMatutino) {
		this.horarioMatutino = horarioMatutino;
	}

	public Map<String, HorarioEstagio> getHorarioVespertino() {
		return horarioVespertino;
	}

	public void setHorarioVespertino(Map<String, HorarioEstagio> horarioVespertino) {
		this.horarioVespertino = horarioVespertino;
	}

	public Map<String, HorarioEstagio> getHorarioNoturno() {
		return horarioNoturno;
	}

	public void setHorarioNoturno(Map<String, HorarioEstagio> horarioNoturno) {
		this.horarioNoturno = horarioNoturno;
	}

	public boolean isPodeAlterarEmpresaEstagio() {
		return podeAlterarEmpresaEstagio;
	}

	@Override
	public ListaMensagens validaConvenioSelecionado(ConvenioEstagio convenioEstagio) {
		ListaMensagens lista = new ListaMensagens();
		if (!convenioEstagio.isAprovado())
			lista.addErro("O conv�nio de est�gio selecionado n�o est� com status APROVADO.");
		return lista;
	}

	@Override
	public String selecionaConvenioEstagio(ConvenioEstagio convenioEstagio) throws ArqException {
		ConcedenteEstagio concedente = getGenericDAO().refresh(convenioEstagio.getConcedente());
		obj.setConcedente(concedente);
		obj.setEmpresaEstagio(concedente.getPessoa());
		return carregarCadastroAvulso();
	}

	@Override
	public String seletorConvenioEstagioVoltar() {
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");		
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.CADASTRO_ESTAGIO_AVULSO);		
		return forward(buscaDiscenteMBean.getListPage());
	}

}
