/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 18/12/2009
 *
 */
package br.ufrn.sigaa.ensino.infantil.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.ensino.infantil.DiscenteInfantilDao;
import br.ufrn.sigaa.arq.dao.ensino.infantil.TurmaInfantilDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.infantil.dominio.DiscenteInfantil;
import br.ufrn.sigaa.ensino.medio.dominio.MatriculaComponenteDependencia;
import br.ufrn.sigaa.ensino.negocio.dominio.MatriculaMov;
import br.ufrn.sigaa.mensagens.MensagensInfantil;

/**
 * Controlador para efetuar matrícula em turmas dos alunos do ensino infantil. 
 * 
 * @author Leonardo Campos
 *
 */
@Component("matriculaInfantilMBean") @Scope("session")
public class MatriculaInfantilMBean extends SigaaAbstractController<MatriculaComponente> {
	
	/** Alunos ativos que podem se matricular nas turmas */
	private Collection<DiscenteInfantil> discentes = new ArrayList<DiscenteInfantil>();
	
	/** DataModel usado na exibição da lista de {@link Turma} */
	private DataModel modelTurmas = new ListDataModel();
	
	/** Turma selecionada para matrícula */
	private Turma turma;
	
	public MatriculaInfantilMBean() {
		clear();
	}

	/**
	 * Instancia os atributos necessários para iniciar as operações.
	 */
	private void clear() {
		obj = new MatriculaComponente();
		discentes = new ArrayList<DiscenteInfantil>();
		modelTurmas = new ListDataModel();
	}
	
	/**
	 * Popula as informações necessárias para iniciar o caso de uso
	 * e encaminha para a tela de seleção da turma.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/infantil/menu.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException {
		clear();
		checkRole(SigaaPapeis.GESTOR_INFANTIL);
		modelTurmas = new ListDataModel((ArrayList<Turma>) getDAO(TurmaDao.class).findGeral(getNivelEnsino(), new Unidade(getUnidadeGestora()), null, null, null, null, new Integer[]{SituacaoTurma.ABERTA}, CalendarUtils.getAnoAtual(), null, null, null,  new ModalidadeEducacao(ModalidadeEducacao.PRESENCIAL), null, null, null, null,null,null,null));
		return forward(ConstantesNavegacaoInfantil.MATRICULA_SELECAO_TURMA);
	}
	
	/**
	 * Carrega as informações da turma selecionada e encaminha para
	 * a tela de seleção dos alunos.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/infantil/MatriculaInfantil/selecao_turma.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String selecionarTurma() throws ArqException {
		turma = (Turma) modelTurmas.getRowData();
		discentes = getDAO(DiscenteInfantilDao.class).findNaoMatriculados(turma.getDisciplina().getId(), getNivelInfantil(), CalendarUtils.getAnoAtual());
		prepareMovimento(SigaaListaComando.MATRICULAR_TECNICO);
		return forward(ConstantesNavegacaoInfantil.MATRICULA_SELECAO_DISCENTES);
	}

	/**
	 * Confirma as matrículas dos alunos selecionados na turma selecionada anteriormente.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/infantil/MatriculaInfantil/selecao_discentes.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String confirmar() throws ArqException {
		Collection<DiscenteAdapter> discentes = new ArrayList<DiscenteAdapter>();
		for(DiscenteAdapter d: this.discentes){
			if(d.isSelecionado())
				discentes.add(d);
		}
		
		if(discentes.isEmpty()){
			addMensagem(MensagensInfantil.SELECIONE_ALUNO_MATRICULA);
			return null;
		}
		
		//Válida se algum discente ainda está matriculado em alguma turma
		validaMatriculaComponente();

		//Valida à Capacidade da Turma
		if ( turma.getCapacidadeAluno() < discentes.size() + turma.getQtdMatriculados() ) {
			addMensagem(MensagensInfantil.CAPACIDADE_TURMA_EXCEDIDA);
			return null;
		}
		
		if (hasErrors())
			return null;
		
		turma = getGenericDAO().refresh(turma);
		Collection<Turma> turmas = new ArrayList<Turma>();
		turmas.add(turma);

		MatriculaMov mov = new MatriculaMov();
		mov.setCodMovimento(getUltimoComando());
		mov.setDiscentes(discentes);
		mov.setTurmas(turmas);
		
		try {
			execute(mov);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		return cancelar();
	}
	
	private void validaMatriculaComponente() throws DAOException {
		TurmaInfantilDao dao = getDAO(TurmaInfantilDao.class);
		try {
			List<MatriculaComponente> matriculasAtivas = dao.findMatriculaDiscente(discentes);
			if ( matriculasAtivas != null && !matriculasAtivas.isEmpty() ) {
				for (MatriculaComponente matriculaComponente : matriculasAtivas)
					addMensagemErro("O discente " + matriculaComponente.getDiscente().getNome() + 
						" está se encontra matriculado na turma " + matriculaComponente.getTurma().getDescricaoTurmaInfantilResumida() + "." );
			}
		} finally {
			dao.close();
		}
	}

	/**
	 * Encaminha para a tela de seleção da turma na qual se deseja efetuar a matrícula dos alunos.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/infantil/MatriculaInfantil/selecao_discentes.jsp</li>
	 * </ul>
	 * @return
	 */
	public String telaSelecaoTurma(){
		return forward(ConstantesNavegacaoInfantil.MATRICULA_SELECAO_TURMA);
	}
	
	/**
	 * Método auxiliar para obter o nível de ensino infantil da turma selecionada.
	 * @return
	 */
	private int getNivelInfantil() {
		if(turma != null && turma.getDisciplina() != null && turma.getDisciplina().getCodigo() != null){
			String cod = turma.getDisciplina().getCodigo();
			int tam = cod.length();
			return Integer.parseInt(cod.substring(tam-1));
		}
		return 0;
	}

	public DataModel getModelTurmas() {
		return modelTurmas;
	}

	public void setModelTurmas(DataModel modelTurmas) {
		this.modelTurmas = modelTurmas;
	}

	public Collection<DiscenteInfantil> getDiscentes() {
		return discentes;
	}

	public void setDiscentes(Collection<DiscenteInfantil> discentes) {
		this.discentes = discentes;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}
}
