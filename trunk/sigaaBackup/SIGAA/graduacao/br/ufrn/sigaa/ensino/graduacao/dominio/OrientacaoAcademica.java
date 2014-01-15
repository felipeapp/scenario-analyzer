/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Jul 6, 2007
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Date;
import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * Entidade que representa o vínculo entre aluno e seu orientador acadêmico.
 * O orientador acadêmico tem a mesma função/permissões do coordenador no que diz
 * respeito a orientação de matrícula, onde deve possuir os seguintes papeis: SigaaPapeis.COORDENADOR_CURSO e SigaaPapeis.SECRETARIA_COORDENACAO 
 * @author Victor Hugo
 *
 */
@Entity
@Table(name = "orientacao_academica", schema = "graduacao", uniqueConstraints = {})
public class OrientacaoAcademica implements PersistDB, Comparable<OrientacaoAcademica>, ViewAtividadeBuilder{

	// Usado nas pós-graduações
	public static Character ORIENTADOR = new Character('O');
	public static Character CoORIENTADOR = new Character('C');
	// Usado na graduação
	public static Character ACADEMICO = new Character('A');

	public static final int MAXIMO_ORIENTACOES = 60;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_orientacao_academica", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_servidor", unique = false, nullable = true, insertable = true, updatable = true)
	private Servidor servidor;

	/** docentes externos podem orientar discentes de STRICTO */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_docente_externo", unique = false, nullable = true, insertable = true, updatable = true)
	private DocenteExterno docenteExterno;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_discente", unique = false, nullable = true, insertable = true, updatable = true)
	private Discente discente;

	@Temporal(TemporalType.DATE)
	@Column(name = "inicio", unique = false, nullable = true, insertable = true, updatable = true)
	private Date inicio;

	@Temporal(TemporalType.DATE)
	@Column(name = "fim", unique = false, nullable = true, insertable = true, updatable = true)
	private Date fim;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro", unique = false, nullable = true, insertable = true, updatable = true)
	@CriadoEm
	private Date dataCadastro;

	/** data que esta orientação foi finalizada */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada", unique = false, nullable = true, insertable = true, updatable = true)
	@CriadoPor
	private RegistroEntrada registroEntrada;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_finalizacao", unique = false, nullable = true, insertable = true, updatable = true)
	private Date dataFinalizacao;

	/** usuário que finalizou esta orientação */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_finalizacao", unique = false, nullable = true, insertable = true, updatable = true)
	private RegistroEntrada registroFinalizacao;

	private Character tipoOrientacao;
	
	@Transient
	/** atributo utilizado no relatório de orientações analítico para exibir o tipo da bolsa dos orientandos */
	private String tipoBolsa;

	/** atributo utilizado no relatório de orientações sintético, que armazena a quantidade de orientandos que o servidor possui */
	@Transient
	private int total;

	private boolean cancelado = false;

	/**
	 * Construtor da classe. Não passando parâmetros.
	 */
	public OrientacaoAcademica() {
		super();
	}

	/**
	 * Construtor da classe. Neste seta o id passado por parâmetro. 
	 * @param id
	 */
	public OrientacaoAcademica(Integer id) {
		this.id = id;
	}
	
	/**
	 * Construtor da classe. Neste seta o id, idservidor, siape, nomeServidor, IdDiscente, matricula, nivel, nomeDiscente,
	 * inicio, fim, tipo, passados por parâmetro.
	 * @param id
	 * @param idServidor
	 * @param siape
	 * @param nomeServidor
	 * @param idDiscente
	 * @param matricula
	 * @param nivel
	 * @param nomeDiscente
	 * @param inicio
	 * @param fim
	 * @param tipo
	 */
	public OrientacaoAcademica( int id, int idServidor, int siape, String nomeServidor, int idDiscente,
			Long matricula, char nivel, String nomeDiscente, Date inicio, Date fim, Character tipo ){
		this.id = id;
		this.servidor = new Servidor(idServidor, nomeServidor, siape);
		this.discente = new Discente(idDiscente, matricula, nomeDiscente);
		this.discente.setNivel( nivel );
		this.inicio = inicio;
		this.fim = fim;
		this.tipoOrientacao = tipo;
	}

	/**
	 * Construtor da classe. Neste repassa todos os parâmetros passados no construtor anterior acrescido do emailDiscente. 
	 * @param id
	 * @param idServidor
	 * @param siape
	 * @param nomeServidor
	 * @param idDiscente
	 * @param matricula
	 * @param nivel
	 * @param nomeDiscente
	 * @param emailDiscente
	 * @param inicio
	 * @param fim
	 * @param tipo
	 */
	public OrientacaoAcademica( int id, int idServidor, int siape, String nomeServidor, int idDiscente,
			Long matricula, char nivel, String nomeDiscente, String emailDiscente, Date inicio, Date fim, Character tipo ){
		this.id = id;
		this.servidor = new Servidor(idServidor, nomeServidor, siape);
		this.discente = new Discente(idDiscente, matricula, nomeDiscente, emailDiscente);
		this.discente.setNivel( nivel );
		this.inicio = inicio;
		this.fim = fim;
		this.tipoOrientacao = tipo;
	}

	/**
	 * Construtor da classe. Neste repassa todos os parâmetros passados no construtor anterior acrescido do idUsuario.
	 * @param id
	 * @param idServidor
	 * @param siape
	 * @param nomeServidor
	 * @param idDiscente
	 * @param matricula
	 * @param nivel
	 * @param status
	 * @param nomeDiscente
	 * @param emailDiscente
	 * @param inicio
	 * @param fim
	 * @param tipo
	 * @param idUsuario
	 */
	public OrientacaoAcademica( int id, int idServidor, int siape, String nomeServidor, int idDiscente,
			Long matricula, char nivel, int status,  String nomeDiscente, String emailDiscente, Date inicio, Date fim, Character tipo, int idUsuario ){
		this.id = id;
		this.servidor = new Servidor(idServidor, nomeServidor, siape);
		this.discente = new Discente(idDiscente, matricula, nomeDiscente, emailDiscente);
		this.discente.setNivel( nivel );
		this.discente.setStatus( status );
		this.inicio = inicio;
		this.fim = fim;
		this.tipoOrientacao = tipo;
		this.getDiscente().setUsuario(new Usuario());
		this.getDiscente().getUsuario().setId(idUsuario);
	}
	
	@Override
	public boolean equals(Object obj) {

		if (obj != null) {
			OrientacaoAcademica other = (OrientacaoAcademica) obj;
			if (other.id == this.id)
				return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	/**
	 * Retorna a Descrição do Tipo de Orientação de acordo com seu tipo correspondente. 
	 * @return
	 */
	public String getTipoOrientacaoString() {
		String tipo = "Indefinido";
		if ( tipoOrientacao != null && tipoOrientacao.equals(OrientacaoAcademica.ORIENTADOR)) {
			tipo = "Orientador";
		} else if ( tipoOrientacao != null && tipoOrientacao.equals(OrientacaoAcademica.CoORIENTADOR)) {
			tipo = "Co-Orientador";
		} else if ( tipoOrientacao != null && tipoOrientacao.equals(OrientacaoAcademica.ACADEMICO)) {
			tipo = "Acadêmica";
		}
		return tipo;

	}

	/**
	 * Indica se a orientação está ativa
	 * @return
	 */
	@Transient
	public boolean isAtivo() {
		
		Date hj = new Date();
		
		if (fim == null || fim.after(hj))
			return true;
		else
			return false;
	}

	public boolean isOrientador(){
		return tipoOrientacao.equals( ORIENTADOR );
	}

	public boolean isCoOrientador(){
		return tipoOrientacao.equals( CoORIENTADOR );
	}

	public boolean isAcademico(){
		return tipoOrientacao.equals( ACADEMICO );
	}

	public boolean hasOrientadorDefinido(){
		if( !isEmpty(servidor) || !isEmpty( docenteExterno ) )
			return true;
		return false;
	}

	public String getDescricaoOrientador(){
		if( servidor != null )
			return servidor.toString();
		else if( docenteExterno != null )
			return docenteExterno.toString();
		return null;
	}

	public String getNomeOrientador(){
		if( servidor != null )
			return servidor.getNome();
		else if( docenteExterno != null )
			return docenteExterno.getNome();
		return null;
	}
	
	public Pessoa getPessoa(){
		if( servidor != null )
			return servidor.getPessoa();
		else if( docenteExterno != null )
			return docenteExterno.getPessoa();
		return null;
	}

	public String getMatriculaOrientador(){
		if( servidor != null )
			return String.valueOf( servidor.getSiape() );
		else if( docenteExterno != null )
			return docenteExterno.getMatricula();
		return null;
	}

	public boolean isExterno(){
		return !isEmpty( docenteExterno );
	}

	public int getIdDocente(){
		if( servidor != null )
			return servidor.getId();
		else if( docenteExterno != null )
			return docenteExterno.getId();
		return 0;
	}

	/**
	 * anula o servidor ou docente externo caso o id seja 0
	 */
	public void ajustar(){
		if( servidor != null && servidor.getId() == 0 )
			servidor = null;
		if( docenteExterno!= null && docenteExterno.getId() == 0 )
			docenteExterno = null;
	}

	/**
	 * Verifica se a orientação passada por parâmetro é igual a atual.
	 */
	public int compareTo(OrientacaoAcademica other) {
		int result = 0;
		result = getNomeOrientador().compareTo(other.getNomeOrientador());

		if( result == 0 )
			result = discente.getNome().compareTo( other.getDiscente().getNome() );

		return result;
	}

	@Transient
	public int getQtdMeses(){
		return CalendarUtils.calculoMeses(inicio, fim);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}
	
	public Date getDataFinalizacao() {
		return dataFinalizacao;
	}

	public void setDataFinalizacao(Date dataFinalizacao) {
		this.dataFinalizacao = dataFinalizacao;
	}

	public RegistroEntrada getRegistroFinalizacao() {
		return registroFinalizacao;
	}

	public void setRegistroFinalizacao(RegistroEntrada registroFinalizacao) {
		this.registroFinalizacao = registroFinalizacao;
	}

	public boolean isCancelado() {
		return cancelado;
	}

	public void setCancelado(boolean cancelado) {
		this.cancelado = cancelado;
	}

	public DocenteExterno getDocenteExterno() {
		return docenteExterno;
	}

	public void setDocenteExterno(DocenteExterno docenteExterno) {
		this.docenteExterno = docenteExterno;
	}
	
	public Character getTipoOrientacao() {
		return tipoOrientacao;
	}
	
	public void setTipoOrientacao(Character tipoOrientacao) {
		this.tipoOrientacao = tipoOrientacao;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	@Transient
	public String getItemView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Transient
	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("discente.pessoa.nome", "discenteNome");
		itens.put("discente.curso.nome", "cursoNome");
		itens.put("inicio", null);
		itens.put("fim", null);
		return itens;
	}

	@Transient
	public void setDiscenteNome( String nome ) {
		if ( this.getDiscente() == null ) {
			this.setDiscente( new Discente() );
		}
		this.getDiscente().getPessoa().setNome(nome);
	}
	
	@Transient
	public void setCursoNome( String nome ) {
		if ( this.getDiscente() == null ) {
			this.setDiscente( new Discente() );
		}
		this.getDiscente().getCurso().setNome(nome);
	}

	@Transient
	public float getQtdBase() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Transient
	public String getTituloView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Transient
	public String getTipoBolsa() {
		return tipoBolsa;
	}

	@Transient
	public void setTipoBolsa(String tipoBolsa) {
		this.tipoBolsa = tipoBolsa;
	}
	

}
