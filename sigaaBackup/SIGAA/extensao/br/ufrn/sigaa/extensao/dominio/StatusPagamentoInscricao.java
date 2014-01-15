package br.ufrn.sigaa.extensao.dominio;


/**
 * <p>Representa os poss�veis status de inscri��o em cursos e eventos de extens�o com rela��o ao pagamento dessa incri��o. </p>
 * 
 * @author jadson
 *
 */
public enum StatusPagamentoInscricao {

	/** 
	 * <p>NAO_GERENCIADO = A confirma��o do pagamento da inscri��o n�o � gerenciada pelo sistema (Para suportar pagamentos anteriores ao sistema suportar essa funcionalidade).</p>
	 * <p>EM_ABERTO = A inscri��o foi criada e ainda n�o foi paga.</p>
	 * <p>CONFIRMADO_MANUALMENTE = Algum gestor quitou a inscri��o do usu�rio no sistema mediante a apresenta��o do comprovante de pagamento da GRU.</p>
	 * <p>CONFIRMADO_AUTOMATICAMENTE = A inscri��o foi quitada automaticamente por meio do recebimento do arquivo de retorno do banco.</p>
	 * <p>ESTORNADO = O pagamento foi estornado.</p>
	 */ 
	NAO_GERENCIADO(0, "N�O GERENCIADO"), EM_ABERTO(1, "EM ABERTO"), CONFIRMADO_MANUALMENTE(2, "CONFIRMADO"), CONFIRMADO_AUTOMATICAMENTE(3, "CONFIRMADO")
		, ESTORNADO(4, "ESTORNADO");
	
	/**  
	 * O valor do enum, deve ser igual a posi��o que foi declarado <br/>
	 *  Nas pesquisa com HQL o valor utilizado pelo hibernate � o valor do m�todo toString() <br/>.
	 * 
	 * {@link http://www.hibernate.org/265.html}
	 * {@link http://www.guj.com.br/java/144147-persistencia-de-enum-enumtypeordinal}
	 */
	private int valor;
	
	/** A descri��o mostrada para o usu�rio*/
	private String descricao;
	
	private StatusPagamentoInscricao(int valor, String descricao){
		this.valor = valor;
		this.descricao = descricao;
	}
	
	/**
	 * Retorna o status da multa a partir da descri��o.
	 * @param status
	 * @return
	 */
	public static StatusPagamentoInscricao getStatusMulta(Integer status) {
		if(status == NAO_GERENCIADO.valor)
			return  NAO_GERENCIADO;
		if(status == EM_ABERTO.valor)
			return  EM_ABERTO;
		if(status == CONFIRMADO_MANUALMENTE.valor)
			return  CONFIRMADO_MANUALMENTE;
		if(status == CONFIRMADO_AUTOMATICAMENTE.valor)
			return  CONFIRMADO_AUTOMATICAMENTE;
		if(status == ESTORNADO.valor)
			return  ESTORNADO;
		return null;
	}
	
	/** Verifica se o status � o status de confirma��o manual. */
	public boolean isConfirmadoManualmente(){
		return this.valor == CONFIRMADO_MANUALMENTE.getValor();
	}
	
	/** Verifica se o status � o status de confirma��o autom�tico. */
	public boolean isConfirmadoAutomaticamente(){
		return this.valor == CONFIRMADO_AUTOMATICAMENTE.getValor();
	}
	
	/** Verifica se o status � o status onde o usu�rio ainda n�o realizou o pagamento. */
	public boolean isAberto(){
		return this.valor == EM_ABERTO.getValor();
	}
	

	/** Verifica se o status � do pagamento � o status estornado. */
	public boolean isEstornado(){
		return this.valor == EM_ABERTO.getValor();
	}
	
	public int getValor() {
		return valor;
	}

	public String getDescricao() {
		return descricao;
	}

	/**  M�todo chamado pelo hibernate para pegar o valor do enum nas consultas HQLs  */
	@Override
	public String toString() {
		return String.valueOf(valor);
	}
	
}
