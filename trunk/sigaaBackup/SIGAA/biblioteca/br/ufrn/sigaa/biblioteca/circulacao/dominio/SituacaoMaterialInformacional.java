/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 20/10/2008
 *
 */
package br.ufrn.sigaa.biblioteca.circulacao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.StringUtils;

/**
 *
 *    Indica a situação que o material está no momento: disponível, emprestado, danificado, perdido,
 *  em encadernação, em processo técnico, em reforma, etc... <br/>
 *    Um Material só pode ser emprestado se estiver <strong>disponível</strong>.
 *
 * @author jadson
 * @since 20/10/2008
 * @version 1.0 criacao da classe
 *
 */
@Entity
@Table(name = "situacao_material_informacional", schema = "biblioteca")
public class SituacaoMaterialInformacional implements Validatable {
	
	/** Id da situação. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })
	@Column(name = "id_situacao_material_informacional")
	private int id;

	/** Nome da situação. */
	@Column(nullable=false)
	private String descricao;

	/**
	 *  Indica que os materiais nessa situação podem ser emprestados.
	 *  Criei essa variável para não ficar comparando os o id da classe.
	 */
	@Column(name = "situacao_disponivel", nullable=false)
	private boolean situacaoDisponivel = false;
	
	/**
	 *  Indica que os materiais nessa situação estão emprestados
	 *  Criei essa variável para não ficar comparando os o id da classe.
	 */
	@Column(name = "situacao_emprestado", nullable=false)
	private boolean situacaoEmprestado = false;
	
	/**
	 *  Indica que os materiais nessa situação não são vistos nas buscas. Só em algum relatório específico.
	 *  Criei essa variável para não ficar comparando os o id da classe.
	 */
	@Column(name = "situacao_de_baixa", nullable=false)
	private boolean situacaoDeBaixa = false;
	
	
	/** <p>Campo utiliado para indica em quais situações os materiais serão visíveis pelos usuários da biblioteca.</p>
	 *  <p>Na busca do acervo utilizada pelos bibliotecários, os materiais sempre aparecem, independente da situação.</p>
	 */
	@Column(name = "visivel_pelo_usuario", nullable=false)
	private boolean visivelPeloUsuario = true;
	
	/** <p>Indica quando uma situação pode ser editada pelo administrado do sistema.</p>
	 *  <p>As situação "disponível", "emprestado", "em baixa" não podem ser editadas pelo usuário porque fazerm parte das regras de negócio do sistema.</p> 
	 */
	@Column(name = "editavel", nullable=false)
	private boolean editavel = true;
	
	
	/** <p>Campo utilizado para verificar se a situação foi removida do sistema.</p>
	 *  <p>Na remoção, os materiais devem ser migrados para alguma situação ativa no sistema, mesmo a situação "emprestado" e "em baixa".
	 *  Para nunca existem materiais em situação removidas.</p>
	 */
	@Column(name = "ativo", nullable=false)
	private boolean ativo = true;
	
	
	/**
	 * Construtor para Hibernate e JSF.
	 */
	public SituacaoMaterialInformacional(){

	}

	/**
	 * Construtor de um objeto persistente
	 */
	public SituacaoMaterialInformacional (int id){
		this.id = id;
	}
	
	/**
	 * Construtor de um objeto persistente
	 */
	public SituacaoMaterialInformacional (String descricao){
		this.descricao = descricao;
	}
	
	/**
	 * Construtor de um objeto com um id e descrição
	 */
	public SituacaoMaterialInformacional (int id, String descricao){
		this(id);
		this.descricao = descricao;
	}
	
	/**
	 * Construtor de um objeto não persistido completo
	 */
	public SituacaoMaterialInformacional (String descricao, boolean situacaoDisponivel, boolean situacaoEmprestado, boolean situacaoDeBaixa){
		this(descricao);
		this.situacaoDisponivel = situacaoDisponivel;
		this.situacaoEmprestado = situacaoEmprestado;
		this.situacaoDeBaixa = situacaoDeBaixa;
	}
	
	/**
	 * Construtor de um objeto persistido e  completo
	 */
	public SituacaoMaterialInformacional (int id, String descricao, boolean situacaoDisponivel, boolean situacaoEmprestado, boolean situacaoDeBaixa){
		this.id = id;
		this.descricao = descricao;
		this.situacaoDisponivel = situacaoDisponivel;
		this.situacaoEmprestado = situacaoEmprestado;
		this.situacaoDeBaixa = situacaoDeBaixa;
	}
	

	/**
	 * Validar se as variáveis estão preenchidas corretamente
	 */
	public ListaMensagens validate() {

		ListaMensagens mensagens = new ListaMensagens();

		if (StringUtils.isEmpty(descricao))
			mensagens.addErro("É preciso informar a descrição da Situação do Material Informacional");

		if(id > 0){
			if(! editavel){
				mensagens.addErro("Essa situação não pode ser removida, pois ela faz parte das regras do sistema.");
			}
		}
		
		return mensagens;
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

	public String getDescricao() {
		return descricao;
	}


	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public boolean isSituacaoEmprestado(){
		return situacaoEmprestado;
	}
	
	public boolean isSituacaoDisponivel(){
		return situacaoDisponivel;
	}
	
	public boolean isSituacaoDeBaixa(){
		return situacaoDeBaixa;
	}

	public void setSituacaoDisponivel(boolean situacaoDisponivel) {
		this.situacaoDisponivel = situacaoDisponivel;
	}

	public void setSituacaoEmprestado(boolean situacaoEmprestado) {
		this.situacaoEmprestado = situacaoEmprestado;
	}

	public void setSituacaoDeBaixa(boolean situacaoDeBaixa) {
		this.situacaoDeBaixa = situacaoDeBaixa;
	}

	public boolean isVisivelPeloUsuario() {
		return visivelPeloUsuario;
	}

	public void setVisivelPeloUsuario(boolean visivelPeloUsuario) {
		this.visivelPeloUsuario = visivelPeloUsuario;
	}

	public boolean isEditavel() {
		return editavel;
	}

	public void setEditavel(boolean editavel) {
		this.editavel = editavel;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
}
