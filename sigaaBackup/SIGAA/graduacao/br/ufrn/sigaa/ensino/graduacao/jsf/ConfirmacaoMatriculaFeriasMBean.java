/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 20/11/2008
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.graduacao.ConfirmacaoMatriculaFeriasDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoEnsinoIndividualDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.util.VerificaSituacaoUsuarioBibliotecaUtil;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.ensino.dao.TurmaEADDao;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.ConfirmacaoMatriculaFerias;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.negocio.MatriculaGraduacaoValidator;
import br.ufrn.sigaa.mensagens.MensagensGraduacao;

/**
 * MBeam respons�vel por realizar a l�gica da confirma��o da 
 * matr�cula em turma de f�rias pelos alunos.
 * Esta confirma��o da matr�cula � realizada ap�s o chefe de departamento criar a turma solicitada pelo discente.
 * O discente s� pode ser matriculado em turma de f�rias caso ele tenha solicitado anteriormente a turma.
 * @author Victor Hugo
 */
@Component("confirmacaoMatriculaFeriasBean") @Scope("session")
public class ConfirmacaoMatriculaFeriasMBean extends SigaaAbstractController<ConfirmacaoMatriculaFerias> {

	/** Lista de turmas de f�rias*/
	private List<Turma> turmasFerias;
	/** Calend�rio acad�mico associado*/
	private CalendarioAcademico cal;
	/** Caso o aluno j� esteja inserido em uma solicita��o de turma de f�rias este atributo referencia esta turma, para destacar na view a turma que ele encontra-se matriculado */
	private ConfirmacaoMatriculaFerias confirmacaoAnterior; 
	
	public ConfirmacaoMatriculaFeriasMBean() {
		clear();
	}

	/** Limpa os dados do MBean para sua utiliza��o em uma nova opera��o de confirma��o Matricula Ferias */
	private void clear() {
		initObj();
		turmasFerias = new ArrayList<Turma>();
		cal = getCalendarioVigente();
		confirmacaoAnterior = null;
	}
	
	/** Inicializa os atributos do controller. */
	private void initObj() {
		obj = new ConfirmacaoMatriculaFerias();
	}

	/**
	 * O m�todo inicial serve para realizar algumas valida��es tais como valida se est� no per�odo de f�rias, verificar se o 
	 * discente j� tem matr�cula para o per�odo vigente.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/matricula/ferias/turmas_ferias.jsp</li>
	 *    <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/solicitacao_turma/termo_turma_ferias.jsp</li>
	 *    <li>/SIGAA/app/sigaa.ear/sigaa.war/portais/discente/menu_discente.jsp</li>
	 *   </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException{
		
		clear();
		
		TurmaDao dao = getDAO(TurmaDao.class);
		DiscenteAdapter discente = getDiscenteUsuario();
		
		CalendarioAcademico cal = getCalendarioVigente();
		if( true ){
			addMensagemErro( CalendarioAcademico.getDescricaoPeriodo("Matr�cula em turma de f�rias", cal.getInicioMatriculaTurmaFerias(), cal.getFimMatriculaTurmaFerias()) );
			return null;
		}
		
		if( cal.getAnoFeriasVigente() == null || cal.getPeriodoFeriasVigente() == null ){
			addMensagem(MensagensGraduacao.ANO_PERIODO_FERIAS_NAO_DEFINIDO_CALENDARIO_ACADEMICO);
		}
		
		/* Regras de obrigatoriedade: <br/>
		 * RESOLU��O No 028/2010-CONSAD, de 16 de setembro de 2010
		 * Art. 3o
		 * I - tratando-se de discente
		 * a) imposibilidade de se efetuar matricula em disciplina.
		 */
		try {
			VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioPossuiInrregularidadeAdministrativa(discente.getPessoa().getId());
		} catch (NegocioException ne) {
			addMensagemErro(ne.getMessage());
			return null;
		}
		
		if (hasErrors()) return null;
		
