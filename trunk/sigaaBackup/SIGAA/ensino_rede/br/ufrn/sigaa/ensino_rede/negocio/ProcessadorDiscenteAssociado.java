/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 26/08/2013
 *
 */
package br.ufrn.sigaa.ensino_rede.negocio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.negocio.PessoaMov;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino_rede.dominio.AlteracaoDiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.dominio.AlteracaoStatusDiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.dominio.DiscenteAssociado;
import br.ufrn.sigaa.negocio.ProcessadorPessoa;

public class ProcessadorDiscenteAssociado  extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);
		DiscenteAssociado discente = ((MovimentoCadastro) mov).getObjMovimentado();
		GenericDAO dao = getGenericDAO(mov);
		try {
			// persiste dados pessoais (somente se cadastrando discente)
			if (mov.getCodMovimento() == SigaaListaComando.CADASTRAR_DISCENTE_REDE)
				gravarDadosPessoais(mov);
			if (mov.getCodMovimento() == SigaaListaComando.ALTERAR_DISCENTE_REDE) {
				registraAlteracaoDiscente(mov);
			}
			dao.createOrUpdate(discente);
		} finally {
			dao.close();
		}
		return discente;
	}

	private void registraAlteracaoDiscente(Movimento mov) throws DAOException {
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		DiscenteAssociado discente = movimento.getObjMovimentado();
		GenericDAO dao = getGenericDAO(mov);
		try {
			DiscenteAssociado discenteDB = dao.findByPrimaryKey(discente.getId(), DiscenteAssociado.class);
			if (discenteDB != null) {
				// se houve alteração de status
				if (discenteDB.getStatus() != discente.getStatus()) {
					AlteracaoStatusDiscenteAssociado alteracaoStatus = new AlteracaoStatusDiscenteAssociado();
					alteracaoStatus.setDiscente(discente);
					alteracaoStatus.setEntrada(movimento.getRegistroEntrada());
					alteracaoStatus.setMovimento(movimento.getCodMovimento().getId());
					alteracaoStatus.setStatus(discenteDB.getStatus().getId());
					alteracaoStatus.setUsuario((Usuario) movimento.getUsuarioLogado());
					dao.create(alteracaoStatus);
				}
				AlteracaoDiscenteAssociado alteracao = new AlteracaoDiscenteAssociado();
				alteracao.populaDiferencas(discenteDB, discente);
				if (alteracao.isHouveAlteracao()) {
					alteracao.setRegistroEntrada(movimento.getRegistroEntrada());
					alteracao.setCodigoMovimento(movimento.getCodMovimento().getId());
					alteracao.setData(new Date());
					dao.create(alteracao);
				}
			}
		} finally {
			dao.close();
		}
	}

	private void gravarDadosPessoais(Movimento mov) throws NegocioException, ArqException, RemoteException {
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		DiscenteAssociado discente = movimento.getObjMovimentado();
		PessoaMov pessoaMov = new PessoaMov();
		if (discente.getPessoa().getId() == 0)
			pessoaMov.setCodMovimento(SigaaListaComando.CADASTRAR_PESSOA);
		else
			pessoaMov.setCodMovimento(SigaaListaComando.ALTERAR_PESSOA);
		pessoaMov.setPessoa(discente.getPessoa());
		pessoaMov.setUsuarioLogado(movimento.getUsuarioLogado());
		pessoaMov.setRegistroEntrada(movimento.getRegistroEntrada());
		pessoaMov.setSistema(movimento.getSistema());
		ProcessadorPessoa proc = new ProcessadorPessoa();
		proc.execute(pessoaMov);
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		DiscenteAssociado discente = ((MovimentoCadastro) mov).getObjMovimentado();
		ListaMensagens lista = new ListaMensagens();
		validateRequired(discente.getDadosCurso(), "Dados do Curso", lista);
		validateRequired(discente.getAnoIngresso(), "Ano de Ingresso", lista);
		validateRequired(discente.getPeriodoIngresso(), "Período de Ingresso", lista);
		validateRequired(discente.getStatus(), "Status", lista);	
		validateRequired(discente.getNivel(), "Nível de Ensino", lista);
		checkValidation(lista);
	}

}
