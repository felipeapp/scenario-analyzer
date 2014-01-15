/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dao.TransferenciaTurmaDao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.RegistroTransferencia;
import br.ufrn.sigaa.ensino.graduacao.dominio.ReservaCurso;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.TurmaOrigemTurmaDestinos;
import br.ufrn.sigaa.ensino.graduacao.jsf.TransferenciaTurmaMBean;
import br.ufrn.sigaa.ensino.util.HorarioTurmaUtil;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.parametros.dominio.ParametrosTecnico;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.processamento.dao.ProcessamentoMatriculaDao;
import br.ufrn.sigaa.processamento.dao.ProcessamentoMatriculaGraduacaoDao;

/**
 * Processador para efetuar transferências de alunos entre turmas
 *
 * @author Leonardo
 * @author Ricardo Wendell
 *
 */
public class ProcessadorTransferenciaTurmas extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		validate(mov);

		MovimentoTransferenciaTurmas movTransferencia = (MovimentoTransferenciaTurmas) mov;
		Turma turmaOrigem = movTransferencia.getTurmaOrigem();
		Turma turmaDestino = movTransferencia.getTurmaDestino();
		
		// DAOs
		TurmaDao turmaDao = getDAO(TurmaDao.class, mov);
		try {
			if(!movTransferencia.isTransfTurmasByDiscente()){
				
				// Definir alunos a serem transferidos, dependendo do tipo de transferência
				Collection<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>();
				Collection<SolicitacaoMatricula> solicitacoes = new ArrayList<SolicitacaoMatricula>();
				
				int totalMatriculas = 0;
				int totalSolicitacoes = 0;
				if ( isAutomatica(mov) ) {
					if ( movTransferencia.getQtdMatriculas() > 0) {
						matriculas = discenteAptosTransferencia(MatriculaComponente.class, turmaOrigem, mov);
						totalMatriculas = movTransferencia.getQtdMatriculas();
					}
					if ( movTransferencia.getQtdSolicitacoes() > 0) {
						solicitacoes = discenteAptosTransferencia(SolicitacaoMatricula.class, turmaOrigem, mov);
						totalSolicitacoes = movTransferencia.getQtdSolicitacoes() ;
					}
				} else {
					matriculas = movTransferencia.getMatriculas();
					solicitacoes = movTransferencia.getSolicitacoes();
					
					totalMatriculas = matriculas.size();
					totalSolicitacoes = solicitacoes.size() ;
				}
				
		
				// Coleção que irá armazenar os discentes efetivamente transferidos
				List<Discente> discentes = new ArrayList<Discente>();
				
				turmaOrigem = turmaDao.findByPrimaryKey( turmaOrigem.getId(), Turma.class);
				turmaDestino = turmaDao.findByPrimaryKey( turmaDestino.getId(), Turma.class);
				
				// Efetuar e persistir transferências de matrículas
				for( MatriculaComponente m : matriculas ){ 
					
					// Validar choque de horários
					if ( !HorarioTurmaUtil.hasChoqueHorarios(turmaDestino, m.getDiscente(), turmaOrigem) ) {
						turmaDao.updateField(MatriculaComponente.class, m.getId(), "turma", turmaDestino );
						// Update do atributo componenteCurricular em matriculaComponente, quando a a transferência for entre turmas de componentes curriculares diferentes.
						if (m.getComponente().getId() != turmaDestino.getDisciplina().getId()){
							turmaDao.updateField(MatriculaComponente.class, m.getId(), "componente", turmaDestino.getDisciplina() );
						}
						
						RegistroTransferencia registro = gerarRegistroTransferencia(m.getDiscente(), turmaOrigem, turmaDestino,isAutomatica(mov), getDAO(DiscenteDao.class, mov));
						registro.setMatricula(m);
						
						turmaDao.create( registro );
						
						discentes.add(m.getDiscente().getDiscente());
						if (--totalMatriculas <= 0) {
							break;
						}
					}
				}
				
				// Efetuar e persistir transferências de solicitações
				for( SolicitacaoMatricula s : solicitacoes ){ 
					
					// Validar choque de horários
					if ( !HorarioTurmaUtil.hasChoqueHorarios(turmaDestino, s.getDiscente(), turmaOrigem) ) {
						turmaDao.updateField(SolicitacaoMatricula.class, s.getId(), "turma", turmaDestino );
						
						RegistroTransferencia registro = gerarRegistroTransferencia(s.getDiscente(), turmaOrigem, turmaDestino, isAutomatica(mov), getDAO(DiscenteDao.class, mov));
						registro.setSolicitacaoMatricula(s);
						turmaDao.create( registro );
						
						discentes.add(s.getDiscente());
						if (--totalSolicitacoes <= 0) {
							break;
						}
					}
				}
				
				// Notificar os discentes transferidos por e-mail
				notificarDiscente(discentes, turmaOrigem, turmaDestino, mov);
		
				Comparator<Discente> discenteComparator = new Comparator<Discente>() {
					public int compare(Discente d1, Discente d2) {
						return d1.getNome().compareTo(d2.getNome());
					}
				};
				Collections.sort(discentes, discenteComparator);
				return discentes;
			
			}else{
				
				List<TurmaOrigemTurmaDestinos> listTurmaOrigemDestinos = movTransferencia.getListTurmaOrigemDestinos();
				
				for (TurmaOrigemTurmaDestinos t : listTurmaOrigemDestinos) {
					
					if ( t.getTurmaDestino() != null ){
						
						if( t.getTurmaDestino().getId() != 0 && t.getTurmaDestino().getId() != t.getTurma().getId() ){
						
							t.setTurmaDestino(turmaDao.findByPrimaryKey( t.getTurmaDestino().getId(), Turma.class));
							
							try {
								turmaDao.updateField(MatriculaComponente.class, t.getMatricula().getId(), "turma", t.getTurmaDestino() );
								if (t.getMatricula().getComponente().getId() != t.getTurmaDestino().getDisciplina().getId()){
									turmaDao.updateField(MatriculaComponente.class, t.getMatricula().getId(), "componente", t.getTurmaDestino().getDisciplina() );
								}
							} catch (DAOException d) {
								d.getMessage();
							} 
							
							RegistroTransferencia registro = gerarRegistroTransferencia(t.getMatricula().getDiscente(), t.getTurma(), t.getTurmaDestino(),false, getDAO(DiscenteDao.class, mov));
							registro.setMatricula(t.getMatricula());
							turmaDao.create( registro );
						}	
						
					}
				
				}
				
				return listTurmaOrigemDestinos;
			} 
		} finally {
			if(turmaDao != null) turmaDao.close();
		}
		
	}

	/**
	 * Método responsável por retornar os discentes aptos para a transferência de turmas. 
	 * @param <T>
	 * @param classe
	 * @param turmaOrigem
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	private <T> Collection<T> discenteAptosTransferencia( Class<T> classe, Turma turmaOrigem, Movimento mov) throws DAOException{
		MatriculaComponenteDao matriculaDao = getDAO(MatriculaComponenteDao.class, mov);
		SolicitacaoMatriculaDao solicitacaoDao = getDAO(SolicitacaoMatriculaDao.class, mov);
		TransferenciaTurmaDao transDao = getDAO(TransferenciaTurmaDao.class, mov);
		ProcessamentoMatriculaDao procDao = getDAO(ProcessamentoMatriculaGraduacaoDao.class, mov);
		
		try {
		char nivel = turmaOrigem.getDisciplina().getNivel();
		String estrategiaTransferencia = nivel == NivelEnsino.GRADUACAO ? ParametroHelper.getInstance().getParametro(ParametrosGraduacao.TIPO_ESTRATEGIA_TRANSF_TURMA) 
				: ParametroHelper.getInstance().getParametro(ParametrosTecnico.TIPO_ESTRATEGIA_TRANSF_TURMA);
		Collection<T> lista = new ArrayList<T>();
		// Para cada turma busca as matrículas e ordena
		Map<MatrizCurricular, ReservaCurso> vagasReservadas = procDao.findInformacoesVagasTurma(turmaOrigem.getId());
		int matriculados = procDao.findTotalMatriculadosTurma(turmaOrigem.getId());
		turmaOrigem.setQtdMatriculados(matriculados);
		
		if ( classe.equals(MatriculaComponente.class)) {
			if ( estrategiaTransferencia.equals(TransferenciaTurmaMBean.PRIORIDADE_MATRICULA) ){
				lista = (Collection<T>) transDao.findMatriculasEmOrdemParaTransferenciaPorPrioridade(turmaOrigem, vagasReservadas, MatriculaComponente.class);
			} else if ( estrategiaTransferencia.equals(TransferenciaTurmaMBean.RANDOMICA) ) {
				lista = (Collection<T>) matriculaDao.findMatriculadosAleatoriosByTurma(turmaOrigem, null);
			}	
		} else {
			if ( estrategiaTransferencia.equals(TransferenciaTurmaMBean.PRIORIDADE_MATRICULA) ) {
				lista = (Collection<T>) transDao.findMatriculasEmOrdemParaTransferenciaPorPrioridade(turmaOrigem, vagasReservadas, SolicitacaoMatricula.class);
			} else if ( estrategiaTransferencia.equals(TransferenciaTurmaMBean.RANDOMICA) ) {
				lista = (Collection<T>) solicitacaoDao.findEmEsperaAleatoriasByTurma(turmaOrigem, null);
			}
			
		}
		return lista;
		} finally {
			if(matriculaDao != null) matriculaDao.close();  
			if(solicitacaoDao != null) solicitacaoDao.close();
			if(transDao != null) transDao.close();
			if(procDao != null) procDao.close();
		}
	}
	
	/**
	 * Notificar discente que uma de suas turmas foi alterada
	 * 
	 * @param d
	 * @param turmaOrigem
	 * @param turmaDestino
	 * @throws DAOException 
	 */
	private void notificarDiscente(Collection<Discente> discentes, Turma turmaOrigem, Turma turmaDestino, Movimento mov) throws DAOException {
		PessoaDao dao = getDAO(PessoaDao.class, mov);
		
		try {
			// seta os emails dos discentes que não foram setados
			for (Discente d : discentes) {
				Pessoa pessoa;
				if (isEmpty(d.getPessoa().getEmail())) {
					pessoa = dao.findLeveByDiscente(d.getId()); 
					if (pessoa != null)
						d.setPessoa(pessoa);
				}
			}
			for (Discente d : discentes) {
				if (!isEmpty(d.getPessoa().getEmail())) {
		
					MailBody mail = new MailBody();
					mail.setEmail( d.getPessoa().getEmail() );
					mail.setNome( d.getNome() );
		
					// Definição do corpo da mensagem
					mail.setAssunto("SIGAA - Notificação de Transferência entre Turmas");
					mail.setMensagem("Caro(a) " + d.getNome() +", <br><br>" +
							"Sua matrícula na turma " + turmaOrigem.getDescricaoSemDocente() + 
							", no horário " + turmaOrigem.getDescricaoHorario() + ", " + 
							" foi transferida para a turma " +  turmaDestino.getDescricaoSemDocente() +
							", no horário " + turmaDestino.getDescricaoHorario() +  
							", de acordo com a necessidade do ajustes de turmas pelo departamento que a oferece.<br>");
		
					mail.setReplyTo( mov.getUsuarioLogado().getEmail() ); // Email do usuário que efetuou a transferência
					Mail.send(mail);
				}
			}
		} finally {
			if(dao != null) dao.close();
		}
	}

	/**
	 * Método responsável por gerar o registro de transferência do aluno entre as turmas, 
	 * armazenando a turma de destino e a turma de origem.
	 * 
	 * @param discente
	 * @param turmaOrigem
	 * @param turmaDestino
	 * @return
	 * @throws DAOException 
	 */
	private RegistroTransferencia gerarRegistroTransferencia(DiscenteAdapter discente,
			Turma turmaOrigem, Turma turmaDestino, boolean automatica, DiscenteDao dao) throws DAOException {
		RegistroTransferencia registro = new RegistroTransferencia();
		if (discente.isTecnico()) 
			registro.setDiscente(dao.findByMatricula(discente.getDiscente().getDiscente().getMatricula()));
		else
			registro.setDiscente(discente.getDiscente());
		registro.setTurmaOrigem(turmaOrigem);
		registro.setTurmaDestino(turmaDestino);
		registro.setAutomatica( automatica );
		return registro;
	}

	/**
	 * Define o tipo de transferência
	 * 
	 * @param mov
	 * @return
	 */
	private boolean isAutomatica(Movimento mov) {
		return SigaaListaComando.TRANSFERENCIA_AUTOMATICA.equals(  mov.getCodMovimento() );
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		
		MovimentoTransferenciaTurmas movTransferencia = (MovimentoTransferenciaTurmas) mov;
		ListaMensagens lista = new ListaMensagens();

		if(!movTransferencia.isTransfTurmasByDiscente()){
			Turma turmaOrigem = movTransferencia.getTurmaOrigem();
			Turma turmaDestino = movTransferencia.getTurmaDestino();
	
			TransferenciaTurmasValidator.validaTurmaOrigem(turmaOrigem, lista, movTransferencia.isTransfTurmasByDiscente(),  mov.getUsuarioLogado().isUserInRole(SigaaPapeis.DAE, SigaaPapeis.CDP, SigaaPapeis.ADMINISTRADOR_DAE));
			TransferenciaTurmasValidator.validaTurmaDestino(turmaOrigem, turmaDestino, lista, movTransferencia.isTransfTurmasByDiscente(), movTransferencia.isAdministrador());

		
			TransferenciaTurmasValidator.validaAlunos(isAutomatica(mov), 
					movTransferencia.getQtdMatriculas(), movTransferencia.getQtdSolicitacoes(), 
					turmaOrigem, turmaDestino, 
					movTransferencia.getMatriculas(), movTransferencia.getSolicitacoes(), 
					lista);
		}	

		checkValidation(lista);
	}

}
