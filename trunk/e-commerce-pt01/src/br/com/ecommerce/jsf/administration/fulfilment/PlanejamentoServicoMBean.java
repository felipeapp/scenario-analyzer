package br.com.ecommerce.jsf.administration.fulfilment;

import java.text.ParseException;
import java.util.List;

import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.ecommerce.arq.erros.DAOException;
import br.com.ecommerce.arq.jsf.CadastroAbstractController;
import br.com.ecommerce.arq.mensagem.MensagensArquitetura;
import br.com.ecommerce.arq.sbeans.SBeanCadastro;
import br.com.ecommerce.arq.sbeans.SBeanInativacao;
import br.com.ecommerce.dao.ProdutoDAO;
import br.com.ecommerce.dominio.DiaSemana;
import br.com.ecommerce.dominio.PlanejamentoServico;
import br.com.ecommerce.dominio.produto.Produto;
import br.com.ecommerce.jsf.ConstantesNavegacao;

/**
 * Controlador respons�vel por opera��es relacionadas a agendamento de servi�os.
 * Trabalha em cima do cadastro, remo��o, listagem. Oferece m�todos para
 * suggestion-box, e listagem de items.
 * 
 * @author Mario Torres
 * 
 */
@Component
@Scope("session")
public class PlanejamentoServicoMBean extends
		CadastroAbstractController<PlanejamentoServico> {

	/**
	 * P�gina repons�vel pelo crud do servi�o.
	 */
	private static final String CRUD_FORM = "/administration/fulfilment/services/planejamento.jsf";

	/**
	 * P�gina repons�vel pela visualiza��o detalhada do tipo de produto.
	 */
	private static final String VISUALIZAR_DETALHADAMENTE = "";

	/**
	 * Planejamento que esta sendo trabalhado atualmente.
	 */
	private PlanejamentoServico planejamento;

	/**
	 * Planejamento que ser� visualizado.
	 */
	private PlanejamentoServico planejamentoVisualizado;

	/**
	 * Processador Cadastro.
	 */
	@Autowired
	private SBeanCadastro sBeanCadastro;

	/**
	 * Processador respons�vel pela inativa��o de PersistDBs
	 */
	@Autowired
	private SBeanInativacao sBeanInativacao;

	public PlanejamentoServicoMBean() {
		reset();
	}

	/**
	 * Reinicia as vari�veis do mbean.
	 */
	private void reset() {
		obj = new PlanejamentoServico();
		obj.setProduto(new Produto());

		// planejamento = new PlanejamentoServico();
	}

	/**
	 * M�todo chamado para se enviar para a p�gina de crud.
	 * 
	 * @return forward(CRUD_FORM)
	 */
	public String iniciarCadastro() {
		reset();
		return forward(CRUD_FORM);
	}

	/**
	 * M�todo chamado para se validar e cadastrar um planejamento de servi�o.
	 * 
	 * @return null
	 * @throws DAOException
	 * @throws ParseException
	 */
	public String cadastrarNovoPlanejamento() throws DAOException,
			ParseException {

		if(isEmpty(obj.getProduto()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Produto");
		
		if(isEmpty(obj.getEquipe()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Equipe");
		
		if (hasMensagens())
			return null;

		sBeanCadastro.cadastrar(obj);

		addMensagem(MensagensArquitetura.OPERACAO_REALIZADA_COM_SUCESSO,
				"Cadastro de Planejamento");

		reset();
		return cancelar();
	}
	
	/**
	 * M�todo usado para se cancelar.
	 * @return
	 */
	public String cancelar(){
		return redirect(ConstantesNavegacao.PORTAL_ADMINISTRACAO);
	}

	/**
	 * Permite a visualiza��o de forma detalhada de um determinado servi�o.
	 * 
	 * @return null
	 * @throws DAOException
	 * @throws NumberFormatException
	 */
	public String visualizarDetalhadamentePlanejamento()
			throws NumberFormatException, DAOException {
		PlanejamentoServico planejamentoServico = getGenericDAO()
				.findByPrimaryKey(
						Integer.parseInt(getParameter("idPlanejamentoServico")),
						PlanejamentoServico.class);

		if (planejamentoServico.isInativo())
			addMensagem(MensagensArquitetura.ELEMENTO_NAO_DISPONIVEL_NO_BANCO,
					"Planejamento");

		if (hasMensagens())
			return null;

		return forward(VISUALIZAR_DETALHADAMENTE);
	}

	/**
	 * Permite a remo��o de um determinado Planejamento.
	 * 
	 * @return
	 * @throws DAOException
	 * @throws NumberFormatException
	 */
	public String removerPlanejamento() throws NumberFormatException,
			DAOException {
		PlanejamentoServico planejamentoServico = getGenericDAO()
				.findByPrimaryKey(
						Integer.parseInt(getParameter("idPlanejamentoServico")),
						PlanejamentoServico.class);

		if (planejamentoServico.isInativo())
			addMensagem(MensagensArquitetura.SOLICITACAO_JA_PROCESSADA,
					"Remo��o do Planejamento");

		if (hasMensagens())
			return null;

		sBeanInativacao.inativar(planejamentoServico);

		addMensagem(MensagensArquitetura.OPERACAO_REALIZADA_COM_SUCESSO,
				"Remo��o do Planejamento");

		return redirect(CRUD_FORM);
	}

	
	public List<Produto> autocompleteProduto(Object nome) throws HibernateException, DAOException{
		String nomeLike = (String) nome;
	       
        ProdutoDAO dao = getDAO(ProdutoDAO.class);
       
        return dao.autoCompleteProduto(nomeLike);
	}
	
	/**
	 * M�todo usado para se buscar por todos os dias de semana.
	 * 
	 * @return os tipos ativos encontrados.
	 * 
	 * @throws DAOException
	 */
	public List<SelectItem> getAllCombo() throws DAOException {
		return toSelectItems(getGenericDAO().findAll(DiaSemana.class), "id",
				"denominacao");
	}

	/**
	 * Busca todos os planejamentos ativos.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<PlanejamentoServico> getAllPlanejamentoServico()
			throws DAOException {
		return (List<PlanejamentoServico>) getGenericDAO().findAllAtivos(
				PlanejamentoServico.class);
	}

	public PlanejamentoServico getPlanejamento() {
		return planejamento;
	}

	public void setPlanejamento(PlanejamentoServico planejamento) {
		this.planejamento = planejamento;
	}

	public PlanejamentoServico getPlanejamentoVisualizado() {
		return planejamentoVisualizado;
	}

	public void setPlanejamentoVisualizado(
			PlanejamentoServico planejamentoVisualizado) {
		this.planejamentoVisualizado = planejamentoVisualizado;
	}
}
