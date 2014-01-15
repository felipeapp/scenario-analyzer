/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 12/12/2006
 *
 */
package br.ufrn.sigaa.arq.dao.ensino.infantil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.infantil.dominio.DiscenteInfantil;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO com consultas específicas sobre os alunos do ensino infantil.
 * 
 * @author Leonardo Campos
 *
 */
public class DiscenteInfantilDao extends DiscenteDao {


	/**
	 * Busca os alunos ainda não matriculados de um determinado nível no ano informado.
	 */
	public Collection<DiscenteInfantil> findNaoMatriculados(int idComponente, int nivel, int ano) throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();
			hql.append(" SELECT di.id, di.discente.matricula, di.discente.pessoa.nome " );
			hql.append(" FROM DiscenteInfantil di inner join di.discente disc ");
			hql.append(" WHERE disc.status in " + UFRNUtils.gerarStringIn(new int[]{StatusDiscente.CADASTRADO, StatusDiscente.ATIVO}) );
			hql.append(" AND disc.periodoAtual = :nivel ");
			hql.append(" AND disc.id not in (SELECT mat.discente.id " +
					"FROM MatriculaComponente mat " +
					"WHERE mat.ano = :ano " +
					"AND mat.componente.id = :idComponente " +
					"AND mat.situacaoMatricula.id <> :excluida) " +
					"ORDER BY di.discente.pessoa.nome");
			Query q =  getSession().createQuery(hql.toString());
			q.setInteger("nivel", nivel);
			q.setInteger("ano", ano);
			q.setInteger("idComponente", idComponente);
			q.setInteger("excluida", SituacaoMatricula.EXCLUIDA.getId());
			
			@SuppressWarnings("unchecked")
			List<Object[]> lista = q.list();
			List<DiscenteInfantil> result = new ArrayList<DiscenteInfantil>();
			for (Object[] objects : lista) {
				DiscenteInfantil di = new DiscenteInfantil();
				di.setId( (Integer) objects[0] );
				di.setMatricula( (Long) objects[1] );
				di.setPessoa(new Pessoa());
				di.getPessoa().setNome( (String) objects[2]  );
				di.setSelecionado(false);
				result.add(di);
			}
			
			return result;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Busca os alunos por turma
	 * @param idTurma
	 * @param situacaoMatricula
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<DiscenteInfantil> findByTurma(int idTurma, SituacaoMatricula... situacaoMatricula) throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();
			hql.append("SELECT mat.discente FROM MatriculaComponente mat ");
			hql.append(" WHERE mat.discente.status = :statusAluno ");
			hql.append(" and mat.turma.id = :turma ");
			hql.append(" and mat.situacaoMatricula.id in "+ UFRNUtils.gerarStringIn(situacaoMatricula));
			Query q =  getSession().createQuery(hql.toString());
			q.setInteger("turma", idTurma);
			q.setInteger("statusAluno", StatusDiscente.ATIVO);
			return q.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	
	/**
	 * Busca os alunos (ativos e cadastrados) do Ensino Infantil pelo nome e data de nascimento.
	 * @param nome
	 * @param dataNascimento
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<DiscenteInfantil> findNomeDataNascimento(String nome, Date dataNascimento) throws DAOException{
		try {
			String hql = "FROM DiscenteInfantil WHERE discente.status in (:statusDiscente) and " +
					"discente.pessoa.nome = :nome and discente.pessoa.dataNascimento = :dataNascimento";
			Query q =  getSession().createQuery(hql);
			q.setString("nome", nome);
			q.setDate("dataNascimento", dataNascimento);
			List<Integer> situacao = new ArrayList<Integer>();
			situacao.add(StatusDiscente.ATIVO);
			situacao.add(StatusDiscente.CADASTRADO);
			q.setParameterList("statusDiscente", situacao);
			return q.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

}
