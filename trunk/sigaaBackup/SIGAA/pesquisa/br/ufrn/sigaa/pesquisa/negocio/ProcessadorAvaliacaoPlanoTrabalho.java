/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 10/10/2006
 *
 */

package br.ufrn.sigaa.pesquisa.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pesquisa.dominio.HistoricoPlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;

/**
 * Processador responsável por persistir a avaliação dada por um consultor a
 * um plano de trabalho
 *
 * @author ricardo
 *
 */
public class ProcessadorAvaliacaoPlanoTrabalho extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException,
		RemoteException {

		PlanoTrabalho planoTrabalho = (PlanoTrabalho) mov;

		GenericDAO dao = getDAO(mov);
		PlanoTrabalho planoTrabalhoBanco = planoTrabalho;
		try {
			Usuario usuario = (Usuario) mov.getUsuarioLogado();

			planoTrabalhoBanco = dao.findByPrimaryKey(planoTrabalho.getId(), PlanoTrabalho.class);

			planoTrabalhoBanco.setParecerConsultor(planoTrabalho.getParecerConsultor());
			planoTrabalhoBanco.setStatus(planoTrabalho.getStatus());
			planoTrabalhoBanco.setDataAvaliacao(new Date());

			if (usuario.getConsultor() != null) {
				planoTrabalhoBanco.setConsultor(usuario.getConsultor());
			}

			dao.update(planoTrabalhoBanco);

			// Gravar histórico
			HistoricoPlanoTrabalho historico = new HistoricoPlanoTrabalho();
			historico.setPlanoTrabalho(planoTrabalhoBanco);
			historico.setStatus(planoTrabalho.getStatus());
			historico.setData(new Date());
			historico.setRegistroEntrada( mov.getUsuarioLogado().getRegistroEntrada() );
			historico.setTipoBolsa( isEmpty( planoTrabalho.getTipoBolsa() ) ? 
					planoTrabalhoBanco.getTipoBolsa() : planoTrabalho.getTipoBolsa() );
			dao.create(historico);

		} finally {
			dao.close();
		}

		return planoTrabalhoBanco;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
	}

}
