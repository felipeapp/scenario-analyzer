/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '24/04/2007'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.negocio;

import java.util.ArrayList;

import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ensino.latosensu.dominio.TrabalhoFinalLato;
import br.ufrn.sigaa.ensino.latosensu.form.TrabalhoFinalLatoForm;

/**
 * Classe com as valida��es do trabalho final lato sensu
 * 
 * @author Leonardo
 *
 */
public class TrabalhoFinalLatoValidator {

	public static void validaTrabalhoFinal(TrabalhoFinalLatoForm form, TrabalhoFinalLato trabalhoFinal, ArrayList<MensagemAviso> lista){
		ValidatorUtil.validateRequired(trabalhoFinal.getTitulo(), "T�tulo", lista);
		ValidatorUtil.validateRequired(trabalhoFinal.getDiscenteLato(), "Aluno", lista);
		ValidatorUtil.validateRequired(trabalhoFinal.getServidor(), "Orientador", lista);
		if( form != null ){
			if( trabalhoFinal.getId() == 0 )
				ValidatorUtil.validateRequired(form.getArquivoTrabalhoFinal().getFileName(), "Arquivo do Trabalho Final", lista);
		}
	}
	
}
