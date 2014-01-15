/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 16/04/2010
 */
package br.ufrn.ambientes.dominio;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.annotations.Required;

/**
 * Utilizada para registrar os possíveis tipos de ambientes
 * (que não o de produção) que podem ter a segurança reforçada
 * para usuários externos.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
@Entity @Table(name="tipo_ambiente", schema="comum")
public class TipoAmbiente implements PersistDB {

	public static final int DESENVOLVIMENTO = 1;
	public static final int TREINAMENTO = 2;
	public static final int PRODUCAO = 3;
	public static final int HOMOLOGACAO = 4;

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="comum.tipo_ambiente_seq") })
	private int id;
	
	/** Nome do ambiente */
	@Required
	private String nome;
	
	/** Se o ambiente está ativo ou não */
	@CampoAtivo
	private boolean ativo;

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

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
}
