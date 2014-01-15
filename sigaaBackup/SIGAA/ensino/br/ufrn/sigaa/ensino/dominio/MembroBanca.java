/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/02/2011
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.rh.dominio.Formacao;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Esta entidade relaciona um docente, seja ele interno ou externo � Institui��o, 
 * a uma banca de defesa de TCC.
 * 
 * @author arlindo
 *
 */
@Entity
@Table(name = "membro_banca", schema = "ensino")
public class MembroBanca implements PersistDB {
	
	/** Chave prim�ria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="ensino.membro_banca_seq") })
	@Column(name = "id_membro_banca")	
	private int id;		
	
	/** Banca ao qual o docente est� participando. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_banca")
	private BancaDefesa banca;
	
	/** Tipo do membro da banca. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_membro_banca")
	private TipoMembroBanca tipo;	
	
	/** Servidor docente participante da banca. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_servidor")
	private Servidor servidor;

	/** Docente externo � UFRN participante da banca. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_docente_externo")
	private DocenteExterno docenteExterno;
	
	/** Pessoa que cont�m os dados pessoais do docente da 
	 * banca caso seja um membro externo a Institui��o que N�O SEJA NEM DOCENTE EXTERNO. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pessoa_membro_externo")
	private Pessoa pessoaMembroExterno;	

	/** Maior forma��o do docente caso seja um membro externo a Institui��o que N�O SEJA NEM DOCENTE EXTERNO. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_maior_formacao")
	private Formacao maiorFormacao;

	/** Institui��o de origem do docente caso seja um membro externo a Institui��o que N�O SEJA NEM DOCENTE EXTERNO. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_instituicao")
	private InstituicoesEnsino instituicao;

	/** Ano de conclus�o da �LTIMA titula��o desta pessoa. 
	 * Utilizado no cadastro de banca quando � um docente externo a Institui��o e que NAO � DOCENTE EXTERNO.
	 * @return
	 */
	@Column(name="ano_conclusao")
	private Integer anoConclusao;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public BancaDefesa getBanca() {
		return banca;
	}

	public void setBanca(BancaDefesa banca) {
		this.banca = banca;
	}

	public TipoMembroBanca getTipo() {
		return tipo;
	}

