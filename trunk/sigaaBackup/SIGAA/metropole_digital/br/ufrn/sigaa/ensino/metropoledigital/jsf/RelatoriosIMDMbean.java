package br.ufrn.sigaa.ensino.metropoledigital.jsf;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.metropoledigital.dao.AcompanhamentoSemanalDiscenteDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.TurmaEntradaTecnicoDao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.AcompanhamentoSemanalDiscente;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.PeriodoAvaliacao;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;

/**
 * Classe responsável por gerar os relatórios do Curso técnico do IMD
 * 
 * @author Rafael Silva
 *
 */

@Component("relatoriosIMD") @Scope("request")
public class RelatoriosIMDMbean extends SigaaAbstractController{
	
	/**Variável estática com a URL do relatório de notas do aluno*/
	private static final String JSP_NOTAS_SEMANAIS_ALUNO = "/metropole_digital/relatorios/notas_semanais_discente.jsf"; 
	/**Turma de Entrada Tecnico*/
	private TurmaEntradaTecnico turmaEntrada;
	/**Discente técnico*/
	private DiscenteTecnico discenteTecnico;
	/**Lista de discentes da turma*/
	private List<DiscenteTecnico> listaDiscentesTurma;
	/**Lista de perídos de avaliação da turma*/
	private List<PeriodoAvaliacao> listaPeriodosTurma;
	/**Lista dos acompanhamentos semanais dos discentes da turma*/
	private List<AcompanhamentoSemanalDiscente> listaAcompanhamentoSemanal; 
	
	
	public RelatoriosIMDMbean() {
		
	}
	
	/**
	 * Método responsável por carregar as notas semanais do Aluno.
	 * 
	 * @throws DAOException 
	 */
	public String notasSemanaisDiscente() throws DAOException{
		discenteTecnico = (DiscenteTecnico) getDiscenteUsuario();
		turmaEntrada = getTurma(discenteTecnico.getTurmaEntradaTecnico().getId());

		listaPeriodosTurma = getPeriodosTurma(turmaEntrada.getId());
		listaAcompanhamentoSemanal = getAcompanhamentoSemanalDiscente(turmaEntrada.getId(), discenteTecnico.getId());
		
		if (listaAcompanhamentoSemanal == null) {
			addMensagemErro("Não há notas semanais cadastradas para a sua matrícula");
			return null;
		}
		
		return forward(JSP_NOTAS_SEMANAIS_ALUNO);
	}
	
	
	
	
	// MÉTODOS AUXILIARES
	/**
	 * Retorna a turma de entrada que possui o id informado.
	 * 
	 * @param idTurmaEntrada
	 * @return
	 * @throws DAOException
	 */
	private TurmaEntradaTecnico getTurma(int idTurmaEntrada) throws DAOException{
		return getDAO(TurmaEntradaTecnicoDao.class).findByPrimaryKey(idTurmaEntrada, TurmaEntradaTecnico.class);
	}
	
	
	/**
	 * Retorna a lista dos períodos do cronograma da turma selecionada.
	 * 
	 * @param idTurmaEntrada
	 * @return
	 * @throws DAOException
	 */
	private List<PeriodoAvaliacao> getPeriodosTurma(int idTurmaEntrada) throws DAOException{
		return (List<PeriodoAvaliacao>) getDAO(TurmaEntradaTecnicoDao.class).findPeriodosByTurmaEntrada(idTurmaEntrada);
	}
	
	/**
	 * Retorna os acompanhamentos semanais do discente na turma.
	 * 
	 * @param idTurmaEntrada
	 * @param idDiscenteTecnico
	 * @return
	 * @throws DAOException
	 */
	public List<AcompanhamentoSemanalDiscente> getAcompanhamentoSemanalDiscente(int idTurmaEntrada, int idDiscenteTecnico) throws DAOException{
		return (List<AcompanhamentoSemanalDiscente>) getDAO(AcompanhamentoSemanalDiscenteDao.class).findAcompanhamentosByDiscenteTurmaEntrada(idTurmaEntrada, idDiscenteTecnico); 
	}
		
	//GETTERS AND SETTERS
	public TurmaEntradaTecnico getTurmaEntrada() {
		return turmaEntrada;
	}

	public void setTurmaEntrada(TurmaEntradaTecnico turmaEntrada) {
		this.turmaEntrada = turmaEntrada;
	}

	public DiscenteTecnico getDiscenteTecnico() {
		return discenteTecnico;
	}

	public void setDiscenteTecnico(DiscenteTecnico discenteTecnico) {
		this.discenteTecnico = discenteTecnico;
	}

	public List<DiscenteTecnico> getListaDiscentesTurma() {
		return listaDiscentesTurma;
	}

	public void setListaDiscentesTurma(List<DiscenteTecnico> listaDiscentesTurma) {
		this.listaDiscentesTurma = listaDiscentesTurma;
	}

	public List<PeriodoAvaliacao> getListaPeriodosTurma() {
		return listaPeriodosTurma;
	}

	public void setListaPeriodosTurma(List<PeriodoAvaliacao> listaPeriodosTurma) {
		this.listaPeriodosTurma = listaPeriodosTurma;
	}

	public List<AcompanhamentoSemanalDiscente> getListaAcompanhamentoSemanal() {
		return listaAcompanhamentoSemanal;
	}

	public void setListaAcompanhamentoSemanal(
			List<AcompanhamentoSemanalDiscente> listaAcompanhamentoSemanal) {
		this.listaAcompanhamentoSemanal = listaAcompanhamentoSemanal;
	}
}
