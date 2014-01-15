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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Textos didáticos ou discussões publicadas por docentes
 * @author Gleydson
 */
@Entity
@Table(name = "texto_didatico", schema = "prodocente")
@PrimaryKeyJoinColumn(name = "id_texto_didatico")
public class TextoDidatico extends Publicacao {

	@Column(name = "autores")
	private String autores;

	@Column(name = "pagina_inicial")
	private Integer paginaInicial;

	@Column(name = "pagina_final")
	private Integer paginaFinal;

	@Column(name = "destaque")
	private Boolean destaque;

	/**
	 * Usada pelos Texto_Discussao, pois a tabela texto_discussao se uniu com texto_didatico
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	@Column(name="premiada")
	private Boolean premiada;

	/**Diz se é texto_discussao ou texto_didatico pois a tabela texto_discussao se uniu com texto_didatico @author Edson Anibal (ambar@info.ufrn.br) */
	@Column(name="texto_discussao")
	private Boolean textoDiscussao;

	@JoinColumn(name = "id_tipo_instancia", referencedColumnName = "id_tipo_instancia")
	@ManyToOne(fetch = FetchType.EAGER)
	private TipoInstancia tipoInstancia = new TipoInstancia();


	/** Creates a new instance of TextoDidatico */
	public TextoDidatico() {
	}

	public String getAutores() {
		return autores;
	}

	public void setAutores(String autores) {
		this.autores = autores;
	}

	public Boolean getDestaque() {
		return destaque;
	}

	public void setDestaque(Boolean destaque) {
		this.destaque = destaque;
	}

	/** Usada pelos Texto_Discussao, pois a tabela texto_discussao se uniu com texto_didatico @author Edson Anibal (ambar@info.ufrn.br) */
	public Boolean getPremiada() { return premiada;	}
	/** Usada pelos Texto_Discussao, pois a tabela texto_discussao se uniu com texto_didatico @author Edson Anibal (ambar@info.ufrn.br) */
	public void setPremiada(Boolean premiada) {	this.premiada = premiada; }

	/**Diz se é texto_discussao ou texto_didatico pois a tabela texto_discussao se uniu com texto_didatico @author Edson Anibal (ambar@info.ufrn.br) */
	public Boolean getTextoDiscussao() { return textoDiscussao; }
	/**Diz se é texto_discussao ou texto_didatico pois a tabela texto_discussao se uniu com texto_didatico @author Edson Anibal (ambar@info.ufrn.br) */
	public void setTextoDiscussao(Boolean textoDiscussao) { this.textoDiscussao = textoDiscussao; }


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

	public TipoInstancia getTipoInstancia() {
		return tipoInstancia;
	}

	public void setTipoInstancia(TipoInstancia tipoInstancia) {
		this.tipoInstancia = tipoInstancia;
	}

	/*
	 * Campos Obrigatórios: Titulo, Participacao, Local, Data, Tipo Instancia
	 */

	@Override
	public ListaMensagens validate(){

		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(textoDiscussao, "Tipo de Texto", lista);

		if (!ValidatorUtil.isEmpty(textoDiscussao) && !textoDiscussao) //Só valida se for texto_didatico
		   ValidatorUtil.validateRequiredId(getTipoInstancia().getId(), "Tipo Instância", lista);

		ValidatorUtil.validateRequired(getAutores(), "Autores", lista);
		ValidatorUtil.validateRequired(getArea(), "Área", lista);
		ValidatorUtil.validateRequired(getSubArea(), "Sub-Área", lista);
		ValidatorUtil.validatePagina((paginaInicial!=null?paginaInicial:0), (paginaFinal!=null?paginaFinal:0), lista);


		lista.addAll(super.validate().getMensagens());

		return lista;
	}

}
