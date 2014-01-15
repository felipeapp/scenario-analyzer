/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '09/04/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.monitoria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.monitoria.dominio.AutorizacaoReconsideracao;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.TipoProjetoEnsino;
import br.ufrn.sigaa.projetos.dominio.SolicitacaoReconsideracao;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;


/**
 * Dao com consultas para solicitações de reconsideração de avaliação de projetos de ensino.
 * @author ilueny
 *
 */ 
public class ReconsideracaoDao extends GenericSigaaDAO {

	/**
	 * Autorizações de reconsideração do projeto com a unidade informada.
	 * 
	 * @author ilueny
	 */
	public AutorizacaoReconsideracao autorizacoesParaProjetoDaUnidade(int idProjeto, int idUnidade) throws DAOException {

		String hql = "select ar from AutorizacaoReconsideracao ar inner join ar.projetoEnsino pm " +
		" where (pm.id = :idProjeto) and (pm.projeto.unidade.id = :idUnidade)";

		Query query = getSession().createQuery(hql);
		query.setInteger( "idProjeto", idProjeto );
		query.setInteger( "idUnidade", idUnidade );

		return (AutorizacaoReconsideracao)query.uniqueResult();
 		
	}

	/**
	 * Retorna todas as solicitações de reconsideração de projetos
	 * realizadas pelo coordenador do projeto.
	 * 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AutorizacaoReconsideracao> findAllAutorizacoesAtivas() throws DAOException {
	    String hql = " select ar.id, ar.dataAutorizacao, ar.dataSolicitacao, ar.autorizado, ar.ativo, pe.id, pe.tipoProjetoEnsino, p.id, p.ano, p.titulo " +
			" from AutorizacaoReconsideracao ar " +
			" inner join ar.projetoEnsino pe " +
			" inner join pe.projeto p where ar.ativo = trueValue() and p.ativo = trueValue()";
	    	Query query = getSession().createQuery(hql);
		List<Object> lista = query.list();

		ArrayList<AutorizacaoReconsideracao> result = new ArrayList<AutorizacaoReconsideracao>();
		for (int a = 0; a < lista.size(); a++) {
			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);
			AutorizacaoReconsideracao ar = new AutorizacaoReconsideracao();
			ar.setId((Integer) colunas[col++]);
			ar.setDataAutorizacao((Date) colunas[col++]);
			ar.setDataSolicitacao((Date) colunas[col++]);
			ar.setAutorizado((Boolean) colunas[col++]);
			ar.setAtivo((Boolean) colunas[col++]);
			ar.setProjetoEnsino(new ProjetoEnsino((Integer) colunas[col++]));
			ar.getProjetoEnsino().setTipoProjetoEnsino((TipoProjetoEnsino) colunas[col++]);
			ar.getProjetoEnsino().getProjeto().setId((Integer) colunas[col++]);
			ar.getProjetoEnsino().getProjeto().setAno((Integer) colunas[col++]);
			ar.getProjetoEnsino().getProjeto().setTitulo((String) colunas[col++]);
			result.add(ar);
		}
		return result;	
	}

	
	
	/**
	 * Retorna todos os projetos do Servidor logado ou do usuário que criou o projeto
	 * passíveis de pedido de reconsideração para requisitos formais.
	 *
	 * @param usuario Usuário logado
	 * @param coordenador true se coordenador
	 * @return
	 * @throws DAOException
	 *
	 * @author ilueny
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoEnsino> findProjetosReconsideraveis(int idServidor, int idUsuario) throws DAOException {

			String hql = " select distinct projeto from ProjetoEnsino projeto left join projeto.equipeDocentes equipeDocente" +
			" where " +
			"((equipeDocente.servidor.id = :idServidor and equipeDocente.coordenador = trueValue()) " +
					" or (projeto.projeto.registroEntrada.usuario.id = :idUsuario))" +
					
			" and (projeto.editalMonitoria.edital.fimSubmissao < :fimSubmissao)" +     /// prazo de edital para envio de propostas acabou!
			" and (projeto.editalMonitoria.dataFimReconsideracaoReqFormais >= :fimReconsideracaoReqFormais)" +     /// prazo de edital para envio de reconsideração por req. formais acabou!			
			" and ((projeto.projeto.situacaoProjeto.id = :idAberto)" +
					" or (projeto.projeto.situacaoProjeto.id = :idDepartamentos))";

			Query query = getSession().createQuery(hql);

			query.setInteger( "idServidor", idServidor);
			query.setInteger( "idUsuario", idUsuario);
			query.setDate("fimSubmissao", new Date());
			query.setDate("fimReconsideracaoReqFormais", new Date());			
			query.setInteger("idAberto", TipoSituacaoProjeto.MON_CADASTRO_EM_ANDAMENTO);
			query.setInteger("idDepartamentos", TipoSituacaoProjeto.MON_AGUARDANDO_AUTORIZACAO_DEPARTAMENTOS);

			return query.list();

	}
	
	

	/**
	 * Retorna todos os pedidos de reconsideração de avaliação do projeto. 
	 * 
	 * @param idProjeto
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<SolicitacaoReconsideracao> findReconsideracaoByProjeto(Integer idProjeto) throws DAOException{		
		Criteria c = getSession().createCriteria(SolicitacaoReconsideracao.class);
		c.createCriteria("projeto").add(Expression.eq("id", idProjeto));		
		return c.list();
	}
	
}