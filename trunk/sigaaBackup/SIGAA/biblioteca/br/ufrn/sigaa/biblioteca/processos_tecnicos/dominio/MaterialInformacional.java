/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 09/06/2009
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Colecao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SituacaoMaterialInformacional;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.StatusMaterialInformacional;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoMaterial;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;

/**
 *
 *   Material informacional representa os materiais que podem ser emprestados no sistema.
 *   Uma abstra��o para facilitar consultas dos empr�stimos.
 *
 * @author Jadson
 * @since 01/04/2009
 * @version 1.0 cria��o da classe
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="material_informacional", schema="biblioteca")
public abstract class MaterialInformacional implements PersistDB {


	/** 
	 * UM EXEMPLAR E UM FASC�CULO DEVEM POSSUIR IDS DIFERENTES PARA OS EMPR�STIMOS,
	 * OU SEJA, SEREM GERADOS PELA MESMA SEQU�NCIA. ESSA CLASSE GARANTE ISSO.
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.material_informacional_sequence") })
    @Column(name = "id_material_informacional")
	protected int id;


	/**
	 * Guarda o c�digo de barras. � o �nico dado que realmente � �nico tanto para fasc�culos como para exemplar e anexos.
	 * 
	 * Para os livros mais novos esse n�mero vai ser sempre igual ao n�mero do patrim�nio
	 * Exceto para anexo que vai ser o numeroPatrimonio mais uma letra. 2000000001a, 2000000001b
	 * Para os c�digos de barras do fasc�culo, vai ser o n�mero de patrim�nio da assinatura mais um n�mero gerado pelo sistema */
	@Column(name = "codigo_barras", nullable=false)
	protected String codigoBarras;
	
	
	/**
	 *     N�mero que vai ser usado para gerar as letras dos c�digos de barras dos anexos de
	 * exemplares e fasc�culos ( no caso de fasc�culos s�o chamados suplementos )
	 * BibliotecaUtil.geraCaraterCorespondente() */
	@Column(name = "numero_gerador_codigo_barras_anexos", nullable=false)
	protected int numeroGeradorCodigoBarrasAnexos;
	
	
	/** Todo exemplar deve possuir uma localiza��o que informa onde ele fica fisicamente
	 * Os dados desse campo s�o usados para gerar a etiqueta de lombada.
	 * <b>IMPORTANTE</b> Os dados da localiza��o s�o separados por espa�o. exemplo: "371.2 C376o DISSERT"
	 * <b>IMPORTANTE 2</b> Esses dados vem do campo 090$a + 090$b + 090$c + 090$d + [algo opcional]
	 */
	@Column(name = "numero_chamada")
	protected String numeroChamada;
	
	
	/**
	 * Alguns materiais possuem uma 2� localiza��o, por exemplo mapas para indicar a gaveta onde
	 * est�o localizados. Essa informa��o vem aqui.
	 */
	@Column(name = "segunda_localizacao", length=60)
	protected String segundaLocalizacao;
	
	
	/** Guarda informa��es gerais que um material pode ter. ENCARDENADO, etc.. */
	@Column(name = "nota_geral", nullable=true)
	protected String notaGeral;
	
	
	/** Mais um campo de nota, serve tanto para fasc�culos quanto para exemplares.      *
	 * Essa nota dever� conter as informa��es que ser�o mostradas ao usu�rio           */
	@Column(name = "nota_usuario", nullable=true)
	protected String notaUsuario;
	
	
	
	
	///////////////////////////// INFORMA��ES PARA EMPR�STIMO ////////////////////////////////


	/** Informa onde o item est� dentro do conjunto do bibliotecas da Institui��o */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_biblioteca", referencedColumnName = "id_biblioteca")
	protected Biblioteca biblioteca;


	/** Informa a cole��o que o item pertence */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_colecao", referencedColumnName = "id_colecao")
	protected Colecao colecao;


	/** Informa a situa��o do item: se est� dispon�vel, emprestado, danificado, etc..*/
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_situacao_material_informacional", referencedColumnName = "id_situacao_material_informacional")
	protected SituacaoMaterialInformacional situacao;


	/** Informa o status do item: especial, n�o circula, regular, etc..*/
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_status_material_informacional", referencedColumnName = "id_status_material_informacional")
	protected StatusMaterialInformacional status;
	
	
	/**  Guarda o tipo do material do item Livros, CD-ROM, Disquete, Vinil, Mapas
	 *  alguns tipos de materiais possuem um limitador nos prazos dos empr�stimos, exemplo
	 *  monografia e disserta��es tem um limite de 24h   */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_tipo_material", referencedColumnName = "id_tipo_material")
	protected TipoMaterial tipoMaterial;
	
	
	/**
	 *  Guarda as formas de documento que o material 
	 *  est� dispon�vel atualmente.
	 */
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="biblioteca.material_informacional_formato_documento", 
			joinColumns={@JoinColumn(name="id_material_informacional")}, 
			inverseJoinColumns={@JoinColumn(name="id_forma_documento")})
	private Collection<FormaDocumento> formasDocumento;
	
	
	/** Guarda o motivo pelo qual foi dado baixa no material do acervo. Para materiais n�o tombados
	 * a baixa � direta pela biblioteca no SIGAA, apenas alterando a Situa��o.
	 * Para materiais tombados a baixa tem que ser feita pelo SIPAC. */
	@Column (name="motivo_baixa")
	private String motivoBaixa;
	
	
	/**
	 * <p>Guarda as informa��es do T�tulo (N�mero do Sistema + Autor + T�tulo) dos materiais baixados </p>
	 *
	 * <p>Utilizado para permitir remover um T�tulo (A pagar o cache), quando ele s� possuir materiais baixados
	 * e continuar com essas informa��es para serem mostradas no relat�rio de materiais baixados.</p>
	 */
	@Column (name="informacoes_titulo_material_baixado")
	private String informacoesTituloMaterialBaixado;
	
	
	/**
	 *  <p>Caso um material seja removido do sistema ele ser� desativado.</p>
	 *  <p> Nesse caso o material n�o aparecer� em nenhum lugar nas pesquisas do sistema, nem
	 *  no relat�rio de materiais baixados.<br/>
	 *  <i> ( Esta situa��o � uma exce��o no sistema, s� ocorre por causa de algum erro de um
	 *  bibliotec�rio na inclus�o do material, diferente de quando o material fica na situa��o
	 *  de baixado, esta geralmente ocorre porque est� danificado demais e n�o pode fazer mais
	 *  parte do acervo ).</i></p>
	 */
	@Column (name="ativo")
	protected boolean ativo;
	
	
	/** Refer�ncia para o material que esse material est� substitu�ndo no acervo.
	 *  Caso esse material tenha sido inclu�do no acervo com o objetivo de substituir outro, usado muito 
	 *  quando o usu�rio perde um material.
	 *  */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_material_que_eu_substituo", referencedColumnName="id_material_informacional")
	private MaterialInformacional  materialQueEuSubstituo;
	
	
	
	//////////////////////////// INFORMA��ES DE AUDITORIA  /////////////////////////////////////

	/**
	 * Registro de entrada  do usu�rio que criaou o material
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_criacao")
	@CriadoPor
	protected RegistroEntrada registroCriacao;

	/**
	 * Data de cadastro
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@CriadoEm
	@Column(name="data_criacao")
	protected Date dataCriacao;

	/**
	 * Registro de entrada  do usu�rio que realizou a �ltima atualiza��o
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_ultima_atualizacao")
	@AtualizadoPor
	protected RegistroEntrada registroUltimaAtualizacao;

	/**
	 * Data da �ltima atualiza��o
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_ultima_atualizacao")
	@AtualizadoEm
	protected Date dataUltimaAtualizacao;

	//////////////////////////////////////////////////////////////////////////////////////////

	
	/** Guarda uma refer�ncia ao material na base antiga. */
	private String codmerg;
	
	
	/** registro do ALEPH, utilizado na migra��o, n�o utilizado no sistema*/
	@Transient
	private String recKey;
	

	/**
	 * Guarda se esse objeto foi selecionado ou n�o na p�gina de gera��o de etiquetas para materiais.
	 */
	@Transient
	protected boolean selecionado;
	
	/**
	 * Usado nas p�ginas do sistema quando a sele��o dos materiais tem mais de dois valores, n�o � apenas sim/n�o
	 */
	@Transient
	protected Integer opcaoSelecao;
	
	
	/**
	 * Auxilia na exibi��o do prazo de empr�stimo do material
	 */
	@Transient
	protected Date prazoEmprestimo;
	
	
	/**
	 *   Guarda temporariamente o nome do usu�rio que criou o material, usado nas p�ginas do sistema.
	 */
	@Transient
	protected String nomeUsuario;
	
	
	/**
	 *   Guarda temporariamente se o material deve ser mostrado na p�gina desabilitado ou n�o, por
	 *   algum motivo espec�fico do caso que est� sendo usado.
	 */
	@Transient
	protected boolean desabilitado;
	
	
	/**
	 *   Guarda temporariamente se o material pode ser renovado
	 */
	@Transient
	protected boolean podeRenovar;
	
	/**
	 *   Guarda temporariamente se � o �ltimo dia da renova��o do empr�stimo do material
	 */
	@Transient
	protected boolean ultimoDiaRenovacao;
	
	
	/**  guarda temporariamente alguma informa��o sobre o material          */
	@Transient
	protected String informacao;
	
	
	/**  guarda temporariamente alguma informa��o sobre o material          */
	@Transient
	protected String informacao2;
	
	
	/**  Guarda temporariamente o prazo que o usu�rio tem para concluir a reserva do material. Durante essa prazo o material vai est� 
	 * dispon�vel agurdando a conclus�o da reserva pelo usu�rio. Assim deve mostra essa informa��o para n�o confundir o usu�rio em achar
	 * que ele pode realizar o empr�stimos do material.*/
	@Transient
	protected Date prazoConcluirReserva;
	
	
	/**
	 * Retorna o t�tulo desse material, seja ele exemplar ou fasc�culo.
	 */
	public TituloCatalografico getTituloCatalografico (){
		TituloCatalografico t = null;
		if (this instanceof Exemplar)
			t = ((Exemplar) this).getTituloCatalografico();
		else if (this instanceof Fasciculo)
			t = ((Fasciculo) this).getAssinatura().getTituloCatalografico();

		return t;
	}
	
	/**
	 * Seta o t�tulo catalogr�fico desse material, seja ele exemplar ou fasc�culo
	 */
	public void setTituloCatalografico (TituloCatalografico t){
		if (this instanceof Exemplar)
			((Exemplar) this).setTituloCatalografico(t);
		else if (this instanceof Fasciculo)
			((Fasciculo) this).getAssinatura().setTituloCatalografico(t);
	}


	
	/**
	 * Diz quando um material est� emprestado.
	 */
	public boolean isEmprestado(){
		return this.situacao.isSituacaoEmprestado();
	}
	
	
	/**
	 * Diz quando um material est� dispon�vel.
	 */
	public boolean isDisponivel(){
		return this.situacao.isSituacaoDisponivel();
	}
	
	
	/**
	 * N�o deve aparecer nas pesquisas
	 */
	public boolean isDadoBaixa() {
		return this.situacao.isSituacaoDeBaixa();
	}
	
	
	/**
	 *    Usado para salvar TODAS as informa��es de um material no hist�rico.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		StringBuilder infoMaterial = new StringBuilder(
				" <strong>C�digo de Barras:</strong> "+this.getCodigoBarras()+" \n"
			   +" <strong>Situa��o:</strong> "+ (this.getSituacao() != null ? this.getSituacao().getDescricao()+" \n" : "")
		       +" <strong>Status:</strong> "+  (this.getStatus().getDescricao() != null ? this.getStatus().getDescricao()+" \n" : "")
		       +" <strong>N�mero Chamada:</strong> "+  (this.getNumeroChamada() != null ? this.getNumeroChamada()+" \n": "" )
		       +" <strong>Segunda Localiza��o:</strong> "+  (this.getSegundaLocalizacao() != null ? this.getSegundaLocalizacao()+" \n": "" )
		       +" <strong>Biblioteca:</strong> "+  (this.getBiblioteca().getDescricao() != null ? this.getBiblioteca().getDescricao()+" \n" : "" )
		       +" <strong>Cole��o:</strong> "+  (this.getColecao().getDescricao() != null ? this.getColecao().getDescricao()+" \n": "" )
		       +" <strong>Tipo Material:</strong> "+  (this.getTipoMaterial().getDescricao() != null ? this.getTipoMaterial().getDescricao()+" \n": "")  
		 	   +" <strong>Formas de Documento:</strong> "+  (this.getTipoMaterial().getDescricao() != null ? this.getTipoMaterial().getDescricao()+" \n": "") ) ;
		
		//Monta a string contendo todas formas de documento associadas ao material
		if( getFormasDocumento() != null ){ 
			
			StringBuilder infoFormasDocumento = new StringBuilder(" <strong>Formas de Documento:</strong> ");
			for (FormaDocumento f : getFormasDocumento()) {
				infoFormasDocumento.append(", ");
				infoFormasDocumento.append(f.getDenominacao());
			}
			infoMaterial.append(infoFormasDocumento.delete(0, 1));
			
		}
		
		if(this instanceof Exemplar){
			
			Exemplar temp = (Exemplar) this;
			
			if(temp.getNumeroVolume() != null)
				infoMaterial.append(" <strong>N�mero do Volume:</strong> "+temp.getNumeroVolume()+" \n");
			
			
			if(StringUtils.notEmpty(temp.getNotaConteudo()))
				infoMaterial.append(" <strong>Nota de Conte�do:</strong> "+temp.getNotaConteudo()+" \n");
			
			if(StringUtils.notEmpty(temp.getNotaTeseDissertacao()))
				infoMaterial.append(" <strong>Nota de Tese e Disserta��o:</strong> "+temp.getNotaTeseDissertacao()+" \n");
			
		}
		
		if(this instanceof Fasciculo){
			
			Fasciculo temp = (Fasciculo) this;
			
			if(temp.getAnoCronologico() != null)
				infoMaterial.append(" <strong>Ano Cronol�gico:</strong> "+temp.getAnoCronologico()+" \n");
			
			if(temp.getAno() != null)
			infoMaterial.append(" <strong>Ano:</strong> "+temp.getAno()+" \n");
			
			if(temp.getVolume() != null)
				infoMaterial.append(" <strong>Volume:</strong> "+temp.getVolume()+" \n");
			
			if(temp.getNumero() != null)
				infoMaterial.append(" <strong>N�mero:</strong> "+temp.getNumero()+" \n");
			
			if(temp.getEdicao() != null)
				infoMaterial.append(" <strong>Edi��o:</strong> "+temp.getEdicao()+" \n");
			
			if(StringUtils.notEmpty(temp.getDescricaoSuplemento()))
			infoMaterial.append(" <strong>Descri��o do Suplemento:</strong> "+temp.getDescricaoSuplemento()+" \n");
		}
		
		
		if(StringUtils.notEmpty(this.getNotaGeral()))
			infoMaterial.append(" <strong>Nota Geral:</strong> "+this.getNotaGeral()+" \n");
		
		if(StringUtils.notEmpty(this.getNotaUsuario()))
			infoMaterial.append(" <strong>Nota ao Usu�rio:</strong> "+this.getNotaUsuario()+" \n");
		
		return infoMaterial.toString();
	}
	
	/**
	 * Popula a lista de formas de documento que o material possui.
	 * Utilizado no formul�rio de inclus�o dos materiais.
	 * @param idsFormaDocumento
	 */
	public void popularFormasDocumento(Collection<String> idsFormaDocumento){
		setFormasDocumento(new ArrayList<FormaDocumento>());
		if( !isEmpty(idsFormaDocumento) ){
			for (String idFormaDocumento : idsFormaDocumento){
				try{
					getFormasDocumento().add(new FormaDocumento(Integer.valueOf(idFormaDocumento)));
				}catch(NumberFormatException nfe){
					continue;
				}
			}
		}
	}
	
	/**
	 * Retorna uma lista dos id's das formas de documento que o material possui.
	 * Utilizado no formul�rio de edi��o dos materiais.
	 */
	public Collection<String> extrairFormasDocumento(){
		
		if( isEmpty(getFormasDocumento()) )
			return new ArrayList<String>();
		
		Collection<String> idsFormasDocumento = new ArrayList<String>();
		
		for (FormaDocumento formaDocumento : getFormasDocumento())
				idsFormasDocumento.add( String.valueOf(formaDocumento.getId()) );
		
		return idsFormasDocumento; 
		
	}
	
	/**
	 * Retorna uma string contendo todos as formas de documento do material 
	 * separadas por v�rgula.
	 * Utilizado na visualiza��o dos materiais.
	 */
	public String getDescricaoFormasDocumento(){
		
		if( isEmpty(getFormasDocumento()) )
			return null;
		
		StringBuilder strDescricaoFD = new StringBuilder();
		for (FormaDocumento formaDocumento : getFormasDocumento()) {
			strDescricaoFD.append(", ");
			strDescricaoFD.append(formaDocumento.getDenominacao() );
		}
		strDescricaoFD.delete(0, 2);
		
		return strDescricaoFD.toString(); 
		
	}
	
	// sets e gets
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}
	
	public String getCodigoBarras() {
		return codigoBarras;
	}

	public void setCodigoBarras(String codigoBarras) {
		this.codigoBarras = codigoBarras;
	}

	public Biblioteca getBiblioteca() {
		return biblioteca;
	}

	public void setBiblioteca(Biblioteca biblioteca) {
		this.biblioteca = biblioteca;
	}

	public Colecao getColecao() {
		return colecao;
	}

	public void setColecao(Colecao colecao) {
		this.colecao = colecao;
	}

	public SituacaoMaterialInformacional getSituacao() {
		return situacao;
	}

	public void setSituacao(SituacaoMaterialInformacional situacao) {
		this.situacao = situacao;
	}

	public StatusMaterialInformacional getStatus() {
		return status;
	}

	public void setStatus(StatusMaterialInformacional status) {
		this.status = status;
	}

	public TipoMaterial getTipoMaterial() {
		return tipoMaterial;
	}

	public void setTipoMaterial(TipoMaterial tipoMaterial) {
		this.tipoMaterial = tipoMaterial;
	}
	
	public Collection<FormaDocumento> getFormasDocumento() {
		return formasDocumento;
	}

	public void setFormasDocumento(Collection<FormaDocumento> formasDocumento) {
		this.formasDocumento = formasDocumento;
	}

	/**
	 * Adiciona uma nova forma do documento ao material
	 *
	 * @param formaDocumento
	 */
	public void adicionaFormasDocumento(FormaDocumento formaDocumento) {
		if( this.formasDocumento == null)
			this.formasDocumento = new ArrayList<FormaDocumento>();
		formasDocumento.add(formaDocumento);
	}
	
	public RegistroEntrada getRegistroCriacao() {
		return registroCriacao;
	}

	public void setRegistroCriacao(RegistroEntrada registroCriacao) {
		this.registroCriacao = registroCriacao;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public RegistroEntrada getRegistroUltimaAtualizacao() {
		return registroUltimaAtualizacao;
	}

	public void setRegistroUltimaAtualizacao(
			RegistroEntrada registroUltimaAtualizacao) {
		this.registroUltimaAtualizacao = registroUltimaAtualizacao;
	}

	public Date getDataUltimaAtualizacao() {
		return dataUltimaAtualizacao;
	}

	public void setDataUltimaAtualizacao(Date dataUltimaAtualizacao) {
		this.dataUltimaAtualizacao = dataUltimaAtualizacao;
	}
	
	
	public int getNumeroGeradorCodigoBarrasAnexos() {
		return numeroGeradorCodigoBarrasAnexos;
	}

	/**
	 * Verifica se o item pode ser emprestado. S� pode se ele estiver dispon�vel.
	 */
	public boolean podeSerEmprestado(){
		return this.situacao.isSituacaoDisponivel() && status.isPermiteEmprestimo();
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(new Integer(id));
	}
	

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}
	
	public boolean isExemplar (){
		return this instanceof Exemplar;
	}

	public String getNumeroChamada() {
		return numeroChamada;
	}
	
	public void setNumeroChamada(String numeroChamada) {
		this.numeroChamada = numeroChamada;
	}

	public String getCodmerg() {
		return codmerg;
	}

	public void setCodmerg(String codmerg) {
		this.codmerg = codmerg;
	}

	public String getRecKey() {
		return recKey;
	}

	public void setRecKey(String recKey) {
		this.recKey = recKey;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public String getNotaUsuario() {
		return notaUsuario;
	}

	public void setNotaUsuario(String notaUsuario) {
		this.notaUsuario = notaUsuario;
	}

	public String getNotaGeral() {
		return notaGeral;
	}

	public void setNotaGeral(String notaGeral) {
		this.notaGeral = notaGeral;
	}

	public String getMotivoBaixa() {
		return motivoBaixa;
	}

	public void setMotivoBaixa(String motivoBaixa) {
		this.motivoBaixa = motivoBaixa;
	}

	public void setNumeroGeradorCodigoBarrasAnexos(int numeroGeradorCodigoBarrasAnexos) {
		this.numeroGeradorCodigoBarrasAnexos = numeroGeradorCodigoBarrasAnexos;
	}

	public String getNomeUsuario() {
		return nomeUsuario;
	}

	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}

	public String getSegundaLocalizacao() {
		return segundaLocalizacao;
	}

	public void setSegundaLocalizacao(String segundaLocalizacao) {
		this.segundaLocalizacao = segundaLocalizacao;
	}

	public Date getPrazoEmprestimo() {
		return prazoEmprestimo;
	}

	public void setPrazoEmprestimo(Date prazoEmprestimo) {
		this.prazoEmprestimo = prazoEmprestimo;
	}

	public boolean isDesabilitado() {
		return desabilitado;
	}

	public void setDesabilitado(boolean desabilitado) {
		this.desabilitado = desabilitado;
	}

	public String getInformacao() {
		return informacao;
	}

	public void setInformacao(String informacao) {
		this.informacao = informacao;
	}

	public String getInformacao2() {
		return informacao2;
	}

	public void setInformacao2(String informacao2) {
		this.informacao2 = informacao2;
	}

	public Integer getOpcaoSelecao() {
		return opcaoSelecao;
	}

	public void setOpcaoSelecao(Integer opcaoSelecao) {
		this.opcaoSelecao = opcaoSelecao;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public String getInformacoesTituloMaterialBaixado() {
		return informacoesTituloMaterialBaixado;
	}

	public void setInformacoesTituloMaterialBaixado(String informacoesTituloMaterialBaixado) {
		this.informacoesTituloMaterialBaixado = informacoesTituloMaterialBaixado;
	}

	public boolean isPodeRenovar() {
		return podeRenovar;
	}

	public void setPodeRenovar(boolean podeRenovar) {
		this.podeRenovar = podeRenovar;
	}

	public boolean isUltimoDiaRenovacao() {
		return ultimoDiaRenovacao;
	}

	public void setUltimoDiaRenovacao(boolean ultimoDiaRenovacao) {
		this.ultimoDiaRenovacao = ultimoDiaRenovacao;
	}

	public Date getPrazoConcluirReserva() {
		return prazoConcluirReserva;
	}

	public void setPrazoConcluirReserva(Date prazoConcluirReserva) {
		this.prazoConcluirReserva = prazoConcluirReserva;
	}

	public MaterialInformacional getMaterialQueEuSubstituo() {
		return materialQueEuSubstituo;
	}

	public void setMaterialQueEuSubstituo(MaterialInformacional materialQueEuSubstituo) {
		this.materialQueEuSubstituo = materialQueEuSubstituo;
	}
	
	
}
