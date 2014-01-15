/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 06/06/2007
 */
package br.ufrn.sigaa.arq.dao.ensino;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.relatorios.LinhaRelatorioOcupacaoTurma;

/**
 * Dao para gerar relatório de ocupação de turma.
 * @author leonardo
 *
 */
public class RelatorioOcupacaoTurmaDao extends GenericSigaaDAO {

	/**
	 * Consulta que gera o relatório de ocupação das turmas da disciplina passada como argumento.
	 * @param idDisciplina
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public Map<String, LinhaRelatorioOcupacaoTurma> findOcupacaoTurmasByComponente(int idDisciplina, int ano, int periodo) throws DAOException {
		
		Map<String, LinhaRelatorioOcupacaoTurma> relatorio = new TreeMap<String, LinhaRelatorioOcupacaoTurma>();
		
		StringBuilder hql = new StringBuilder();
		hql.append(" select t.disciplina.detalhes.nome, t.disciplina.detalhes.codigo, t.id, t.codigo, t.capacidadeAluno");
		hql.append(" from Turma t");
		hql.append(" where t.ano = :ano");
		hql.append(" and t.periodo = :periodo");
		hql.append(" and t.disciplina.id = :idDisciplina");
		hql.append(" order by t.disciplina.detalhes.nome asc");
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		q.setInteger("idDisciplina", idDisciplina);
		
		@SuppressWarnings("unchecked")
		List<Object[]> turmas = q.list();
		Iterator<Object[]> it = turmas.iterator();
		
		while(it.hasNext()){
			int col = 0;
			Object[] colunas = it.next();
			
			String nomeDisciplina = (String) colunas[col++];
			
			LinhaRelatorioOcupacaoTurma linha = relatorio.get(nomeDisciplina);
			if(linha == null){
				linha = new LinhaRelatorioOcupacaoTurma();
			}
			
			linha.setNomeDisciplina(nomeDisciplina);
			linha.setCodigoDisciplina((String) colunas[col++]);
			
			int idTurma = (Integer) colunas[col++];
			String codigoTurma = (String) colunas[col++];
			int capacidade = (Integer) colunas[col++];
//			int matriculados = (Integer) colunas[col++];
			
			Turma turma = new Turma(idTurma);
			turma.setCodigo(codigoTurma);
			turma.setCapacidadeAluno(capacidade);
			
			linha.getTurmas().put(codigoTurma, turma);
			
			relatorio.put(nomeDisciplina, linha);
		}
		
		return relatorio;
	}

}
