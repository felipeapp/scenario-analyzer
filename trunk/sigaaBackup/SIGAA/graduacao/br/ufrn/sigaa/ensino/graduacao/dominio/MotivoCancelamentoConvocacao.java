/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 14/12/2010
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Registra os motivos para o cancelamento de uma convoca��o do vestibular.
 * 
 * @author Leonardo Campos
 *
 */
@Entity
@Table(name = "motivo_cancelamento_convocacao", schema = "graduacao", uniqueConstraints = {})
public class MotivoCancelamentoConvocacao implements Validatable {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_motivo_cancelamento_convocacao", nullable = false)
	private int id;
	
	private String descricao;
	
	public static final MotivoCancelamentoConvocacao NAO_COMPARECIMENTO_CADASTRO = new MotivoCancelamentoConvocacao(1, "N�O COMPARECIMENTO AO CADASTRO");
	public static final MotivoCancelamentoConvocacao RECONVOCACAO_PRIMEIRA_OPCAO = new MotivoCancelamentoConvocacao(2, "RECONVOCA��O PARA A PRIMEIRA OP��O");
	public static final MotivoCancelamentoConvocacao RECONVOCACAO_OUTRO_TURNO = new MotivoCancelamentoConvocacao(3, "RECONVOCA��O PARA OUTRO TURNO");
	public static final MotivoCancelamentoConvocacao REGRA_REGULAMENTO = new MotivoCancelamentoConvocacao(4, "REGRA DO REGULAMENTO");
	public static final MotivoCancelamentoConvocacao NAO_CUMPRIMENTO_TERMO_COMPROMISSO = new MotivoCancelamentoConvocacao(5, "N�O CUMPRIMENTO DO TERMO DE COMPROMISSO");
	public static final MotivoCancelamentoConvocacao DECISAO_CAMARA_GRADUACAO = new MotivoCancelamentoConvocacao(6, "DECIS�O DA C�MARA DE GRADUA��O");
	public static final MotivoCancelamentoConvocacao EXCEDENTE_NUMERO_VAGAS = new MotivoCancelamentoConvocacao(7, "A CONVOCA��O EXCEDE O N�MERO DE VAGAS");
	
	
	public MotivoCancelamentoConvocacao() {
	}
	
	public MotivoCancelamentoConvocacao(int id) {
		this.id = id;
	}
	
	public MotivoCancelamentoConvocacao(int id, String descricao) {
		this.id = id;
		this.descricao = descricao;
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

	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descri��o", lista);
		return lista;
	}
	
	
}
