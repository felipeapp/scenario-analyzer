/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * 
 * Created on 29/03/2006
 *
 */
package br.ufrn.sigaa.monitoria.negocio;

import java.util.HashSet;
import java.util.Set;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.projetos.dominio.MembroComissao;

/**
 * Movimento para distribuição de projetos de monitoria e resumos do SID
 *
 * @author ilueny santos
 *
 */
public class DistribuicaoMonitoriaMov extends MovimentoCadastro {
	
	//tipos de distribuição
	public static final int DISTRIBUIR_PROJETOS		= 1;
	public static final int DISTRIBUIR_RESUMOS 		= 2;
	public static final int DISTRIBUIR_RELATORIOS 	= 3;	

	
	private Set<MembroComissao> avaliadoresRemovidos =  new HashSet<MembroComissao>();
	
	

	public Set<MembroComissao> getAvaliadoresRemovidos() {
		return avaliadoresRemovidos;
	}

	public void setAvaliadoresRemovidos(Set<MembroComissao> avaliadoresRemovidos) {
		this.avaliadoresRemovidos = avaliadoresRemovidos;
	}

	
}