		MatriculaComponenteDao mtDao = getDAO(MatriculaComponenteDao.class);
		Collection<MatriculaComponente> matriculasFeriasDiscente = mtDao.findByDiscente(discente, cal.getAnoFeriasVigente(), cal.getPeriodoFeriasVigente(), 
				SituacaoMatricula.APROVADO, SituacaoMatricula.REPROVADO, SituacaoMatricula.REPROVADO_FALTA, SituacaoMatricula.REPROVADO_MEDIA_FALTA, 
				SituacaoMatricula.APROVEITADO_CUMPRIU, SituacaoMatricula.APROVEITADO_DISPENSADO, SituacaoMatricula.APROVEITADO_TRANSFERIDO, 
				  SituacaoMatricula.MATRICULADO, SituacaoMatricula.TRANCADO);
		
		
		//verifica se ele tem alguma matr�cula no per�odo de f�rias vigente
		if( !isEmpty(matriculasFeriasDiscente) ){
			addMensagemErro("Voc� j� cursou uma turma de f�rias no per�odo " + cal.getAnoFeriasVigente() + "." + cal.getPeriodoFeriasVigente() );
			return null;
		}
		
		//carregando turmas de f�rias abertas
		if(discente.isDiscenteEad()) {
			TurmaEADDao teaddao = getDAO(TurmaEADDao.class);
			turmasFerias = teaddao.findGeral(
					NivelEnsino.GRADUACAO,
					null, null,	null, null,	null,
					new Integer[] { SituacaoTurma.ABERTA, SituacaoTurma.A_DEFINIR_DOCENTE },
					cal.getAnoFeriasVigente(),
					cal.getPeriodoFeriasVigente(),
					((DiscenteGraduacao) discente).getPolo(),
					null, 
					new ModalidadeEducacao(ModalidadeEducacao.A_DISTANCIA), 
					discente.getCurriculo(), 
					null, null, null, null, null);
		} else {
			turmasFerias = (List<Turma>) dao.findGeral(
					NivelEnsino.GRADUACAO,
					null, null,	null, null,	null,
					new Integer[] { SituacaoTurma.ABERTA, SituacaoTurma.A_DEFINIR_DOCENTE },
					cal.getAnoFeriasVigente(),
					cal.getPeriodoFeriasVigente(),
					null,
					null, 
					new ModalidadeEducacao(ModalidadeEducacao.PRESENCIAL), 
					null, null, null, null, null, null,null);
		}
		
		// remove da lista as n�o matricul�veis
		Iterator<Turma> iterator = turmasFerias.iterator();
		while (iterator.hasNext())
			if (!iterator.next().getDisciplina().isMatriculavel())
				iterator.remove();
		
		Collections.sort(turmasFerias, new Comparator<Turma>() {
			public int compare(Turma t1, Turma t2) {
				int result;
				result = t1.getDisciplina().getUnidade().getNome().compareTo( t2.getDisciplina().getUnidade().getNome() );
				if( result == 0 )
					result = t1.getDisciplina().getNome().compareTo( t2.getDisciplina().getNome() );
				if( result == 0 )
					result = t1.getCodigo().compareTo( t2.getCodigo() );
				return result;
			}
		});
		
		initObj();
		obj.setDiscente((DiscenteGraduacao) discente);
		
		ConfirmacaoMatriculaFeriasDao confDao = getDAO( ConfirmacaoMatriculaFeriasDao.class );
		confirmacaoAnterior = confDao.findByDiscenteTurmaConfirmado(discente, null, cal.getAnoFeriasVigente(), cal.getPeriodoFeriasVigente());
		if(confirmacaoAnterior != null)
			confirmacaoAnterior.getTurma().getDescricaoSemDocente();
		
		prepareMovimento(SigaaListaComando.CONFIRMACA_MATRICULA_FERIAS);
		setOperacaoAtiva(SigaaListaComando.CONFIRMACA_MATRICULA_FERIAS.getId());
		
