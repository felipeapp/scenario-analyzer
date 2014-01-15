/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validateMinLength;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;

/**
 * Define os tipo de participações no ENADE - Exame Nacional de Desempenho de
 * Estudantes
 * 
 * @author Édipo Elder F. de Melo
 */
@Entity
@Table(name = "participacao_enade", schema = "graduacao")
public class ParticipacaoEnade implements Validatable {
	
	/** Ano de início do cadastrado do ENADE INGRESSANTE. */ 
	public static final int ANO_INICIO_ENADE_INGRESSANTE = ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.ANO_INICIO_ENADE_INGRESSANTE);
	/** Ano de início do cadastrado do ENADE CONCLUINTE. */
	public static final int ANO_INICIO_ENADE_CONCLUINTE = ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.ANO_INICIO_ENADE_CONCLUINTE);

	/** Chave Primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_participacao_enade", unique = true, nullable = false)
	private int id;
	
	/** Tipo do ENADE: Ingressante ou Concluinte */
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "tipo_enade", nullable = false)
	private TipoENADE tipoEnade;
	
	/** Descrição do tipo de participação no ENADE. */
	private String descricao;
	
	/** Indica se a participação é pendente. */
	@Column(name = "participacao_pendente")
	private boolean participacaoPendente;
	
	/** Indica se o tipo de participação é ativa para utilização no SIGAA. */
	@CampoAtivo
	private boolean ativo;

	public ParticipacaoEnade() {
	}
	
	public ParticipacaoEnade(int id) {
		super();
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public TipoENADE getTipoEnade() {
		return tipoEnade;
	}

	public void setTipoEnade(TipoENADE tipoEnade) {
		this.tipoEnade = tipoEnade;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public boolean isParticipacaoPendente() {
		return participacaoPendente;
	}

	public void setParticipacaoPendente(boolean participacaoPendente) {
		this.participacaoPendente = participacaoPendente;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	@Override
	public String toString() {
		return descricao;
	}

	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		validateRequired(tipoEnade, "Tipo ENADE", lista);
		validateMinLength(descricao, 5, "Descrição", lista);
		return lista;
	}
}
