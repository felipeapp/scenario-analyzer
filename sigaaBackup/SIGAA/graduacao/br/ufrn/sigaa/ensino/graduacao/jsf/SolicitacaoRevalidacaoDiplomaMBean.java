/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 09/01/2009
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.gru.negocio.GuiaRecolhimentoUniaoHelper;
import br.ufrn.sigaa.arq.dao.MunicipioDao;
import br.ufrn.sigaa.arq.dao.graduacao.AgendaRevalidacaoDiplomaDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoRevalidacaoDiplomaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.ensino.graduacao.dominio.AgendaRevalidacaoDiploma;
import br.ufrn.sigaa.ensino.graduacao.dominio.EditalRevalidacaoDiploma;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoRevalidacaoDiploma;
import br.ufrn.sigaa.negocio.MovimentoRevalidacaoDiploma;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pais;
import br.ufrn.sigaa.pessoa.dominio.TipoLogradouro;

/**
 * Classe Managed-Bean de Revalidação de Diplomas
 *  
 * @author Mário Rizzi
 */
@Component("solRevalidacaoDiploma") @Scope("session") 
public class SolicitacaoRevalidacaoDiplomaMBean extends SigaaAbstractController<SolicitacaoRevalidacaoDiploma>{


	private static final String BASE_REPORT_PACKAGE = "/br/ufrn/sigaa/relatorios/fontes/";
	private static final String FILE_NAME_REPORT = "trf_11578_relatorio_revalidacao_diploma";

	/** Possui todos municípios associados ao estado selecionado */
	private Collection<SelectItem> municipiosUf = new ArrayList<SelectItem>();
	
	/** Possui todos os horários associados a data selecionada */
	private Collection<SelectItem> horariosData = new ArrayList<SelectItem>();
	
	/** Possui os dados preenchidos pelo inscrito */
	private Collection<SolicitacaoRevalidacaoDiploma> solicitacoesRevalidacaoDiploma;
	
	/** Define o edital no qual os inscritos estão associados */
	private EditalRevalidacaoDiploma editalRevalidacaoDiploma;
	
	/** Filtros utilizados na área restrita **/
	private String filtroData;
	private String filtroHorario;
	private String filtroNome;
	private Integer filtroAgenda;
	private boolean filtroEspera = false;
	
	/** Filtros utilizados na parte pública **/
	private Long filtroCpf;
	private String filtroPassaporte;
	private Integer filtroNacionalidade; 
	
	/** Verificadores dos filtros utilizados na parte pública */
	public boolean buscaNome = false;
	public boolean buscaData = false;
	
	/** Define o formato padrão para geração do relatório */
	private String relatorioformato = "pdf";
	
	/** Define se o reagendamento e a inscrição estão disponíveis */
	private boolean abertoInscricao = false;
	private boolean abertoReagendamento = false;
	
	public SolicitacaoRevalidacaoDiplomaMBean() throws DAOException{
		initObj();
	}

	/**
	 * Inicializa objetos envolvidos no caso de uso.
	 * @throws DAOException
	 */
	private void initObj() throws DAOException{
	
		removeOperacaoAtiva();
		obj = new SolicitacaoRevalidacaoDiploma();
		obj.setTipoLogradouro( new TipoLogradouro() );
		obj.setUnidadeFederativa(new UnidadeFederativa());
		
		obj.setMunicipio( new Municipio() );
		obj.setAgendaRevalidacaoDiploma( new AgendaRevalidacaoDiploma() );
		obj.setPaisUniversidade(new Pais(Pais.BRASIL));
		obj.setAgendaRevalidacaoDiploma(new AgendaRevalidacaoDiploma());
		obj.setEditalRevalidacaoDiploma((EditalRevalidacaoDiploma) getGenericDAO().findLast(EditalRevalidacaoDiploma.class));
		
		this.horariosData = new ArrayList<SelectItem>();
		
		this.filtroNacionalidade = Pais.BRASIL;
		this.filtroCpf = null;
		this.filtroPassaporte = null;
		this.filtroData = null;
		this.buscaNome = false;	
		this.buscaData = false;
		
	}	
	
