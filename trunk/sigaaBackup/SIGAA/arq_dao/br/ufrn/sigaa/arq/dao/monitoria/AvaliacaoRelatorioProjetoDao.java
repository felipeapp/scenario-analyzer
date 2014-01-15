/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '25/06/2007'
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
import br.ufrn.sigaa.monitoria.dominio.AvaliacaoRelatorioProjeto;
import br.ufrn.sigaa.monitoria.dominio.RelatorioProjetoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.MembroComissao;

public class AvaliacaoRelatorioProjetoDao extends GenericSigaaDAO {

	/**
	 * Retorna a avaliação ativa do membro da comissão no relatório informado
	 *
	 * @param relatorio
	 * @param avaliador
	 * @return
	 * @throws DAOException
	 */
	public AvaliacaoRelatorioProjeto findByRelatorioAvaliador(int idRelatorio, int idMembroComissao) throws DAOException {
		
		if ( (idRelatorio > 0) && (idMembroComissao > 0) ){

			Criteria c = getSession().createCriteria(AvaliacaoRelatorioProjeto.class);

			c.add(Expression.eq("relatorioProjetoMonitoria.id", idRelatorio));
			c.add(Expression.eq("avaliador.id", idMembroComissao));
			c.add(Expression.isNull("registroEntradaRetiradaDistribuicao")); //avaliação ativa
			
			c.setMaxResults(1);
			
			return (AvaliacaoRelatorioProjeto) c.uniqueResult();

		}else{
			return null;
		}

	}


	@SuppressWarnings("unchecked")
	public List<AvaliacaoRelatorioProjeto> findAvaliacoesByRelatorio(int idRelatorio) throws DAOException {
		
		Criteria c = getCriteria(AvaliacaoRelatorioProjeto.class);
		c.add(Expression.eq("relatorioProjetoMonitoria.id", idRelatorio));
		return c.list();
		
	}
	
	

	/**
	 * Retorna todos os avaliadores do relatório que possuem avaliações NÃO canceladas
	 * 
	 * @param projeto
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<MembroComissao> findAvaliadoresByRelatorio(int idRelatorio) throws DAOException {
		
		Query q = getSession().createQuery(
				"select distinct avaliador from " +
				"RelatorioProjetoMonitoria rel " +
				"inner join rel.avaliacoes avals " +							
				"inner join avals.avaliador avaliador " +				
				"where rel.id = :idRelatorio " +
				"and avals.statusAvaliacao.id != :idStatus"
				);
		
		q.setInteger("idRelatorio", idRelatorio);
		q.setInteger("idStatus", StatusAvaliacao.AVALIACAO_CANCELADA);

		return q.list();
	}

	
	/**
	 * 
	 * Retorna todas as avaliações do projeto com status igual/diferente do status informado
	 * 
	 * @param idProjeto
	 * @param igualAoStatus true para igual ao status informado, false para diferente do status informado
	 * @param idStatusAvaliacao
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AvaliacaoRelatorioProjeto> findByRelatorioStatusAvaliacao(int idRelatorio, boolean igualAoStatus, int idStatusAvaliacao) throws DAOException {
		try {

			Criteria c = getCriteria(AvaliacaoRelatorioProjeto.class);
			if (igualAoStatus)
				c.createCriteria("statusAvaliacao").add(Expression.eq("id", idStatusAvaliacao));
			
			if (!igualAoStatus)
				c.createCriteria("statusAvaliacao").add(Expression.ne("id", idStatusAvaliacao));

			if(idRelatorio > 0)
				c.createCriteria("relatorioProjetoMonitoria").add(Expression.eq("id", idRelatorio));
			
			return c.list();

		} catch(Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}


	/**
	 * Retorna todas as avaliações de relatórios onde o avaliador atuou ou atua 
	 * 
	 * @param membro membro da comissão
	 * @return
	 * @throws DAOException
	 */
	public Collection<AvaliacaoRelatorioProjeto> findByAvaliador(int idServidorMembroComissao) throws DAOException {
		
		StringBuilder hql = new StringBuilder();
	    	hql.append("select avaliacao.id as id,  avaliacao.dataAvaliacao as dataAvaliacao, " +
	    			"status as statusAvaliacao, " +
	    			"relatorio.id, " +
	    			"pe.id, " +
	    			"pe.projeto.id, " +
	    	    		"pe.projeto.ano, " +
	    	    		"pe.projeto.titulo, " +
	    	    		"tp.descricao, " +
	    	    		"tr.descricao, " +
	    	    		"avaliador.id, s.id, pes.id, pes.nome " +
	    	    		"from AvaliacaoRelatorioProjeto avaliacao " +
	    	    		"inner join avaliacao.avaliador avaliador " +							
	    	    		"inner join avaliador.servidor s " +
	    	    		"inner join s.pessoa pes " +
	    	    		"inner join avaliacao.statusAvaliacao status " +
	    	    		"inner join avaliacao.relatorioProjetoMonitoria relatorio " +
	    	    		"inner join relatorio.tipoRelatorio tr " +
	    	    		"inner join relatorio.projetoEnsino pe " +
	    	    		"inner join pe.tipoProjetoEnsino tp " +
	    	    		"where s.id = :idServidor " +
    				"and status.id != :idStatusCancelado");
	    
		Query q = getSession().createQuery(hql.toString());	
		q.setInteger("idServidor", idServidorMembroComissao);
		q.setInteger("idStatusCancelado", StatusAvaliacao.AVALIACAO_CANCELADA);
		
		@SuppressWarnings("unchecked")
		List<Object> lista = q.list();

		ArrayList<AvaliacaoRelatorioProjeto> result = new ArrayList<AvaliacaoRelatorioProjeto>();
		for (int a = 0; a < lista.size(); a++) {
			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);
			AvaliacaoRelatorioProjeto avaliacao = new AvaliacaoRelatorioProjeto();
			
			avaliacao.setId((Integer) colunas[col++]);
			avaliacao.setDataAvaliacao((Date) colunas[col++]);
			avaliacao.setStatusAvaliacao((StatusAvaliacao) colunas[col++]);
			RelatorioProjetoMonitoria relatorio = new RelatorioProjetoMonitoria((Integer) colunas[col++]);
				relatorio.getProjetoEnsino().setId((Integer) colunas[col++]);
				relatorio.getProjetoEnsino().getProjeto().setId((Integer) colunas[col++]);
				relatorio.getProjetoEnsino().getProjeto().setAno((Integer) colunas[col++]);
				relatorio.getProjetoEnsino().getProjeto().setTitulo((String) colunas[col++]);
				relatorio.getProjetoEnsino().getTipoProjetoEnsino().setDescricao((String) colunas[col++]);
				relatorio.getTipoRelatorio().setDescricao((String) colunas[col++]);
			avaliacao.setRelatorioProjetoMonitoria(relatorio);
			MembroComissao avaliador = new MembroComissao((Integer) colunas[col++]);
				Servidor s = new Servidor((Integer) colunas[col++]);
					Pessoa p = new Pessoa((Integer) colunas[col++]);
						p.setNome((String) colunas[col++]);
					s.setPessoa(p);
			avaliador.setServidor(s);				
			avaliacao.setAvaliador(avaliador);			
			result.add(avaliacao);
		}
		
		return result;	
		
		
	}

	
	

}
