/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 08/10/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.dominio;

import java.util.Date;

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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.dominio.Unidade;

/**
 *
 * <p>Classe que representa as notas que podem ser enviadas ao setor de circulação sobre algum material informacional</p>
 * <p>As notas vão ter a informação de quando serão mostras ao usuário (no empréstimo, renovação ou devolução ) e se elas bloqueiam 
 * a realização do empréstimo ou renovação.  Esse caso de uso vai engloblar em um só as operações de mostrar uma nota ao usuário no 
 * momento do empréstimo, e a operação de bloquear um material informacional que já existe em circulação.</p>
 * 
 * <p><strong> Observação 1 : </strong>  Uma nota vai permanecer ativa enquanto uma das variáveis bloquearMaterial, mostrarEmprestimo, mostrarRenovacao, mostrarDevolucao
 * estiver com valor verdadeiro.  </p>
 * 
 * <p><strong> Observação 2 : </strong> Nos caso de apenas mostrar uma nota, a nota será mostrada apenas 1 vez, o primeiro caso de ocorrer, desativa os demais.  </p>
 * 
 * <p><strong> Observação 3 : </strong> Nos caso de bloqueio do material, o material não poderá ser bloqueado no último dia da sua renovação, para dar tempo ao usuário de devolver o material, caso o material for bloqueado e 
 * estiver empréstado, será enviado um email informativo ao usuário que estiver com o material.  </p>
 * 
 * <p> <i> Essas notas podem ser criadas pelos próprios usuário de circulação ou pelos usuário de catalogação. </i> </p>
 * 
 * @author jadson
 *
 */
@Entity
@Table(name = "nota_circulacao", schema = "biblioteca")
public class NotaCirculacao implements Validatable{

	/**
	 * O id
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.resgistro_extras_sequence") })
	@Column(name = "id_nota_circulacao")
	private int id;
	
	
	/**
	 * A informação que é mostrada ao operador de circulação.
	 */
	private String nota;
	
	
	/** O material a quem essa nota pertence */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_material_informacional", referencedColumnName = "id_material_informacional", nullable = true)	
	private MaterialInformacional material;
	
	/**
	 * <p> Se a nota vai bloquear o emprétimo ou renovação material. </p>
	 * <p> 
	 * 		<ul> 
	 * 			<li> Caso seja bloqueado, o material vai permanecer bloqueado indefinidamente, até que algum o desbloquei </li> 
	 * 			<li> Caso não seja bloqueado, a mensagem vai ser mostrada no próximo empréstimo, renovação ou devolução apenas uma vez e será inativada </li> 
	 *      </ul> 
	 * 
	 * </p>
	 */
	@Column (name="bloquear_material", nullable=false)
	private boolean bloquearMaterial = true;
	
	/**
	 * Se a nota vai ser mostrada no empréstimo próximo empréstimo do material
	 */
	@Column (name="mostrar_emprestimo", nullable=false)
	private boolean mostrarEmprestimo = false;
	
	/**
	 * Se a nota vai ser mostrada na próxima renovação do empréstimo do material
	 */
	@Column (name="mostrar_renovacao", nullable=false)
	private boolean mostrarRenovacao = false;
	
	/**
	 * Se a nota vai ser mostrada na próximo devolução do empréstimo.
	 */
	@Column (name="mostrar_devolucao", nullable=false)
	private boolean mostrarDevolucao = false;
	
	
	//////////////// Dados de auditoria  ////////////////////
	/**
	 * Registro entrada do usuário que cadastrou a nota
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;
	
	/**
	 * Data de cadastro da nota
	 */
	@CriadoEm
	@Column(name="data_cadastro")
	private Date dataCadastro;
	
	/**
	 * Registro de entrada do usuário que inativou a nota
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_inativacao")
	@AtualizadoPor
	private RegistroEntrada registroInativacao;

	/**
	 * Data da inativacao da nota
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_inativacao")
	@AtualizadoEm
	private Date dataInativacao;
	
	////////////////////////////////////////////////
	
	public NotaCirculacao() {
	}
	
	
	/**
	 * 
	 * Construtor de uma nota recebendo as informações do material a quem ela pertence. Utilizando para montar as informações 
	 * necessárias de uma nota depois de uma consulta no banco.
	 * 
	 * @param idMaterial
	 * @param codigoBarrasMaterial
	 * @param idBiblioteca
	 * @param descricaoBiblioteca
	 * @param nota
	 * @param bloquearMaterial
	 * @param mostrarEmprestimo
	 * @param mostrarRenovacao
	 * @param mostrarDevolucao
	 * @param exemplar
	 */
	public NotaCirculacao(int idMaterial, String codigoBarrasMaterial, boolean materialAtivo, int idBiblioteca, String descricaoBiblioteca
			, int idUnidade
			, int idSituacao, boolean situacaoDisponivel, boolean situacaoEmprestado, boolean situacaoDeBaixa
			, int idNota, String nota, boolean bloquearMaterial, boolean mostrarEmprestimo, boolean mostrarRenovacao, boolean mostrarDevolucao
			, boolean exemplar) {
	
		this(idNota, nota, bloquearMaterial, mostrarEmprestimo, mostrarRenovacao, mostrarDevolucao);
		
		if(exemplar)
			this.material = new Exemplar(idMaterial);
		else
			this.material = new Fasciculo(idMaterial);
			
		
		material.setCodigoBarras(codigoBarrasMaterial);
		material.setAtivo(materialAtivo);
		material.setBiblioteca( new Biblioteca(idBiblioteca, descricaoBiblioteca)) ;
		material.getBiblioteca().setUnidade(new Unidade(idUnidade));
		
		material.setSituacao( new SituacaoMaterialInformacional(idSituacao, "", situacaoDisponivel, situacaoEmprestado, situacaoDeBaixa));
	}
	
