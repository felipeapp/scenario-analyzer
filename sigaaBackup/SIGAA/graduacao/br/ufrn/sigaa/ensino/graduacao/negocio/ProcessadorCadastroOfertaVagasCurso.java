/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.rmi.RemoteException;
import java.util.Iterator;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.graduacao.OfertaVagasCursoDao;
import br.ufrn.sigaa.ensino.dominio.CotaOfertaVagaCurso;
import br.ufrn.sigaa.ensino.dominio.OfertaVagasCurso;

/** Processador responsável pelo cadastro / alteração de ofertas de vagas
 * @author Édipo Elder F. Melo
 *
 */
public class ProcessadorCadastroOfertaVagasCurso extends AbstractProcessador {

	/** Cadastra / altera as ofertas de vagas
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		OfertaVagasCursoDao ofertaDao = getDAO(OfertaVagasCursoDao.class, mov);
		MovimentoCadastroOfertaVagasCurso movimento = (MovimentoCadastroOfertaVagasCurso) mov;
		try {
			for (OfertaVagasCurso oferta : movimento.getListaOfertaVagasCurso()) {
				// zero vagas para o curso (remover do banco)
				if (oferta.getId() != 0
						&& oferta.getVagasPeriodo1() == 0
						&& oferta.getVagasPeriodo2() == 0
						&& oferta.getVagasOciosasPeriodo1() == 0
						&& oferta.getVagasOciosasPeriodo2() == 0) {
					ofertaDao.remove(oferta);
				} else {
					// criando/atualizando as vagas
					if (oferta.getVagasPeriodo1() != 0
							|| oferta.getVagasPeriodo2() != 0
							|| oferta.getVagasOciosasPeriodo1() != 0
							|| oferta.getVagasOciosasPeriodo2() != 0) {
						oferta.setFormaIngresso(movimento.getFormaIngresso());
						oferta.setAno(movimento.getAno());
						oferta.setTotalVagas(oferta.getVagasPeriodo1() + oferta.getVagasPeriodo2());
						oferta.setTotalVagasOciosas(oferta.getVagasOciosasPeriodo1() + oferta.getVagasOciosasPeriodo2());
						// remove da lista as cotas que não possuem vagas
						Iterator<CotaOfertaVagaCurso> iterator = oferta.getCotas().iterator();
						while(iterator.hasNext()) {
							CotaOfertaVagaCurso cota = iterator.next();
							if (cota.getVagasPeriodo1() == 0 && cota.getVagasPeriodo2() == 0) {
								iterator.remove();
								ofertaDao.remove(cota);
							} else {
								cota.setTotalVagas(cota.getVagasPeriodo1() + cota.getVagasPeriodo2());
								cota.setTotalVagasOciosas(cota.getVagasOciosasPeriodo1() + cota.getVagasOciosasPeriodo2());
							}
						}
						ofertaDao.createOrUpdate(oferta);
					}
				}
			}
		} finally {
			ofertaDao.close();
		}
		return movimento;
	}

	/** Valida as vagas ofertadas
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoCadastroOfertaVagasCurso movimento = (MovimentoCadastroOfertaVagasCurso) mov;
		for (OfertaVagasCurso oferta : movimento.getListaOfertaVagasCurso()) {
			// zero vagas para o curso (remover do banco)
			if (oferta.getVagasPeriodo1() < 0 
					|| oferta.getVagasPeriodo2() < 0
					|| oferta.getVagasOciosasPeriodo1() < 0 
					|| oferta.getVagasOciosasPeriodo2() < 0) {
				throw new NegocioException(
						"Não é possível cadastrar uma vaga menor que zero");
			}
		}

	}

}
