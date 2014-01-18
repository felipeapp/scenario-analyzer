package br.ufrn.sigaa.ensino.metropoledigital.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.RegistroQuantitativoConvocadosGrupo;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.ReservaVagaGrupo;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.ReservaVagaProcessoSeletivo;
import br.ufrn.sigaa.ensino.tecnico.dominio.InscricaoProcessoSeletivoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.OpcaoPoloGrupo;
import br.ufrn.sigaa.ensino.tecnico.dominio.ProcessoSeletivoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.ResultadoClassificacaoCandidatoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.SituacaoCandidatoTecnico;
/***
 * 
 * @author Rafael Barros
 * 
 * Dao responsável por todas as consultas adequadas para o novo processo seletivo do IMD
 *
 */
public class ConvocacaoIMDDao extends GenericSigaaDAO {

	/**
	 * Lista a quantidade total de vagas disponíveis para um determinado processo seletivo do IMD e uma opção polo grupo
	 * 
	 * @param idProcessoSeletivo, idOpcao
	 * @return
	 * @throws DAOException
	 */
	public Long findTotalConvocadosPorPoloByPS(int idProcessoSeletivo, int idOpcao) throws DAOException{
		try {
			String projecao = "COUNT(disc.opcaoPoloGrupo.id)";
			//CONSULTA GERAL
			String hql = "SELECT " + projecao + " FROM ConvocacaoProcessoSeletivoTecnico conv, ConvocacaoProcessoSeletivoDiscenteTecnico cdisc, DiscenteTecnico disc WHERE conv.id = cdisc.convocacaoProcessoSeletivo.id AND disc.id = cdisc.discente.id AND conv.processoSeletivo.id = " + idProcessoSeletivo + " AND disc.opcaoPoloGrupo.id = " + idOpcao;
			
			Query q = getSession().createQuery(hql);
			
			return (Long) q.uniqueResult();
			
		} catch (DAOException e) {
			 throw new DAOException(e);
		}
	}
	
	/**
	 * Lista a quantidade total de vagas remanescentes para um determinado processo seletivo do IMD e um polo
	 * 
	 * @param idProcessoSeletivo, idPolo
	 * @return
	 * @throws DAOException
	 */
	public Long findTotalRemanescentePoloByPSAndPolo(int idProcessoSeletivo, int idPolo, int idStatus) throws DAOException{
		try {
			//CONSULTA QUE IRÁ RETORNAR A QUANTIDADE DE CONVOCADOS QUE NÃO FORAM EXCLUÍDOS NO PÓLO
			/*String projecao = "COUNT(disc.opcaoPoloGrupo.id)";
			String hql = "SELECT " + projecao + " FROM ConvocacaoProcessoSeletivoTecnico conv, ConvocacaoProcessoSeletivoDiscenteTecnico cdisc, DiscenteTecnico disc, Discente d WHERE " +
					     " conv.id = cdisc.convocacaoProcessoSeletivo.id AND disc.id = d.id AND disc.id = cdisc.discente.id AND conv.processoSeletivo.id = " + idProcessoSeletivo + " AND " +
					     " disc.opcaoPoloGrupo.polo.id = " + idPolo  + "AND d.status <> " + idStatus + " AND d.status > 0" ;
			Query q = getSession().createQuery(hql);
			Long qtdConvocadosPolo = (Long) q.uniqueResult();*/
			
			String sql = "SELECT COUNT(*) FROM tecnico.convocacao_processo_seletivo_tecnico conv " +
						 " INNER JOIN tecnico.convocacao_processo_seletivo_discente_tecnico cdisc ON (conv.id_convocacao_processo_seletivo_tecnico = cdisc.id_convocacao_processo_seletivo) " +  
						 " INNER JOIN tecnico.discente_tecnico dt ON (dt.id_discente = cdisc.id_discente) " +
						 " INNER JOIN public.discente d on d.id_discente = dt.id_discente " +  
						 " INNER JOIN tecnico.opcao_polo_grupo op ON op.id_opcao_polo_grupo = conv.id_opcao " +
						 " WHERE  (op.id_polo = " + idPolo + ")  AND conv.id_processo_seletivo = " + idProcessoSeletivo + " AND d.status <> " + idStatus + " AND d.status > 0";
			
			@SuppressWarnings("unchecked")
			String resultado = (String) getSession().createSQLQuery (sql).uniqueResult().toString();
			Long qtdConvocadosPolo = Long.parseLong(resultado);
				
			//CONSULTA QUE IRÁ RETORNAR A QUANTIDADE TOTAL DE VAGAS PARA O PÓLO INFORMADO			
			String projecao2 = "SUM(rvps.vagas)";
			String hql2 = "SELECT " + projecao2 + " FROM ReservaVagaProcessoSeletivo rvps WHERE rvps.processo.id = " + idProcessoSeletivo + " AND rvps.opcao.polo.id = " + idPolo ;
			Query q2 = getSession().createQuery(hql2);
			Long qtdVagasPolo = (Long) q2.uniqueResult();
			
			Long vagasRemanescentesPolo = qtdVagasPolo - qtdConvocadosPolo;
			
			return vagasRemanescentesPolo;
			
		} catch (DAOException e) {
			 throw new DAOException(e);
		}
	}
	
