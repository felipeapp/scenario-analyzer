/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 26/09/2006
 *
 */
package br.ufrn.sigaa.ensino.tecnico.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.RegistroTransferencia;
import br.ufrn.sigaa.ensino.graduacao.negocio.MatriculaGraduacaoValidator;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.relatorios.LinhaAuxiliarTransfTurmaEntrada;

/**
 * Processador responsável pela transferência entre turma de entrada do(s) discente(s) selecionados e seus respectivos componentes  
 * 
 * @author Jean Guerethes
 */
public class ProcessadorTransferenciaTurmaEntradaTecnico extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		carregarDados(mov);

		MovimentoTransferenciaTurmaTurmaEntrada movTransferencia = (MovimentoTransferenciaTurmaTurmaEntrada) mov;
		Comando comando = mov.getCodMovimento();
		GenericDAO dao = getGenericDAO(mov);
		
		try {
			if ( comando.equals(SigaaListaComando.EFETUAR_TRANSFERENCIA_ENTRE_TURMAS) ) {
				
				Set<DiscenteAdapter> chaves = movTransferencia.getTurmas().keySet();
				for (DiscenteAdapter chave : chaves) { 
					Collection<LinhaAuxiliarTransfTurmaEntrada> linhas = movTransferencia.getTurmas().get(chave);
						if ( !linhas.isEmpty() ) {
							for (LinhaAuxiliarTransfTurmaEntrada linha : linhas) {
								
								validate(mov);
								
								linha.getTurma().getId();
								RegistroTransferencia registro = gerarRegistroTransferencia(chave, linha.getTurma(), linha.getTurmaDestino(), false, getDAO(DiscenteDao.class, mov));
								registro.setMatricula( linha.getMatriculaComp() );
								dao.create( registro );
								dao.updateField(MatriculaComponente.class, linha.getMatriculaComp().getId(), "turma.id", linha.getTurmaDestino().getId());
								dao.updateField(DiscenteTecnico.class, chave.getId(), "turmaEntradaTecnico", movTransferencia.getTurmaEntradaDestino());
							}
						} else {
							dao.updateField(DiscenteTecnico.class, chave.getDiscente().getId(), "turmaEntradaTecnico", movTransferencia.getTurmaEntradaDestino());
						}
				}
			}
		} finally {
			dao.close();
		}
		return null;
	}

	/**
	 * Responsável por carregar os dados a serem utilizados no processamento da mudança das turma de entradas e dos componentes curriculares
	 * @param mov
	 * @throws DAOException
	 */
	private void carregarDados(Movimento mov) throws DAOException {
		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class, mov);
		MovimentoTransferenciaTurmaTurmaEntrada movTransferencia = (MovimentoTransferenciaTurmaTurmaEntrada) mov;
		try {
			Set<DiscenteAdapter> chaves = movTransferencia.getTurmas().keySet();
			Collection<Turma> turmasMatriculadas;
			for (DiscenteAdapter chave : chaves) {
				Collection<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>();
				if ( chave instanceof DiscenteTecnico )
					matriculas = dao.findByDiscente((DiscenteTecnico) chave, SituacaoMatricula.MATRICULADO);
				else if ( chave instanceof DiscenteAdapter )
					matriculas = dao.findByDiscente((DiscenteAdapter) chave, SituacaoMatricula.MATRICULADO);
				
				turmasMatriculadas = new ArrayList<Turma>();
				for( MatriculaComponente tmd: matriculas ) {
					if (!isEmpty(tmd.getTurma()) ) {
						tmd.getTurma().getHorarios().iterator();
						turmasMatriculadas.add(tmd.getTurma());
					} 
				}
				for (LinhaAuxiliarTransfTurmaEntrada linha : movTransferencia.getTurmas().get(chave)) {
					for (MatriculaComponente matriculaComponente : matriculas) {
						if ( matriculas.size() == 1 )
							linha.setMatriculaComp( matriculaComponente );
						if ( !isEmpty( matriculaComponente.getTurma() ) && matriculaComponente.getTurma().getId() == linha.getTurma().getId() ) {
							linha.setMatriculaComp( matriculaComponente );
							linha.setTurmasMatriculadas(turmasMatriculadas);
						}
					}
				}
			}
		} finally {
			dao.close();
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
	
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		ListaMensagens lista = new ListaMensagens();
		TurmaDao tDao = getDAO(TurmaDao.class, mov);
		
		try {
			MovimentoTransferenciaTurmaTurmaEntrada movTransferencia = (MovimentoTransferenciaTurmaTurmaEntrada) mov;
			Set<DiscenteAdapter> chaves = movTransferencia.getTurmas().keySet();  
			for (DiscenteAdapter chave : chaves) {
				for (LinhaAuxiliarTransfTurmaEntrada linhaAuxiliarTransfTurmaEntrada : movTransferencia.getTurmas().get(chave)) {
					//Removendo a turma que será Trocada pela nova Turma.
					linhaAuxiliarTransfTurmaEntrada.getTurmasMatriculadas().remove( linhaAuxiliarTransfTurmaEntrada.getTurma() );
					if ( linhaAuxiliarTransfTurmaEntrada.getTurmaDestino().getId() > 0 ) {
						linhaAuxiliarTransfTurmaEntrada.getTurmaDestino().setHorarios(new ArrayList<HorarioTurma>());
	 					linhaAuxiliarTransfTurmaEntrada.getTurmaDestino().getHorarios().addAll(tDao.findHorariosByTurma(linhaAuxiliarTransfTurmaEntrada.getTurmaDestino()));
						MatriculaGraduacaoValidator.validarChoqueHorarios( linhaAuxiliarTransfTurmaEntrada.getTurmaDestino(), 
								linhaAuxiliarTransfTurmaEntrada.getTurmasMatriculadas(), lista);
					}
					
					tDao.initialize(linhaAuxiliarTransfTurmaEntrada.getTurmaDestino());
					if (linhaAuxiliarTransfTurmaEntrada.getTurmaDestino().getId() > 0 && 
							tDao.findTotalMatriculados(linhaAuxiliarTransfTurmaEntrada.getTurmaDestino().getId()) + 1 > linhaAuxiliarTransfTurmaEntrada.getTurmaDestino().getCapacidadeAluno()) {
						lista.addErro("A turma "+ linhaAuxiliarTransfTurmaEntrada.getTurmaDestino().getCodigo() +" da disciplina " + linhaAuxiliarTransfTurmaEntrada.getTurma().getNomeResumido() + 
								" excedeu a sua capacidade de "+ linhaAuxiliarTransfTurmaEntrada.getTurmaDestino().getCapacidadeAluno() +" vaga(s). Não é possível realizar sua matrícula.<br/>");
					} else if ( linhaAuxiliarTransfTurmaEntrada.getTurmaDestino().getId() == 0 ) {
						lista.addErro("Não há turma correspondentes para a turma " + linhaAuxiliarTransfTurmaEntrada.getTurma().getDescricao() + " para o discente " + 
								linhaAuxiliarTransfTurmaEntrada.getDiscente().getNome()+ ".");
					}
				}
	        }
		} finally {
			tDao.close();
		}
		
		checkValidation(lista);
	}

}