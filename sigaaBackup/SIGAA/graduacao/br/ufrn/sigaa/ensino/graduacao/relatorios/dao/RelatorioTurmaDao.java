package br.ufrn.sigaa.ensino.graduacao.relatorios.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;

/**
 * Dao utilizado para armazenar as consultas feitas nos relatórios de turma
 * 
 * @author Bernardo
 *
 */
public class RelatorioTurmaDao extends GenericSigaaDAO {

	/**
	 * Consulta para relacionar a forma como as turmas
	 * e suas vagas estão ocupadas.
	 * 
	 * @param nivel
	 * @param unidade
	 * @param situacao
	 * @param ano
	 * @param periodo
	 * @param cursos
	 * @param turmasEAD
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findOcupacaoTurmas(Character nivel, Unidade unidade, 
			Integer[] situacao, Integer ano, Integer periodo, Collection<Curso> cursos,
			ModalidadeEducacao modalidade,Curso curso) throws DAOException{
		
		try {
			StringBuilder projecao = new StringBuilder();
			
			List<SituacaoMatricula> situacoesOcupamVagas = new ArrayList<SituacaoMatricula>();
				situacoesOcupamVagas.addAll(SituacaoMatricula.getSituacoesReprovadas());
				situacoesOcupamVagas.add(SituacaoMatricula.MATRICULADO);
				situacoesOcupamVagas.add(SituacaoMatricula.APROVADO);
				situacoesOcupamVagas.add(SituacaoMatricula.TRANCADO);
				situacoesOcupamVagas.add(SituacaoMatricula.CANCELADO);
					
				projecao.append(" SELECT cc.id_disciplina,u.id_unidade,u.nome,u.sigla,ccd.codigo as codigoComponente,ccd.nome as componente, ");
				projecao.append(" t.codigo, t.descricao_horario, st.descricao, st.id_situacao_turma, t.capacidade_aluno,t.id_turma, ");
				projecao.append(" (SELECT CAST( (SELECT COUNT(id_situacao_matricula) FROM ensino.matricula_componente ");
				projecao.append("	where id_turma = t.id_turma AND id_situacao_matricula IN " + gerarStringIn(situacoesOcupamVagas) + ") AS integer)) as \"matriculados\", ");
				projecao.append(" (SELECT CAST( (SELECT COUNT(id_solicitacao_matricula) FROM graduacao.solicitacao_matricula WHERE id_turma = t.id_turma ");
				projecao.append("       AND anulado = falseValue() AND id_matricula_gerada IS NULL ");
				projecao.append("       AND status IN " + gerarStringIn(SolicitacaoMatricula.getStatusSolicitacoesPendentes()) ); 
				projecao.append("  )  AS integer)) AS solicitacoes_pendentes ");
				projecao.append(" FROM ensino.turma t ");
				projecao.append(" INNER JOIN ensino.situacao_turma st ON t.id_situacao_turma = st.id_situacao_turma ");
				projecao.append(" INNER JOIN ensino.componente_curricular cc ON t.id_disciplina = cc.id_disciplina ");
				projecao.append(" INNER JOIN ensino.componente_curricular_detalhes ccd ON ccd.id_componente_detalhes = cc.id_detalhe ");
				projecao.append(" INNER JOIN comum.unidade u ON u.id_unidade = cc.id_unidade ");
				
				if (nivel == NivelEnsino.GRADUACAO && !isEmpty(curso)) {
					projecao.append("  LEFT JOIN graduacao.reserva_curso rc on rc.id_turma = t.id_turma ");
					projecao.append("  LEFT JOIN  ensino.turma sub on sub.id_turma_agrupadora = t.id_turma_agrupadora ");					
				}
				projecao.append("  WHERE t.ano = " + ano +	" AND t.periodo = " + periodo );
				projecao.append("  AND t.id_situacao_turma NOT IN (" + gerarStringIn(SituacaoTurma.getSituacoesInvalidas()) + ")");
				projecao.append("  AND t.agrupadora = falseValue()  AND nivel = '" + nivel + "'");
		
			if (situacao != null) 
				projecao.append(" AND t.id_situacao_turma IN " + gerarStringIn(situacao) );
			
			if (curso != null){		
				projecao.append(" 	AND  ( ( rc.id_matriz_curricular IN ((select id_matriz_curricular FROM graduacao.matriz_curricular m  WHERE m.id_curso = " + curso.getId() +")) OR rc.id_curso = " + curso.getId() +" )" +
								"		OR " +
								//"		--casos de subturma. entre todas a subturma de uma agrupadora, somente uma terá reserva." +
								"	     (rc.id_reserva_curso is null " + //turmas sem reserva
								"	     AND t.id_turma_agrupadora is not null " +//-- e é uma subturma
								//"	     -- se existir alguma reserva para umas das subturmas, então a subturma deverá ser trazida." +
								"	     AND exists" +
								//"		-- verifica se existe alguma reserva para algumas das subturmas" +
								"		(select id_reserva_curso from graduacao.reserva_curso where id_turma in" +
								//"			-- todas as subturmas da agrupadora" +
								"			(select id_turma from ensino.turma where id_turma_agrupadora = id_turma_agrupadora)))) ");
			}
			
			if (!isEmpty(unidade) && unidade.getId()>0)	
				projecao.append(" AND cc.id_unidade = "+ unidade.getId());
						
			projecao.append(" GROUP BY u.nome,ccd.nome,u.sigla, u.id_unidade,ccd.codigo, t.codigo, t.descricao_horario, st.descricao,");
			projecao.append(" st.id_situacao_turma, t.capacidade_aluno,t.id_turma,cc.id_disciplina ");
			projecao.append(" ORDER BY u.nome,ccd.nome,u.sigla, u.id_unidade,ccd.codigo, t.codigo, t.descricao_horario, st.descricao,");
			projecao.append("  st.id_situacao_turma, t.capacidade_aluno,t.id_turma,cc.id_disciplina");
			
			HashMap<Integer, Integer> idTurmaPosicaoLista = new HashMap<Integer, Integer>();		
			
			Query q = getSession().createSQLQuery( projecao.toString() );
			
			@SuppressWarnings("unchecked")
			List< Object[] > lista = q.list();
			
			ArrayList<Turma> result = new ArrayList<Turma>();

			
			for (int a = 0; a < lista.size(); a++) {
				int col = 0;
				Object[] colunas = lista.get(a);
				
				Turma t = new Turma();
				t.setDisciplina(new ComponenteCurricular((Integer) colunas[col++]));
				
				Integer idUnidade = (Integer) colunas[col++];
				if (idUnidade != null){
					t.getDisciplina().getUnidade().setId(idUnidade);
					t.getDisciplina().getUnidade().setNome((String)colunas[col++]);
					t.getDisciplina().getUnidade().setSigla((String)colunas[col++]);
				}
				
				
				if (idTurmaPosicaoLista.containsKey(t.getId()))
					continue;

				t.getDisciplina().setCodigo((String) colunas[col++]);
				t.getDisciplina().setNome((String) colunas[col++]);
				t.setCodigo((String) colunas[col++]);
				t.setDescricaoHorario((String) colunas[col++]);
				t.setSituacaoTurma(new SituacaoTurma());
				t.getSituacaoTurma().setDescricao((String) colunas[col++]);
				t.getSituacaoTurma().setId((Integer) colunas[col++]);
				
				t.setCapacidadeAluno((Integer) colunas[col++]);
				t.setId((Integer) colunas[col++]);	
				

				long totalMatriculados = (Integer) colunas[col++];
				t.setQtdMatriculados(totalMatriculados);

				t.setQtdEspera( (Integer) colunas[col++] );
				
				result.add(t);
				idTurmaPosicaoLista.put(t.getId(), result.size()-1);				
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}
}
