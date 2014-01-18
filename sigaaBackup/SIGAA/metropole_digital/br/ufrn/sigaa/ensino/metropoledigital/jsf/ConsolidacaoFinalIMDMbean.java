package br.ufrn.sigaa.ensino.metropoledigital.jsf;

import java.util.ArrayList;
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
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.metropoledigital.dao.ConsolidacaoFinalIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.ConsolidacaoParcialIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.ParametrosAcademicosIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.TutoriaIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.MatriculaTurma;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.NotaIMD;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.ParametrosAcademicosIMD;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.SituacaoMatriculaTurma;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.TutoriaIMD;
import br.ufrn.sigaa.ensino.metropoledigital.negocio.MovimentoConsolidacaoNotas;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;

/**
 * Bean responsável por gerenciar a consolidação final das turmas do IMD.
 * 
 * @author Rafael Silva
 *
 */
@Scope("session")
@Component("consolidacaoFinalIMD")
public class ConsolidacaoFinalIMDMbean extends SigaaAbstractController{
	/**Link para a listagem de turmas para a consolidacao final*/
	private static final String JSP_LISTA_TURMA_CONSOLIDACAO = "/metropole_digital/consolidacao/lista_turma_consolidacao_final.jsp";	
	/**Link para a consolidação parcial de notas*/
	public static final String JSP_CONSOLIDACAO_FINAL = "/metropole_digital/consolidacao/consolidacao_final.jsp";
	/**Link para o relatório das notas de consolidacao final*/
	public static final String JSP_RELATORIO_CONSOLIDACAO_FINAL = "/metropole_digital/consolidacao/relatorio_consolidacao_final.jsp";
	/**Link para o detalhamento de notas*/
	public static final String JSP_DETALHAR_NOTAS = "/metropole_digital/consolidacao/detalhar_notas.jsp";	
	/**Link para o portal do tutor*/
	public static final String JSP_PORTAL = "/metropole_digital/principal.jsp";		
	/**Lista dos alunos matriculados na turma*/
	private List<MatriculaTurma> alunosMatriculados = new ArrayList<MatriculaTurma>();			
	/**Lista de turmas de entrada habilitadas para a consolidacao final*/
	private List<TurmaEntradaTecnico> listaTurmasConsolidacao = new ArrayList<TurmaEntradaTecnico>(); 	
	/**Lista de Tutorias ativas vinculadas a turma*/
	private List<TutoriaIMD> tutorias = new ArrayList<TutoriaIMD>();	
	/**id da turma de entrada selecionada*/
	private int idTurmaEntrada;
	
	private List<Turma> listaDisciplinas;
	
