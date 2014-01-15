package br.ufrn.sigaa.assistencia.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.assistencia.dominio.RestricaoSolicitacaoBolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.SituacaoBolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.TipoBolsaAuxilio;

public class RestricaoSolicitacaoBolsaAuxilioDao extends GenericSigaaDAO {

	@SuppressWarnings("unchecked")
	public Collection<RestricaoSolicitacaoBolsaAuxilio> findAllRestricoes(TipoBolsaAuxilio tipoBolsa) throws HibernateException, DAOException {

		StringBuilder sql = new StringBuilder(
			" select rs.id_restricao_solicitacao_bolsa_auxilio, tba.id_tipo_bolsa_auxilio, tba.denominacao, rs.id_bolsa_auxilio_restricao, " +
			" tba1.denominacao as restricao, situa.id_situacao_bolsa_auxilio, situa.denominacao as situacao, rs.data_cadastro, rs.id_registro_cadastro" +
			" from sae.restricao_solicitacao rs " +
			" join sae.tipo_bolsa_auxilio tba using ( id_tipo_bolsa_auxilio )" +
			" join sae.tipo_bolsa_auxilio tba1 on ( rs.id_bolsa_auxilio_restricao = tba1.id_tipo_bolsa_auxilio ) " +
			" join sae.situacao_bolsa_auxilio situa on ( rs.id_situacao = situa.id_situacao_bolsa_auxilio ) " +
			" where rs.ativo = trueValue() and rs.id_tipo_bolsa_auxilio = :tipoBolsa" +
			" order by tba1.denominacao, situa.denominacao");	

		Query q = getSession().createSQLQuery(sql.toString());
		q.setInteger("tipoBolsa", tipoBolsa.getId());
		Collection<Object[]> bulk = q.list();
		Collection<RestricaoSolicitacaoBolsaAuxilio> result = new ArrayList<RestricaoSolicitacaoBolsaAuxilio>();
		for (Object[] obj : bulk) {
			int count = 0;
			RestricaoSolicitacaoBolsaAuxilio linha = new RestricaoSolicitacaoBolsaAuxilio();
			linha.setId( (Integer) obj[count++] );
			linha.setTipoBolsaAuxilio(new TipoBolsaAuxilio((Integer) obj[count++]));
			linha.getTipoBolsaAuxilio().setDenominacao((String) obj[count++]);

			linha.setBolsasAuxilioRestricao(new TipoBolsaAuxilio((Integer) obj[count++]));
			linha.getBolsasAuxilioRestricao().setDenominacao((String) obj[count++]);
			linha.setSituacao(new SituacaoBolsaAuxilio((Integer) obj[count++]));
			linha.getSituacao().setDenominacao((String) obj[count++]);
			
			linha.setDataCadastro((Date) obj[count++] );
			linha.setRegistroCadastro(new RegistroEntrada());
			linha.getRegistroCadastro().setId((Integer) obj[count++]);
			
			result.add(linha);
		}
		
		return result;
	}
	
}