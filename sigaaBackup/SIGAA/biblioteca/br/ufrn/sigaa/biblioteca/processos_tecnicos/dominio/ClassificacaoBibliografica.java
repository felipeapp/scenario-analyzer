/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p>Classe que guarda as configurações das classificações bibliográficas utilizadas no sistema  </p>
 *
 * <p> <i> Será possível cadastrar até 3 classificações bibligráficas para serem utilizadas simutaneamente pelo sistema. 
 *         O limite de 3 é porque os dados dessa classificação precisam ser guardados em cache dentro de variáveis fixa 
 *         na classe {@link TituloCatalografico}</i> </p>
 * 
 * 
 * <p> <i>Teoricamente esses informações devem ser cadastradas no início da utilização do sistema e não 
 * deveriam serem alteradas posteriormente para não gerar incosistência da base de dados, uma vez que teriam 
 * que serem alterados todos os Títulos que possuem a classificação alterada.</i> </p>
 * 
 * @author jadson
 *
 */
@Entity
@Table(name = "classificacao_bibliografica", schema = "biblioteca")
public class ClassificacaoBibliografica implements Validatable {

	/** Indica qual a ordem da classificação configurada no sistema, se ela será a primeira a segunda ou a terceira classificação.*/
	public enum OrdemClassificacao{
	
		/** Representa a primeira classificação das 3 que o sistema suporta */
		PRIMERA_CLASSIFICACAO(0, "1ª Classificação"), 
		/** Representa a segunda classificação das 3 que o sistema suporta */
		SEGUNDA_CLASSIFICACAO(1, "2ª Classificação"), 
		/** Representa a terceira classificação das 3 que o sistema suporta */
		TERCEIRA_CLASSIFICACAO(2, "3ª Classificação");
	
		/** O valor salvo no banco */
		private int valor;
		
		/** A crescrição que identifica a ordem para o usuário */
		private String descricao;
		
		/** Construtor padrão*/
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
	
		/**  Método chamado pelo hibernate para pegar o valor do enum nas consultas HQLs  */
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
	
	/** A descrição da classificação utilizada que é mostrada para o usuário.*/
	private String descricao;
	
	/** O valor do campo marc a partir do qual a classificação vai ser informada. */
	@Enumerated(EnumType.ORDINAL) // O valor salvo no banco vai ser a ordem em que as variáveis são declaradas
	@Column(name="campo", nullable=false)
	private CamposMarcClassificacaoBibliografica campoMARC;
	
	/** Indica se essa classificação é a primeira a segunda ou a terceira classificação utilizada no sistema. 
	 * Informação necessária para saber onde essa informação vai ser guardada no banco e de onde será consultada nos relatórios */
	@Enumerated(EnumType.ORDINAL) // O valor salvo no banco vai ser a ordem em que as variáveis são declaradas
	@Column(name="ordem", nullable=false)
	private OrdemClassificacao ordem;
	
	/** Representa o valor de uma classe principal para a respectiva classificação bibliográfica. */
	@CollectionOfElements 
	@JoinTable(name = "classe_principal_classificacao_bibliografica", schema = "biblioteca", joinColumns = @JoinColumn(name = "id_classificacao_bibliografica"))
	@Column(name = "classe_principal")
	private List<String> classesPrincipaisClassificacaoBibliografica = new ArrayList<String>();

	/** Se a classificação foi desativa no sistema, não sendo mais utilizada */
	@Column(name = "ativa")
	private boolean ativa;

	
	////////////////////////////INFORMAÇÕES DE AUDITORIA  ///////////////////////////////////////


	/**
	 * informações de quem criou
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
	 * registro entrada  do usuário que realizou a última atualização
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_ultima_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroUltimaAtualizacao;

	/**
	 * data da última atualização
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
	 * Adiciona uma nova classe principal à classificação.
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
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		ListaMensagens mensagens = new ListaMensagens();
		
		if (StringUtils.isEmpty(descricao)) {
			mensagens.addErro("A descrição da classificação deve ser definida.");
		}
		
		if (ordem == null) {
			mensagens.addErro("A ordem da classificação deve ser definida.");
		}
		
		if (campoMARC == null) {
			mensagens.addErro("O campo MARC da classificação deve ser definida.");
		}
		
		if (classesPrincipaisClassificacaoBibliografica.size() == 0) {
			mensagens.addErro("A classificação deve conter ao menos uma classe principal.");
		}
		
		for (String classePrincipal : classesPrincipaisClassificacaoBibliografica) {
			if (classePrincipal.length() > 5) {
				mensagens.addErro("A classe principal '" + classePrincipal + "' é inválida, pois seu tamanho ultrapassa o limite de 5 caracteres.");
			}
		}
		
		return mensagens;
	}
	
	/** Verifica se essa classificação é a primeira */
	public boolean isPrimeiraClassificacao(){
		return this.ordem == OrdemClassificacao.PRIMERA_CLASSIFICACAO;
	}
	
	/** Verifica se essa classificação é a segunda */
	public boolean isSegundaClassificacao(){
		return this.ordem == OrdemClassificacao.SEGUNDA_CLASSIFICACAO;
	}
	
	/** Verifica se essa classificação é a terceira */
	public boolean isTerceiraClassificacao(){
		return this.ordem == OrdemClassificacao.TERCEIRA_CLASSIFICACAO;
	}
	
	/** Retorna descrição de todas as classes principais da classificação */
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
	 * Ver comentários da classe pai.<br/>
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