	/**
	 * Lista as turmas habilitadas para a consolidação final.
	 * 
	 * @return
	 * @throws NegocioException 
	 * @throws ArqException 
	 * @throws HibernateException 
	 */
	public String listarTurmasConsolidacao() throws HibernateException, ArqException, NegocioException{			
		TutoriaIMDDao tutoriaDao = getDAO(TutoriaIMDDao.class);
		listaTurmasConsolidacao = new ArrayList<TurmaEntradaTecnico>();
		try {
		
			for (TurmaEntradaTecnico t : tutoriaDao.findTurmasByTutor(getUsuarioLogado().getPessoa().getId())) {
				if (t.getDadosTurmaIMD().isConsolidadoParcialmente()) {
					listaTurmasConsolidacao.add(t);
				}						
			}
			
			if (listaTurmasConsolidacao.isEmpty()) {					
				addMessage("Não existe turmas a serem consolidadas", TipoMensagemUFRN.ERROR);
				return null;			
			}
			
			if (listaTurmasConsolidacao.size()==1) {
				idTurmaEntrada = listaTurmasConsolidacao.get(0).getId();
				return cadastroNotasRecuperacao();
			}
		} finally{
			tutoriaDao.close();
		}
		return forward(JSP_LISTA_TURMA_CONSOLIDACAO);				
	}
	
	
	/**
	 * Relatório de Consolidação Final 
	 *  
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/app/
	 * 			metropole_digital/consolidacao/consolidacao_final.jsp</li>
	 * </ul>
	 * @return
	 * @throws HibernateException
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	public String relatorioConsolidacaoFinal() throws HibernateException, ArqException, NegocioException{
		ConsolidacaoFinalIMDDao dao = getDAO(ConsolidacaoFinalIMDDao.class);
		try {
			for (MatriculaTurma mt : alunosMatriculados) {
				for (MatriculaComponente mc : mt.getMatriculasComponentes().values()) {
					if (mc.getRecuperacao()==null) {
						addMessage("Operação não realizada. Existem notas de recuperação ainda não cadastradas!", TipoMensagemUFRN.ERROR);
						return forward(JSP_CONSOLIDACAO_FINAL);
					}
				}
			}
			processadorCadastroNotasRecuperacao();			
			listaDisciplinas = dao.findTurmasComponente(idTurmaEntrada);
			tutorias = dao.findDadosTutoria(idTurmaEntrada);
			alunosMatriculados = dao.findMatriculasTurma(idTurmaEntrada);
			
			ParametrosAcademicosIMDDao parametrosDao = getDAO(ParametrosAcademicosIMDDao.class);
			ParametrosAcademicosIMD parametrosAcademicos = parametrosDao.getParametrosAtivo();
			for (MatriculaTurma mt : alunosMatriculados) {
				List<NotaIMD> notasIMD = dao.findNotasAluno(idTurmaEntrada, mt.getDiscente().getId());
				Double mediaAluno = mt.getMediaCalculada(notasIMD);
				SituacaoMatriculaTurma sit = new SituacaoMatriculaTurma();
				if (mediaAluno >= parametrosAcademicos.getMediaAprovacao()) {
					sit.setId(SituacaoMatriculaTurma.APROVADO);
					mt.setSituacaoMatriculaTurma(sit);
				}else{
					sit.setId(SituacaoMatriculaTurma.REPROVADO_NOTA);
					mt.setSituacaoMatriculaTurma(sit);
				}
			}
			
			
		} finally {
			dao.close();
		}
		return forward(JSP_RELATORIO_CONSOLIDACAO_FINAL);		
	}
	
	/**
	 * Redireciona para a tela de cadastro das notas de recuperação.
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/app/
	 * 			metropole_digital/principal.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws NegocioException 
	 * @throws ArqException 
	 * @throws HibernateException 
	 */
	public String cadastroNotasRecuperacao() throws HibernateException, ArqException, NegocioException{	
		ConsolidacaoFinalIMDDao dao = getDAO(ConsolidacaoFinalIMDDao.class);
		
		idTurmaEntrada = getParameterInt("id");
		List<Turma> listaTurmas = dao.findTurmasComponente(idTurmaEntrada);								
		try {
			if (listaTurmas.isEmpty()) {
				addMessage("Não existe(m) turma(s) com disciplinas à consolidar.", TipoMensagemUFRN.ERROR);
				return forward(JSP_PORTAL);			
			}
			
			ConsolidacaoParcialIMDDao consolidacaoParcial = getDAO(ConsolidacaoParcialIMDDao.class);
			if (!consolidacaoParcial.isConsolidadoParcialmente(idTurmaEntrada)) {
				addMessage("Esta turma ainda não foi consolidada parcialmente. É necessário realizar a consolidação parcial antes da consolidação final.", TipoMensagemUFRN.ERROR);
				return forward(JSP_PORTAL);			
			}
			

			tutorias = dao.findDadosTutoria(idTurmaEntrada);
			listaDisciplinas = dao.findTurmasComponente(idTurmaEntrada);				
			alunosMatriculados = dao.findMatriculasTurmaRecuperacao(idTurmaEntrada);	
			
			if (alunosMatriculados.isEmpty()) {
				return relatorioConsolidacaoFinal();
			}
		} finally {
			dao.close();
		}
		return  forward(JSP_CONSOLIDACAO_FINAL);
	}
	
	
	/**
	 * Realiza o cadastro das notas de recuperação.
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/app/
	 * 			metropole_digital/consolidacao/consolidacao_final.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String cadastrarNotasRecuperacao() throws ArqException, NegocioException{
		processadorCadastroNotasRecuperacao();
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO, "Consolidação");
		return forward(JSP_CONSOLIDACAO_FINAL);
	}
	
	/**
	 * Chama o processador responsável pelo cadastro das notas de recuperação.
	 * 
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public void processadorCadastroNotasRecuperacao() throws ArqException, NegocioException{
		MovimentoConsolidacaoNotas mov = new MovimentoConsolidacaoNotas();
		mov.setMatriculas(alunosMatriculados);
		mov.setTurmaEntrada(getTurmaEntrada());
		mov.setModulo(getTurmaEntrada().getDadosTurmaIMD().getCronograma().getModulo());
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_NOTAS_RECUPERACAO);	
		prepareMovimento(SigaaListaComando.CADASTRAR_NOTAS_RECUPERACAO);
		execute(mov);
		removeOperacaoAtiva();		
	}
	
	/**
	 * Realiza a consolidação final das notas.
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/app/
	 * 			metropole_digital/consolidacao/relatorio_consolidacao_final.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public String consolidarNotas() throws NegocioException, ArqException{
		MovimentoConsolidacaoNotas mov = new MovimentoConsolidacaoNotas();
		mov.setMatriculas(alunosMatriculados);
		mov.setTurmaEntrada(getTurmaEntrada());
		mov.setModulo(getTurmaEntrada().getDadosTurmaIMD().getCronograma().getModulo());
		mov.setCodMovimento(SigaaListaComando.CONSOLIDACAO_FINAL_IMD);	
		prepareMovimento(SigaaListaComando.CONSOLIDACAO_FINAL_IMD);
		execute(mov);
		removeOperacaoAtiva();
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO, "Consolidação");
		return forward(JSP_RELATORIO_CONSOLIDACAO_FINAL);
	}
	
	/**
	 * Redireciona para a página de cadastro das notas de recuperação.
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/app/
	 * 			metropole_digital/consolidacao/relatorio_consolidacao_final.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String voltarFormConsolidacao() throws HibernateException, DAOException{
		ConsolidacaoFinalIMDDao dao = new ConsolidacaoFinalIMDDao();
		try {
			alunosMatriculados = dao.findMatriculasTurmaRecuperacao(idTurmaEntrada);
			if (alunosMatriculados.isEmpty()) {
				voltarPortal();
				
			}
		} finally {
			dao.close();
		}		
		return forward(JSP_CONSOLIDACAO_FINAL);
	}
	
	/**
	 * Redireciona para a o Portal do Tutor.
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/app/
	 * 			metropole_digital/consolidacao/consolidacao_final.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String voltarPortal(){
		return forward(JSP_PORTAL);
	}
	
	
		
	/**
	 * Retorna a turma de Entrada
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


	//GETTERS AND SETTERS
	public List<TurmaEntradaTecnico> getListaTurmasConsolidacao() {
		return listaTurmasConsolidacao;
	}

	public void setListaTurmasConsolidacao(
			List<TurmaEntradaTecnico> listaTurmasConsolidacao) {
		this.listaTurmasConsolidacao = listaTurmasConsolidacao;
	}

	public int getIdTurmaEntrada() {
		return idTurmaEntrada;
	}

	public void setIdTurmaEntrada(int idTurmaEntrada) {
		this.idTurmaEntrada = idTurmaEntrada;
	}

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
	public List<Turma> getListaDisciplinas() {
		return listaDisciplinas;
	}
	public void setListaDisciplinas(List<Turma> listaDisciplinas) {
		this.listaDisciplinas = listaDisciplinas;
	}
}
