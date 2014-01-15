/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 05/10/2006
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.sigaa.arq.dao.pesquisa.AvaliacaoProjetoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.pesquisa.dominio.AvaliacaoProjeto;
import br.ufrn.sigaa.pesquisa.dominio.Consultor;

/**
 * Processador para cadastrar Avalia��es de Projetos de Pesquisa
 * @author Ricardo Wendell
 *
 */
public class ProcessadorAvaliacaoProjetoPesquisa extends AbstractProcessador {

	/**
	 * M�todo respons�vel por processar a avalia��o do projeto de pesquisa
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		AvaliacaoProjeto avaliacao = (AvaliacaoProjeto) mov;

		// Valida��o
		validate(avaliacao);

		AvaliacaoProjetoDao  dao = getDAO(AvaliacaoProjetoDao.class, mov);
		try {
			// Cadastrar notas dadas ao projeto pelo consultor
			if ( SigaaListaComando.AVALIAR_PROJETO_PESQUISA.equals( avaliacao.getCodMovimento()) ) {

				// Persistir dados da avalia��o
				avaliacao.setSituacao(AvaliacaoProjeto.REALIZADA);
				avaliacao.calcularMedia();
				avaliacao.setDataAvaliacao(new Date());

				if ( avaliacao.getId() == 0 ) {
					avaliacao.setTipoDistribuicao( AvaliacaoProjeto.CONSULTORIA_ESPECIAL );
					dao.create(avaliacao);

					// Contabilizar avalia��o efetuada
					Consultor consultor = avaliacao.getConsultor();
					consultor = dao.findByPrimaryKey(consultor.getId(), Consultor.class);
					consultor.incrementarQtdAvaliacoes();
					dao.update(consultor);
				} else {
					dao.update(avaliacao);
				}

			} else if ( SigaaListaComando.DESISTIR_AVALIACAO_PROJETO_PESQUISA.equals( avaliacao.getCodMovimento()) ) {

				// Buscar todas as avalia��es pendentes para este consultor e marc�-las como desistentes
				Collection<AvaliacaoProjeto> pendentes = dao.findByConsultor(avaliacao.getConsultor().getId(), true);

				for (AvaliacaoProjeto pendente : pendentes) {
					pendente.setJustificativa(avaliacao.getJustificativa());
					pendente.setMedia(0.0);
					pendente.setDataAvaliacao(new Date());
					pendente.setSituacao(AvaliacaoProjeto.DESISTENTE);
					dao.update(pendente);
				}

			}
		} finally {
			dao.close();
		}

		return avaliacao;
	}

	/**
	 * M�todo respons�vel pelo valida��o do processamento da avalia��o do projeto de pesquisa.
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {

		AvaliacaoProjeto avaliacao = (AvaliacaoProjeto) mov;
		ArrayList<MensagemAviso> erros = new ArrayList<MensagemAviso>();

		if ( SigaaListaComando.AVALIAR_PROJETO_PESQUISA.equals( avaliacao.getCodMovimento()) ) {



		} else if ( SigaaListaComando.DESISTIR_AVALIACAO_PROJETO_PESQUISA.equals( avaliacao.getCodMovimento()) ) {
			if ( avaliacao.getJustificativa() == null || avaliacao.getJustificativa().trim().length() == 0 ) {
				erros.add(new MensagemAviso("No caso da desist�ncia da avalia��o dos projetos, � necess�rio informar uma justificativa",
						TipoMensagemUFRN.ERROR));
			}
		}

		checkValidation(erros);
	}

}
