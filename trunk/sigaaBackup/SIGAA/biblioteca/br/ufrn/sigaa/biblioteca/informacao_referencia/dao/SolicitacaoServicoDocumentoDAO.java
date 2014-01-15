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
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.SolicitacaoCatalogacao;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.SolicitacaoNormalizacao;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.SolicitacaoServico.TipoSituacao;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.SolicitacaoServicoDocumento;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.TipoDocumentoNormalizacaoCatalogacao;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.TipoServicoInformacaoReferencia;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * DAO para realizar consultas sobre a entidade SolicitacaoServico
 * 
 * @author Felipe Rivas
 */
public class SolicitacaoServicoDocumentoDAO extends GenericSigaaDAO {

	
	
	/**
	 * Conta a quantidade de requisições abertas para o usuário, utilizado para impedir que o usuário realiza mais de uma solicitação por fez.
	 *
	 * @param tipoServico
	 * @param idPessoa
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public int contaSolicitacoesDoUsuaro(TipoServicoInformacaoReferencia tipoServico, int idPessoa, TipoSituacao... situacoes) throws  DAOException{
		
		StringBuilder hql =  new StringBuilder(" SELECT COUNT(DISTINCT id) FROM ");
		
		if(tipoServico == null || tipoServico == TipoServicoInformacaoReferencia.CATALOGACAO_NA_FONTE)
			hql.append(" SolicitacaoCatalogacao s ");
		if(tipoServico == null || tipoServico == TipoServicoInformacaoReferencia.NORMALIZACAO)
			hql.append(" SolicitacaoNormalizacao s ");
		
		
		hql.append(" WHERE s.pessoa.id = :idPessoa ");
		hql.append(" AND s.situacao in "+UFRNUtils.gerarStringIn(situacoes));
		hql.append(" AND s.ativa = :true");
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idPessoa", idPessoa);
		q.setBoolean("true", true);
		
		return ( (Long) q.uniqueResult()).intValue();
		
	}
	
	
	/**
	 * <p> Conta a quantidade de biblioteca no sistema que estão configuradas para realizar um determinado serviço. </p>
	 *
	 * @param tipoServico
	 * @param idPessoa
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public int contaBibliotecasComServicoAtivo(TipoServicoInformacaoReferencia tipoServico) throws  DAOException{
		
		StringBuilder hql =  new StringBuilder(" SELECT COUNT(DISTINCT b.id) ");
		
		hql.append(" FROM  ServicosInformacaoReferenciaBiblioteca s ");
		hql.append(" INNER JOIN s.biblioteca b ");
		hql.append(" WHERE b.ativo = :true ");
		hql.append(" AND b.unidade IS NOT NULL "); // biblioteca interna
		
		if(tipoServico == null || tipoServico == TipoServicoInformacaoReferencia.CATALOGACAO_NA_FONTE)
			hql.append(" AND s.realizaCatalogacaoNaFonte = :true");
		if(tipoServico == null || tipoServico == TipoServicoInformacaoReferencia.NORMALIZACAO)
			hql.append(" AND s.realizaNormalizacao = :true");
		if(tipoServico == null || tipoServico == TipoServicoInformacaoReferencia.ORIENTACAO_NORMALIZACAO)
			hql.append(" AND s.realizaOrientacaoNormalizacao = :true");
		
		
		Query q = getSession().createQuery(hql.toString());
		q.setBoolean("true", true);
		
		return ( (Long) q.uniqueResult()).intValue();
		
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
	public List<SolicitacaoServicoDocumento> findSolicitacoesAtivas( Pessoa pessoa, Discente discente, Servidor servidor, 
			Integer numeroSolicitacao, String nomeUsuarioSolicitante,
			TipoServicoInformacaoReferencia tipoServico, TipoDocumentoNormalizacaoCatalogacao tipoDocumento, 
			Biblioteca biblioteca, Date dataInicial, Date dataFinal, boolean buscasApenasAtivas, TipoSituacao... situacoes) throws DAOException {
		
		
		List<SolicitacaoServicoDocumento> solicitacoes = new ArrayList<SolicitacaoServicoDocumento>();
		
		if(tipoServico == null || tipoServico == TipoServicoInformacaoReferencia.CATALOGACAO_NA_FONTE)
			solicitacoes.addAll(findSolicitacoesCatalogacaoAtivas( pessoa,  discente, servidor,
					numeroSolicitacao, nomeUsuarioSolicitante,
					tipoDocumento, biblioteca,  dataInicial,  dataFinal, buscasApenasAtivas, situacoes));
		
		if(tipoServico == null || tipoServico == TipoServicoInformacaoReferencia.NORMALIZACAO)
			solicitacoes.addAll(findSolicitacoesNormalizacaoAtivas( pessoa,  discente, servidor,
					 numeroSolicitacao, nomeUsuarioSolicitante,  tipoDocumento,  biblioteca,  dataInicial,  dataFinal, buscasApenasAtivas, situacoes));
		
		return solicitacoes;
	}

	/**
	 * 
	 * Retorna as solicitações de catalogação ativas de acordo com os critéridos passados.
	 *
	 * @param pessoa
	 * @param discente
	 * @param servidor
	 * @param tipoDocumento
	 * @param biblioteca
	 * @param dataInicial
	 * @param dataFinal
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	private List<SolicitacaoCatalogacao> findSolicitacoesCatalogacaoAtivas( Pessoa pessoa, Discente discente, Servidor servidor, 
			Integer numeroSolicitacao, String nomeUsuarioSolicitante,
			TipoDocumentoNormalizacaoCatalogacao tipoDocumento, 
			Biblioteca biblioteca, Date dataInicial, Date dataFinal, boolean buscasApenasAtivas, TipoSituacao... situacoes) throws DAOException {
		
		String projecao = " id, numeroSolicitacao, tipoDocumento.id, tipoDocumento.denominacao, outroTipoDocumento, dataCadastro, pessoa.id, pessoa.nome, biblioteca.id, biblioteca.descricao, situacao, ativa ";
		
		StringBuilder hql = new StringBuilder(" SELECT "+projecao+" FROM SolicitacaoCatalogacao s WHERE 1=1 ");
		
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
		
		if (tipoDocumento != null)
			hql.append(" AND s.tipoDocumento.id = :idTipoDocumento ");
		
		if (situacoes != null && situacoes.length > 0)
			hql.append(" AND s.situacao in "+UFRNUtils.gerarStringIn(situacoes));
		
		if (dataInicial != null && dataFinal != null)
			hql.append(" AND ( s.dataCadastro between :dataInicial AND :dataFinal ) ");
		
		if(buscasApenasAtivas)
			hql.append(" AND s.ativa = :true");
		
		hql.append(" ORDER BY situacao, dataCadastro");
	
	
		Query q = getSession().createQuery(hql.toString());
		if (pessoa != null)  q.setInteger("idPessoa", pessoa.getId());
		if (discente != null)q.setInteger("idDiscente", discente.getId());
		if (servidor != null)q.setInteger("idServidor", servidor.getId());
		
		if (numeroSolicitacao != null)                    q.setInteger("numeroSolicitacao", numeroSolicitacao);
		if (StringUtils.notEmpty(nomeUsuarioSolicitante)) q.setString("nomeUsuarioSolicitante", "%"+StringUtils.toAsciiAndUpperCase(nomeUsuarioSolicitante)+"%" );
		
		if (biblioteca != null)    q.setInteger("idBiblioteca", biblioteca.getId());
		if (tipoDocumento != null) q.setInteger("idTipoDocumento", tipoDocumento.getId());
		
		if (dataInicial != null && dataFinal != null) {	
			dataInicial = CalendarUtils.configuraTempoDaData(dataInicial, 0, 0, 0, 0);
			dataFinal = CalendarUtils.configuraTempoDaData(dataFinal, 23, 59, 59, 0);
			
			q.setTimestamp("dataInicial", dataInicial);
			q.setTimestamp("dataFinal", dataFinal);
		}
		
		if(buscasApenasAtivas)
			q.setBoolean("true", true);
		
		q.setMaxResults(101);
		
		@SuppressWarnings("unchecked")
		List<SolicitacaoCatalogacao> lista = new ArrayList<SolicitacaoCatalogacao>(HibernateUtils.parseTo(q.list(), projecao, SolicitacaoCatalogacao.class, "s"));
		return lista;
		
	}
	
	/**
	 * 
	 * Retorna as solicitações de normalizacao ativas de acordo com os critéridos passados.
	 *
	 * @param pessoa
	 * @param discente
	 * @param servidor
	 * @param tipoDocumento
	 * @param biblioteca
	 * @param dataInicial
	 * @param dataFinal
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	private List<SolicitacaoNormalizacao> findSolicitacoesNormalizacaoAtivas( Pessoa pessoa, Discente discente,Servidor servidor,
			Integer numeroSolicitacao, String nomeUsuarioSolicitante,
			TipoDocumentoNormalizacaoCatalogacao tipoDocumento, 
			Biblioteca biblioteca, Date dataInicial, Date dataFinal, boolean buscasApenasAtivas, TipoSituacao... situacoes) throws DAOException {
		
		String projecao = " id, numeroSolicitacao, tipoDocumento.id, tipoDocumento.denominacao, outroTipoDocumento, dataCadastro, pessoa.id, pessoa.nome, biblioteca.id, biblioteca.descricao, situacao, ativa ";
		
		StringBuilder hql = new StringBuilder(" SELECT "+projecao+" FROM SolicitacaoNormalizacao s WHERE 1=1 ");
		
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
		
		if (tipoDocumento != null)
			hql.append(" AND s.tipoDocumento.id = :idTipoDocumento ");
		
		if (situacoes != null && situacoes.length > 0)
			hql.append(" AND s.situacao in "+UFRNUtils.gerarStringIn(situacoes));
		
		if (dataInicial != null && dataFinal != null)
			hql.append(" AND ( s.dataCadastro between :dataInicial AND :dataFinal ) ");
		
		if(buscasApenasAtivas)
			hql.append(" AND s.ativa = :true");
		
		hql.append(" ORDER BY situacao, dataCadastro");
	
	
		Query q = getSession().createQuery(hql.toString());
		if (pessoa != null)  q.setInteger("idPessoa", pessoa.getId());
		if (discente != null)q.setInteger("idDiscente", discente.getId());
		if (servidor != null)q.setInteger("idServidor", servidor.getId());
		
		if (numeroSolicitacao != null)                    q.setInteger("numeroSolicitacao", numeroSolicitacao);
		if (StringUtils.notEmpty(nomeUsuarioSolicitante)) q.setString("nomeUsuarioSolicitante", "%"+StringUtils.toAsciiAndUpperCase(nomeUsuarioSolicitante)+"%" );
		
		if (biblioteca != null)    q.setInteger("idBiblioteca", biblioteca.getId());
		if (tipoDocumento != null) q.setInteger("idTipoDocumento", tipoDocumento.getId());
		
		if (dataInicial != null && dataFinal != null) {	
			dataInicial = CalendarUtils.configuraTempoDaData(dataInicial, 0, 0, 0, 0);
			dataFinal = CalendarUtils.configuraTempoDaData(dataFinal, 23, 59, 59, 0);
			
			q.setTimestamp("dataInicial", dataInicial);
			q.setTimestamp("dataFinal", dataFinal);
		}
		
		if(buscasApenasAtivas)
			q.setBoolean("true", true);
		
		q.setMaxResults(301);
		
		@SuppressWarnings("unchecked")
		List<SolicitacaoNormalizacao> lista = new ArrayList<SolicitacaoNormalizacao>(HibernateUtils.parseTo(q.list(), projecao, SolicitacaoNormalizacao.class, "s"));
		return lista;
		
	}
	

}
