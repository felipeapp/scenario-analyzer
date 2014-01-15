/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criada em: 29/10/2009
 *
 */

package br.ufrn.sigaa.pid.dominio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;

/**
 * Classe utilizada pelo Plano Individual do Docente - PID.
 * Representa a CH de um Projeto (pode ser Pesquisa ou Extensão)
 *  
 * @author agostinho campos
 * 
 */

@Entity
@Table(name = "carga_horaria_projeto", schema = "pid")
public class CargaHorariaProjeto implements PersistDB, Validatable {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_carga_horaria_projeto")
	private int id;
	
	/**
	 * Carga Horária Projetos Pesquisa
	 */
	@Column(name="ch_pesquisa")
	private double chPesquisa;
	
	/**
	 * Valor em percentual do tempo dedicado pelo docente
	 */
	@Column(name="percentual_pesquisa")
	private Integer percentualPesquisa = 0;
	
	/**
	 * Carga Horária Projetos Extensão
	 */
	@Column(name="ch_extensao")
	private double chExtensao;
	
	/**
	 * Valor em percentual do tempo dedicado pelo docente
	 */
	@Column(name="percentual_extensao")
	private Integer percentualExtensao = 0;
	
	@Transient
	private  MembroProjeto membroProjeto;
	
	@OneToMany(mappedBy="cargaHorariaProjeto",fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name="id_carga_horaria_projeto")
	private List<ChProjetoPIDMembroProjeto> chProjetoPIDMembroProjeto = new ArrayList<ChProjetoPIDMembroProjeto>();
	
	public CargaHorariaProjeto() {}
	
	public CargaHorariaProjeto(int idCargaHorariaProjeto, int chSemanalPesquisa, int tipoProjeto, Date dataCadastro, String descricao, int anoProjeto, int idMembroProjeto, Integer chDedicada, String tituloProjeto) {
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Adiciona na lista e seta a associação bidirecional
	 * 
	 * @param chProjetoMembroProjeto
	 * @param membroProjeto
	 */
	public void addCHProjetoPIDMembroProjeto(ChProjetoPIDMembroProjeto chProjetoMembroProjeto, MembroProjeto membroProjeto) {
		chProjetoMembroProjeto.setCargaHorariaProjeto(this);
		chProjetoMembroProjeto.setMembroProjeto(membroProjeto);
		chProjetoPIDMembroProjeto.add(chProjetoMembroProjeto);
	}
	
	/**
	 * @return the chPesquisa
	 */
	public double getChPesquisa() {
		return chPesquisa;
	}

	/**
	 * @param chPesquisa the chPesquisa to set
	 */
	public void setChPesquisa(double chPesquisa) {
		this.chPesquisa = chPesquisa;
	}

	/**
	 * @return the chExtensao
	 */
	public double getChExtensao() {
		return chExtensao;
	}

	/**
	 * @param chExtensao the chExtensao to set
	 */
	public void setChExtensao(double chExtensao) {
		this.chExtensao = chExtensao;
	}

	public br.ufrn.sigaa.projetos.dominio.MembroProjeto getMembroProjeto() {
		return membroProjeto;
	}

	public void setMembroProjeto(
			br.ufrn.sigaa.projetos.dominio.MembroProjeto membroProjeto) {
		this.membroProjeto = membroProjeto;
	}

	/**
	 * @return the percentualPesquisa
	 */
	public Integer getPercentualPesquisa() {
		return percentualPesquisa;
	}

	/**
	 * @param percentualPesquisa the percentualPesquisa to set
	 */
	public void setPercentualPesquisa(Integer percentualPesquisa) {
		this.percentualPesquisa = percentualPesquisa;
	}

	/**
	 * @return the percentualExtensao
	 */
	public Integer getPercentualExtensao() {
		return percentualExtensao;
	}

	/**
	 * @param percentualExtensao the percentualExtensao to set
	 */
	public void setPercentualExtensao(Integer percentualExtensao) {
		this.percentualExtensao = percentualExtensao;
	}
	
	/**
	 * @return the chProjetoPIDMembroProjeto
	 */
	public List<ChProjetoPIDMembroProjeto> getChProjetoPIDMembroProjeto() {
		return chProjetoPIDMembroProjeto;
	}

	/**
	 * @param chProjetoPIDMembroProjeto the chProjetoPIDMembroProjeto to set
	 */
	public void setChProjetoPIDMembroProjeto(
			List<ChProjetoPIDMembroProjeto> chProjetoPIDMembroProjeto) {
		this.chProjetoPIDMembroProjeto = chProjetoPIDMembroProjeto;
	}

	/**
	 * Valida o preenchimento de alguns campos da classe 
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		return lista;
	}

}
