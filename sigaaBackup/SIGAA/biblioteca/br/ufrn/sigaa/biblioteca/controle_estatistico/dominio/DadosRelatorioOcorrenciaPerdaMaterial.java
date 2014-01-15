package br.ufrn.sigaa.biblioteca.controle_estatistico.dominio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.DevolucaoMaterialPerdido;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.DevolucaoMaterialPerdido.TipoDevolucaoMaterialPerdido;

/**
 * Guarda os dados das comunicações de perda realizadas
 * @author jadson
 *
 */
public class DadosRelatorioOcorrenciaPerdaMaterial{

	/** id do emprétimo */
	private int idEmprestimo;

	/** código de barras */
	private String codigoBarras;

	/** descrição da situação */
	private String descricaoSituacaoMaterial;

	/**se o material está baixado */
	private boolean baixado;

	/** o id do Título do material */
	private int idTitulo;

	/** A descrição do Título, usado somente se o material não está baixado*/
	private String descricaoTitulo;
	
	/** informação material baixado */
	private String informacaoMaterialBaixado;

	/** biblioteca do material */
	private String descricaoBiblioteca;

	/** tipo de devolução, se ela já ocorreu */
	private Short valorTipoDevolucao;

	/** motivo da não entrega do material, se ele não foi reposto pelo usuário */
	private String motivoNaoEntregaMaterial;

	/** o usuário que perdeu o material */
	private String nomeUsuarioPerdeuMaterial;

	/** o código de barras do material que substituíu o mateiral baixado */
	private String codigoBarrasSubstituidor;

	/** o usuário que devolveu */
	private String usuarioDevolveuEmprestimo;

	/** a data que foi devolvido */
	private Date dataDevolucao;

	/** Os dados da prorrogação, pode ter sido prorrogado mais de uma vez */
	private List<DadosProrrogacaoRelatorioOcorrenciaPerdaMaterial> dadosProrrogacoes;
	
	
	/** Guarda a situação da devolução da comunicação do material perdido */
	public enum SituacaoDevolucao{
		/** O empréstimos não foi devolvido ainda.*/
		EM_ABERTO(0, "EM ABERTO")
		/** O usuário entregou um novo material na biblioteca que é similar ao atual, permanecerá com o mesmo código de barras, 
		 * é como se não houve-se a troca e não será dado baixa no material antigo. */
		, REPOSTO_SIMILAR(1, "REPOSTO SIMILAR")
		/** O usuário entregou um novo material na biblioteca. */
		, REPOSTO_EQUIVALENTE(2, "REPOSTO EQUIVALENTE")
		/** O usuário entregou um novo material na biblioteca e o material anterior foi substituído */
		, SUBSTITUIDO(3, "SUBSTITUÍDO")
		/** O usuário não entregou o nome material na biblioteca por algum motivo de força maior */
		, NAO_REPOSTO(4, "NÃO REPOSTO");
		
		private SituacaoDevolucao(int valor, String descricao){
			this.valor = valor;
			this.descricao = descricao;
		}
		
		/**O valor da situação*/
		private int valor;
		/**A descrição da situação*/
		private String descricao;
		
		public int getValor() {
			return valor;
		}
		public String getDescricao() {
			return descricao;
		}
		
		
	}
	
	/** Guarda como está a situação de devolução do material perdido, se o usuário respos, e ainda está empréstado, se não vai devolver, etc..*/
	private SituacaoDevolucao situacaoDevolucao;
	
	
	public DadosRelatorioOcorrenciaPerdaMaterial(int idEmprestimo,
			String codigoBarras, String descricaoSituacaoMaterial,
			boolean baixado, int idTitulo, String informacaoMaterialBaixado,
			String descricaoBiblioteca, Short valorTipoDevolucao,
			String motivoNaoEntregaMaterial, String nomeUsuarioPerdeuMaterial,
			String codigoBarrasSubstituidor, String usuarioDevolveuEmprestimo,
			Date dataDevolucao) {
		this.idEmprestimo = idEmprestimo;
		this.codigoBarras = codigoBarras;
		this.descricaoSituacaoMaterial = descricaoSituacaoMaterial;
		this.baixado = baixado;
		this.idTitulo = idTitulo;
		this.informacaoMaterialBaixado = informacaoMaterialBaixado;
		this.descricaoBiblioteca = descricaoBiblioteca;
		this.valorTipoDevolucao = valorTipoDevolucao;
		this.motivoNaoEntregaMaterial = motivoNaoEntregaMaterial;
		this.nomeUsuarioPerdeuMaterial = nomeUsuarioPerdeuMaterial;
		this.codigoBarrasSubstituidor = codigoBarrasSubstituidor;
		this.usuarioDevolveuEmprestimo = usuarioDevolveuEmprestimo;
		this.dataDevolucao = dataDevolucao;
	}