	/**
	 * Método que reinicializa um mbean e cria uma nova instância
	 * 
	 * <br />
	 * <br/><b>Método chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li>sigaa.war\public\revalidacao_diplomas\form_busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String getIniciar() throws ArqException{
		initObj();
		return "";
	}
	
	/**
	 * Reset a MBean e redireciona para o formulário de cadastro.
	 * 
	 * <br />
	 * <br/><b>Método chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li>sigaa.war/graduacao/menus/relatorios_dae.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String formRelatorioRevalidacao(){
		resetBean();
		return forward("/graduacao/revalidacao_diploma/form_relatorio.jsp");
	}
	
	/**
	 * Reset a MBean e redireciona para página do sub-sistema.
	 * 
	 * <br/>
	 * <br/><b>Método chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * <li>sigaa.war/public/revalidacao_diplomas/form_busca.jsp</li>
	 * <li>sigaa.war/public/revalidacao_diplomas/form_cadastro.jsp</li>
	 * <li>sigaa.war/public/revalidacao_diplomas/form_reagendamento.jsp</li>
	 * </ul>
	 */
	@Override
	public String cancelar() {
		return super.cancelar();
	}	
	
	/**
	 * Reset a MBean e redireciona para página pública principal.
	 * 
	 * <br/>
	 * <br/><b>Método chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * <li>sigaa.war/public/revalidacao_diplomas/form_busca.jsp</li>
	 * <li>sigaa.war/public/revalidacao_diplomas/form_cadastro.jsp</li>
	 * <li>sigaa.war/public/revalidacao_diplomas/form_reagendamento.jsp</li>
	 * </ul>
	 * 
	 */
	public String cancelarPublic(){
		resetBean();
		return redirectJSF("/sigaa/public/");
	}
	
	
	/**
	 * Redireciona para página de busca de solicitações na área restrita.
	 * 
	 * <br/><br/>Não é chamado por JSP.
	 * 
	 */
	@Override
	public String getListPage() {
		return "/graduacao/revalidacao_diploma/form_busca.jsp";
	}
	
	
	
	/**
	 * Redireciona para o formulário de cadastro na área restrita do SIGAA.
	 * 
	 * <br/><b>Método chamado pela(s) seguinte(s) JSP(s):</b> nenhuma jsp
	 * 
	 */
	public String getFormPagePublic() {
		return "/public/revalidacao_diplomas/form_cadastro.jsp";
	}
	
	/**
	 * Redireciona para o formulário de cadastro na área restrita do SIGAA.
	 * 
	 * <br/><b>Método chamado pela(s) seguinte(s) JSP(s):</b> nenhuma jsp
	 * 
	 */
	@Override
	public String getFormPage() {
		return "/graduacao/revalidacao_diploma/form_cadastro.jsp";
	}
	
	/**
	 * Método responsável em definir o diretório raiz do caso de uso.
	 * <br/><br/>Não é chamado por JSP.
	 */
	@Override
	public String getDirBase() {
		return "/public/revalidacao_diplomas/";
	}
	
