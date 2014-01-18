package br.ufrn.sigaa.arq.dao.sae;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.assistencia.dominio.AnoPeriodoReferenciaSAE;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.SituacaoBolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.TipoBolsaAuxilio;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Discente;

public class RenovacaoBolsaAuxilioDao extends GenericSigaaDAO {
	
	@SuppressWarnings("unchecked")
	public List<BolsaAuxilio> findAllBolsaPassivelRenovacao(int idDiscente, AnoPeriodoReferenciaSAE anoPeriodoRef, boolean renovacao) throws DAOException {
		try {
			int anoPeriodo = Integer.parseInt(anoPeriodoRef.getAnoAlimentacao() + "" + anoPeriodoRef.getPeriodoAlimentacao());
			String sql = "SELECT ba.id_bolsa_auxilio, tba.id_tipo_bolsa_auxilio, tba.denominacao as tipo_bolsa, " +
					"  sba.id_situacao_bolsa_auxilio, sba.denominacao as situacao_bolsa, bap.ano, bap.periodo " +
					" FROM sae.bolsa_auxilio ba" +
					" JOIN sae.bolsa_auxilio_periodo bap using ( id_bolsa_auxilio )" +
					" JOIN sae.criterio_solicitacao_renovacao csr on (" +
					" 	csr.id_tipo_bolsa_auxilio = ba.id_tipo_bolsa_auxilio and csr.id_situacao_bolsa = ba.id_situacao_bolsa )" +
					" JOIN sae.tipo_renovacao tr on ( tr.id_tipo_renovacao_bolsa = csr.tipo_renovacao )" +
					" JOIN sae.tipo_bolsa_auxilio tba on ( ba.id_tipo_bolsa_auxilio = tba.id_tipo_bolsa_auxilio ) " +
					" JOIN sae.situacao_bolsa_auxilio sba on ( sba.id_situacao_bolsa_auxilio = ba.id_situacao_bolsa ) " +
					" WHERE ba.id_discente = " + idDiscente + 
					" and ba.solicitada_renovacao is null " +
					" AND cast(" + anoPeriodo + " - cast((bap.ano ||''|| bap.periodo) as integer) as text) SIMILAR TO '('|| tr.condicao ||')' "; 
			
			Query q = getSession().createSQLQuery(sql);
			List<Object[]> lista = q.list();
			List<BolsaAuxilio> resultado = new ArrayList<BolsaAuxilio>();
			for (Iterator<Object[]> iterator = lista.iterator(); iterator.hasNext();) {
				int count  = 0;
				Object[] objects = iterator.next();
				BolsaAuxilio linha = new BolsaAuxilio();
				linha.setId( (Integer) objects[count++]);
				linha.setTipoBolsaAuxilio(new TipoBolsaAuxilio((Integer) objects[count++]));
				linha.getTipoBolsaAuxilio().setDenominacao( (String) objects[count++] );
				linha.setSituacaoBolsa(new SituacaoBolsaAuxilio((Integer) objects[count++]));
				linha.getSituacaoBolsa().setDenominacao( (String) objects[count++] );
				linha.setAno((Integer) objects[count++]);
				linha.setPeriodo((Integer) objects[count++]);
				linha.setRenovacao(renovacao);
				resultado.add(linha);
			}
			
			return resultado;
			
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	public boolean countAllBolsaPassivelRenovacao(int idDiscente, AnoPeriodoReferenciaSAE anoPeriodoRef ) throws DAOException {
		return findAllBolsaPassivelRenovacao(idDiscente, anoPeriodoRef, true).size() > 0;
	}
	
	public void registrarResposta(Usuario usuario, int ano, int periodo, boolean solicitadoRenovacao) {
		update("INSERT INTO sae.registro_visualizao_renovacao(id_registro_visualizao_renovacao, data_cadastro, id_registro_cadastro, " +
				" ano, periodo, id_discente, solicitado_renovacao) VALUES(nextval('sae.registro_visualizao_renovacao_seq'), now(), ?, ?, ?, ?, ?) ", 
						new Object[] { usuario.getRegistroEntrada().getId(), ano, periodo, usuario.getDiscente().getId(), solicitadoRenovacao });
	}

	public boolean jaRespondido(Discente discente, int ano, int periodo) throws DAOException {
		Query q = getSession().createSQLQuery(
				" select count(id_registro_visualizao_renovacao) " +
				" from sae.registro_visualizao_renovacao " +
				" where ano = :ano and periodo = :periodo " +
				" and id_discente = :discente and ativo = trueValue()");
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		q.setInteger("discente", discente.getId());
		return ((BigInteger) q.uniqueResult()).intValue() > 0;
	}

	public boolean desejaRenovarBolsa(Discente discente, int ano, int periodo) throws DAOException {
		Query q = getSession().createSQLQuery(
				" select solicitado_renovacao " +
				" from sae.registro_visualizao_renovacao " +
				" where ano = :ano and periodo = :periodo " +
				" and id_discente = :discente and ativo = trueValue()");
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		q.setInteger("discente", discente.getId());
		Boolean solicitado = (Boolean) q.uniqueResult();
		return solicitado != null ? solicitado : false;
	}
	
}