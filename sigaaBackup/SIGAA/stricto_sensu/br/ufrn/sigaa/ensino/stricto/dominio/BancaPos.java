/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 22/10/2007
 *
 */

package br.ufrn.sigaa.ensino.stricto.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateMaxLength;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.stricto.negocio.ParametrosProgramaPosHelper;
import br.ufrn.sigaa.parametros.dominio.MensagensStrictoSensu;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Informa��es de cadastro de banca de p�s: tipo(pode ser de qualifica��o ou de defesa), o local onde ocorrer� a banca,
 * os membros que participar�o da banca e o n�vel (Mestrado ou Doutorado).
 * Banca contempla as pessoas respons�veis por examinar o trabalho desenvolvido.
 * @author Andre Dantas
 */
@Entity
@Table(name = "banca_pos", schema = "stricto_sensu")
public class BancaPos implements Validatable {

	/** Constante que define o tipo de banca: QUALIFICA��O. */
	public static final int BANCA_QUALIFICACAO = 1;
	
	/** Constante que define o tipo de banca: DEFESA. */
	public static final int BANCA_DEFESA = 2;
	
	/** Constante que define a banca como Ativa */
	public static final int ATIVO = 1;
	
	/** Constante que define a banca como Cancelado */
	public static final int CANCELADA = 2;
	
	/** Constante que define a banca que est� pendente de aprova��o */
	public static final int PENDENTE_APROVACAO = 3;	

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_banca_pos", nullable = false)
	private int id;

	/**
	 * Dados da banca de defesa, tais como: o t�tulo, resumo, quantidade de p�ginas, data do cadastro, dentre outros.
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_dados_defesa")
	private DadosDefesa dadosDefesa;

	/**
	 * Local onde ser� realizada a banca de defesa ou qualifica��o.
	 */
	private String local;

	/**
	 * Tipo de banca, ou seja, se a banca � do tipo Defesa ou Qualifica��o. 
	 * @see BancaPos#BANCA_DEFESA
	 * @see BancaPos#BANCA_QUALIFICACAO 
	 */
	private int tipoBanca; // qualifica��o ou defesa

	/**
	 * Data e Hora referente a realiza��o da banca de defesa ou qualifica��o.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	private Date data;
	
	
	/**
	 * Hora.
	 * Campo n�o obrigat�rio de preenchimento;
	 */
	@Transient
	private Date hora;
	
	/**
	 * Identificador do arquivo da ata da banca de defesa ou qualifica��o.
	 */
	@Column(name = "id_arquivo_ata")
	private Integer idArquivo;

