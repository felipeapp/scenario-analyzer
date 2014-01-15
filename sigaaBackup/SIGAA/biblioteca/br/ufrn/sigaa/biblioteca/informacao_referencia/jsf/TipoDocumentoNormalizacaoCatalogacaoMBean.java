/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Oct 7, 2008
 *
 */
package br.ufrn.sigaa.biblioteca.informacao_referencia.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.TipoDocumentoNormalizacaoCatalogacao;

/**
 * Tipo de documento utilizado na solicitação de normalização e catalogação na biblioteca.
 * @author Victor Hugo
 */
@Component("tipoDocumentoNormalizacaoCatalogacaoMBean") 
@Scope("request")
public class TipoDocumentoNormalizacaoCatalogacaoMBean extends SigaaAbstractController<TipoDocumentoNormalizacaoCatalogacao> {

	/**
	 * Construtor padrão
	 */
	public TipoDocumentoNormalizacaoCatalogacaoMBean() {
		obj = new TipoDocumentoNormalizacaoCatalogacao();
	}

	
	/**
	 * Limpa os dados do objeto antes de cadatrar um novo.<br/>
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/TipoDocumentoNormalizacaoCatalogacao/form.jsp
	 *
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#beforeCadastrarAndValidate()
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		checkChangeRole();
		prepareMovimento(ArqListaComando.CADASTRAR);
		setConfirmButton("Cadastrar");
		obj = new TipoDocumentoNormalizacaoCatalogacao();
		return forward(getFormPage());
	}
	
	
	/**
	 * Metodo que cadastra ou altera um tipo de documento de ormalização e catalogação na biblioteca.
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/TipoDocumentoNormalizacaoCatalogacao/form.jsp
	 */
	@Override
	public String cadastrar() throws ArqException {

		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		// Verifica se o objeto nao foi removido
		if (obj == null){
			addMensagemErro("Esse Tipo de Documento já foi removido.");
			return forward(getListPage());
		}
		
		// Valida o objeto
		ListaMensagens lista = obj.validate();
	
		// Se ocorreram erros, exiba-os e retorne.
		if (lista != null && !lista.isEmpty()){
			addMensagens(lista);
			return forward(getFormPage());
		}
	
		// Prepara o movimento, setando o objeto
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);

		try {
			// Se for operacao de cadastrar, a id do objeto sera' igual a zero
			if (obj.getId() == 0){
				// Seta a operacao como cadastrar
				mov.setCodMovimento(ArqListaComando.CADASTRAR);
				// Tenta executar a operacao
				execute(mov);
				// Prepara o movimento para permitir a insercao de um novo objeto
				prepareMovimento(ArqListaComando.CADASTRAR);
			} else {
				/* Nao era operacao de cadastrar, entao e' de alterar */
				// Seta a operacao como alterar
				mov.setCodMovimento(ArqListaComando.ALTERAR);
				// Tenta executar a operacao
				execute(mov);
			}

			all = null; //  atualiza a listagem
			
			// Se chegou aqui, nao houve erros. Exibe a mensagem de sucesso.
			addMensagemInformation("Operação Realizada com sucesso.");
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}

		// Retorna para a pagina de listagem.
		return forward(getListPage());
	}
	
	
	/**
	 * Metodo que evita NullPointerException quando o usuario tenta
	 * alterar um objeto que ja' foi removido
	 * 
	 * * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/TipoDocumentoNormalizacaoCatalogacao/lista.jsp
	 * @return
	 * @throws ArqException
	 */
	public String preAtualizar () throws ArqException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		obj = new TipoDocumentoNormalizacaoCatalogacao();

		prepareMovimento(ArqListaComando.ALTERAR);

		// Se o objeto foi encontrado, exibe a tela de alterar
		// Obs.: O método atualizar popula o objeto usando o parâmetro "id"
		if (obj != null)
			return super.atualizar();

		// Senao, exibe a mensagem de erro
		obj = new TipoDocumentoNormalizacaoCatalogacao();
		addMensagemErro("Esse Tipo de Documento já foi removido.");
		return forward(getListPage());
	}
	
	
	
	
	/**
	 * Metodo que remove o objeto, verificando se o mesmo existe
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/FormaDocumento/confirmaRemocaoColecao.jsp
	 */
	@Override
	public String remover() throws ArqException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);

		prepareMovimento(ArqListaComando.ALTERAR);
		
		GenericDAO dao = null; 
		
		try {
			
			dao = getGenericDAO();
		
			obj = new TipoDocumentoNormalizacaoCatalogacao();
			obj.setId(getParameterInt("id", 0));
			
			populateObj(); // Popula com os dados no banco para pode validar e atualizar com os dados not null
			
			// Se o objeto a remover foi encontrado, desativa
			if (obj != null && obj.isAtivo()){
				
				obj.setAtivo(false);
				MovimentoCadastro mov = new MovimentoCadastro();
				mov.setCodMovimento(ArqListaComando.ALTERAR);
				mov.setObjMovimentado(obj);
				execute(mov);
				
				all = null;
				addMensagemInformation("Tipo de Documento removido com sucesso. ");
				
				return forward(getListPage());
			} else
				addMensagemErro("Esse Tipo de Documento já foi removido.");

		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		}finally{
			if(dao != null ) dao.close();
		}
		
		
		all = null;

		return forward(getListPage());
	}
	
	
	
	
	
	
	/**
	 * 
	 * Volta para a página que lista as coleções.
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/TipoDocumentoNormalizacaoCatalogacao/form.jsp
	 *
	 * @return
	 */
	public String voltar(){
		return forward(getListPage());
	}
	
	
	/**
	 * Retorna todas as colecao ativas que pode ser alteráveis, ordenadas pela descricao
	 * Chamado na pagina sigaa.war/biblioteca/biblioteca/lista.jsp
	 */
	@Override
	public Collection <TipoDocumentoNormalizacaoCatalogacao> getAll() throws DAOException{
		
		if (all == null)
			all = getGenericDAO().findAllAtivos(TipoDocumentoNormalizacaoCatalogacao.class, "denominacao");
		
		return all;
	}
	
	
	/**
	 * Retorn os tipos de documentos ativos em forma de selectItem para usar nos combo box.
	 * 
	 * Chamado na pagina sigaa.war/biblioteca/informaao_referenci/normalizacao_catalogacao/novaSolcitacaoCatalogacao.jsp
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getAllCombo() throws DAOException{
		
		if(all == null){
			getAll();
		}
		return toSelectItems(all, "id", "denominacao");
	}
	
	
	
	/**
	 * Metodo que retorna a quantidade de tipos de documentos cadastrados
	 * Chamado na pagina /biblioteca/biblioteca/lista.jsp
	 * @return
	 * @throws ArqException
	 */
	public int getSize () throws ArqException{
		if(all == null)
			return 0;
		else
			return all.size();
	}
	
	
	
}
