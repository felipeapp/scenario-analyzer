/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Informações de cadastro de banca de pós: tipo(pode ser de qualificação ou de defesa), o local onde ocorrerá a banca,
 * os membros que participarão da banca e o nível (Mestrado ou Doutorado).
 * Banca contempla as pessoas responsáveis por examinar o trabalho desenvolvido.
 * @author Andre Dantas
 */
@Entity
@Table(name = "banca_pos", schema = "stricto_sensu")
public class BancaPos implements Validatable {

	/** Constante que define o tipo de banca: QUALIFICAÇÃO. */
	public static final int BANCA_QUALIFICACAO = 1;
	
	/** Constante que define o tipo de banca: DEFESA. */
	public static final int BANCA_DEFESA = 2;
	
	/** Constante que define a banca como Ativa */
	public static final int ATIVO = 1;
	
	/** Constante que define a banca como Cancelado */
	public static final int CANCELADA = 2;
	
	/** Constante que define a banca que está pendente de aprovação */
	public static final int PENDENTE_APROVACAO = 3;	

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_banca_pos", nullable = false)
	private int id;

	/**
	 * Dados da banca de defesa, tais como: o título, resumo, quantidade de páginas, data do cadastro, dentre outros.
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_dados_defesa")
	private DadosDefesa dadosDefesa;

	/**
	 * Local onde será realizada a banca de defesa ou qualificação.
	 */
	private String local;

	/**
	 * Tipo de banca, ou seja, se a banca é do tipo Defesa ou Qualificação. 
	 * @see BancaPos#BANCA_DEFESA
	 * @see BancaPos#BANCA_QUALIFICACAO 
	 */
	private int tipoBanca; // qualificação ou defesa

	/**
	 * Data e Hora referente a realização da banca de defesa ou qualificação.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	private Date data;
	
	
	/**
	 * Hora.
	 * Campo não obrigatório de preenchimento;
	 */
	@Transient
	private Date hora;
	
	/**
	 * Identificador do arquivo da ata da banca de defesa ou qualificação.
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
	 * Matrícula no componente de qualificação associado a esta banca.
	 * Tais como: A situação da turma, o discente, turma, média final do discente, número de faltas, dentre outros.
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_matricula_componente")
	private MatriculaComponente matriculaComponente;
	
	/**
	 * Atributo responsável por armazenar o usuário logado.
	 */
	@Transient
	private UsuarioGeral usuario;
	
	/** 
	 * Atributo responsável por armazenar o status da situação da banca. 
	 *  1 - ATIVO;
	 *  2 - CANCELADO;
	 * */
	@Column(name = "status")
	private Integer status;
	
	/** Atributo responsável por armazenar a observação contendo a justificativa de cancelamento da banca. */
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
	 * Retorna a Chave Primária
	 */
	public int getId() {
		return id;
	}

	/**
	 * Seta a Chave Primária
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
	 * Retorna o local da realização da banca.
	 */
	public String getLocal() {
		return local;
	}

	/** 
	 * Seta o local da realização da banca.
	 */
	public void setLocal(String local) {
		this.local = local;
	}
	
	/** 
	 * Retorna a hora da realização da banca (transiente).
	 * Recebe as horas e minutos definidos na data da realização da banca, caso não seja definido um valor diretamente. 
	 */
	public Date getHora() {
		if( !isEmpty( this.data ) 
				&& CalendarUtils.calculaQuantidadeHorasEntreDatas( CalendarUtils.descartarHoras(this.data), this.data ) > 0  )
			this.hora =  this.data;
		return hora;
	}

	/** 
	 * Seta a hora da realização da banca (transiente).
	 */
	public void setHora(Date hora) {
		this.hora = hora;
	}
	
	/** 
	 * Retorna a data da realização da banca.
	 */
	public Date getData() {
		return data;
	}

	/** 
	 * Seta a data da realização da banca.
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
			strDataHora += CalendarUtils.format(getHora(), " 'às' HH:mm");
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
	 * Retorna a observação da banca
	 * @return the observacao
	 */
	public String getObservacao() {
		return observacao;
	}

	/**
	 * Seta a observação da banca
	 * @param observacao the observacao to set
	 */
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	/**
	 * Atributo transiente que indica que a defesa é antiga 
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
	 * Esse método serve para verificar se os campos obrigatórios foram preenchidos, sendo eles os seguintes: matriculaComponente,
	 * tipoBanca, local, título, o número de páginas, data da defesa, o resumo e a palavra chave. 
	 * 
	 */	
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		
		if (isEmpty(matriculaComponente) && !isDefesaAntiga())
			validateRequired(matriculaComponente, "Atividade Matriculada", erros);
		validateRequired(tipoBanca, "Tipo de Banca", erros);
		validateRequired(local, "Local", erros);
		validateMaxLength(local, 100, "Local", erros);
		validateRequired(getTitulo(), "Título", erros);
		validateRequired(getPaginas(), "Páginas", erros);
		if( getPaginas() != null)
			ValidatorUtil.validaFloatPositivo( Float.valueOf( getPaginas() ), "Páginas", erros);
		
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
	 * Verifica se o usuário é PPG
	 * @return
	 */
	private boolean isPPG() {
		return usuario != null && usuario.isUserInRole(SigaaPapeis.PPG, SigaaPapeis.ADMINISTRADOR_STRICTO);
	}

