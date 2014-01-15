package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.HorarioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.Horario;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;

/**
 * Serve para cadastrar os hor�rios das unidade respons�veis e dos n�veis de ensino. Sendo necess�rio informar a hora de in�cio, 
 * Hora final, Turno e a Ordem.  
 * @author Andre Dantas
 *
 */
@Component("horario")
@Scope("session")
public class HorarioMBean extends SigaaAbstractController<Horario> {

	
	/** Carrega os hor�rios j� cadastrados quando informados a unidade Respons�vel e o N�vel de Ensino. */ 
	private Collection<Horario> horariosDaUnidade;

	/** campos em String das horas de in�cio e fim para serem validadas antes de serem atribuidas a Date.*/
	private String horaInicio;

	/** Hora t�rmino do hor�rio da turma */
	private String horaFim;

	/** Retorna a hora Inicial */
	public String getHoraInicio() {
		return horaInicio;
	}

	/** Seta a hora inicial */
	public void setHoraInicio(String horaInicio) {
		this.horaInicio = horaInicio;
	}

	/** Retorna a hora Final */
	public String getHoraFim() {
		return horaFim;
	}

	/** Seta a hora final */
	public void setHoraFim(String horaFim) {
		this.horaFim = horaFim;
	}

	/** Construtor da classe */
	public HorarioMBean() {
		initObj(null);
	}

	/** 
	 * Usado quando o construtor � chamado, tem como fun��o inicializar
	 * todos os campos que ser�o utilizados. 
	 */
	private void initObj(Horario h) {
		Unidade un = new Unidade();
		char nivel = ' ';
		Short turno = null;
		if (h != null) {
			un = h.getUnidade();
			nivel = h.getNivel();
			turno = h.getTipo();
		}
		obj = new Horario();
		obj.setUnidade(un);
		obj.setNivel(nivel);
		obj.setTipo(turno);
	}

	/** 
	 * Serve para inicializar todos os atributos que ser�o utilizados ao decorrer da opera��es e testa tamb�m se o usu�rio 
	 * tem permiss�o para acessar essa funcionalidade, caso o usu�rio logado possua permiss�o ele ser� redirecionado
	 * para a tela do cadastro do hor�rio.
	 * 
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/menus/administracao.jsp</li>
	 * <li>/sigaa.war/ensino/formacao_complementar/menus/curso.jsp</li>
	 * <li>/sigaa.war/ensino/tecnico/menu/curso.jsp</li>
	 * <li>/sigaa.war/medio/menus/curso.jsp</li>
	 * </ul>
	 */
	public String iniciar() throws ArqException  {
		initObj(null);
		checkRole(SigaaPapeis.ADMINISTRADOR_SIGAA, SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR,
				SigaaPapeis.GESTOR_MEDIO);

		HorarioDao dao = getDAO(HorarioDao.class);
		try {
			if (isFormacaoComplementar())
				horariosDaUnidade =
					dao.findAtivoByUnidade((Unidade) getUsuarioLogado().getPermissao(
								SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR).iterator().next().getUnidadePapel(), getNivelEnsino());
			else
				horariosDaUnidade =
					dao.findAtivoByUnidade(getUsuarioLogado().getUnidade(), getNivelEnsino());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (isUnidadeNivelPreDefinidos()) {
			dao.lock(getParametrosAcademicos().getUnidade());
			obj.setUnidade(getParametrosAcademicos().getUnidade());
			obj.setNivel(getNivelEnsino());
		}
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_HORARIO.getId());
		setConfirmButton("Cadastrar");
		
		return forward(getFormPage());
	}
	
	/**
	 * Inicia o processo de atualiza��o de um hor�rio.
	 *  <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/Horario/lista.jsp.</li>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/Horario/form.jsp.</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarAtualizar() throws ArqException {
		setId();
		setOperacaoAtiva(SigaaListaComando.ALTERAR_HORARIO.getId());
		return carregarDadosAtualizar();
	}

	/**
	 * Carrega os dados necess�rios para a atualiza��o de um hor�rio tendo o id do objeto j� definido.
	 * 
	 * @return
	 * @throws DAOException
	 */
	private String carregarDadosAtualizar() throws DAOException {
		HorarioDao dao = getDAO(HorarioDao.class);
		try {
			obj = dao.refresh(obj);
			horariosDaUnidade = dao.findAtivoByUnidade(getUsuarioLogado().getUnidade(), getNivelEnsino());
		} finally {
			dao.close();
		}
		
		setConfirmButton("Alterar");
		
		return forward(getFormPage());
	}

