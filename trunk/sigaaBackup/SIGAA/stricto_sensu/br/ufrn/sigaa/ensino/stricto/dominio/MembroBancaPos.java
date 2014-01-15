/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 09/11/2007
 *
 */

package br.ufrn.sigaa.ensino.stricto.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

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
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.rh.dominio.Formacao;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Esta entidade relaciona um docente, seja ele interno ou externo � Institui��o, a
 * uma {@link BancaPos banca de p�s gradua��o}.
 * 
 * @author Andr� Dantas
 */
@Entity
@Table(name = "membro_banca_pos", schema = "stricto_sensu")
public class MembroBancaPos implements Validatable {

	/** Constante que define o membro como sendo o presidente da banca. */
	public static final int PRESIDENTE_BANCA = 1;
	/** Constante que define o membro como sendo examinador interno da banca. */
	public static final int EXAMINADOR_INTERNO = 2;
	/** Constante que define o membro como sendo examinador externo da banca. */
	public static final int EXAMINADOR_EXTERNO_AO_PROGRAMA = 3;
	/** Constante que define o membro da banca como sendo examinador externo � institui��o. */
	public static final int EXAMINADOR_EXTERNO_A_INSTITUICAO = 4;

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_banca_pos_docente", nullable = false)		
	private int id;

	/** Fun��o do docente na banca. */
	private Integer funcao;

	/** Servidor docente do programa participante da banca. */
	@ManyToOne
	@JoinColumn(name = "id_docente_programa")
	private Servidor docentePrograma;

	/** Servidor docente  da UFRN, por�m externo ao programa, participante da banca. */
	@ManyToOne
	@JoinColumn(name = "id_docente_externo_programa")
	private Servidor docenteExternoPrograma;

	/** Docente externo � UFRN participante da banca. */
	@ManyToOne
	@JoinColumn(name = "id_docente_externo")
	private DocenteExterno docenteExternoInstituicao;

	/** Banca de p�s ao qual o docente est� participando. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_banca")
	private BancaPos banca;

	/** Maior forma��o do docente caso seja um membro externo a Institui��o que N�O SEJA NEM DOCENTE EXTERNO. */
	@ManyToOne
	@JoinColumn(name = "id_maior_formacao")
	private Formacao maiorFormacao;

	/** Institui��o de origem do docente caso seja um membro externo a Institui��o que N�O SEJA NEM DOCENTE EXTERNO. */
	@ManyToOne
	@JoinColumn(name = "id_instituicao")
	private InstituicoesEnsino instituicao;

	/** Pessoa que cont�m os dados pessoais do docente da banca caso seja um membro externo a Institui��o que N�O SEJA NEM DOCENTE EXTERNO. */
	@ManyToOne
	@JoinColumn(name = "id_pessoa_membro_externo")
	private Pessoa pessoaMembroExterno;

	/** Ano de conclus�o da �LTIMA titula��o desta pessoa. 
	 * Utilizado no cadastro de banca quando � um docente externo a Institui��o e que NAO � DOCENTE EXTERNO.
	 * @return
	 */
	@Column(name="anoconclusao")
	private Integer anoConclusao;

	/** Retorna a chave prim�ria.
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Seta a chave prim�ria.
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna o servidor docente do programa participante da banca. 
	 * @return
	 */
	public Servidor getDocentePrograma() {
		return docentePrograma;
	}

	/** Seta o servidor docente do programa participante da banca.
	 * @param docentePrograma
	 */
	public void setDocentePrograma(Servidor docentePrograma) {
		this.docentePrograma = docentePrograma;
	}

	/** Retorna a banca de p�s ao qual o docente est� participando. 
	 * @return
	 */
	public BancaPos getBanca() {
		return banca;
	}

	/** Seta a banca de p�s ao qual o docente est� participando.
	 * @param banca
	 */
	public void setBanca(BancaPos banca) {
		this.banca = banca;
	}

	/** Retorna a pessoa que cont�m os dados pessoais do docente da banca caso seja um membro externo a Institui��o que N�O SEJA NEM DOCENTE EXTERNO. 
	 * @return
	 */
	public Pessoa getPessoaMembroExterno() {
		return pessoaMembroExterno;
	}

	/** Seta a pessoa que cont�m os dados pessoais do docente da banca caso seja um membro externo a Institui��o que N�O SEJA NEM DOCENTE EXTERNO.
	 * @param pessoa
	 */
	public void setPessoaMembroExterno(Pessoa pessoa) {
		this.pessoaMembroExterno = pessoa;
	}

	/** Retorna a maior forma��o do docente caso seja um membro externo a Institui��o que N�O SEJA NEM DOCENTE EXTERNO. 
	 * @return
	 */
	public Formacao getMaiorFormacao() {
		return maiorFormacao;
	}

