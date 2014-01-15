/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 02/03/2010
 * Autor:     David Pereira
 */
package br.ufrn.sigaa.ensino.graduacao.negocio.calculos;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MovimentacaoAlunoDao;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.calculos.CalculosDiscenteChainNode;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Classe para atualizar o período atual, os prazos e o perfil
 * inicial de um discente de graduação.
 * 
 * @author David Pereira
 *
 */
public class AtualizarPrazosPerfilDiscente extends CalculosDiscenteChainNode<DiscenteGraduacao> {

	@Override
	public void processar(DiscenteGraduacao d, Movimento mov, boolean preProcessamento) throws ArqException, NegocioException {
		PerfilInicialFactory.getPerfilInicial(d).calcular(d, mov);
		atualizarPeriodoAtual(d, mov);
		CalculoPrazoMaximoFactory.getCalculoGraduacao(d).calcular(d, mov);
	}

	/**
	 * Atualiza o período atual do discente através de um cálculo com base no ano-período atual.
	 * 
	 * @param d
	 * @param mov
	 * @throws ArqException
	 */
	public void atualizarPeriodoAtual(DiscenteGraduacao d, Movimento mov) throws ArqException {
		DiscenteDao dao = getDAO(DiscenteDao.class, mov);
		MovimentacaoAlunoDao movDao = getDAO(MovimentacaoAlunoDao.class, mov);
		MatriculaComponenteDao matriculaDao = getDAO(MatriculaComponenteDao.class);
		
		int anoReferencia = 0;
		int periodoReferencia = 0;
		int periodoAtual = 0; 
		
		try {
			
			if (StatusDiscente.getStatusConcluinte().contains(d.getStatus())) {
				MatriculaComponente ultimaMatricula = matriculaDao.findAnoPeriodoUltimaMatriculaComponente(d.getId());
				anoReferencia = ultimaMatricula.getAno();
				periodoReferencia = ultimaMatricula.getPeriodo();
			} else {
				CalendarioAcademico calendario = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();
				anoReferencia = calendario.getAno();
				periodoReferencia = calendario.getPeriodo();
			}
			
			periodoAtual = dao.calculaPeriodoAtualDiscente(d, anoReferencia, periodoReferencia);
			dao.updateField(Discente.class, d.getId(), "periodoAtual", periodoAtual);
		} finally {
			dao.close();
			movDao.close();
			matriculaDao.close();
		}
	}
	
}
