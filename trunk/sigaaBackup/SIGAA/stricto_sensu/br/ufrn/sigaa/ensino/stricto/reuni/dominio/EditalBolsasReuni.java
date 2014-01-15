/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.stricto.reuni.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validateMinValue;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.CompareToBuilder;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;

/**
 * Entidade que registra as datas, parâmetros e informações adicionais
 * referentes a um edital de concessão de bolsas REUNI de assistência ao ensino.
 * 
 * @author wendell
 *
 */
@Entity
@Table(name="edital_bolsas_reuni", schema="stricto_sensu")
public class EditalBolsasReuni implements Validatable{

	private int id;
	private String descricao;
	
	private Collection<ComponenteCurricular> componentesPrioritarios;
	
	private Date dataInicioSubmissao;
	private Date dataFimSubmissao;
	
	private Date dataInicioSelecao;
	private Date dataFimSelecao;
	
	private Integer idArquivoEdital;
	
	@CriadoPor
	private RegistroEntrada registroCadastro;
	@CriadoEm
	private Date dataCadastro;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_edital_bolsas_reuni")
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@ManyToMany
	@JoinTable(name = "componente_curricular_edital_reuni", schema="stricto_sensu",
		    joinColumns = @JoinColumn (name="id_edital_bolsas_reuni"),
		    inverseJoinColumns = @JoinColumn(name="id_componente_curricular"))		
	public Collection<ComponenteCurricular> getComponentesPrioritarios() {
		return componentesPrioritarios;
	}
	public void setComponentesPrioritarios(
			Collection<ComponenteCurricular> componentesPrioritarios) {
		this.componentesPrioritarios = componentesPrioritarios;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio_submissao")
	public Date getDataInicioSubmissao() {
		return dataInicioSubmissao;
	}
	public void setDataInicioSubmissao(Date dataInicioSubmissao) {
		this.dataInicioSubmissao = dataInicioSubmissao;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim_submissao")
	public Date getDataFimSubmissao() {
		return dataFimSubmissao;
	}
	public void setDataFimSubmissao(Date dataFimSubmissao) {
		this.dataFimSubmissao = dataFimSubmissao;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio_selecao")
	public Date getDataInicioSelecao() {
		return dataInicioSelecao;
	}
	public void setDataInicioSelecao(Date dataInicioSelecao) {
		this.dataInicioSelecao = dataInicioSelecao;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim_selecao")
	public Date getDataFimSelecao() {
		return dataFimSelecao;
	}
	public void setDataFimSelecao(Date dataFimSelecao) {
		this.dataFimSelecao = dataFimSelecao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro")
	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}
	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	public Date getDataCadastro() {
		return dataCadastro;
	}
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}
	
	@Column(name="id_arquivo_edital")
	public Integer getIdArquivoEdital() {
		return idArquivoEdital;
	}
	public void setIdArquivoEdital(Integer idArquivoEdital) {
		this.idArquivoEdital = idArquivoEdital;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		
		validateRequired(descricao, "Descrição", lista);
		validateRequired(dataInicioSubmissao, "Início do período de submissões de propostas", lista);

		validateRequired(dataFimSubmissao, "Fim do período de submissões de propostas", lista);
		validateMinValue(dataFimSubmissao, dataInicioSubmissao, "Período de submissões de propostas", lista);
		
		validateMinValue(dataFimSelecao, dataInicioSelecao, "Período de seleção de bolsistas", lista);
		
		return lista;
	}

	@Override
	public String toString() {
		return descricao;
	}
	
	/**
	 * Adiciona um componente curricular à lista de componentes prioritários
	 * 
	 * @param componente
	 */
	public void adicionarComponenteCurricular(ComponenteCurricular componente) {
		if (componentesPrioritarios == null) {
			componentesPrioritarios = new ArrayList<ComponenteCurricular>();	
		}
		if (!componentesPrioritarios.contains(componente)) {
			componentesPrioritarios.add(componente);
			Collections.sort((List<ComponenteCurricular>) componentesPrioritarios, new Comparator<ComponenteCurricular> () {
				public int compare(ComponenteCurricular c1, ComponenteCurricular c2) {
					return new CompareToBuilder()
						.append(c1.getCodigo(), c2.getCodigo())
						.toComparison();
				}
			});
		}
	}
	
	/**
	 * Remove um componente curricular da lista de componentes prioritários
	 * 
	 * @param componente
	 */
	public void removerComponenteCurricular(ComponenteCurricular componente) {
		if (componentesPrioritarios != null) {
			componentesPrioritarios.remove(componente);
		}
	}	
}
