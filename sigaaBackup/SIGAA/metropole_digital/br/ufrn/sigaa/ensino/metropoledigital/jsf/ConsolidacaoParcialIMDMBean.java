package br.ufrn.sigaa.ensino.metropoledigital.jsf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.metropoledigital.dao.ConsolidacaoParcialIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.MatriculaTurma;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.NotaIMD;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.TutorIMD;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.TutoriaIMD;
import br.ufrn.sigaa.ensino.metropoledigital.negocio.MovimentoConsolidacaoNotas;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;

/**
 * Bean responsável por realizar consolidação parcial das notas do IMD. 
 * 
 * @author Rafael Silva
 *
 */
@Scope("session")
@Component("consolidacaoParcialIMD")
public class ConsolidacaoParcialIMDMBean extends SigaaAbstractController<MatriculaTurma>{
	/**Link para a consolidação parcial de notas*/
	public static final String JSP_CONSOLIDACAO_PARCIAL = "/metropole_digital/consolidacao/consolidacao_parcial.jsp";
	/**Link para o detalhamento de notas*/
	public static final String JSP_DETALHAR_NOTAS = "/metropole_digital/consolidacao/detalhar_notas.jsp";	
	/**Link para o portal do tutor*/
	public static final String JSP_PORTAL = "/metropole_digital/principal.jsp";	
	/**Link para a listagem de turmas*/
	public static final String JSP_LISTA_TURMA_CONSOLIDACAO = "/metropole_digital/consolidacao/lista_turmas_consolidacao_parcial.jsp";
	
	/**Lista de alunos que estão matriculados na turma*/
	private List<MatriculaTurma> alunosMatriculados =  new ArrayList<MatriculaTurma>();
	/**Lista de Tutorias ativas vinculadas a turma*/
	private List<TutoriaIMD> tutorias = new ArrayList<TutoriaIMD>();
	/**Lista de notas dos alunos por turma*/
	private List<NotaIMD> listaNotas = new ArrayList<NotaIMD>();	
	/**Detalhamento de notas do aluno*/
	private List<NotaIMD> notasAluno = new ArrayList<NotaIMD>(); 		
	/**Turma de entrada selecionada*/
	private int idTurmaEntrada;
	

	
	/**
	 * Método chamando para exibir o relatório de consolidação parcial da turma. 
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/app/
	 * 			metropole_digital/consolidacao/consolidacao_parcial.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws HibernateException 
	 * @throws DAOException
	 */
	public  String relatorioConsolidacaoParcial() throws HibernateException, DAOException{
		ConsolidacaoParcialIMDDao dao = getDAO(ConsolidacaoParcialIMDDao.class);
		if (getParameterInt("id")!=null) {
			idTurmaEntrada = getParameterInt("id");
		}
							
		try {			
			tutorias = dao.findDadosTutoria(idTurmaEntrada);
			alunosMatriculados = dao.findAlunosTurma(idTurmaEntrada);
			
			if (dao.isConsolidadoParcialmente(idTurmaEntrada)) {
				addMessage("Esta operação já foi realizada!", TipoMensagemUFRN.ERROR);
				return forward(JSP_PORTAL);
			}			
			
		} finally{
			dao.close();
		}		
		return forward(JSP_CONSOLIDACAO_PARCIAL);
	}
	
