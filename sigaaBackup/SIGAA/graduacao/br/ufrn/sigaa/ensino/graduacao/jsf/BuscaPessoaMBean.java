/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Managed bean para a realiza��o de buscas por pessoas
 *
 * @author Andre M Dantas
 *
 */
public class BuscaPessoaMBean extends SigaaAbstractController<Pessoa> {

	/**
	 * C�digo da opera��o a ser realizada (Deve ser passado como par�metro ou
	 * definido no Bean)
	 */
	private int codigoOperacao;

	/** Opera��o ativa */
	private OperacaoPessoa operacao;

	/** Tipo de busca */
	private String tipoBusca;

	public BuscaPessoaMBean() {
		obj = new Pessoa();
	}

	public int getCodigoOperacao() {
		return codigoOperacao;
	}

	public void setCodigoOperacao(int codigoOperacao) {
		this.codigoOperacao = codigoOperacao;
	}

	public OperacaoPessoa getOperacao() {
		return operacao;
	}

	public void setOperacao(OperacaoPessoa operacao) {
		this.operacao = operacao;
	}

	/**
	 * Retorna qual opera��o o usu�rio vai executar agora, a partir do c�digo da opera��o.
	 * N�o � chamado por nenhuma JSP.
	 * 
	 * @return P�gina de busca geral de pessoas
	 * @throws SegurancaException
	 */
	public String popular() throws SegurancaException {
		operacao = OperacaoPessoa.getOperacao(codigoOperacao);
		// checkRole( new int[] {SigaaPapeis.DAE, SigaaPapeis.CDP,
		return forward("/geral/pessoa/busca_geral.jsp");
	}

	/**
	 * Busca pessoas de acordo com os crit�rios de busca informados.<br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa/geral/pessoa/busca_geral.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.jsf.AbstractControllerCadastro#buscar()
	 */
	@Override
	public String buscar() throws SegurancaException {
		// checkRole( new int[] {SigaaPapeis.DAE, SigaaPapeis.CDP,
		// SigaaPapeis.COORDENADOR_CURSO} );
		tipoBusca = getParameter("tipoBusca");
		operacao = OperacaoPessoa.getOperacao(codigoOperacao);
		
		// Realizar a consulta
		PessoaDao dao = getDAO(PessoaDao.class);
		try {
			// Efetuar a consulta

			if ("nome".equalsIgnoreCase(tipoBusca)){
				 setResultadosBusca(dao.findByNomeTipo(obj.getNome(), Pessoa.PESSOA_FISICA, null));
			}else{ 
				if ("cpf".equalsIgnoreCase(tipoBusca)){

					if (obj == null || obj.getCpf_cnpj() == null || obj.getCpf_cnpj() == 0) {
						addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "CPF");
						setResultadosBusca(null);
					}else{
						setResultadosBusca(dao.findByCpfCnpj(obj.getCpf_cnpj()));
					}
				}else
					setResultadosBusca(null);
			}

			// Caso tenha sido retornado apenas 1 discente, j� redirecionar para
			// a opera��o
			if (getResultadosBusca() != null && getResultadosBusca().size() == 1)
				return redirecionarPessoa(getResultadosBusca().iterator().next());

			if (getResultadosBusca() == null || getResultadosBusca().isEmpty())
				addMensagemErro("Nenhum registro encontrado de acordo com os crit�rios de busca informados.");

		} catch (DAOException e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErro("Um problema ocorreu durante a consulta. Contacte os administradores do sistema");
			return null;
		}
		return forward("/geral/pessoa/busca_geral.jsp");
	}

	/**
	 * Carrega do banco a pessoa selecionada na p�gina.
	 * 
	 * <br/>
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/geral/pessoa/busca_geral.jsp</li>
	 * </ul>
	 * 
	 * @return A p�gina que representa a opera��o a ser realizada sobre a pessoa.
	 */
	public String escolhePessoa() {
		getParameterInt("idPessoa");

		Pessoa pessoa = null;
		try {
			pessoa = getGenericDAO().findByPrimaryKey(getParameterInt("idPessoa"),
					Pessoa.class);
		} catch (DAOException e) {
			notifyError(e);
			addMensagemErro("Erro ao buscar as informa��es do discente");
			e.printStackTrace();
			return null;
		}

		if (pessoa == null) {
			addMensagemErro("Discente n�o encontrado!");
			return null;
		}

		return redirecionarPessoa(pessoa);
	}
	
	/**
	 * Retorna a p�gina que representa a opera��o a ser realizada sobre uma pessoa.
	 * 
	 * <p>M�todo n�o � chamado em nenhuma JSP.</p>
	 * 
	 * @param pessoa Pessoa sobre a qual a opera��o ser� realizada.
	 * @return P�gina referente � opera��o.
	 */
	private String redirecionarPessoa(Pessoa pessoa) {
		// Redirecionar para a opera��o especificada
		operacao = OperacaoPessoa.getOperacao(codigoOperacao);
		OperadorPessoa mBean = (OperadorPessoa) getMBean(operacao.getMBean());
		mBean.setPessoa(pessoa);
		return mBean.selecionaPessoa();
	}

	public String getTipoBusca() {
		return tipoBusca;
	}

	public void setTipoBusca(String tipoBusca) {
		this.tipoBusca = tipoBusca;
	}
	
}