	public void setTipo(TipoMembroBanca tipo) {
		this.tipo = tipo;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public DocenteExterno getDocenteExterno() {
		return docenteExterno;
	}

	public void setDocenteExterno(DocenteExterno docenteExterno) {
		this.docenteExterno = docenteExterno;
	}

	public Pessoa getPessoaMembroExterno() {
		return pessoaMembroExterno;
	}

	public void setPessoaMembroExterno(Pessoa pessoaMembroExterno) {
		this.pessoaMembroExterno = pessoaMembroExterno;
	}

	public Formacao getMaiorFormacao() {
		return maiorFormacao;
	}

	public void setMaiorFormacao(Formacao maiorFormacao) {
		this.maiorFormacao = maiorFormacao;
	}

	public InstituicoesEnsino getInstituicao() {
		return instituicao;
	}

	public void setInstituicao(InstituicoesEnsino instituicao) {
		this.instituicao = instituicao;
	}

	public Integer getAnoConclusao() {
		return anoConclusao;
	}

	public void setAnoConclusao(Integer anoConclusao) {
		this.anoConclusao = anoConclusao;
	}
	
	/** Retorna os dados pessoais do membro da banca.
	 * @return
	 */
	public Pessoa getPessoa() {
		if (servidor != null)
			return servidor.getPessoa();
		else if (docenteExterno != null)
			return docenteExterno.getPessoa();
		else
			return pessoaMembroExterno;
	}	
	
	/** Compara este objeto ao passado por par�metro, verificando se o objeto pessoa � igual. 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "pessoa");
	}

	/** Retorna o c�digo hash deste objeto.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(getPessoa());
	}	
	
	/** Retorna uma descri��o textual da titula��o do membro da banca (Doutor, Mestre, ou Especialista).
	 * @return
	 */
	public String getDescricaoFormacao() {
		if (maiorFormacao != null){
			if (maiorFormacao.getId() == Formacao.DOUTOR)
				return "Doutor";
			else if (maiorFormacao.getId() == Formacao.MESTRE)
				return "Mestre";
			else if (maiorFormacao.getId() == Formacao.ESPECIALISTA)
				return "Especialista";
		} 
		return "";
	}
	
	/** 
	 * Retorna uma identifica��o do membro da banca, contendo o nome do examinador, a sigla da institui��o (caso externo),
	 * a matr�cula SIAPE (caso membro da institui��o), e o CPF (caso membro externo � institui��o).
	 * @return
	 */
	public String getMembroIdentificacao() {
		if (isExternoInstituicao()) {
			return pessoaMembroExterno.getNome() + " - " + instituicao.getSigla();
		} else {
			if (!isEmpty(servidor))
				return servidor.getSiapeNome();
			if (!isEmpty(docenteExterno))
				return docenteExterno.getNome();
			else
				return "";
		}
	}
	
	/** 
	 * Retorna uma descri��o textual do tipo do membro da banca.
	 * @return
	 */
	public String getDescricao() {		
		StringBuilder sb = new StringBuilder();
		if (!isEmpty(tipo.getDescricao()))
			sb.append(tipo.getDescricao());
		if (!isEmpty(getMembroIdentificacao()))
			sb.append(" - " + getMembroIdentificacao());
		if (instituicao != null && !isEmpty(instituicao.getSigla()))
			sb.append(" - " + instituicao.getSigla());
		return sb.toString();
	}	
	
	/** Retorna o nome do membro da banca.
	 * @return
	 */
	public String getNome() {
		if (servidor != null && servidor.getPessoa() != null)
			return servidor.getPessoa().getNome();
		else if (docenteExterno != null && docenteExterno.getPessoa() != null)
			return docenteExterno.getPessoa().getNome();
		else if (pessoaMembroExterno != null)
			return pessoaMembroExterno.getNome();		
		return "";
	}	
	
	/** Valida os dados:<br>
	 * <ul>
	 * <li>caso examinador externo � institui��o: institui��o, maior forma��o, nome e e-mail do examinador.</li>
	 * <li>demais casos: docente do programa.
	 * </ul>
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		if (isExternoInstituicao()) {
			ValidatorUtil.validateRequired(instituicao, "Institui��o de Ensino", lista);
			ValidatorUtil.validateRequired(maiorFormacao, "Maior Forma��o", lista);
			ValidatorUtil.validateRequired(pessoaMembroExterno.getNome(), "Nome", lista);
			if (("").equals(pessoaMembroExterno.getEmail())) {
				ValidatorUtil.validateRequired(null, "Email", lista);
			}else{
				ValidatorUtil.validateEmail(pessoaMembroExterno.getEmail(), "Email", lista);
			}
			
			if( getAnoConclusao() != null)
				ValidatorUtil.validaFloatPositivo( Float.valueOf( getAnoConclusao() ), "Ano de Conclus�o", lista);

			if (!pessoaMembroExterno.isInternacional()){
				if (ValidatorUtil.isEmpty(pessoaMembroExterno.getCpf_cnpj()))
					ValidatorUtil.validateRequired(pessoaMembroExterno.getCpf_cnpj(), "CPF", lista);
				else
					ValidatorUtil.validateCPF_CNPJ(pessoaMembroExterno.getCpf_cnpj(), "CPF", lista);
			}
		} else {
			if (isEmpty(servidor) && isEmpty(docenteExterno))
				lista.addErro("Docente: Campo obrigat�rio n�o informado");
		}
		return lista;
	}	
	
	@Transient
	public boolean isInterno(){
		return (tipo != null && tipo.getId() == TipoMembroBanca.EXAMINADOR_INTERNO);
	}
	
	@Transient
	public boolean isExterno(){
		return (tipo != null && tipo.getId() == TipoMembroBanca.EXAMINADOR_EXTERNO);
	}	
	
	@Transient
	public boolean isExternoInstituicao(){
		return (tipo != null && tipo.getId() == TipoMembroBanca.EXAMINADOR_EXTERNO_A_INSTITUICAO);
	}
	
	/** Retorna uma descri��o textual do membro da banca, para o certificado de participa��o. 
	 * @return
	 */
	public String getDescricaoCertificado() {
		return getNome() + " (" + (instituicao != null ? instituicao.getSigla() + " - " : "") + getTipo().getDescricao() + ")";
	}	
}
