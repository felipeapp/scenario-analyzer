/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on '01/08/2012'
 *
 */

package br.ufrn.sigaa.ensino.stricto.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.MatriculaStrictoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;

/**
 *	MBean respons�vel por gerenciar a matr�cula de um aluno em stricto em outros programas.
 * 
 * @author Henrique Andr�
 */
@Component("matriculaOutroProgramaStrictoBean") @Scope("session")
public class MatriculaOutroProgramaStrictoMBean extends SigaaAbstractController<MatriculaComponente> {

	/**
	 * Lista das unidades que est�o em per�odo de matr�cula
	 */
	private List<Unidade> programasMatriculasAberta;
	
	/**
	 * Referencia para {@link MatriculaStrictoMBean}
	 */
	private MatriculaStrictoMBean mBean;
	
	/**
	 * Cojunto de turmas abertas do programa selecionado
	 */
	private Collection<Turma> resultadoTurmasBuscadas;
	
	/**
	 * Contrutor padr�o
	 * @throws DAOException
	 */
	public MatriculaOutroProgramaStrictoMBean() throws DAOException {
		mBean = getMBean("matriculaStrictoBean");
		carregarProgramasMatriculasAberta();
	}

	/**
	 * Carrega os programas que est�o em per�odo de matr�cula
	 * 
	 * @throws DAOException
	 */
	private void carregarProgramasMatriculasAberta() throws DAOException {
		programasMatriculasAberta = getDAO(MatriculaStrictoDao.class).findOutrosProgramasEmPeriodoDeMatricula(mBean.getDiscente().getUnidade());
	}
	
	/**
	 * Seleciona o programa que o discente selecionou
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/stricto/matricula/opcoes.jsp</li>
	 * </ul>
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public String selecionarPrograma() throws NegocioException, ArqException {
		
		TurmaDao dao = getDAO(TurmaDao.class);
		
		Integer unidadeId = getParameterInt("idUnidade");
		Unidade programa = dao.findByPrimaryKey(unidadeId, Unidade.class);
		
		CalendarioAcademico calendario = CalendarioAcademicoHelper.getCalendario(programa);
		
		if (calendario == null)
			throw new NegocioException("N�o foi poss�vel carregar o calend�rio acad�mico atual do programa:" + programa.getNome());
		
		
		Collection<Integer> tiposTurma = new ArrayList<Integer>(3);
		tiposTurma.add(Turma.REGULAR);
		
		resultadoTurmasBuscadas = new ArrayList<Turma>();
			resultadoTurmasBuscadas = dao.findAbertasByComponenteCurricular(null, null,
					null, programa.getId(), null, null,  null, null, null,
					calendario.getAno() , calendario.getPeriodo(), true, getNivelEnsino(), false, tiposTurma.toArray(new Integer[]{}) );
		
		
		
			
		
		mBean.setCalendarioMatricula(calendario);
		mBean.inicializarBeanMatriculaGraduacao();
		
		return forward("/stricto/matricula/outros_programas/turmas_abertas.jsp");
	}
	
	/**
	 * Seleciona turma que o discente escolheu
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/stricto/matricula/outros_programas/turmas_abertas.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String selecionarTurmas() throws ArqException{
		mBean.getMatriculaGraduacaoBean().adicionarTurma();
		
		if (hasErrors())
			return null;
		
		return telaSelecaoTurmas();
	}
	
	/**
	 * Tela que mostra as turmas escolhidas pelo discente
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/stricto/matricula/outros_programas/botoes_operacao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String telaSelecaoTurmas() {
		
		return forward("/stricto/matricula/outros_programas/turmas_selecionadas.jsp");
	}

	public List<Unidade> getProgramasMatriculasAberta() {
		return programasMatriculasAberta;
	}
	
	public void setProgramasMatriculasAberta(List<Unidade> programasMatriculasAberta) {
		this.programasMatriculasAberta = programasMatriculasAberta;
	}

	public Collection<Turma> getResultadoTurmasBuscadas() {
		return resultadoTurmasBuscadas;
	}

	public void setResultadoTurmasBuscadas(Collection<Turma> resultadoTurmasBuscadas) {
		this.resultadoTurmasBuscadas = resultadoTurmasBuscadas;
	}
	
}
