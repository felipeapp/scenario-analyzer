/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 26/04/2013
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * <p>Informa as movimenta��es que uma Solicita��o de Material de outra Biblioteca passou.</p>
 *
 * @author jadson - jadson@info.ufrn.br
 * @vesion 1.0 - cria��o da classe.
 * @since 26/04/2013
 *
 */
@Entity
@Table(name = "movimentacao_solicitacao_material_outra_biblioteca", schema = "biblioteca")
public class MovimentacaoSolicitacaoMaterialOutraBiblioteca {

	

	/**
	 * <p>Representa os poss�veis estados que uma reserva de um material pode assumir durande o seu ciclo de vida </p>
	 * 
	 * @author jadson
	 *
	 */
	public enum StatusSolicitacaoMaterial{
		/** 
		 * <p>SOLICITADO = O usu�rio solicitou que um material fosse transferido de uma biblioteca para o outra.</p>
		 * <p>ENVIADO = O material foi enviado para a biblioteca que o usu�rio solicitou, n�o podendo ser mais emprestado na biblioteca origem dele.</p>
		 * <p>NEGADO = A solicita��o de envio do material foi negada por um bibliotec�rio da biblioteca do material.</p>
		 * <p>RECEBIDO = O material foi recebido na biblioteca de destino da solicita��o.</p>
		 * <p>RETORNADO = O material est� retornando para a biblioteca a qual ele pertence.</p>
		 * <p>FINALIZADO = A solicita��o foi finalizada, o material pode ser emprestado na biblioteca origem novamente.</p>
		 */ 
		SOLICITADO(0, "SOLICITADO"), ENVIADO(1, "ENVIADO"), NEGADO(2, "CANCELADA"), RECEBIDO(3,"RECEBIDO"), RETORNADO(4, "RETORNADO"), FINALIZADO(5, "FINALIZADO");
		
		/**  O valor do enum, deve ser igual a posi��o que foi declarado. J� que n�o tem como - de uma maneira f�cil de simples, sem 
		 * herdar de outras classes e sobrescrever um monte de m�todo  - fazer o hibernate 
		 * pegar o valor desejado, o hibernate simplemente pega o valor da posi��o onde a vari�vel do 
		 * enum foi declarada para realizar a persist�ncia. <br/>
		 * Diferentemente da pesquisa, onde o valor utilizado  pelo hibernate � o valor do m�todo toString() <br/>.
		 * 
		 * 
		 * {@link http://www.hibernate.org/265.html}
		 * {@link http://www.guj.com.br/java/144147-persistencia-de-enum-enumtypeordinal}
		 */
		private int valor;
		
		private String descricao;
		
		
		private StatusSolicitacaoMaterial(int valor, String descricao){
			this.valor = valor;
			this.descricao = descricao;
		}
		
		/**
		 * retorna a descri��o para exibi��o nas p�ginas.
		 * @return
		 */
		public String getDescricao() {
			return descricao;
		}

		/**  M�todo chamado pelo hibernate para pegar o valor do enum nas consultas HQLs  */
		@Override
		public String toString() {
			return String.valueOf(valor);
		}
		
		
		/**
		 * @return se o status que em que o material est� em tramite, nesse caso os empr�stimos da biblioteda destino devem ser permitidos e na biblioteca do material devem ser negados.
		 */
		public boolean isEmTramite(){
			return this == StatusSolicitacaoMaterial.ENVIADO || this == StatusSolicitacaoMaterial.RECEBIDO || this == StatusSolicitacaoMaterial.RETORNADO;
		} 
		
		
	}
	
	
	/** O id */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.solicitacoes_usuario_sequence") })
	@Column(name = "id_movimentacao_solicitacao_material_outra_biblioteca", nullable = false)
	private int id;
	
	
	/**
	 * O status da solicita��o
	 */
	@Enumerated(EnumType.ORDINAL) // O valor salvo no banco vai ser a ordem em que as vari�veis s�o declaradas
	@Column(name="status", nullable=false)
	private StatusSolicitacaoMaterial status;
	
	
	/**
	 * A data em que a movimenta��o passou para esse status.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@CriadoEm
	@Column(name = "data", nullable=false)
	private Date data;
	
	/**
	* Guarda o usu�rio que enviou modou o passou o material para esse status (seja o usu�rio que enviou o material, ou o usu�rio que confirmou o seu recebimento.)
	*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario", referencedColumnName ="id_usuario", nullable=false)
	private Usuario usuario;
	
	/**
	 * Registro de entrada  do usu�rio que criou a movimenta��o. Se precisar auditar isso algum dia.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_criacao", nullable=false)
	@CriadoPor
	protected RegistroEntrada registroCriacao;
	
}
