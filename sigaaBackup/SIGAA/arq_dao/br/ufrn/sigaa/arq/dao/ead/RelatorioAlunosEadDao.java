/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 27/07/2009
 *
 */
package br.ufrn.sigaa.arq.dao.ead;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;

/**
 * Dao que retorna os dados formatados para serem exibidos no relatório de notas da EAD.
 * 
 * @author Fred_Castro
 *
 */
public class RelatorioAlunosEadDao extends GenericSigaaDAO {

	/**
	 * Retorna todos os cursos de EAD.
	 * 
	 * @return
	 * @throws ArqException
	 */
	public List<Curso> findAllCursosEad() throws ArqException{

		String hql = "select distinct pc.curso from PoloCurso pc";

		Query q = getSession().createQuery(hql);

		@SuppressWarnings("unchecked")
		List<Curso> lista = q.list();
		return lista;
	}

	/**
	 * Gera o relatório de alunos de acordo com os filtros passados.
	 * 
	 * @param idCurso
	 * @param ano
	 * @param periodo
	 * @param turmaAberta
	 * @return
	 * @throws HibernateException
	 * @throws ArqException
	 */
	public List<String[]> geraRelatorio(int idCurso, int ano, int periodo , boolean turmaAberta) throws HibernateException, ArqException {

		if (idCurso <= 0)
			return geraRelatorioGeral(ano, periodo, turmaAberta);

		String situacaoTurma = "";
		if ( turmaAberta )
			situacaoTurma = " where t.id_situacao_turma = " + SituacaoTurma.ABERTA + " or t.id_situacao_turma = " + SituacaoTurma.A_DEFINIR_DOCENTE + " ";
			
		String sql = "select " + 
		"cd.codigo, cd.nome, t.codigo as turma,s.id_situacao_turma, s.descricao as situacao, m.nome || '/' || u.sigla as polo, " + 
		"(select count(id_matricula_componente) from ensino.matricula_componente mc2 join discente d on mc2.id_discente = d.id_discente and d.id_curso = " + idCurso + " and mc2.id_turma = t.id_turma and mc2.id_situacao_matricula in ("+getSituacoesMatriculadas()+")) as matriculados, " + 
		"(select count(id_matricula_componente) from ensino.matricula_componente mc2 join discente d on mc2.id_discente = d.id_discente and d.id_curso = " + idCurso + " and mc2.id_turma = t.id_turma and mc2.id_situacao_matricula in ("+getSituacoesAprovadas()+")) as aprovados, " + 
		"(select count(id_matricula_componente) from ensino.matricula_componente mc2 join discente d on mc2.id_discente = d.id_discente and d.id_curso = " + idCurso + " and mc2.id_turma = t.id_turma and mc2.id_situacao_matricula in ("+getSituacoesReprovadas()+")) as reprovados, " + 
		"(select count(id_matricula_componente) from ensino.matricula_componente mc2 join discente d on mc2.id_discente = d.id_discente and d.id_curso = " + idCurso + " and mc2.id_turma = t.id_turma and mc2.id_situacao_matricula in ("+getSituacoesTrancadas()+")) as trancados " + 

		"from ensino.turma t " + 
		"join ensino.componente_curricular_detalhes cd on cd.id_componente = t.id_disciplina " + 
		"join ensino.situacao_turma s on s.id_situacao_turma = t.id_situacao_turma " + 
		"join ead.polo p on t.id_polo = p.id_polo " + 
		"join comum.municipio m on m.id_municipio = p.id_cidade " + 
		"join comum.unidade_federativa u on u.id_unidade_federativa = m.id_unidade_federativa " + 
		"and id_disciplina in  " + 
		"( " + 
		"select cc.id_disciplina " + 
		"from ensino.componente_curricular cc " + 
		"join graduacao.curriculo_componente cuc on cuc.id_componente_curricular = cc.id_disciplina " + 
		"join graduacao.curriculo cu on cuc.id_curriculo = cu.id_curriculo " + 
		"join curso c on c.id_curso = cu.id_curso and c.id_curso = " + idCurso + " " + 
		"join ead.polo_curso pc on c.id_curso = pc.id_curso " + 
		"group by cc.id_disciplina " + 
		") " + 

		" and t.ano = " + ano + " " +
		" and t.periodo = " + periodo + " " +
		situacaoTurma +

		"group by t.id_turma, cd.codigo,cd.nome,t.codigo,t.ano,t.periodo,m.nome, u.sigla,t.capacidade_aluno,s.id_situacao_turma, s.descricao " + 
		"order by codigo, turma";

		@SuppressWarnings("unchecked")
		List<String[]> lista = getSession().createSQLQuery(sql).list();
		return lista;
	}

	private String getSituacoesTrancadas() {

		SituacaoMatricula s = SituacaoMatricula.TRANCADO;

		return ""+s.getId();
	}

