/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 13/07/2011
 * Autor: Rafael Gomes
 */
package br.ufrn.sigaa.ensino.medio.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.medio.dao.MatriculaDiscenteSerieDao;
import br.ufrn.sigaa.ensino.medio.dao.TurmaSerieDao;
import br.ufrn.sigaa.ensino.medio.dominio.DiscenteMedio;
import br.ufrn.sigaa.ensino.medio.dominio.MatriculaDiscenteSerie;
import br.ufrn.sigaa.ensino.medio.dominio.RegistroTransferenciaMedio;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerie;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerieAno;
import br.ufrn.sigaa.ensino.util.HorarioTurmaUtil;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Processador para efetuar transfer�ncias de alunos entre turmas do ensino m�dio.
 *
 * @author Rafael Gomes
 */
public class ProcessadorTransferenciaTurmasMedio extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		validate(mov);

		MovimentoTransferenciaTurmasMedio movTransferencia = (MovimentoTransferenciaTurmasMedio) mov;
		TurmaSerie turmaSerieOrigem = movTransferencia.getTurmaSerieOrigem();
		TurmaSerie turmaSerieDestino = movTransferencia.getTurmaSerieDestino();
		
		// DAOs
		TurmaSerieDao turmaDao = getDAO(TurmaSerieDao.class, mov);
		MatriculaDiscenteSerieDao mdsDao = getDAO(MatriculaDiscenteSerieDao.class, mov);
		
		// Definir alunos a serem transferidos, dependendo do tipo de transfer�ncia
		Collection<MatriculaDiscenteSerie> matriculas = new ArrayList<MatriculaDiscenteSerie>();
		try {
			int totalMatriculas = 0;
			if ( isAutomatica(mov) ) {
				if ( movTransferencia.getQtdMatriculas() > 0) {
					matriculas = mdsDao.findAlunosAleatoriosByTurma(turmaSerieOrigem, null);
					totalMatriculas = movTransferencia.getQtdMatriculas();
				}
			} else {
				matriculas = movTransferencia.getMatriculas();
				totalMatriculas = matriculas.size();
			}
			
			// Cole��o que ir� armazenar os discentes efetivamente transferidos
			List<Discente> discentes = new ArrayList<Discente>();
			
			turmaSerieDestino = turmaDao.findByPrimaryKey( turmaSerieDestino.getId(), TurmaSerie.class);
	
			List<Integer> idsDiscente = new ArrayList<Integer>();
			for (Iterator<MatriculaDiscenteSerie> it = matriculas.iterator(); it.hasNext(); ) {
				MatriculaDiscenteSerie m = it.next();
				idsDiscente.add(m.getDiscenteMedio().getId());
			}
	
			List<MatriculaComponente> matriculasTodosDiscentes = 
				mdsDao.findDisciplinasByMatriculasDiscenteSerie(turmaSerieOrigem, idsDiscente);
				
			// Efetuar e persistir transfer�ncias de matr�culas em componentes
			for( MatriculaDiscenteSerie mds : matriculas ){ 
					
				// Transfere as turmas de componenteCurricular para as matriculaComponente da turmaSerie de Destino.
				if(persistirTransferencias(matriculasTodosDiscentes, mds, turmaSerieOrigem, turmaSerieDestino, mov)){
					
					// Update da turmaSerie em MatriculaDiscenteSerie
					turmaDao.updateField(MatriculaDiscenteSerie.class, mds.getId(), "turmaSerie", turmaSerieDestino );
					RegistroTransferenciaMedio registro = gerarRegistroTransferencia(mds.getDiscenteMedio(), turmaSerieOrigem, turmaSerieDestino,isAutomatica(mov));
					registro.setMatriculaDiscenteSerie(mds);
					turmaDao.create( registro );
					
					discentes.add(mds.getDiscenteMedio().getDiscente());
					if (--totalMatriculas <= 0) {
						break;
					}
					
				}	
			}
			
			// Notificar os discentes transferidos por e-mail
			for (Discente d : discentes) {
				notificarDiscente(d, turmaSerieOrigem, turmaSerieDestino, mov);
			}
	
			Comparator<Discente> discenteComparator = new Comparator<Discente>() {
				public int compare(Discente d1, Discente d2) {
					return d1.getNome().compareTo(d2.getNome());
				}
			};
			Collections.sort(discentes, discenteComparator);
			return discentes;
		} finally {
			if(turmaDao != null) turmaDao.close();
			if(mdsDao != null) mdsDao.close();
		}
	
	}
	
	/**
	 * M�todo respons�vel pela transfer�ncia entre as disciplinas (classe {@link Turma})
	 * da TurmaSerie de origem para as disciplinas da TurmaSerie de destino.
	 * 
	 * @param matriculasOrigemTodosDiscentes
	 * @param mds
	 * @param turmaSerieOrigem
	 * @param turmaSerieDestino
	 * @param mov
	 * @throws DAOException 
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	private boolean persistirTransferencias(List<MatriculaComponente> matriculasOrigemTodosDiscentes, 
			MatriculaDiscenteSerie mds, TurmaSerie turmaSerieOrigem, TurmaSerie turmaSerieDestino, Movimento mov ) 
			throws DAOException, NegocioException	{
		
		TurmaDao turmaDao = getDAO(TurmaDao.class, mov);
		DiscenteMedio discente = mds.getDiscenteMedio();
		boolean matriculasTransferidasSucesso = false;
		
		try {
			List<MatriculaComponente> matriculasOrigemDiscenteAtual = new ArrayList<MatriculaComponente>();
			Collection<Turma> disciplinasTurmaOrigem = new ArrayList<Turma>();
			for( MatriculaComponente mc : matriculasOrigemTodosDiscentes ){ 
				if (discente.getId() == mc.getDiscente().getId()){
					matriculasOrigemDiscenteAtual.add(mc);
					disciplinasTurmaOrigem.add(mc.getTurma());	
				}
			}	
			
			Map<Integer, Turma> mapTurmasDestinoByComponente = new HashMap<Integer, Turma>();
			for ( TurmaSerieAno tsa : turmaSerieDestino.getDisciplinas() ){
				mapTurmasDestinoByComponente.put(tsa.getTurma().getDisciplina().getId(), tsa.getTurma());
			}
			
			for( MatriculaComponente mc : matriculasOrigemDiscenteAtual){
				Turma turmaOrigem = mc.getTurma();
				Turma turmaDestino = new Turma();
				if ( mapTurmasDestinoByComponente.get(turmaOrigem.getDisciplina().getId()) != null ) {
					turmaDestino = mapTurmasDestinoByComponente.get(turmaOrigem.getDisciplina().getId());
				
					// Validar choque de hor�rios de turma com turmas em depend�ncia do aluno.
					if ( !HorarioTurmaUtil.hasChoqueHorariosTurmas(turmaDestino, discente, disciplinasTurmaOrigem) ) {
						try {
							turmaDao.updateField(MatriculaComponente.class, mc.getId(), "turma", turmaDestino );
							matriculasTransferidasSucesso = true;
						} catch (DAOException e) {
							e.getMessage();
							matriculasTransferidasSucesso = false;
						}
					} else {
						matriculasTransferidasSucesso = false;
						throw new NegocioException("Aten��o! N�o foi poss�vel transferir o discente, " +
								"devido a choque de hor�rios da disciplina '"+ turmaDestino.getDescricaoSemDocente() +
								"' com outras turmas matriculadas.");
					}
				}		
			} 		
		} finally {
			if ( turmaDao != null ) turmaDao.close();
		}
		return matriculasTransferidasSucesso;
	}

	
	/**
	 * Notificar discente que uma de suas turmas foi alterada
	 * 
	 * @param d
	 * @param turmaOrigem
	 * @param turmaDestino
	 * @throws DAOException 
	 */
	private void notificarDiscente(Discente d, TurmaSerie turmaSerieOrigem, TurmaSerie turmaSerieDestino, Movimento mov) throws DAOException {
		MailBody mail = new MailBody();
		UsuarioDao udao = getDAO(UsuarioDao.class, mov);
		try {
			Usuario usuarioDiscente = udao.findLeveByDiscente(d);
			String emailDiscente = null;
			if (usuarioDiscente != null)
				emailDiscente = usuarioDiscente.getEmail();
			else
				emailDiscente = d.getPessoa().getEmail();
			if (emailDiscente != null ) {
	
				mail.setEmail( emailDiscente );
				mail.setNome( d.getNome() );
	
				// Defini��o do corpo da mensagem
				mail.setAssunto("SIGAA - Notifica��o de Transfer�ncia entre Turmas");
				mail.setMensagem("Caro(a) " + d.getNome() +", <br><br>" +
						"Sua matr�cula na turma " + turmaSerieOrigem.getDescricaoCompleta() + 
						", foi transferida para a turma " +  turmaSerieDestino.getDescricaoCompleta() +
						", de acordo com a necessidade do ajustes de turmas pelo departamento que a oferece.<br>");
	
				mail.setReplyTo( mov.getUsuarioLogado().getEmail() ); // Email do usu�rio que efetuou a transfer�ncia
				Mail.send(mail);
			}
		} finally {
			if(udao != null) udao.close();
		}
	}

	/**
	 * M�todo respons�vel por criar o registro de transfer�ncia do aluno entre as turmas.
	 * 
	 * @param discente
	 * @param turmaOrigem
	 * @param turmaDestino
	 * @return
	 * @throws DAOException 
	 */
	private RegistroTransferenciaMedio gerarRegistroTransferencia(DiscenteMedio discente,
			TurmaSerie turmaSerieOrigem, TurmaSerie turmaSerieDestino, boolean automatica) throws DAOException {
		
		RegistroTransferenciaMedio registro = new RegistroTransferenciaMedio();
		
		registro.setDiscente(discente);
		registro.setTurmaSerieOrigem(turmaSerieOrigem);
		registro.setTurmaSerieDestino(turmaSerieDestino);
		registro.setAutomatica( automatica );
		return registro;
	}

	/**
	 * Define o tipo de transfer�ncia
	 * 
	 * @param mov
	 * @return
	 */
	private boolean isAutomatica(Movimento mov) {
		return SigaaListaComando.TRANSFERENCIA_AUTOMATICA_MEDIO.equals(  mov.getCodMovimento() );
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		MovimentoTransferenciaTurmasMedio movTransferencia = (MovimentoTransferenciaTurmasMedio) mov;
		ListaMensagens lista = new ListaMensagens();

		TurmaSerie turmaSerieOrigem = movTransferencia.getTurmaSerieOrigem();
		TurmaSerie turmaSerieDestino = movTransferencia.getTurmaSerieDestino();

		TransferenciaTurmasMedioValidator.validaTurmaSerieOrigem(turmaSerieOrigem, lista);
		TransferenciaTurmasMedioValidator.validaTurmaSerieDestino(turmaSerieOrigem, turmaSerieDestino, lista);

	
		TransferenciaTurmasMedioValidator.validaAlunos(isAutomatica(mov), 
				movTransferencia.getQtdMatriculas(),
				turmaSerieOrigem, turmaSerieDestino, 
				movTransferencia.getMatriculas(),  
				lista);

		checkValidation(lista);
	}

}
