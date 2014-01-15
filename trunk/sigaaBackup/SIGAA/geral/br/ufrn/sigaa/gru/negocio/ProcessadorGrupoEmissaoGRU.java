/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
import br.ufrn.comum.gru.dominio.GrupoEmissaoGRU;

/** Processador respons�vel pela persist�ncia de um {@link GrupoEmissaoGRU Grupo de Emiss�o de GRU}. 
 * @author �dipo Elder F. de Melo
 *
 */
public class ProcessadorGrupoEmissaoGRU extends AbstractProcessador{

	/** Persiste o {@link GrupoEmissaoGRU}
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);
		GrupoEmissaoGRU config = ((MovimentoCadastro) mov).getObjMovimentado();
		GenericDAO dao = DAOFactory.getGeneric(Sistema.COMUM);
		try {
			dao.createOrUpdate(config);
		} finally {
			dao.close();
		}
		return config;
	}

	/** Valida os dados para a persist�ncia do {@link GrupoEmissaoGRU}
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		ListaMensagens lista = new ListaMensagens();
		GrupoEmissaoGRU grupo = ((MovimentoCadastro) mov).getObjMovimentado();
		boolean gruSimples = (Boolean) ((MovimentoCadastro) mov).getObjAuxiliar();
		if (gruSimples) {
			validateRequired(grupo.getCodigoGestao(), "C�digo da Gest�o", lista);
			validateRequired(grupo.getCodigoUnidadeGestora(), "C�digo da Unidade Gestora", lista);
			if (grupo.isAtivo()) {
				GenericDAO dao = DAOFactory.getGeneric(Sistema.COMUM);
				try {
					String[] fields = {"codigoGestao", "codigoUnidadeGestora", "ativo"};
					Object[] values = {grupo.getCodigoGestao(), grupo.getCodigoUnidadeGestora(), true};
					Collection<GrupoEmissaoGRU> configs = dao.findByExactField(GrupoEmissaoGRU.class, fields, values);
					if (!isEmpty(configs)) {
						for (GrupoEmissaoGRU grupoDB : configs)
							if (grupo.getId() != grupoDB.getId())
								lista.addErro("Existe outro Grupo de Emiss�o de GRU ativo com o mesmo valor. Por favor inative o anterior ou informe outros valores.");
					}
				} finally {
					dao.close();
				}
			}
		} else {
			validateRequired(grupo.getAgencia(), "Ag�ncia", lista);
			validateRequired(grupo.getCodigoCedente(), "C�digo Cedente", lista);
			validateRequired(grupo.getConvenio(), "N�mero do Conv�nio", lista);
			if (grupo.isAtivo()) {
				GenericDAO dao = DAOFactory.getGeneric(Sistema.COMUM);
				try {
					String[] fields = {"agencia", "codigoCedente", "convenio", "ativo"};
					Object[] values = {grupo.getAgencia(), grupo.getCodigoCedente(), grupo.getConvenio(), true};
					Collection<GrupoEmissaoGRU> configs = dao.findByExactField(GrupoEmissaoGRU.class, fields, values);
					if (!isEmpty(configs)) {
						for (GrupoEmissaoGRU grupoDB : configs)
							if (grupo.getId() != grupoDB.getId())
								lista.addErro("Existe outro Grupo de Emiss�o de GRU ativo com o mesmo valor. Por favor inative o anterior ou informe outros valores.");
					}
				} finally {
					dao.close();
				}
			}
		}
		checkValidation(lista);
	}

}