	/** Seta a maior forma��o do docente caso seja um membro externo a Institui��o que N�O SEJA NEM DOCENTE EXTERNO.
	 * @param maiorFormacao
	 */
	public void setMaiorFormacao(Formacao maiorFormacao) {
		this.maiorFormacao = maiorFormacao;
	}

	/** Retorna a institui��o de origem do docente caso seja um membro externo a Institui��o que N�O SEJA NEM DOCENTE EXTERNO. 
	 * @return
	 */
	public InstituicoesEnsino getInstituicao() {
		return instituicao;
	}

	/** Seta a institui��o de origem do docente caso seja um membro externo a Institui��o que N�O SEJA NEM DOCENTE EXTERNO.
	 * @param instituicao
	 */
	public void setInstituicao(InstituicoesEnsino instituicao) {
		this.instituicao = instituicao;
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
		if (isExterno()) {
			ValidatorUtil.validateRequired(instituicao, "Institui��o de Ensino", lista);
			ValidatorUtil.validateRequired(maiorFormacao, "Maior Forma��o", lista);
			ValidatorUtil.validateRequired(pessoaMembroExterno.getNome(), "Nome", lista);
			ValidatorUtil.validateRequired(pessoaMembroExterno.getEmail(), "Email", lista);
			ValidatorUtil.validateEmail(pessoaMembroExterno.getEmail(), "Email", lista);
		} else {
			if (isEmpty(docentePrograma) && isEmpty(docenteExternoInstituicao) && isEmpty(docenteExternoPrograma))
				lista.addErro("Docente do Programa: Campo obrigat�rio n�o informado");
		}
		return lista;
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

	/** Retorna a fun��o do docente na banca. 
	 * @return
	 */
	public Integer getFuncao() {
		return funcao;
	}

	/** Seta a fun��o do docente na banca.
	 * @param funcao
	 */
	public void setFuncao(Integer funcao) {
		this.funcao = funcao;
	}

	/** Indica se a fun��o do membro da banca � de examinador externo � institui��o.
	 * @return
	 */
	public boolean isExterno() {
		return funcao == EXAMINADOR_EXTERNO_A_INSTITUICAO;
	}
	
	/** Indica se a fun��o do membro da banca � de presidente da banca.
	 * @return
	 */
	public boolean isPresidenteBanca(){
		return funcao == PRESIDENTE_BANCA;
	}
	
	/** Indica se a fun��o do membro da banca � de presidente da banca.
	 * @return
	 */
	public boolean isExaminadorInterno(){
		return funcao == EXAMINADOR_INTERNO;
	}

	/** Indica se a fun��o do membro da banca � de examinador externo ao programa.
	 * @return
	 */
	public boolean isExaminadorExternoPrograma(){
		return funcao == EXAMINADOR_EXTERNO_AO_PROGRAMA;	
	}

	/** Retorna uma descri��o textual da titula��o do membro da banca (Doutor, Mestre, ou Especialista).
	 * @return
	 */
	public String getTipoDescricaoTitulo() {
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
	
	/** Retorna uma descri��o textual da fun��o do membro na banca: Presidente, Interno, Externo ao Programa, ou Externo � Institui��o.
	 * @return
	 */
	public String getTipoDescricao() {
		if (funcao == EXAMINADOR_EXTERNO_A_INSTITUICAO)
			return "Externo � Institui��o";
		else if (funcao == EXAMINADOR_EXTERNO_AO_PROGRAMA)
			return "Externo ao Programa";
		else if (funcao == EXAMINADOR_INTERNO)
			return "Interno";
		else if (funcao == PRESIDENTE_BANCA)
			return "Presidente";
		else
			return "";
	}

	/** Retorna uma descri��o textual mais completa da fun��o do membro na banca.
	 * @see MembroBancaPos#getTipoDescricao()
	 * @return
	 */
	public String getTipoDescricaoCompleto() {
		if (funcao == PRESIDENTE_BANCA)
			return getTipoDescricao();
		else
			return "Examinador " + getTipoDescricao();
	}

	/** Retorna uma identifica��o do membro da banca, contendo o nome do examinador, a sigla da institui��o (caso externo ao programa),
	 * a matr�cula SIAPE (caso membro da institui��o), e o CPF (caso membro externo � institui��o).
	 * @return
	 */
	public String getMembroIdentificacao() {
		if (isExterno()) {
			return pessoaMembroExterno.getNome() + " - " + instituicao.getSigla();
		} else {
			if (!isEmpty(docentePrograma))
				return docentePrograma.getSiapeNome();
			if (!isEmpty(docenteExternoPrograma))
				return docenteExternoPrograma.getSiapeNome();
			else if (!isEmpty(docenteExternoInstituicao))
				return docenteExternoInstituicao.getCpfNome() + " - " + instituicao.getSigla();
			else
				return "";
		}
	}

	/** Retorna o nome do membro da banca.
	 * @return
	 */
	public String getNome() {
		if (isExterno()) {
			return pessoaMembroExterno.getNome();
		} else {
			if (!isEmpty(docentePrograma))
				return docentePrograma.getNome();
			else if (!isEmpty(docenteExternoPrograma))
				return docenteExternoPrograma.getNome();
			else if (!isEmpty(docenteExternoInstituicao))
				return docenteExternoInstituicao.getNome();
			else
				return "";
		}
	}

	/** Retorna o e-mail do membro da banca.
	 * @return
	 */
	public String getEmail(){
		if (isExterno()) {
			return pessoaMembroExterno.getEmail();
		} else if ( isServidor() ){
			return getServidor().getPessoa().getEmail();
		} else {
			return null;
		}
	}
	
	/** Retorna uma descri��o textual do tipo do membro da banca.
	 *@see #getTipoDescricao()
	 * @return
	 */
	public String getDescricao() {
		
		StringBuilder sb = new StringBuilder();
		sb.append(getTipoDescricao());
		if (!isEmpty(getMembroIdentificacao()))
			sb.append(" - " + getMembroIdentificacao());
		if (instituicao != null && !isEmpty(instituicao.getSigla()))
			sb.append(" - " + instituicao.getSigla());
		
		return sb.toString();
//		return getTipoDescricao() + " - " + getMembroIdentificacao() + " ("+instituicao.getSigla()+")";
	}

	/** Retorna uma descri��o textual do membro da banca, para o certificado de participa��o. 
	 * @return
	 */
	public String getDescricaoCertificado() {
		return getNome() + " (" + (instituicao != null ? instituicao.getSigla() + " - " : "") + getTipoDescricaoCompleto() + ")";
	}

	/** Retorna o servidor docente da UFRN, por�m externo ao programa, participante da banca. 
	 * @return
	 */
	public Servidor getDocenteExternoPrograma() {
		return docenteExternoPrograma;
	}

	/** Seta o servidor docente da UFRN, por�m externo ao programa, participante da banca.
	 * @param docenteExternoPrograma
	 */
	public void setDocenteExternoPrograma(Servidor docenteExternoPrograma) {
		this.docenteExternoPrograma = docenteExternoPrograma;
	}

	/** Indica se o membro da banca � servidor.
	 * @return
	 */
	public boolean isServidor() {
		return getServidor() != null;
	}

	/** Retorna os dados do servidor do membro da banca.  
	 * @return
	 */
	public Servidor getServidor() {
		if (docentePrograma != null && docentePrograma.getId() > 0)
			return docentePrograma;
		else if (docenteExternoPrograma != null && docenteExternoPrograma.getId() > 0)
			return docenteExternoPrograma;
		else
			return null;
	}

	/** Retorna os dados pessoais do membro da banca.
	 * @return
	 */
	public Pessoa getPessoa() {
		if (docentePrograma != null)
			return docentePrograma.getPessoa();
		else if (docenteExternoInstituicao != null)
			return docenteExternoInstituicao.getPessoa();
		else if (docenteExternoPrograma != null)
			return docenteExternoPrograma.getPessoa();
		else
			return pessoaMembroExterno;
	}

	/** Retorna o ano de conclus�o da �LTIMA titula��o desta pessoa. Utilizado no cadastro de banca quando � um docente externo a Institui��o e que NAO � DOCENTE EXTERNO.
	 * @return
	 */
	public Integer getAnoConclusao() {
		return anoConclusao;
	}

	/** Seta o ano de conclus�o da �LTIMA titula��o desta pessoa. Utilizado no cadastro de banca quando � um docente externo a Institui��o e que NAO � DOCENTE EXTERNO.
	 * @param anoConclusao
	 */
	public void setAnoConclusao(Integer anoConclusao) {
		this.anoConclusao = anoConclusao;
	}

	/** Seta o docente externo � Institui��o participante da banca. 
	 * @param docenteExternoInstituicao
	 */
	public void setDocenteExternoInstituicao(
			DocenteExterno docenteExternoInstituicao) {
		this.docenteExternoInstituicao = docenteExternoInstituicao;
	}
	
	/** Retorna o docente externo � Institui��o participante da banca. 
	 * @return
	 */
	public DocenteExterno getDocenteExternoInstituicao() {
		return docenteExternoInstituicao;
	}


}
