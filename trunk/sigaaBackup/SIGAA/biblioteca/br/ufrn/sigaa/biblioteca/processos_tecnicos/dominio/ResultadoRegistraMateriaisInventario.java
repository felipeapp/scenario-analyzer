package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;

/**
 * Classe com as informações do resultado do processamento do registro de um lote de materiais. 
 * 
 * @author Felipe
 *
 */
public class ResultadoRegistraMateriaisInventario {
	
	/**
	 * Lista dos materiais a serem registrados.
	 */
	private List<ItemResultadoRegistraMateriaisInventario> itemList;
	
	public ResultadoRegistraMateriaisInventario() {
		itemList = new ArrayList<ItemResultadoRegistraMateriaisInventario>();
	}

	public ResultadoRegistraMateriaisInventario(List<ItemResultadoRegistraMateriaisInventario> itemList) {
		this.itemList = itemList;
	}

	public List<ItemResultadoRegistraMateriaisInventario> getItemList() {
		return itemList;
	}
	
	/**
	 * Retorna uma lista de mensagens resultantes do processamento do registro da lista de materiais.
	 * 
	 * @return
	 */
	public ListaMensagens getMensagens() {
		ListaMensagens mensagens = new ListaMensagens();
		
		for (ItemResultadoRegistraMateriaisInventario item : itemList) {
			if (!item.isConcluido()) {
				mensagens.addErro(item.getMensagemErro());
			}
		}

		if (mensagens.size() == 0) {
			mensagens.addInformation("Materiais registrados com sucesso.");
		} else {
			if (mensagens.size() < itemList.size()) {
				mensagens.addInformation("Demais materiais registrados com sucesso.");
			}
		}
		
		return mensagens;
	}

	/**
	 * Retorna uma String com as mensagens resultantes do processamento do registro da lista de materiais.
	 * 
	 * @return
	 */
	public String getStringMensagens() {
		StringBuilder sb = new StringBuilder();
		
		for (MensagemAviso erro : this.getMensagens().getMensagens()) {
			sb.append(erro.getMensagem() + "\n");
		}
		
		return sb.toString();
	}

	/**
	 * Adiciona uma mensagem de erro à lista de mensagens resultante do processamento do registro.
	 * 
	 * @param codigoBarras
	 * @param mensagemErro
	 */
	public void addErro(String codigoBarras, String mensagemErro) {
		this.itemList.add(new ItemResultadoRegistraMateriaisInventario(codigoBarras, false, mensagemErro));
	}

	/**
	 * Adiciona uma mensagem de sucesso à lista de mensagens resultante do processamento do registro.
	 * 
	 * @param codigoBarras
	 * @param mensagemErro
	 */
	public void addSucesso(String codigoBarras) {
		this.itemList.add(new ItemResultadoRegistraMateriaisInventario(codigoBarras, true, null));
	}

	/**
	 * Retorna uma lista com os registros concluídos com sucesso.
	 * 
	 * @return
	 */
	public List<ItemResultadoRegistraMateriaisInventario> getItensConcluidos() {
		List<ItemResultadoRegistraMateriaisInventario> itensConcluidos = new ArrayList<ItemResultadoRegistraMateriaisInventario>();
		
		for (ItemResultadoRegistraMateriaisInventario item : itemList) {
			if (item.isConcluido()) {
				itensConcluidos.add(item);
			}
		}
		
		return itensConcluidos;
	}
}
