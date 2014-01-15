/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on '23/08/2010'
 *
 */

package br.ufrn.sigaa.ensino.graduacao.negocio.calculos;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MovimentacaoAlunoDao;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Implementação do cálculo do prazo máximo, para discentes com ano de entrada anterior a RESOLUÇÃO No 227/2009-CONSEPE, de 03 de dezembro de 2009
 * 
 * @author Henrique André
 *
 */
public class CalculoPrazoMaximoGraduacaoAntigo implements CalculoPrazoMaximo<Integer> {

	@Override
	public Integer calcular(DiscenteAdapter discente, Movimento mov) throws ArqException {
		DiscenteDao dao = DAOFactory.getInstance().getDAOMov(DiscenteDao.class, mov);
		MovimentacaoAlunoDao movDao = DAOFactory.getInstance().getDAOMov(MovimentacaoAlunoDao.class, mov);
		try {
			int incr = 0;
			if (discente.isRegular()) {

				// diminui do semestre atual
				if (discente.getCurriculo() != null && discente.getCurriculo().getSemestreConclusaoMaximo() != null)
					incr = discente.getCurriculo().getSemestreConclusaoMaximo() - 1;

				// além do prazo máximo do currículo, deve ser somado o número de trancamento de programas
				Collection<MovimentacaoAluno> trancamentos = dao.findTrancamentosByDiscente(discente, true);
				if ( !isEmpty(trancamentos) )
					incr += trancamentos.size();

				// e incrementa também o número de prorrogações administrativas e judiciais
				Collection<MovimentacaoAluno> prorrogacoes = dao.findProrrogacoesByDiscente(discente, TipoMovimentacaoAluno.PRORROGACAO_ADMINISTRATIVA, TipoMovimentacaoAluno.PRORROGACAO_JUDICIAL);
				if ( !isEmpty( prorrogacoes ) ) {
					for (MovimentacaoAluno prorrog : prorrogacoes) {
						incr += prorrog.getValorMovimentacao();
					}
				}
				
				// e decrementa também o número de antecipações administrativas e judiciais
				int antecipacoes = movDao.countAntecipacoesByDiscente(discente);
				if ( antecipacoes > 0 ) {
					incr -= antecipacoes;
				}
			} else {
				// se especial
				incr = ParametrosGestoraAcademicaHelper.getParametros(discente).getMaxDisciplinasAlunoEspecial();
			}

			int prazoMaximo = DiscenteHelper.somaSemestres( discente.getAnoIngresso() , discente.getPeriodoIngresso(), incr);
			
			// TODO: Não é responsabilidade da implementação persistir a informação
			if (discente.getPrazoConclusao() == null || prazoMaximo != discente.getPrazoConclusao()) {
				dao.updateField(Discente.class, discente.getId(), "prazoConclusao", prazoMaximo);
			}
			
			return prazoMaximo;
		} finally {
			dao.close();
			movDao.close();
		}
	}

	@Override
	public char getNivel() {
		return NivelEnsino.GRADUACAO;
	}

}
