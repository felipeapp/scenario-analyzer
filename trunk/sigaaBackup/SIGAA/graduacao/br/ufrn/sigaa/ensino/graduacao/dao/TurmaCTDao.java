/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 04/04/2012
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dao;

import java.sql.SQLException;
import java.util.ArrayList;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;

/**
 * DAO com consultas das informações sobre as turmas do CT.
 * 
 * @author Fred_Castro
 *
 */
public class TurmaCTDao extends GenericSigaaDAO {

	
	/**
	 * Retorna as informações dos alunos necessárias para a geração do arquivo de importação de acordo
	 * com o nível de ensino, ano e período letivo. 
	 *
	 * @param nivel
	 * @return
	 * @throws DAOException
	 * @throws SQLException
	 */
	public String findTurmasCTAbertasPorAnoEPeriodoEmCSV (int ano, int periodo) throws DAOException {
		
		String situacoesAbertas = UFRNUtils.gerarStringIn(SituacaoTurma.getSituacoesAbertas());
		String solicitacoesValidas = UFRNUtils.gerarStringIn(SolicitacaoMatricula.getSolicitacoesValidas());
		String situacoesMatriculasAtivas = UFRNUtils.gerarStringIn(SituacaoMatricula.getSituacoesAtivas());
		
		String sql =	"select u.nome as unidade, cc.codigo as codigo_componente, ccd.nome as nome_componente, ccd.ch_total as ch_componente, " +
						"t.codigo as turma, t.descricao_horario, t.local, st.descricao as situacao, " +
						"coalesce(p1.nome, p2.nome) as docente, " +
						"dt.ch_dedicada_periodo as ch_docente, " +
						"t.capacidade_aluno as capacidade, " +
						"(select count(sm.id_solicitacao_matricula) from graduacao.solicitacao_matricula sm where sm.id_turma = t.id_turma and sm.status in " + solicitacoesValidas + " and sm.anulado = false) as solicitacoes_matricula, " + 
						"(select count(id_matricula_componente) from ensino.matricula_componente mc where mc.id_turma = t.id_turma and mc.id_situacao_matricula in " + situacoesMatriculasAtivas + ") as total_matriculas " +
						"from comum.unidade u " +
						"join ensino.componente_curricular cc using(id_unidade) " +
						"join ensino.componente_curricular_detalhes ccd on (cc.id_detalhe = ccd.id_componente_detalhes) " +
						"join ensino.turma t using(id_disciplina) " +
						"join ensino.situacao_turma st using(id_situacao_turma) " +
						"left join ensino.docente_turma dt using(id_turma) " +
						"left join rh.servidor s on (s.id_servidor = dt.id_docente) " +
						"left join comum.pessoa p1 on (s.id_pessoa = p1.id_pessoa) " +
						"left join ensino.docente_externo de using (id_docente_externo) " +
						"left join comum.pessoa p2 on (de.id_pessoa = p2.id_pessoa) " +
						"where (u.id_gestora = 445 or u.id_gestora = 351 or u.id_unidade = 351 or cc.codigo ILIKE 'AGP%' or cc.codigo ILIKE 'ZOO%') " + // CT
						"and t.ano = " + ano + " and t.periodo = " + periodo + " " +
						"and t.id_situacao_turma in " + situacoesAbertas + " " + // turmas abertas
						"and cc.nivel = '" + NivelEnsino.GRADUACAO + "' " +
						"order by u.nome, cc.codigo, t.codigo, docente";
		
		@SuppressWarnings("unchecked")
		ArrayList <Object []> linhas = (ArrayList<Object[]>) getSession().createSQLQuery(sql).list();
		StringBuffer rs = new StringBuffer();
		
		rs.append("unidade;codigo_componente;nome_componente;ch_componente;turma;descricao_horario;local;situacao;docente;ch_docente;capacidade;solicitacoes_matricula;total_matriculas;");
		
		for (Object [] l : linhas){
			rs.append("\n");
			for (Object c : l){
				if (c != null){
					if (c instanceof String){
						c = ((String) c).replace(";", " ");
						c = ((String) c).replace("\n", " ");
						c = ((String) c).replace("\r", " ");
					}
					
					rs.append(c);
				}
				
				rs.append(";");
			}
		}
		
		return rs.toString();
	}
}