	/**
	 * verifica se usuario ja possui unidade e nivel pre-definido
	 *  <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/Horario/lista.jsp.</li>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/Horario/form.jsp.</li>
	 * </ul>
	 * @param nivelEnsino
	 * @return
	 */
	public boolean isUnidadeNivelPreDefinidos() {
		char nivelEnsino = getNivelEnsino();
		return 	nivelEnsino == NivelEnsino.TECNICO || 
				nivelEnsino == NivelEnsino.FORMACAO_COMPLEMENTAR ||
				nivelEnsino == NivelEnsino.MEDIO;
	}
	
	/**
	 * Direciona o usu�rio para a p�gina de listagem de hor�rios, 
	 * onde � poss�vel editar ou excluir um hor�rio existente.
	 * <br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/formacao_complementar/menus/curso.jsp</li>
	 * <li>/sigaa.war/ensino/tecnico/menu/curso.jsp</li>
	 * <li>/sigaa.war/medio/menus/curso.jsp</li>
	 * </ul>
	 * 
	 */
	public String listar() throws DAOException {
		initObj(null);
		HorarioDao dao = getDAO(HorarioDao.class);
		try {
			horariosDaUnidade = dao.findAtivoByUnidade(getUsuarioLogado().getVinculoAtivo().getUnidade(), getNivelEnsino());
		} finally {
			dao.close();
		}	
		return forward(getListPage());
	}
	
	/**
	 * Informa��o do diret�rio que se encontra as view's.
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>N�o Invocado por JSP.</li>
	 * </ul>
	 */
	@Override
	public String getDirBase() {
		return "/ensino/Horario";
	}
	
	/** 
	 * M�todo utilizado quando no processo de cadastro de um novo hor�rio, o usu�rio preenche a 
	 * Unidade Respons�vel e o N�vel, sendo assim gerado abaixo do relat�rio uma tabela com todos os hor�rios 
	 * j� cadastrados com o n�vel e unidade informados.
	 * 
	 *  Invocado por /SIGAA/app/sigaa.ear/sigaa.war/ensino/horario/horarios.jsp
	 * @throws DAOException 
	 */
	public void carregarHorarios(ValueChangeEvent e) throws DAOException {
	    Object v = e != null ? e.getNewValue() : null;
		if (v != null && ((Character) v)!='0' ) {
			obj.setNivel((Character) v);
		HorarioDao dao = getDAO(HorarioDao.class);
		horariosDaUnidade = dao.findByUnidadeOtimizado(obj.getUnidade().getId(), obj.getNivel());
		}
	}
	
