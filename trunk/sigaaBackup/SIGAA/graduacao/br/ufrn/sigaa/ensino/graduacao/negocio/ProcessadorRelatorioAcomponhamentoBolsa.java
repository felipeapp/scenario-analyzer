/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.graduacao.AcompanhamentoBolsasDao;
import br.ufrn.sigaa.assistencia.relatorios.LinhaDadosIndiceAcademico;

/**
 * Processador responsável por realizar consultas e retornar dados para a
 * geração de relatórios de acompanhamento de bolsas
 * 
 * @author Édipo Elder F. Melo
 * 
 */
public class ProcessadorRelatorioAcomponhamentoBolsa extends
		AbstractProcessador {
	
	/** DAO utilizado para realizar as consultas. */
	private AcompanhamentoBolsasDao dao;
	
	/** Dados do relatório. */
	private List<Map<String, Object>> dadosRelatorio;
	
	/** Dados do relatório. */
	private List<LinhaDadosIndiceAcademico> dadosAcademicos;

	/**
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		dao = getDAO(AcompanhamentoBolsasDao.class, mov);
		MovimentoAcompanhamentoBolsas movBolsas = (MovimentoAcompanhamentoBolsas) mov;
		switch (movBolsas.getConsulta()) {
			case MovimentoAcompanhamentoBolsas.CONSULTA_CARGA_HORARIA_SEMESTRE_ATUAL:
				dadosRelatorio = consultaCargaHorariaSemestreAtual(mov); break;
			case MovimentoAcompanhamentoBolsas.CONSULTA_DESEMPENHO_ACADEMICO:
				dadosAcademicos = consultaDesempenhoAcademico(mov); dadosRelatorio = null; break;
			case MovimentoAcompanhamentoBolsas.CONSULTA_BOLSISTA_DUPLO_VINCULO:
				dadosRelatorio = consultaBolsistaDuploVinculo(mov); break;
			default:
				dadosRelatorio = null;
		}
		dao.close();
		return dadosRelatorio != null ? dadosRelatorio : dadosAcademicos;
	}

	/** Consulta alunos com vínculo empregatício ou beneficiário de outra bolsa, exceto alimentação e transporte, ou qualquer tipo de ajuda financeira proveniente de órgãos públicos ou privados;
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	private List<Map<String, Object>> consultaBolsistaDuploVinculo(Movimento mov) throws DAOException {
		return dao.relatorioBolsistaDuploOuVinculo();
	}

	/** Consulta alunos bolsistas com que tenham desempenho acadêmico satisfatório, em freqüência e aprovação em, no mínimo, 80% das atividades curriculares.
	 * @param mov
	 * @return
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	private List<LinhaDadosIndiceAcademico> consultaDesempenhoAcademico(Movimento mov) throws DAOException, NegocioException {
		MovimentoAcompanhamentoBolsas movBolsas = (MovimentoAcompanhamentoBolsas) mov;
		Integer anoConsolidacao = dao.findLastConsolidacaoRealizada();
		if ( anoConsolidacao == null ) {
			throw new NegocioException("Não há índice consolidado, é necessário realizar a consolidação dos índices primeiro.");
		}
		return dao.bolsistasDesempenhoAcademico(movBolsas.getAnoIngresso(), movBolsas.getPeriodoIngresso(), movBolsas.getAno(), 
				movBolsas.getPeriodo(), movBolsas.getIdTipoBolsa(), anoConsolidacao, movBolsas.getIdSituacaoBolsa());
	}

	/**
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
	}
	
	/** Consulta alunos regulares, bolsistas, da UFRN, matriculados com o percentual da carga horária prevista para o nível da estrutura curricular correspondente ao período atual do aluno.
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	private List<Map<String, Object>> consultaCargaHorariaSemestreAtual(Movimento mov) throws DAOException {
		MovimentoAcompanhamentoBolsas movBolsas = (MovimentoAcompanhamentoBolsas) mov;
		return dao.relatorioBolsistasCargaHoraria(movBolsas.getAno(), movBolsas.getPeriodo());
	}

}
