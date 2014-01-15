package br.ufrn.rh.dominio;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.AbstractMovimento;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.HibernateUtils;

/**
 * Classe que contém dados sobre as condições de trabalho de uma unidade organizacional
 * @author Itamir Filho
 *
 */

@Entity
@Table(schema = "comum", name = "processo_trabalho")
public class ProcessoTrabalho extends AbstractMovimento implements PersistDB{
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	@Column(name = "id_unidade")
	private int idUnidade;
	
	@Column(name = "id_usuario")
	private int idUsuario;
	
	@Column(name = "data_cadastro")
	private Date dataCadastro;
	
	@Column(name = "atividades_possiveis", columnDefinition= HibernateUtils.TEXT_COLUMN_DEFINITION)
	private String atividadesPossiveis;
	
	//atividades possíveis
	@ManyToMany(fetch = FetchType.LAZY)
 	@JoinTable( name = "proctrab_atividade_ambiente", schema="comum", joinColumns = @JoinColumn(name = "id_processo_trabalho"), inverseJoinColumns = @JoinColumn(name = "id_atividade_ambiente")) 
	private List<AtividadeAmbiente> atividadesAmbiente;
	
	//tecnologias utilizadas
	//atividades referentes aos modulos selecionados
	@ManyToMany(fetch = FetchType.LAZY)
 	@JoinTable( name = "proctrab_atividade_modulo", schema="comum", joinColumns = @JoinColumn(name = "id_processo_trabalho"), inverseJoinColumns = @JoinColumn(name = "id_atividade_modulo"))
	private List<ModuloAtividade> atividadesModulo;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdUnidade() {
		return idUnidade;
	}

	public void setIdUnidade(int idUnidade) {
		this.idUnidade = idUnidade;
	}

	public String getAtividadesPossiveis() {
		return atividadesPossiveis;
	}

	public void setAtividadesPossiveis(String atividadesPossiveis) {
		this.atividadesPossiveis = atividadesPossiveis;
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public List<AtividadeAmbiente> getAtividadesAmbiente() {
		return atividadesAmbiente;
	}

	public void setAtividadesAmbiente(List<AtividadeAmbiente> atividadesAmbiente) {
		this.atividadesAmbiente = atividadesAmbiente;
	}

	public List<ModuloAtividade> getAtividadesModulo() {
		return atividadesModulo;
	}

	public void setAtividadesModulo(List<ModuloAtividade> atividadesModulo) {
		this.atividadesModulo = atividadesModulo;
	}
	
}