	/**
	 * Prepara o formulário para o cadastro da solicitação de revalidação de diploma
	 * <br/>
	 * <b>Método chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li> /SIGAA/app/sigaa.ear/sigaa.war/public/revalidacao_diplomas/form_busca.jsp</li>
	 * 	<li> /SIGAA/app/sigaa.ear/sigaa.war/graduacao/revalidacao_diploma/form_busca.jsp</li>
	 * </ul>
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		initObj();
		setConfirmButton("Cadastrar");
		prepareMovimento(SigaaListaComando.CADASTRA_REVALIDACAO_DIPLOMA);
		return forward(getFormPage());
	}
	

	/**
	 * Inicia o formulário de cadastro de revalidação na parte pública.
	 * <br />
	 * <br/><b>Método chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li>sigaa.war\public\revalidacao_diplomas\form_busca.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String preCadastrarPublico() throws ArqException, NegocioException {
		
		initObj();
		
		if(!isAbertoInscricao())
			return null;
		
		setConfirmButton("Cadastrar");
		setReadOnly(false);
		prepareMovimento(SigaaListaComando.CADASTRA_REVALIDACAO_DIPLOMA);
		
		return forward(getFormPagePublic());
	}
	
	/**
	 * Verifica se estão abertas as inscrições.
	 * 
	 * <br />
	 * <br/><b>Método chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li>sigaa.war\public\revalidacao_diplomas\form_busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public boolean isAbertoReagendamento() throws ArqException{
		 
		abertoReagendamento = true;
		
		if(isEmpty(obj.getEditalRevalidacaoDiploma()) || obj.getEditalRevalidacaoDiploma().getId()==0)
			obj.setEditalRevalidacaoDiploma((EditalRevalidacaoDiploma) getGenericDAO().findLast(EditalRevalidacaoDiploma.class));
		
		Date agendaInicio = obj.getEditalRevalidacaoDiploma().getInicioAgenda();
		Date agendaFim = obj.getEditalRevalidacaoDiploma().getFinalAgenda();

		Date dataAtual = new Date();
		
		//Verificação do período de inscrição  
		if( !CalendarUtils.isDentroPeriodo(agendaInicio, agendaFim, dataAtual) ){
			 if( CalendarUtils.compareTo(dataAtual, agendaFim)>0)
				addMensagemErro("O período para reagendamento está encerrado. Para maiores informações, por favor entre em contato.");
			 else if( CalendarUtils.compareTo(dataAtual, agendaInicio)<0)
				 addMensagemErro("O reagendamento ainda não disponível. Para maiores informações, por favor entre em contato.");
			 abertoReagendamento = false;
		}

		return abertoReagendamento;
	}
	
	/**
	 * Verifica se estão abertas as inscrições.
	 * 
	 * <br />
	 * <br/><b>Método chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li>sigaa.war\public\revalidacao_diplomas\form_busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public boolean isAbertoInscricao() throws ArqException{
		 
		abertoInscricao = true;
		
		
		if(obj == null)
		obj = new SolicitacaoRevalidacaoDiploma();
		
		PersistDB editalRevalidacaoDiploma = getGenericDAO().findLast(EditalRevalidacaoDiploma.class);
		if(editalRevalidacaoDiploma==null || editalRevalidacaoDiploma.getId()==0) {
			addMensagemErro("Não foi encontrado Edital na base de dados.");
			return false;
		}
		
		obj.setEditalRevalidacaoDiploma( (EditalRevalidacaoDiploma) editalRevalidacaoDiploma );
		
		Date periodoInicio = obj.getEditalRevalidacaoDiploma().getInicioInscricao();
		Date periodoFim = obj.getEditalRevalidacaoDiploma().getFinalInscricao();
		
		Date dataAtual = new Date();
	
		//Verificação do período de inscrição  
		if( !CalendarUtils.isDentroPeriodo(periodoInicio, periodoFim, dataAtual) ){
			 if( CalendarUtils.compareTo(dataAtual, periodoFim)>0)
				addMensagemErro("As inscrições foram encerradas. Para maiores informações, por favor entre em contato.");
			 else if( CalendarUtils.compareTo(dataAtual, periodoInicio)<0)
				 addMensagemErro("As inscrições ainda não foram abertas. Para maiores informações, por favor entre em contato.");
			abertoInscricao = false;
		}
		
		return abertoInscricao;
	}
	
	/**
	 * Cadastro da solicitação para agendamento
	 * 
	 * <br />
	 * <br/><b>Método chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li>sigaa.war\site\revalidacao_diplomas\form_cadastro.jsp</li>
	 * </ul>
	 * 
	 */
	@Override
	public String cadastrar() throws ArqException {
		
		MovimentoRevalidacaoDiploma mov = new MovimentoRevalidacaoDiploma();
		mov.setRevalidacaoDiploma(obj);
		mov.setCodMovimento(SigaaListaComando.CADASTRA_REVALIDACAO_DIPLOMA);
		
		//Valida se o usuário durante atualização do cadastro selecionou o campo data e horário de agendamento. 
		Collection<AgendaRevalidacaoDiploma> dataHorarioDisponivel = 
			getDAO(AgendaRevalidacaoDiplomaDao.class).findAllDatasDisponiveis(obj);
		
		//Valida se o usuário durante atualização do cadastro selecionou o campo data e horário de agendamento. 
		if(!isEmpty(dataHorarioDisponivel) && isEmpty(mov.getRevalidacaoDiploma().getAgendaRevalidacaoDiploma().getId()))
			addMensagemErro("Os campos Data e Hora de agendamento são obrigatórios.");
		

		if(hasErrors())
			return null;
		
		if(mov.getRevalidacaoDiploma().getAgendaRevalidacaoDiploma() == null || 
				mov.getRevalidacaoDiploma().getAgendaRevalidacaoDiploma().getId()==0)
			mov.getRevalidacaoDiploma().setAgendaRevalidacaoDiploma(null);
		
		
		try {
			execute(mov);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
			clearDataHorario();
			return null;
		}finally{
			filtroData = null;
			filtroHorario = null;
		}
	
		
		if(!isEmpty(getUsuarioLogado())){
			addMensagemInformation("Dados atualizados com sucesso!");
			return cancelar();
		}else{
			obj = getGenericDAO().findByPrimaryKey(obj.getId(), SolicitacaoRevalidacaoDiploma.class);
			return redirectJSF("/sigaa/public/revalidacao_diplomas/view.jsp");
		}	
		
	}
	
