/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 26/09/2006
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import static br.ufrn.arq.util.ValidatorUtil.validaData;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;

import org.apache.commons.validator.EmailValidator;

import com.sun.rmi.rmid.ExecOptionPermission;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.rh.dominio.Cargo;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pesquisa.dominio.FinanciamentoProjetoPesq;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.form.ProjetoPesquisaForm;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;
import br.ufrn.sigaa.projetos.dominio.CronogramaProjeto;
import br.ufrn.sigaa.projetos.dominio.FuncaoMembro;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;

/**
 * Classe para fazer valida��es do cadastro de projeto de pesquisa
 * @author Victor Hugo
 *
 */
public class ProjetoPesquisaValidator {

	/** Carga hor�ria m�xima dedicada */
	public static final int LIMITE_CH_DEDICADA = 30;

	/**
	 * M�todo para validar formul�rio de dados b�sicos do projeto de pesquisa (1� tela)
	 * @param projeto
	 * @param pForm
	 * @param lista
	 */
	public static void validaDadosBasicos(ProjetoPesquisa projeto, ProjetoPesquisaForm pForm,
			ListaMensagens lista) {

	    validateRequired(projeto.getTitulo(), "T�tulo do Projeto", lista);
		validateRequired(projeto.getCentro(), "Centro", lista);

		if (projeto.isInterno() && projeto.getEdital() != null) {
			validateRequired(projeto.getEdital(), "Edital de Pesquisa", lista);
		} else {
			validaData(Formatador.getInstance().formatarData(projeto.getDataInicio()), "In�cio do Projeto", lista);
			validaData(Formatador.getInstance().formatarData(projeto.getDataFim()), "Fim do Projeto", lista);
		}

		if ( projeto.getTitulo() != null && projeto.getTitulo().trim().length() > 400 ) {
			lista.addErro("O t�tulo do projeto deve conter no m�ximo 400 caracteres.");
		}

		validateRequired(projeto.getPalavrasChave(), "Palavras-Chave", lista);
		if (!(EmailValidator.getInstance().isValid(projeto.getEmail()))) {
			lista.addErro("Formato do Email incorreto.");
		}
//		validateRequired(projeto.getEmail(), "E-mail", lista);
		
		if(!projeto.isInterno()){
			validateRequired(projeto.getCategoria(), "Categoria do projeto", lista);
			if ( projeto.getDefinicaoPropriedadeIntelectual() != null && projeto.getDefinicaoPropriedadeIntelectual().trim().length() > 400 ) {
				lista.addErro("A defini��o da propriedade intelectual deve conter no m�ximo 400 caracteres.");
			}
		}

		validateRequired(projeto.getAreaConhecimentoCnpq(), "Grande �rea / �rea de Conhecimento", lista);
		
		if( projeto.getDataInicio() != null && projeto.getDataFim() != null ){
			if( projeto.getDataFim().before( projeto.getDataInicio() )   ){
				lista.addErro("A data de in�cio deve ser posterior � data de fim do projeto.");
			}
		}
		
		if( pForm != null && !pForm.isIsolado() && projeto.getLinhaPesquisa().getGrupoPesquisa() != null ){
		    validateRequiredId(projeto.getLinhaPesquisa().getGrupoPesquisa().getId(),
		            "Como o projeto est� vinculado a um grupo de pesquisa, � necess�rio inform�-lo", lista);
		}

		if ( projeto.getLinhaPesquisa() == null
				|| (projeto.getLinhaPesquisa().getId() <= 0 && projeto.getLinhaPesquisa().getNome() == null)
				|| (projeto.getLinhaPesquisa().getId() <= 0 && projeto.getLinhaPesquisa().getNome() != null && "".equals(projeto.getLinhaPesquisa().getNome().trim())) ) {
			lista.addErro("� necess�rio informar a linha de pesquisa do projeto");
		} else if (projeto.getLinhaPesquisa().getNome().length() > 200){
			lista.addErro("A linha de pesquisa deve conter no m�ximo 200 caracteres.");
		}
	}

	/**
	 * Valida��o dos detalhes do projeto
	 *
	 * @param projeto
	 * @param pForm
	 * @param lista
	 */
	public static void validaDetalhes(ProjetoPesquisa projeto, ProjetoPesquisaForm pForm,
			ListaMensagens lista, Usuario usuario) {

		validateRequired(projeto.getDescricao(), "Descri��o Resumida", lista);
		validateRequired(projeto.getJustificativa(), "Introdu��o/Justificativa", lista);
		validateRequired(projeto.getObjetivos(), "Objetivos", lista);
		validateRequired(projeto.getMetodologia(), "Metodologia", lista);
		validateRequired(projeto.getBibliografia(), "Refer�ncias", lista);

		if (usuario != null && !usuario.isUserInRole( SigaaPapeis.GESTOR_PESQUISA)) {
			validaTamanhoDetalhes(projeto, pForm, lista);
		}

	}

