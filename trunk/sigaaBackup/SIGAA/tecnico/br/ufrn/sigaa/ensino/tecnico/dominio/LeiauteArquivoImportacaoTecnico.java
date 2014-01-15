/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/10/2012
 *
 */
package br.ufrn.sigaa.ensino.tecnico.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.ArrayList;
import java.util.Collection;

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

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;

/**
 * Define um leiaute de dados de um arquivo que é utilizado na importação de
 * discentes aprovados em outros concursos como, por exemplo, o SiSU.
 * 
 * @author Édipo Elder F. de Melo
 * @author Fred_Castro
 * 
 */
@Entity
@Table(name = "leiaute_arquivo_importacao_tecnico", schema = "tecnico")
public class LeiauteArquivoImportacaoTecnico implements Validatable {

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_leiaute_arquivo_importacao_tecnico", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Descrição do leiaute utilizado. Exemplo: Arquivo de importação do SiSU 2011 */
	private String descricao;
	
	/** Este leiaute é utilizado para importação de dados de um determinada Forma de Ingresso como, por exemplo, SiSU. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_forma_ingresso",nullable=false)
	private FormaIngresso formaIngresso;
	
	/** Coleção de mapeamentos a serem utilizados na importação de arquivos. */
	@OneToMany(fetch = FetchType.EAGER, mappedBy="leiauteArquivoImportacao")
	private Collection<MapeamentoAtributoCampoTecnico> mapeamentoAtributos;

	/** Indica se este leiaute está ativo para utilização no sistema. */
	@CampoAtivo
	private boolean ativo;

	/** Indica se a primeira linha do arquivo de importação de dados possui um cabeçalho. */
	@Column(name="possui_cabecalho")
	private boolean possuiCabecalho;
	
	/**
	 * Construtor padrão.
	 */
	public LeiauteArquivoImportacaoTecnico() {
		super();
		formaIngresso = new FormaIngresso();
		mapeamentoAtributos = new ArrayList<MapeamentoAtributoCampoTecnico>();
	}

	/** Valida os dados a serem persistidos.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		validateRequired(descricao, "Descrição", lista);
		validateRequired(mapeamentoAtributos, "Mapeamento entre atributos e campos", lista);
		return lista;
	}

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

	public Collection<MapeamentoAtributoCampoTecnico> getMapeamentoAtributos() {
		return mapeamentoAtributos;
	}

	public void setMapeamentoAtributos(
			Collection<MapeamentoAtributoCampoTecnico> mapeamentoAtributos) {
		this.mapeamentoAtributos = mapeamentoAtributos;
	}

	/** Retorna uma representação textual deste leiaute
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(descricao).append(": ");
		for (MapeamentoAtributoCampoTecnico hist : mapeamentoAtributos)
			str.append(hist.getCampo()).append(" -> ").append(hist.getAtributoMapeavel().getAtributo());
		return str.toString();
	}

	public FormaIngresso getFormaIngresso() {
		return formaIngresso;
	}

	public void setFormaIngresso(FormaIngresso formaIngresso) {
		this.formaIngresso = formaIngresso;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public boolean isPossuiCabecalho() {
		return possuiCabecalho;
	}

	public void setPossuiCabecalho(boolean possuiCabecalho) {
		this.possuiCabecalho = possuiCabecalho;
	}
	
}
