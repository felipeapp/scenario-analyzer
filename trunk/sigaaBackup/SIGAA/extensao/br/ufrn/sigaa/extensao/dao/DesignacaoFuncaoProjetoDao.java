package br.ufrn.sigaa.extensao.dao;

import java.util.Collection;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.DesignacaoFuncaoProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;

/**
 * Dao respons�vel pelas consultas nas designa��es dos membros das a��es de extens�o 
 *  
 * @author guerethes
 */
public class DesignacaoFuncaoProjetoDao extends GenericSigaaDAO {

	/** Carrega um projeto, informando uma a��o de extens�o. */
	public Projeto findProjetoLeve(int atividadeExtensao) throws DAOException {
		String projecao = " ae.projeto.id, ae.projeto.titulo, ae.projeto.dataInicio, ae.projeto.dataFim, " +
						  " ae.projeto.coordenador.id, ae.projeto.coordenador.pessoa.id ";
		String hql = " select " + projecao +
					 " from AtividadeExtensao ae " +
					 " inner join ae.projeto pro " +
					 " where ae.ativo = trueValue() " +
					 " and pro.ativo = trueValue() " +
					 " and ae.id = :idAtividadeExtensao ";

		Query query = getSession().createQuery(hql);
		query.setInteger( "idAtividadeExtensao", atividadeExtensao );
		@SuppressWarnings("unchecked")
		Collection<AtividadeExtensao> lista = HibernateUtils.parseTo(query.list(), projecao, AtividadeExtensao.class, "ae");
		if (!ValidatorUtil.isEmpty(lista)) {
			return lista.iterator().next().getProjeto();
		} else { 
			return null;
		}
	}
	
	/** Carrega todas as designa��es do membro no projeto. */
	@SuppressWarnings("unchecked")
	public Collection<DesignacaoFuncaoProjeto> findDesignacoesMembroProjeto(int idMembroProjeto, int idProjeto) throws DAOException {
		String projecao = " dfp.membroProjeto.id, dfp.tipoDesignacao.id, " +
						  " dfp.tipoDesignacao.denominacao, dfp.membroProjeto.pessoa.nome ";
		String hql = " select " + projecao +
					 " from DesignacaoFuncaoProjeto dfp " +
					 " join dfp.membroProjeto mp " +
					 " join mp.pessoa p " +
					 " where dfp.ativo = trueValue() " +
					 " and dfp.membroProjeto.id = :idMembroProjeto " +
					 " and dfp.projeto.id = :idProjeto ";

		Query query = getSession().createQuery(hql);
		query.setInteger( "idMembroProjeto", idMembroProjeto );
		query.setInteger( "idProjeto", idProjeto );
		return HibernateUtils.parseTo(query.list(), projecao, DesignacaoFuncaoProjeto.class, "dfp");
	}
	
	/** Retornar um boolean informando se a pessoa apresentar a designa��o para o projeto informado */
	public boolean findDesignacaoMembroProjeto( int idProjeto, int idPessoa, int idDesignacao, boolean apenasCoordenador ){
		String sql = "SELECT count(*)" +
				" FROM projetos.projeto proj";
		
			sql += " JOIN projetos.membro_projeto mpcoord on ( proj.id_coordenador = mpcoord.id_membro_projeto )";

		if ( !apenasCoordenador ) {
			sql += " LEFT JOIN extensao.designacao_funcao_projeto dfp on ( proj.id_projeto = dfp.id_projeto" +
				   " 	and dfp.id_tipo_designacao_funcao_projeto = "+idDesignacao+" and dfp.ativo = trueValue() )" +
				   " LEFT JOIN projetos.membro_projeto mpdesig on ( dfp.id_membro_projeto = mpdesig.id_membro_projeto )";
		}
		
		sql += " WHERE 1=1 ";
		
		if ( idProjeto > 0 )
			sql += "AND proj.id_projeto = " + idProjeto + "";
	   
		if ( apenasCoordenador )
			sql += " AND mpcoord.id_pessoa = " +idPessoa;
		else
			sql += " AND ( mpcoord.id_pessoa = "+idPessoa+" or mpdesig.id_pessoa = "+idPessoa+" )";
		
		return getJdbcTemplate().queryForLong(sql, new Object[] {}) > 0;
	}
	
	/** Retorna o projeto no qual o servidor apresenta a designa��o informada */
	public Collection<Integer> findDesignacoesByServidor( int idServidor, boolean returnProjetos, Integer... idDesignacao ) throws HibernateException, DAOException{
		String sql = returnProjetos ? "SELECT proj.id_projeto" : "SELECT a.id_atividade"; 
				  sql += " FROM projetos.projeto proj" +
					 " JOIN extensao.atividade a using ( id_projeto ) " +
					 " JOIN projetos.membro_projeto mpcoord on ( proj.id_coordenador = mpcoord.id_membro_projeto and mpcoord.ativo = trueValue() )" +
				     " LEFT JOIN extensao.designacao_funcao_projeto dfp on ( proj.id_projeto = dfp.id_projeto" +
				     " 	and dfp.id_tipo_designacao_funcao_projeto in ( :idDesignacao ) and dfp.ativo = trueValue() )" +
				     " LEFT JOIN projetos.membro_projeto mpdesig on ( dfp.id_membro_projeto = mpdesig.id_membro_projeto )" +
				     " WHERE ( mpcoord.id_servidor = :idPessoa or mpdesig.id_servidor = :idPessoa)";
		
		Query query = getSession().createSQLQuery(sql);
		query.setParameterList( "idDesignacao", idDesignacao);
		query.setInteger( "idPessoa", idServidor);
		return query.list();
	}
	
}