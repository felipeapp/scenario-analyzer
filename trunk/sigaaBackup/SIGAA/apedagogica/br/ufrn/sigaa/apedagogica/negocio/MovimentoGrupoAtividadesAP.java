package br.ufrn.sigaa.apedagogica.negocio;

import java.util.List;
import java.util.Map;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.apedagogica.dominio.AtividadeArquivo;

/**
 * Classe responsável em movimentar todas operações que envolvem
 * o grupo de atividade de atualização pedagógica
 * @author Mário Rizzi
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
