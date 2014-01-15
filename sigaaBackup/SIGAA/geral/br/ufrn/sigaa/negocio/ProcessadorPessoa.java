/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/10/2007
 *
 */
package br.ufrn.sigaa.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.sincronizacao.SincronizadorPessoas;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.negocio.PessoaMov;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.util.PessoaHelper;
import br.ufrn.sigaa.pessoa.dominio.ContaBancaria;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Classe responsável em processar o movimento relacionado a Pessoa.
 * 
 * @author Andre M Dantas
 */
public class ProcessadorPessoa extends ProcessadorCadastro {

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		PessoaDao dao = getDAO(PessoaDao.class, mov);
		try {
			if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_PESSOA) || mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_PESSOA)) {
				PessoaMov pMov = (PessoaMov) mov;
				ListaMensagens erros = new ListaMensagens();
				Pessoa pessoa = pMov.getPessoa();
				
				PessoaValidator.validarDadosPessoais(pessoa, null, pMov.getTipoValidacao(), erros);
				if (pessoa.isPJ())
					PessoaValidator.validarDadosPJ(pMov.getPessoaJuridica(), null, erros);
				
				if (!pessoa.isInternacional() && pessoa.getCpf_cnpj() != null && pessoa.getCpf_cnpj() > 0 && dao.existePessoa(pessoa) 
						&& (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_PESSOA) || mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_PESSOA))) {
					erros.getMensagens().add(new MensagemAviso("Já existe uma pessoa cadastrada com este " + ((pessoa.isPF())?"CPF":"CNPJ"),
							TipoMensagemUFRN.ERROR));
				}
				checkValidation(erros);
				
				anularTransientObjects(pessoa);
			}
		} finally {
			dao.close();
		}
	}

	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {

		validate(movimento);
		PessoaMov pMov = (PessoaMov) movimento;
//		checando nulos
		if (pMov.getPessoa().getTipoRaca() != null && pMov.getPessoa().getTipoRaca().getId() == 0)
			pMov.getPessoa().setTipoRaca(null);
		if (pMov.getPessoa().getEstadoCivil() != null && pMov.getPessoa().getEstadoCivil().getId() == 0)
			pMov.getPessoa().setEstadoCivil(null);
		if( (pMov.getPessoa().getCpf_cnpj() != null) && (pMov.getPessoa().getCpf_cnpj() == 0) )
			pMov.getPessoa().setCpf_cnpj(null);
		if (pMov.getPessoa().getUnidadeFederativa() != null && pMov.getPessoa().getUnidadeFederativa().getId() == 0) {
			pMov.getPessoa().setUnidadeFederativa(null);
		}

		GenericDAO dao = getGenericDAO(pMov);
		PersistDB objMovimentado = null;
		try {

			if (pMov.getPessoa().isPF()) {
				if (pMov.getPessoa().getId() == 0)
					pMov.getPessoa().setDataCadastro(new Date());
				pMov.getPessoa().setUltimaAtualizacao(new Date());
				objMovimentado = pMov.getPessoa();
			} else {
				objMovimentado = pMov.getPessoaJuridica();
				// objMovimentado =  dao.findByPrimaryKey(objMovimentado.getId(), PessoaJuridica.class);
			}

			pMov.setObjMovimentado(objMovimentado);
			if (pMov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_PESSOA)) {
				persistirContaBancaria( (Pessoa) objMovimentado, dao);
				
				if (objMovimentado.getId() == 0) {
					int idPessoa = SincronizadorPessoas.getNextIdPessoa();
					objMovimentado.setId(idPessoa);
				}
				
		    	dao.create(objMovimentado);
			} else if (pMov.getCodMovimento().equals(SigaaListaComando.ALTERAR_PESSOA)) {

				// gravando histórico dos dados pessoais originais
				PessoaHelper.alteraCriaPessoa( (Pessoa) objMovimentado, dao, movimento.getUsuarioLogado().getRegistroEntrada(), movimento.getCodMovimento().getId() );
				/*try {
				} catch (NegocioException e) {
					e.printStackTrace();
					throw e;
				}*/

				persistirContaBancaria( (Pessoa) objMovimentado, dao);
				dao.update(objMovimentado);

				
				if (!((Pessoa) objMovimentado).isInternacional()){
					int idPessoaAntesSincronizacao = objMovimentado.getId();
					
					/* Sincroniza nome, sexo, data de nascimento e Email com os outros bancos */
					SincronizadorPessoas.usandoSistema(movimento, Sistema.COMUM).sincronizarPessoa((Pessoa) objMovimentado);
					SincronizadorPessoas.usandoSistema(movimento, Sistema.getSistemaAdministrativoAtivo()).sincronizarPessoa((Pessoa) objMovimentado);
					
					if(objMovimentado.getId() != idPessoaAntesSincronizacao)
						objMovimentado.setId(idPessoaAntesSincronizacao);
				}
				
			} else if (pMov.getCodMovimento().equals(SigaaListaComando.REMOVER_PESSOA)) {
				anularTransientObjects(pMov.getPessoa());
				remover(pMov);
			}
		} catch (NegocioException e) {
			Pessoa p = (Pessoa) objMovimentado;
			if( p != null && p.getContaBancaria() != null && p.getContaBancaria().getId() > 0 )
				p.getContaBancaria().setId(0);
			if (p != null && pMov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_PESSOA) && p.getId() > 0)
				p.setId(0);
			if(p != null && p.getEnderecoContato() != null && p.getEnderecoContato().getId() > 0)
				p.getEnderecoContato().setId(0);

			e.printStackTrace();
			throw e;
		} catch( Exception e ){
			Pessoa p = (Pessoa) objMovimentado;
			if( p != null && p.getContaBancaria() != null && p.getContaBancaria().getId() > 0 )
				p.getContaBancaria().setId(0);
			if (p != null && pMov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_PESSOA) && p.getId() > 0)
				p.setId(0);
			if(p != null && p.getEnderecoContato() != null && p.getEnderecoContato().getId() > 0)
				p.getEnderecoContato().setId(0);

			throw new ArqException(e);
		}finally {
			dao.close();
		}

		return objMovimentado;
	}

	/**
	 * Persiste as informações da conta bancária da pessoa
	 * @param pessoa
	 * @param dao
	 * @throws DAOException
	 */
	public static void persistirContaBancaria(Pessoa pessoa, GenericDAO dao) throws DAOException {
	   ContaBancaria conta = pessoa.getContaBancaria();
	   if (conta == null) return;

	   if (conta.getBanco().getId() <= 0) {
	       pessoa.setContaBancaria(null);
	       return;
	   }

	   if (conta.getId() == 0) {
	       dao.create(conta);
	   } else {
		   dao.clearSession();
	       dao.update(conta);
	   }
	   dao.detach(conta);
	}

	/**
	 * Anula objetos transientes de pessoa! Para evitar transient object exception
	 * @param pessoa
	 */
	public static void anularTransientObjects(Pessoa pessoa) {
		
		if (pessoa.getTipoRaca() != null && pessoa.getTipoRaca().getId() == 0)
			pessoa.setTipoRaca(null);
		
		if (pessoa.getEstadoCivil() != null && pessoa.getEstadoCivil().getId() == 0)
			pessoa.setEstadoCivil(null);
		
		if (pessoa.getUnidadeFederativa() != null && pessoa.getUnidadeFederativa().getId() == 0) {
			pessoa.setUnidadeFederativa(null);
		}
		if (pessoa.getMunicipio() != null && pessoa.getMunicipio().getId() == 0) {
			pessoa.setMunicipio(null);
		}
		if (pessoa.getTipoNecessidadeEspecial() != null && pessoa.getTipoNecessidadeEspecial().getId() == 0) {
			pessoa.setTipoNecessidadeEspecial(null);
		}
		if (pessoa.getTituloEleitor() != null && pessoa.getTituloEleitor().isEmpty() ) {
			pessoa.setTituloEleitor(null);
		} else if( pessoa.getTituloEleitor() != null && isEmpty(pessoa.getTituloEleitor().getUnidadeFederativa()) ) {
			pessoa.getTituloEleitor().setUnidadeFederativa(null);
		}
		if (pessoa.getEnderecoContato() != null) {
			if (pessoa.getEnderecoContato().getId() == 0 && pessoa.getEnderecoContato().isEmpty()) {
				pessoa.setEnderecoContato(null);
			} else {
				if (pessoa.getEnderecoContato().getMunicipio() != null && pessoa.getEnderecoContato().getMunicipio().getId() == 0)
					pessoa.getEnderecoContato().setMunicipio(null);
				if (pessoa.getEnderecoContato().getUnidadeFederativa() != null 
						&& pessoa.getEnderecoContato().getUnidadeFederativa().getId() == 0)
					pessoa.getEnderecoContato().setUnidadeFederativa(null);
			}
		}
		if(pessoa.getPais() != null && pessoa.getPais().getId() == 0)
			pessoa.setPais(null);
		
		if(pessoa.getTipoRedeEnsino() != null && pessoa.getTipoRedeEnsino().getId() == 0)
			pessoa.setTipoRedeEnsino(null);
		
		if (pessoa.getContaBancaria() != null && pessoa.getContaBancaria().getBanco().getId() == 0)
			pessoa.setContaBancaria(null);
	}

}