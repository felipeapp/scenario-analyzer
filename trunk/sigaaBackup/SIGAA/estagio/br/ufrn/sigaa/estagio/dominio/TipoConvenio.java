/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 28/09/2010
 */
package br.ufrn.sigaa.estagio.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Entidade que representa os Tipos de Conv�nios de Est�gios.
 * 
 * @author arlindo
 *
 */
@Entity
@Table(name = "tipo_convenio", schema = "estagio")
public class TipoConvenio implements PersistDB {
	
	/** Indica que o conv�nio � est�gio curricular obrigat�rio. */
	public static final int ESTAGIO_CURRICULAR_OBRIGATORIO = 1;
	/** Indica que o conv�nio � est�gio curricular n�o obrigat�rio. */
	public static final int ESTAGIO_CURRICULAR_NAO_OBRIGATORIO = 2;
	/** Indica que o conv�nio � est�gio curricular obrigat�rio/n�o obrigat�rio. */
	public static final int ESTAGIO_CURRICULAR_OBRIGATORIO_NAO_OBRIGATORIO = 3;
	
	/** Chave prim�ria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="estagio.hibernate_sequence") })	
	@Column(name = "id_tipo_convenio")
	private int id;
	
	/** Descri��o do Tipo do Conv�nio */
	private String descricao;
	
	/** Texto explicativo sobre o tipo */
	@Column(name = "texto_detalhe")
	private String textoDetalhe;
	
	/** Indica se permite realizar solicita��o de conv�nio de est�gio */
	@Column(name = "permite_solicitacao")
	private boolean permiteSolicitacao;
	
	/** Indica se permite realizar cadastro avulso de est�gio direto pela coordena��o. 
	 * � utilizando para os agentes de integra��o externos (CIEE, IEL...). */
	@Column(name = "permite_cadastro_direto_coordenacao")
	private boolean permiteCadastroDiretoCoordenacao;
	
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

	public String getTextoDetalhe() {
		return textoDetalhe;
	}

	public void setTextoDetalhe(String textoDetalhe) {
		this.textoDetalhe = textoDetalhe;
	}

	public boolean isPermiteSolicitacao() {
		return permiteSolicitacao;
	}

	public void setPermiteSolicitacao(boolean permiteSolicitacao) {
		this.permiteSolicitacao = permiteSolicitacao;
	}

	public boolean isPermiteCadastroDiretoCoordenacao() {
		return permiteCadastroDiretoCoordenacao;
	}

	public void setPermiteCadastroDiretoCoordenacao(
			boolean permiteCadastroDiretoCoordenacao) {
		this.permiteCadastroDiretoCoordenacao = permiteCadastroDiretoCoordenacao;
	}

}
