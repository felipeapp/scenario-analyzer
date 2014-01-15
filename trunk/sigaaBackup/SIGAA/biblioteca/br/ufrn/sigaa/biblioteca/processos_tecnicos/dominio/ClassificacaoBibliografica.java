/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 13/02/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.StringUtils;

/**
 *
 * <p>Classe que guarda as configura��es das classifica��es bibliogr�ficas utilizadas no sistema  </p>
 *
 * <p> <i> Ser� poss�vel cadastrar at� 3 classifica��es bibligr�ficas para serem utilizadas simutaneamente pelo sistema. 
 *         O limite de 3 � porque os dados dessa classifica��o precisam ser guardados em cache dentro de vari�veis fixa 
 *         na classe {@link TituloCatalografico}</i> </p>
 * 
 * 
 * <p> <i>Teoricamente esses informa��es devem ser cadastradas no in�cio da utiliza��o do sistema e n�o 
 * deveriam serem alteradas posteriormente para n�o gerar incosist�ncia da base de dados, uma vez que teriam 
 * que serem alterados todos os T�tulos que possuem a classifica��o alterada.</i> </p>
 * 
 * @author jadson
 *
 */
@Entity
@Table(name = "classificacao_bibliografica", schema = "biblioteca")
public class ClassificacaoBibliografica implements Validatable {

	/** Indica qual a ordem da classifica��o configurada no sistema, se ela ser� a primeira a segunda ou a terceira classifica��o.*/
	public enum OrdemClassificacao{
	
		/** Representa a primeira classifica��o das 3 que o sistema suporta */
		PRIMERA_CLASSIFICACAO(0, "1� Classifica��o"), 
		/** Representa a segunda classifica��o das 3 que o sistema suporta */
		SEGUNDA_CLASSIFICACAO(1, "2� Classifica��o"), 
		/** Representa a terceira classifica��o das 3 que o sistema suporta */
		TERCEIRA_CLASSIFICACAO(2, "3� Classifica��o");
	
		/** O valor salvo no banco */
		private int valor;
		
		/** A crescri��o que identifica a ordem para o usu�rio */
		private String descricao;
		
		/** Construtor padr�o*/
		private OrdemClassificacao(int valor, String descricao){
			this.valor = valor;
			this.descricao = descricao;
		}

		public int getValor() { return valor; }
		public String getDescricao() { return descricao; }

		/** Recupera a ordem a partir do seu valor */
		public static OrdemClassificacao getOrdem(Integer valorOrdem) {
			if (valorOrdem.equals(PRIMERA_CLASSIFICACAO.valor)) {
				return PRIMERA_CLASSIFICACAO;
			} else if (valorOrdem.equals(SEGUNDA_CLASSIFICACAO.valor)) {
				return SEGUNDA_CLASSIFICACAO;
			} else if (valorOrdem.equals(TERCEIRA_CLASSIFICACAO.valor)) {
				return TERCEIRA_CLASSIFICACAO;
			} else {
				return null;
			}
		}
	
		/**  M�todo chamado pelo hibernate para pegar o valor do enum nas consultas HQLs  */
		@Override
		public String toString() {
			return String.valueOf(valor);
		}
	}
		