		return forward("/graduacao/matricula/ferias/turmas_ferias.jsf");
	}
	
	/**
	 * Este m�todo inicia a confirma��o da matricula na turma de f�rias, realizado pelo discente solicitante da turma de f�rias.
	 * Verifica se o discente solicitou turma de ferias no semestre atual, se a turma foi criada e redireciona para o termo de confirma��o.<br>
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/graduacao/matricula/ferias/turmas_ferias.jsp 
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarConfirmacao() throws ArqException{
		
		if (obj.getId() > 0 || isEmpty(obj.getDiscente())) {
			addMensagemErro("Caro discente, foi detectado que o bot�o Voltar do navegador foi utilizado. Por favor use a navega��o oferecida pelo sistema.");
			return cancelar();
		}
		
		SolicitacaoEnsinoIndividualDao seiDao = getDAO( SolicitacaoEnsinoIndividualDao.class );
		
		int id = getParameterInt("id");
		Turma turma = getGenericDAO().findByPrimaryKey(id, Turma.class);
		obj.setTurma(turma);
		
		MatriculaGraduacaoValidator.validarAlunoOutroCampus(turma, getGenericDAO().refresh(obj.getDiscente()), erros);
		if(hasErrors())
			return forward("/graduacao/matricula/ferias/turmas_ferias.jsf");
		
		if( confirmacaoAnterior != null ){
			
			if( confirmacaoAnterior.getTurma().getId() == turma.getId() ){
				addMensagemErro("Voc� j� solicitou matr�cula nesta turma de f�rias." );
				return null;
			}else 
				addMensagemWarning("Voc� havia solicitado matr�cula na turma de f�rias " + confirmacaoAnterior.getTurma().getDescricaoSemDocente() + ", caso confirme a matr�cula nessa turma a outra solicita��o inicial ser� cancelada. ");
			
		}
		obj.setConfirmou(false);
		return telaConfirmacao();
	}
	
	/**
	 * Este m�todo verifica as credenciais do discente e gera a solicita��o de matricula na turma de f�rias solicitada.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/solicitacao_turma/termo_turma_ferias.jsp</li>
	 *   </ul>
	 *  
	 * @return
	 * @throws ArqException 
	 */
	public String confirmarMatricula() throws ArqException{
		
		if (obj != null && (isEmpty(obj.getTurma()) || obj.getTurma().getId() == 0)) {
			addMensagemErro("Caro discente, foi detectado que o bot�o Voltar do navegador foi utilizado. Por favor use a navega��o oferecida pelo sistema.");
			return cancelar();
		}
		
		if( !obj.isConfirmou() ) {
			addMensagemErro("Para efetuar a matr�cula, � necess�rio aceitar os Termos de Matr�cula em Turmas de F�rias.");
			return null;
		}
		
		if( !checkOperacaoAtiva(SigaaListaComando.CONFIRMACA_MATRICULA_FERIAS.getId()) )
			return cancelar();
			
		if( !confirmaSenha() )
			return null;
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.CONFIRMACA_MATRICULA_FERIAS);
		mov.setObjMovimentado(obj);
		
		try {
			execute(mov);
			removeOperacaoAtiva();
		} catch (NegocioException e) {
			removeOperacaoAtiva();
			addMensagens(e.getListaMensagens());
			return null;
		} 
		addMensagem(OPERACAO_SUCESSO);
		return cancelar();
	}
	
	/**
	 * Exibe comprovante ao aluno.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>N�o invocado por JSP.</li>
	 *   </ul>
	 *   
	 * @return
	 */
	public String verComprovante(){
		//TODO verificar se o aluno possui um comprovante a ser exibido e, caso afirmativo, carrega-lo
		return telaComprovante();
	}
	/**
	 * Exibe a tela de comprova��o para o aluno.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>N�o invocado por JSP.</li>
	 *   </ul>
	 * 
	 * @return
	 */
	public String telaConfirmacao(){
		return forward( "/graduacao/solicitacao_turma/termo_turma_ferias.jsf" );
	}

	/**
 	 * Exibe o aluno a tela do comprovante.
 	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>N�o invocado por JSP.</li>
	 *   </ul>
	 *   
	 * @return
	 */
	public String telaComprovante(){
		return forward(  "/graduacao/solicitacao_turma/comprovante_turma_ferias.jsf" );
	}

	public List<Turma> getTurmasFerias() {
		return turmasFerias;
	}

	public void setTurmasFerias(List<Turma> turmasFerias) {
		this.turmasFerias = turmasFerias;
	}

	public CalendarioAcademico getCal() {
		return cal;
	}

	public void setCal(CalendarioAcademico cal) {
		this.cal = cal;
	}

	public ConfirmacaoMatriculaFerias getConfirmacaoAnterior() {
		return confirmacaoAnterior;
	}

	public void setConfirmacaoAnterior(ConfirmacaoMatriculaFerias confirmacaoAnterior) {
		this.confirmacaoAnterior = confirmacaoAnterior;
	}

}
