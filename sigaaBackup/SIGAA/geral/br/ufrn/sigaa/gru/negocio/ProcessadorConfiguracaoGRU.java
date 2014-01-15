/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 16/08/2012
 *
 */
package br.ufrn.sigaa.gru.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.gru.dominio.ConfiguracaoGRU;

/** Processador responsável pela persistência de uma {@link ConfiguracaoGRU Configuração de GRU}
 * @author Édipo Elder F. de Melo
 *
 */
public class ProcessadorConfiguracaoGRU extends AbstractProcessador{

	/** Executa a persistÊncia da {@link ConfiguracaoGRU}
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);
		ConfiguracaoGRU config = ((MovimentoCadastro) mov).getObjMovimentado();
		GenericDAO dao = DAOFactory.getGeneric(Sistema.COMUM);
		try {
			dao.createOrUpdate(config);
		} finally {
			dao.close();
		}
		return config;
	}

	/** Valida os dados da {@link ConfiguracaoGRU}
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		ListaMensagens lista = new ListaMensagens();
		ConfiguracaoGRU config = ((MovimentoCadastro) mov).getObjMovimentado();
		validateRequired(config.getDescricao(), "Descrição", lista);
		validateRequired(config.getGrupoEmissaoGRU(), "Grupo de Emissao da GRU", lista);
		validateRequired(config.getTipoArrecadacao(), "Tipo de Arrecadacao", lista);
		validateRequired(config.getUnidade(), "Unidade", lista);
		if (config.isAtivo()) {
			GenericDAO dao = DAOFactory.getGeneric(Sistema.COMUM);
			try {
				String[] fields = {"grupoEmissaoGRU.id", "tipoArrecadacao.id", "unidade.id", "ativo"};
				Object[] values = {config.getGrupoEmissaoGRU().getId(), config.getTipoArrecadacao().getId(), config.getUnidade().getId(), true};
				Collection<ConfiguracaoGRU> configs = dao.findByExactField(ConfiguracaoGRU.class, fields, values);
				if (!isEmpty(configs)) {
					for (ConfiguracaoGRU configDB : configs)
						if (config.getId() != configDB.getId())
							lista.addErro("Existe outra configuração ativa com o mesmo Grupo de Emissao da GRU, Tipo de Arrecadacao e Unidade. Por favor inative a anterior ou informe outros valores.");
				}
			} finally {
				dao.close();
			}
		}
		checkValidation(lista);
	}

}
