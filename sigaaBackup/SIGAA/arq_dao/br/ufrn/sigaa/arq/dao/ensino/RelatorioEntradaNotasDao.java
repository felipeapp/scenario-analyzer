/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 01/10/2010
 *
 */
package br.ufrn.sigaa.arq.dao.ensino;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.hibernate.HibernateException;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.relatorios.LinhaRelatorioEntradaNotas;

/**
 * Dao para armazenar as consultas utilizadas para gerar o relatório de entrada de notas.
 * 
 * @author Leonardo Campos
 *
 */
public class RelatorioEntradaNotasDao extends GenericSigaaDAO {

	/**
	 * Busca turmas de ensino técnico por ano, período e módulo. 
	 * @param ano
	 * @param periodo
	 * @param idModulo
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<Turma> findTurmasTecnico(Integer ano, Integer periodo, Integer idModulo) throws HibernateException, DAOException {
		
		String sql = "select t.id_turma, t.ano, t.periodo, t.codigo as cod_turma, st.descricao as situacao, " +
		" cc.id_disciplina, cc.codigo as cod_disciplina, ccd.nome" +
		" from ensino.turma t" +
		" join ensino.componente_curricular cc on (cc.id_disciplina=t.id_disciplina)" +
		" join ensino.componente_curricular_detalhes ccd on (ccd.id_componente_detalhes = cc.id_detalhe)" +
		" join ensino.situacao_turma st on (st.id_situacao_turma=t.id_situacao_turma)" +
		" join tecnico.modulo_disciplina md on (md.id_disciplina=t.id_disciplina)" +
		" where t.ano=" +ano+
		" and t.periodo=" +periodo+
		" and md.id_modulo=" +idModulo+
		" and t.id_situacao_turma <> " +SituacaoTurma.EXCLUIDA+
		" group by t.id_turma, t.ano, t.periodo, t.codigo, st.descricao, cc.id_disciplina, cc.codigo, ccd.nome" +
		" order by t.codigo, cc.id_disciplina";
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = getSession().createSQLQuery(sql).list();
		List<Turma> lista = new ArrayList<Turma>();
		
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
			int col = 0;
			Object[] colunas = iterator.next();
			Turma turma = new Turma((Integer) colunas[col++]);
			turma.setAno((Integer) colunas[col++]);
			turma.setPeriodo((Integer) colunas[col++]);
			turma.setCodigo((String) colunas[col++]);
			turma.setSituacaoTurma(new SituacaoTurma());
			turma.getSituacaoTurma().setDescricao((String) colunas[col++]);
			turma.setDisciplina(new ComponenteCurricular((Integer) colunas[col++]));
			turma.getDisciplina().setCodigo((String) colunas[col++]);
			turma.getDisciplina().setDetalhes(new ComponenteDetalhes());
			turma.getDisciplina().getDetalhes().setNome((String) colunas[col++]);
			lista.add(turma);
		}	
		
		return lista;
	}
	
	/**
	 * Busca as informações do relatório de entrada de notas a partir de conjunto de turmas passado como argumento.
	 * @param mapaTurmas
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Set<LinhaRelatorioEntradaNotas> findRelatorioEntradaNotas(Map<Integer, Turma> mapaTurmas) throws HibernateException, DAOException {

		String sql = "select t.id_turma, count(distinct mc.id_matricula_componente) as matriculados," +
				" count(distinct mc.id_matricula_componente) - sum(case when n.unidade=1 and n.nota is not null then 1 else 0 end) as unidade1," +
				" count(distinct mc.id_matricula_componente) - sum(case when n.unidade=2 and n.nota is not null then 1 else 0 end) as unidade2," +
				" count(distinct mc.id_matricula_componente) - sum(case when n.unidade=3 and n.nota is not null then 1 else 0 end) as unidade3" +
				" from ensino.turma t" +
				" left join ensino.matricula_componente mc on (mc.id_turma=t.id_turma)" +
				" left join ensino.nota_unidade n on (n.id_matricula_componente=mc.id_matricula_componente)" +
				" where (n.id_nota_unidade is null or (n.id_nota_unidade is not null and n.ativo = trueValue())) and mc.id_situacao_matricula in " +UFRNUtils.gerarStringIn(SituacaoMatricula.getSituacoesMatriculadoOuConcluido())+
				" and t.id_turma in " +UFRNUtils.gerarStringIn(mapaTurmas.values())+
				" group by t.id_turma";

		@SuppressWarnings("unchecked")
		List<Object[]> list = getSession().createSQLQuery(sql).list();
		Set<LinhaRelatorioEntradaNotas> lista = new TreeSet<LinhaRelatorioEntradaNotas>();
		
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
			int col = 0;
			Object[] colunas = iterator.next();
			LinhaRelatorioEntradaNotas linha = new LinhaRelatorioEntradaNotas();
			Integer idTurma = (Integer) colunas[col++];
			linha.setTurma(mapaTurmas.get(idTurma));
			linha.setMatriculados((BigInteger) colunas[col++]);
			linha.setUnidade1((BigInteger) colunas[col++]);
			linha.setUnidade2((BigInteger) colunas[col++]);
			linha.setUnidade3((BigInteger) colunas[col++]);
			lista.add(linha);
		}	
			
		return lista;
	}
}
