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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Entidade que registra os tipos de participação que um docente pode ter em uma produção intelectual
 *
 * @author Gleydson
 */
@Entity
@Table(name = "tipo_participacao", schema = "prodocente")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class TipoParticipacao implements Validatable {

	public static final int AUTOR_AUDIO_VISUAIS = 1;

	public static final int DIRETOR_AUDIO_VISUAL = 2;

	public static final int PRODUTOR_AUDIO_VISUAL = 3;

	public static final int INTERPRETE_AUDIO_VISUAL = 4;

	public static final int SONOPLASTA_AUDIO_VISUAL = 5;

	public static final int OUTRO_AUDIO_VISUAL = 6;

	public static final int PRODUTOR_PROGRAMACAO_VISUAL = 7;

	public static final int COLABORADOR_PROGRAMACAO_VISUAL = 8;

	public static final int OUTRO_PROGRAMACAO_VISUAL = 9;

	public static final int PRODUTOR_MONTAGEM = 10;

	public static final int COLABORADOR_MONTAGEM = 11;

	public static final int OUTRO_MONTAGEM = 12;

	public static final int TRABALHO_INDIVIDUAL_ARTIGO_PERIODICOS_JORNAIS = 13;

	public static final int EQUIPE_RESPONSAVEL_ARTIGO_PERIODICOS_JORNAIS = 14;

	public static final int EQUIPE_COLABORADOR_ARTIGO_PERIODICOS_JORNAIS = 16;

	public static final int TRADUTOR_ARTIGO_PERIODICOS_JORNAIS = 17;

	public static final int OUTROS_ARTIGO_PERIODICOS_JORNAIS = 18;

	/** Usados também em PRODUCAO TECNOLOGICA = Texto Discussão */
	public static final int TRABALHO_INDIVIDUAL_TEXTO_DISCUSSAO = 19;

	public static final int EQUIPE_RESPONSAVEL_TEXTO_DISCUSSAO = 20;

	public static final int EQUIPE_COLABORADOR_TEXTO_DISCUSSAO = 21;

	public static final int OUTROS_TEXTO_DISCUSSAO = 22;

	public static final int TRABALHO_INDIVIDUAL_LIVRO = 23;

	public static final int EQUIPE_RESPONSAVEL_LIVRO = 24;

	public static final int EQUIPE_COLABORADOR_LIVRO = 25;

	public static final int PREFACIADOR_LIVRO = 26;

	public static final int TRADUTOR_LIVRO = 27;

	public static final int EDITOR_LIVRO = 28;

	public static final int ORGANIZADOR_LIVRO = 29;

	public static final int ILUSTRADOR_LIVRO = 30;

	public static final int OUTROS_LIVRO = 31;

	public static final int TRABALHO_INDIVIDUAL_PUBLICACAO_EVENTO = 32;

	public static final int EQUIPE_RESPONSAVEL_PUBLICACAO_EVENTO = 33;

	public static final int EQUIPE_COLABORADOR_PUBLICACAO_EVENTO = 34;

	public static final int OUTRA_PUBLICACAO_EVENTO = 35;

	public static final int COORDENADOR_AUDIO_VISUAL = 38;

	public static final int COORDENADOR_PUBLICACAO_EVENTO = 39;

	public static final int PALESTRANTE_MINISTRANTE_PUBLICACAO_EVENTO = 40;

	public static final int COLABORADOR_PUBLICACAO_EVENTO = 41;

	/** Tipo de participação para as produções que não possuem um tipo de participação */
	public static final int AUTOR_GENERICO = 99;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_tipo_participacao", nullable = false)
	private int id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_producao")
	private TipoProducao tipoProducao = new TipoProducao();

	@Column(name = "descricao")
	private String descricao;

	private boolean ativo;
	
	public TipoParticipacao() {

	}
	public TipoParticipacao(int id) {
		this.id=id;
	}

	public Integer getIdObject() {
		return id;
	}

	public void setIdObject(Integer idObject) {
		if ( idObject != null ) {
			id = idObject;
		}
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

	public void setId(Integer id) {
		this.id = 0;
	}

	public TipoProducao getTipoProducao() {
		return tipoProducao;
	}

	public void setTipoProducao(TipoProducao tipoProducao) {
		this.tipoProducao = tipoProducao;
	}

	public boolean isAtivo() {
		return ativo;
	}
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	/*
	 * Campo Obrigatorio: Descricao, Tipo Producao
	 */
	public ListaMensagens validate(){

		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(getDescricao(),"Descrição", lista);
		ValidatorUtil.validateRequiredId(getTipoProducao().getId(), "Tipo Produção", lista);

		return lista;
	}
}