/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 01/12/2008
 *
 */

package br.ufrn.sigaa.biblioteca.informacao_referencia.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.SolicitacaoOrientacao;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.SolicitacaoServico.TipoSituacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * DAO para realizar consultas sobre a entidade SolicitacaoServico
 * 
 * @author Felipe Rivas
 */
public class SolicitacaoOrientacaoDAO extends GenericSigaaDAO {

	
	/**
	 * Conta a quantidade de requisições abertas para o usuário, utilizado para impedir que o usuário realiza mais de uma solicitação por fez.
	 *
	 * @param tipoServico
	 * @param idPessoa
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public int contaSolicitacoesDoUsuaro(int idPessoa, TipoSituacao... situacoes) throws  DAOException{
		
		StringBuilder hql =  new StringBuilder(" SELECT COUNT(DISTINCT id) FROM ");
		hql.append(" SolicitacaoOrientacao s ");
		
		hql.append(" WHERE s.pessoa.id = :idPessoa ");
		hql.append(" AND s.situacao in "+UFRNUtils.gerarStringIn(situacoes));
		hql.append(" AND s.ativa = :true");
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idPessoa", idPessoa);
		q.setBoolean("true", true);
		
		return ( (Long) q.uniqueResult()).intValue();
		
	}
	
	
	/**
	 * Retorna a data final da última solicitação do usuário. 
	 *
	 * @param tipoServico
	 * @param idPessoa
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public Date recuperaDataFinalUltimaSolicitacoesAtivasDoUsuaro(int idPessoa) throws  DAOException{
		
		StringBuilder hql =  new StringBuilder(" SELECT s.dataFim FROM ");
		hql.append(" SolicitacaoOrientacao s ");
		
		hql.append(" WHERE s.pessoa.id = :idPessoa ");
		hql.append(" AND s.situacao NOT IN "+UFRNUtils.gerarStringIn(TipoSituacao.getTipoSituacoesInativas() )  );
		hql.append(" AND s.ativa = :true");
		hql.append(" ORDER BY s.dataFim DESC ");
		
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idPessoa", idPessoa);
		q.setBoolean("true", true);
		q.setMaxResults(1);
		return (Date) q.uniqueResult();
		
	}
	
	
	/**
	 * Retorna as Solicitações de Serviço (serviços disponibilizados pela biblioteca ao usuário) pendentes de atendimento pelos 
	 * bibliotecários.
	 * 
	 * @param pessoa
	 * @param discente
	 * @param servidor
	 * @param tipoServico
	 * @param biblioteca
	 * @param dataInicial
	 * @param dataFinal
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<SolicitacaoOrientacao> findSolicitacoesAtivas( Pessoa pessoa, Discente discente,Servidor servidor, 
			Integer numeroSolicitacao, String nomeUsuarioSolicitante,
			Biblioteca biblioteca, Date dataInicial, Date dataFinal,
			boolean buscasApenasAtivas, TipoSituacao... situacoes) throws DAOException {
		
		String projecao = " id, numeroSolicitacao, dataInicio, dataFim, dataCadastro, pessoa.id, pessoa.nome, biblioteca.id, biblioteca.descricao, situacao, ativa ";
		
		StringBuilder hql = new StringBuilder(" SELECT "+projecao+" FROM SolicitacaoOrientacao s WHERE 1=1 ");
		
		if (pessoa != null) 
			hql.append(" AND s.pessoa.id = :idPessoa ");
		
		if (discente != null)
			hql.append(" AND s.discente.id = :idDiscente ");
		
		if (servidor != null)
			hql.append(" AND s.servidor.id = :idServidor ");
		
		if (numeroSolicitacao != null) 
			hql.append(" AND s.numeroSolicitacao = :numeroSolicitacao ");
		
		if (StringUtils.notEmpty(nomeUsuarioSolicitante)) 
			hql.append(" AND s.pessoa.nomeAscii like :nomeUsuarioSolicitante ");
		
		if (biblioteca != null)
			hql.append(" AND s.biblioteca.id = :idBiblioteca ");
		
		if (situacoes != null && situacoes.length > 0)
			hql.append(" AND s.situacao in "+UFRNUtils.gerarStringIn(situacoes));
		
		if (dataInicial != null && dataFinal != null)
			hql.append(" AND ( s.dataCadastro between :dataInicial AND :dataFinal ) ");
		
		if(buscasApenasAtivas)
			hql.append(" AND s.ativa = :true");
		
		hql.append(" ORDER BY situacao, dataCadastro");
	
	
		Query q = getSession().createQuery(hql.toString());
		if (pessoa != null)   q.setInteger("idPessoa", pessoa.getId());
		if (discente != null) q.setInteger("idDiscente", discente.getId());
		if (servidor != null) q.setInteger("idServidor", servidor.getId());
		
		if (numeroSolicitacao != null)                    q.setInteger("numeroSolicitacao", numeroSolicitacao);
		if (StringUtils.notEmpty(nomeUsuarioSolicitante)) q.setString("nomeUsuarioSolicitante", "%"+StringUtils.toAsciiAndUpperCase(nomeUsuarioSolicitante)+"%" );
		
		if (biblioteca != null)q.setInteger("idBiblioteca", biblioteca.getId());
		
		if (dataInicial != null && dataFinal != null) {	
			dataInicial = CalendarUtils.configuraTempoDaData(dataInicial, 0, 0, 0, 0);
			dataFinal = CalendarUtils.configuraTempoDaData(dataFinal, 23, 59, 59, 0);
			
			q.setTimestamp("dataInicial", dataInicial);
			q.setTimestamp("dataFinal", dataFinal);
		}
		
		if(buscasApenasAtivas)
			q.setBoolean("true", true);
		
		q.setMaxResults(301);
		
		List<SolicitacaoOrientacao> lista = new ArrayList<SolicitacaoOrientacao>(HibernateUtils.parseTo(q.list(), projecao, SolicitacaoOrientacao.class, "s"));
		return lista;
	}

}
