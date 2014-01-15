/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/10/2012
 *
 */
package br.ufrn.sigaa.ensino.tecnico.dominio;

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
 * Registra os motivos para o cancelamento de uma convocação do vestibular.
 * 
 * @author Leonardo Campos
 * @author Fred_Castro
 *
 */
@Entity
@Table(name = "motivo_cancelamento_convocacao_tecnico", schema = "tecnico", uniqueConstraints = {})
public class MotivoCancelamentoConvocacaoTecnico implements Validatable {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_motivo_cancelamento_convocacao_tecnico", nullable = false)
	private int id;
	
	private String descricao;
	
	public static final MotivoCancelamentoConvocacaoTecnico NAO_COMPARECIMENTO_CADASTRO = new MotivoCancelamentoConvocacaoTecnico(1, "NÃO COMPARECIMENTO AO CADASTRO");
	public static final MotivoCancelamentoConvocacaoTecnico RECONVOCACAO_PRIMEIRA_OPCAO = new MotivoCancelamentoConvocacaoTecnico(2, "RECONVOCAÇÃO PARA A PRIMEIRA OPÇÃO");
	public static final MotivoCancelamentoConvocacaoTecnico RECONVOCACAO_OUTRO_TURNO = new MotivoCancelamentoConvocacaoTecnico(3, "RECONVOCAÇÃO PARA OUTRO TURNO");
	public static final MotivoCancelamentoConvocacaoTecnico REGRA_REGULAMENTO = new MotivoCancelamentoConvocacaoTecnico(4, "REGRA DO REGULAMENTO");
	public static final MotivoCancelamentoConvocacaoTecnico NAO_CUMPRIMENTO_TERMO_COMPROMISSO = new MotivoCancelamentoConvocacaoTecnico(5, "NÃO CUMPRIMENTO DO TERMO DE COMPROMISSO");
	
	
	public MotivoCancelamentoConvocacaoTecnico() {
	}
	
	public MotivoCancelamentoConvocacaoTecnico(int id) {
		this.id = id;
	}
	
	public MotivoCancelamentoConvocacaoTecnico(int id, String descricao) {
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
		ValidatorUtil.validateRequired(descricao, "Descrição", lista);
		return lista;
	}
}