	/** O id */
	@Id
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })
	@Column (name="id_classificacao_bibliografica")
	private int id;
	
	/** A descri��o da classifica��o utilizada que � mostrada para o usu�rio.*/
	private String descricao;
	
	/** O valor do campo marc a partir do qual a classifica��o vai ser informada. */
	@Enumerated(EnumType.ORDINAL) // O valor salvo no banco vai ser a ordem em que as vari�veis s�o declaradas
	@Column(name="campo", nullable=false)
	private CamposMarcClassificacaoBibliografica campoMARC;
	
	/** Indica se essa classifica��o � a primeira a segunda ou a terceira classifica��o utilizada no sistema. 
	 * Informa��o necess�ria para saber onde essa informa��o vai ser guardada no banco e de onde ser� consultada nos relat�rios */
	@Enumerated(EnumType.ORDINAL) // O valor salvo no banco vai ser a ordem em que as vari�veis s�o declaradas
	@Column(name="ordem", nullable=false)
	private OrdemClassificacao ordem;
	
	/** Representa o valor de uma classe principal para a respectiva classifica��o bibliogr�fica. */
	@CollectionOfElements 
	@JoinTable(name = "classe_principal_classificacao_bibliografica", schema = "biblioteca", joinColumns = @JoinColumn(name = "id_classificacao_bibliografica"))
	@Column(name = "classe_principal")
	private List<String> classesPrincipaisClassificacaoBibliografica = new ArrayList<String>();

	/** Se a classifica��o foi desativa no sistema, n�o sendo mais utilizada */
	@Column(name = "ativa")
	private boolean ativa;

	
	////////////////////////////INFORMA��ES DE AUDITORIA  ///////////////////////////////////////


	/**
	 * informa��es de quem criou
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_criacao")
	@CriadoPor
	private RegistroEntrada registroCriacao;

	/**
	 * data de cadastro
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@CriadoEm
	@Column(name="data_criacao")
	private Date dataCriacao;

	/**
	 * registro entrada  do usu�rio que realizou a �ltima atualiza��o
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_ultima_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroUltimaAtualizacao;

	/**
	 * data da �ltima atualiza��o
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_ultima_atualizacao")
	@AtualizadoEm
	private Date dataUltimaAtualizacao;


	//////////////////////////////////////////////////////////////////////////////////////////
	
	
	public ClassificacaoBibliografica() {
		
	}

	public ClassificacaoBibliografica(int id) {
		this.id = id;
	}

	public ClassificacaoBibliografica(int id, String descricao) {
		this(id);
		this.descricao = descricao;
	}

	/**
	 * Adiciona uma nova classe principal � classifica��o.
	 *
	 * @param classePrincipal
	 */
	public void adicionaClassesPrincipais(String classePrincipal) {
		
		if ( this.classesPrincipaisClassificacaoBibliografica == null)
			this.classesPrincipaisClassificacaoBibliografica = new ArrayList<String>();
		this.classesPrincipaisClassificacaoBibliografica.add(classePrincipal);
	}

	
	/**
	 * 
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		ListaMensagens mensagens = new ListaMensagens();
		
		if (StringUtils.isEmpty(descricao)) {
			mensagens.addErro("A descri��o da classifica��o deve ser definida.");
		}
		
		if (ordem == null) {
			mensagens.addErro("A ordem da classifica��o deve ser definida.");
		}
		
		if (campoMARC == null) {
			mensagens.addErro("O campo MARC da classifica��o deve ser definida.");
		}
		
		if (classesPrincipaisClassificacaoBibliografica.size() == 0) {
			mensagens.addErro("A classifica��o deve conter ao menos uma classe principal.");
		}
		
		for (String classePrincipal : classesPrincipaisClassificacaoBibliografica) {
			if (classePrincipal.length() > 5) {
				mensagens.addErro("A classe principal '" + classePrincipal + "' � inv�lida, pois seu tamanho ultrapassa o limite de 5 caracteres.");
			}
		}
		
		return mensagens;
	}
	
	/** Verifica se essa classifica��o � a primeira */
	public boolean isPrimeiraClassificacao(){
		return this.ordem == OrdemClassificacao.PRIMERA_CLASSIFICACAO;
	}
	
	/** Verifica se essa classifica��o � a segunda */
	public boolean isSegundaClassificacao(){
		return this.ordem == OrdemClassificacao.SEGUNDA_CLASSIFICACAO;
	}
	
	/** Verifica se essa classifica��o � a terceira */
	public boolean isTerceiraClassificacao(){
		return this.ordem == OrdemClassificacao.TERCEIRA_CLASSIFICACAO;
	}
	
	/** Retorna descri��o de todas as classes principais da classifica��o */
	public String getDescricaoClassesPrincipais() {
		StringBuilder descricaoClassesPrincipais = new StringBuilder();
		
		for (String s : classesPrincipaisClassificacaoBibliografica) {
			if (classesPrincipaisClassificacaoBibliografica.indexOf(s) == 0) {
				descricaoClassesPrincipais.append(s);
			} else {
				descricaoClassesPrincipais.append(", " + s);
			}
		}
		
		return descricaoClassesPrincipais.toString();
	}
	
	/**
	 * Ver coment�rios da classe pai.<br/>
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ClassificacaoBibliografica [descricao=" + descricao
				+ ", campoMARC=" + (campoMARC!= null ? campoMARC.getDescricao() :" ") 
				+ ", ordem=" + (ordem != null ? ordem.getDescricao() : " ")
				+ ", classesPrincipaisClassificacaoBibliografica="
				+ (classesPrincipaisClassificacaoBibliografica != null ? classesPrincipaisClassificacaoBibliografica : " ") + "]";
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
		ClassificacaoBibliografica other = (ClassificacaoBibliografica) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
	////// sets e gets //////
	
	public int getId() {return id;}
	public void setId(int id) {this.id = id;}
	public String getDescricao() {return descricao;}
	public void setDescricao(String descricao) {this.descricao = descricao;}
	public CamposMarcClassificacaoBibliografica getCampoMARC() {return campoMARC;}
	public void setCampoMARC(CamposMarcClassificacaoBibliografica campoMARC) {this.campoMARC = campoMARC;}
	public OrdemClassificacao getOrdem() {return ordem;}
	public void setOrdem(OrdemClassificacao ordem) {this.ordem = ordem;}
	public List<String> getClassesPrincipaisClassificacaoBibliografica() {return classesPrincipaisClassificacaoBibliografica;}
	public void setClassesPrincipaisClassificacaoBibliografica(List<String> classesPrincipaisClassificacaoBibliografica) {this.classesPrincipaisClassificacaoBibliografica = classesPrincipaisClassificacaoBibliografica;}
	public boolean isAtiva() {return ativa;}
	public void setAtiva(boolean ativa) {this.ativa = ativa;}
	public RegistroEntrada getRegistroCriacao() {return registroCriacao;}
	public void setRegistroCriacao(RegistroEntrada registroCriacao) {this.registroCriacao = registroCriacao;}
	public Date getDataCriacao() {return dataCriacao;}
	public void setDataCriacao(Date dataCriacao) {this.dataCriacao = dataCriacao;}
	public RegistroEntrada getRegistroUltimaAtualizacao() {return registroUltimaAtualizacao;}
	public void setRegistroUltimaAtualizacao(RegistroEntrada registroUltimaAtualizacao) {this.registroUltimaAtualizacao = registroUltimaAtualizacao;}
	public Date getDataUltimaAtualizacao() {return dataUltimaAtualizacao;}
	public void setDataUltimaAtualizacao(Date dataUltimaAtualizacao) {this.dataUltimaAtualizacao = dataUltimaAtualizacao;}	
	
}
