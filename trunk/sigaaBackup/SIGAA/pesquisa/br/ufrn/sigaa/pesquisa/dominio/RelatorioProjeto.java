/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 22/09/2006
 *
 */
package br.ufrn.sigaa.pesquisa.dominio;


import java.util.Date;
import java.util.HashMap;

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

import br.ufrn.arq.dominio.AbstractMovimento;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * Classe que representa o Relat�rio do Projeto de Pesquisa
 * submetido pelo coordenador ao final do projeto.
 *
 * @author ilueny santos
 */

@SuppressWarnings("serial")
@Entity
@Table(name = "relatorio_projeto", schema = "pesquisa", uniqueConstraints = {})
public class RelatorioProjeto extends AbstractMovimento implements PersistDB, Validatable, ViewAtividadeBuilder {

	/** Armazena os status de avalia��o */
	public static final int PENDENTE = 1;
	public static final int APROVADO = 2;
	public static final int NECESSITA_CORRECAO = 3;

	/** Chave prim�ria */
	private int id;

	/** Armazena o resumo do Relat�rio de Projeto */
	private String resumo;

	/** Respons�vel por armazenar a data de envio do Relat�rio de Projeto */
	private Date dataEnvio;

	/** Respons�vel por armazenar o usu�rio que realizou o cadastro do Relat�rio de Projeto */
	private Usuario usuarioCadastro;

	/** Respons�vel por armazenar o projeto de pesquisa do Relat�rio de Projeto */
	private ProjetoPesquisa projetoPesquisa = new ProjetoPesquisa();

	/** Respons�vel por armazenar o consultor do Relat�rio de Projeto */
	private Consultor consultor;

	/** Respons�vel por armazenar o parecer do Consultor do Relat�rio de Projeto */
	private String parecerConsultor;

	/** Respons�vel por armazenar a data da avalia��o do Relat�rio de Projeto */
	private Date dataAvaliacao;

	/** Respons�vel por armazenar a avalia��o do Relat�rio de Projeto */
	private int avaliacao;

	/** Respons�vel por armazenar se o Relat�rio de Projeto � edital */
	private boolean editavel;
	
	/** Respons�vel por armazenar se o Relat�rio de Projeto foi enviado */
	private boolean enviado;

	/** default constructor */
	public RelatorioProjeto() {
	}

	/** minimal constructor */
	public RelatorioProjeto(int idRelatorioProjeto) {
		this.id = idRelatorioProjeto;
	}

	/** full constructor */
	public RelatorioProjeto(int idRelatorioProjeto,
			String resumo, Date dataEnvio,
			ProjetoPesquisa projetoPesquisa) {

		this.id = idRelatorioProjeto;
		this.dataEnvio = dataEnvio;
		this.resumo = resumo;
		this.projetoPesquisa =  projetoPesquisa;

	}

	/** Respons�vel por retornar a informa��o sobre o relat�rio se o mesmo pode ser ou n�o editado */
	@Transient
	public boolean isEditavel() {
		return editavel;
	}

	/** Respons�vel por seta a informa��o de editavel ou n�o para o relat�rio */
	public void setEditavel(boolean editavel) {
		this.editavel = editavel;
	}

	/**
	 * Retorna o Status no formato de String 
	 */
	@Transient
	public String getStatusString() {
		switch(this.avaliacao) {
			case PENDENTE: return "PENDENTE";
			case APROVADO: return "APROVADO";
			case NECESSITA_CORRECAO: return "NECESSITA CORRE��O";
			default: return "INDEFINIDO";
		}
	}