	/** Adiciona as informações da prorrogação ao objeto */
	public void adicionarProrrogacao(DadosProrrogacaoRelatorioOcorrenciaPerdaMaterial dados){
		if(dadosProrrogacoes == null)
			dadosProrrogacoes = new ArrayList<DadosProrrogacaoRelatorioOcorrenciaPerdaMaterial>();
		
		dadosProrrogacoes.add(dados);
	}

	/** Retorna a situação do material com relação a perda.*/
	public void calculaSituacaoPerda(){
		
		if(valorTipoDevolucao != null){ // para comunicações feita após as mudanças da tarefa     #88009 Reserva de Material e Comunicação de Material Perdido 
			
			TipoDevolucaoMaterialPerdido tipo =  DevolucaoMaterialPerdido.TipoDevolucaoMaterialPerdido.getTipoDevolucao(valorTipoDevolucao);
			
			if(tipo == TipoDevolucaoMaterialPerdido.USUARIO_ENTREGOU_MATERIAL_SIMILAR){
				this.situacaoDevolucao = SituacaoDevolucao.REPOSTO_SIMILAR;
			}
			
			if(tipo == TipoDevolucaoMaterialPerdido.USUARIO_ENTREGOU_MATERIAL_EQUIVALENTE){
				
				if(StringUtils.notEmpty(codigoBarrasSubstituidor))
					this.situacaoDevolucao = SituacaoDevolucao.SUBSTITUIDO;
				else
					this.situacaoDevolucao = SituacaoDevolucao.REPOSTO_EQUIVALENTE;
			}
			
			if(tipo == TipoDevolucaoMaterialPerdido.USUARIO_NAO_ENTREGOU_MATERIAL_SUBSTITUTO)
				this.situacaoDevolucao = SituacaoDevolucao.NAO_REPOSTO;
			
		}else{ // Para emprétimos anteriores quando não temos os dados da devolução ou empréstimos novos que ainda não foram devolvidos
			
			if(usuarioDevolveuEmprestimo == null && dataDevolucao == null)
				this.situacaoDevolucao = SituacaoDevolucao.EM_ABERTO;
			else
				this.situacaoDevolucao = SituacaoDevolucao.REPOSTO_SIMILAR;
		}
	}
	
	
	/** Indica que o usuário não repos o material perdido.*/
	public boolean isUsuarioNaoReposMaterial(){
		return situacaoDevolucao == SituacaoDevolucao.NAO_REPOSTO;
	}
	
	/** Indica que o usuário não repos o material perdido.*/
	public boolean isUsuarioReposMaterial(){
		return situacaoDevolucao == SituacaoDevolucao.REPOSTO_EQUIVALENTE 
		|| situacaoDevolucao == SituacaoDevolucao.SUBSTITUIDO
		|| situacaoDevolucao == SituacaoDevolucao.REPOSTO_SIMILAR;
	}
	
	/** Indica que o usuário não repos o material perdido.*/
	public boolean isUsuarioReposMaterialSimilar(){
		return situacaoDevolucao == SituacaoDevolucao.REPOSTO_SIMILAR;
	}
	
	/** Indica que a comunicação ainda está em aberto.*/
	public boolean isComunicaoEmAberto(){
		return situacaoDevolucao == SituacaoDevolucao.EM_ABERTO;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idEmprestimo;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DadosRelatorioOcorrenciaPerdaMaterial other = (DadosRelatorioOcorrenciaPerdaMaterial) obj;
		if (idEmprestimo != other.idEmprestimo)
			return false;
		return true;
	}

	
	
	public SituacaoDevolucao getSituacaoDevolucao() {
		return situacaoDevolucao;
	}

	public String getDescricaoTitulo() {
		return descricaoTitulo;
	}

	public int getIdEmprestimo() {
		return idEmprestimo;
	}

	public String getCodigoBarras() {
		return codigoBarras;
	}

	public String getDescricaoSituacaoMaterial() {
		return descricaoSituacaoMaterial;
	}

	public boolean isBaixado() {
		return baixado;
	}

	public int getIdTitulo() {
		return idTitulo;
	}

	public String getInformacaoMaterialBaixado() {
		return informacaoMaterialBaixado;
	}

	public String getDescricaoBiblioteca() {
		return descricaoBiblioteca;
	}

	public Short getValorTipoDevolucao() {
		return valorTipoDevolucao;
	}

	public String getMotivoNaoEntregaMaterial() {
		return motivoNaoEntregaMaterial;
	}

	public String getNomeUsuarioPerdeuMaterial() {
		return nomeUsuarioPerdeuMaterial;
	}

	public String getCodigoBarrasSubstituidor() {
		return codigoBarrasSubstituidor;
	}

	public String getUsuarioDevolveuEmprestimo() {
		return usuarioDevolveuEmprestimo;
	}

	public Date getDataDevolucao() {
		return dataDevolucao;
	}

	public List<DadosProrrogacaoRelatorioOcorrenciaPerdaMaterial> getDadosProrrogacoes() {
		return dadosProrrogacoes;
	}

	public void setDescricaoTitulo(String descricaoTitulo) {
		this.descricaoTitulo = descricaoTitulo;
	}
	 
	 
	 
}