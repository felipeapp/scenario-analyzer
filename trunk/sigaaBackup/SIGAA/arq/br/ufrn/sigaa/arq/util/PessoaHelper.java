/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on 28/10/2004
 *
 */
package br.ufrn.sigaa.arq.util;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.RegistroAcessoPublico;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.pessoa.dominio.AlteracaoPessoa;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/***
 * Utilizado para atualizar os dados pessoais das pessoas.
 *
 * @author Gleydson
 *
 */
public class PessoaHelper {

	/**
	 * Registra a alteração da Pessoa passando o movimento
	 * 
	 * @param pessoaAlterada
	 * @param dao
	 * @param entradaPublico
	 * @return
	 * @throws NegocioException
	 * @throws DAOException
	 */
	public static Pessoa alteraCriaPessoa(Pessoa pessoaAlterada, GenericDAO dao, MovimentoCadastro mov, int comando) throws NegocioException, DAOException {

		if (mov == null)
	        throw new IllegalArgumentException("Movimento não foi definido.");

		return alteraCriaPessoa(pessoaAlterada, dao, mov.getRegistroEntrada(), mov.getRegistroAcessoPublico(), comando);
		
	}	
	
	/**
	 * Metodo utilizado quando a origem da alteração vier da Área Interna
	 * 
	 * @param pessoaAlterada
	 * @param dao
	 * @param entrada
	 * @return
	 * @throws NegocioException
	 * @throws DAOException
	 */
	public static Pessoa alteraCriaPessoa(Pessoa pessoaAlterada, GenericDAO dao, RegistroEntrada entrada, int comando) throws NegocioException, DAOException {
		
		if (entrada == null)
	        throw new IllegalArgumentException("O registro de entrada deve ser definido.");
		
		return alteraCriaPessoa(pessoaAlterada, dao, entrada, null, comando);
	}

	/**
	 * Metodo utilizado quando a origem da alteração vier da Área Pública
	 * 
	 * @param pessoaAlterada
	 * @param dao
	 * @param entradaPublico
	 * @return
	 * @throws NegocioException
	 * @throws DAOException
	 */
	public static Pessoa alteraCriaPessoa(Pessoa pessoaAlterada, GenericDAO dao, RegistroAcessoPublico entradaPublico, int comando) throws NegocioException, DAOException {
		

		if (entradaPublico == null)
	        throw new IllegalArgumentException("O registro de acesso público deve ser definido.");
		
		return alteraCriaPessoa(pessoaAlterada, dao, null, entradaPublico, comando);
	}
	
	/**
	 * Este método grava um histórico dos dados pessoais de uma Pessoa quando estes são alterados
	 * 
	 * @param pessoaAlterada
	 * @param dao
	 * @param entrada
	 * @param entradaPublico
	 * @return
	 * @throws NegocioException
	 * @throws DAOException
	 */
	private static Pessoa alteraCriaPessoa(Pessoa pessoaAlterada, GenericDAO dao, RegistroEntrada entrada, RegistroAcessoPublico entradaPublico, int comando) throws NegocioException, DAOException {
		
		Pessoa pessoaOriginal = null;

		//TODO verificar isto
		if( pessoaAlterada.getId() == 0 ){
			NegocioException e = new NegocioException();
			e.addErro("Erro ao registrar alteração dos dados pessoais, contacte a administração do sistema.");
			throw e;
		}

		if (entrada == null && entradaPublico == null)
	        throw new IllegalArgumentException("Algum mecanismo de registro deve ser definido.");

		if (isEmpty(comando))
			throw new IllegalArgumentException("O comando que gerou a ação não foi definido.");
		
		try {
			pessoaOriginal = dao.findByPrimaryKey(pessoaAlterada.getId(), Pessoa.class);
		} catch (DAOException e) {
			e.printStackTrace();
			throw e;
		}

//		TODO verificar isto
		if( pessoaOriginal == null || pessoaOriginal.getId() == 0 ){
			NegocioException e = new NegocioException();
			e.addErro("Erro ao registrar alteração dos dados pessoais, contacte a administração do sistema.");
			throw e;
		}

		AlteracaoPessoa historicoPessoa = new AlteracaoPessoa();

		historicoPessoa.setPessoa( pessoaOriginal );
		historicoPessoa.setRegistroEntrada( entrada );
		historicoPessoa.setRegistroPublico( entradaPublico );
		historicoPessoa.setDataCadastro( new Date() );
		historicoPessoa.setCodComando(comando);
		
		popularAlteracaoPessoa( pessoaOriginal, historicoPessoa );

		// Tratando constraints
		if (historicoPessoa.getIdMunicipioNaturalidade() == 0)
			historicoPessoa.setIdMunicipioNaturalidade(-1);
		if (historicoPessoa.getEnderecoContatoIdMunicipio() == 0) {
			historicoPessoa.setEnderecoContatoIdMunicipio(-1);
		}

		dao.create( historicoPessoa );
		dao.detach(pessoaOriginal);

		return pessoaAlterada;
	}
	
