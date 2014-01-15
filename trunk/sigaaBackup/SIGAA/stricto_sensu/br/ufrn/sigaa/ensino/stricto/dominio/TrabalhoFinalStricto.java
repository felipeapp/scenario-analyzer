/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.stricto.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;

/**
 * @author Andre Dantas
 */
@Entity
@Table(name = "trabalho_final_stricto", schema = "stricto_sensu")
public class TrabalhoFinalStricto implements PersistDB, Validatable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_trabalho_final_stricto", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	private String titulo;

	private Integer paginas;

	private String resumo;

	@ManyToOne
	@JoinColumn(name = "id_discente")
	private DiscenteStricto discente;

	@ManyToOne
	@JoinColumn(name = "id_area")
	private AreaConhecimentoCnpq area;

	@ManyToOne
	@JoinColumn(name = "id_subarea")
	private AreaConhecimentoCnpq subArea;
	
	@Column(name="id_arquivo")
	private int idArquivo;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public Integer getPaginas() {
		return paginas;
	}

	public void setPaginas(Integer paginas) {
		this.paginas = paginas;
	}

	public String getResumo() {
		return resumo;
	}

	public void setResumo(String resumo) {
		this.resumo = resumo;
	}

	public DiscenteStricto getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteStricto discente) {
		this.discente = discente;
	}

	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		validateRequired(discente, "Discente", erros);
		validateRequired(titulo, "Título", erros);
		validateRequired(paginas, "Páginas", erros);
		validateRequired(resumo, "Resumo", erros);
		validateRequired(area, "Área", erros);
		validateRequired(subArea, "Sub Área", erros);

		return erros;
	}

	public AreaConhecimentoCnpq getArea() {
		return area;
	}

	public void setArea(AreaConhecimentoCnpq area) {
		this.area = area;
	}

	public AreaConhecimentoCnpq getSubArea() {
		return subArea;
	}

	public void setSubArea(AreaConhecimentoCnpq subArea) {
		this.subArea = subArea;
	}

	public String getDescricao() {
		return titulo ;
	}

	public int getIdArquivo() {
		return idArquivo;
	}

	public void setIdArquivo(int idArquivo) {
		this.idArquivo = idArquivo;
	}

}
