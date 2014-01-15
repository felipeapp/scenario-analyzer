/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Controlador repons�vel pelo gerenciamento da matr�cula por hor�rio.
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
    
    /** md5 gerado pelas prefer�ncias escolhidas numa matr�cula por hor�rio */
	private String md5;
	
	private DiscenteTecnico discente;
	
	private List<Turma> turmas;
	
	private int idQuantidadeModulos = 4;
	
	public MatriculaModuloAvancadoMBean() throws ArqException {
		clear();
	}

	/**
	 * Limpa os dados do MBean para sua utiliza��o em uma nova opera��o de matr�cula por hor�rio.
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
	 * In�cio do caso de uso para o discente. Verifica as permiss�es e popula as informa��es
	 * para a realiza��o do caso de uso.
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarDiscente() throws ArqException{
		if(!(getUsuarioLogado().getDiscenteAtivo().isTecnico() 
				&& MetropoleDigitalHelper.isMetropoleDigital(getUsuarioLogado().getDiscenteAtivo().getDiscente()))){
			addMensagemErro("Somente alunos do Metr�pole Digital podem solicitar matr�cula no m�dulo avan�ado.");
			return cancelar();
		}
		
		setDiscente(getUsuarioLogado().getDiscenteAtivo().getDiscente());
		
		return iniciar();
	}

	/**
	 * In�cio o caso de uso para o coordenador de curso. Checa os pap�is e
	 * encaminha para busca de discente para efetuar a matr�cula.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *	      <li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/coordenacao.jsp</li>
	 *	</ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarCoordenacao() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.COORDENADOR_TECNICO);
		if(ArrayUtils.idContains( getCursoAtualCoordenacao().getId(), ParametroHelper.getInstance().getParametroIntArray(ParametrosTecnico.ID_CURSO_METROPOLE_DIGITAL))) {
			addMensagemErro("No momento esta opera��o s� est� dispon�vel para a coordena��o do Metr�pole Digital.");
			return cancelar();
		}
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.MATRICULA_HORARIO);
		return buscaDiscenteMBean.popular();
	}

	/**
	 * Popula as informa��es necess�rias e inicia o caso de uso de matr�cula por
	 * hor�rio.
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	private String iniciar() throws DAOException, ArqException {
		carregarModulos();
		clear();
		
		int ano = ParametroHelper.getInstance().getParametroInt(ParametrosTecnico.ANO_INGRESSO_PERMITE_MATRICULA_MODULO_AVANCADO);
		if(discente.getAnoIngresso() != ano)
			addMensagemErro("A matr�cula est� habilitada apenas para alunos da turma " + ano);
		
		if ( !discente.isAtivo() )
			addMensagemErro("Apenas alunos ativos podem realizar matr�culas.");
		
		CalendarioAcademico cal = getCalendarioVigente();
		if( !CalendarUtils.isDentroPeriodo(cal.getInicioMatriculaOnline(), cal.getFimMatriculaOnline()) )
			addMensagemErro("N�o est� no per�odo oficial de matr�culas on-line");

		if ( hasOnlyErrors() ) {
			return cancelar();
		} 
		
		obj.setDiscente(discente.getDiscente());
		obj.setAno(cal.getAno());
		obj.setPeriodo(cal.getPeriodo());
		
		MatriculaHorario matricula = getDAO(MatriculaHorarioDao.class).findByAnoPeriodoDiscente(obj.getDiscente(), obj.getAno(), obj.getPeriodo());
		if(matricula != null){
			if(isDiscenteLogado()){
				addMensagemErro("Voc� j� realizou solicita��o de matr�cula para o ano-per�odo atual.");
				return cancelar();
			} else {
				addMensagemErro("O discente selecionado j� realizou solicita��o de matr�cula para o ano-per�odo atual.");
				return iniciarCoordenacao();
			}
		}
		
		setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
		prepareMovimento(ArqListaComando.CADASTRAR);
		
		return telaPreferenciaHorario();
	}

	/**
	 * Carrega as informa��es da solicita��o de matr�cula do aluno e encaminha para a tela do comprovante.
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
			addMensagemWarning("Voc� ainda n�o realizou matr�cula on-line no semestre " + getCalendarioVigente().getAnoPeriodo());
			return null;
		}
		obj = matricula;
		generateMD5();
		return telaComprovante();
	}
	
	/**
	 * Gera uma MD5 com base nas prefer�ncias de matr�cula por hor�rio selecionadas.
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
	 * Carrega as op��es de m�dulos (�nfases) para escolha do aluno.
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
	 * M�todo chamado quando um determinado m�dulo (�nfase) � selecionado na
	 * view. Popula as op��es de hor�rio, caso ainda n�o tenha sido feito.
	 * Inverte a ordem das op��es de hor�rio das �nfases principal e secund�ria,
	 * caso se aplique.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Adiciona um op��o de hor�rio selecionada � lista de op��es da �nfase
	 * principal ou secund�ria, de acordo com o par�metro tipo.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *	      <li>sigaa.war/ensino/matricula_horario/preferencia_horario.jsp</li>
	 *	</ul>
	 */
	public void adicionarOpcaoHorario(){
		if(obj.getOpcoesModulo().isEmpty()){
			addMensagemErroAjax("Informe primeiro a �nfase.");
			return;
		}
		
		Integer tipo = getParameterInt("tipo");
		
		if(opcoes.get(tipo).isEmpty()){
			addMensagemErroAjax("Todas as op��es de hor�rio j� foram adicionadas.");
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
	 * Remove a op��o de hor�rio selecionada da lista de op��es selecionadas da
	 * �nfase prim�ria e coloca novamente na lista de op��es pass�veis de
	 * sele��o para aquela �nfase.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Remove a op��o de hor�rio selecionada da lista de op��es selecionadas da
	 * �nfase secund�ria e coloca novamente na lista de op��es pass�veis de
	 * sele��o para aquela �nfase.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Remove a op��o de hor�rio selecionada da lista de op��es selecionadas da
	 * �nfase terci�ria e coloca novamente na lista de op��es pass�veis de
	 * sele��o para aquela �nfase.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Remove a op��o de hor�rio selecionada da lista de op��es selecionadas da
	 * �nfase quatern�ria e coloca novamente na lista de op��es pass�veis de
	 * sele��o para aquela �nfase.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Reordena as op��es de hor�rio da �nfase passada como argumento.
	 * @param i Se 0, �nfase principal. Se 1, �nfase secund�ria.
	 */
	private void reordenarOpcoes(int i) {
		if(!obj.getOpcoesModulo().get(i).getOpcoesHorario().isEmpty()){
			int ordem = 1;
			for(OpcaoHorario op: obj.getOpcoesModulo().get(i).getOpcoesHorario())
				op.setOrdem(ordem++);
		}
	}
	
	/**
	 * Cadastra as informa��es da solicita��o de matr�cula na base de dados.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *	      <li>sigaa.war/ensino/matricula_horario/preferencia_horario.jsp</li>
	 *	</ul>
	 */
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		if(!checkOperacaoAtiva(ArqListaComando.CADASTRAR.getId())){
			return cancelar();
		}
		
		if(opcoes.isEmpty()){
			addMensagemErro("Selecione suas Op��es de �nfase.");
			return null;
		}
		
		boolean erro = false;
		if(!opcoes.get(0).isEmpty()){
			addMensagemErro("1� Op��o de �nfase: ainda h� op��es de hor�rio dispon�veis.");
			erro = true;
		}
		if( isSegundaOpcao() && !opcoes.get(1).isEmpty()){
			addMensagemErro("2� Op��o de �nfase: ainda h� op��es de hor�rio dispon�veis.");
			erro = true;
		}
		if( isTerceiraOpcao() && !opcoes.get(2).isEmpty()){
			addMensagemErro("3� Op��o de �nfase: ainda h� op��es de hor�rio dispon�veis.");
			erro = true;
		}
		if(erro){
			addMensagemErro("Voc� deve adicionar TODAS as op��es de hor�rio para concluir a solicita��o de matr�cula.");
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
	 * Inicia o caso de uso do processamento de matr�cula, populando as informa��es necess�rias.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Invoca o processador para efetuar o processamento das matr�culas.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Confirma o resultado do processamento invocando o processador para persistir as matr�culas nas turmas.
	 *  <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *	      <li>sigaa.war/ensino/matricula_horario/processamento.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public String confirmarProcessamento() throws ArqException {
		if(ValidatorUtil.isEmpty(turmas)){
			addMensagemErro("N�o � poss�vel confirmar o processamento sem haver turmas processadas.");
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
		
		addMensagemInformation("Processamento de matr�cula confirmado com sucesso!");
		
		return cancelar();
	}
	
	/**
	 * Encaminha para a tela do processamento de matr�cula.
	 * @return
	 */
	public String telaProcessamento(){
		return forward("/ensino/matricula_horario/processamento.jsp");
	}
	
	/**
	 * Encaminha para a tela de sele��o de prefer�ncias de �nfase e op��es de hor�rio.
	 * @return
	 */
	public String telaPreferenciaHorario(){
		return forward("/ensino/matricula_horario/preferencia_horario.jsp");
	}
	
	/**
	 * Encaminha para a tela do comprovante de solicita��o de matr�cula.
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
			return emptyCombo("-- SELECIONE A 1� OP��O DE �NFASE --");
		}
		Collection<OpcaoHorario> col = opcoes.get(0);
		if(col.isEmpty()){
			return emptyCombo("-- NENHUMA OP��O DE HOR�RIO DISPON�VEL --");
		}
		return toSelectItems(col, "opcao", "descricao");
	}

	/**
	 * Retorna uma cole��o de selectItems com somente um elemento cujo r�tulo
	 * cont�m o texto passado como argumento.
	 * 
	 * @param label Texto do r�tulo 
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
			return emptyCombo("-- SELECIONE A 2� OP��O DE �NFASE --");
		}
		Collection<OpcaoHorario> col = opcoes.get(1);
		if(col.isEmpty()){
			return emptyCombo("-- NENHUMA OP��O DE HOR�RIO DISPON�VEL --");
		}
		return toSelectItems(col, "opcao", "descricao");
	}
	
	public Collection<SelectItem> getOpcoesTerciariaCombo(){
		if(opcoes.isEmpty() || opcoes.size() < 3){
			return emptyCombo("-- SELECIONE A 2� OP��O DE �NFASE --");
		}
		Collection<OpcaoHorario> col = opcoes.get(2);
		if(col.isEmpty()){
			return emptyCombo("-- NENHUMA OP��O DE HOR�RIO DISPON�VEL --");
		}
		return toSelectItems(col, "opcao", "descricao");
	}
	
	public Collection<SelectItem> getOpcoesQuaternariaCombo(){
		if(opcoes.isEmpty() || opcoes.size() < 4){
			return emptyCombo("-- SELECIONE A 3� OP��O DE �NFASE --");
		}
		Collection<OpcaoHorario> col = opcoes.get(3);
		if(col.isEmpty()){
			return emptyCombo("-- NENHUMA OP��O DE HOR�RIO DISPON�VEL --");
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
			addMensagemErro("N�o foi poss�vel carregar o discente escolhido");
		}
	}

	public DiscenteAdapter getDiscente() {
		return discente;
	}

	/** 
	 * Verifica se o ator da opera��o � um discente.
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
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
