/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/10/2010
 *
 */
package br.ufrn.sigaa.ensino.metropoledigital.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.faces.component.html.HtmlDataTable;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ArrayUtils;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaHorarioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaHorario;
import br.ufrn.sigaa.ensino.dominio.OpcaoHorario;
import br.ufrn.sigaa.ensino.dominio.OpcaoModulo;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.metropoledigital.negocio.MetropoleDigitalHelper;
import br.ufrn.sigaa.ensino.negocio.MovimentoMatriculaHorario;
import br.ufrn.sigaa.ensino.tecnico.dao.DiscenteTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dao.ModuloDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.Modulo;
import br.ufrn.sigaa.parametros.dominio.ParametrosTecnico;

/**
 * Controlador reponsável pelo gerenciamento da matrícula por horário.
 * 
 * @author Leonardo Campos
 *
 */
@SuppressWarnings("serial")
@Component("matriculaModuloAvancadoMBean")
@Scope("session")
public class MatriculaModuloAvancadoMBean extends SigaaAbstractController<MatriculaHorario> implements OperadorDiscente {

	private List<Modulo> modulos = new ArrayList<Modulo>();
	
	private Integer[] idEnfase;
	
	private List<Collection<OpcaoHorario>> opcoes = new ArrayList<Collection<OpcaoHorario>>();
	
	private String[] opcaoEnfase;
	
	private int[] ordens;
	
    private HtmlDataTable[] itens;
    
    /** md5 gerado pelas preferências escolhidas numa matrícula por horário */
	private String md5;
	
	private DiscenteTecnico discente;
	
	private List<Turma> turmas;
	
	private int idQuantidadeModulos = 4;
	
	public MatriculaModuloAvancadoMBean() throws ArqException {
		clear();
	}

	/**
	 * Limpa os dados do MBean para sua utilização em uma nova operação de matrícula por horário.
	 * @throws ArqException 
	 * 
	 */
	private void clear() throws ArqException {
		obj = new MatriculaHorario();
		opcoes = new ArrayList<Collection<OpcaoHorario>>();
		md5 = "";

		if ( isPortalDiscente() ) {
			setDiscente(getUsuarioLogado().getDiscenteAtivo().getDiscente());
			carregarModulos();
			idQuantidadeModulos = modulos.size();
			
			if(modulos != null && !modulos.isEmpty()){
				int size = modulos.size();
				
				idEnfase = new Integer[size];
				opcaoEnfase = new String[size];
				ordens = new int[size];
				itens = new HtmlDataTable[size];
				
				for (int i = 0; i < size; i++) {
					idEnfase[i] = 0;
					opcaoEnfase[i] = "";
					ordens[i] = 1;
					itens[i] = new HtmlDataTable();
				}
			}
		}
	}

