/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 26/03/2009
 *
 */	
package br.ufrn.sigaa.assistencia.cadunico.dominio;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.questionario.dominio.Questionario;

/**
 * Representa a adesão ao programa de cadastro único de bolsa
 * 
 * @author Henrique André
 * 
 */
@Entity
@Table(name = "adesao_cadastro_unico", schema = "sae")
public class AdesaoCadastroUnicoBolsa implements PersistDB, Comparable<AdesaoCadastroUnicoBolsa> {

	public static int ALUNO_PRIORITARIO = ParametroHelper.getInstance().getParametroInt(ConstantesParametroGeral.ALUNO_PRIORITARIO);
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_adesao")
	private int id;

	@ManyToOne
	@JoinColumn(name = "id_cadastro_unico")
	private FormularioCadastroUnicoBolsa cadastroUnico;

	@ManyToOne
	@JoinColumn(name = "id_discente")
	private Discente discente;

	private boolean ativo = true;

	@Column(name = "pontuacao")
	private int pontuacao;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_contato_familia")
	private ContatoFamilia contatoFamilia;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "adesao")
	private List<QuantidadeItemConfortoCadastroUnico> listaConfortoFamiliar;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_alteracao")
	@AtualizadoEm
	private Date dataAlteracao;
	
	private int ano;
	
	private int periodo;
	
	public Questionario getQuestionario() {
		return cadastroUnico.getQuestionario();
	}

	/**
	 * Informa se o discente que realizou a adesão ao cadastro único é Prioritário
	 * 
	 * @return
	 */
	public boolean isPrioritario() {
		if (pontuacao < ALUNO_PRIORITARIO)
			return true;
		return false;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public FormularioCadastroUnicoBolsa getCadastroUnico() {
		return cadastroUnico;
	}

	public void setCadastroUnico(FormularioCadastroUnicoBolsa cadastroUnico) {
		this.cadastroUnico = cadastroUnico;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public int getPontuacao() {
		return pontuacao;
	}

	public void setPontuacao(int pontuacao) {
		this.pontuacao = pontuacao;
	}

	public ContatoFamilia getContatoFamilia() {
		return contatoFamilia;
	}

	public void setContatoFamilia(ContatoFamilia contatoFamilia) {
		this.contatoFamilia = contatoFamilia;
	}

	public List<QuantidadeItemConfortoCadastroUnico> getListaConfortoFamiliar() {
		return listaConfortoFamiliar;
	}

	public void setListaConfortoFamiliar(
			List<QuantidadeItemConfortoCadastroUnico> listaConfortoFamiliar) {
		this.listaConfortoFamiliar = listaConfortoFamiliar;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}
	
	/**
	 * Retorna SIM ou NÃO
	 * 
	 * @return
	 */
	public String getPrioritarioFormatado() {
		if (isPrioritario())
			return "SIM";
		else 
			return "NÃO";
	}

	public int compareTo(AdesaoCadastroUnicoBolsa o) {
		// Primeiro critério: se é prioritário.
    	int result = 0;
    	if ((this.isPrioritario() && o.isPrioritario()) || (!this.isPrioritario() && !o.isPrioritario())) {
    		result = 0;
    	} else if(this.isPrioritario() && !o.isPrioritario()) {
    		result = -1;
    	} else if(!this.isPrioritario() && o.isPrioritario()) {
    		result = 1;
    	}
		if (result == 0) {
			// Segundo critério: Ordem alfabética
			result = this.getDiscente().getPessoa().compareTo(o.getDiscente().getPessoa());
		}
		return result;
	}
	
}