	/**
	 * Valida��o dos limites de quantidade de caracteres para os campos da descri��o do projeto de pesquisa. 
	 * @param projetoPesquisa
	 * @param pForm
	 * @param lista
	 */
	public static void validaTamanhoDetalhes(ProjetoPesquisa projetoPesquisa, ProjetoPesquisaForm pForm,
			ListaMensagens lista) {
		// S� verificar tamanho de campos se n�o for renova��o
		if ( !SigaaListaComando.RENOVAR_PROJETO_PESQUISA.equals(projetoPesquisa.getProjeto().getCodMovimento()) ) {
			if( projetoPesquisa.getDescricao() != null && projetoPesquisa.getDescricao().trim().length() > 15000 ) {
				lista.addErro("A descri��o deve ter no m�ximo 15.000 caracteres.");
			}

			if( projetoPesquisa.getJustificativa() != null && projetoPesquisa.getJustificativa().trim().length() > 15000 ) {
				lista.addErro("A introdu��o/justificativa deve ter no m�ximo 15.000 caracteres.");
			}

			if( projetoPesquisa.getObjetivos() != null && projetoPesquisa.getObjetivos().trim().length() > 15000 ) {
				lista.addErro("Os objetivos devem ter no m�ximo 15.000 caracteres.");
			}

			if( projetoPesquisa.getMetodologia() != null && projetoPesquisa.getMetodologia().trim().length() > 15000 ) {
				lista.addErro("A metodologia deve ter no m�ximo 15.000 caracteres.");
			}
			
			if (projetoPesquisa.getBibliografia() != null && projetoPesquisa.getBibliografia().trim().length() > 15000) {
				lista.addErro("A refer�ncia deve ter no m�ximo 15.000 caracteres.");
			}
		}

	}

	/**
	 * Valida a tela de membros do projeto.
	 * @param projeto
	 * @param pForm
	 * @param lista
	 */
	public static void validaDocentes(ProjetoPesquisa projeto, ProjetoPesquisaForm pForm,
			ListaMensagens lista) {

		// Verificar se foram informados os membros do projeto
		if( projeto.getMembrosProjeto() == null || projeto.getMembrosProjeto().size() == 0 ){
			lista.addErro("� necess�rio informar pelo menos um membro para o projeto.");
		} else {
			// Validar coordenador do projeto
			int cont = 0;
			for( MembroProjeto membro : projeto.getMembrosProjeto() ){

				prepararDadosColaborador(membro);

				// Valida��es de coordenadores
				if( membro.getFuncaoMembro().getId() == FuncaoMembro.COORDENADOR) {

					if(projeto.getEdital() != null){
						// Validar permiss�es de participa��o como membro, dependendo da categoria
						if(!projeto.getEdital().isProfessorSubstitutoCadastraProjeto() ){
							if (membro.getServidor() != null && !Cargo.DOCENTE_PERMANENTE.contains(membro.getServidor().getCargo().getId()) 
									&& Cargo.DOCENTE_SUPERIOR_VISITANTE != membro.getServidor().getCargo().getId()) {
								lista.addErro("Apenas docentes permamentes ou visitantes podem coordenar projetos");
							}  
						}
						
						if( projeto.getEdital().isApenasColaboradorVoluntarioCadastraProjeto() ) { //verifica parametro do edital
							if (  membro.getDocenteExterno() != null && !membro.getDocenteExterno().isColaboradorVoluntario() && projeto.isInterno() ) {
								lista.addErro("Somente docentes externos que foram cadastrados " +
								"como colaboradores volunt�rios podem coordenar projetos de pesquisa.");
							}
						}
					}

					// Definir o coordenador do projeto
					if (membro.isCategoriaDocente()) {
						projeto.setCoordenador(membro.getServidor());
						projeto.setCoordenadorExterno(null);
					} else {
						projeto.setCoordenadorExterno(membro.getDocenteExterno());
						projeto.setCoordenador(null);
					}
					projeto.getProjeto().setCoordenador(membro);
					cont++;
				}
			}

			if( cont == 0 ) {
				lista.addErro("� necess�rio definir um membro como sendo o coordenador do projeto.");
			}
			else if( cont > 1 ) {
				lista.addErro("Um projeto s� pode possuir um �nico coordenador");
			}
		}
	}

	/**
	 * Valida os dados obrigat�rios ao adicionar um docente externo como membro do projeto.
	 * @param membro
	 * @param cpf
	 * @param lista
	 */
	public static void validarDocenteExterno(MembroProjeto membro, String cpf, ListaMensagens lista) {
		DocenteExterno externo = membro.getDocenteExterno();
		validateRequired(externo.getPessoa().getNome(), "Nome", lista);
		externo.getPessoa().setNome( externo.getPessoa().getNome().trim().toUpperCase());

		if (!externo.getPessoa().isInternacional()){
			externo.getPessoa().setCpf_cnpj( ValidatorUtil.validateCPF_CNPJ(cpf, "CPF", lista) );
		} else {
			externo.getPessoa().setCpf_cnpj(null);
		}
		validateRequired(externo.getPessoa().getSexo() == 'N' ? null : externo.getPessoa().getSexo(), "Sexo", lista);
		validateRequired(externo.getFormacao().getId(), "Forma��o", lista);
		validateRequired(externo.getTipoDocenteExterno(), "Tipo", lista);
		validateRequiredId(membro.getChDedicada(), "CH dedicada ao projeto", lista);
		
		if (externo.getInstituicao() == null)
			externo.setInstituicao(new InstituicoesEnsino());
		
	}