	/**
	 * Início do caso de uso para o discente. Verifica as permissões e popula as informações
	 * para a realização do caso de uso.
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarDiscente() throws ArqException{
		if(!(getUsuarioLogado().getDiscenteAtivo().isTecnico() 
				&& MetropoleDigitalHelper.isMetropoleDigital(getUsuarioLogado().getDiscenteAtivo().getDiscente()))){
			addMensagemErro("Somente alunos do Metrópole Digital podem solicitar matrícula no módulo avançado.");
			return cancelar();
		}
		
		setDiscente(getUsuarioLogado().getDiscenteAtivo().getDiscente());
		
		return iniciar();
	}

	/**
	 * Início o caso de uso para o coordenador de curso. Checa os papéis e
	 * encaminha para busca de discente para efetuar a matrícula.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *	      <li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/coordenacao.jsp</li>
	 *	</ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarCoordenacao() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.COORDENADOR_TECNICO);
		if(ArrayUtils.idContains( getCursoAtualCoordenacao().getId(), ParametroHelper.getInstance().getParametroIntArray(ParametrosTecnico.ID_CURSO_METROPOLE_DIGITAL))) {
			addMensagemErro("No momento esta operação só está disponível para a coordenação do Metrópole Digital.");
			return cancelar();
		}
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.MATRICULA_HORARIO);
		return buscaDiscenteMBean.popular();
	}

	/**
	 * Popula as informações necessárias e inicia o caso de uso de matrícula por
	 * horário.
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	private String iniciar() throws DAOException, ArqException {
		carregarModulos();
		clear();
		
		int ano = ParametroHelper.getInstance().getParametroInt(ParametrosTecnico.ANO_INGRESSO_PERMITE_MATRICULA_MODULO_AVANCADO);
		if(discente.getAnoIngresso() != ano)
			addMensagemErro("A matrícula está habilitada apenas para alunos da turma " + ano);
		
		if ( !discente.isAtivo() )
			addMensagemErro("Apenas alunos ativos podem realizar matrículas.");
		
		CalendarioAcademico cal = getCalendarioVigente();
		if( !CalendarUtils.isDentroPeriodo(cal.getInicioMatriculaOnline(), cal.getFimMatriculaOnline()) )
			addMensagemErro("Não está no período oficial de matrículas on-line");

		if ( hasOnlyErrors() ) {
			return cancelar();
		} 
		
		obj.setDiscente(discente.getDiscente());
		obj.setAno(cal.getAno());
		obj.setPeriodo(cal.getPeriodo());
		
		MatriculaHorario matricula = getDAO(MatriculaHorarioDao.class).findByAnoPeriodoDiscente(obj.getDiscente(), obj.getAno(), obj.getPeriodo());
		if(matricula != null){
			if(isDiscenteLogado()){
				addMensagemErro("Você já realizou solicitação de matrícula para o ano-período atual.");
				return cancelar();
			} else {
				addMensagemErro("O discente selecionado já realizou solicitação de matrícula para o ano-período atual.");
				return iniciarCoordenacao();
			}
		}
		
		setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
		prepareMovimento(ArqListaComando.CADASTRAR);
		
		return telaPreferenciaHorario();
	}

	/**
	 * Carrega as informações da solicitação de matrícula do aluno e encaminha para a tela do comprovante.
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String verComprovante() throws ArqException{
		clear();
		obj.setDiscente(getUsuarioLogado().getDiscenteAtivo().getDiscente());
		obj.setAno(getCalendarioVigente().getAno());
		obj.setPeriodo(getCalendarioVigente().getPeriodo());
		
		MatriculaHorario matricula = getDAO(MatriculaHorarioDao.class).findByAnoPeriodoDiscente(obj.getDiscente(), obj.getAno(), obj.getPeriodo());
		if(matricula == null){
			addMensagemWarning("Você ainda não realizou matrícula on-line no semestre " + getCalendarioVigente().getAnoPeriodo());
			return null;
		}
		obj = matricula;
		generateMD5();
		return telaComprovante();
	}
	
	/**
	 * Gera uma MD5 com base nas preferências de matrícula por horário selecionadas.
	 */
	private void generateMD5() {
		StringBuilder ids = new StringBuilder();
		for(OpcaoModulo opm: obj.getOpcoesModulo()){
			ids.append(opm.getModulo().getId());
			for(OpcaoHorario oph: opm.getOpcoesHorario()){
				ids.append(oph.getOpcao());
			}
		}
		md5 = UFRNUtils.toMD5(ids.toString()).toUpperCase();
	}
	
	/**
	 * Carrega as opções de módulos (ênfases) para escolha do aluno.
	 * @throws DAOException 
	 */
	private void carregarModulos() throws DAOException {
		ModuloDao dao = getDAO(ModuloDao.class);
		try {
			
			//CENEP ou ANGICOS ou CAICO 
			if ( discente.getOpcaoPoloGrupo().isCenep() || discente.getOpcaoPoloGrupo().isAngicos() 
					|| discente.getOpcaoPoloGrupo().isCaico() ) {
				modulos = (List<Modulo>) dao.findByIdModulos(new int[]{96054103});
			} 
		
			//MOSSORO
			if ( discente.getOpcaoPoloGrupo().isMossoro() ) {
				modulos = (List<Modulo>) dao.findByIdModulos(new int[]{96054103,96054138});
			}
		
			//NATAL
			if ( discente.getOpcaoPoloGrupo().isNatal() ) {
				modulos = (List<Modulo>) dao.findByIdModulos(ParametroHelper.getInstance().getParametroIntArray(ParametrosTecnico.ID_ENFASES_MODULO_AVANCADO_METROPOLE_DIGITAL));
			}
			
		} finally {
			dao.close();
		}
	}

	public boolean isSegundaOpcao() {
		return idQuantidadeModulos >= 2;
	}
	
	public boolean isTerceiraOpcao() {
		return idQuantidadeModulos >= 3;
	}

	public boolean isQuartaOpcao() {
		return idQuantidadeModulos >= 4;
	}

