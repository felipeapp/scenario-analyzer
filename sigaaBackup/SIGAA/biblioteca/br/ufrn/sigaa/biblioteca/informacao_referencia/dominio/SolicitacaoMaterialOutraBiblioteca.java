/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p>Solicitação de Material de outra Biblioteca é uma solicitação que o usuário da biblioteca pode fazer se existir material disponível em 
 * outra biblioteca. Após essa solicitação o bibliotecário vai enviar o material para a biblioteca solicitada 
 * (mas o material continua pertencendo a biblioteca origem, não há mudanças na biblioteca do material, nem no patrimônio). 
 * Ao chegar o material pode ser emprestado para o usuário na biblioteca destino </p>
 *
 * <p>A implicação maior desse caso de uso vai ser momento do empréstimo  ou devolução. 
 * ( o empréstimo/devolução vai passar a ser feito se o material for da biblioteca ou ele estiver transferido temporariamente para ela). E vai deve 
 * ser negado na biblioteca origem do mateiral a partir do momento que existir uma transferência virgente. </p>
 *
 * @author jadson - jadson@info.ufrn.br
 * @vesion 1.0 - criação da classe.
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
	
	
	/** O material que foi feita a solicitaçao. */
	@ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE, CascadeType.PERSIST}, fetch=FetchType.LAZY)
	@JoinColumn(name = "id_material", referencedColumnName = "id_material_informacional", nullable = false)
	private MaterialInformacional material;
	
	
	/** É sempre a biblioteca do material. Só está repetido para melhorar a visualização e consultas */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_biblioteca_origem", referencedColumnName = "id_biblioteca", nullable = false)
	protected Biblioteca bibliotecaOrigem;
	
	
	/** A biblioteca para onde a solicitação foi feita.  */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_biblioteca_destino", referencedColumnName = "id_biblioteca", nullable = false)
	protected Biblioteca bibliotecaDestino;
	
	
	/**  
	 * Informação do usuário que solicitou o material de outra biblioteca.
	 */
	@ManyToOne(cascade  =  {}, fetch=FetchType.LAZY)
	@JoinColumn(name = "id_usuario_biblioteca", referencedColumnName ="id_usuario_biblioteca", nullable=false)
	private UsuarioBiblioteca usuarioSolicitante;
	
	
	/**  <p>Caso a solicitação precise ser cancelada.</p> 
	 *   
	 *   <p>Caso a solicitação seja cancelada é como se o material nunca tivesse saída da biblioteca origem.</p>
	 *    
	 *   <p>Isso deve ser usado apenas em caso de erros, e deve ser feito mediantes justificativa 
	 * para ficar registrado e sabemos que o material realmente está presente na biblioteca origem dele.  </p> 
	 */
	@Column (name="ativo", nullable=false)
	protected boolean ativo = true;
	
	
	////////// Em caso de cancelamento ////////////////
	
	/** O bibliotecário deve informar uma justificativa caso a solicitação deve ser cancelada no meio do caminho. */
	@Column (name="motivo_cancelamento", nullable=true)
	private String motivoCancelamento;
	
	/**
	* Guarda o usuário que enviou cancelou a solicitação.
	*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_cancelamento", referencedColumnName ="id_usuario", nullable=true)
	private Usuario usuarioCancelamento;
	
	/**
	 * Registro de entrada  do usuário que realizou a última atualização. No caso de cancelamento, vai guardar o registro de entrada de quem cancelou.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_ultima_atualizacao", nullable=true)
	@AtualizadoPor
	protected RegistroEntrada registroUltimaAtualizacao;
	
}
