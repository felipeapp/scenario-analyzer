package br.ufrn.sigaa.assistencia.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.graduacao.AbstractRelatorioSqlDao;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilioPeriodo;
import br.ufrn.sigaa.assistencia.dominio.SituacaoBolsaAuxilio;

public class FinalizarBolsaAuxilioDao extends AbstractRelatorioSqlDao {
	
	public List<BolsaAuxilioPeriodo> findBolsistasAtivos(Integer ano, Integer periodo, Integer tipoBolsa) throws DAOException {
		
		String projecao = "bap.bolsaAuxilio.id, bap.ano, bap.periodo, bap.bolsaAuxilio.situacaoBolsa.denominacao, " +
				" bap.bolsaAuxilio.tipoBolsaAuxilio.denominacao, bap.bolsaAuxilio.discente.matricula, bap.bolsaAuxilio.discente.pessoa.nome";
		
		StringBuilder hql = new StringBuilder();
		hql.append("select ");
		hql.append(HibernateUtils.removeAliasFromProjecao(projecao));
		hql.append(" from BolsaAuxilioPeriodo bap " +
				   " inner join bap.bolsaAuxilio ba " +
				   " inner join ba.situacaoBolsa sit " +
				   " where 1 = 1 ");
		
		if ( ano != null && periodo != null  )
			hql.append(" and bap.ano = :ano and bap.periodo = :periodo ");
		if ( tipoBolsa != null && tipoBolsa > 0 ) 
			hql.append(" and bap.bolsaAuxilio.tipoBolsaAuxilio.id = :tipoBolsa ");

		hql.append(" and bap.bolsaAuxilio.situacaoBolsa.id = :situacaoTipoBolsa " +
				" order by bap.bolsaAuxilio.tipoBolsaAuxilio.denominacao, bap.bolsaAuxilio.discente.pessoa.nome");
		
		Query query = getSession().createQuery(hql.toString());
		if ( ano != null && periodo != null  ) {
			query.setInteger("ano", ano);
			query.setInteger("periodo", periodo);
		}
		if ( tipoBolsa != null && tipoBolsa > 0 )
			query.setInteger("tipoBolsa", tipoBolsa);
		
		query.setInteger("situacaoTipoBolsa", SituacaoBolsaAuxilio.BOLSA_DEFERIDA_E_CONTEMPLADA);
	
		@SuppressWarnings("unchecked")
		List<BolsaAuxilioPeriodo> lista = (List<BolsaAuxilioPeriodo>) HibernateUtils.parseTo(query.list(), projecao, BolsaAuxilioPeriodo.class, "bap");
		return lista;		
	}
	
	public void finalizarBolsistas(Collection<BolsaAuxilio> bolsasAuxilio, RegistroEntrada registro) {
		update("UPDATE sae.bolsa_auxilio SET id_situacao_bolsa = " + SituacaoBolsaAuxilio.BOLSA_FINALIZADA +" WHERE id_bolsa_auxilio in " + UFRNUtils.gerarStringIn(bolsasAuxilio));
		update("UPDATE sae.bolsa_auxilio_periodo SET data_finalizacao = now(), id_registro_finalizacao = " + registro.getId() + " WHERE id_bolsa_auxilio in " + UFRNUtils.gerarStringIn(bolsasAuxilio) );
	}

}