	/**
	 * Método chamado quando um determinado módulo (ênfase) é selecionado na
	 * view. Popula as opções de horário, caso ainda não tenha sido feito.
	 * Inverte a ordem das opções de horário das ênfases principal e secundária,
	 * caso se aplique.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *	      <li>sigaa.war/ensino/matricula_horario/preferencia_horario.jsp</li>
	 *	</ul>
	 * @param event
	 * @throws DAOException
	 */
	public void selecionarModulo(ValueChangeEvent event) throws DAOException {
		Integer id = (Integer) event.getNewValue();
		if(obj.getOpcoesModulo().isEmpty()){
			int pos = 0;
			for (int i = 0; i < modulos.size(); i++) {
				if(modulos.get(i).getId() == id)
					pos = i;
			}
			
			MatriculaHorarioDao dao = getDAO(MatriculaHorarioDao.class);
			
			OpcaoModulo op = new OpcaoModulo();
			Modulo modulo = modulos.get(pos);
			op.setModulo(modulo);
			op.setOrdem(1);
			obj.addOpcaoModulo(op);
			opcoes.add( dao.findOpcoesByModulo(modulo, getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo(), discente) );
			
			op = new OpcaoModulo();
			Modulo modulo2 = modulos.get(Math.abs(--pos));
			op.setModulo(modulo2);
			op.setOrdem(2);
			obj.addOpcaoModulo(op);
			opcoes.add( dao.findOpcoesByModulo(modulo2, getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo(), discente) );			
			
		} else {
			Collections.swap(obj.getOpcoesModulo(), 0, 1);
			Collections.swap(opcoes, 0, 1);
			
			int aux = ordens[0];
			ordens[0] =  ordens[1];
			ordens[1] = aux;
			
			int ordem = 1;
			for(OpcaoModulo op: obj.getOpcoesModulo())
				op.setOrdem(ordem++);
		}
	}
	
	public void selecionarPrimeiraOpcao(ValueChangeEvent event) throws DAOException {
		Integer id = (Integer) event.getNewValue();
		
		int pos = 0;
		for (int i = 0; i < modulos.size(); i++) {
			if(modulos.get(i).getId() == id)
				pos = i;
		}
		
		MatriculaHorarioDao dao = getDAO(MatriculaHorarioDao.class);
		
		OpcaoModulo op = new OpcaoModulo();
		Modulo modulo = modulos.get(pos);
		modulos.remove(pos);
		op.setModulo(modulo);
		op.setOrdem(1);
		obj.addOpcaoModulo(op);
		opcoes.add( dao.findOpcoesByModulo(modulo, getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo(), discente) );
	}
	
	public void selecionarSegundaOpcao(ValueChangeEvent event) throws DAOException {
		Integer id = (Integer) event.getNewValue();
		
		int pos = 0;
		for (int i = 0; i < modulos.size(); i++) {
			if(modulos.get(i).getId() == id)
				pos = i;
		}
		
		MatriculaHorarioDao dao = getDAO(MatriculaHorarioDao.class);
		
		OpcaoModulo op = new OpcaoModulo();
		Modulo modulo = modulos.get(pos);
		modulos.remove(pos);
		op.setModulo(modulo);
		op.setOrdem(2);
		obj.addOpcaoModulo(op);
		opcoes.add( dao.findOpcoesByModulo(modulo, getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo(), discente) );
		
	}

	public void selecionarTerceiraOpcao(ValueChangeEvent event) throws DAOException {
		Integer id = (Integer) event.getNewValue();
		
		int pos = 0;
		for (int i = 0; i < modulos.size(); i++) {
			if(modulos.get(i).getId() == id)
				pos = i;
		}
		
		MatriculaHorarioDao dao = getDAO(MatriculaHorarioDao.class);
		
		OpcaoModulo op = new OpcaoModulo();
		Modulo modulo = modulos.get(pos);
		modulos.remove(pos);
		op.setModulo(modulo);
		op.setOrdem(3);
		obj.addOpcaoModulo(op);
		opcoes.add( dao.findOpcoesByModulo(modulo, getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo(), discente) );
		
		op = new OpcaoModulo();
		Modulo modulo2 = modulos.iterator().next();
		op.setModulo(modulo2);
		op.setOrdem(4);
		obj.addOpcaoModulo(op);
		idEnfase[3] = modulo2.getId();
		opcoes.add( dao.findOpcoesByModulo(modulo2, getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo(), discente) );
			
	}
	
