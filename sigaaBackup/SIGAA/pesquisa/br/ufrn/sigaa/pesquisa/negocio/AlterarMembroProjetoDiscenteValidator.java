/**
 * 
 */
package br.ufrn.sigaa.pesquisa.negocio;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.pesquisa.dominio.MembroProjetoDiscente;
import br.ufrn.sigaa.pesquisa.form.MembroProjetoDiscenteForm;

/**
 * Classe que implementa a validação dos dados do bolsista que podem ser
 * alterados pelo GESTOR_PESQUISA (dados bancários e data de indicação/substituição)
 *  
 * @author Leonardo Campos
 *
 */
public class AlterarMembroProjetoDiscenteValidator {

	public static void validaAlteracao(MembroProjetoDiscente membro, MembroProjetoDiscenteForm form, ListaMensagens lista){
		ValidatorUtil.validateRequired(membro.getDiscente().getPessoa().getContaBancaria().getNumero(), "Número da Conta", lista);
		ValidatorUtil.validateRequired(membro.getDiscente().getPessoa().getContaBancaria().getAgencia(), "Número da Agência", lista);
		membro.setDataInicio( ValidatorUtil.validaData(form.getDataInicio(), "Data de Início", lista) );
		if( form.getDataFim() != null && !"".equals(form.getDataFim()))
			membro.setDataFim( ValidatorUtil.validaData(form.getDataFim(), "Data de Fim", lista) );
		ValidatorUtil.validaInicioFim(membro.getDataInicio(), membro.getDataFim(), "Período da Bolsa", lista);
	}
}
