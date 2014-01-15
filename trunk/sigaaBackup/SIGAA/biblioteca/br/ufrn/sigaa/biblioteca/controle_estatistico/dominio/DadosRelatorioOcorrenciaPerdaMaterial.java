package br.ufrn.sigaa.biblioteca.controle_estatistico.dominio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.DevolucaoMaterialPerdido;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.DevolucaoMaterialPerdido.TipoDevolucaoMaterialPerdido;

/**
 * Guarda os dados das comunica��es de perda realizadas
 * @author jadson
 *
 */
public class DadosRelatorioOcorrenciaPerdaMaterial{

	/** id do empr�timo */
	private int idEmprestimo;

	/** c�digo de barras */
	private String codigoBarras;

	/** descri��o da situa��o */
	private String descricaoSituacaoMaterial;

	/**se o material est� baixado */
	private boolean baixado;

	/** o id do T�tulo do material */
	private int idTitulo;

	/** A descri��o do T�tulo, usado somente se o material n�o est� baixado*/
	private String descricaoTitulo;
	
	/** informa��o material baixado */
	private String informacaoMaterialBaixado;

	/** biblioteca do material */
	private String descricaoBiblioteca;

	/** tipo de devolu��o, se ela j� ocorreu */
	private Short valorTipoDevolucao;

	/** motivo da n�o entrega do material, se ele n�o foi reposto pelo usu�rio */
	private String motivoNaoEntregaMaterial;

	/** o usu�rio que perdeu o material */
	private String nomeUsuarioPerdeuMaterial;

	/** o c�digo de barras do material que substitu�u o mateiral baixado */
	private String codigoBarrasSubstituidor;

	/** o usu�rio que devolveu */
	private String usuarioDevolveuEmprestimo;

	/** a data que foi devolvido */
	private Date dataDevolucao;

	/** Os dados da prorroga��o, pode ter sido prorrogado mais de uma vez */
	private List<DadosProrrogacaoRelatorioOcorrenciaPerdaMaterial> dadosProrrogacoes;
	
	
	/** Guarda a situa��o da devolu��o da comunica��o do material perdido */
	public enum SituacaoDevolucao{
		/** O empr�stimos n�o foi devolvido ainda.*/
		EM_ABERTO(0, "EM ABERTO")
		/** O usu�rio entregou um novo material na biblioteca que � similar ao atual, permanecer� com o mesmo c�digo de barras, 
		 * � como se n�o houve-se a troca e n�o ser� dado baixa no material antigo. */
		, REPOSTO_SIMILAR(1, "REPOSTO SIMILAR")
		/** O usu�rio entregou um novo material na biblioteca. */
		, REPOSTO_EQUIVALENTE(2, "REPOSTO EQUIVALENTE")
		/** O usu�rio entregou um novo material na biblioteca e o material anterior foi substitu�do */
		, SUBSTITUIDO(3, "SUBSTITU�DO")
		/** O usu�rio n�o entregou o nome material na biblioteca por algum motivo de for�a maior */
		, NAO_REPOSTO(4, "N�O REPOSTO");
		
		private SituacaoDevolucao(int valor, String descricao){
			this.valor = valor;
			this.descricao = descricao;
		}
		
		/**O valor da situa��o*/
		private int valor;
		/**A descri��o da situa��o*/
		private String descricao;
		
		public int getValor() {
			return valor;
		}
		public String getDescricao() {
			return descricao;
		}
		
		
	}
	
	/** Guarda como est� a situa��o de devolu��o do material perdido, se o usu�rio respos, e ainda est� empr�stado, se n�o vai devolver, etc..*/
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

	/** Adiciona as informa��es da prorroga��o ao objeto */
	public void adicionarProrrogacao(DadosProrrogacaoRelatorioOcorrenciaPerdaMaterial dados){
		if(dadosProrrogacoes == null)
			dadosProrrogacoes = new ArrayList<DadosProrrogacaoRelatorioOcorrenciaPerdaMaterial>();
		
		dadosProrrogacoes.add(dados);
	}

	/** Retorna a situa��o do material com rela��o a perda.*/
	public void calculaSituacaoPerda(){
		
		if(valorTipoDevolucao != null){ // para comunica��es feita ap�s as mudan�as da tarefa     #88009 Reserva de Material e Comunica��o de Material Perdido 
			
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
			
		}else{ // Para empr�timos anteriores quando n�o temos os dados da devolu��o ou empr�stimos novos que ainda n�o foram devolvidos
			
			if(usuarioDevolveuEmprestimo == null && dataDevolucao == null)
				this.situacaoDevolucao = SituacaoDevolucao.EM_ABERTO;
			else
				this.situacaoDevolucao = SituacaoDevolucao.REPOSTO_SIMILAR;
		}
	}
	
	
	/** Indica que o usu�rio n�o repos o material perdido.*/
	public boolean isUsuarioNaoReposMaterial(){
		return situacaoDevolucao == SituacaoDevolucao.NAO_REPOSTO;
	}
	
	/** Indica que o usu�rio n�o repos o material perdido.*/
	public boolean isUsuarioReposMaterial(){
		return situacaoDevolucao == SituacaoDevolucao.REPOSTO_EQUIVALENTE 
		|| situacaoDevolucao == SituacaoDevolucao.SUBSTITUIDO
		|| situacaoDevolucao == SituacaoDevolucao.REPOSTO_SIMILAR;
	}
	
	/** Indica que o usu�rio n�o repos o material perdido.*/
	public boolean isUsuarioReposMaterialSimilar(){
		return situacaoDevolucao == SituacaoDevolucao.REPOSTO_SIMILAR;
	}
	
	/** Indica que a comunica��o ainda est� em aberto.*/
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