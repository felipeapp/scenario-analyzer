/*
 * DescritorDadosRegistroControle.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * Campos Universit�rio Lagoa Nova
 * Natal - RN - Brasil
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import static br.ufrn.arq.util.StringUtils.unescapeHTML;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/**
 *
 *    Descritor que informa onde come�a e onde termina e o que significa algum 
 * dado que est� no campo de controle
 *
 * @author jadson
 * @since 28/07/2008
 * @version 1.0 criacao da classe
 *
 */
@Entity
@Table(name = "descritor_campo_controle", schema = "biblioteca")
public class DescritorCampoControle implements Validatable{

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })	
	@Column (name="id_descritor_campo_controle")
	private int id;

	/* Onde come�a o dado no campo de controle*/
	@Column(name = "posicao_inicio", nullable=false)
	private int posicaoInicio;

	/* Onde termina o dado no campo de controle*/
	@Column(name = "posicao_fim", nullable=false)
	private int posicaoFim;

	/* o que o dado que vai de posicaoInicio ate posicaoFim significa*/
	@Column(name = "descricao", nullable=false)
	private String descricao;

	/* multiplosValores indica que entre as posi��es dos dados descritos pelo descritor pode haver 
	 * V�rios valores, sendo valido apenas para o descritores que a dist�ncia entre 
	 * as posi��es fim e inicio for > 1 
	 * 
	 * Ex.:  campos idiomas geralmente possuem 3 posi��es com valores: 'eng', 'por', 'esp', isso � valor �nico para o campo.
	 *       <strong> O valor inteiro deve ser validado</strong>
	 *       valor multiplos, por exemplo, s�o os campos de 3 posi��es que podem assum os valores 'a' ou 'b' ou 'c' ou 'z'.
	 *       valores v�lidos: 'aaa', 'bbb', 'ccc', 'azb', 'bca'. valores n�o v�lidos: 'abd', 'abx', 'paa'. 
	 *       <strong>� preciso validar se cada parte possui o valor correto.</strong>
	 */
	@Column(name = "multiplos_valores", nullable=false)
	private boolean multiplosValores;


	/* Para etiquetas de controle != 007*/
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name="id_formato_material_etiqueta", referencedColumnName="id_formato_material_etiqueta")
	private FormatoMaterialEtiqueta formatoMaterialEtiqueta;



	/* Para a etiqueta 007 */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name="id_categoria_material", referencedColumnName="id_categoria_material")
	private CategoriaMaterial categoriaMaterial;


	/*
	 * Os dados para os quais esse descritor aponta possuem um conjunto de valores poss�veis.
	 */
	@OneToMany(mappedBy="descritorCampoControle", cascade={CascadeType.REMOVE})
	private List<ValorDescritorCampoControle> valoresDescritorCampoControle;

	private String info;

	/**
	 * M�todo que diz se o descritor possui m�ltiplos valores ou n�o.
	 * @return
	 */
	public boolean possuiMultiploValores(){
		return multiplosValores;
	}

	
	@Override
	public String toString() {
		return "DescritorCampoControle [id=" + id + ", posicaoInicio="
				+ posicaoInicio + ", posicaoFim=" + posicaoFim + ", descricao="
				+ descricao + ", multiplosValores=" + multiplosValores
				+ ", formatoMaterialEtiqueta=" + formatoMaterialEtiqueta
				+ ", categoriaMaterial=" + categoriaMaterial
				+ ", valoresDescritorCampoControle="
				+ valoresDescritorCampoControle + ", info=" + info + "]";
	}
	
	

	//set e gets indesejados


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;

	}

	public int getPosicaoInicio() {
		return posicaoInicio;
	}

	public void setPosicaoInicio(int posicaoInicio) {
		this.posicaoInicio = posicaoInicio;
	}

	public int getPosicaoFim() {
		return posicaoFim;
	}

	public void setPosicaoFim(int posicaoFim) {
		this.posicaoFim = posicaoFim;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = unescapeHTML(descricao);
	}


	public List<ValorDescritorCampoControle> getValoresDescritorCampoControle() {
		return valoresDescritorCampoControle;
	}

	public void setValoresDescritorCampoControle(List<ValorDescritorCampoControle> valoresDescritorCampoControle) {
		this.valoresDescritorCampoControle = valoresDescritorCampoControle;
	}
	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = unescapeHTML(info);
	}

	public CategoriaMaterial getCategoriaMaterial() {
		return categoriaMaterial;
	}

	public void setCategoriaMaterial(CategoriaMaterial categoriaMaterial) {
		this.categoriaMaterial = categoriaMaterial;
	}

	public boolean isMultiplosValores() {
		return multiplosValores;
	}

	public void setMultiplosValores(boolean multiplosValores) {
		this.multiplosValores = multiplosValores;
	}

	public FormatoMaterialEtiqueta getFormatoMaterialEtiqueta() {
		return formatoMaterialEtiqueta;
	}

	public void setFormatoMaterialEtiqueta(FormatoMaterialEtiqueta formatoMaterialEtiqueta) {
		this.formatoMaterialEtiqueta = formatoMaterialEtiqueta;
	}

	public ListaMensagens validate() {
		ListaMensagens mensagens = new ListaMensagens();
		if (posicaoInicio < 0 || posicaoFim < 0) {
			mensagens.addErro("As posi��es de in�cio e fim n�o podem possuir valores negativos");
		}
		if (posicaoFim < posicaoInicio) {
			mensagens.addErro("A Posi��o Fim tem que ser maior que a Posi��o In�cio");
		}
		if (descricao == null || "".equals(descricao)) {
			mensagens.addErro("Descri��o do Descritor do Campo de Controle � inv�lida");
		}
		return mensagens;
	}

}
