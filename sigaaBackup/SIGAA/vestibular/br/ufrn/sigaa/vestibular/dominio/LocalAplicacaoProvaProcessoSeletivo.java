/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 30/07/2008
 *
 */
package br.ufrn.sigaa.vestibular.dominio;

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

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Entidade que associa um local de aplica��o de prova a um processo seletivo
 * 
 * @author �dipo Elder
 * 
 */
@Entity
@Table(name = "local_aplicacao_prova_processo_seletivo", schema = "vestibular", uniqueConstraints = {})
public class LocalAplicacaoProvaProcessoSeletivo implements Validatable {

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_local_aplicacao_prova_processo_seletivo", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Local de aplica��o de prova associado. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_local_aplicacao_prova")
	private LocalAplicacaoProva localAplicacaoProva;

	/** Processo Seletivo associado. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_processo_seletivo")
	private ProcessoSeletivoVestibular processoSeletivoVestibular;

	/** Coordenador do local de aplica��o de prova. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_servidor")
	private Servidor coordenador;
	
	/** Nome do coordenador do local de aplica��o. */
	@Column(name = "coordenador")
	private String nomeCoordenador;

	/** Dados da reuni�o com os fiscais (local, data e hora). */
	@Column(name = "local_reuniao")
	private String localReuniao;

	/** Construtor padr�o. */
	public LocalAplicacaoProvaProcessoSeletivo() {
		this.localAplicacaoProva = new LocalAplicacaoProva();
		this.processoSeletivoVestibular = new ProcessoSeletivoVestibular();
	}

	/** Retorna o coordenador do local de aplica��o de prova. 
	 * @return
	 */
	public Servidor getCoordenador() {
		return coordenador;
	}

	/**
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Retorna o local de aplica��o de prova associado. 
	 * @return
	 */
	public LocalAplicacaoProva getLocalAplicacaoProva() {
		return localAplicacaoProva;
	}

	/** Retorna os dados da reuni�o com os fiscais (local, data e hora). 
	 * @return
	 */
	public String getLocalReuniao() {
		return localReuniao;
	}

	/** Retorna o processo Seletivo associado. 
	 * @return
	 */
	public ProcessoSeletivoVestibular getProcessoSeletivoVestibular() {
		return processoSeletivoVestibular;
	}

	/** Seta o coordenador do local de aplica��o de prova. 
	 * @param coordenador
	 */
	public void setCoordenador(Servidor coordenador) {
		this.coordenador = coordenador;
	}

	/**
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Seta o local de aplica��o de prova associado. 
	 * @param localAplicacaoProva
	 */
	public void setLocalAplicacaoProva(LocalAplicacaoProva localAplicacaoProva) {
		this.localAplicacaoProva = localAplicacaoProva;
	}

	/** Seta os dados da reuni�o com os fiscais (local, data e hora). 
	 * @param localReuniao
	 */
	public void setLocalReuniao(String localReuniao) {
		this.localReuniao = localReuniao;
	}

	/** Seta o processo Seletivo associado. 
	 * @param processoSeletivoVestibular
	 */
	public void setProcessoSeletivoVestibular(
			ProcessoSeletivoVestibular processoSeletivoVestibular) {
		this.processoSeletivoVestibular = processoSeletivoVestibular;
	}

	/** Valida os dados da associa��o: local de aplica��o de prova, processo seletivo.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		if (localAplicacaoProva == null)
			lista.addErro("Local de aplica��o de prova n�o pode ser nulo");
		if (processoSeletivoVestibular == null)
			lista.addErro("Processo seletivo n�o pode ser nulo");
		return null;
	}
	
	/** Retorna uma representa��o textual da associa��o entre o local de aplica��o de prova e o processo seletivo, no formato:
	 * nome do processo seletivo, seguido de v�rgula, seguido do nome do local de aplica��o.
	 */
	@Override
	public String toString() {
		return getProcessoSeletivoVestibular().toString() + ", "
				+ getLocalAplicacaoProva().toString();
	}

	/** Retorna o nome do coordenador do local de aplica��o.  
	 * @return the nomeCoordenador
	 */
	public String getNomeCoordenador() {
		return nomeCoordenador;
	}

	/** Seta o nome do coordenador do local de aplica��o.
	 * @param nomeCoordenador the nomeCoordenador to set
	 */
	public void setNomeCoordenador(String nomeCoordenador) {
		this.nomeCoordenador = nomeCoordenador;
	}

}