	/**
	 * Condição que determina se banca vai ser validada pelo Prazo máximo
	 * @return
	 */
	public boolean isValidarPrazoCadastro() {
		return !isEmpty(getData()) && isDefesa() && !isDefesaAntiga() && id == 0 && !isPPG();
	}

	/**
	 * Condição que determina se banca vai ser validada pelo Prazo máximo
	 * @return
	 */
	public boolean isValidarPrazoQualificacao() {
		return !isEmpty(getData()) && !isDefesa() && !isDefesaAntiga() && id == 0 && !isPPG();
	}
	
	/** 
	 * Retorna o título do trabalho.
	 */
	public String getTitulo() {
		return dadosDefesa != null ? dadosDefesa.getTitulo() : "";
	}
	
	/**
	 * Define o título do trabalho.
	 * @param titulo
	 */
	public void setTitulo(String titulo) {
		if(dadosDefesa != null) dadosDefesa.setTitulo(titulo);
	}

	/** 
	 * Retorna o número de páginas do trabalho.
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
	 * Retorna a grande área de conhecimento do CNPq.
	 */
	public AreaConhecimentoCnpq getGrandeArea() {
		return dadosDefesa != null ? (dadosDefesa.getArea() != null ? dadosDefesa.getArea().getGrandeArea() : null) : null;
	}
	
	/** 
	 * Retorna a área de conhecimento do CNPq.
	 */
	public AreaConhecimentoCnpq getArea() {
		return dadosDefesa != null ? (dadosDefesa.getArea() != null ? dadosDefesa.getArea().getArea() : null) : null;
	}

	/** 
	 * Retorna a sub-área de conhecimento do CNPq.
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
	 * Indica se a banca é de qualificação.
	 */
	public boolean isQualificacao() {
		return tipoBanca == BANCA_QUALIFICACAO;
	}

	/**
	 * Indica se a banca é de defesa.
	 */
	public boolean isDefesa() {
		return tipoBanca == BANCA_DEFESA;
	}

	/** 
	 * Retorna uma descrição textual do tipo de banca (defesa ou qualificação).
	 */
	public String getTipoDescricao() {
		return isQualificacao() ? "QUALIFICAÇÃO" : "DEFESA";
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
	 * Retorna uma descrição textual dos membros da banca.
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
	 * Retorna uma descrição textual do Status informado.
	 * @param status
	 * @return
	 */
	@Transient
	public String getDescricaoStatus(){
		if( status != null ){
			switch (status) {
				case ATIVO:          return "ATIVO";
				case CANCELADA:      return "CANCELADA";
				case PENDENTE_APROVACAO:      return "PENDENTE DE APROVAÇÃO";
				default:             return null;
			}
		}else{
			return "INDEFINIDO";
		}
	}
	
	/** 
	 * Indica se a banca é de mestrado.
	 */
	public boolean isMestrado() {
		return dadosDefesa.getDiscente().getNivel() == NivelEnsino.MESTRADO;
	}
	
	/** 
	 * Indica se a banca é de doutorado.
	 */
	public boolean isDoutorado() {
		return dadosDefesa.getDiscente().getNivel() == NivelEnsino.DOUTORADO;
	}

	/**
	 * Indica se a banca é de mestrado ou doutorado.
	 */
	public String getNivel() {
		return isMestrado() ? "MESTRADO" : "DOUTORADO";
	}
	
	public String getGrauObtido() {
		return isMestrado() ? "MESTRE" : "DOUTOR";
	}
	
	/** 
	 * Retorna a matrícula no componente de qualificação associado a esta banca. 
	 */
	public MatriculaComponente getMatriculaComponente() {
		return matriculaComponente;
	}

	/** 
	 * Seta a matrícula no componente de qualificação associado a esta banca. 
	 */
	public void setMatriculaComponente(MatriculaComponente matriculaComponente) {
		this.matriculaComponente = matriculaComponente;
	}
	
	/**
	 * Retorna uma descrição textual da banca no formato: descrição do tipo da
	 * banca, seguido de vírgula, seguido do título do trabalho.
	 */
	@Override
	public String toString() {
		return getTipoDescricao() + ", " + getTitulo(); 
	}

	/**
	 * Retorna o usuário
	 * @return the usuario
	 */
	public UsuarioGeral getUsuario() {
		return usuario;
	}

	/**
	 * Seta o usuário
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
	
	/** Indica se a banca está pendente de aprovação.
	 * @return
	 */
	public boolean isPendente(){
		return status == PENDENTE_APROVACAO;
	}	
	
	public String getDataFormatada(){
		return Formatador.getInstance().formatarData(getData());
	}
	
}
