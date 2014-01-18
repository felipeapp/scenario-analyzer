package br.ufrn.sigaa.assistencia.jsf.helper;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.sigaa.arq.dao.sae.RenovacaoBolsaAuxilioDao;
import br.ufrn.sigaa.assistencia.dao.AnoPeriodoReferenciaSAEDao;
import br.ufrn.sigaa.assistencia.dominio.AnoPeriodoReferenciaSAE;
import br.ufrn.sigaa.pessoa.dominio.Discente;

public class RenovacaoBolsaHelper {

	public static void verificarNecessidadeRenovacao(Discente discente, ListaMensagens lista) throws ArqException {

		RenovacaoBolsaAuxilioDao dao = DAOFactory.getInstance().getDAO(RenovacaoBolsaAuxilioDao.class);
		AnoPeriodoReferenciaSAEDao anoPeriodoRefDao = DAOFactory.getInstance().getDAO(AnoPeriodoReferenciaSAEDao.class);
		
		try {
			AnoPeriodoReferenciaSAE anoPeriodoRef = anoPeriodoRefDao.anoPeriodoVigente();
			Boolean haBolsaPassivelRenovacao = dao.countAllBolsaPassivelRenovacao(discente.getId(), anoPeriodoRef);
			Boolean desejaRenovarBolsa = dao.desejaRenovarBolsa(discente, anoPeriodoRef.getAno(), anoPeriodoRef.getPeriodo());
			
			if ( haBolsaPassivelRenovacao && desejaRenovarBolsa ) {
				lista.addErro("O s� � poss�vel realizar matr�cula depois da solicita��o de renova��o da bolsa. Para renovar a bolsa siga o caminho: " + 
					RepositorioDadosInstitucionais.get("siglaSigaa") + " -> Portal do Discente -> Bolsas -> Solicita��o de Bolsas -> Renovar Bolsa Aux�lio.");
			}
			
		} finally {
			dao.close();
			anoPeriodoRefDao.close();
		}
		
	}

}