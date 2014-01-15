/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 22/01/2010
 *
 */
package br.ufrn.sigaa.arq.dao.projetos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.projetos.dominio.HistoricoSituacaoProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Dao para as consultas sobre o histórico das situações de projetos.
 * 
 * @author Igor Linnik
 *
 */
public class HistoricoSituacaoProjetoDao extends GenericSigaaDAO {
	
	/**
	 * Método usado para buscar informações sobre as possíveis mudanças de Situação dos projetos
	 * de pesquisa, monitoria, extensão e associados.  
	 * 
	 * @param idProjeto
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	
	public Collection<HistoricoSituacaoProjeto> relatorioHistoricoByIdProjeto(Integer idProjeto) throws HibernateException, DAOException {
		
		StringBuilder hql = new StringBuilder();
		hql.append(
				
				" select historico.data, situacao.descricao, pessoa.nome " +
				" from   HistoricoSituacaoProjeto as historico " +
				" inner  join historico.projeto proj " +
				" inner  join historico.situacaoProjeto situacao " +
				" left  join historico.registroEntrada as registro   " +
				" left  join registro.usuario usuario " +
				" left  join usuario.pessoa pessoa " + 
				" where  proj.id = :idProjeto " +
				" order  by historico.data asc " 
				
				
				);
		
		Query q = getSession().createQuery( hql.toString() );
		q.setInteger("idProjeto", idProjeto);
		
		@SuppressWarnings("unchecked")
		List<Object> lista = q.list();
		String temp = "";
		ArrayList<HistoricoSituacaoProjeto> result = new ArrayList<HistoricoSituacaoProjeto>();
		for (int a = 0; a < lista.size(); a++) {
			
			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);
			HistoricoSituacaoProjeto historico = new HistoricoSituacaoProjeto();
			if(a == 0 || !temp.equalsIgnoreCase((String) colunas[1])){
				//Evitando erros de NULLPOINTER
				historico.setProjeto(new Projeto());
				historico.setRegistroEntrada(new RegistroEntrada());
				historico.getRegistroEntrada().setUsuario(new UsuarioGeral());
				historico.getRegistroEntrada().getUsuario().setPessoa(new PessoaGeral());
				
				historico.setData((Date) colunas[col++]);
				historico.getProjeto().getSituacaoProjeto().setDescricao((String) colunas[col++]);
				historico.getRegistroEntrada().getUsuario().getPessoa().setNome((String) colunas[col++]);
				temp = historico.getProjeto().getSituacaoProjeto().getDescricao();
	
				result.add(historico);
			}
		}
		
		return result;		
		
	}
	
	/**
	 * Retorna a última situação ativa do projeto passado.
	 * A situação Removida não é retornada.
	 * Método utilizado para recuperar ações removidas pelo usuário.
	 * A ação retorna para a situação anterior a remoção. 
	 * 
	 * @param idProjeto
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public TipoSituacaoProjeto ultimaSituacaoHistorico(Integer idProjeto) throws HibernateException, DAOException{
		
		StringBuilder hql = new StringBuilder();
		hql.append(" select historico from HistoricoSituacaoProjeto as historico ");
		hql.append(" join historico.projeto as proj ");
		hql.append(" join historico.situacaoProjeto as situacao ");
		hql.append(" where ");
		hql.append(" proj.id = :idProjeto ");
		hql.append(" and situacao.id not in (:idsSituacaoRemovido) ");
		hql.append(" order  by  historico.data desc");
		
		Query q = getSession().createQuery( hql.toString() );
		q.setInteger("idProjeto", idProjeto);
		q.setParameterList("idsSituacaoRemovido", 
				new Integer[] {TipoSituacaoProjeto.EXCLUIDO, TipoSituacaoProjeto.EXTENSAO_REMOVIDO, 
								TipoSituacaoProjeto.PROJETO_BASE_REMOVIDO, TipoSituacaoProjeto.MON_REMOVIDO});
		HistoricoSituacaoProjeto historico = new HistoricoSituacaoProjeto();
		q.setMaxResults(1);
		historico = (HistoricoSituacaoProjeto) q.uniqueResult();
		
		return historico.getSituacaoProjeto();
	}
	
	/**
	 * Retorna a última situação do projeto passado.
	 * @param idProjeto
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public TipoSituacaoProjeto findUltimaSituacaoHistorico(Integer idProjeto) throws HibernateException, DAOException{
		
		String projecao = " h.id, h.situacaoProjeto.id, h.situacaoProjeto.descricao ";
		String hql = " select " + projecao + " from HistoricoSituacaoProjeto as h "
		+ " where h.projeto.id = :idProjeto order by h.data desc ";
		
		Query q = getSession().createQuery( hql.toString() );
		q.setInteger("idProjeto", idProjeto);
		q.setMaxResults(1);
		@SuppressWarnings("unchecked")
		Collection<HistoricoSituacaoProjeto> result = HibernateUtils.parseTo(q.list(), projecao, HistoricoSituacaoProjeto.class, "h");
		
		if (ValidatorUtil.isNotEmpty(result)) {
			return result.iterator().next().getSituacaoProjeto();
		} else {
			return null;
		}		
	}
	
	/**
	 * Método utilizado para recuperar a quantidade de registros de histórico
	 * 
	 * @param idProjeto
	 * @return
	 */
	public Integer quantidadeRegistrosHistorico(Integer idProjeto){
		return count("from projetos.historico_situacao_projeto where id_projeto = "+idProjeto+"");
	}
	

	/**
	 * Método utilizado para verificar se o projeto já passou por determinada situação.
	 * 
	 * @param idProjeto
	 * @param idTipoSituacao
	 * @return
	 */
	public boolean isSituacaoPresenteNoHistoricoProjeto(Integer idProjeto, Integer idTipoSituacao){
		return count("from projetos.historico_situacao_projeto where id_projeto = "+idProjeto+" and id_tipo_situacao_projeto = "+idTipoSituacao) > 0;
	}

	/**
	 * Responsável pela busca dos projetos que já entraram em andamento em algum momento, dentre os projetos
	 * informandos.
	 */
	public Collection<Integer> projetoJaEmExecucao( Collection<Projeto> projetos ) throws HibernateException, DAOException{
		if(projetos != null && projetos.size() > 0) {
			String hql = " select h.projeto.id from HistoricoSituacaoProjeto as h "
					+ " where h.projeto.id in " + UFRNUtils.gerarStringIn(projetos) +
					" and h.situacaoProjeto = " + TipoSituacaoProjeto.EM_ANDAMENTO;
			
			Query q = getSession().createQuery( hql.toString() );
			@SuppressWarnings("unchecked")
			Collection<Integer> result = q.list();
			return result;
		}
		return new ArrayList<Integer>();
	}

}