	/**
	 * Lista a quantidade total de convocados de um determinado polo e processo seletivo
	 * 
	 * @param idProcessoSeletivo, idPolo
	 * @return
	 * @throws DAOException
	 */
	public Long findTotalConvocadosByPSAndPolo(int idProcessoSeletivo, int idPolo) throws DAOException{
		try {
			
			String sql = "SELECT COUNT(*) FROM tecnico.convocacao_processo_seletivo_tecnico conv " +
						 " INNER JOIN tecnico.convocacao_processo_seletivo_discente_tecnico cdisc ON (conv.id_convocacao_processo_seletivo_tecnico = cdisc.id_convocacao_processo_seletivo) " +  
						 " INNER JOIN tecnico.discente_tecnico dt ON (dt.id_discente = cdisc.id_discente) " +
						 " INNER JOIN public.discente d on d.id_discente = dt.id_discente " +  
						 " INNER JOIN tecnico.opcao_polo_grupo op ON op.id_opcao_polo_grupo = conv.id_opcao " +
						 " WHERE  op.id_polo = " + idPolo + "  AND conv.id_processo_seletivo = " + idProcessoSeletivo + " AND d.status > 0";
			
			@SuppressWarnings("unchecked")
			String resultado = (String) getSession().createSQLQuery (sql).uniqueResult().toString();
			return Long.parseLong(resultado);	
			
		} catch (DAOException e) {
			 throw new DAOException(e);
		}
	}
	
	/**
	 * Lista a quantidade total de excluídos de um determinado polo e processo seletivo
	 * 
	 * @param idProcessoSeletivo, idPolo
	 * @return
	 * @throws DAOException
	 */
	public Long findTotalExcluidosByPSAndPolo(int idProcessoSeletivo, int idPolo) throws DAOException{
		try {
			
			String sql = "SELECT COUNT(*) FROM tecnico.convocacao_processo_seletivo_tecnico conv " +
						 " INNER JOIN tecnico.convocacao_processo_seletivo_discente_tecnico cdisc ON (conv.id_convocacao_processo_seletivo_tecnico = cdisc.id_convocacao_processo_seletivo) " +  
						 " INNER JOIN tecnico.discente_tecnico dt ON (dt.id_discente = cdisc.id_discente) " +
						 " INNER JOIN public.discente d on d.id_discente = dt.id_discente " +  
						 " INNER JOIN tecnico.opcao_polo_grupo op ON op.id_opcao_polo_grupo = conv.id_opcao " +
						 " WHERE  op.id_polo = " + idPolo + "  AND conv.id_processo_seletivo = " + idProcessoSeletivo + " AND d.status = 10";
			
			@SuppressWarnings("unchecked")
			String resultado = (String) getSession().createSQLQuery (sql).uniqueResult().toString();
			return Long.parseLong(resultado);	
			
		} catch (DAOException e) {
			 throw new DAOException(e);
		}
	}
	