	/**
	 * Prepara os dados do membro do projeto de acordo com sua categoria informada.
	 * @param membro
	 */
	public static void prepararDadosColaborador(MembroProjeto membro) {
		switch (membro.getCategoriaMembro().getId()) {
			case CategoriaMembro.SERVIDOR:
			case CategoriaMembro.DOCENTE:
				membro.setDiscente(null);
				membro.setDocenteExterno(null);
				break;
			case CategoriaMembro.DISCENTE:
				membro.setServidor(null);
				membro.setDocenteExterno(null);
				break;
			case CategoriaMembro.EXTERNO:
				membro.setServidor(null);
				membro.setDiscente(null);
				if (membro.getDocenteExterno() != null && membro.getDocenteExterno().getInstituicao() != null && membro.getDocenteExterno().getInstituicao().getId() == 0) 
					membro.getDocenteExterno().setInstituicao(null);
				if (membro.getDocenteExterno() != null && ValidatorUtil.isEmpty(membro.getDocenteExterno().getTipoDocenteExterno()) )
					membro.getDocenteExterno().setTipoDocenteExterno(null);
				break;
		}
	}

	/**
	 * Valida��o dos dados obrigat�rios ao adicionar um financiamento a um projeto de pesquisa externo. 
	 * @param financiamento
	 * @param lista
	 */
	public static void validaAdicionaFinanciamentos(FinanciamentoProjetoPesq financiamento, ListaMensagens lista) {
		ValidatorUtil.validateRequiredId(financiamento.getEntidadeFinanciadora().getId(), "Entidade Financiadora", lista);
		ValidatorUtil.validateRequiredId(financiamento.getTipoNaturezaFinanciamento().getId(), "Natureza do Financiamento", lista);
		ValidatorUtil.validaData(Formatador.getInstance().formatarData(financiamento.getDataInicio()), "Data de In�cio", lista);
		ValidatorUtil.validaData(Formatador.getInstance().formatarData(financiamento.getDataFim()), "Data de Fim", lista);
	}

	/**
	 * Valida a tela do cronograma.
	 * @param projetoPesquisa
	 * @param lista
	 */
	public static void validaCronograma(ProjetoPesquisa projetoPesquisa, ListaMensagens lista) {
		if(projetoPesquisa.getCronogramas().isEmpty())
			lista.addErro("� necess�rio informar ao menos uma atividade no cronograma");

		for (CronogramaProjeto crono : projetoPesquisa.getCronogramas()) {
			if( crono.getDescricao().trim().length() > 300 ) {
				lista.addErro("A descricao da atividade <i>" + crono.getDescricao() + "<i> deve conter no m�ximo 300 caracteres.");
			}
		}

	}

	/**
	 * Realiza as valida��es necess�rias quando um projeto � renovado.
	 * cada projeto s� pode ser renovado uma quantidade determinada de vezes que � configurada por par�metro.
	 *
	 * @see br.ufrn.sigaa.arq.dominioConstantesParametro.NUMERO_MAXIMO_RENOVACOES_PROJETO
	 * @param projetoPesquisa
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaRenovacao(ProjetoPesquisa projetoPesquisa, ListaMensagens lista) throws DAOException {

		ParametroHelper helper = ParametroHelper.getInstance();
		int qtdMaximaRenovacoes = helper.getParametroInt(ConstantesParametro.NUMERO_MAXIMO_RENOVACOES_PROJETO);

		if( projetoPesquisa.getNumeroRenovacoes() >= qtdMaximaRenovacoes ) {
			lista.addErro("Este projeto j� foi renovado a quantidade m�xima de vezes portanto, n�o poder� ser renovado novamente.");
		}

	}

	/**
	 * Limpa alguns dados da pessoa que n�o s�o utilizados.
	 * @param pessoa
	 */
	public static void limparDadosPessoa(Pessoa pessoa) {
		pessoa.setValido(true);
		pessoa.setUnidadeFederativa(null);
		pessoa.setMunicipio(null);
		//pessoa.setEnderecoResidencial(null);
		//pessoa.setEnderecoProfissional(null);
		pessoa.setPais(null);
		pessoa.setTipoRedeEnsino(null);
		pessoa.setEstadoCivil(null);
		pessoa.setIdentidade(null);
		pessoa.setEnderecoContato(null);
	}

}