	/**
	 * Adiciona um opção de horário selecionada à lista de opções da ênfase
	 * principal ou secundária, de acordo com o parâmetro tipo.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *	      <li>sigaa.war/ensino/matricula_horario/preferencia_horario.jsp</li>
	 *	</ul>
	 */
	public void adicionarOpcaoHorario(){
		if(obj.getOpcoesModulo().isEmpty()){
			addMensagemErroAjax("Informe primeiro a ênfase.");
			return;
		}
		
		Integer tipo = getParameterInt("tipo");
		
		if(opcoes.get(tipo).isEmpty()){
			addMensagemErroAjax("Todas as opções de horário já foram adicionadas.");
			return;
		}
		
		String opcao = opcaoEnfase[tipo];		
		
		OpcaoHorario opcaoHorario = new OpcaoHorario();
		opcaoHorario.setOrdem(ordens[tipo]++);
		opcaoHorario.setOpcao(opcao);
		
		obj.getOpcoesModulo().get(tipo).addOpcaoHorario(opcaoHorario);
		
		opcoes.get(tipo).remove(opcaoHorario);
		
	}

	/**
	 * Remove a opção de horário selecionada da lista de opções selecionadas da
	 * ênfase primária e coloca novamente na lista de opções passíveis de
	 * seleção para aquela ênfase.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *	      <li>sigaa.war/ensino/matricula_horario/preferencia_horario.jsp</li>
	 *	</ul>
	 * @return
	 */
	public String removerOpcaoHorarioEnfasePrimaria() {
		OpcaoHorario opcao = (OpcaoHorario) itens[0].getRowData();
		obj.getOpcoesModulo().get(0).removeOpcaoHorario(opcao);
		opcoes.get(0).add(opcao);
		ordens[0]--;
		reordenarOpcoes(0);
		return null;
	}
	
	/**
	 * Remove a opção de horário selecionada da lista de opções selecionadas da
	 * ênfase secundária e coloca novamente na lista de opções passíveis de
	 * seleção para aquela ênfase.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *	      <li>sigaa.war/ensino/matricula_horario/preferencia_horario.jsp</li>
	 *	</ul>
	 * @return
	 */
	public String removerOpcaoHorarioEnfaseSecundaria() {
		OpcaoHorario opcao = (OpcaoHorario) itens[1].getRowData();
		obj.getOpcoesModulo().get(1).removeOpcaoHorario(opcao);
		opcoes.get(1).add(opcao);
		ordens[1]--;
		reordenarOpcoes(1);
		return null;
	}
	
	/**
	 * Remove a opção de horário selecionada da lista de opções selecionadas da
	 * ênfase terciária e coloca novamente na lista de opções passíveis de
	 * seleção para aquela ênfase.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *	      <li>sigaa.war/ensino/matricula_horario/preferencia_horario.jsp</li>
	 *	</ul>
	 * @return
	 */
	public String removerOpcaoHorarioEnfaseTerciaria() {
		OpcaoHorario opcao = (OpcaoHorario) itens[2].getRowData();
		obj.getOpcoesModulo().get(2).removeOpcaoHorario(opcao);
		opcoes.get(2).add(opcao);
		ordens[2]--;
		reordenarOpcoes(2);
		return null;
	}
	
	/**
	 * Remove a opção de horário selecionada da lista de opções selecionadas da
	 * ênfase quaternária e coloca novamente na lista de opções passíveis de
	 * seleção para aquela ênfase.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *	      <li>sigaa.war/ensino/matricula_horario/preferencia_horario.jsp</li>
	 *	</ul>
	 * @return
	 */
	public String removerOpcaoHorarioEnfaseQuaternaria() {
		OpcaoHorario opcao = (OpcaoHorario) itens[3].getRowData();
		obj.getOpcoesModulo().get(3).removeOpcaoHorario(opcao);
		opcoes.get(3).add(opcao);
		ordens[3]--;
		reordenarOpcoes(3);
		return null;
	}
	
	/**
	 * Reordena as opções de horário da ênfase passada como argumento.
	 * @param i Se 0, ênfase principal. Se 1, ênfase secundária.
	 */
	private void reordenarOpcoes(int i) {
		if(!obj.getOpcoesModulo().get(i).getOpcoesHorario().isEmpty()){
			int ordem = 1;
			for(OpcaoHorario op: obj.getOpcoesModulo().get(i).getOpcoesHorario())
				op.setOrdem(ordem++);
		}
	}
	
