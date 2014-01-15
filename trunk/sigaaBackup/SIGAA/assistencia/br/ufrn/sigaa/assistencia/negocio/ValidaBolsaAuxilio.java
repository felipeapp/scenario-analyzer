package br.ufrn.sigaa.assistencia.negocio;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilioAtleta;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilioCreche;

/**
 * Classe responsável por implementar a validação dos dados de uma Bolsa de Auxílio.
 *
 */
public class ValidaBolsaAuxilio {
	
	public static void validate(BolsaAuxilio bolsaAuxilio, Object bolsaAuxiliar, ListaMensagens lista) {
		if ( bolsaAuxiliar instanceof BolsaAuxilioCreche )
			validaBolsaCreche(bolsaAuxilio, (BolsaAuxilioCreche) bolsaAuxiliar, lista);
		else if ( bolsaAuxiliar instanceof BolsaAuxilioAtleta )
			validaBolsaAtleta(bolsaAuxilio, (BolsaAuxilioAtleta) bolsaAuxiliar, lista);
		else if ( bolsaAuxilio.getTipoBolsaAuxilio().isOculos() )
			validaBolsaOculos(bolsaAuxilio, lista);
		else if ( bolsaAuxilio.getTipoBolsaAuxilio().isPromisaes() )
			validaBolsaPromisaes(bolsaAuxilio, lista);
		else
			validate(bolsaAuxilio, lista);
	}
	
	/** Valida dados referentes à Bolsa Promisaes. */
	private static void validaBolsaPromisaes(BolsaAuxilio bolsaAuxilio, ListaMensagens lista) {
		ValidatorUtil.validateRequired(bolsaAuxilio.getRegistroNacionalEstrangeiro(), "RNE (Registro Nacional de Estrangeiro)", lista);
		ValidatorUtil.validateRequired(bolsaAuxilio.getJustificativaRequerimento(), "Justificativa de Requerimento", lista);
	}

	/** Valida dados referentes à Bolsa Óculos. */
	private static void validaBolsaOculos(BolsaAuxilio bolsaAuxilio, ListaMensagens lista) {
		ValidatorUtil.validateRequired(bolsaAuxilio.getJustificativaRequerimento(), "Justificativa de Requerimento", lista);
	}

	/** Valida dados referentes à Bolsa Atleta. */
	private static void validaBolsaAtleta(BolsaAuxilio bolsaAuxilio, BolsaAuxilioAtleta bolsaAuxiliar, ListaMensagens lista) {
		ValidatorUtil.validateRequiredId(bolsaAuxiliar.getModalidadeEsportiva().getId(), "Assinale a modalidade desportiva que pratica", lista);
		ValidatorUtil.validateRequired(bolsaAuxiliar.getFederacao(), "Escreva o nome da Federação em que está associado", lista);
		ValidatorUtil.validateRequired(bolsaAuxilio.getJustificativaRequerimento(), "Justificativa de Requerimento", lista);
	}
	
	private static void validate(BolsaAuxilio bolsaAuxilio, ListaMensagens lista) {
		ValidatorUtil.validateRequired(bolsaAuxilio.getJustificativaRequerimento(), "Justificativa de Requerimento", lista);
		ValidatorUtil.validateRequired(bolsaAuxilio.getTurnoAtividade(), "Atividades Acadêmicas em Turnos Consecutivos", lista);
		ValidatorUtil.validateRequired(bolsaAuxilio.getCustoMensalTransporte(), "Custo Mensal", lista);
	}

	/** Valida dados referentes à Bolsa Creche. */
	private static void validaBolsaCreche(BolsaAuxilio bolsaAuxilio, BolsaAuxilioCreche bolsaAuxCreche, ListaMensagens lista) {
		ValidatorUtil.validateRequired(bolsaAuxilio.getJustificativaRequerimento(), "Justificativa de Requerimento", lista);
		ValidatorUtil.validateRequired(bolsaAuxilio.getTurnoAtividade(), "Atividades Acadêmicas em Turnos Consecutivos", lista);

		ValidatorUtil.validateRequired(bolsaAuxCreche.getTrabalhoCandidato(), "Trabalho do(a) candidato(a)", lista);
		ValidatorUtil.validateRequired(bolsaAuxCreche.getSalarioCandidato(), "Salário do(a) candidato(a)", lista);
		ValidatorUtil.validateRequired(bolsaAuxCreche.getNumeroFilhosCandidato(), "Número de Filhos", lista);
		ValidatorUtil.validateRequired(bolsaAuxCreche.getIdadeFilhosCandidato(), "Idade dos Filhos", lista);
		
		if ( bolsaAuxCreche.isSolteiro() ) {
			if ( bolsaAuxCreche.isRecebePensao() )
				ValidatorUtil.validateRequired(bolsaAuxCreche.getValorPensao(), "Valor da Pensão", lista);
		} else {
			ValidatorUtil.validateRequired(bolsaAuxCreche.getTrabalhoConjugeCandidato(), "Trabalho do Cônjuge", lista);
			ValidatorUtil.validateRequired(bolsaAuxCreche.getSalarioConjugeCandidato(), "Salário do Cônjuge", lista);
		}
		
		if ( lista.isEmpty() ) {
			String [] idadesInformadas = bolsaAuxCreche.getIdadeFilhosCandidato().split(",");
			if ( bolsaAuxCreche.getNumeroFilhosCandidato() > idadesInformadas.length || bolsaAuxCreche.getNumeroFilhosCandidato() < idadesInformadas.length)
				lista.addErro("A quantidade de filhos informada não corresponde as quantidade de idades informadas.");
		}
	
	}
	
}