/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 02/03/2010
 * Autor:     David Pereira
 */
package br.ufrn.sigaa.ensino.graduacao.negocio.calculos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.graduacao.DiscenteGraduacaoDao;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.TipoGenerico;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.ensino.negocio.calculos.CalculosDiscenteChainNode;

/**
 * Classe para calcular o status atual de um
 * discente de graduação.
 * 
 * @author David Pereira
 *
 */
public class CalcularStatusDiscente extends CalculosDiscenteChainNode<DiscenteGraduacao> {

	/**
	 * Recalcula e altera status do discente para GRADUANDO ou FORMANDO com base
	 * no total de créditos e CH integralizados CUIDADO! ao final desse método
	 * os totais integralizados do discente podem estar alterados.
	 *
	 * @param preProcessamento - Indica se o status não deve ser atualizado pois está 
	 * 							 realizando cálculos do pré-processamento da rematrícula
	 * @throws ArqException
	 */
	@Override
	public void processar(DiscenteGraduacao d, Movimento mov, boolean preProcessamento) throws ArqException, NegocioException {

			
			if (StatusDiscente.getAtivos().contains(d.getStatus())) {	
				DiscenteGraduacaoDao dgdao = getDAO(DiscenteGraduacaoDao.class, mov);
				DiscenteDao ddao = getDAO(DiscenteDao.class, mov);
				try {
					
					// status discente atual
					int status = d.getStatus();
					
					// variável que controla a quantidade de componentes do currículo que estão pendentes que o aluno ainda não se matriculou 
					short pendenciasMatricula = verificaPendencias(d,	ddao);

					status = definirStatusDiscente(d, mov, pendenciasMatricula);

					if ( preProcessamento ) {
						boolean possivelFormando = (  status == StatusDiscente.FORMANDO );
						dgdao.updateField(DiscenteGraduacao.class, d.getId(), "possivelFormando",  possivelFormando);
					} else {
						// se o status foi alterado, atualiza no banco e no objeto
						if ( status != d.getStatus()) {
							DiscenteHelper.alterarStatusDiscente(d, status, mov, dgdao);
							d.setStatus(status);
						}
					}
				} finally {
					dgdao.close();
					ddao.close();
				}
			}
	}

	/**
	 * Verifica a quantidade de componente pendentes
	 * @param d
	 * @param pendenciasMatricula
	 * @param ddao
	 * @return
	 * @throws DAOException
	 */
	private short verificaPendencias(DiscenteGraduacao d, DiscenteDao ddao) throws DAOException {
		
		short pendenciasMatricula = 0;
		
		if (d.getTotalAtividadesPendentes() == null || d.getTotalAtividadesPendentes() > 0) {
			List<MatriculaComponente> disciplinas = ddao.findDisciplinasConcluidasMatriculadas(d.getId(), true);
			Collection<ComponenteCurricular> componentesPendentes = ddao.findByDisciplinasCurricularesPendentes(d.getId(), disciplinas, new ArrayList<TipoGenerico>());
			// total de pendências
			short pendentes = (short) componentesPendentes.size();
			
			for (ComponenteCurricular c : componentesPendentes) {
				if (!c.isMatriculado() && !c.isMatriculadoEmEquivalente())
					pendenciasMatricula++;
			}
			
			d.setTotalAtividadesPendentes(pendentes);
		}
		return pendenciasMatricula;
	}

	/**
	 * Define se o discente é GRADUANDO, FORMANDO ou ATIVO
	 * 
	 * @param d
	 * @param mov
	 * @param pendenciasMatricula
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private int definirStatusDiscente(DiscenteGraduacao d, Movimento mov, short pendenciasMatricula) throws DAOException, NegocioException, ArqException {
		int status = 0;
		
		if (IntegralizacoesHelper.isGraduando(d, mov)) 
			status = StatusDiscente.GRADUANDO;
		else if (IntegralizacoesHelper.isFormando(d, pendenciasMatricula, mov))
			status = StatusDiscente.FORMANDO;
		else
			status = StatusDiscente.ATIVO;
		return status;
	}

}
