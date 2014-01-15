/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 23/04/2013
 * 
 */
package br.ufrn.sigaa.biblioteca.informacao_referencia.dominio;

import javax.persistence.CascadeType;
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

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * <p>Solicita��o de Material de outra Biblioteca � uma solicita��o que o usu�rio da biblioteca pode fazer se existir material dispon�vel em 
 * outra biblioteca. Ap�s essa solicita��o o bibliotec�rio vai enviar o material para a biblioteca solicitada 
 * (mas o material continua pertencendo a biblioteca origem, n�o h� mudan�as na biblioteca do material, nem no patrim�nio). 
 * Ao chegar o material pode ser emprestado para o usu�rio na biblioteca destino </p>
 *
 * <p>A implica��o maior desse caso de uso vai ser momento do empr�stimo  ou devolu��o. 
 * ( o empr�stimo/devolu��o vai passar a ser feito se o material for da biblioteca ou ele estiver transferido temporariamente para ela). E vai deve 
 * ser negado na biblioteca origem do mateiral a partir do momento que existir uma transfer�ncia virgente. </p>
 *
 * @author jadson - jadson@info.ufrn.br
 * @vesion 1.0 - cria��o da classe.
 * @since 23/04/2013
 *
 */
@Entity
@Table(name = "solicitacao_material_outra_biblioteca", schema = "biblioteca")
public class SolicitacaoMaterialOutraBiblioteca {
	
	
	/** O id */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.solicitacoes_usuario_sequence") })
	@Column(name = "id_solicitacao_material_outra_biblioteca", nullable = false)
	private int id;
	
	
	/** O material que foi feita a solicita�ao. */
	@ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE, CascadeType.PERSIST}, fetch=FetchType.LAZY)
	@JoinColumn(name = "id_material", referencedColumnName = "id_material_informacional", nullable = false)
	private MaterialInformacional material;
	
	
	/** � sempre a biblioteca do material. S� est� repetido para melhorar a visualiza��o e consultas */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_biblioteca_origem", referencedColumnName = "id_biblioteca", nullable = false)
	protected Biblioteca bibliotecaOrigem;
	
	
	/** A biblioteca para onde a solicita��o foi feita.  */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_biblioteca_destino", referencedColumnName = "id_biblioteca", nullable = false)
	protected Biblioteca bibliotecaDestino;
	
	
	/**  
	 * Informa��o do usu�rio que solicitou o material de outra biblioteca.
	 */
	@ManyToOne(cascade  =  {}, fetch=FetchType.LAZY)
	@JoinColumn(name = "id_usuario_biblioteca", referencedColumnName ="id_usuario_biblioteca", nullable=false)
	private UsuarioBiblioteca usuarioSolicitante;
	
	
	/**  <p>Caso a solicita��o precise ser cancelada.</p> 
	 *   
	 *   <p>Caso a solicita��o seja cancelada � como se o material nunca tivesse sa�da da biblioteca origem.</p>
	 *    
	 *   <p>Isso deve ser usado apenas em caso de erros, e deve ser feito mediantes justificativa 
	 * para ficar registrado e sabemos que o material realmente est� presente na biblioteca origem dele.  </p> 
	 */
	@Column (name="ativo", nullable=false)
	protected boolean ativo = true;
	
	
	////////// Em caso de cancelamento ////////////////
	
	/** O bibliotec�rio deve informar uma justificativa caso a solicita��o deve ser cancelada no meio do caminho. */
	@Column (name="motivo_cancelamento", nullable=true)
	private String motivoCancelamento;
	
	/**
	* Guarda o usu�rio que enviou cancelou a solicita��o.
	*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_cancelamento", referencedColumnName ="id_usuario", nullable=true)
	private Usuario usuarioCancelamento;
	
	/**
	 * Registro de entrada  do usu�rio que realizou a �ltima atualiza��o. No caso de cancelamento, vai guardar o registro de entrada de quem cancelou.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_ultima_atualizacao", nullable=true)
	@AtualizadoPor
	protected RegistroEntrada registroUltimaAtualizacao;
	
}