	/**
	 * popula os dados da entidade AlteracaoPessoa com os dados de Pessoa
	 *
	 * @param pessoaOriginal a pessoa com os dados originais que serão salvos no histórico
	 * @param historicoPessoa histórico da pessoa a ser populado
	 */
	private static void popularAlteracaoPessoa( Pessoa pessoaOriginal, AlteracaoPessoa historicoPessoa ){

		historicoPessoa.setAbreviatura( pessoaOriginal.getAbreviatura() );
		historicoPessoa.setCpf_cnpj( pessoaOriginal.getCpf_cnpj() );

		historicoPessoa.setNome( pessoaOriginal.getNome() );
		historicoPessoa.setNomeAscii( pessoaOriginal.getNomeAscii() );
		historicoPessoa.setNomeMae( pessoaOriginal.getNomeMae() );
		historicoPessoa.setNomePai( pessoaOriginal.getNomePai() );
		historicoPessoa.setSexo( pessoaOriginal.getSexo() );

		historicoPessoa.setDataNascimento( pessoaOriginal.getDataNascimento() );
		historicoPessoa.setEmail( pessoaOriginal.getEmail() );
		historicoPessoa.setEndereco( pessoaOriginal.getEndereco() );

		historicoPessoa.setFuncionario( pessoaOriginal.isFuncionario() );
		historicoPessoa.setMunicipioNaturalidadeOutro( pessoaOriginal.getMunicipioNaturalidadeOutro() );
		historicoPessoa.setOutroDocumento( pessoaOriginal.getOutroDocumento() );
		historicoPessoa.setPassaporte( pessoaOriginal.getPassaporte() );
		historicoPessoa.setUltimaAtualizacao( pessoaOriginal.getUltimaAtualizacao() );
		historicoPessoa.setValido( pessoaOriginal.isValido() );
		historicoPessoa.setTelefone( pessoaOriginal.getTelefone() );
		historicoPessoa.setCelular( pessoaOriginal.getCelular() );
		historicoPessoa.setFax( pessoaOriginal.getFax() );
		historicoPessoa.setOrigem( pessoaOriginal.getOrigem() );
		historicoPessoa.setCodigoAreaNacionalTelefoneCelular( pessoaOriginal.getCodigoAreaNacionalTelefoneCelular() );
		historicoPessoa.setCodigoAreaNacionalTelefoneFixo( pessoaOriginal.getCodigoAreaNacionalTelefoneFixo() );


		if( pessoaOriginal.getCertificadoMilitar() != null ){
			historicoPessoa.setCertificadoMilitarCategoria( pessoaOriginal.getCertificadoMilitar().getCategoria() );
			historicoPessoa.setCertificadoMilitarDataExpedicao( pessoaOriginal.getCertificadoMilitar().getDataExpedicao() );
			historicoPessoa.setCertificadoMilitarNumero( pessoaOriginal.getCertificadoMilitar().getNumero() );
			historicoPessoa.setCertificadoMilitarOrgaoExpedicao( pessoaOriginal.getCertificadoMilitar().getOrgaoExpedicao() );
			historicoPessoa.setCertificadoMilitarSerie( pessoaOriginal.getCertificadoMilitar().getSerie() );
		}

		if( pessoaOriginal.getContaBancaria() != null ){
			historicoPessoa.setContaBancariaAgencia( pessoaOriginal.getContaBancaria().getAgencia() );
			historicoPessoa.setContaBancariaIdBanco( pessoaOriginal.getContaBancaria().getBanco().getId() );
			historicoPessoa.setContaBancariaNumero( pessoaOriginal.getContaBancaria().getNumero() );
			historicoPessoa.setContaBancariaVariacao( pessoaOriginal.getContaBancaria().getVariacao() );
			historicoPessoa.setContaBancariaOperacao( pessoaOriginal.getContaBancaria().getOperacao() );
		}


		/**
		 * Endereço contato
		 */
		if( pessoaOriginal.getEnderecoContato() != null ){
			historicoPessoa.setEnderecoContatoBairro( pessoaOriginal.getEnderecoContato().getBairro() );
			historicoPessoa.setEnderecoContatoCaixaPostal( pessoaOriginal.getEnderecoContato().getCaixaPostal() );
			historicoPessoa.setEnderecoContatoCep( pessoaOriginal.getEnderecoContato().getCep() );
			historicoPessoa.setEnderecoContatoComplemento( pessoaOriginal.getEnderecoContato().getComplemento() );
			historicoPessoa.setEnderecoContatoCorrespondencia( pessoaOriginal.getEnderecoContato().isCorrespondencia() );
			
			if( pessoaOriginal.getEnderecoContato().getMunicipio() != null )
				historicoPessoa.setEnderecoContatoIdMunicipio( pessoaOriginal.getEnderecoContato().getMunicipio().getId() );

			if( pessoaOriginal.getEnderecoContato().getPais() != null )
				historicoPessoa.setEnderecoContatoIdPais( pessoaOriginal.getEnderecoContato().getPais().getId() );

			if( pessoaOriginal.getEnderecoContato().getUnidadeFederativa() != null )
				historicoPessoa.setEnderecoContatoIdUf( pessoaOriginal.getEnderecoContato().getUnidadeFederativa().getId() );

			historicoPessoa.setEnderecoContatoLogradouro( pessoaOriginal.getEnderecoContato().getLogradouro() );
			historicoPessoa.setEnderecoContatoMunicipioOutro( pessoaOriginal.getEnderecoContato().getMunicipioOutro() );
			historicoPessoa.setEnderecoContatoNomePais( pessoaOriginal.getEnderecoContato().getNomePais() );
			historicoPessoa.setEnderecoContatoNumero( pessoaOriginal.getEnderecoContato().getNumero() );

		}


		if( pessoaOriginal.getIdentidade() != null ){
			historicoPessoa.setIdentidadeDataExpedicao( pessoaOriginal.getIdentidade().getDataExpedicao() );
			if( pessoaOriginal.getIdentidade().getUnidadeFederativa()!= null )
				historicoPessoa.setIdentidadeIdUf( pessoaOriginal.getIdentidade().getUnidadeFederativa().getId() );
			historicoPessoa.setIdentidadeNumero( pessoaOriginal.getIdentidade().getNumero() );
			historicoPessoa.setIdentidadeOrgaoExpedicao( pessoaOriginal.getIdentidade().getOrgaoExpedicao() );
		}

		if( pessoaOriginal.getEstadoCivil() != null )
			historicoPessoa.setIdEstadoCivil( pessoaOriginal.getEstadoCivil().getId() );

		if( pessoaOriginal.getGrauFormacao() != null )
			historicoPessoa.setIdGrauFormacao( pessoaOriginal.getGrauFormacao().getId() );

		if( pessoaOriginal.getMunicipio() != null )
			historicoPessoa.setIdMunicipioNaturalidade( pessoaOriginal.getMunicipio().getId() );

		if( pessoaOriginal.getPais() != null )
			historicoPessoa.setIdPais( pessoaOriginal.getPais().getId() );

		if( pessoaOriginal.getTipoEtnia() != null )
			historicoPessoa.setIdTipoEtnia( pessoaOriginal.getTipoEtnia().getId() );

		if( pessoaOriginal.getTipoRaca() != null )
			historicoPessoa.setIdTipoRaca( pessoaOriginal.getTipoRaca().getId() );

		if( pessoaOriginal.getTipoRedeEnsino() != null )
			historicoPessoa.setIdTipoRedeEnsino( pessoaOriginal.getTipoRedeEnsino().getId() );

		if( pessoaOriginal.getUnidadeFederativa() != null )
			historicoPessoa.setIdUfNaturalidade( pessoaOriginal.getUnidadeFederativa().getId() );


		if( pessoaOriginal.getTituloEleitor() != null ){
			historicoPessoa.setTituloEleitorDataExpedicao( pessoaOriginal.getTituloEleitor().getDataExpedicao() );
			if (pessoaOriginal.getTituloEleitor().getUnidadeFederativa() != null)
				historicoPessoa.setTituloEleitorIdUf( pessoaOriginal.getTituloEleitor().getUnidadeFederativa().getId() );
			historicoPessoa.setTituloEleitorNumero( pessoaOriginal.getTituloEleitor().getNumero() );
			historicoPessoa.setTituloEleitorSecao( pessoaOriginal.getTituloEleitor().getSecao() );
			historicoPessoa.setTituloEleitorZona( pessoaOriginal.getTituloEleitor().getZona() );
		}

	}

	/**
	 * Invalida a pessoa passada como argumento.
	 * 
	 * @param pessoaInvalidada
	 * @param dao
	 * @param mov
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public static void invalidarPessoa(Pessoa pessoaInvalidada, GenericDAO dao, MovimentoCadastro mov ) throws DAOException, NegocioException {
		
		pessoaInvalidada.setCpf_cnpj(null);
		pessoaInvalidada.setValido(false);
		pessoaInvalidada.setEnderecoContato(null);
		pessoaInvalidada.setUnidadeFederativa(null);
		pessoaInvalidada.setMunicipio(null);
		pessoaInvalidada.setTipoRaca(null);
		pessoaInvalidada.setTipoEtnia(null);
		pessoaInvalidada.setPais(null);
		pessoaInvalidada.setIdentidade(null);
		pessoaInvalidada.setEstadoCivil(null);
		pessoaInvalidada.setTipoRedeEnsino(null);
		pessoaInvalidada.setSexo('M');
		
		alteraCriaPessoa( pessoaInvalidada, dao, mov, mov.getCodMovimento().getId() );
		
		dao.update(pessoaInvalidada);
	}
	
}
