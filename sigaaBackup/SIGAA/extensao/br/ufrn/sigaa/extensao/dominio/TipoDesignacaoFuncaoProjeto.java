package br.ufrn.sigaa.extensao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

@Entity
@Table(schema = "extensao", name = "tipo_designacao_funcao_projeto")
public class TipoDesignacaoFuncaoProjeto implements Validatable {

	
	public static final Integer GERENCIAR_PARTICIPANTES_EXTENSAO = 1;
	public static final Integer FUNCAO_COORDENACAO_EXTENSAO = 2;
	
	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
    @Column(name = "id_tipo_designacao_funcao_projeto", nullable = false)
	private int id;

	private String denominacao;

	private boolean extensao;
	private boolean monitoria;
	private boolean associados;
	private boolean pesquisa;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDenominacao() {
		return denominacao;
	}
	public void setDenominacao(String denominacao) {
		this.denominacao = denominacao;
	}
	public boolean isExtensao() {
		return extensao;
	}
	public void setExtensao(boolean extensao) {
		this.extensao = extensao;
	}
	public boolean isMonitoria() {
		return monitoria;
	}
	public void setMonitoria(boolean monitoria) {
		this.monitoria = monitoria;
	}
	public boolean isAssociados() {
		return associados;
	}
	public void setAssociados(boolean associados) {
		this.associados = associados;
	}
	public boolean isPesquisa() {
		return pesquisa;
	}
	public void setPesquisa(boolean pesquisa) {
		this.pesquisa = pesquisa;
	}
	
	@Override
	public ListaMensagens validate() {
		return null;
	}
	
}