	/**
	 * Lista a quantidade total de suplentes para um determinado processo seletivo do IMD e um polo
	 * 
	 * @param idProcessoSeletivo, idPolo
	 * @return
	 * @throws DAOException
	 */
	public Long findTotalSuplentesByPSAndPolo(int idProcessoSeletivo, int idPolo) throws DAOException{
		try {
			//CONSULTA QUE IRÁ RETORNAR A QUANTIDADE DE CONVOCADOS QUE NÃO FORAM EXCLUÍDOS NO PÓLO
			String projecao = "COUNT(ips.id)";
			String hql = "SELECT " + projecao + " FROM InscricaoProcessoSeletivoTecnico ips, ResultadoClassificacaoCandidatoTecnico res WHERE " +
					     " res.inscricaoProcessoSeletivo.id = ips.id AND ips.opcao.polo.id = " + idPolo  + " AND ips.processoSeletivo.id = " + idProcessoSeletivo + " AND res.situacaoCandidato.id = 3";
			Query q = getSession().createQuery(hql);
			return (Long) q.uniqueResult();
			
		} catch (DAOException e) {
			 throw new DAOException(e);
		}
	}
	
	
	/**
	 * Lista a quantidade total de suplentes para um determinado processo seletivo do IMD e um polo
	 * 
	 * @param idProcessoSeletivo, idOpcao
	 * @return
	 * @throws DAOException
	 */
	public Long findTotalSuplentesOpcaoByPSAndOpcao(int idProcessoSeletivo, int idOpcao) throws DAOException{
		try {
			//CONSULTA QUE IRÁ RETORNAR A QUANTIDADE DE CONVOCADOS QUE NÃO FORAM EXCLUÍDOS NO PÓLO
			String projecao = "COUNT(ips.id)";
			String hql = "SELECT " + projecao + " FROM InscricaoProcessoSeletivoTecnico ips, ResultadoClassificacaoCandidatoTecnico res WHERE " +
					     " res.inscricaoProcessoSeletivo.id = ips.id AND ips.opcao.id = " + idOpcao  + " AND ips.processoSeletivo.id = " + idProcessoSeletivo + " AND res.situacaoCandidato.id = 3";
			Query q = getSession().createQuery(hql);
			return (Long) q.uniqueResult();
			
		} catch (DAOException e) {
			 throw new DAOException(e);
		}
	}
	
	/**
	 * Lista a quantidade total de vagas de um determinado processo seletivo
	 * 
	 * @param idProcessoSeletivo
	 * @return
	 * @throws DAOException
	 */
	public Long findTotalVagasPS(int idProcessoSeletivo) throws DAOException{
		try {
			String projecao = "SUM(vagas)";
			//CONSULTA GERAL
			String hql = "SELECT " + projecao + " FROM ReservaVagaProcessoSeletivo WHERE processo.id = " + idProcessoSeletivo;
			
			Query q = getSession().createQuery(hql);
			
			@SuppressWarnings("unchecked")
			Collection<Long> res = q.list();
			
			if(res.size() > 0) {
				for(Long reg: res){
					if(reg != null) {
						return reg;
					}
				}
			} 
			return null;
		} catch (DAOException e) {
			 throw new DAOException(e);
		}
	}
	