	/**
	 * 
	 * Construtor de uma nota de circulação para ser persistida no banco
	 * 
	 * @param codigoBarrasMaterial
	 * @param idBiblioteca
	 * @param descricaoBiblioteca
	 * @param nota
	 * @param bloquearMaterial
	 * @param mostrarEmprestimo
	 * @param mostrarRenovacao
	 * @param mostrarDevolucao
	 * @param exemplar
	 */
	public NotaCirculacao(MaterialInformacional material, String nota, boolean bloquearMaterial, boolean mostrarEmprestimo, boolean mostrarRenovacao, boolean mostrarDevolucao ) {
		this(0, nota, bloquearMaterial, mostrarEmprestimo, mostrarRenovacao, mostrarDevolucao);
		this.material = material;
	}
	
	/**
	 * 
	 * Construtor que recupera apenas a nota de circulação, sem a informação do material
	 * 
	 * @param codigoBarrasMaterial
	 * @param idBiblioteca
	 * @param descricaoBiblioteca
	 * @param nota
	 * @param bloquearMaterial
	 * @param mostrarEmprestimo
	 * @param mostrarRenovacao
	 * @param mostrarDevolucao
	 * @param exemplar
	 */
	public NotaCirculacao(int id, String nota, boolean bloquearMaterial, boolean mostrarEmprestimo, boolean mostrarRenovacao, boolean mostrarDevolucao ) {
		this.id = id;
		this.nota = nota;
		this.bloquearMaterial = bloquearMaterial;
		this.mostrarEmprestimo = mostrarEmprestimo;
		this.mostrarRenovacao = mostrarRenovacao;
		this.mostrarDevolucao = mostrarDevolucao;
	}
	
	
	
	/**
	 * Verifica se a nota está inativa
	 *
	 * @return
	 */
	public boolean isNotaInativa(){
		if(  !bloquearMaterial &&  !mostrarEmprestimo &&  !mostrarRenovacao &&  !mostrarDevolucao)
			return true;
		else
			return false;
	}
	
	
	


	/**
	 * Valida os dados de uma nota para inserção no banco.
	 * 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		ListaMensagens mensagens = new ListaMensagens();

		if(StringUtils.isEmpty(nota))
			mensagens.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "nota");
		else{
			if(nota.length() > 200)
				mensagens.addMensagem(MensagensArquitetura.MAXIMO_CARACTERES, "Nota", "200");
		}
			
			
		if(  isNotaInativa() )
			mensagens.addErro("Informe pelo menos uma situação em que a nota será mostrada em circulação");
		
		return mensagens;
	}

	
	//////// sets e gets //////////
	
	public int getId() {
		return id;
	}

	
	public void setId(int id) {
		this.id = id;
	}


	public String getNota() {
		return nota;
	}


	public void setNota(String nota) {
		this.nota = nota;
	}


	public MaterialInformacional getMaterial() {
		return material;
	}


	public void setMaterial(MaterialInformacional material) {
		this.material = material;
	}


	public boolean isBloquearMaterial() {
		return bloquearMaterial;
	}


	public void setBloquearMaterial(boolean bloquearMaterial) {
		this.bloquearMaterial = bloquearMaterial;
	}


	public boolean isMostrarEmprestimo() {
		return mostrarEmprestimo;
	}


	public void setMostrarEmprestimo(boolean mostrarEmprestimo) {
		this.mostrarEmprestimo = mostrarEmprestimo;
	}


	public boolean isMostrarRenovacao() {
		return mostrarRenovacao;
	}


	public void setMostrarRenovacao(boolean mostrarRenovacao) {
		this.mostrarRenovacao = mostrarRenovacao;
	}


	public boolean isMostrarDevolucao() {
		return mostrarDevolucao;
	}


	public void setMostrarDevolucao(boolean mostrarDevolucao) {
		this.mostrarDevolucao = mostrarDevolucao;
	}


	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}


	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}


	public Date getDataCadastro() {
		return dataCadastro;
	}


	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}


	public RegistroEntrada getRegistroInativacao() {
		return registroInativacao;
	}


	public void setRegistroInativacao(RegistroEntrada registroInativacao) {
		this.registroInativacao = registroInativacao;
	}


	public Date getDataInativacao() {
		return dataInativacao;
	}


	public void setDataInativacao(Date dataInativacao) {
		this.dataInativacao = dataInativacao;
	}

	
	
}
