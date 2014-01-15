/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 29/04/2011
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.ReservaCurso;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.negocio.TransferenciaTurmasComparator;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.processamento.dominio.MatriculaEmProcessamento;

/**
 * Dao para realizar operações referentes às transferencias de alunos entre turmas.
 * 
 * @author bernardo
 *
 */
public class TransferenciaTurmaDao extends GenericSigaaDAO {
	
	/**
	 * Select otimizado para trazer o id, a {@link SituacaoTurma}, o tipo e o {@link ComponenteCurricular} das {@link Turma} com ids passados.
	 * Utilizado na transferência de aluno entre turmas.
	 *
	 * @param ids
	 * @return
	 */
	public List<Turma> findTurmasTransferenciaByIds(Object[] ids) {

		String sql = "select t.id_turma, t.id_situacao_turma, t.tipo, t.id_disciplina, t.codigo, cc.codigo as codigo_cc, ccd.nome from ensino.turma t " +
						"inner join ensino.componente_curricular cc on (t.id_disciplina = cc.id_disciplina) " +
						"inner join ensino.componente_curricular_detalhes ccd on (ccd.id_componente_detalhes = cc.id_detalhe) " +
				" where id_turma in " + gerarStringIn(ids) +
				" order by t.ano desc, t.periodo desc, ccd.nome";

		@SuppressWarnings("unchecked")
		List<Turma> lista = getJdbcTemplate().query(sql, new RowMapper(){

			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Turma turma = new Turma();
				turma.setId(rs.getInt("id_turma"));
				turma.setSituacaoTurma(new SituacaoTurma());
				turma.getSituacaoTurma().setId(rs.getInt("id_situacao_turma"));
				turma.setTipo(rs.getInt("tipo"));
				turma.setCodigo(rs.getString("codigo"));
				turma.setDisciplina(new ComponenteCurricular());
				turma.getDisciplina().setId(rs.getInt("id_disciplina"));
				turma.getDisciplina().setCodigo(rs.getString("codigo_cc"));
				turma.getDisciplina().setDetalhes(new ComponenteDetalhes());
				turma.getDisciplina().getDetalhes().setNome(rs.getString("nome"));
				return turma;
			}

		});
		return lista;
	}
	
	public String getSqlMatriculasTurma(Turma turma) {
		return "select distinct d.id_discente, mc.id_matricula_componente, " 
			+ "(select valor from ensino.indice_academico_discente where id_indice_academico = 6 and id_discente = d.id_discente) as iea, "
			+ "d.id_matriz_curricular, d.matricula, d.curso, d.nome, "
			+ "coalesce((d.periodo_atual = cc.semestre_oferta), falseValue()) as nivelado, "
			+ "coalesce(d.possivel_formando, falseValue()) as formando, "
			+ "coalesce((d.periodo_atual > cc.semestre_oferta), falseValue()) as recuperacao, "
			+ "coalesce((d.periodo_atual < cc.semestre_oferta), falseValue()) as adiantado, "
			+ "cc.semestre_oferta is null as eletivo, "
			
			+ "(select distinct mc1.id_matricula_componente from ensino.matricula_componente mc1 "
			+ "join graduacao.confirmacao_matricula_ferias cmf on cmf.id_matricula_gerada = mc1.id_matricula_componente "
			+ "join graduacao.turma_solicitacao_turma tst on (tst.id_turma = mc1.id_turma) "
			+ "join graduacao.discentes_solicitacao ds on( ds.id_solicitacao_turma = tst.id_solicitacao ) "
			+ "join graduacao.solicitacao_ensino_individual sei on (sei.id_solicitacao_turma = tst.id_solicitacao) "
			+ " where mc1.ano = "+turma.getAno()+" and mc1.periodo = "+turma.getPeriodo()+" "
			+ " and id_situacao_matricula = 1 "
			+ "and confirmou = true "
			+ "and mc1.id_matricula_componente = mc.id_matricula_componente) is not null as vestibular "
			
			+ "from ensino.matricula_componente mc, "
			+ "(select d.id_discente, d.id_curriculo, d.id_forma_ingresso, d.matricula, c.nome as curso, p.nome, (select valor from ensino.indice_academico_discente where id_indice_academico = 6 and id_discente = d.id_discente) as iea, dg.possivel_formando, dg.id_matriz_curricular, greatest(("+turma.getAno()+" - d.ano_ingresso) * 2 "
			+ "+ ("+turma.getPeriodo()+" - d.periodo_ingresso + 1) "
			+ "- (select count(*) from ensino.movimentacao_aluno "
			+ "where ativo = trueValue() and id_tipo_movimentacao_aluno = " + TipoMovimentacaoAluno.TRANCAMENTO + " and id_discente = d.id_discente), 0) + dg.perfil_inicial as periodo_atual "
			+ "from discente d left outer join curso c on (c.id_curso = d.id_curso), graduacao.discente_graduacao dg, ensino.matricula_componente mc, ensino.turma t, comum.pessoa p "
			+ "where d.id_discente = dg.id_discente_graduacao and mc.id_discente = d.id_discente and mc.id_turma = t.id_turma and d.id_pessoa = p.id_pessoa "
			+ "and t.id_turma = "+turma.getId()+" and d.status in (1, 2, 8)) as d left join "
			+ "graduacao.curriculo c on (d.id_curriculo = c.id_curriculo) left join "
			
			+ "((select * from ensino.turma t, graduacao.curriculo_componente cc where t.id_disciplina = cc.id_componente_curricular and t.id_turma = "+turma.getId()+") union "
			+ "(select t.*, cuc.* from ensino.turma t, graduacao.curriculo_componente cuc, ensino.componente_curricular cc where t.id_disciplina = cc.id_disciplina and cc.id_bloco_subunidade = cuc.id_componente_curricular and t.id_turma = "+turma.getId()+")"
			+ ") as cc "
			
			+ "on (c.id_curriculo = cc.id_curriculo) "
			+ "where mc.id_turma = "+turma.getId()+" and mc.id_discente = d.id_discente and mc.id_situacao_matricula in " + gerarStringIn(SituacaoMatricula.getSituacoesMatriculadas()) + " "
			+ "order by eletivo desc, adiantado desc, recuperacao desc, formando desc, nivelado desc, vestibular desc, iea asc";
	}
	
	public String getSqlSolicitacoesTurma(Turma turma) {
		return "select distinct d.id_discente, sm.id_solicitacao_matricula, " 
			+ "(select valor from ensino.indice_academico_discente where id_indice_academico = 6 and id_discente = d.id_discente) as iea, "
			+ "d.id_matriz_curricular, d.matricula, d.curso, d.nome, "
			
			+ "coalesce((d.periodo_atual = cc.semestre_oferta), falseValue()) as nivelado, "
			+ "coalesce(d.possivel_formando, falseValue()) as formando, "
			+ "coalesce((d.periodo_atual > cc.semestre_oferta), falseValue()) as recuperacao, "
			+ "coalesce((d.periodo_atual < cc.semestre_oferta), falseValue()) as adiantado, "
			+ "cc.semestre_oferta is null as eletivo, "
			
			+ "(select distinct sm1.id_solicitacao_matricula from graduacao.Solicitacao_Matricula sm1 "
			+ "join graduacao.turma_solicitacao_turma tst on (tst.id_turma = sm1.id_turma) "
			+ "join graduacao.discentes_solicitacao ds on( ds.id_solicitacao_turma = tst.id_solicitacao ) "
			+ "join graduacao.solicitacao_ensino_individual sei on (sei.id_solicitacao_turma = tst.id_solicitacao) "
			+ " where sm1.ano = "+turma.getAno()+" and sm1.periodo = "+turma.getPeriodo()+" "
			+ " and status IN " +  gerarStringIn(new int[]{SolicitacaoMatricula.CADASTRADA, SolicitacaoMatricula.SUBMETIDA})
			+ " and sm1.id_solicitacao_matricula = sm.id_solicitacao_matricula) is not null as vestibular "
			
			+ "from graduacao.Solicitacao_Matricula sm, "
			+ "(select d.id_discente, d.id_curriculo, d.id_forma_ingresso, d.matricula, c.nome as curso, p.nome, " 
			+ "(select valor from ensino.indice_academico_discente where id_indice_academico = 6 and id_discente = d.id_discente) as iea, " 
			+ "dg.possivel_formando, dg.id_matriz_curricular, greatest(("+turma.getAno()+" - d.ano_ingresso) * 2 "
			+ "+ ("+turma.getPeriodo()+" - d.periodo_ingresso + 1) "
			+ "- (select count(*) from ensino.movimentacao_aluno "
			+ "   where ativo = trueValue() and id_tipo_movimentacao_aluno = " + TipoMovimentacaoAluno.TRANCAMENTO + " "
			+ "	  and id_discente = d.id_discente), 0) + dg.perfil_inicial as periodo_atual "
			
			+ "from discente d left outer join curso c on (c.id_curso = d.id_curso), graduacao.discente_graduacao dg, graduacao.solicitacao_matricula sm0, ensino.turma t, comum.pessoa p "
			+ "where d.id_discente = dg.id_discente_graduacao " 
			+ "and sm0.id_discente = d.id_discente " 
			+ "and sm0.id_turma = t.id_turma " 
			+ "and d.id_pessoa = p.id_pessoa "
			+ "and t.id_turma = "+turma.getId()+" and d.status in (1, 2, 8)) as d " 
			
			+ "left join graduacao.curriculo c on (d.id_curriculo = c.id_curriculo) "
			+ "left join ((select * from ensino.turma t, graduacao.curriculo_componente cc where t.id_disciplina = cc.id_componente_curricular and t.id_turma = "+turma.getId()+") union "
			+ "(select t.*, cuc.* from ensino.turma t, graduacao.curriculo_componente cuc, ensino.componente_curricular cc where t.id_disciplina = cc.id_disciplina and cc.id_bloco_subunidade = cuc.id_componente_curricular and t.id_turma = "+turma.getId()+")"
			+ ") as cc "
			
			+ "on (c.id_curriculo = cc.id_curriculo) "
			+ "where sm.id_turma = "+turma.getId()+ " "
			+ "and sm.id_discente = d.id_discente "
			+ "and sm.id_matricula_gerada is null "
			+ "and sm.anulado = falseValue() "
			+ "and sm.status IN "+ gerarStringIn(new int[]{SolicitacaoMatricula.SUBMETIDA, SolicitacaoMatricula.ORIENTADO, SolicitacaoMatricula.VISTA}) + " " 
			+ "order by eletivo desc, adiantado desc, recuperacao desc, formando desc, nivelado desc, vestibular desc, iea asc";
	}
		
	@SuppressWarnings("unchecked")
	public <T> Collection<T> findMatriculasEmOrdemParaTransferenciaPorPrioridade(Turma turmaOrigem, Map<MatrizCurricular, ReservaCurso> vagasPorReserva, final Class<T> classe) {
		String sql = classe.equals(MatriculaComponente.class) ? getSqlMatriculasTurma(turmaOrigem) : getSqlSolicitacoesTurma(turmaOrigem);
		
		List<MatriculaEmProcessamento> resultado = getJdbcTemplate().query(sql, new RowMapper() {
			public Object mapRow(ResultSet rs, int row) throws SQLException {
				MatriculaEmProcessamento matricula = new MatriculaEmProcessamento();

				boolean nivelado = rs.getBoolean("nivelado");
				boolean formando = rs.getBoolean("formando");
				boolean recuperacao = rs.getBoolean("recuperacao");
				boolean adiantado = rs.getBoolean("adiantado");
				boolean eletivo = rs.getBoolean("eletivo");
				boolean vestibular = rs.getBoolean("vestibular");

				int tipo = MatriculaEmProcessamento.OUTROS;
				if (nivelado) tipo = MatriculaEmProcessamento.NIVELADO;
				else if (formando) tipo = MatriculaEmProcessamento.FORMANDO;
				else if (recuperacao) tipo = MatriculaEmProcessamento.RECUPERACAO;
				else if (adiantado) tipo = MatriculaEmProcessamento.ADIANTADO;
				else if (eletivo) tipo = MatriculaEmProcessamento.ELETIVO;
				else if (vestibular) tipo = MatriculaEmProcessamento.VESTIBULAR;

				matricula.setIdMatrizCurricular(rs.getInt("id_matriz_curricular"));
				matricula.setIdDiscente(rs.getInt("id_discente"));
				
				if (classe.equals(MatriculaComponente.class)) 
					matricula.setIdMatriculaComponente(rs.getInt("id_matricula_componente"));
				else
					matricula.setIdMatriculaComponente(rs.getInt("id_solicitacao_matricula"));
				
				matricula.setTipo(tipo);
				matricula.setIndice(rs.getDouble("iea"));
				matricula.setMatricula(rs.getLong("matricula"));
				matricula.setNome(rs.getString("nome"));
				matricula.setCurso(rs.getString("curso"));
				
				return matricula;
			}
		});

		// Monta mapa de matrículas de acordo com a ordem de prioridade
		List<MatriculaEmProcessamento> matriculasTransferencia = new ArrayList<MatriculaEmProcessamento>();
		
		for (MatriculaEmProcessamento linha : resultado) {
			matriculasTransferencia.add(linha);
		}

		Collections.sort(matriculasTransferencia, new TransferenciaTurmasComparator());
		
		Collection<T> matriculas = new ArrayList<T>();
		Collection<T> solicitacoes = new ArrayList<T>();
	
		for ( MatriculaEmProcessamento mep : matriculasTransferencia ) {
			if (classe.equals(MatriculaComponente.class)) {
				MatriculaComponente mc = new MatriculaComponente();
				mc.setId(mep.getIdMatriculaComponente());
				mc.setDiscente(new Discente(mep.getIdDiscente(), mep.getMatricula(), mep.getNome()));
				matriculas.add((T) mc);
			} else {
				SolicitacaoMatricula sol = new SolicitacaoMatricula();
				sol.setId(mep.getIdMatriculaComponente());
				sol.setDiscente(new Discente(mep.getIdDiscente(), mep.getMatricula(), mep.getNome()));
				solicitacoes.add((T) sol);
			}
		}
		
		if (!isEmpty(matriculas))
			return matriculas;
		else
			return solicitacoes;
	}

}
