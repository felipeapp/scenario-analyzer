/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 13/08/2013
 *
 */
package br.ufrn.sigaa.ensino_rede.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.hibernate.HibernateException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino_rede.dao.DiscenteAssociadoDao;
import br.ufrn.sigaa.ensino_rede.dao.TurmaRedeDao;
import br.ufrn.sigaa.ensino_rede.dominio.ComponenteCurricularRede;
import br.ufrn.sigaa.ensino_rede.dominio.DiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.dominio.DocenteRede;
import br.ufrn.sigaa.ensino_rede.dominio.DocenteTurmaRede;
import br.ufrn.sigaa.ensino_rede.dominio.MatriculaComponenteRede;
import br.ufrn.sigaa.ensino_rede.dominio.TurmaRede;

/**
 * Processador responsável pelo cadastro de turmas de ensino em rede
 *
 * @author Diego Jácome
 */
public class ProcessadorCadastroTurmaRede  extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,RemoteException {
	
		if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRO_TURMA_REDE)) 
			return cadastrarTurma(mov);
		else if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_TURMA_REDE)) 
			return alterarTurma(mov);

		return null;
	}

	// MÉTODOS PARA CRIAÇÃO DA TURMA
	
	/**
	 * Método para o cadastramento de uma nova turma.
	 * 
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	private Object cadastrarTurma(Movimento mov) throws DAOException, NegocioException, ArqException {
		
		MovimentoCadastroTurmaRede cmov = (MovimentoCadastroTurmaRede) mov;
		TurmaRedeDao dao = null;
		
		try {
						
			dao = getDAO(TurmaRedeDao.class, cmov);
			TurmaRede turma = cmov.getTurma();
			CampusIes campus = cmov.getCampus();
			ArrayList<DiscenteAssociado> discentes = cmov.getDiscentes();
			ArrayList<DocenteRede> docentes = cmov.getDocentes();
			
			validate(cmov);
			
			processarTurma(turma,campus,discentes,docentes,dao);
			processarDiscentes(turma,discentes,dao);
			processarDocentes(turma,docentes,dao);
			
		} finally {
			if (dao != null)
				dao.close();
		}
		return null;
	}

	/**
	 * Método auxiliar do excute(), para o cadastro de turma.
	 * @param campus 
	 * @param discentes 
	 * 
	 * @param t
	 * @param dao
	 * @return
	 * @throws DAOException
	 */
	private void processarTurma(TurmaRede turma, CampusIes campus, ArrayList<DiscenteAssociado> discentes, ArrayList<DocenteRede> docentes, TurmaRedeDao dao) throws DAOException {

		if (discentes != null && !discentes.isEmpty())
			turma.setDadosCurso(discentes.get(0).getDadosCurso());
		
		if (docentes != null && !docentes.isEmpty())
			turma.setSituacaoTurma(new SituacaoTurma(SituacaoTurma.ABERTA));
		else
			turma.setSituacaoTurma(new SituacaoTurma(SituacaoTurma.A_DEFINIR_DOCENTE));
		
		String codigo = gerarCodigoTurma(turma.getComponente(), campus, turma.getAno(), turma.getPeriodo(), dao);
		turma.setCodigo(codigo);
		dao.create(turma);
	}
	
	/**
	 * Método auxiliar do excute(), para o cadastro de matrículas dos discentes.
	 * 
	 * @param t
	 * @param discentes
	 * @param dao
	 * @return
	 * @throws DAOException
	 */
	private void processarDiscentes(TurmaRede t , ArrayList<DiscenteAssociado> discentes, TurmaRedeDao dao) throws DAOException {

		if (discentes != null && !discentes.isEmpty() ){
			
			for (DiscenteAssociado d : discentes){
				
				MatriculaComponenteRede m = new MatriculaComponenteRede();
				m.setDiscente(d);
				m.setSituacao(new SituacaoMatricula(SituacaoMatricula.MATRICULADO.getId()));
				m.setAno(t.getAno());
				m.setPeriodo(t.getPeriodo());
				m.setTurma(t);
				m.setMesInicio(CalendarUtils.getMesByData(t.getDataInicio()));
				m.setMesFim(CalendarUtils.getMesByData(t.getDataInicio()));
				m.setDataCadastro(new Date());
				
				dao.create(m);
				
			}
			
		}
		
	}
	
	/**
	 * Método auxiliar do excute(), para o cadastro de docentesTurma.
	 * 
	 * @param t
	 * @param docentes
	 * @param dao
	 * @return
	 * @throws DAOException
	 */
	private void processarDocentes(TurmaRede t, ArrayList<DocenteRede> docentes, TurmaRedeDao dao) throws DAOException {

		if (docentes != null && !docentes.isEmpty() ){
			
			for (DocenteRede d : docentes){
				
				DocenteTurmaRede dt = new DocenteTurmaRede();
				dt.setDocente(d);
				dt.setTurma(t);
				
				dao.create(dt);
			}
			
		}
		
	}
	
	// MÉTODOS PARA ALTERAÇÃO DA TURMA
	
	/**
	 * Método para a alteração de uma turma.
	 * 
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	private Object alterarTurma(Movimento mov) throws NegocioException, ArqException {
		
		MovimentoCadastroTurmaRede cmov = (MovimentoCadastroTurmaRede) mov;
		TurmaRedeDao dao = null;
		
		try {
						
			dao = getDAO(TurmaRedeDao.class, cmov);
			
			TurmaRede turma = cmov.getTurma();
			ArrayList<DiscenteAssociado> discentes = cmov.getDiscentes();
			ArrayList<DocenteRede> docentes = cmov.getDocentes();
			
			ArrayList<MatriculaComponenteRede> matriculados = (ArrayList<MatriculaComponenteRede>) dao.findParticipantesTurma(turma.getId(), SituacaoMatricula.getSituacoesMatriculadas());
			
			ArrayList<DiscenteAssociado> discentesRemovidos = cmov.getDiscentesRemovidos();
			ArrayList<DocenteRede> docentesRemovidos = cmov.getDocentesRemovidos();

			desmarcarDiscentesMatriculados(matriculados,discentes);
			desmarcarDocenteTurmaAssociados(turma.getDocentesTurmas(),docentes);
			
			validate(cmov);
			
			// ATENÇÃO COM CASCADE TYPE DE DOCENTES TURMA
			// SE QUISER UTILIZAR UPDATE - DEVE-SE REMOVER OS DOCENTES LISTADOS PARA REMOÇÃO DA TURMA
			dao.updateFields(TurmaRede.class, turma.getId(), 
					new String [] {"ano","periodo","dataInicio","dataFim"}, 
					new Object[] {turma.getAno(),turma.getPeriodo(),turma.getDataInicio(),turma.getDataFim()});
			
			processarDiscentes(turma,discentes,dao);
			removerDiscentes(matriculados,discentesRemovidos,dao);
			
			processarDocentes(turma,docentes,dao);
			removerDocentes(turma,docentesRemovidos,dao);

		} finally {
			if (dao != null)
				dao.close();
		}
		return null;
	}

	/**
	 * Método que retira os discentes que já estão matriculados na turma das matrículas a cadastrar.
	 * 
	 * @param matriculados
	 * @param discentesRemovidos
	 * @param dao
	 * @return
	 * @throws DAOException
	 */
	private void desmarcarDiscentesMatriculados(ArrayList<MatriculaComponenteRede> matriculados, ArrayList<DiscenteAssociado> discentes) throws HibernateException, DAOException {
		
		// Remove os discentes que já estão matriculados da lista de discentes que serão matriculados
		if (!isEmpty(matriculados) && !isEmpty(discentes)){
			
			Iterator<DiscenteAssociado> it = discentes.iterator();
			while (it.hasNext()){
				DiscenteAssociado d = it.next();
				for (MatriculaComponenteRede m : matriculados){
					if (d.getId() == m.getDiscente().getId())
						it.remove();
				}
			}
		}
		
	}
	
	/**
	 * Método que retira os docentes que já estão associados na turma dos docentes a cadastrar.
	 * 
	 * @param matriculados
	 * @param discentesRemovidos
	 * @param dao
	 * @return
	 * @throws DAOException
	 */
	private void desmarcarDocenteTurmaAssociados(Set<DocenteTurmaRede> associados, ArrayList<DocenteRede> docentes) {
		
		// Remove os docentes que já estão associados da lista de docentes que serão associados
		if (!isEmpty(associados) && !isEmpty(docentes)){
			
			Iterator<DocenteRede> it = docentes.iterator();
			while (it.hasNext()){
				DocenteRede d = it.next();
				for (DocenteTurmaRede dt : associados){
					if (d.getId() == dt.getDocente().getId())
						it.remove();
				}
			}
		}
	}
	
	/**
	 * Método auxiliar do excute(), para cancelamento de matrículas na operação de alteração.
	 * 
	 * @param matriculados
	 * @param discentesRemovidos
	 * @param dao
	 * @return
	 * @throws DAOException
	 */
	private void removerDiscentes(ArrayList<MatriculaComponenteRede> matriculados, ArrayList<DiscenteAssociado> discentesRemovidos, TurmaRedeDao dao) throws DAOException {

		if (!isEmpty(discentesRemovidos) && !isEmpty(matriculados)){
			for (DiscenteAssociado dr : discentesRemovidos){
				for (MatriculaComponenteRede m : matriculados){
					if (m.getDiscente().getId() == dr.getId())
						dao.updateField(MatriculaComponenteRede.class, m.getId(), "situacao.id", SituacaoMatricula.CANCELADO);
				}
			}
		}
		
	}
	
	/**
	 * Método auxiliar do excute(), para remoção de docentesTurma na operação de alteração.
	 * 
	 * @param turma
	 * @param docentesRemovidos
	 * @param dao
	 * @return
	 * @throws DAOException
	 */
	private void removerDocentes(TurmaRede turma, ArrayList<DocenteRede> docentesRemovidos, TurmaRedeDao dao) throws DAOException {

		ArrayList<DocenteTurmaRede> docentesTurma = (ArrayList<DocenteTurmaRede>) dao.findByExactField(DocenteTurmaRede.class, "turma.id", turma.getId());
		
		if (!isEmpty(docentesTurma) && !isEmpty(docentesRemovidos)){
			for (DocenteRede dr : docentesRemovidos){
				for (DocenteTurmaRede dt : docentesTurma){
					if (dt.getDocente().getId() == dr.getId())
						dao.remove(dt);
				}
			}
		}		
	}
	
	// MÉTODOS GERAIS
	
	/**
	 * Gera o código da turma
	 * @param componente
	 * @param campus 
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	private String gerarCodigoTurma(ComponenteCurricularRede componente, CampusIes campus, int ano, int periodo, TurmaRedeDao dao) throws DAOException {

		int qtdTurmas = dao.countTurmasByComponenteAnoPeriodo(componente.getId(), campus.getId(),  ano, periodo);
		qtdTurmas++;
		if( qtdTurmas < 10 )
			return "0" + qtdTurmas;
		else
			return qtdTurmas + "";

	}
	
	public void validate(Movimento mov, boolean alterar) throws NegocioException, ArqException {

		checkRole(new int[] { SigaaPapeis.COORDENADOR_GERAL_REDE , SigaaPapeis.COORDENADOR_UNIDADE_REDE },mov); 

		MovimentoCadastroTurmaRede cmov = (MovimentoCadastroTurmaRede) mov;
		TurmaRede turma = cmov.getTurma();
		ArrayList<DiscenteAssociado> discentes = cmov.getDiscentes();
		
		DiscenteAssociadoDao daDao = null;
		TurmaRedeDao tDao = null;

		try {
		
			if(isEmpty(turma.getAno()))
				throw new NegocioException ("Ano da Turma: Campo obrigatório não informado.");
			if(isEmpty(turma.getPeriodo()))
				throw new NegocioException ("Período da Turma: Campo obrigatório não informado.");
			if(isEmpty(turma.getDataInicio()))
				throw new NegocioException ("Início da Turma: Campo obrigatório não informado.");
			if(isEmpty(turma.getDataFim()))
				throw new NegocioException ("Fim da Turma: Campo obrigatório não informado.");
	
			if (!alterar && isEmpty(discentes))
				throw new NegocioException ("É necessário escolher pelo menos um discente.");

			if (!isEmpty(discentes)){
		
				daDao = getDAO(DiscenteAssociadoDao.class, cmov);
				tDao = getDAO(TurmaRedeDao.class, cmov);
				ArrayList<Integer> idDiscentesAproveitados = null;
				
				if (!alterar)
					idDiscentesAproveitados = daDao.findDiscentePorSituacao(turma.getComponente(), discentes, SituacaoMatricula.getSituacoesPagasEMatriculadas());			
				else
					idDiscentesAproveitados = daDao.findDiscentePorSituacao(turma.getComponente(), discentes, SituacaoMatricula.getSituacoesPagas());			
					
				if (!isEmpty(idDiscentesAproveitados) && !isEmpty(discentes)){
					String erroMsg = "";
					boolean erro = false;
					
					for (DiscenteAssociado d : discentes){
						for (Integer idAprovado : idDiscentesAproveitados){
							if (d.getId() == idAprovado.intValue()){
								erroMsg += erroMsg == "" ? d.getNome() : ", " + d.getNome(); 
								erro = true;
							}			
						}
					}
					
					if (erro){
						erroMsg = "Os seguintes discentes já foram aproveitados na turma: " +erroMsg;
						throw new NegocioException (erroMsg);
					}						
				}
				
				if (alterar){
					
					ArrayList<DiscenteAssociado> discentesRemovidos = cmov.getDiscentesRemovidos();
					
					ArrayList<MatriculaComponenteRede> matriculados = (ArrayList<MatriculaComponenteRede>) tDao.findParticipantesTurma(turma.getId(), SituacaoMatricula.getSituacoesMatriculadas());
					ArrayList<DiscenteAssociado> discentesMatriculados = new ArrayList<DiscenteAssociado>();
					if (!isEmpty(matriculados)){
						for (MatriculaComponenteRede m : matriculados)
							discentesMatriculados.add(m.getDiscente());
					}	
					
					if (!isEmpty(discentesMatriculados) && !isEmpty(discentesRemovidos)){
						
						boolean todosRemovidos = true;
						for (DiscenteAssociado da: discentesMatriculados){
							boolean naoRemovido = true;
							for (DiscenteAssociado dr: discentesRemovidos){
								if (da.getId() == dr.getId())
									naoRemovido = false;
							}
							if (naoRemovido)
								todosRemovidos = false;
						}
						
						if (todosRemovidos && isEmpty(discentes))
							throw new NegocioException ("É necessário escolher pelo menos um discente.");

					}
				}
			}
		} finally {
			if (daDao != null)
				daDao.close();
			if (tDao != null)
				tDao.close();
		}
		
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {

		if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRO_TURMA_REDE)) 
			validate(mov, false);
		else if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_TURMA_REDE)) 
			validate(mov, true);
		
	}

}
