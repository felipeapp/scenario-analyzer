/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 10/02/2009
 *
 */
package br.ufrn.sigaa.extensao.relatorio.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.ValidatorUtil;

/*******************************************************************************
 * <p>
 * Utilizado para enviar o arquivo completo do Relatório da Ação de Extensão
 * caso tenha sido elaborado também em outro formato (Word, Excel, PDF e
 * outros). Utilizado também para anexar outros documentos que o elaborador
 * julgar indispensáveis para aprovação do relatório.
 * </p>
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(schema = "extensao", name = "arquivo_relatorio_acao_extensao")
public class ArquivoRelatorioAcaoExtensao implements Validatable {

	/** Identificador único do arquivo do relatório. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_arquivo_relatorio_acao_extensao")           
	private int id;

	/**Descrição do arquivo */
	@Column(name = "descricao")
	private String descricao;

	/**Id do Arquivo */
	@Column(name = "id_arquivo")
	private Integer idArquivo;

	/** Relatório ao qual o arquivo está vinculado. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_relatorio_acao_extensao")
	private RelatorioAcaoExtensao relatorioAcaoExtensao;

	/** Informa se a foto esta ativa no sistema. */
	@Column(name = "ativo")
	private boolean ativo;

	/** default constructor */
	public ArquivoRelatorioAcaoExtensao() {
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getIdArquivo() {
		return idArquivo;
	}

	public void setIdArquivo(Integer idArquivo) {
		this.idArquivo = idArquivo;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descrição", lista);
		return lista;
	}

	public RelatorioAcaoExtensao getRelatorioAcaoExtensao() {
		return relatorioAcaoExtensao;
	}

	public void setRelatorioAcaoExtensao(
			RelatorioAcaoExtensao relatorioAcaoExtensao) {
		this.relatorioAcaoExtensao = relatorioAcaoExtensao;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "idArquivo");
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

}
