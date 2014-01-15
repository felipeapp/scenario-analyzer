/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/08/2011
 *
 */
package br.ufrn.sigaa.extensao.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.parametros.dominio.ParametrosExtensao;

@Scope("session")
@Component("siglasExtensaoMBean")
public class SiglasExtensaoMBean {
	
	public String getSiglaFundoExtensaoPadrao() {
		return ParametroHelper.getInstance().getParametro(ParametrosExtensao.SIGLA_FUNDO_EXTENSAO_PADRAO);
	}
	
	public String getNomeFundoExtensaoPadrao() {
		return ParametroHelper.getInstance().getParametro(ParametrosExtensao.NOME_FUNDO_EXTENSAO_PADRAO);
	}	

}