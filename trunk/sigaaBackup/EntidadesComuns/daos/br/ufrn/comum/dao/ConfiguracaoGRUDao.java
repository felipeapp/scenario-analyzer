package br.ufrn.comum.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.dao.GenericSharedDBDao;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.dao.dialect.SQLDialect;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.gru.dominio.ConfiguracaoGRU;
import br.ufrn.comum.gru.dominio.GuiaRecolhimentoUniao;

/**
 * DAO para consultas relacionadas a Configuração da GRU
 * 
 * @author Weksley Viana
 */
public class ConfiguracaoGRUDao extends GenericSharedDBDao {
	
	/**
	 * Busca as configurações da GRU
	 * */
	@SuppressWarnings("unchecked")
	public List<ConfiguracaoGRU> findGrusEmitidas(long referencia, Long cpf, Date dataInicio, Date dataFinal, Date dataPagInicio, Date dataPagFinal, int idCodigoRecolhimento, int idTipoGRU, int idTipoArrecadacao, 
				int idUnidadeFavorecida, boolean pagas, boolean arrecadadas, PagingInformation paginacao) throws HibernateException, DAOException{
		
		StringBuilder hql = new StringBuilder();
		
		hql.append(" GuiaRecolhimentoUniao gru " +
				" where gru.configuracaoGRU.ativo = " + SQLDialect.TRUE);
		
		if(referencia > 0)
			hql.append(" and gru.numeroReferenciaNossoNumero =:referencia ");
		
		if(pagas)
			hql.append(" and gru.quitada = "  + SQLDialect.TRUE);
		
		if(arrecadadas)
			hql.append(" and gru.arrecadada = "  + SQLDialect.TRUE);
		
		if (cpf != null && cpf > 0)
			hql.append(" and gru.cpf =:cpf ");
		
		if (!ValidatorUtil.isEmpty(idCodigoRecolhimento))
			hql.append(" and gru.tipoArrecadacao.codigoRecolhimento.id =:idCodigoRecolhimento ");
		
		if (!ValidatorUtil.isEmpty(idUnidadeFavorecida))
			hql.append(" and gru.unidadeFavorecida.id =:idUnidadeFavorecida ");
		
		if (!ValidatorUtil.isEmpty(idTipoArrecadacao))
			hql.append(" and gru.configuracaoGRU.tipoArrecadacao.id =:idTipoArrecadacao ");
		
		if (idTipoGRU > 0)
			hql.append(" and gru.tipo =:idTipoGRU ");
		
		if (dataInicio != null && dataFinal != null)
			hql.append(" and gru.dataDocumento between :dataInicial and :dataFinal  ");
		
		if (dataPagInicio != null && dataPagFinal != null)
			hql.append(" and gru.dataPagamento between :dataPagInicial and :dataPagFinal  ");
		
		Query q = getSession().createQuery("select gru from " + hql.toString() + " order by gru.configuracaoGRU.tipoArrecadacao.id, gru.dataDocumento desc, gru.dataPagamento desc");
		
		if (referencia > 0 )
			q.setLong("referencia", referencia);
		
		if (cpf != null && cpf > 0)
			q.setLong("cpf", cpf);
		
		if (!ValidatorUtil.isEmpty(idCodigoRecolhimento))
			q.setInteger("idCodigoRecolhimento", idCodigoRecolhimento);
		
		if (!ValidatorUtil.isEmpty(idTipoArrecadacao))
			q.setInteger("idTipoArrecadacao", idTipoArrecadacao);
		
		if (!ValidatorUtil.isEmpty(idUnidadeFavorecida))
			q.setInteger("idUnidadeFavorecida", idUnidadeFavorecida);
		
		if (idTipoGRU > 0)
			q.setInteger("idTipoGRU", idTipoGRU);
		
		if (dataInicio != null && dataFinal != null){
			q.setDate("dataInicial", dataInicio);
			q.setDate("dataFinal", dataFinal);
		}
		
		if (dataPagInicio != null && dataPagFinal != null){
			q.setDate("dataPagInicial", dataPagInicio);
			q.setDate("dataPagFinal", dataPagFinal);
		}
		
		if (paginacao != null){

			Query qPag = getSession().createQuery("select count(*) from " + hql.toString());
			
			if (referencia > 0 )
				qPag.setLong("referencia", referencia);
			
			if (cpf != null && cpf > 0)
				qPag.setLong("cpf", cpf);
			
			if (!ValidatorUtil.isEmpty(idCodigoRecolhimento))
				qPag.setInteger("idCodigoRecolhimento", idCodigoRecolhimento);
			
			if (!ValidatorUtil.isEmpty(idTipoArrecadacao))
				qPag.setInteger("idTipoArrecadacao", idTipoArrecadacao);
			
			if (!ValidatorUtil.isEmpty(idUnidadeFavorecida))
				qPag.setInteger("idUnidadeFavorecida", idUnidadeFavorecida);
			
			if (idTipoGRU>0)
				qPag.setInteger("idTipoGRU", idTipoGRU);
			
			if (dataInicio != null && dataFinal != null){
				qPag.setTimestamp("dataInicial", dataInicio);
				qPag.setTimestamp("dataFinal", dataFinal);
			}
			
			if (dataPagInicio != null && dataPagFinal != null){
				qPag.setTimestamp("dataPagInicial", dataPagInicio);
				qPag.setTimestamp("dataPagFinal", dataPagFinal);
			}

			paginacao.setTotalRegistros(((Long) qPag.uniqueResult()).intValue());	
			

			q.setFirstResult(paginacao.getPaginaAtual() * paginacao.getTamanhoPagina());
			q.setMaxResults(paginacao.getTamanhoPagina());
		}
		
		q.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		List<GuiaRecolhimentoUniao> grus = q.list();
		List<ConfiguracaoGRU> configuracoes = new ArrayList<ConfiguracaoGRU>();
		for(GuiaRecolhimentoUniao gru : grus){
			if (!configuracoes.contains(gru.getConfiguracaoGRU())){
				gru.getConfiguracaoGRU().getGrus().clear();
				gru.getConfiguracaoGRU().getGrus().add(gru);
				configuracoes.add(gru.getConfiguracaoGRU());
			}else{
				configuracoes.get(configuracoes.indexOf(gru.getConfiguracaoGRU())).getGrus().add(gru);
			}	
		}
		
		return configuracoes;
	}

}
