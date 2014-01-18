/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
 * Created on 21/08/2013
*/
package br.ufrn.sigaa.ensino_rede.negocio;

import java.util.Date;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino_rede.dominio.AlteracaoSituacaoDocenteRede;
import br.ufrn.sigaa.ensino_rede.dominio.DocenteRede;
import br.ufrn.sigaa.ensino_rede.dominio.SituacaoDocenteRede;

/**
 * Classe auxiliar para operações em docente de ensino em rede.
 *
 * @author Diego Jácome
 *
 */
public class DocenteRedeHelper {

	public static AlteracaoSituacaoDocenteRede createAlteracaoDocenteRede (DocenteRede docente, Movimento mov, SituacaoDocenteRede situacaoAntiga ,SituacaoDocenteRede situacaoNova){
		
		AlteracaoSituacaoDocenteRede alteracao = new AlteracaoSituacaoDocenteRede();
		alteracao.setDocente(docente);
		alteracao.setSituacaoAntiga(situacaoAntiga);
		alteracao.setSituacaoNova(situacaoNova);
		alteracao.setUsuario((Usuario) mov.getUsuarioLogado());
		alteracao.setRegistro(mov.getUsuarioLogado().getRegistroEntrada());
		alteracao.setDataAlteracao(new Date());
		return alteracao;
	}
	
}