	/**
	 * Cadastro da solicitação para agendamento público
	 * 
	 * <br />
	 * <br/><b>Método chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li>sigaa.war\public\revalidacao_diplomas\form_cadastro.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String cadastrarPublico() throws ArqException{
		
		isAbertoInscricao();
		return cadastrar();
		
	}
	
	/**
	 * Método utilizado quando se deseja limpar os dados da data e horário de agendamento carregados no formulário de cadastro.
	 */
	private void clearDataHorario(){
		
		if(obj.getAgendaRevalidacaoDiploma() != null && obj.getAgendaRevalidacaoDiploma().getId()==0){
			setFiltroData(null);
			setHorariosData(new ArrayList<SelectItem>());
		}
		
	}
	
	/**
	 * Cadastro da solicitação para reagendamento
	 * 
	 * <br />
	 * <br/><b>Método chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li>sigaa.war\public\revalidacao_diplomas\form_reagendamento.jsp</li>
	 * </ul>
	 * 
	 */
	public String cadastrarReagendamento() throws ArqException {
		
		MovimentoRevalidacaoDiploma mov = new MovimentoRevalidacaoDiploma();
		mov.setRevalidacaoDiploma(obj);
		mov.setCodMovimento(SigaaListaComando.REAGENDAMENTO_REVALIDACAO_DIPLOMA);
		
		//Valida se o usuário durante atualização do cadastro selecionou o campo data e horário de agendamento. 
		if(isEmpty(mov.getRevalidacaoDiploma().getAgendaRevalidacaoDiploma().getId()))
			addMensagemErro("Os campos Data e Hora de agendamento são obrigatórios.");
			
		if(hasErrors())
			return null;
		
		try {
			execute(mov);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
			clearDataHorario();
			return null;
		}
		return forward("/public/revalidacao_diplomas/view.jsp");
		
	}
	
		
		
	/**
	 * Remove uma solicitação.
	 * 
	 * <br />
	 * <br/><b>Método chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li>sigaa.war/graduacao/revalidacao_diploma/form_busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	@Override
	public String remover() throws ArqException {
		Integer id = getParameterInt("id");
		obj = getGenericDAO().findByPrimaryKey(id, SolicitacaoRevalidacaoDiploma.class);
		prepareMovimento(ArqListaComando.REMOVER);
		super.remover();
		return forward("/graduacao/revalidacao_diploma/form_busca.jsp");
	}
		
	
	/**
	 * Prepara formulário para atualização dos dados do inscrito.
	 * 
	 * <br />
	 * <br/><b>Método chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li>sigaa.war/graduacao/revalidacao_diploma/form_atualizar.jsp</li>
	 * </ul>
	 * 
	 */
	@Override
	public String atualizar() throws ArqException {
		
		prepareMovimento(SigaaListaComando.CADASTRA_REVALIDACAO_DIPLOMA);
		GenericDAO dao = getGenericDAO();
		
		setId();
		setReadOnly(false);
		setOperacaoAtiva(2);
		
		this.obj = dao.findByPrimaryKey(obj.getId(), SolicitacaoRevalidacaoDiploma.class);
		carregarMunicipios(obj.getUnidadeFederativa().getId());
		
		if(!isEmpty(obj.getAgendaRevalidacaoDiploma()) && !isEmpty(obj.getAgendaRevalidacaoDiploma().getData()))
			carregarHorarios(obj.getAgendaRevalidacaoDiploma().getData());

		if(obj.getAgendaRevalidacaoDiploma() == null || 
				obj.getAgendaRevalidacaoDiploma().getId()==0)
			obj.setAgendaRevalidacaoDiploma(new AgendaRevalidacaoDiploma());
		
		
		if(!isEmpty(obj.getAgendaRevalidacaoDiploma()) && !isEmpty(obj.getAgendaRevalidacaoDiploma().getData()))
				setFiltroData(Formatador.getInstance().formatarData(obj.getAgendaRevalidacaoDiploma().getData()));
		
		obj.setConfirmaEmail(obj.getEmail());
		obj.setConfirmaEmailUniversidade(obj.getEmailUniversidade());
		
		if(obj!=null && !isEmpty(obj.getId()))
			setConfirmButton("Alterar");
		else
			setConfirmButton("Cadastrar");
		
		return forward(getFormPage());
	}
	
	/**
	 * Converte string para int e invoca o método carregarMunicipios.
	 * 
	 * <br />
	 * <br/><b>Método chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li>sigaa.war/public/revalidacao_diplomas/form_cadastro.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws DAOException
	 */
	public void carregarMunicipios(ValueChangeEvent e) throws DAOException {
		
		String selectId = e.getComponent().getId();
		if (selectId != null && e.getNewValue() != null) {
			Integer ufId = (Integer) e.getNewValue();
			carregarMunicipios(ufId);
		}
	}
	
