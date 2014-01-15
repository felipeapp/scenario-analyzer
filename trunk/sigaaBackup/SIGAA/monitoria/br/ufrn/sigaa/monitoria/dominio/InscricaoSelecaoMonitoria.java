/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 07/03/2007
 *
 */
package br.ufrn.sigaa.monitoria.dominio;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.comum.dominio.notificacoes.Notificacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.DadosAluno;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;

/*******************************************************************************
 * <p>
 * Esta classe representa uma inscrição de um discente para a seleção de um
 * projeto de monitoria.
 * </p>
 * 
 * @author Victor Hugo
 * 
 ******************************************************************************/

@Entity
@Table(name = "inscricao_selecao_monitoria", schema = "monitoria")
public class InscricaoSelecaoMonitoria implements PersistDB {

	/** Chave primrária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_inscricao_selecao_monitoria")
	private int id;

	/** Discente de graduação que realizou a inscrição */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_discente")
	private DiscenteGraduacao discente;

	/** Projeto de ensino da inscrição da seleção */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_projeto_monitoria")
	private ProjetoEnsino projetoEnsino;

	/** Prova realizada para a seleção */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_prova_selecao")
	private ProvaSelecao provaSelecao;
	
	/** Registro de entrada do inscrito */
	@CriadoPor
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntrada;

	/** Data de realização da inscrição */
	@CriadoEm
	@Column(name = "data_cadastro")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCadastro;

	/** Utilizado para informar se a inscrição é valida */
	@Column(name = "ativo")
	private boolean ativo =  true;
	
	/** Informações básicas da inscrição  */
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_dados_aluno")
	private DadosAluno dados;

	/** Informação sobre o discente se é prioritário */
	@Transient
	private Boolean prioritario;
	
	/** Armazena a notificação a ser enviada ao discente */
	@Transient
	private Notificacao notificacao;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public DiscenteGraduacao getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteGraduacao discente) {
		this.discente = discente;
	}

	public ProjetoEnsino getProjetoEnsino() {
		return projetoEnsino;
	}

	public void setProjetoEnsino(ProjetoEnsino projetoEnsino) {
		this.projetoEnsino = projetoEnsino;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public ProvaSelecao getProvaSelecao() {
		return provaSelecao;
	}

	public void setProvaSelecao(ProvaSelecao provaSelecao) {
		this.provaSelecao = provaSelecao;
	}

	@Override
	public boolean equals(Object obj) {

		boolean result = false;

		if (obj == null) {
			return false;
		}
		if (obj instanceof InscricaoSelecaoMonitoria) {
			InscricaoSelecaoMonitoria o = (InscricaoSelecaoMonitoria) obj;
			result = this.getDiscente().equals(o.getDiscente());
		}
		return result;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public DadosAluno getDados() {
		return dados;
	}

	public void setDados(DadosAluno dados) {
		this.dados = dados;
	}

	/**
	 * Informa se o discente possui algum tipo de prioridade na seleção.
	 * Esta informação é gerada a partir da adesão do discente ao cadastro único.
	 * 
	 * @return <code>true</code> se o discente for prioritário.
	 */
	public Boolean getPrioritario() {
	    return prioritario;
	}

	public void setPrioritario(Boolean prioritario) {
	    this.prioritario = prioritario;
	}

	public Notificacao getNotificacao() {
		return notificacao;
	}

	public void setNotificacao(Notificacao notificacao) {
		this.notificacao = notificacao;
	}
	
}