	/**
	 * Cadastra as informações da solicitação de matrícula na base de dados.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *	      <li>sigaa.war/ensino/matricula_horario/preferencia_horario.jsp</li>
	 *	</ul>
	 */
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		if(!checkOperacaoAtiva(ArqListaComando.CADASTRAR.getId())){
			return cancelar();
		}
		
		if(opcoes.isEmpty()){
			addMensagemErro("Selecione suas Opções de Ênfase.");
			return null;
		}
		
		boolean erro = false;
		if(!opcoes.get(0).isEmpty()){
			addMensagemErro("1ª Opção de Ênfase: ainda há opções de horário disponíveis.");
			erro = true;
		}
		if( isSegundaOpcao() && !opcoes.get(1).isEmpty()){
			addMensagemErro("2ª Opção de Ênfase: ainda há opções de horário disponíveis.");
			erro = true;
		}
		if( isTerceiraOpcao() && !opcoes.get(2).isEmpty()){
			addMensagemErro("3ª Opção de Ênfase: ainda há opções de horário disponíveis.");
			erro = true;
		}
		if(erro){
			addMensagemErro("Você deve adicionar TODAS as opções de horário para concluir a solicitação de matrícula.");
		}
		
		if (!hasErrors()) {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			if (obj.getId() == 0) {
				
				mov.setCodMovimento(ArqListaComando.CADASTRAR);
				try {
					execute(mov);
				} catch (Exception e) {
					notifyError(e);
					addMensagemErroPadrao();
					e.printStackTrace();
					return telaPreferenciaHorario();
				}
				
				removeOperacaoAtiva();
				generateMD5();
				return telaComprovante();
			}
		}
		return null;
	}
	
	/** 
	 * Inicia o caso de uso do processamento de matrícula, populando as informações necessárias.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *	      <li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/coordenacao.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public String preProcessar() throws ArqException{
		prepareMovimento(SigaaListaComando.PROCESSAR_MATRICULA_HORARIO);
		turmas = new ArrayList<Turma>();
		return telaProcessamento();
	}
	
	/**
	 * Invoca o processador para efetuar o processamento das matrículas.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *	      <li>sigaa.war/ensino/matricula_horario/processamento.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public String processar() throws ArqException {
		prepareMovimento(SigaaListaComando.PROCESSAR_MATRICULA_HORARIO);
		MovimentoMatriculaHorario mov = new MovimentoMatriculaHorario();
		mov.setCodMovimento(SigaaListaComando.PROCESSAR_MATRICULA_HORARIO);
		mov.setCalendarioAcademicoAtual(getCalendarioVigente());
		
		try {
			turmas = execute(mov);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
		
		return null;
	}
	
	/**
	 * Confirma o resultado do processamento invocando o processador para persistir as matrículas nas turmas.
	 *  <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *	      <li>sigaa.war/ensino/matricula_horario/processamento.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public String confirmarProcessamento() throws ArqException {
		if(ValidatorUtil.isEmpty(turmas)){
			addMensagemErro("Não é possível confirmar o processamento sem haver turmas processadas.");
			return null;
		}
		prepareMovimento(SigaaListaComando.PERSISTIR_MATRICULA_HORARIO);
		MovimentoMatriculaHorario mov = new MovimentoMatriculaHorario();
		mov.setCodMovimento(SigaaListaComando.PERSISTIR_MATRICULA_HORARIO);
		mov.setTurmas(turmas);
		mov.setCalendarioAcademicoAtual(getCalendarioVigente());
		
		try {
			execute(mov);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
		
		addMensagemInformation("Processamento de matrícula confirmado com sucesso!");
		
		return cancelar();
	}
	
	/**
	 * Encaminha para a tela do processamento de matrícula.
	 * @return
	 */
	public String telaProcessamento(){
		return forward("/ensino/matricula_horario/processamento.jsp");
	}
	
	/**
	 * Encaminha para a tela de seleção de preferências de ênfase e opções de horário.
	 * @return
	 */
	public String telaPreferenciaHorario(){
		return forward("/ensino/matricula_horario/preferencia_horario.jsp");
	}
	
	/**
	 * Encaminha para a tela do comprovante de solicitação de matrícula.
	 * @return
	 */
	public String telaComprovante() {
		return forward("/ensino/matricula_horario/comprovante.jsp");
	}
	
	public List<Modulo> getModulos() {
		return modulos;
	}

	public void setModulos(List<Modulo> modulos) {
		this.modulos = modulos;
	}

	public Collection<SelectItem> getModulosCombo(){
		return toSelectItems(modulos, "id", "descricao");
	}
	
	public Collection<SelectItem> getOpcoesPreferencialCombo(){
		if(opcoes.isEmpty()){
			return emptyCombo("-- SELECIONE A 1ª OPÇÃO DE ÊNFASE --");
		}
		Collection<OpcaoHorario> col = opcoes.get(0);
		if(col.isEmpty()){
			return emptyCombo("-- NENHUMA OPÇÃO DE HORÁRIO DISPONÍVEL --");
		}
		return toSelectItems(col, "opcao", "descricao");
	}

	/**
	 * Retorna uma coleção de selectItems com somente um elemento cujo rótulo
	 * contém o texto passado como argumento.
	 * 
	 * @param label Texto do rótulo 
	 * @return
	 */
	private Collection<SelectItem> emptyCombo(String label) {
		SelectItem item = new SelectItem("0", label);
		Collection<SelectItem> res = new ArrayList<SelectItem>();
		res.add(item);
		return res;
	}
	
	public Collection<SelectItem> getOpcoesSecundariaCombo(){
		if(opcoes.isEmpty() || opcoes.size() < 2){
			return emptyCombo("-- SELECIONE A 2ª OPÇÃO DE ÊNFASE --");
		}
		Collection<OpcaoHorario> col = opcoes.get(1);
		if(col.isEmpty()){
			return emptyCombo("-- NENHUMA OPÇÃO DE HORÁRIO DISPONÍVEL --");
		}
		return toSelectItems(col, "opcao", "descricao");
	}
	
	public Collection<SelectItem> getOpcoesTerciariaCombo(){
		if(opcoes.isEmpty() || opcoes.size() < 3){
			return emptyCombo("-- SELECIONE A 2ª OPÇÃO DE ÊNFASE --");
		}
		Collection<OpcaoHorario> col = opcoes.get(2);
		if(col.isEmpty()){
			return emptyCombo("-- NENHUMA OPÇÃO DE HORÁRIO DISPONÍVEL --");
		}
		return toSelectItems(col, "opcao", "descricao");
	}
	
	public Collection<SelectItem> getOpcoesQuaternariaCombo(){
		if(opcoes.isEmpty() || opcoes.size() < 4){
			return emptyCombo("-- SELECIONE A 3ª OPÇÃO DE ÊNFASE --");
		}
		Collection<OpcaoHorario> col = opcoes.get(3);
		if(col.isEmpty()){
			return emptyCombo("-- NENHUMA OPÇÃO DE HORÁRIO DISPONÍVEL --");
		}
		return toSelectItems(col, "opcao", "descricao");
	}

	public List<Collection<OpcaoHorario>> getOpcoes() {
		return opcoes;
	}

	public void setOpcoes(List<Collection<OpcaoHorario>> opcoes) {
		this.opcoes = opcoes;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String selecionaDiscente() throws ArqException {
		return iniciar();
	}

	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		try {
			this.discente = getDAO(DiscenteTecnicoDao.class).findByPrimaryKey(discente.getId(), DiscenteTecnico.class);
		} catch (DAOException e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErro("Não foi possível carregar o discente escolhido");
		}
	}

	public DiscenteAdapter getDiscente() {
		return discente;
	}

	/** 
	 * Verifica se o ator da operação é um discente.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/matricula/cabecalho_botoes_superiores.jsp</li>
	 * </ul>
	 */
	public boolean isDiscenteLogado() {
		return getUsuarioLogado().getDiscenteAtivo() != null;
	}

	public List<Turma> getTurmas() {
		return turmas;
	}

	public void setTurmas(List<Turma> turmas) {
		this.turmas = turmas;
	}

	public String[] getOpcaoEnfase() {
		return opcaoEnfase;
	}

	public void setOpcaoEnfase(String[] opcaoEnfase) {
		this.opcaoEnfase = opcaoEnfase;
	}

	public HtmlDataTable[] getItens() {
		return itens;
	}

	public void setItens(HtmlDataTable[] itens) {
		this.itens = itens;
	}

	public Integer[] getIdEnfase() {
		return idEnfase;
	}

	public void setIdEnfase(Integer[] idEnfase) {
		this.idEnfase = idEnfase;
	}
}
