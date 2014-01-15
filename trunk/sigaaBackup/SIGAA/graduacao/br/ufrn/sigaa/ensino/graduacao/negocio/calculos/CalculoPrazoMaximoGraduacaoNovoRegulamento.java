/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Implementa��o do c�lculo do prazo m�ximo para o novo regulamento da gradu��o.
 * <br>
 * Veja RESOLU��O No 227/2009-CONSEPE, de 03 de dezembro de 2009
 * 
 * @author Henrique Andr�
 *
 */
public class CalculoPrazoMaximoGraduacaoNovoRegulamento implements CalculoPrazoMaximo<Integer> {

	@Override
	public Integer calcular(DiscenteAdapter discente, Movimento mov) throws ArqException {
		
		DiscenteGraduacao dg = (DiscenteGraduacao) discente;
		DiscenteDao dao = DAOFactory.getInstance().getDAOMov(DiscenteDao.class, mov);
		MovimentacaoAlunoDao movDao = DAOFactory.getInstance().getDAOMov(MovimentacaoAlunoDao.class, mov);
		
		try {
			int incr = 0;
			if (dg!= null && dg.isRegular()) {

				// diminui do semestre atual
				if (dg.getCurriculo() != null && dg.getCurriculo().getSemestreConclusaoMaximo() != null)
					incr = dg.getCurriculo().getSemestreConclusaoMaximo() - 1;

				// al�m do prazo m�ximo do curr�culo, deve ser somado o n�mero de trancamento de programas
				Collection<MovimentacaoAluno> trancamentos = dao.findTrancamentosByDiscente(dg, true);
				if ( !isEmpty(trancamentos) )
					incr += trancamentos.size();

				// e incrementa tamb�m o n�mero de prorroga��es administrativas e judiciais
				Collection<MovimentacaoAluno> prorrogacoes = dao.findProrrogacoesByDiscente(dg, TipoMovimentacaoAluno.PRORROGACAO_ADMINISTRATIVA, TipoMovimentacaoAluno.PRORROGACAO_JUDICIAL);
				if ( !isEmpty(prorrogacoes) ) {
					for (MovimentacaoAluno prorrog : prorrogacoes) {
						incr += prorrog.getValorMovimentacao();
					}
				}
				
				// e decrementa tamb�m o n�mero de antecipa��es administrativas e judiciais
				int antecipacoes = movDao.countAntecipacoesByDiscente(dg);
				if ( antecipacoes > 0 ) {
					incr -= antecipacoes;
				}

				// para alunos a quem seja atribu�do um perfil inicial diferente de 0 (zero),
				// o n�mero de n�veis adicionais ser� descontado do n�mero de per�odos m�ximo para conclus�o do curso.
				if (dg.getPerfilInicial() != null && dg.getPerfilInicial() > 0)
					incr -= dg.getPerfilInicial();
				
			} else {
				// se especial
				incr = ParametrosGestoraAcademicaHelper.getParametros(discente).getMaxDisciplinasAlunoEspecial();
			}

			int prazoMaximo = DiscenteHelper.somaSemestres( discente.getAnoIngresso() , discente.getPeriodoIngresso(), incr);
			
			// TODO: N�o � responsabilidade da implementa��o persistir a informa��o
			if (discente.getPrazoConclusao() == null || prazoMaximo != discente.getPrazoConclusao() && discente.getId()!=0) {
				dao.updateField(Discente.class, discente.getId(), "prazoConclusao", prazoMaximo);
			}
			
			return prazoMaximo;
		} finally {
			dao.close();
			movDao.close();
		}
	}
	
	public char getNivel() {
		return NivelEnsino.GRADUACAO;
	}

}
