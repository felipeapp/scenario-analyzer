/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/08/2012
 *
 */
package br.ufrn.sigaa.arq.dao.ead;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * DAO para consulta de turmas EAD
 * @author Diego Jácome
 *
 */
public class TurmaEadDao extends GenericSigaaDAO {

	/**
	 * Consulta geral de turmas de ensino a distância.
	 * @param codigoComp
	 * @param nome
	 * @param nomeDocente
	 * @param situacao
	 * @param ano
	 * @param periodo
	 * @param polo
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	public ArrayList<Turma> findTurmaEad(String nomeDocente, String nomeComponente , String codigoComp, Integer ano, Integer periodo, List<Integer> idsPolo) throws DAOException, LimiteResultadosException {


		ArrayList<Turma> result = new ArrayList<Turma>();
		result.addAll(findTurmaEad(nomeDocente,nomeComponente,codigoComp,ano,periodo,idsPolo,false));

		// exclui as repetidas pois as turmas com docente externo vem na primeira consulta também com a coleção de docentes vazia
		Collection<Turma> comDocentesExternos = findTurmaEad(nomeDocente,nomeComponente,codigoComp,ano,periodo,idsPolo,true);
		for ( Turma t : comDocentesExternos ) {
			int existe = result.indexOf(t);
			if ( existe != -1 ) {
				Turma outra = result.get( result.indexOf(t) );
				outra.getDocentesTurmas().addAll(t.getDocentesTurmas());
			} else {
				result.add(t);
			}
		}
		return result;
	}
	
	/**
	 * Consulta geral de turmas de ensino a distância.
	 * @param codigoComp
	 * @param nome
	 * @param nomeDocente
	 * @param situacao
	 * @param ano
	 * @param periodo
	 * @param polo
	 * @param externos
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Turma> findTurmaEad(String nomeDocente, String nomeComponente , String codigoComp, Integer ano, Integer periodo, List<Integer> idsPolo, boolean externos) throws HibernateException, DAOException{
		
		StringBuffer hql = new StringBuffer();
				
		hql.append("select t from Turma t ");
		
		if (!externos)
		    hql.append(" left join t.docentesTurmas as dt " +
			   " left join dt.docente as d ");
		else
		    hql.append(" join t.docentesTurmas as dt " +
 		   " join dt.docenteExterno as d ");
			
		hql.append(	" left join t.polo p" +
				" left join p.cidade c" +
				" left join c.unidadeFederativa uf" +
				" where (t.polo.id is not null or t.distancia = trueValue())");

		hql.append(" AND t.disciplina.nivel = '" +NivelEnsino.GRADUACAO+ "' ");
		hql.append(" AND t.turmaAgrupadora.id is null " );
		hql.append(" AND t.situacaoTurma.id in " + gerarStringIn( new int[] {SituacaoTurma.A_DEFINIR_DOCENTE, SituacaoTurma.ABERTA} ));
		
		if (!ValidatorUtil.isEmpty(nomeDocente))
			hql.append(" AND (" + UFRNUtils.toAsciiUpperUTF8("d.pessoa.nomeAscii") + " like :nomeDocente)");
		if (!ValidatorUtil.isEmpty(codigoComp))
			hql.append(" AND t.disciplina.codigo = :codigoComp ");
		if (idsPolo!=null && !idsPolo.isEmpty())
			hql.append( " AND t.polo.id in " + UFRNUtils.gerarStringIn(idsPolo) );
		if (!ValidatorUtil.isEmpty(nomeComponente))
			hql.append(" AND "+ UFRNUtils.toAsciiUpperUTF8("t.disciplina.codigo.detalhes.nome") + "like :nomeComponente");
		hql.append(" AND t.ano = :ano " );
		hql.append(" AND t.periodo = :periodo " );
		hql.append(" AND t.situacaoTurma.id != " + SituacaoTurma.EXCLUIDA);
		hql.append(" order by t.ano desc, t.periodo desc, t.disciplina.detalhes.nome asc, t.codigo asc");
		
		Query q = getSession().createQuery(hql.toString());
		if (!ValidatorUtil.isEmpty(nomeDocente))
			q.setString("nomeDocente", "%" + StringUtils.toAscii(nomeDocente.toUpperCase()) + "%" );
		if (!ValidatorUtil.isEmpty(nomeComponente))
			q.setString("nomeComponente", "%" + StringUtils.toAscii(nomeComponente.toUpperCase()) + "%" );
		if (!ValidatorUtil.isEmpty(codigoComp))
			q.setString("codigoComp", codigoComp);
		q.setInteger("ano", ano );
		q.setInteger("periodo", periodo );
		
		ArrayList <Turma> turmas = (ArrayList<Turma>) q.list();
		
		findQtdMatriculados(turmas);
		
		return turmas;
	}

	/**
	 * Busca a quantidade de alunos matrículados numa turma. 
	 * @param turmas
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public void findQtdMatriculados(ArrayList<Turma> turmas)
			throws DAOException {
		// Contabilização de matrículas ativas
		if (!ValidatorUtil.isEmpty(turmas)){
			String countMatriculas = "select mc.turma.id , count(mc.id) from MatriculaComponente as mc where mc.turma.id in "+ UFRNUtils.gerarStringIn(turmas)+
				" and mc.situacaoMatricula.id in  "+UFRNUtils.gerarStringIn(SituacaoMatricula.getSituacoesAtivas())+" group by mc.turma.id ";
			
			Query count = getSession().createQuery(countMatriculas);
			ArrayList<Object[]> res = (ArrayList<Object[]>) count.list();
			for (Object[] linha : res){
				Integer i = 0;
				Turma t = new Turma();
				t.setId((Integer)linha[i++]);
				Integer index = turmas.indexOf(t);
				if (index!=-1)
					turmas.get(index).setQtdMatriculados(((Number)linha[i++]).longValue());
			}
		}
	}	
}