	/**
	 * Realiza o detalhamento das notas do aluno selecionado na página de consolidação parcial.
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/app/
	 * 			metropole_digital/consolidacao/detalhar_notas.jsp</li>
	 * </ul> 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String detalharNotas() throws HibernateException, DAOException{
		ConsolidacaoParcialIMDDao dao = getDAO(ConsolidacaoParcialIMDDao.class);
		try {
			notasAluno = dao.findNotasAluno(getTurmaEntrada().getId(),getParameterInt("idDiscente"));			
		} finally {
			dao.close();
		}		
		return forward(JSP_DETALHAR_NOTAS);
	}
	
	/**
	 * Realiza a consolidação de notas.
	 *  
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/app/
	 * 			metropole_digital/consolidacao/consolidacao_parcial.jsp</li>
	 * </ul> 
	 * 
	 * @return
	 * @throws HibernateException 
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String consolidarNotas() throws NegocioException, ArqException {
		ConsolidacaoParcialIMDDao dao = getDAO(ConsolidacaoParcialIMDDao.class);
		MovimentoConsolidacaoNotas mov = new MovimentoConsolidacaoNotas();
		try {			
			validaDadosConsolidacao();			
			if (!hasErrors()) {
				mov.setMatriculas(alunosMatriculados);
				mov.setTurmaEntrada(getTurmaEntrada());
				mov.setModulo(getTurmaEntrada().getDadosTurmaIMD().getCronograma().getModulo());
				mov.setCodMovimento(SigaaListaComando.CONSOLIDACAO_PARCIAL_IMD);			
				execute(mov);
				addMensagem(MensagensArquitetura.OPERACAO_SUCESSO, "Consolidação");
			}								
		} finally {
			dao.close();
		}
		
		//caso não exista erro executar o processador cadastro.
		return forward(JSP_CONSOLIDACAO_PARCIAL);
	}
	
	/**
	 * Redireciona o sistema para a página de consolidação parcial.
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * 
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/WEB-INF/app/
	 * 			metropole_digital/consolidacao/detalhar_notas.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String voltarFormConsolidacao(){
		return forward(JSP_CONSOLIDACAO_PARCIAL);
	}
	
	/**
	 * Redireciona a página para o portal do tutor. 
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * 
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/WEB-INF/app/
	 * 			metropole_digital/consolidacao/consolidacao_parcial.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String voltarPortal(){
		return forward(JSP_PORTAL);
	}
	
	/**
	 * Retorna os dados da turma selecionada.
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * 
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/WEB-INF/app/
	 * 			metropole_digital/consolidacao/consolidacao_parcial.jsp</li>
	 * 
	 * 		<li>/SIGAA/app/sigaa.ear/WEB-INF/app/
	 * 			metropole_digital/consolidacao/detalhar_notas.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public TurmaEntradaTecnico getTurmaEntrada(){		
		if (tutorias.isEmpty()) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS, TipoMensagemUFRN.ERROR);			
		}else{
			return tutorias.get(0).getTurmaEntrada();
		}
		return new TurmaEntradaTecnico();
	}
	
	/**
	 * Retorna o(s) tutor(es) vinculado(s) a turma. 
	 * 
	 * @return
	 */
	public List<TutorIMD> getTutores(){
		if (tutorias.isEmpty()) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS, TipoMensagemUFRN.ERROR);			
		}else{
			List<TutorIMD> tutores = new ArrayList<TutorIMD>();
			for (TutoriaIMD t : tutorias) {
				tutores.add(t.getTutor());
			}
			return tutores;
		}
		return Collections.emptyList();		
	}
	
	/**
	 * Verifica se os acompanhamentos semanais da turma foram cadastrados. 
	 * 
	 * @throws HibernateException
	 * @throws DAOException
	 */
	private void validaDadosConsolidacao() throws HibernateException, DAOException{
		ConsolidacaoParcialIMDDao dao = getDAO(ConsolidacaoParcialIMDDao.class);
		dao.isConsolidadoParcialmente(getTurmaEntrada().getId());
		try {
			boolean possuiNotasPendentes = false;
			for (MatriculaTurma matricula : alunosMatriculados) {
				if (matricula.getNotaParcial()==null) {
					possuiNotasPendentes=true;
					break;
				}
			}
			
			if (dao.possuiAcompanhamentosPendentes(getTurmaEntrada().getId()) || possuiNotasPendentes) {
				erros.addErro("Operação não realizada. Existem acompanhamentos semanais e/ou notas das disciplinas pendente(s) de cadastro.");
			}
			
			
		} finally  {
			dao.close();
		}		
	}
	
	
	//GETTERS AND SETTERS
	public List<MatriculaTurma> getAlunosMatriculados() {
		return alunosMatriculados;
	}

	public void setAlunosMatriculados(List<MatriculaTurma> alunosMatriculados) {
		this.alunosMatriculados = alunosMatriculados;
	}

	public List<TutoriaIMD> getTutorias() {
		return tutorias;
	}

	public void setTutorias(List<TutoriaIMD> tutorias) {
		this.tutorias = tutorias;
	}

	public List<NotaIMD> getListaNotas() {
		return listaNotas;
	}

	public void setListaNotas(List<NotaIMD> listaNotas) {
		this.listaNotas = listaNotas;
	}

	public List<NotaIMD> getNotasAluno() {
		return notasAluno;
	}

	public void setNotasAluno(List<NotaIMD> notasAluno) {
		this.notasAluno = notasAluno;
	}

}
