package br.ufrn.sigaa.extensao.dominio;


/**
 * <p>Representa os possíveis status de inscrição em cursos e eventos de extensão com relação ao pagamento dessa incrição. </p>
 * 
 * @author jadson
 *
 */
public enum StatusPagamentoInscricao {

	/** 
	 * <p>NAO_GERENCIADO = A confirmação do pagamento da inscrição não é gerenciada pelo sistema (Para suportar pagamentos anteriores ao sistema suportar essa funcionalidade).</p>
	 * <p>EM_ABERTO = A inscrição foi criada e ainda não foi paga.</p>
	 * <p>CONFIRMADO_MANUALMENTE = Algum gestor quitou a inscrição do usuário no sistema mediante a apresentação do comprovante de pagamento da GRU.</p>
	 * <p>CONFIRMADO_AUTOMATICAMENTE = A inscrição foi quitada automaticamente por meio do recebimento do arquivo de retorno do banco.</p>
	 * <p>ESTORNADO = O pagamento foi estornado.</p>
	 */ 
	NAO_GERENCIADO(0, "NÃO GERENCIADO"), EM_ABERTO(1, "EM ABERTO"), CONFIRMADO_MANUALMENTE(2, "CONFIRMADO"), CONFIRMADO_AUTOMATICAMENTE(3, "CONFIRMADO")
		, ESTORNADO(4, "ESTORNADO");
	
	/**  
	 * O valor do enum, deve ser igual a posição que foi declarado <br/>
	 *  Nas pesquisa com HQL o valor utilizado pelo hibernate é o valor do método toString() <br/>.
	 * 
	 * {@link http://www.hibernate.org/265.html}
	 * {@link http://www.guj.com.br/java/144147-persistencia-de-enum-enumtypeordinal}
	 */
	private int valor;
	
	/** A descrição mostrada para o usuário*/
	private String descricao;
	
	private StatusPagamentoInscricao(int valor, String descricao){
		this.valor = valor;
		this.descricao = descricao;
	}
	
	/**
	 * Retorna o status da multa a partir da descrição.
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
	
	/** Verifica se o status é o status de confirmação manual. */
	public boolean isConfirmadoManualmente(){
		return this.valor == CONFIRMADO_MANUALMENTE.getValor();
	}
	
	/** Verifica se o status é o status de confirmação automático. */
	public boolean isConfirmadoAutomaticamente(){
		return this.valor == CONFIRMADO_AUTOMATICAMENTE.getValor();
	}
	
	/** Verifica se o status é o status onde o usuário ainda não realizou o pagamento. */
	public boolean isAberto(){
		return this.valor == EM_ABERTO.getValor();
	}
	

	/** Verifica se o status é do pagamento é o status estornado. */
	public boolean isEstornado(){
		return this.valor == EM_ABERTO.getValor();
	}
	
	public int getValor() {
		return valor;
	}

	public String getDescricao() {
		return descricao;
	}

	/**  Método chamado pelo hibernate para pegar o valor do enum nas consultas HQLs  */
	@Override
	public String toString() {
		return String.valueOf(valor);
	}
	
}
