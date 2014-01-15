/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '26/10/2006'
 *
 */
package br.ufrn.sigaa.prodocente.producao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;

/**
 *
 * @author Gleydson
 */
@Deprecated
@Entity
@Table(name = "texto_discussao", schema = "prodocente")
@PrimaryKeyJoinColumn(name = "id_texto_discussao")
public class TextoDiscussao extends Publicacao {

	@Column(name = "autores")
	private String autores;
	
	@Column(name = "pagina_inicial")
	private Integer paginaInicial;

	@Column(name = "pagina_final")
	private Integer paginaFinal;

	@Column(name = "destaque")
	private Boolean destaque;

	/** Creates a new instance of TextoDiscussao */
	public TextoDiscussao() {
	}

	public String getAutores() {
		return autores;
	}

	public void setAutores(String autores) {
		this.autores = autores;
	}
	
	public Integer getPaginaFinal() {
		return paginaFinal;
	}

	public void setPaginaFinal(Integer paginaFinal) {
		this.paginaFinal = paginaFinal;
	}

	public Integer getPaginaInicial() {
		return paginaInicial;
	}

	public void setPaginaInicial(Integer paginaInicial) {
		this.paginaInicial = paginaInicial;
	}

	public Boolean getDestaque() {
		return destaque;
	}

	public void setDestaque(Boolean destaque) {
		this.destaque = destaque;
	}


	/*
	 * Campos Obrigatorios: Titulo, Participacao, Local, Data
	 * 						Implementados no Pai
	 */
	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getAutores(), "Autores", lista);
		ValidatorUtil.validateRequired(getArea(), "Área", lista);
		ValidatorUtil.validateRequired(getSubArea(), "Sub-Área", lista);
		lista.addAll(super.validate().getMensagens());

		return lista;
	}


}
