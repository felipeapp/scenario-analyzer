/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 19/07/2007
 *
 */

package br.ufrn.sigaa.pesquisa.negocio;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.pesquisa.CotaDocenteDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.pesquisa.dominio.CotaDocente;
import br.ufrn.sigaa.pesquisa.dominio.Cotas;

/**
 * Processador responsável pela distribuição e ajuste de cotas de bolsas
 * de iniciação científica destinadas aos docentes
 *
 * @author Ricardo Wendell
 *
 */
public class ProcessadorDistribuicaoCotasDocente extends AbstractProcessador {

	/* 
	 * @see br.ufrn.arq.ejb.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		// Verificar permissões
		checkRole(SigaaPapeis.GESTOR_PESQUISA, mov);

		MovimentoDistribuicaoCotasDocentes movDistribuicao = (MovimentoDistribuicaoCotasDocentes) mov;

		// Realizar operação desejada
		if (movDistribuicao.getCodMovimento().equals(SigaaListaComando.DISTRIBUIR_COTAS_PESQUISA)) {
			distribuirCotas(movDistribuicao);
		}
		else if (movDistribuicao.getCodMovimento().equals(SigaaListaComando.AJUSTAR_COTAS_PESQUISA)) {
			ajustarCotas(movDistribuicao);
		}
		else if (movDistribuicao.getCodMovimento().equals(SigaaListaComando.ADICIONAR_COTA_DOCENTE)) {
			adicionarDocente(movDistribuicao);
		}


		return null;
	}

	private void distribuirCotas(MovimentoDistribuicaoCotasDocentes movDistribuicao) throws DAOException {

	    Collection<CotaDocente> cotasDocentes = movDistribuicao.getCotasDocentes();
	    
	    // DAOs
        CotaDocenteDao cotaDao = getDAO(CotaDocenteDao.class, movDistribuicao);
	    
		// Persistir distribuição de cotas
		try {
			for (CotaDocente cota : cotasDocentes ) {
				cota.setRegistroEntrada(movDistribuicao.getUsuarioLogado().getRegistroEntrada());
				cota.setDataConcessao(new Date());
				cotaDao.createNoFlush(cota);
			}
		} finally {
			cotaDao.close();
		}

	}

	private void ajustarCotas(MovimentoDistribuicaoCotasDocentes movDistribuicao) throws NegocioException, ArqException {

		Collection<CotaDocente> cotas = movDistribuicao.getCotasDocentes();

		// Validar distribuição
		validate(movDistribuicao);

		// DAOs
		CotaDocenteDao cotaDao = getDAO(CotaDocenteDao.class, movDistribuicao);

		for (CotaDocente cota : cotas) {
			cotaDao.updateNoFlush(cota);
		}
		cotaDao.close();
	}

	private void adicionarDocente(MovimentoDistribuicaoCotasDocentes movDistribuicao) throws DAOException, NegocioException {
		CotaDocente cotaDocente = movDistribuicao.getCotaDocente();

		CotaDocenteDao cotaDocenteDao = getDAO(CotaDocenteDao.class, movDistribuicao);

		try {
			if ( cotaDocenteDao.findByDocenteAndEdital(cotaDocente.getDocente(), cotaDocente.getEdital()) != null) {
				NegocioException ne = new NegocioException();
				ne.addErro("O docente informado já está cadastrado para esta distribuição");
				throw ne;
			}

			cotaDocente.setRegistroEntrada( movDistribuicao.getUsuarioLogado().getRegistroEntrada() );
			cotaDocente.setDataConcessao( new Date() );
			
			for(Cotas c: cotaDocente.getEdital().getCotas()){
				Cotas cNova = new Cotas();
				cNova.setTipoBolsa(c.getTipoBolsa());
				cNova.setQuantidade(0);
				cotaDocente.addCotas(cNova);
			}

			cotaDocenteDao.create( cotaDocente );
		} finally {
			cotaDocenteDao.close();
		}
	}

	/* 
	 * @see br.ufrn.arq.ejb.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {

	}

}
