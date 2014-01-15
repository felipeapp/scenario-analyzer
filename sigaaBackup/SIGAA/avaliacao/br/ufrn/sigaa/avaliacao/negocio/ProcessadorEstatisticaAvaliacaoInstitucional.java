/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 01/07/2010
 *
 */
package br.ufrn.sigaa.avaliacao.negocio;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.avaliacao.AvaliacaoInstitucionalDao;
import br.ufrn.sigaa.avaliacao.dao.CalendarioAvaliacaoDao;
import br.ufrn.sigaa.avaliacao.dominio.CalendarioAvaliacao;
import br.ufrn.sigaa.avaliacao.dominio.EstatisticaAvaliacaoInstitucional;

/** Processador responsável por atualizar as estatísticas da Avaliação Institucional.
 * @author Édipo Elder F. Melo
 *
 */
public class ProcessadorEstatisticaAvaliacaoInstitucional extends AbstractProcessador {

	/** Processa e atualiza as estatísticas da Avaliação Institucional.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		AvaliacaoInstitucionalDao dao = getDAO(AvaliacaoInstitucionalDao.class, mov);
		CalendarioAvaliacaoDao calDao = getDAO(CalendarioAvaliacaoDao.class, mov);
		try {
			dao.getJdbcTemplate().update("delete from avaliacao.dados_estatisticos");
			Collection<CalendarioAvaliacao> calendarios = calDao.findAllPeriodoPreenchimentoAtivo(0, null);
			int id = 0;
			if (calendarios != null) {
				for (CalendarioAvaliacao calendario : calendarios) {
					// computa os totais
					int ano = calendario.getAno();
					int periodo = calendario.getPeriodo();
					int idFormulario = calendario.getFormulario().getId();
					int qtdAlunosAvaliacao = dao.findQtdAlunosAvaliacao(ano, periodo, idFormulario);
					int qtdDisciplinasAvaliadas = dao.findQtdDisciplinasAvaliadas(ano, periodo, idFormulario);
					int qtdTurmasAvaliadas = dao.findQtdTurmasAvaliadas(ano, periodo, idFormulario);
//					int qtdTurmasNaoAvaliadas = dao.findQtdTurmasNaoAvaliadas(ano, periodo, idFormulario);
					// remove os dados estatísticos anteriores
					// insere os novos dados
					if (qtdAlunosAvaliacao > 0)
						dao.create(new EstatisticaAvaliacaoInstitucional(id, ano, periodo, idFormulario, "Alunos que avaliaram", qtdAlunosAvaliacao, null));
					if (qtdDisciplinasAvaliadas > 0)
						dao.create(new EstatisticaAvaliacaoInstitucional(id, ano, periodo, idFormulario, "Disciplinas avaliadas", qtdDisciplinasAvaliadas, null));
					if (qtdTurmasAvaliadas > 0)
						dao.create(new EstatisticaAvaliacaoInstitucional(id, ano, periodo, idFormulario, "Turmas avaliadas", qtdTurmasAvaliadas, null));
//					dao.create(new EstatisticaAvaliacaoInstitucional(0, ano, periodo, idFormulario, "Turmas sem avaliação", qtdTurmasNaoAvaliadas, null));
				}
			}
		} finally {
			dao.close();
			calDao.close();
		}
		return null;
	}

	/** Valiada os dados a serem processados.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
	}

}
