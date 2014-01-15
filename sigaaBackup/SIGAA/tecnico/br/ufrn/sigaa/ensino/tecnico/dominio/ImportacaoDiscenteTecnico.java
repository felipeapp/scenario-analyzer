/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/10/2012
 *
 */
package br.ufrn.sigaa.ensino.tecnico.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;

/** Dados de importação de discentes que foram aprovados no processo seletivo.
 * 
 * @author Édipo Elder F. de Melo
 * @author Fred_Castro
 *
 */
@Entity
@Table(name = "importacao_discente_tecnico", schema = "tecnico")
public class ImportacaoDiscenteTecnico implements PersistDB {
	
	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_importacao_discente_tecnico", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Registro de Entrada do usuário que cadastrou a convocação. */
	@CriadoPor
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada cadastradoPor;
	
	/** Data de cadastro da convocação. */
	@CriadoEm
	@Column(name = "data_cadastro")
	private Date dataCadastro;
	
	/** Lista de Resultados Importados. */
	@ManyToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="resultados_importados", schema="tecnico",
			joinColumns=@JoinColumn(name="id_importacao_discente_tecnico"),  
			inverseJoinColumns=@JoinColumn(name="id_resultado_classificacao"))
	private Collection<ResultadoClassificacaoCandidatoTecnico> resultadosImportados;
	
	/** Processo Seletivo para o qual os dados serão importados. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_processo_seletivo")
	private ProcessoSeletivoTecnico processoSeletivo;
	
	public ImportacaoDiscenteTecnico() {
		this.processoSeletivo = new ProcessoSeletivoTecnico();
		this.resultadosImportados = new ArrayList<ResultadoClassificacaoCandidatoTecnico>();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public RegistroEntrada getCadastradoPor() {
		return cadastradoPor;
	}

	public void setCadastradoPor(RegistroEntrada cadastradoPor) {
		this.cadastradoPor = cadastradoPor;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	/** Adiciona um discente à lista de discentes cadastrados.
	 * @param resultado
	 */
	public void addDiscente(ResultadoClassificacaoCandidatoTecnico resultado) {
		if (this.resultadosImportados == null)
			resultadosImportados = new ArrayList<ResultadoClassificacaoCandidatoTecnico>();
		resultadosImportados.add(resultado);
	}
	
	@Override
	public String toString() {
		return processoSeletivo != null ? processoSeletivo.getNome() : "";
	}

	public ProcessoSeletivoTecnico getProcessoSeletivo() {
		return processoSeletivo;
	}

	public void setProcessoSeletivo(ProcessoSeletivoTecnico processoSeletivo) {
		this.processoSeletivo = processoSeletivo;
	}

	public Collection<ResultadoClassificacaoCandidatoTecnico> getResultadosImportados() {
		return resultadosImportados;
	}

	public void setResultadosImportados(
			Collection<ResultadoClassificacaoCandidatoTecnico> resultadosImportados) {
		this.resultadosImportados = resultadosImportados;
	}
}
