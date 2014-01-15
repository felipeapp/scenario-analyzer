package br.ufrn.sigaa.apedagogica.negocio;

import java.util.List;
import java.util.Map;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.apedagogica.dominio.AtividadeArquivo;

/**
 * Classe respons�vel em movimentar todas opera��es que envolvem
 * o grupo de atividade de atualiza��o pedag�gica
 * @author M�rio Rizzi
 *
 */
public class MovimentoGrupoAtividadesAP extends MovimentoCadastro {

	/** Atributo que define o participante */
	private List<AtividadeArquivo> listaAtividadesAnexo;

	public List<AtividadeArquivo> getListaAtividadesAnexo() {
		return listaAtividadesAnexo;
	}

	public void setListaAtividadesAnexo(
			List<AtividadeArquivo> listaAtividadesAnexo) {
		this.listaAtividadesAnexo = listaAtividadesAnexo;
	}

	
}
