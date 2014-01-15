/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * MBeam responsável por realizar a lógica da confirmação da 
 * matrícula em turma de férias pelos alunos.
 * Esta confirmação da matrícula é realizada após o chefe de departamento criar a turma solicitada pelo discente.
 * O discente só pode ser matriculado em turma de férias caso ele tenha solicitado anteriormente a turma.
 * @author Victor Hugo
 */
@Component("confirmacaoMatriculaFeriasBean") @Scope("session")
public class ConfirmacaoMatriculaFeriasMBean extends SigaaAbstractController<ConfirmacaoMatriculaFerias> {

	/** Lista de turmas de férias*/
	private List<Turma> turmasFerias;
	/** Calendário acadêmico associado*/
	private CalendarioAcademico cal;
	/** Caso o aluno já esteja inserido em uma solicitação de turma de férias este atributo referencia esta turma, para destacar na view a turma que ele encontra-se matriculado */
	private ConfirmacaoMatriculaFerias confirmacaoAnterior; 
	
	public ConfirmacaoMatriculaFeriasMBean() {
		clear();
	}

	/** Limpa os dados do MBean para sua utilização em uma nova operação de confirmação Matricula Ferias */
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
	 * O método inicial serve para realizar algumas validações tais como valida se está no período de férias, verificar se o 
	 * discente já tem matrícula para o período vigente.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
			addMensagemErro( CalendarioAcademico.getDescricaoPeriodo("Matrícula em turma de férias", cal.getInicioMatriculaTurmaFerias(), cal.getFimMatriculaTurmaFerias()) );
			return null;
		}
		
		if( cal.getAnoFeriasVigente() == null || cal.getPeriodoFeriasVigente() == null ){
			addMensagem(MensagensGraduacao.ANO_PERIODO_FERIAS_NAO_DEFINIDO_CALENDARIO_ACADEMICO);
		}
		
		/* Regras de obrigatoriedade: <br/>
		 * RESOLUÇÃO No 028/2010-CONSAD, de 16 de setembro de 2010
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
		
		
		//verifica se ele tem alguma matrícula no período de férias vigente
		if( !isEmpty(matriculasFeriasDiscente) ){
			addMensagemErro("Você já cursou uma turma de férias no período " + cal.getAnoFeriasVigente() + "." + cal.getPeriodoFeriasVigente() );
			return null;
		}
		
		//carregando turmas de férias abertas
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
		
		// remove da lista as não matriculáveis
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
	 * Este método inicia a confirmação da matricula na turma de férias, realizado pelo discente solicitante da turma de férias.
	 * Verifica se o discente solicitou turma de ferias no semestre atual, se a turma foi criada e redireciona para o termo de confirmação.<br>
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/graduacao/matricula/ferias/turmas_ferias.jsp 
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarConfirmacao() throws ArqException{
		
		if (obj.getId() > 0 || isEmpty(obj.getDiscente())) {
			addMensagemErro("Caro discente, foi detectado que o botão Voltar do navegador foi utilizado. Por favor use a navegação oferecida pelo sistema.");
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
				addMensagemErro("Você já solicitou matrícula nesta turma de férias." );
				return null;
			}else 
				addMensagemWarning("Você havia solicitado matrícula na turma de férias " + confirmacaoAnterior.getTurma().getDescricaoSemDocente() + ", caso confirme a matrícula nessa turma a outra solicitação inicial será cancelada. ");
			
		}
		obj.setConfirmou(false);
		return telaConfirmacao();
	}
	
	/**
	 * Este método verifica as credenciais do discente e gera a solicitação de matricula na turma de férias solicitada.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/solicitacao_turma/termo_turma_ferias.jsp</li>
	 *   </ul>
	 *  
	 * @return
	 * @throws ArqException 
	 */
	public String confirmarMatricula() throws ArqException{
		
		if (obj != null && (isEmpty(obj.getTurma()) || obj.getTurma().getId() == 0)) {
			addMensagemErro("Caro discente, foi detectado que o botão Voltar do navegador foi utilizado. Por favor use a navegação oferecida pelo sistema.");
			return cancelar();
		}
		
		if( !obj.isConfirmou() ) {
			addMensagemErro("Para efetuar a matrícula, é necessário aceitar os Termos de Matrícula em Turmas de Férias.");
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
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>Não invocado por JSP.</li>
	 *   </ul>
	 *   
	 * @return
	 */
	public String verComprovante(){
		//TODO verificar se o aluno possui um comprovante a ser exibido e, caso afirmativo, carrega-lo
		return telaComprovante();
	}
	/**
	 * Exibe a tela de comprovação para o aluno.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>Não invocado por JSP.</li>
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
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>Não invocado por JSP.</li>
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
