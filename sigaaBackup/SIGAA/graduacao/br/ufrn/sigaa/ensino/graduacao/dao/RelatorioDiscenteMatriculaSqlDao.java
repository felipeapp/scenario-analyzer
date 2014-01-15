/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 01/04/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.graduacao.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.util.List;
import java.util.Map;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.graduacao.AbstractRelatorioSqlDao;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * DAO que contém as consultas para os relatórios referentes a discentes e matrículas.
 * 
 * @author Rafael Gomes
 *
 */
public class RelatorioDiscenteMatriculaSqlDao extends AbstractRelatorioSqlDao{

	/**
	 * Realiza um busca pelos discentes de graduação ingressantes no ano e período informados, que não possuem registro 
	 * de solicitação de matrícula ou matrículas em espera para o período.
	 * 
	 * @param campus
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findAlunosIngressantesSemMatricula(int ano, int periodo) throws DAOException {
		
		StringBuilder sqlconsulta = new StringBuilder(		
				"SELECT distinct fi.descricao as forma_ingresso, d.matricula, d.ano_ingresso || '.' || " +
				"d.periodo_ingresso as ingresso, p.nome, sd.descricao as situacao, c.nome as curso, m.nome as municipio, " +
				"t.sigla as turno, h.nome as habilitacao, me.descricao as modalidade, mpo.nome as municipio_polo ");
		sqlconsulta.append(		
				" FROM discente d" +
				" INNER JOIN graduacao.discente_graduacao dg ON dg.id_discente_graduacao = d.id_discente" +
				" LEFT JOIN ead.polo po on (dg.id_polo = po.id_polo)" +
				" LEFT JOIN comum.municipio mpo on (mpo.id_municipio = po.id_cidade)" +
				" INNER JOIN comum.pessoa p ON d.id_pessoa = p.id_pessoa" +
				" INNER JOIN status_discente sd ON d.STATUS = sd.status" +
				" LEFT JOIN ensino.forma_ingresso fi USING(id_forma_ingresso)" +
				" LEFT JOIN graduacao.matriz_curricular mc USING(id_matriz_curricular)" +  
				" LEFT JOIN curso c ON c.id_curso = mc.id_curso" + 
				" LEFT JOIN ensino.turno t ON t.id_turno = mc.id_turno" + 
				" LEFT JOIN graduacao.habilitacao h ON h.id_habilitacao = mc.id_habilitacao" +
				" LEFT JOIN comum.modalidade_educacao me ON me.id_modalidade_educacao = c.id_modalidade_educacao" +  
				" LEFT JOIN comum.municipio m ON (m.id_municipio = c.id_municipio) "); 
		sqlconsulta.append(			
				" WHERE d.ano_ingresso = "+ano+
				" AND d.periodo_ingresso = "+periodo+
				" AND NOT EXISTS ( select id_discente from graduacao.solicitacao_matricula where id_discente = d.id_discente and ano = "+ano+
					" and periodo = "+periodo+" and status not in " + gerarStringIn( new int[] { SolicitacaoMatricula.NEGADA, 
					SolicitacaoMatricula.EXCLUIDA, SolicitacaoMatricula.NEGADA_OUTRO_PROGRAMA } ) + "and anulado = falseValue() ) " +
				" AND d.id_discente NOT IN ( select distinct id_discente from ensino.matricula_componente where ano = "+ano+
					" and periodo = "+periodo+" and id_situacao_matricula in "+ gerarStringIn( new int[] {SituacaoMatricula.EM_ESPERA.getId(), 
					SituacaoMatricula.MATRICULADO.getId()} ) + ")"+ 
				" AND d.nivel = '"+NivelEnsino.GRADUACAO+"'" +
				" AND d.status not in " +gerarStringIn( new int[] {StatusDiscente.CANCELADO, StatusDiscente.GRADUANDO, StatusDiscente.EXCLUIDO, StatusDiscente.PENDENTE_CADASTRO, StatusDiscente.PRE_CADASTRADO} )+
				" AND d.tipo = "+Discente.REGULAR);
		sqlconsulta.append(
				" GROUP BY fi.descricao, d.matricula, d.ano_ingresso, d.periodo_ingresso, p.nome, sd.descricao," +
				" c.nome, m.nome, t.sigla, h.nome, me.descricao, mpo.nome" +
				" ORDER BY fi.descricao, c.nome, m.nome, p.nome");
		
		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}
}
