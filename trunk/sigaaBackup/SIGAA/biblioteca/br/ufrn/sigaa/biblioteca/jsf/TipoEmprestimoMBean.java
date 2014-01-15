/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 04/03/2009
 *
 */
package br.ufrn.sigaa.biblioteca.jsf;

import java.util.Collection;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoEmprestimo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoRemoveStatusTipoMaterialETipoEmprestimo;


/**
 * MBean que gerencia a parte de criação e alteração dos tipos de emprétimos existentes no sistema.
 * 
 * @author jadson
 *
 */
@Component("tipoEmprestimo")
@Scope("request")
public class TipoEmprestimoMBean extends SigaaAbstractController <TipoEmprestimo> {

	public TipoEmprestimoMBean(){
		obj = new TipoEmprestimo();
	}
	
	/**
	 * Metodo que remove o objeto, verificando se o mesmo existe
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/TipoEmprestimo/lista.jsp
	 * 
	 */
	@Override
	public String remover() throws ArqException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		obj.setId( getParameterInt("idTipoEmprestimo"));
		
		//populateObj(true);

		GenericDAO dao = getGenericDAO();
		
		obj = dao.refresh(obj);
		dao.detach(obj);

		// Se o objeto a remover foi encontrado, desativa
		if ( obj.isAtivo() ){
			prepareMovimento(SigaaListaComando.REMOVER_STATUS_MATERIAL_TIPO_MATERIAL_E_TIPO_EMPRESTIMO);

			obj.setAtivo(false);

			MovimentoRemoveStatusTipoMaterialETipoEmprestimo mov = new MovimentoRemoveStatusTipoMaterialETipoEmprestimo(obj);
			mov.setCodMovimento(SigaaListaComando.REMOVER_STATUS_MATERIAL_TIPO_MATERIAL_E_TIPO_EMPRESTIMO);
			
			try {
				execute(mov);
				addMensagemInformation("Tipo de Empréstimo removido com sucesso.");
				
				all = null;
				
			} catch (NegocioException ne) {
				ne.printStackTrace();
				addMensagens(ne.getListaMensagens());
			}
			
		} else
			addMensagemErro("Tipo de Empréstimo já foi removido");

		
		return forward(getListPage());
		
	}
	
	
	/**
	 * 
	 * Volta para a página que lista os tipos de emprestimos.
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/TipoEmprestimo/form.jsp
	 *
	 * @return
	 */
	public String voltar(){
		return forward(getListPage());
	}
	
	
	/**
	 * Metodo que evita NullPointerException quando o usuario tenta
	 * alterar um objeto que ja' foi removido
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/TipoEmprestimo/lista.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String preAtualizar () throws ArqException {
		// Tenta pegar o objeto do banco
		populateObj(true);

		prepareMovimento(ArqListaComando.ALTERAR);

		// Se o objeto foi encontrado, exibe a tela de alterar
		if (obj != null){
			if(obj.isAtivo())
				return super.atualizar();
			else{
				addMensagemErro("Tipo de Empréstimo já foi removido.");
				all = null;
				return forward(getListPage());
			}
		}	
			
		// Senao, exibe a mensagem de erro
		obj = new TipoEmprestimo();
		addMensagemErro("Este objeto foi removido.");
		
		listar();
		
		return forward(getListPage());
	}
	
	/**
	 * Método que verifica se já existe um objeto cadastrado com este nome.
	 */
	@Override
	public void beforeCadastrarAfterValidate() throws NegocioException, SegurancaException, DAOException {
		GenericDAO dao = getGenericDAO();
		List<TipoEmprestimo> t = (List<TipoEmprestimo>) dao.findByExactField
						(TipoEmprestimo.class, "descricao", obj.getDescricao());
		
		for (TipoEmprestimo tipoEmprestimo : t) {
			if(tipoEmprestimo.isAtivo() && tipoEmprestimo.getId() != obj.getId())
				throw new NegocioException("Já existe um Tipo de Empréstimo com a mesma descrição.");
		}
			
	}
	
	/**
	 * Metodo que cadastra ou altera um objeto
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/TipoEmprestimo/form.jsp
	 * 
	 */
	@Override
	public String cadastrar() throws ArqException {

		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		try {
			
			// Verifica se o objeto nao foi removido
			if (obj == null){
				addMensagemErro("Este objeto foi removido.");
				return forward(getListPage());
			}
			
			// Valida o objeto
			ListaMensagens lista = obj.validate();
			
			beforeCadastrarAfterValidate();
		
			// Se ocorreram erros, exiba-os e retorne.
			if (lista != null && !lista.isEmpty()){
				addMensagens(lista);
				return forward(getFormPage());
			}
		
			if (!hasErrors()){
				// Prepara o movimento, setando o objeto
				MovimentoCadastro mov = new MovimentoCadastro();
				mov.setObjMovimentado(obj);
				
				
				// Se for operacao de cadastrar, a id do objeto sera' igual a zero
				if (obj.getId() == 0){
					// Seta a operacao como cadastrar
					mov.setCodMovimento(ArqListaComando.CADASTRAR);
					// Tenta executar a operacao
					executeWithoutClosingSession(mov, getCurrentRequest());
					// Prepara o movimento para permitir a insercao de um novo objeto
					prepareMovimento(ArqListaComando.CADASTRAR);
				} else {
					/* Nao era operacao de cadastrar, entao e' de alterar */
					// Seta a operacao como alterar
					mov.setCodMovimento(ArqListaComando.ALTERAR);
					// Tenta executar a operacao
					executeWithoutClosingSession(mov, getCurrentRequest());
				}
	
				// Se chegou aqui, nao houve erros. Exibe a mensagem de sucesso.
				addMessage("Operação realizada com sucesso", TipoMensagemUFRN.INFORMATION);
			}
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
		
		all = null;

		// Retorna para a pagina de listagem.
		return forward(getListPage());
	}
	
	/**
	 * Retorna todos os tipos de empréstimo ATIVOS.
	 * Usado em /biblioteca/TipoEmprestimo/lista.jsp
	 */
	@Override
	public Collection <TipoEmprestimo> getAll () throws DAOException{
		GenericDAO dao = getGenericDAO();
		try {
		if (all == null)
			all = dao.findAllAtivos(TipoEmprestimo.class, "id");
		} finally {
			dao.close();
		}
		
		return all;
	}
	
	/**
	 * Retorna a quantidade de tipos de empréstimos ativos e alteráveis
	 * Usado em /biblioteca/TipoEmprestimo/lista.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public int getSize () throws DAOException{
		return getAll().size();
	}
}
