/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '03/05/2007'
 *
 */
package br.ufrn.sigaa.prodocente.relatorios.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.Formatador;

/**
 * Entidade que agrega relatórios de produtividade que foram gerados e seus resultados armazenados
 * para que sua classificação seja utilizada com uma finalidade específica.
 * 
 * @author wendell
 *
 */
@Entity
@Table(name = "classificacao_relatorio", schema = "prodocente")
public class ClassificacaoRelatorio implements Validatable {

	public static final int EM_PROCESSAMENTO = 1;
	public static final int PROCESSADO_COM_SUCESSO = 2;
	public static final int PROCESSADO_COM_FALHA = 3;
	
	/**
	 * identificador da entidade
	 */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_classificacao_relatorio", nullable = false)
	private int id;

	/**
	 * data que esta classificação foi criada
	 */
	@Column(name = "data_classificacao")
    @Temporal(TemporalType.TIMESTAMP)
	private Date dataClassificacao;

	/**
	 * String que descreve a finalidade desta classificação, informada pelo usuário.
	 */
	@Column(name = "finalidade")
	private String finalidade;

	/**
	 * Relatório de produtividade ao qual esta classificação está relacionada
	 */
	@JoinColumn(name="id_relatorio_produtividade")
	@ManyToOne
	private RelatorioProdutividade relatorioProdutividade;

	/**
	 * Registro de entrada do usuário criado desta classificação
	 */
	@JoinColumn(name="id_entrada")
	@ManyToOne
	private RegistroEntrada registroEntrada;

	/**
	 * Ano de vigencia desta classificação
	 */
	@Column(name = "ano_vigencia")
    private Integer anoVigencia;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "classificacaoRelatorio")
	private Collection<EmissaoRelatorio> emissaoRelatorioCollection = new ArrayList<EmissaoRelatorio>();

	/**
	 * Este atributo guarda o status do processamento deste relatório.
	 * Caso haja erro no processamento este relatório deve ser desconsiderado.
	 */
	@Column(name="status_processamento")
	private int statusProcessamento = EM_PROCESSAMENTO;
	
	/**
	 * Este atributo indica se esta classificação está ativa ou não. 
	 * Classificações inativas não aparecem para os usuários.
	 */
	private boolean ativo = true;
	
	public String getDescricao() {
		return "Gerado em " + Formatador.getInstance().formatarDataHora(this.dataClassificacao) + " - " + finalidade;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ListaMensagens validate() {
		return null;
	}

	/**
	 * @return the dataClassificacao
	 */
	public Date getDataClassificacao() {
		return dataClassificacao;
	}

	/**
	 * @param dataClassificacao
	 *            the dataClassificacao to set
	 */
	public void setDataClassificacao(Date dataClassificacao) {
		this.dataClassificacao = dataClassificacao;
	}

	/**
	 * @return the emissaoRelatorioCollection
	 */
	public Collection<EmissaoRelatorio> getEmissaoRelatorioCollection() {
		return emissaoRelatorioCollection;
	}

	/**
	 * @param emissaoRelatorioCollection
	 *            the emissaoRelatorioCollection to set
	 */
	public void setEmissaoRelatorioCollection(
			Collection<EmissaoRelatorio> emissaoRelatorioCollection) {
		this.emissaoRelatorioCollection = emissaoRelatorioCollection;
	}

	/**
	 * @return the finalidade
	 */
	public String getFinalidade() {
		return finalidade;
	}

	/**
	 * @param finalidade
	 *            the finalidade to set
	 */
	public void setFinalidade(String finalidade) {
		this.finalidade = finalidade;
	}


	 /**
     * Determines whether another object is equal to this EmissaoRelatorio.  The result is
     * <code>true</code> if and only if the argument is not null and is a EmissaoRelatorio object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ClassificacaoRelatorio)) {
            return false;
        }
        ClassificacaoRelatorio other = (ClassificacaoRelatorio)object;
        if (this.id != other.id || this.id == 0) return false;
        return true;
    }

	 /**
     * Returns a string representation of the object.  This implementation constructs
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "br.ufrn.sigaa.prodocente.relatorios.dominio.ClassificacaoRelatorio[id=" + id + "]";
    }

	/**
	 * @return the relatorioProdutividade
	 */
	public RelatorioProdutividade getRelatorioProdutividade() {
		return relatorioProdutividade;
	}

	/**
	 * @param relatorioProdutividade the relatorioProdutividade to set
	 */
	public void setRelatorioProdutividade(
			RelatorioProdutividade relatorioProdutividade) {
		this.relatorioProdutividade = relatorioProdutividade;
	}

	/**
	 * @return the anoVigencia
	 */
	public Integer getAnoVigencia() {
		return anoVigencia;
	}

	/**
	 * @param anoVigencia the anoVigencia to set
	 */
	public void setAnoVigencia(Integer anoVigencia) {
		this.anoVigencia = anoVigencia;
	}

	/**
	 * @return the registroEntrada
	 */
	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	/**
	 * @param registroEntrada the registroEntrada to set
	 */
	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public int getStatusProcessamento() {
		return statusProcessamento;
	}

	public void setStatusProcessamento(int statusProcessamento) {
		this.statusProcessamento = statusProcessamento;
	}

	
	
	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public String getStatusProcessamentoString(){
		String status = "Indefinido";
		
		switch (statusProcessamento) {
		case EM_PROCESSAMENTO:
			status = "Em processamento";
			break;

		case PROCESSADO_COM_FALHA:
			status = "Processado com erro";
			break;
			
		case PROCESSADO_COM_SUCESSO:
			status = "Processado com sucesso";
			break;
		}
		
		return status;
	}
	
	public boolean isProcessadaComSucesso(){
		return statusProcessamento == PROCESSADO_COM_SUCESSO;
	}
}