	/**
	 * Lista a quantidade total de discentes convocados para cada opção pólo grupo de um determinado processo seletivo
	 * 
	 * @param idProcessoSeletivo, idOpcao
	 * @return
	 * @throws DAOException
	 */
	public Long findTotalVagasReservaVagaPS(int idProcessoSeletivo, int idOpcao) throws DAOException{
		try {
			String projecao = "SUM(vagas)";
			//CONSULTA GERAL
			String hql = "SELECT " + projecao + " FROM ReservaVagaProcessoSeletivo WHERE processo.id = " + idProcessoSeletivo + " AND  opcao.polo.id =  " + idOpcao;
			
			Query q = getSession().createQuery(hql);
			
			@SuppressWarnings("unchecked")
			Collection<Long> res = q.list();
			
			if(res.size() > 0) {
				for(Long reg: res){
					if(reg != null) {
						return reg;
					}
				}
			} 
			return null;
		} catch (DAOException e) {
			 throw new DAOException(e);
		}
	}
	
	
	/**
	 * Lista todos os grupos de reserva de vagas pertencentes a um determinado PS
	 * 
	 * @param idProcessoSeletivo
	 * @return Collection<ReservaVagaProcessoSeletivo>
	 * @throws DAOException
	 */
	public Collection<ReservaVagaProcessoSeletivo> findReservasByPS(int idProcessoSeletivo, Integer idOpcao, Integer idGrupoReservaVaga) throws DAOException{
		try {
			Criteria c = getSession().createCriteria(ReservaVagaProcessoSeletivo.class);
			Criteria cPS = c.createCriteria("processo");
			Criteria cTipo = c.createCriteria("tipoReserva");						
			Criteria cOp = c.createCriteria("opcao");

			cPS.add(Expression.eq("id", idProcessoSeletivo));
			
			if(idOpcao != null && idOpcao > 0){
				cOp.add(Expression.eq("id", idOpcao));
			}
			
			if(idGrupoReservaVaga != null && idGrupoReservaVaga > 0){
				cTipo.add(Expression.eq("id", idGrupoReservaVaga));
			}
			
			cOp.addOrder(Order.asc("id"));
			cTipo.addOrder(Order.asc("prioridade"));
			
			Collection<ReservaVagaProcessoSeletivo> lista = new ArrayList<ReservaVagaProcessoSeletivo>();
			lista = c.list();
			return lista;
			
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	
	/**
	 * Consulta que retorna a quantidade de candidatos convocados para cada grupo de reserva de vagas
	 *  
	 * @param idPS, idSituacao, idGrupo
	 * @return
	 * @throws HibernateException, DAOException
	 */
	public Collection<RegistroQuantitativoConvocadosGrupo> findQuantidadeConvocadosPorGrupo(int idPS, int idSituacao, int idGrupo) throws HibernateException, DAOException{									
		
		GenericSigaaDAO dao = new GenericSigaaDAO();
		
		try {
			String projecao = " ips.processoSeletivo.id, op.id, sit.id, rvg.id, COUNT(sit.id) ";
					
			//CONSULTA GERAL
			String hql = "SELECT " + projecao +
					" FROM ResultadoClassificacaoCandidatoTecnico res, SituacaoCandidatoTecnico sit, InscricaoProcessoSeletivoTecnico ips, " +
					" PessoaTecnico pes, OpcaoPoloGrupo op, ReservaVagaCandidato rvc, ReservaVagaGrupo rvg " +
					" WHERE res.situacaoCandidato.id = sit.id, ips.id = res.inscricaoProcessoSeletivo.id, pes.id = ips.pessoa.id, " +
					" ips.opcao.id = op.id, rvc.candidato.id = ips.id, rvg.id = rvc.tipo.id, ips.id_processo_seletivo_tecnico = " + idPS + " AND sit.id = " + idSituacao  + " AND rvg.id = " + idGrupo +
					" GROUP BY ips.id_processo_seletivo_tecnico, op.id_opcao_polo_grupo, op.descricao, sit.id_situacao_candidato, sit.descricao, rvg.id_reserva_vaga_grupo, rvg.denominacao " + 
					" ORDER BY op.descricao, sit.descricao";
			
			Query q = getSession().createQuery(hql);
			
			@SuppressWarnings("unchecked")
			Collection<Object[]> res = q.list();
			
			Collection<RegistroQuantitativoConvocadosGrupo> listaRetorno = new ArrayList<RegistroQuantitativoConvocadosGrupo>();
			
			Date dataAtual = new Date();
				
			for (Object[] reg : res) {
				int col = 0;
				
				Integer idProcessoSeletivo = (Integer) reg[col++];
				Integer idOpcaoPoloGrupo = (Integer) reg[col++];
				Integer idSituacaoCandidato = (Integer) reg[col++];
				Integer idReservaVagaGrupo = (Integer) reg[col++];
				Integer qtdConvocada = (Integer) reg[col++];
				
				RegistroQuantitativoConvocadosGrupo registro = new RegistroQuantitativoConvocadosGrupo();
				
				if(idProcessoSeletivo != null) {
					ProcessoSeletivoTecnico ps = dao.findByPrimaryKey(idProcessoSeletivo, ProcessoSeletivoTecnico.class);
					registro.setProcessoSeletivo(ps);
				}
				
				if(idOpcaoPoloGrupo != null) {
					OpcaoPoloGrupo opcao = dao.findByPrimaryKey(idOpcaoPoloGrupo, OpcaoPoloGrupo.class);
					registro.setOpcao(opcao);
				}
				
				if(idSituacaoCandidato != null) {
					SituacaoCandidatoTecnico situacao = dao.findByPrimaryKey(idSituacaoCandidato, SituacaoCandidatoTecnico.class);
					registro.setSituacaoCandidato(situacao);
				}
				
				if(idReservaVagaGrupo != null) {
					ReservaVagaGrupo reserva = dao.findByPrimaryKey(idReservaVagaGrupo, ReservaVagaGrupo.class);
					registro.setReservaVagaGrupo(reserva);
				}
				
				if(qtdConvocada != null) {
					registro.setQtdConvocada(qtdConvocada);
				}
				
				listaRetorno.add(registro);
			
			}
			
			return listaRetorno;
		} finally {
			dao.close();
		}
	}
	
	
	
	/**
	 * Consulta que retorna a quantidade de vagas remanescentes do processo seletivo do IMD
	 *  
	 * @param 
	 * @return
	 * @throws HibernateException, DAOException
	 */
	public Integer findQuantidadeRemanescentesPorGrupo(int idPS, int idOpcaoPoloGrupo, int idGrupo, int idSituacao) throws HibernateException, DAOException{									
		
		GenericSigaaDAO dao = new GenericSigaaDAO();
		
		try {
			String projecao = " ips.id, op.id, rvg.id, disc.status, COUNT(disc.status) ";
					
			//CONSULTA GERAL
			String hql = "SELECT " + projecao +
					" FROM InscricaoProcessoSeletivoTecnico ips, ReservaVagaCandidato rvc, ReservaVagaGrupo rvg, " +
					" ProcessoSeletivoTecnico ps, OpcaoPoloGrupo op, ConvocacaoProcessoSeletivoDiscenteTecnico cdisc, Discente disc " +
					" WHERE ips.id = rvc.candidato.id AND rvg.id = rvc.tipo.id AND ps.id = ips.processoSeletivo.id AND" +
					" op.id = ips.opcao.id AND cdisc.inscricaoProcessoSeletivo.id = ips.id AND " +
					" disc.id = cdisc.discente.id AND ps.id = " + idPS + " AND " +
					" op.id = " + idOpcaoPoloGrupo + " AND rvg.id = " + idGrupo + " AND disc.status = " + idSituacao + "" +
					" GROUP BY ips.id, op.id, rvg.id, disc.status";
				
			Query q = getSession().createQuery(hql);
			
			@SuppressWarnings("unchecked")
			Collection<Object[]> res = q.list();
			
			Collection<RegistroQuantitativoConvocadosGrupo> listaRetorno = new ArrayList<RegistroQuantitativoConvocadosGrupo>();
			
			Long qtdRemanescentes = (long) 0;
				
			for (Object[] reg : res) {
				int col = 0;
				
				Integer idProcessoSeletivo = (Integer) reg[col++];
				Integer idOpcao = (Integer) reg[col++];
				Integer idReservaVagaGrupo = (Integer) reg[col++];
				Integer idSituacaoCandidato = (Integer) reg[col++];
				qtdRemanescentes = (Long) reg[col++] ;
				
				break;
			}
			
			return Integer.valueOf(qtdRemanescentes.toString());
		} finally {
			dao.close();
		}
	}
	
	
	
	/**
	 * Consulta que retorna a listagem dos candidatos a serem convocados de acordo com os critérios informados
	 *  
	 * @param 
	 * @return
	 * @throws HibernateException, DAOException
	 */
	public Collection<ResultadoClassificacaoCandidatoTecnico> findCandidatosRemanescentes(int idPS, int idOpcaoPoloGrupo, int idGrupo, int qtdASerConvocada) throws HibernateException, DAOException{									
		
		GenericSigaaDAO dao = new GenericSigaaDAO();
		
		try {
			String projecao = "rcc.id, rcc.inscricaoProcessoSeletivo.id, rcc.inscricaoProcessoSeletivo.pessoa.id, rcc.inscricaoProcessoSeletivo.pessoa.cpf_cnpj, rcc.argumentoFinal,  " +
					"rcc.classificacaoAprovado, rcc.inscricaoProcessoSeletivo.numeroInscricao, rcc.inscricaoProcessoSeletivo.pessoa.nome, rcc.inscricaoProcessoSeletivo.pessoa.email, rcc.inscricaoProcessoSeletivo.reservaVagas, rcc.inscricaoProcessoSeletivo.opcao.id, rcc.inscricaoProcessoSeletivo.opcao.polo.id, rcc.inscricaoProcessoSeletivo.opcao.polo.cidade.id, rcc.inscricaoProcessoSeletivo.opcao.polo.cidade.nome, rcc.inscricaoProcessoSeletivo.opcao.grupo, rcc.inscricaoProcessoSeletivo.opcao.descricao ";
					
			//CONSULTA GERAL
			String hql = "SELECT " + projecao +
					" FROM ResultadoClassificacaoCandidatoTecnico rcc, InscricaoProcessoSeletivoTecnico ips," +
					" SituacaoCandidatoTecnico sit, ProcessoSeletivoTecnico ps, ReservaVagaCandidato rvc," +
					" ReservaVagaGrupo rvg, OpcaoPoloGrupo op, PessoaTecnico p " +
					" WHERE sit.id = rcc.situacaoCandidato.id AND ips.id = rcc.inscricaoProcessoSeletivo.id AND " +
					" ps.id = ips.processoSeletivo.id AND rvc.candidato.id = ips.id AND rvg.id = rvc.tipo.id AND" +
					" op.id = ips.opcao.id AND p.id = ips.pessoa.id " +
					" AND ps.id = :idPS AND sit.id = :situacaoCandidato AND op.id = :idOpcaoPoloGrupo AND rvg.id = :idGrupo " +
					" ORDER BY rcc.classificacaoAprovado";
				
			Query q = getSession().createQuery(hql);
			
			q.setInteger("situacaoCandidato", SituacaoCandidatoTecnico.SUPLENTE.getId());
			q.setInteger("idPS", idPS);
			q.setInteger("idOpcaoPoloGrupo", idOpcaoPoloGrupo);
			q.setInteger("idGrupo", idGrupo);
			
			q.setMaxResults(qtdASerConvocada);
			
			@SuppressWarnings("unchecked")
			List<Object[]> lista = q.list();
			Collection<ResultadoClassificacaoCandidatoTecnico> result = HibernateUtils.parseTo(lista, projecao, ResultadoClassificacaoCandidatoTecnico.class, "rcc");
			return result;
			
		} finally {
			dao.close();
		}
	}
	
	
	/**
	 * Lista o grupo de reserva de vagas de uma determinada inscrição de PS
	 * 
	 * @param inscricao
	 * @return ReservaVagaGrupo
	 * @throws DAOException
	 */
	public ReservaVagaGrupo findGrupoByInscricao(InscricaoProcessoSeletivoTecnico inscricao) throws DAOException{
		try {
			Criteria c = getSession().createCriteria(ReservaVagaGrupo.class);
			
			c.add(Expression.eq("escolaPublica", inscricao.isEscolaPublica()));
			c.add(Expression.eq("baixaRenda", inscricao.isBaixaRenda()));
			c.add(Expression.eq("etnia", inscricao.isEtnia()));

			return (ReservaVagaGrupo) c.uniqueResult();
			
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
}