	/** Respons�vel por gerar a chave prim�ria e retornar o id */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_relatorio_projeto", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return id;
	}

	/** Respons�vel por seta o id */
	public void setId(int id) {
		this.id = id;
	}

	/** Respons�vel por retornar a data de envio */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_envio", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getDataEnvio() {
		return dataEnvio;
	}

	/** Respons�vel por setar a data de envio */
	public void setDataEnvio(Date dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	/** Respons�vel por retornar o Resumo */
	@Column(name = "resumo", unique = false, nullable =  true, insertable =  true, updatable =  true)
	public String getResumo() {
		return resumo;
	}

	/** Respons�vel por setar o resumo */
	public void setResumo(String resumo) {
		this.resumo = resumo != null ? resumo.trim() : resumo;
	}

	/** Respons�vel por retornar o Projeto de Pesquisa */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_projeto_pesquisa", unique = false, nullable = true, insertable = true, updatable = true)
	public ProjetoPesquisa getProjetoPesquisa() {
		return projetoPesquisa;
	}

	/** Respons�vel por setar o Projeto de Pesquisa */
	public void setProjetoPesquisa(ProjetoPesquisa projetoPesquisa) {
		this.projetoPesquisa = projetoPesquisa;
	}

	/**
	 * @return the consultor
	 */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_consultor", unique = false, nullable = true, insertable = true, updatable = true)
	public Consultor getConsultor() {
		return consultor;
	}

	/**
	 * @return the dataAvaliacao
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_avaliacao", unique = false, nullable = true, insertable = true, updatable = true)
	public Date getDataAvaliacao() {
		return dataAvaliacao;
	}

	/**
	 * @return the parecerConsultor
	 */
	@Column(name = "parecer_consultor", unique = false, nullable =  true, insertable =  true, updatable =  true)
	public String getParecerConsultor() {
		return parecerConsultor;
	}

	/**
	 * @return the avaliacao
	 */
	@Column(name = "avaliacao", unique = false, nullable =  false, insertable =  true, updatable =  true)
	public int getAvaliacao() {
		return avaliacao;
	}

	/**
	 * @param avaliacao the avaliacao to set
	 */
	public void setAvaliacao(int avaliacao) {
		this.avaliacao = avaliacao;
	}

	/**
	 * @param consultor the consultor to set
	 */
	public void setConsultor(Consultor consultor) {
		this.consultor = consultor;
	}

	/**
	 * @param dataAvaliacao the dataAvaliacao to set
	 */
	public void setDataAvaliacao(Date dataAvaliacao) {
		this.dataAvaliacao = dataAvaliacao;
	}

	/**
	 * @param parecerConsultor the parecerConsultor to set
	 */
	public void setParecerConsultor(String parecerConsultor) {
		this.parecerConsultor = parecerConsultor;
	}

	/** Respons�vel por retornar o Usu�rio que realizou o cadastro */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_cadastro")
	public Usuario getUsuarioCadastro() {
		return usuarioCadastro;
	}

	/** Respons�vel por seta o usu�rio que realizou o cadastro */
	public void setUsuarioCadastro(Usuario usuarioCadastro) {
		this.usuarioCadastro = usuarioCadastro;
	}

	@Override
	public boolean equals(Object obj) {
    	if (obj != null && obj instanceof RelatorioProjeto) {
			RelatorioProjeto objArg = (RelatorioProjeto) obj;
			if (objArg.getId() == getId())
				return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int result = HashCodeUtil.SEED;
		result = HashCodeUtil.hash(result, getId());
		return result;
	}

	/** Respons�vel por realizar as valida��es para o cadastro */
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();

		// Validar projeto de pesquisa
		if ( getProjetoPesquisa().getId() <= 0 ){
			erros.addErro("� necess�rio selecionar um dos projetos de pesquisa listados");
		}

		// Validar resumo expandido
		if ( !StringUtils.notEmpty(getResumo()) )
			ValidatorUtil.validateRequired(null, "Resumo Expandido", erros);
		else if(getResumo().trim().replace("\r\n", "\n").length() > 10000){
			erros.addMensagem(MensagensArquitetura.MAXIMO_CARACTERES, "Resumo", 10000);
		}
		return erros;
	}

	/**
	 * Retorna o item para a view, formatado, como c�digo, t�tulo e a data de envio j� formatada 
	 */
	@Transient
	public String getItemView() {
		return "  <td>"+getProjetoPesquisa().getCodigo()+ "</td>" +
				"  <td>"+getProjetoPesquisa().getTitulo()+ "</td>" +
			   "  <td style=\"text-align:center\">"+Formatador.getInstance().formatarData(dataEnvio)+"</td>";
	}

	/**
	 * Retorna o cabecalho para o m�todo getItemView
	 */
	@Transient
	public String getTituloView() {
		return  "    <td>C�digo</td>" +
				"    <td>Projeto</td>" +
				"    <td style=\"text-align:center\">Data Envio</td>";
	}

	/**
	 * Adiciona ao hashMap o c�digo do projeto de pesquisa, o titulo e seta a data como
	 * sendo nula.
	 */
	@Transient
	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("projetoPesquisa.codigo", "codigoProjeto");
		itens.put("projetoPesquisa.projeto.titulo", "tituloProjeto");
		itens.put("dataEnvio", null);
		return itens;
	}

	@Transient
	public float getQtdBase() {
		return 1;
	}

	/**
	 * Seta o titulo ao projeto desde que o mesmo n�o seja nulo
	 * 
	 * @param titulo
	 */
	@Transient
	public void setTituloProjeto(String titulo) {
		if (projetoPesquisa == null) {
			projetoPesquisa = new ProjetoPesquisa();
		}
		projetoPesquisa.setTitulo(titulo);
	}

	/**
	 * Seta o c�digo do projeto de pesquisa desde que o mesmo n�o seja nulo.
	 * 
	 * @param codigo
	 */
	@Transient
	public void setCodigoProjeto(CodigoProjetoPesquisa codigo) {
		if (projetoPesquisa == null) {
			projetoPesquisa = new ProjetoPesquisa();
		}
		projetoPesquisa.setCodigo(codigo);
	}

	/** Respons�vel por retornar um boleano com a informa��o se o relat�rio foi enviado ou n�o */
	@Column(name = "enviado")
	public boolean isEnviado() {
		return enviado;
	}

	/** Respons�vel por seta a informa��o se o projeto foi enviado ou n�o */
	public void setEnviado(boolean enviado) {
		this.enviado = enviado;
	}

}