	/** 
	 * M�todo utilizado na tela de horarios, o usu�rio preenche a 
	 * Unidade Respons�vel e o N�vel, sendo assim gerado abaixo uma tabela com todos os hor�rios 
	 * j� cadastrados com o n�vel e unidade informados.
	 * 
	 *  Invocado por /SIGAA/app/sigaa.ear/sigaa.war/ensino/Horario/lista.jsp
	 * @throws ArqException 
	 */
	public String carregarHorarios() throws ArqException {
		erros = validaBusca();
		if (hasErrors()){
			return null;
		}
		HorarioDao dao = getDAO(HorarioDao.class);
		horariosDaUnidade = dao.findByUnidadeOtimizado(obj.getUnidade().getId(), obj.getNivel());
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_HORARIO.getId());
		return null;
	}
	
	/**
	 *  Valida��o dos campos obrigat�rios . 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>N�o Invocado por JSP.</li>
	 * </ul>
	 */
	public ListaMensagens validaBusca(){
		ListaMensagens lista = new ListaMensagens();
		if (obj.getUnidade().getId() == 0) 
			ValidatorUtil.validateRequired(null, "Unidade Respons�vel", lista);	
		if (obj.getNivel() == '0') 
			ValidatorUtil.validateRequired(null, "N�vel de Ensino", lista);
		
		return lista;
	}

	/**
	 * � invocado quando o usu�rio clica em cadastrar Hor�rio, s�o realizadas as valida��es 
	 * de hora, unidade, turno, ordem e n�vel de ensino. 
	 * 
	 * Invocado por /SIGAA/app/sigaa.ear/sigaa.war/ensino/horario/horarios.jsp
	 * @throws ArqException 
	 */
	public String cadastrarHorarios() throws ArqException {
		if( !(isOperacaoAtiva(SigaaListaComando.ALTERAR_HORARIO.getId()) || isOperacaoAtiva(SigaaListaComando.CADASTRAR_HORARIO.getId()) )){
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return cancelar();
		}
		erros = obj.validate();
		if (hasErrors()){
			return null;
		}
		
		HorarioDao dao = getDAO(HorarioDao.class);
		
		try {
			if(getConfirmButton().equalsIgnoreCase("cadastrar")) {
				obj.setCodMovimento(SigaaListaComando.CADASTRAR_HORARIO);
				prepareMovimento(SigaaListaComando.CADASTRAR_HORARIO);
			}
			else if(getConfirmButton().equalsIgnoreCase("alterar")) {
				obj.setCodMovimento(SigaaListaComando.ALTERAR_HORARIO);
				prepareMovimento(SigaaListaComando.ALTERAR_HORARIO);
			}
			executeWithoutClosingSession(obj, getCurrentRequest());
			carregarHorarios(null);
			initObj(obj);
			horariosDaUnidade = dao.findAtivoByUnidade(getUsuarioLogado().getUnidade(), getNivelEnsino());
			removeOperacaoAtiva();
		} catch (br.ufrn.arq.erros.NegocioException nex) {
			addMensagemErro(nex.getMessage());
			if(getConfirmButton().equalsIgnoreCase("alterar"))
				return carregarDadosAtualizar();
			return null;
		} catch (Exception ex) {
			notifyError(ex);
			ex.printStackTrace();
			addMensagemErroPadrao();
		} finally {
			dao.close();
		}
		if(getConfirmButton().equalsIgnoreCase("cadastrar")) {
			addMessage("Hor�rio cadastrado com sucesso", TipoMensagemUFRN.INFORMATION);
			carregarHorarios();
			return null;
		}
		else {
			addMessage("Hor�rio alterado com sucesso", TipoMensagemUFRN.INFORMATION);
			return listar();
		}
	}
	
	@Override
	public String remover() throws ArqException {
		setId();
		GenericDAO dao = getGenericDAO();
		try {
			obj = dao.findByPrimaryKey(obj.getId(), Horario.class);
			if (obj == null) {
				addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
				return cancelar();
			}
			//Verificar se existe alguma refer�ncia ao hor�rio
			int cont = dao.count("from ensino.horario_turma where id_horario = " + obj.getId());
			if(cont > 0) {
				//Verificar se existe alguma turma aberta relacionada ao hor�rio
				cont = dao.count("from ensino.horario_turma ht " +
									"inner join ensino.turma t using (id_turma) " +
								"where id_situacao_turma in " + UFRNUtils.gerarStringIn(SituacaoTurma.getSituacoesAbertas()) + 
									" and id_horario = " + obj.getId());
				if(cont > 0) {
					addMensagemErro("N�o � poss�vel remover o hor�rio pois existem turmas abertas relacionadas a ele.");
					return null;
				}
				prepareMovimento(ArqListaComando.DESATIVAR);
				super.inativar();
			}
			else { //Caso contr�rio, remov�-lo
				prepareMovimento(ArqListaComando.REMOVER);
				MovimentoCadastro mov = new MovimentoCadastro(obj, ArqListaComando.REMOVER);
				execute(mov);
				addMensagem(OPERACAO_SUCESSO);
			}
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		} finally {
			dao.close();
		}
		
		return carregarHorarios();
	}

	/**
	 * � criado um select item para que o usu�rio possa escolher o
	 * turno que o mesmo, deseja cadastrar o hor�rio.
	 * 
	 * Invocado por /SIGAA/app/sigaa.ear/sigaa.war/ensino/horario/horarios.jsp
	 */
	public Collection<SelectItem> getTurnos() {
		ArrayList<SelectItem> turnos = new ArrayList<SelectItem>(0);
		turnos.add(new SelectItem(Horario.MANHA+"", "MANH�"));
		turnos.add(new SelectItem(Horario.TARDE+"", "TARDE"));
		turnos.add(new SelectItem(Horario.NOITE+"", "NOITE"));
		return turnos;
	}

	/**
	 * Retorna os Hor�rios da Unidade.
	 */
	public Collection<Horario> getHorariosDaUnidade() {
		return horariosDaUnidade;
	}

	/**
	 * Seta os hor�rios da unidade.
	 * @param horariosDaUnidade
	 */
	public void setHorariosDaUnidade(Collection<Horario> horariosDaUnidade) {
		this.horariosDaUnidade = horariosDaUnidade;
	}

}