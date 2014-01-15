package br.ufrn.sigaa.bolsas.negocio;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.integracao.dto.TipoBolsaDTO;
import br.ufrn.sigaa.arq.dao.sae.BolsaAuxilioDao;
import br.ufrn.sigaa.assistencia.dao.DesempenhoAcademicoBolsistaDao;
import br.ufrn.sigaa.assistencia.dominio.TipoBolsaAuxilio;
import br.ufrn.sigaa.bolsas.dominio.IntegracaoTipoBolsa;
import br.ufrn.sigaa.pessoa.dominio.Discente;

public class IntegracaoTipoBolsaHelper {

	private static HashMap<Integer, String> bolsasSipac = null;
	private static HashMap<Integer, String> bolsasSigaa = null;
	private static Collection<IntegracaoTipoBolsa> tipoBolsa = null;

	public static int getBolsaSIPAC( TipoBolsaAuxilio tipoBolsaAuxilio, Discente discente ) throws DAOException {
		carregarTiposBolsasIntegracao();
		int bolsaSipac = 0;
		for (IntegracaoTipoBolsa bolsa : tipoBolsa) {
			if ( tipoBolsaAuxilio.getId() == bolsa.getIdBolsaSigaa() ) {
				if ( bolsa.getUf().equals(discente.getCurso().getMunicipio().getUnidadeFederativa().getSigla()) ) {
					bolsaSipac = bolsa.getIdBolsaSipac();
					if ( !bolsa.getMunicipio().isEmpty() && discente.getCurso().getMunicipio().getNome().equals(bolsa.getMunicipio()) ) {
						bolsaSipac = bolsa.getIdBolsaSipac();
					}
				}
			}
		}
		return bolsaSipac;
	}

	private static void carregarTiposBolsasIntegracao() throws DAOException {
		BolsaAuxilioDao dao = DAOFactory.getInstance().getDAO(BolsaAuxilioDao.class);
		try {
			tipoBolsa = dao.findAll(IntegracaoTipoBolsa.class);
		} finally {
			dao.close();
		}
	}
	
	public static HashMap<Integer, String> bolsasSigaa() throws DAOException {
		if ( bolsasSigaa == null ) {
			bolsasSigaa = new HashMap<Integer, String>();
			BolsaAuxilioDao dao = DAOFactory.getInstance().getDAO(BolsaAuxilioDao.class);
			try {
				Collection<TipoBolsaAuxilio> bolsas = dao.findAll(TipoBolsaAuxilio.class);
				for (TipoBolsaAuxilio tipoBolsa : bolsas) {
					bolsasSigaa.put(tipoBolsa.getId(), tipoBolsa.getDenominacao());
				}
			} finally {
				dao.close();
			}
		}
		return bolsasSigaa;
	}

	public static HashMap<Integer, String> bolsasSipac() {
		if ( bolsasSipac == null ) {
			bolsasSipac = new HashMap<Integer, String>();
			DesempenhoAcademicoBolsistaDao dao = DAOFactory.getInstance().getDAO(DesempenhoAcademicoBolsistaDao.class);
			try {
				List<TipoBolsaDTO> bolsas = dao.findAllTipoBolsa();
				for (TipoBolsaDTO tipoBolsaDTO : bolsas) {
					bolsasSipac.put(tipoBolsaDTO.getId(), tipoBolsaDTO.getDenominacao());
				}
			} finally {
				dao.close();
			}
		}
		return bolsasSipac;
	}

}