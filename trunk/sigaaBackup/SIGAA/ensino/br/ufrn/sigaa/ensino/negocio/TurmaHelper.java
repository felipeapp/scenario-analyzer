/*
 * SIGAA - Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 11/08/2009
 */
package br.ufrn.sigaa.ensino.negocio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dao.AlteracaoTurmaDao;
import br.ufrn.sigaa.ensino.dominio.AlteracaoNota;
import br.ufrn.sigaa.ensino.dominio.AlteracaoStatusTurma;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.negocio.dominio.TurmaMov;

/**
 * Classe que contém métodos utilitários para manipulação de turmas
 * @author Victor Hugo
 */
public class TurmaHelper {

	/**
	 * Este método cria uma registro de alteração da turma AlteracaoStatusTurma 
	 * @param turma
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	public static AlteracaoStatusTurma criarAlteracaoStatusTurma( Turma turma, Movimento mov ) throws DAOException{
		
		AlteracaoStatusTurma alteracao = AlteracaoStatusTurma.create(turma, (Usuario) mov.getUsuarioLogado());
		alteracao.setCodMovimento( mov.getCodMovimento().getId() );
		
		TurmaDao dao = null;
		try {
			dao = DAOFactory.getInstance().getDAOMov( TurmaDao.class, mov );
			int idSituacaoAtual = dao.findSituacaoTurmaByIdTurma( turma.getId() );
			alteracao.setSituacaoAntiga( new SituacaoTurma(idSituacaoAtual) );
			dao.create( alteracao );
		} finally{
			if( dao != null )
				dao.close();
		}
		
		return alteracao;
	}
	
	/**
	 * Este método cria registros de alterações de nota: AlteracaoNota
	 * Para que o método funcione corretamente é necessário que as notas iniciais da turma esteja setada no movimento e as matrículas
	 * da turma estejam setadas com as nota_unidade. Alguns ifs evitam NullPointer.
	 * @param t
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	public static void criarAlteracoesNotasEmTurma(Turma t, Movimento mov ) throws DAOException {	
		
		TurmaDao dao =  DAOFactory.getInstance().getDAOMov( TurmaDao.class, mov );
		TurmaMov turmaMov = (TurmaMov) mov;
		try {
			
			if( ! (ValidatorUtil.isEmpty(t) || ValidatorUtil.isEmpty(t.getMatriculasDisciplina()) || ValidatorUtil.isEmpty(turmaMov.getNotasIniciaisTurma()) )  ) {			
				Collection<NotaUnidade> notasIniciaisTurma = turmaMov.getNotasIniciaisTurma();		
				for(MatriculaComponente mc : t.getMatriculasDisciplina()) {
					if( !ValidatorUtil.isEmpty(mc.getNotas()) ) {
						for(NotaUnidade notaAtual : mc.getNotas()) {
							for(NotaUnidade notaInicial : notasIniciaisTurma) {
								if(notaInicial.getId() == notaAtual.getId()) {
									if(notaInicial.getNota() != null && notaAtual.getNota()!=null && notaInicial.getNota().doubleValue() != notaAtual.getNota().doubleValue()) {
										
										AlteracaoNota alteracao = new AlteracaoNota();								
										alteracao.setCodMovimento(mov.getCodMovimento().getId());
										alteracao.setDataAlteracao(new Date());
										alteracao.setUsuario((Usuario)turmaMov.getUsuarioLogado());								
										alteracao.setNotaAntiga(notaInicial.getNota().doubleValue());
										alteracao.setNotaNova(notaAtual.getNota().doubleValue());
										alteracao.setNotaUnidade(notaAtual);
										dao.createNoFlush(alteracao);
										//Evita duplicidade de cadastro em AlteracaoNota. 
										notaInicial.setNota(notaAtual.getNota().doubleValue());
										break;
										
									}
								}
							}
						}
					}
				}
			}
		} finally {
			if( dao != null )
				dao.close();
		}	
		
	}	
	
	
	/**
	 * Informa se a turma esta aberta e a situação anterior era consolidada.
	 * @param t
	 * @return
	 * @throws DAOException
	 */
	public static boolean isReconsolidacaoTurma(Turma t) throws DAOException {		
		AlteracaoTurmaDao dao =  DAOFactory.getInstance().getDAO(AlteracaoTurmaDao.class);
		try {			
			Integer situacaoAnteriorMaisRecenteTurma = dao.getSituacaoAnteriorMaisRecenteTurma(t);			
			if(situacaoAnteriorMaisRecenteTurma != null)			
				return (situacaoAnteriorMaisRecenteTurma == SituacaoTurma.CONSOLIDADA && t.isAberta());			
		} finally {
			if( dao != null )
				dao.close();
		}				
		return false;
	}
	
}
