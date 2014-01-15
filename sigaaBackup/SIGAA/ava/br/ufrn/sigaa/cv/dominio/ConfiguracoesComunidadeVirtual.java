/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 12/12/2011
 *
 */
package br.ufrn.sigaa.cv.dominio;

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

import br.ufrn.sigaa.ava.util.HumanName;

/**
 *  Classe de domínio que guarda as configurações para uma comunidade virtual.<br/>
 * 
 * @author Diego Jácome
 *
 */
@Entity @Table(name="configuracoes_cv", schema="cv")
@HumanName(value="Configurações", genero='F')
public class ConfiguracoesComunidadeVirtual implements DominioComunidadeVirtual {

	/** Define o valor para ordenar os tópicos em ordem decrescente. */
	public static char ORDEM_TOPICO_DECRESCENTE = 'D';
	/** Define o valor para ordenar os tópicos em ordem crescente. */
	public static char ORDEM_TOPICO_CRESCENTE = 'C';
	/** Define o valor para que o professor ordene os tópicos de forma livre. */
	public static char ORDEM_TOPICO_LIVRE = 'L';
	
	/**
	 * Define a unicidade da configuração da comunidade virtual.
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_configuracoes_cv", nullable = false)
	private int id;

	/** Associa a as configurações a comunidade virtual */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_comunidade_virtual")
	private ComunidadeVirtual comunidade;
	
	/** Forma na qual será ordenada os tópicos da comunidade */
	@Column(name="ordem_topico")
	private Character ordemTopico;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ComunidadeVirtual getComunidade() {
		return comunidade;
	}

	public void setComunidade(ComunidadeVirtual comunidade) {
		this.comunidade = comunidade;
	}

	@Override
	public String getMensagemAtividade() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setOrdemTopico(Character ordemTopico) {
		this.ordemTopico = ordemTopico;
	}

	public Character getOrdemTopico() {
		return ordemTopico;
	}

	public boolean isOrdemTopicoDecrescente () {
		return ordemTopico != null && ordemTopico.equals(ORDEM_TOPICO_DECRESCENTE);
	}

	public boolean isOrdemTopicoCrescente () {
		return ordemTopico != null && ordemTopico.equals(ORDEM_TOPICO_CRESCENTE);
	}
	
	public boolean isOrdemTopicoLivre () {
		return ordemTopico != null && ordemTopico.equals(ORDEM_TOPICO_LIVRE);
	}
}