	/**
	 * Lista de membros que participaram da banca, podendo ser docentes da UFRN e externos.
	 */
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "banca")
	@OrderBy("funcao")
	private List<MembroBancaPos> membrosBanca;
	
	/**  
	 * Matr�cula no componente de qualifica��o associado a esta banca.
	 * Tais como: A situa��o da turma, o discente, turma, m�dia final do discente, n�mero de faltas, dentre outros.
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_matricula_componente")
	private MatriculaComponente matriculaComponente;
	
	/**
	 * Atributo respons�vel por armazenar o usu�rio logado.
	 */
	@Transient
	private UsuarioGeral usuario;
	
	/** 
	 * Atributo respons�vel por armazenar o status da situa��o da banca. 
	 *  1 - ATIVO;
	 *  2 - CANCELADO;
	 * */
	@Column(name = "status")
	private Integer status;
	
	/** Atributo respons�vel por armazenar a observa��o contendo a justificativa de cancelamento da banca. */
	@Column(name = "observacao",length = 800)
	private String observacao;
	
	/** 
	 * Retorna a lista dos membros que participaram da banca, podendo ser docentes da UFRN e externos.
	 */
	public List<MembroBancaPos> getMembrosBanca() {
		return membrosBanca;
	}
	
	/**
	 * Remove um membro da lista do cadastro da banca.
	 * @param pessoa
	 * @return
	 */
	public boolean removeMembroBanca(Pessoa pessoa){
		if( !isEmpty(membrosBanca) ){
			for (MembroBancaPos mb : membrosBanca) {
				if( mb.getPessoa().equals(pessoa) ){
					membrosBanca.remove(mb);
					return true;
				}
			}
		}
		return false;
	}

	/** 
	 * Seta a lista de membros que participaram da banca, podendo ser docentes da UFRN e externos.
	 */
	public void setMembrosBanca(List<MembroBancaPos> membrosBanca) {
		this.membrosBanca = membrosBanca;
	}

	/**
	 * Retorna a Chave Prim�ria
	 */
	public int getId() {
		return id;
	}

	/**
	 * Seta a Chave Prim�ria
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** 
	 * Retorna os dados da banca de defesa.
	 */
	public DadosDefesa getDadosDefesa() {
		return dadosDefesa;
	}

	/** 
	 * Seta os dados da banca de defesa.
	 */
	public void setDadosDefesa(DadosDefesa dadosDefesa) {
		this.dadosDefesa = dadosDefesa;
	}

	/** 
	 * Retorna o local da realiza��o da banca.
	 */
	public String getLocal() {
		return local;
	}

	/** 
	 * Seta o local da realiza��o da banca.
	 */
	public void setLocal(String local) {
		this.local = local;
	}
	
	/** 
	 * Retorna a hora da realiza��o da banca (transiente).
	 * Recebe as horas e minutos definidos na data da realiza��o da banca, caso n�o seja definido um valor diretamente. 
	 */
	public Date getHora() {
		if( !isEmpty( this.data ) 
				&& CalendarUtils.calculaQuantidadeHorasEntreDatas( CalendarUtils.descartarHoras(this.data), this.data ) > 0  )
			this.hora =  this.data;
		return hora;
	}

	/** 
	 * Seta a hora da realiza��o da banca (transiente).
	 */
	public void setHora(Date hora) {
		this.hora = hora;
	}
	
	/** 
	 * Retorna a data da realiza��o da banca.
	 */
	public Date getData() {
		return data;
	}

	/** 
	 * Seta a data da realiza��o da banca.
	 */
	public void setData(Date data) {
		this.data = data;
	}
	
	/** 
	 * Retorna a data e a hora formatadas.
	 */
	public String getDescricaoDataHora(){
		String strDataHora = CalendarUtils.format(getData(), "dd 'de' MMMM 'de' yyyy");
		if( !isEmpty( getHora() ) )
			strDataHora += CalendarUtils.format(getHora(), " '�s' HH:mm");
		return strDataHora;
	}
	
	/** 
	 *  Retorna o tipo de banca.
	 */
	public int getTipoBanca() {
		return tipoBanca;
	}

	/**  
	 * Seta o tipo de banca.
	 */
	public void setTipoBanca(int tipoBanca) {
		this.tipoBanca = tipoBanca;
	}
	
	/**
	 * Retorna o status da banca
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * Seta o status da banca
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * Retorna a observa��o da banca
	 * @return the observacao
	 */
	public String getObservacao() {
		return observacao;
	}

	/**
	 * Seta a observa��o da banca
	 * @param observacao the observacao to set
	 */
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	/**
	 * Atributo transiente que indica que a defesa � antiga 
	 */
	@Transient
	public boolean defesaAntiga;

	public boolean isDefesaAntiga() {
		return defesaAntiga;
	}

	public void setDefesaAntiga(boolean defesaAntiga) {
		this.defesaAntiga = defesaAntiga;
	}

	/** 
	 * Esse m�todo serve para verificar se os campos obrigat�rios foram preenchidos, sendo eles os seguintes: matriculaComponente,
	 * tipoBanca, local, t�tulo, o n�mero de p�ginas, data da defesa, o resumo e a palavra chave. 
	 * 
	 */	
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		
		if (isEmpty(matriculaComponente) && !isDefesaAntiga())
			validateRequired(matriculaComponente, "Atividade Matriculada", erros);
		validateRequired(tipoBanca, "Tipo de Banca", erros);
		validateRequired(local, "Local", erros);
		validateMaxLength(local, 100, "Local", erros);
		validateRequired(getTitulo(), "T�tulo", erros);
		validateRequired(getPaginas(), "P�ginas", erros);
		if( getPaginas() != null)
			ValidatorUtil.validaFloatPositivo( Float.valueOf( getPaginas() ), "P�ginas", erros);
		
		validateRequired(data, "Data", erros);
		if (!isDefesaAntiga())
			validateRequired(hora, "Hora", erros);
		validateRequired(getResumo(), "Resumo", erros);
		validateRequired(getPalavraChave(), "Palavra Chave", erros);
		
		try {
			
			Integer prazoMaximo = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.PRAZO_MAXIMO_CADASTRO_BANCA);
			ParametrosProgramaPos parametrosProgramaPos = ParametrosProgramaPosHelper.getParametros(dadosDefesa.getDiscente());

			if (tipoBanca == BANCA_QUALIFICACAO)
				if (parametrosProgramaPos.getPrazoMinCadastroBancaQualificacao()!=null){
					prazoMaximo = parametrosProgramaPos.getPrazoMinCadastroBancaQualificacao();
				}	
			else if (tipoBanca == BANCA_DEFESA)
				if (parametrosProgramaPos.getPrazoMinCadastroBancaDefesa()!=null){
					prazoMaximo = parametrosProgramaPos.getPrazoMinCadastroBancaDefesa();
				}
			
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, prazoMaximo);
			Date dataSemHoras = DateUtils.truncate(cal.getTime(), Calendar.DAY_OF_MONTH);
			
			if (getData().before(dataSemHoras)) {
				if (isValidarPrazoQualificacao())
					erros.addMensagem(UFRNUtils.getMensagem(MensagensStrictoSensu.MENSAGEM_PRAZO_MAXIMO_CADASTRO_BANCA, prazoMaximo));
						
				if (isValidarPrazoCadastro())
					erros.addMensagem(UFRNUtils.getMensagem(MensagensStrictoSensu.MENSAGEM_PRAZO_MAXIMO_CADASTRO_BANCA, prazoMaximo));
			}
			
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return erros;
	}

	/**
	 * Verifica se o usu�rio � PPG
	 * @return
	 */
	private boolean isPPG() {
		return usuario != null && usuario.isUserInRole(SigaaPapeis.PPG, SigaaPapeis.ADMINISTRADOR_STRICTO);
	}

	/**
	 * Condi��o que determina se banca vai ser validada pelo Prazo m�ximo
	 * @return
	 */
	public boolean isValidarPrazoCadastro() {
		return !isEmpty(getData()) && isDefesa() && !isDefesaAntiga() && id == 0 && !isPPG();
	}

	/**
	 * Condi��o que determina se banca vai ser validada pelo Prazo m�ximo
	 * @return
	 */
	public boolean isValidarPrazoQualificacao() {
		return !isEmpty(getData()) && !isDefesa() && !isDefesaAntiga() && id == 0 && !isPPG();
	}
	
	/** 
	 * Retorna o t�tulo do trabalho.
	 */
	public String getTitulo() {
		return dadosDefesa != null ? dadosDefesa.getTitulo() : "";
	}
	
	/**
	 * Define o t�tulo do trabalho.
	 * @param titulo
	 */
	public void setTitulo(String titulo) {
		if(dadosDefesa != null) dadosDefesa.setTitulo(titulo);
	}

	/** 
	 * Retorna o n�mero de p�ginas do trabalho.
	 */
	public Integer getPaginas() {
		return dadosDefesa != null ? dadosDefesa.getPaginas() : null;
	}

	/** 
	 * Retorna o resumo do trabalho.
	 */
	public String getResumo() {
		return dadosDefesa != null ? dadosDefesa.getResumo() : "";
	}

	/** 
	 * Retorna as palavras chaves do trabalho.
	 */
	public String getPalavraChave() {
		return dadosDefesa != null ? dadosDefesa.getPalavrasChave() : "";
	}
	
	/**
	 * Define as palavras chaves do trabalho.
	 * @param palavraChave
	 */
	public void setPalavraChave(String palavraChave) {
		if(dadosDefesa != null) dadosDefesa.setPalavrasChave(palavraChave);
	}
	
	/** 
	 * Retorna a grande �rea de conhecimento do CNPq.
	 */
	public AreaConhecimentoCnpq getGrandeArea() {
		return dadosDefesa != null ? (dadosDefesa.getArea() != null ? dadosDefesa.getArea().getGrandeArea() : null) : null;
	}
	
	/** 
	 * Retorna a �rea de conhecimento do CNPq.
	 */
	public AreaConhecimentoCnpq getArea() {
		return dadosDefesa != null ? (dadosDefesa.getArea() != null ? dadosDefesa.getArea().getArea() : null) : null;
	}

	/** 
	 * Retorna a sub-�rea de conhecimento do CNPq.
	 */
	public AreaConhecimentoCnpq getSubArea() {
		return dadosDefesa != null ? (dadosDefesa.getArea() != null ? dadosDefesa.getArea().getSubarea() : null) : null;
	}
	
	/** 
	 * Retorna a especialidade.
	 */
	public AreaConhecimentoCnpq getEspecialidade() {
		return dadosDefesa != null ? (dadosDefesa.getArea() != null ? dadosDefesa.getArea().getEspecialidade() : null) : null;
	}

	/**
	 * Retorna o ano da data de defesa.
	 */
	public String getAno(){
		SimpleDateFormat ano = new SimpleDateFormat("yyyy");
		return (data != null)?ano.format(data):"";
	}

	/** 
	 * Indica se a banca � de qualifica��o.
	 */
	public boolean isQualificacao() {
		return tipoBanca == BANCA_QUALIFICACAO;
	}

	/**
	 * Indica se a banca � de defesa.
	 */
	public boolean isDefesa() {
		return tipoBanca == BANCA_DEFESA;
	}

	/** 
	 * Retorna uma descri��o textual do tipo de banca (defesa ou qualifica��o).
	 */
	public String getTipoDescricao() {
		return isQualificacao() ? "QUALIFICA��O" : "DEFESA";
	}

	/** 
	 * Retorna o ID do arquivo da ata da banca.
	 */
	public Integer getIdArquivo() {
		return idArquivo;
	}

	/** 
	 * Seta o ID do arquivo da ata da banca.
	 */
	public void setIdArquivo(Integer idArquivo) {
		this.idArquivo = idArquivo;
	}

	/** 
	 * Retorna uma descri��o textual dos membros da banca.
	 */
	public String getDescricaoMembros() {
		StringBuilder sb = new StringBuilder();
		if (!isEmpty(membrosBanca)) {
			for (MembroBancaPos membro : membrosBanca) {
				sb.append(membro.getDescricao() + System.getProperty("line.separator"));
			}
		}
		return sb.toString();
	}

	/** 
	 * Retorna uma descri��o textual do Status informado.
	 * @param status
	 * @return
	 */
	@Transient
	public String getDescricaoStatus(){
		if( status != null ){
			switch (status) {
				case ATIVO:          return "ATIVO";
				case CANCELADA:      return "CANCELADA";
				case PENDENTE_APROVACAO:      return "PENDENTE DE APROVA��O";
				default:             return null;
			}
		}else{
			return "INDEFINIDO";
		}
	}
	
	/** 
	 * Indica se a banca � de mestrado.
	 */
	public boolean isMestrado() {
		return dadosDefesa.getDiscente().getNivel() == NivelEnsino.MESTRADO;
	}
	
	/** 
	 * Indica se a banca � de doutorado.
	 */
	public boolean isDoutorado() {
		return dadosDefesa.getDiscente().getNivel() == NivelEnsino.DOUTORADO;
	}

	/**
	 * Indica se a banca � de mestrado ou doutorado.
	 */
	public String getNivel() {
		return isMestrado() ? "MESTRADO" : "DOUTORADO";
	}
	
	public String getGrauObtido() {
		return isMestrado() ? "MESTRE" : "DOUTOR";
	}
	
	/** 
	 * Retorna a matr�cula no componente de qualifica��o associado a esta banca. 
	 */
	public MatriculaComponente getMatriculaComponente() {
		return matriculaComponente;
	}

	/** 
	 * Seta a matr�cula no componente de qualifica��o associado a esta banca. 
	 */
	public void setMatriculaComponente(MatriculaComponente matriculaComponente) {
		this.matriculaComponente = matriculaComponente;
	}
	
	/**
	 * Retorna uma descri��o textual da banca no formato: descri��o do tipo da
	 * banca, seguido de v�rgula, seguido do t�tulo do trabalho.
	 */
	@Override
	public String toString() {
		return getTipoDescricao() + ", " + getTitulo(); 
	}

	/**
	 * Retorna o usu�rio
	 * @return the usuario
	 */
	public UsuarioGeral getUsuario() {
		return usuario;
	}

	/**
	 * Seta o usu�rio
	 * @param usuario the usuario to set
	 */
	public void setUsuario(UsuarioGeral usuario) {
		this.usuario = usuario;
	}
	
	/** Indica se a banca foi cancelada.
	 * @return
	 */
	public boolean isCancelada(){
		return status == CANCELADA;
	}
	
	/** Indica se a banca est� pendente de aprova��o.
	 * @return
	 */
	public boolean isPendente(){
		return status == PENDENTE_APROVACAO;
	}	
	
	public String getDataFormatada(){
		return Formatador.getInstance().formatarData(getData());
	}
	
}