	/**
	 * Popula os municípios pertencentes a uma determinada UF.
	 * 
	 * <br />
	 * <br/><b>Método chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li>sigaa.war/public/revalidacao_diplomas/form_cadastro.jsp</li>
	 * </ul>
	 * 
	 * @param idUf
	 * @throws DAOException
	 */
	public void carregarMunicipios(Integer idUf) throws DAOException {

		if ( idUf == null ) {
			idUf = obj.getUnidadeFederativa().getId();
		}

		MunicipioDao dao = getDAO(MunicipioDao.class);
		UnidadeFederativa uf = dao.findByPrimaryKey(idUf, UnidadeFederativa.class);
		Collection<Municipio> municipios = dao.findByUF(idUf);
		municipiosUf = new ArrayList<SelectItem>(0);
		municipiosUf.add(new SelectItem(uf.getCapital().getId(), uf.getCapital().getNome()));
		municipiosUf.addAll(toSelectItems(municipios, "id", "nome"));
	
	}
	
	/**
	 * Converte string para data e invoca o método carregaHorarios.
	 * 
	 * <br />
	 * <br/><b>Método chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li>sigaa.war/public/revalidacao_diplomas/form_cadastro.jsp</li>
	 * 	<li>sigaa.war/public/revalidacao_diplomas/form_reagendamento.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws DAOException
	 */
	public void carregarHorarios(ValueChangeEvent e) throws DAOException {
		String value = String.valueOf(e.getNewValue());
		Date dataSel = Formatador.getInstance().parseDate(value);
		carregarHorarios(dataSel);
		
	}
	
