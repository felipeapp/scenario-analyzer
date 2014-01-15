/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on '05/10/2006'
 *
 */
package br.ufrn.sigaa.negocio;

import static br.ufrn.arq.util.ValidatorUtil.validaData;
import static br.ufrn.arq.util.ValidatorUtil.validateAbreviacao;
import static br.ufrn.arq.util.ValidatorUtil.validateBirthday;
import static br.ufrn.arq.util.ValidatorUtil.validateCPF_CNPJ;
import static br.ufrn.arq.util.ValidatorUtil.validateEmail;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.sigaa.form.PessoaForm;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.PessoaJuridica;

/**
 * Valida os dados pessoais.
 * @author Andre M Dantas
 *
 */
public class PessoaValidator {

	public static final int GERAL = 1;
	public static final int DISCENTE = 2;
	public static final int DOCENTE_EXTERNO = 3;

	/**
	 * valida os dados pessoais do aluno
	 */
	public static void validarDadosPessoais(Pessoa p, PessoaForm form, int tipoValidacao, ListaMensagens lista) {
		// valida��es no formul�rio
		if (form != null) {
			 // campos comuns
//			validateRequired(form.getCpf_cnpj(), (p.isPF())?"CPF":"CNPJ", lista);

			// validando n�meros de telefones
			if(!p.isInternacional())
				p.setCpf_cnpj(validateCPF_CNPJ(form.getCpf_cnpj(), ((p.isPF())?"CPF":"CNPJ"), lista));
			else
				p.setCpf_cnpj(0l);

			// campos espec�ficos
			if (p.isPF() && tipoValidacao != DOCENTE_EXTERNO) {
				// valida��es de datas submetidas no form
				p.setDataNascimento(validaData(
						form.getDataNascimento(), "Data de Nascimento", lista));
				if(!p.isInternacional()){
					if (p.getIdentidade().getDataExpedicao() != null) {
						p.getIdentidade().setDataExpedicao(validaData(
							form.getDataExpedicaoIdentidade(), "Data de Expedi��o do Documento de Identidade", lista));
					}
					validateRequired(p.getIdentidade().getNumero(), "RG", lista);
					validateRequired(form.getPessoa().getIdentidade().getOrgaoExpedicao(), "�rg�o de Expedi��o", lista);
					validateRequiredId(form.getPessoa().getIdentidade().getUnidadeFederativa().getId(), "UF da Identidade", lista);
				}
			}
		}

		// campos comuns
//		validateRequired(p.getCpf_cnpj(), (p.isPF())?"CPF":"CNPJ", lista);
		validateRequired(p.getNome(), (p.isPF())?"Nome":"Nome Fantasia", lista);
		if (tipoValidacao != DOCENTE_EXTERNO) {
			validateEmail(p.getEmail(), "E-Mail", lista);
		}

		if ( p.isPF() ) {
			// validateCPF_CNPJ(p.getCpf_cnpj()+"", ((p.isPF())?"CPF":"CNPJ"), lista);
			if(p.getSexo() != 'M' && p.getSexo() != 'F')
				lista.getMensagens().add(new MensagemAviso("Sexo: Campo obrigat�rio n�o informado", TipoMensagemUFRN.ERROR));
			if (tipoValidacao != DOCENTE_EXTERNO) {
				validateAbreviacao(p.getNome(), "Nome", lista);
				validateRequired(p.getNomeMae(), "Nome da M�e", lista);
				validateAbreviacao(p.getNomeMae(), "Nome da M�e", lista);
				validateAbreviacao(p.getNomePai(), "Nome do Pai", lista);
				validateBirthday(p.getDataNascimento(), "Data de Nascimento", lista);
			}
		}

		// campos espec�ficos


//		 setando nulo objetos referentes somente a pessoa f�sica
		if (p.isPJ()) {
			p.setMunicipio(null);
			p.setUnidadeFederativa(null);
			p.setTipoRedeEnsino(null);
			p.setTipoRaca(null);
			p.setIdentidade(null);
			p.setEstadoCivil(null);
			p.setPais(null);
			if (form != null)
				validarDadosPJ(form.getPessoaJuridica(), form, lista);
		}
	}

	public static void validarDadosPJ(PessoaJuridica pj, PessoaForm form, ListaMensagens lista) {
		validateRequired(pj.getNomeDirigente(), "Nome do Dirigente", lista);
	}
}
