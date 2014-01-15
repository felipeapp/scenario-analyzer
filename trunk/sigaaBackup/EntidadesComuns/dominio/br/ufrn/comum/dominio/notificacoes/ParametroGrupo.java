/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: Livraria
 * Data de Criação: 03/08/2009
 */
package br.ufrn.comum.dominio.notificacoes;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/**
 * Define os parâmetros que podem ser utilizados em um grupo
 * de destinatários do e-Comunicação.
 * 
 * @author David Pereira
 *
 */
@Entity @Table(name="parametro_grupo", schema="comum")
public class ParametroGrupo implements Validatable {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	private int id;
	
	/** Nome do grupo de destinatários. Será mostrado ao usuário na hora de enviar mensagem para o grupo. */
	private String nome;
	
	/** Grupo de destinatários ao qual o parâmetro pertence */
	@ManyToOne @JoinColumn(name="id_grupo_destinatarios")
	private GrupoDestinatarios grupo;
	
	/** Consulta que retornará pares (id, descricao) com os valores do combo com as possibilidades de valor para o parâmetro. */
	@Column(name="select_combo")
	private String selectCombo;
	
	/** Se o grupo tem restrição de usuários, ou seja, se alguns usuários podem ver mais valores que outros. */
	@Column(name="restricao_usuario")
	private boolean restricaoUsuario;
	
	/** Consulta para identificar se o usuário possui acesso irrestrito ao grupo ou não. */
	@Column(name="select_restricao_usuario")
	private String selectRestricaoUsuario;
	
	/** Consulta que retornará pares (id, descricao) com os valores do combo com as possibilidades de valor para o parâmetro. 
	 * Recebe como parâmetro o usuário logado. */
	@Column(name="select_combo_restrito")
	private String selectComboRestrito;
	
	/** Se o parâmetro é obrigatório ou não na hora de enviar uma mensagem a um grupo. */
	private Boolean obrigatorio = true;
	
	/** Valor default, utilizado quando o parâmetro não for obrigatório e o usuário não informar o valor. */
	@Column(name="valor_default")
	private String valorDefault;
	
	/** SQL Type do parâmetro. Utilizado para a hora de realizar a atribuição do parâmetro na consulta SQL dos membros do grupo. */
	private int tipo;
	
	/** Valor atribuído ao parâmetro na hora da seleção do grupo de comunicação. Não persistido. */
	@Transient 
	private String valor;

	/** Lista de possíveis valores do parâmetro. */
	@Transient 
	private List<SelectItem> valoresParametro;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public GrupoDestinatarios getGrupo() {
		return grupo;
	}

	public void setGrupo(GrupoDestinatarios grupo) {
		this.grupo = grupo;
	}

	public String getSelectCombo() {
		return selectCombo;
	}

	public void setSelectCombo(String selectCombo) {
		this.selectCombo = selectCombo;
	}

	public Boolean getObrigatorio() {
		return obrigatorio;
	}

	public void setObrigatorio(Boolean obrigatorio) {
		this.obrigatorio = obrigatorio;
	}

	public String getValorDefault() {
		return valorDefault;
	}

	public void setValorDefault(String valorDefault) {
		this.valorDefault = valorDefault;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParametroGrupo other = (ParametroGrupo) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ParametroGrupo [grupo=" + grupo + ", id=" + id + ", nome="
				+ nome + ", obrigatorio=" + obrigatorio + ", restricaoUsuario="
				+ restricaoUsuario + ", selectCombo=" + selectCombo + ", tipo="
				+ tipo + ", valor=" + valor + ", valorDefault=" + valorDefault
				+ "]";
	}

	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		validateRequired(nome, "Nome", erros);
		validateRequired(selectCombo, "Select combo", erros);
		if (!obrigatorio) {
			validateRequired(valorDefault, "Valor padrão", erros);
		}
		return erros;
	}

	public List<SelectItem> getValoresParametro() {
		return valoresParametro;
	}

	public void setValoresParametro(List<SelectItem> valoresParametro) {
		this.valoresParametro = valoresParametro;
	}

	public boolean isRestricaoUsuario() {
		return restricaoUsuario;
	}

	public void setRestricaoUsuario(boolean restricaoUsuario) {
		this.restricaoUsuario = restricaoUsuario;
	}

	public String getSelectRestricaoUsuario() {
		return selectRestricaoUsuario;
	}

	public void setSelectRestricaoUsuario(String selectRestricaoUsuario) {
		this.selectRestricaoUsuario = selectRestricaoUsuario;
	}

	public String getSelectComboRestrito() {
		return selectComboRestrito;
	}

	public void setSelectComboRestrito(String selectComboRestrito) {
		this.selectComboRestrito = selectComboRestrito;
	}

}