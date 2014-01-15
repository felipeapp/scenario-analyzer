/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 27/01/2012
 *
 */
package br.ufrn.sigaa.vestibular.dominio;

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

/** Dados de importação de discentes de outros concursos que não o Vestibular como, 
 * por exemplo, o SiSU.
 * @author Édipo Elder F. de Melo
 *
 */
@Entity
@Table(name = "importacao_discente_outros_concurso", schema = "vestibular")
public class ImportacaoDiscenteOutrosConcursos implements PersistDB {
	
	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_importacao_discente_outros_concurso", unique = true, nullable = false, insertable = true, updatable = true)
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
	@JoinTable(name="resultados_importados_outros_concursos", schema="vestibular",
			joinColumns=@JoinColumn(name="id_importacao_discente_outros_concurso"),  
			inverseJoinColumns=@JoinColumn(name="id_resultado_opcao_curso"))
	private Collection<ResultadoOpcaoCurso> resultadosOpcaoCursoImportados;
	
	/** Processo Seletivo para o qual os dados serão importados. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_processo_seletivo")
	private ProcessoSeletivoVestibular processoSeletivo;
	
	public ImportacaoDiscenteOutrosConcursos() {
		this.processoSeletivo = new ProcessoSeletivoVestibular();
		this.resultadosOpcaoCursoImportados = new ArrayList<ResultadoOpcaoCurso>();
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
	public void addDiscente(ResultadoOpcaoCurso resultado) {
		if (this.resultadosOpcaoCursoImportados == null)
			resultadosOpcaoCursoImportados = new ArrayList<ResultadoOpcaoCurso>();
		resultadosOpcaoCursoImportados.add(resultado);
	}
	
	@Override
	public String toString() {
		return processoSeletivo != null ? processoSeletivo.getNome() : "";
	}

	public ProcessoSeletivoVestibular getProcessoSeletivo() {
		return processoSeletivo;
	}

	public void setProcessoSeletivo(ProcessoSeletivoVestibular processoSeletivo) {
		this.processoSeletivo = processoSeletivo;
	}

	public Collection<ResultadoOpcaoCurso> getResultadosOpcaoCursoImportados() {
		return resultadosOpcaoCursoImportados;
	}

	public void setResultadosOpcaoCursoImportados(
			Collection<ResultadoOpcaoCurso> resultadosOpcaoCursoImportados) {
		this.resultadosOpcaoCursoImportados = resultadosOpcaoCursoImportados;
	}
}
