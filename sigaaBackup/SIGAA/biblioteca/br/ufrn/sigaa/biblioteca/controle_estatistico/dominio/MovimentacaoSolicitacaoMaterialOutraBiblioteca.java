/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p>Informa as movimentações que uma Solicitação de Material de outra Biblioteca passou.</p>
 *
 * @author jadson - jadson@info.ufrn.br
 * @vesion 1.0 - criação da classe.
 * @since 26/04/2013
 *
 */
@Entity
@Table(name = "movimentacao_solicitacao_material_outra_biblioteca", schema = "biblioteca")
public class MovimentacaoSolicitacaoMaterialOutraBiblioteca {

	

	/**
	 * <p>Representa os possíveis estados que uma reserva de um material pode assumir durande o seu ciclo de vida </p>
	 * 
	 * @author jadson
	 *
	 */
	public enum StatusSolicitacaoMaterial{
		/** 
		 * <p>SOLICITADO = O usuário solicitou que um material fosse transferido de uma biblioteca para o outra.</p>
		 * <p>ENVIADO = O material foi enviado para a biblioteca que o usuário solicitou, não podendo ser mais emprestado na biblioteca origem dele.</p>
		 * <p>NEGADO = A solicitação de envio do material foi negada por um bibliotecário da biblioteca do material.</p>
		 * <p>RECEBIDO = O material foi recebido na biblioteca de destino da solicitação.</p>
		 * <p>RETORNADO = O material está retornando para a biblioteca a qual ele pertence.</p>
		 * <p>FINALIZADO = A solicitação foi finalizada, o material pode ser emprestado na biblioteca origem novamente.</p>
		 */ 
		SOLICITADO(0, "SOLICITADO"), ENVIADO(1, "ENVIADO"), NEGADO(2, "CANCELADA"), RECEBIDO(3,"RECEBIDO"), RETORNADO(4, "RETORNADO"), FINALIZADO(5, "FINALIZADO");
		
		/**  O valor do enum, deve ser igual a posição que foi declarado. Já que não tem como - de uma maneira fácil de simples, sem 
		 * herdar de outras classes e sobrescrever um monte de método  - fazer o hibernate 
		 * pegar o valor desejado, o hibernate simplemente pega o valor da posição onde a variável do 
		 * enum foi declarada para realizar a persistência. <br/>
		 * Diferentemente da pesquisa, onde o valor utilizado  pelo hibernate é o valor do método toString() <br/>.
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
		 * retorna a descrição para exibição nas páginas.
		 * @return
		 */
		public String getDescricao() {
			return descricao;
		}

		/**  Método chamado pelo hibernate para pegar o valor do enum nas consultas HQLs  */
		@Override
		public String toString() {
			return String.valueOf(valor);
		}
		
		
		/**
		 * @return se o status que em que o material está em tramite, nesse caso os empréstimos da biblioteda destino devem ser permitidos e na biblioteca do material devem ser negados.
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
	 * O status da solicitação
	 */
	@Enumerated(EnumType.ORDINAL) // O valor salvo no banco vai ser a ordem em que as variáveis são declaradas
	@Column(name="status", nullable=false)
	private StatusSolicitacaoMaterial status;
	
	
	/**
	 * A data em que a movimentação passou para esse status.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@CriadoEm
	@Column(name = "data", nullable=false)
	private Date data;
	
	/**
	* Guarda o usuário que enviou modou o passou o material para esse status (seja o usuário que enviou o material, ou o usuário que confirmou o seu recebimento.)
	*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario", referencedColumnName ="id_usuario", nullable=false)
	private Usuario usuario;
	
	/**
	 * Registro de entrada  do usuário que criou a movimentação. Se precisar auditar isso algum dia.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_criacao", nullable=false)
	@CriadoPor
	protected RegistroEntrada registroCriacao;
	
}