	/**
	 * Carrega horários em relação a uma determinada data.
	 * 
	 * @param dataSel
	 * @throws DAOException
	 */
	private void carregarHorarios(Date dataSel) throws DAOException {
	
		horariosData = new ArrayList<SelectItem>();
		AgendaRevalidacaoDiplomaDao dao = getDAO(AgendaRevalidacaoDiplomaDao.class);
		if(!isEmpty(dataSel)){
			Collection<AgendaRevalidacaoDiploma> horarios = new ArrayList<AgendaRevalidacaoDiploma>(); 
			if(!isEmpty(getUsuarioLogado()) && isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE,SigaaPapeis.DAE))
				horarios = dao.findAllHorariosByData(dataSel);
			else
				horarios = dao.findHorariosByData(dataSel, null, obj.getId());
				
			horariosData.addAll(toSelectItems(horarios, "id", "horario"));
		}else{
			horariosData.addAll(toSelectItems(null, "id", "horario"));
		}
		
	}
	
	/**
	 * Retorna objeto populado a partir de uma id.
	 * 
	 * <br />
	 * <br/><b>Método chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li>sigaa.war/public/revalidacao_diplomas/view.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public AgendaRevalidacaoDiploma getAgenda() throws DAOException{
		if(!isEmpty(obj.getAgendaRevalidacaoDiploma()))		
			return  getDAO(AgendaRevalidacaoDiplomaDao.class).findByPrimaryKey(obj.getAgendaRevalidacaoDiploma().getId(), AgendaRevalidacaoDiploma.class);
		return null;
	}
	
	/**
	 * Retorna um agenda populada de acordo com um id.
	 * 
	 * <br />
	 * <br/><b>Método chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li>sigaa.war/public/revalidacao_diplomas/form_cadastro.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllComboData() throws DAOException {
		
		ArrayList<SelectItem> agendaData = new ArrayList<SelectItem>(); 
		Collection<AgendaRevalidacaoDiploma> agendas = new ArrayList<AgendaRevalidacaoDiploma>();
		
		if(!isEmpty(getUsuarioLogado()) && isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE,SigaaPapeis.DAE)
				&& (getAcessoMenu().isDae() || getAcessoMenu().isAdministradorDAE()))
			agendas = getDAO(AgendaRevalidacaoDiplomaDao.class).findAllDatas(obj.getEditalRevalidacaoDiploma());
		else
			agendas = getDAO(AgendaRevalidacaoDiplomaDao.class).findAllDatasDisponiveis(obj);
		
		for (AgendaRevalidacaoDiploma a : agendas) 
			agendaData.add(new SelectItem(a.getDataConvertida(), a.getDataConvertida()));
		
		return agendaData;
		
	}
	
	/**
	 * Redireciona a página e retorna todas as solicitações de acordo com filtro passado.
	 * 
	 * <br />
	 * <br/><b>Método chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li>sigaa.war/public/revalidacao_diplomas/form_busca.jsp</li>
	 * </ul>
	 * 	
	 * @return
	 * @throws DAOException
	 */
	public String buscarSolicitacoes() throws DAOException{
		
		clearMensagens();
		solicitacoesRevalidacaoDiploma = null;
		if(isEmpty(filtroAgenda))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Edital");
		
		if(hasErrors())
			return null;
		
		getSolicitacao();
		
		return forward("/graduacao/revalidacao_diploma/form_busca.jsp");
		
	}
	
		
	/**
	 * Popula uma coleção de solicitações de acordo
	 * com data e horário especificado.
	 * @throws DAOException
	 */
	private void getSolicitacao() throws DAOException{
		
		Date dataSel = Formatador.getInstance().parseDate(filtroData);
		solicitacoesRevalidacaoDiploma = getDAO(SolicitacaoRevalidacaoDiplomaDao.class).
			findGeral(filtroAgenda, filtroNome, dataSel, filtroHorario, filtroEspera);
		
	}
	
	/**
	 * Retorna um relatório dos inscritos de acordo com o filtro passado na busca.
	 * 
	 * <br />
	 * <br/><b>Método chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li>sigaa.war/graduacao/revalidacao_diploma/form_relatorio.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioDataHorario() throws DAOException{
		getSolicitacao();
		return forward("/graduacao/relatorios/discente/lista_agendamento_revalidacao.jsf");
	}
	
	
	/**
	 * Gerar um relatório conforme os parâmetros.
	 * 
	 * @param response
	 * @param request
	 * @param nomeArquivo
	 * @param formato
	 * @param lista
	 * @throws ArqException
	 */
	private void gerarRelatorios(HttpServletResponse response, HttpServletRequest request, String nomeArquivo,
			String formato, Collection<?> lista) throws ArqException {
		
		try {
				
			Map<String, Object> map = new HashMap<String, Object>();
			
			InputStream report = JasperReportsUtil.getReport(BASE_REPORT_PACKAGE,nomeArquivo+".jasper");
			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lista);
			JasperPrint prt = JasperFillManager.fillReport(report, map, jrds);
			
			JasperReportsUtil.exportar(prt, nomeArquivo, request, response, formato);
	
			FacesContext.getCurrentInstance().responseComplete();
			
		} catch (JRException e) {
			throw new ArqException(e);
		} catch (IOException e) {
			throw new ArqException(e);
		}
		
	}
	
	/**
	 * Gerar relatório no formato PDF ou XLS.
	 * 
	 * <br />
	 * <br/><b>Método chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li>sigaa.war/graduacao/revalidacao_diploma/form_relatorio.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String gerarRelatorio() throws ArqException {
		
		if(ValidatorUtil.isEmpty(filtroAgenda)){
			addMensagemErro("Edital: Campo obrigatório não informado.");
			return null;
		}
		
		getSolicitacao();
		gerarRelatorios(getCurrentResponse(), getCurrentRequest(), 
				FILE_NAME_REPORT,
				getRelatorioformato(), solicitacoesRevalidacaoDiploma);
		return "";
    }
	
	/**
	 * Redireciona para página de resumo contendo os dados do inscritos, e também a data e horário selecionado para o agendamento
	 * <br/><b>Método chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li>sigaa.war/public/revalidacao_diplomas/view.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String visualizarFicha() throws DAOException{
		
		Integer idSolicitacao = getParameterInt("idSolicitacao");
		
		if(!isEmpty(idSolicitacao))
			obj = getGenericDAO().findByPrimaryKey(idSolicitacao, SolicitacaoRevalidacaoDiploma.class);
		
		if(hasErrors())
			return null;
		
		return forward("/public/revalidacao_diplomas/view_inscricao.jsp");
	}
	
	/**
	 * Popula objeto do tipo SolicitaçãoRevaldaçãoDiploma a partir da nacionalidade e CPF ou passaporte.
	 * 
	 * <br />
	 * <br/><b>Método chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li>sigaa.war/public/revalidacao_diplomas/view.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws Exception
	 */
	public String visualizarAgenda() throws Exception {
		
		clearMensagens();
		validaBuscaPublica();	
		
		
		if(!hasErrors() && (getFiltroCpf() != null || getFiltroPassaporte() != null)){
			obj = new SolicitacaoRevalidacaoDiploma();
			if(isEmpty(obj.getEditalRevalidacaoDiploma()) || obj.getEditalRevalidacaoDiploma().getId()==0)
				obj.setEditalRevalidacaoDiploma((EditalRevalidacaoDiploma) getGenericDAO().findLast(EditalRevalidacaoDiploma.class));
			
			obj = getDAO(SolicitacaoRevalidacaoDiplomaDao.class).
				findByNacionalidadeDocumento(obj, getFiltroNacionalidade(), getFiltroCpf(), getFiltroPassaporte());
			
			if(isEmpty(obj))
				addMensagemErro("Não foi encontrado resultado para essa consulta.");
		}
		
		
		if(hasErrors())
			return null;
		
		if(isEmpty(obj.getAgendaRevalidacaoDiploma()))
			if(isAbertoReagendamento()){
			
				prepareMovimento(SigaaListaComando.REAGENDAMENTO_REVALIDACAO_DIPLOMA);
				obj.setAgendaRevalidacaoDiploma(new AgendaRevalidacaoDiploma());
				carregarMunicipios(obj.getUnidadeFederativa().getId());
				setReadOnly(true);
				if(!isEmpty(obj.getId()))
					setConfirmButton("Atualizar");
				else
					setConfirmButton("Cadastrar");
				
				obj.setConfirmaEmail(obj.getEmail());
				obj.setConfirmaEmailUniversidade(obj.getEmailUniversidade());
	
				if(hasErrors())
					return null;
			
				return forward("/public/revalidacao_diplomas/form_reagendamento.jsp");
			
			}
		
		setReadOnly(false);
		setConfirmButton("Cadastrar");
		
		return forward("/public/revalidacao_diplomas/view.jsp");
		
		
	}
	
	
	
	/**
	 * Valida os campos utilizados na busca pública
	 * @throws DAOException
	 */
	private void validaBuscaPublica() throws DAOException{
		
		if(getFiltroNacionalidade() == SolicitacaoRevalidacaoDiploma.NACIONALIDADE_PADRAO && isEmpty(getFiltroCpf())){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "CPF");
					
		}else if(getFiltroNacionalidade() != SolicitacaoRevalidacaoDiploma.NACIONALIDADE_PADRAO
				&& isEmpty(getFiltroNacionalidade())){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Passaporte");
			setFiltroCpf(null); 
		}
		
	}
	
	/**
	 * Retorna uma coleção de todas solicitações de revalidação de objeto.
	 * 
	 * <br />
	 * <br/><b>Método chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li>sigaa.war/graduacao/revalidacao_diplomas/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SolicitacaoRevalidacaoDiploma> getAllAgendamentos() throws DAOException{
		return getGenericDAO().findAll(SolicitacaoRevalidacaoDiploma.class);
	}
	
	/**
	 * Gera uma GRU para o pagamento da inscrição.
	 * 
	 * <br />
	 * <br/><b>Método chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li>sigaa.war/public/revalidacao_diplomas/view.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	public String gerarGRU() throws ArqException, NegocioException {
		try {
			getCurrentResponse().setContentType("application/pdf");
			getCurrentResponse().addHeader("Content-Disposition", "attachment;filename=GRU.pdf");
			GuiaRecolhimentoUniaoHelper.gerarPDF(getCurrentResponse().getOutputStream(), obj.getIdGru());
			FacesContext.getCurrentInstance().responseComplete();
		} catch (IOException e) {
			tratamentoErroPadrao(e);
		}
		return null;
			
	}
	
	
	/**
	 * Retorna uma coleção dos municípios de acordo com UF selecionado no formulário
	 * 
	 * <br />
	 * <br/><b>Método chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li>sigaa.war/public/revalidacao_diplomas/form_cadastro.jsp</li>
	 *	<li>sigaa.war/graduacao/revalidacao_diploma/form_cadastro.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Collection<SelectItem> getMunicipiosUf() {
		if(isEmpty(municipiosUf)){
			municipiosUf = new ArrayList<SelectItem>();
			municipiosUf.add(new SelectItem("0", "Selecione"));
		}	
		return municipiosUf;
	}

	public void setMunicipiosUf(Collection<SelectItem> municipiosUf) {
		this.municipiosUf = municipiosUf;
	}

	
	/**
	 * Verifica se a nacionalidade no inscrito é igual a padrão.
	 * 
	 * <br />
	 * <br/><b>Método chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li>/sigaa.war/public/revalidacao_diplomas/form_busca.jsp</li>
	 *	<li>/sigaa.war/public/revalidacao_diplomas/form_cadastro.jsp</li>
	 *	<li>/sigaa.war/graduacao/revalidacao_diploma/form_cadastro.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isNacionalidadePadrao() throws DAOException{
		if(!isEmpty(getFiltroNacionalidade()))
			return SolicitacaoRevalidacaoDiploma.NACIONALIDADE_PADRAO == getFiltroNacionalidade();
		else
			return false;
	}
	
	public boolean isNacionalidadeCadastro(){
		if(!isEmpty(obj.getPais()) && obj.getPais().getId() > 0)
			return SolicitacaoRevalidacaoDiploma.NACIONALIDADE_PADRAO == obj.getPais().getId();
		return false;
	}

	public String getFiltroData() {
		return filtroData;
	}

	/**
	 * Define a data de agendamento para a busca solicicações de revalidação de diploma.
	 * @param filtroData
	 */
	public void setFiltroData(String filtroData) {
		this.filtroData = filtroData;
	}

	public String getFiltroHorario() {
		return filtroHorario;
	}

	/**
	 * Define o horário do agendamento para a busca solicicações de revalidação de diploma.
	 * @param filtroHorario
	 */
	public void setFiltroHorario(String filtroHorario) {
		this.filtroHorario = filtroHorario;
	}

	public String getFiltroNome() {
		return filtroNome;
	}

	public void setFiltroNome(String filtroNome) {
		this.filtroNome = filtroNome;
	}

	public Integer getFiltroAgenda() {
		return filtroAgenda;
	}

	/**
	 * Define a agenda para a busca solicicações de revalidação de diploma.
	 * @param filtroAgenda
	 */
	public void setFiltroAgenda(Integer filtroAgenda) {
		this.filtroAgenda = filtroAgenda;
	}

	public boolean isFiltroEspera() {
		return filtroEspera;
	}

	/**
	 * Define se busca as solicicações de revalidação de diploma em fila de espera.
	 * @param filtroEspera
	 */
	public void setFiltroEspera(boolean filtroEspera) {
		this.filtroEspera = filtroEspera;
	}

	public Collection<SolicitacaoRevalidacaoDiploma> getSolicitacoesRevalidacaoDiploma() {
		return solicitacoesRevalidacaoDiploma;
	}

	public void setSolicitacoesRevalidacaoDiploma(Collection<SolicitacaoRevalidacaoDiploma> solicitacoesRevalidacaoDiploma) {
		this.solicitacoesRevalidacaoDiploma = solicitacoesRevalidacaoDiploma;
	}

	public Collection<SelectItem> getHorariosData() {
		if(isEmpty(horariosData))
			return new ArrayList<SelectItem>();
		return horariosData;
	}

	public void setHorariosData(Collection<SelectItem> horariosData) {
		this.horariosData = horariosData;
	}
	
	public void setFiltroEspera(Boolean filtroEspera){
		if(this.filtroEspera){
			this.filtroData = null;
			this.filtroHorario = null;
		}	
		this.filtroEspera = filtroEspera;
	}

	public boolean isBuscaNome() {
		return buscaNome;
	}

	public void setBuscaNome(boolean buscaNome) {
		this.buscaNome = buscaNome;
	}
	
	public boolean isBuscaData() {
		return buscaData;
	}

	public void setBuscaData(boolean buscaData) {
		this.buscaData = buscaData;
	}

	public String getRelatorioformato() {
		return relatorioformato;
	}

	public void setRelatorioformato(String relatorioformato) {
		this.relatorioformato = relatorioformato;
	}

	public void setAbertoInscricao(boolean abertoInscricao) {
		this.abertoInscricao = abertoInscricao;
	}

	public EditalRevalidacaoDiploma getEditalRevalidacaoDiploma() {
		return editalRevalidacaoDiploma;
	}

	public void setEditalRevalidacaoDiploma(
			EditalRevalidacaoDiploma editalRevalidacaoDiploma) {
		this.editalRevalidacaoDiploma = editalRevalidacaoDiploma;
	}

	public Long getFiltroCpf() {
		return filtroCpf;
	}

	/**
	 * Define o cpf para a busca solicicações de revalidação de diploma.
	 * @param filtroCpf
	 */
	public void setFiltroCpf(Long filtroCpf) {
		if(!isEmpty(filtroCpf))
			setFiltroPassaporte(null);
		this.filtroCpf = filtroCpf;
	}

	public String getFiltroPassaporte() {
		return filtroPassaporte;
	}


	/**
	 * Define o passaporte para a busca solicicações de revalidação de diploma.
	 * @param filtroCpf
	 */
	public void setFiltroPassaporte(String filtroPassaporte) {
		if(!isEmpty(filtroPassaporte))
			setFiltroCpf(null);
		this.filtroPassaporte = filtroPassaporte;
	}

	public Integer getFiltroNacionalidade() {
		return filtroNacionalidade;
	}

	public void setFiltroNacionalidade(Integer filtroNacionalidade) {
		this.filtroNacionalidade = filtroNacionalidade;
	}

	public void setAbertoReagendamento(boolean abertoReagendamento) {
		this.abertoReagendamento = abertoReagendamento;
	}
	
}
