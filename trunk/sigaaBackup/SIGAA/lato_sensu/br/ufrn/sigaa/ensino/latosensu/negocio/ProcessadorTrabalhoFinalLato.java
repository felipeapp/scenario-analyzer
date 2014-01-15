/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '24/04/2007'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.latosensu.dominio.TrabalhoFinalLato;

/**
 * Processador para cadastrar/alterar/remover trabalhos finais do lato sensu
 *
 * @author Leonardo
 *
 */
public class ProcessadorTrabalhoFinalLato extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		// Verificar papeis
		checkRole(new int[] { SigaaPapeis.GESTOR_LATO,
				SigaaPapeis.COORDENADOR_LATO }, mov);

		// Validar
		validate(mov);

		MovimentoTrabalhoFinalLato movTrabalhoFinal = (MovimentoTrabalhoFinalLato) mov;
		TrabalhoFinalLato trabalhoFinal = movTrabalhoFinal.getTrabalhoFinal();

		if (movTrabalhoFinal.getCodMovimento() == SigaaListaComando.SUBMETER_TRABALHO_FINAL_LATO) {
			if (movTrabalhoFinal.getNome() != null) {
				// Salvar Arquivo
				try {
					if (trabalhoFinal.getIdArquivo() == null
							|| trabalhoFinal.getIdArquivo() == 0) {
						// Buscar um id para o arquivo
						trabalhoFinal.setIdArquivo(EnvioArquivoHelper
								.getNextIdArquivo());
					}

					// Gravar arquivo do edital
					EnvioArquivoHelper.inserirArquivo(trabalhoFinal
							.getIdArquivo(),
							movTrabalhoFinal.getDadosArquivo(),
							movTrabalhoFinal.getContentType(), movTrabalhoFinal
									.getNome());
				} catch (Exception e) {
					throw new ArqException(e);
				}
			}
			// Preparar trabalho final para ser armazenado

			GenericDAO dao = getDAO(mov);
			try {
				if (trabalhoFinal.getId() == 0) {
					dao.create(trabalhoFinal);
				} else {
					dao.update(trabalhoFinal);
				}
			} finally {
				dao.close();
			}
		} else if (movTrabalhoFinal.getCodMovimento() == SigaaListaComando.REMOVER_TRABALHO_FINAL_LATO) {
			// Remover Arquivo

			EnvioArquivoHelper.removeArquivo(trabalhoFinal.getIdArquivo());

			// Remove Trabalho Final
			GenericDAO dao = getDAO(mov);
			try {
				dao.remove(trabalhoFinal);
			} finally {
				dao.close();
			}
		}

		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoTrabalhoFinalLato movTrabalhoFinal = (MovimentoTrabalhoFinalLato) mov;
		TrabalhoFinalLato trabalhoFinal = movTrabalhoFinal.getTrabalhoFinal();
		ArrayList<MensagemAviso> erros = new ArrayList<MensagemAviso>();

		TrabalhoFinalLatoValidator.validaTrabalhoFinal(null, trabalhoFinal,
				erros);

		// Valida o arquivo do trabalho final
		if (trabalhoFinal.getId() == 0 && movTrabalhoFinal.getNome() == null)
			erros
					.add(new MensagemAviso(
							"Para cadastrar um novo trabalho final é obrigatório informar"
									+ "o arquivo do trabalho.",
							TipoMensagemUFRN.ERROR));

		checkValidation(erros);
	}

}