	private String getSituacoesAprovadas() {

		List <SituacaoMatricula> ss = (List<SituacaoMatricula>) SituacaoMatricula.getSituacoesPositivas();

		String rs = "";

		for (SituacaoMatricula s : ss)
			rs = rs.equals("") ? "" + s.getId() : rs + "," + s.getId();

			return rs;
	}

	private String getSituacoesReprovadas() {

		List <SituacaoMatricula> ss = (List<SituacaoMatricula>) SituacaoMatricula.getSituacoesReprovadas();

		String rs = "";

		for (SituacaoMatricula s : ss)
			rs = rs.equals("") ? "" + s.getId() : rs + "," + s.getId();

			return rs;
	}

	private String getSituacoesMatriculadas() {

		String rs = "";

		String aux = getSituacoesAprovadas();
		rs += !aux.equals("") ? aux : "";

		aux = getSituacoesReprovadas();
		rs += !aux.equals("") && !rs.equals("") ? rs + "," + aux : aux;

		aux = getSituacoesTrancadas();
		rs += !aux.equals("") && !rs.equals("") ? rs + "," + aux : aux;

		return rs;
	}

	/**
	 * Gera o relatório de alunos para todos os cursos.
	 * 
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws HibernateException
	 * @throws ArqException
	 */
	private List<String[]> geraRelatorioGeral(int ano, int periodo, boolean turmaAberta) throws HibernateException, ArqException {

		List <Curso> cs = findAllCursosEad();
	
		String cursos = "";

		for (Curso c : cs)
			cursos += cursos.equals("") ? c.getId() : ","+c.getId();

			String situacaoTurma = "";
			if (turmaAberta)
				situacaoTurma = " and (t.id_situacao_turma = " + SituacaoTurma.ABERTA + " or t.id_situacao_turma = " + SituacaoTurma.A_DEFINIR_DOCENTE + ") ";
			
			String sql = "select " + 
			"cd.codigo, cd.nome, t.codigo as turma, s.id_situacao_turma, s.descricao as situacao, m.nome || '/' || u.sigla as polo, " + 
			"(select count(id_matricula_componente) from ensino.matricula_componente mc2 join discente d on mc2.id_discente = d.id_discente and d.id_curso in (" + cursos + ") and mc2.id_turma = t.id_turma and mc2.id_situacao_matricula in ("+getSituacoesMatriculadas()+")) as matriculados, " +
			"(select count(id_matricula_componente) from ensino.matricula_componente mc2 join discente d on mc2.id_discente = d.id_discente and d.id_curso in (" + cursos + ") and mc2.id_turma = t.id_turma and mc2.id_situacao_matricula in ("+getSituacoesAprovadas()+")) as aprovados, " + 
			"(select count(id_matricula_componente) from ensino.matricula_componente mc2 join discente d on mc2.id_discente = d.id_discente and d.id_curso in (" + cursos + ") and mc2.id_turma = t.id_turma and mc2.id_situacao_matricula in ("+getSituacoesReprovadas()+")) as reprovados, " + 
			"(select count(id_matricula_componente) from ensino.matricula_componente mc2 join discente d on mc2.id_discente = d.id_discente and d.id_curso in (" + cursos + ") and mc2.id_turma = t.id_turma and mc2.id_situacao_matricula in ("+getSituacoesTrancadas()+")) as trancados " + 

			"from ensino.turma t " + 
			"join ensino.componente_curricular_detalhes cd on cd.id_componente = t.id_disciplina " + 
			"join ensino.situacao_turma s on s.id_situacao_turma = t.id_situacao_turma " + 
			"join ead.polo p on t.id_polo = p.id_polo " + 
			"join comum.municipio m on m.id_municipio = p.id_cidade " + 
			"join comum.unidade_federativa u on u.id_unidade_federativa = m.id_unidade_federativa " + 
			"and id_disciplina in  " + 
			"( " + 
			"select cc.id_disciplina " + 
			"from ensino.componente_curricular cc " + 
			"join graduacao.curriculo_componente cuc on cuc.id_componente_curricular = cc.id_disciplina " + 
			"join graduacao.curriculo cu on cuc.id_curriculo = cu.id_curriculo " + 
			"join curso c on c.id_curso = cu.id_curso and c.id_curso in (" + cursos + ") " + 
			"join ead.polo_curso pc on c.id_curso = pc.id_curso " + 
			"group by cc.id_disciplina " + 
			") " + 

			" and t.ano = " + ano + " " +
			" and t.periodo = " + periodo + " " +
			situacaoTurma +
			"group by t.id_turma, cd.codigo,cd.nome,t.codigo,t.ano,t.periodo,m.nome, u.sigla,t.capacidade_aluno,s.id_situacao_turma, s.descricao " + 
			"order by codigo, turma";

			@SuppressWarnings("unchecked")
			List<String[]> lista = getSession().createSQLQuery(sql).list();
			return lista;
	}
}
