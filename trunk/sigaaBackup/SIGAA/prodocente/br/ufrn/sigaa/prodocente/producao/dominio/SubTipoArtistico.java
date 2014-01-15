/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '26/10/2006'
 *
 */
package br.ufrn.sigaa.prodocente.producao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Entidade que armazena os sub-tipos de produções artísticas, literárias ou visuais
 *
 * @author Gleydson
 */
@Entity
@Table(name = "sub_tipo_artistico", schema = "prodocente")
public class SubTipoArtistico implements Validatable {

	public static final int FILME_CURTA_DURACAO_AUDIO_VISUAL = 1;

	public static final int FILME_MEDIA_DURACAO_AUDIO_VISUAL = 2;

	public static final int FILME_LONGA_DURACAO_AUDIO_VISUAL = 3;

	public static final int VIDEO_AUDIO_VISUAL = 4;

	public static final int COMPOSICAO_MUSICAL_AUDIO_VISUAL = 5;

	public static final int PARTITURA_MUSICAL_AUDIO_VISUAL = 6;

	public static final int ARRANJO_MUSICAL_AUDIO_VISUAL = 7;

	public static final int FOTOGRAFIAS_AUDIO_VISUAL = 8;

	public static final int GRAVACAO_SOM_AUDIO_VISUAL = 9;

	public static final int CD_ROM_AUDIO_VISUAL = 10;

	public static final int OUTRA_CLASSIFICACAO_AUDIO_VISUAL = 11;

	public static final int CENICAS_MONTAGEM = 12;

	public static final int COREOGRAFIA_MONTAGENS = 13;

	public static final int FIGURINOS_ADERECOS_MONTAGENS = 14;

	public static final int CENARIOS_MONTAGENS = 15;

	public static final int OUTRO_TIPO_ESPECIFICO_MONTAGENS = 16;

	public static final int COREOGRAFO_PUBLICACAO_EVENTO = 17;

	public static final int LITERARIO_PUBLICACAO_EVENTO = 18;

	public static final int MUSICAL_PUBLICACAO_EVENTO = 19;

	public static final int OUTROS_PUBLICACAO_EVENTO = 20;

	public static final int TEATRAL_PUBLICACAO_EVENTO = 21;

	public static final int SONOPLASTIA_AUDIO_VISUAL= 22;

	public static final int CINEMA_AUDIO_VISUAL = 23;

	public static final int DESENHO_AUDIO_VISUAL = 24;

	public static final int ESCULTURA_AUDIO_VISUAL = 25;

	public static final int GRAVURAS_AUDIO_VISUAL = 26;

	public static final int INSTALACAO_AUDIO_VISUAL = 27;

	public static final int PINTURA_AUDIO_VISUAL = 28;

	public static final int TELEVISAO_AUDIO_VISUAL = 29;

	public static final int COREOGRAFO_EXPOSICAO_APRESENTACAO_EVENTOS = 30;

	public static final int LITERARIO_EXPOSICAO_APRESENTACAO_EVENTOS = 31;

	public static final int MUSICAL_EXPOSICAO_APRESENTACAO_EVENTOS = 32;

	public static final int OUTROS_EXPOSICAO_APRESENTACAO_EVENTOS = 33;

	public static final int TEATRAL_EXPOSICAO_APRESENTACAO_EVENTOS = 34;
	
	public static final int SUB_TIPO_ARTISTICO_GENERICO = 99; //USADO EM ProgramacaoVisual.MBEAN



	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_sub_tipo_artistico", nullable = false)
	private int id;

	@Column(name = "descricao")
	private String descricao;

	@JoinColumn(name = "id_tipo_producao", referencedColumnName = "id_tipo_producao")
	@ManyToOne
	private TipoProducao tipoProducao = new TipoProducao();

	private boolean ativo;
	
	/** Creates a new instance of SubTipoArtistico */
	public SubTipoArtistico() {
	}

	public SubTipoArtistico(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public TipoProducao getTipoProducao() {
		return tipoProducao;
	}


	public void setTipoProducao(TipoProducao tipoProducao) {
		this.tipoProducao = tipoProducao;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getIdObject() {
		return id;
	}

	public void setIdObject(Integer idObject) {
		if ( idObject != null ) {
			id = idObject;
		}
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/*
	 * Campo Obrigatorio: Descricao
	 */
	public ListaMensagens validate(){

		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(getDescricao(), "Descrição", lista);
		ValidatorUtil.validateRequiredId(getTipoProducao().getId(), "Tipo de Produção", lista);
		return lista;
	}
}