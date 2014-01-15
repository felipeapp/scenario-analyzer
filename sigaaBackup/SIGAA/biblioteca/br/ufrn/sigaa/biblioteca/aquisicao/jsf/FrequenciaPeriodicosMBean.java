/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 04/01/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.aquisicao.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

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
import br.ufrn.sigaa.biblioteca.aquisicao.negocio.MovimentoRemoveFrequenciaPeriodicos;
import br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.RelatorioTotalPeriodicosPorCNPqMBean;
import br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.RelatorioTotalPeriodicosPorClasseMBean;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FrequenciaPeriodicos;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FrequenciaPeriodicos.UnidadeTempoExpiracao;

/**
 *
 * <p>MBean que gerencia a cria��o, altera��o e remo��o das frequ�ncias de peri�dicos existente no sistema. </p>
 *
 * <p> <i> As frequ�ncias s�o usada para determinar os peri�dicos corrente e n�o correntes dos relat�rios da biblioteca.</i> </p>
 * 
 * @author jadson
 * @see RelatorioTotalPeriodicosPorCNPqMBean
 * @see RelatorioTotalPeriodicosPorClasseMBean
 */
@Component("frequenciaPeriodicosMBean")
@Scope("request")
public class FrequenciaPeriodicosMBean extends SigaaAbstractController <FrequenciaPeriodicos> {

	
	/**
	 * P�gina para confirmar a remo��o da frenqu�ncia de peri�dico, e passar as assinaturas associadas a essa frequ�ncia para outra frequ�ncia ativa no sistema.
	 */
	public static final String PAGINA_CONFIRMA_REMOCAO_FREQUENCIA_PERIODICO = "/biblioteca/FrequenciaPeriodicos/confirmaRemocaoFrequenciaPeriodicos.jsp";
	
	/**
	 * Cont�m a lista de frequencias ativas, para o usu�rio escolher qual a frequ�ncia que as assinaturas removidas v�o passar a possuir.
	 */
	private List<FrequenciaPeriodicos> frequenciasAtivas;
	
	/**
	 * A frequ�ncia que vai substituir a frequ�ncia a ser removida.
	 */
	private FrequenciaPeriodicos frequenciaParaMigraAssinaturas = new FrequenciaPeriodicos(-1);
	
	
	/** Utilizando para guardar o valor da unidade do tipo de expira��o escolhido pelo usu�rio, j� que o JSF n�o intera sobre enums */
	private int valorUnidadeTempoExpiracao = FrequenciaPeriodicos.UnidadeTempoExpiracao.MESES.ordinal();
	
	
	public FrequenciaPeriodicosMBean(){
		obj = new FrequenciaPeriodicos();
	}
	
	
	/**
	 * Sobre escreve o m�todo da classe pai pra zerar os dados do objeto antes de cadatrar, porque o mesmo objeto � usado na altera��o.
	 * 
	 * * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/FrequenciaPeriodicos/lista.jsp
	 * @return
	 * @throws ArqException
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		obj = new FrequenciaPeriodicos();
		return super.preCadastrar();
	}



	/**
	 * <p>Metodo que sobre escreve o m�todo da classe pai para evitar NullPointerException quando o usuario tenta
	 * alterar um objeto que j� foi removido</p>
	 * 
	 * * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/FrequenciaPeriodicos/lista.jsp
	 * @return
	 * @throws ArqException
	 */
	public String preAtualizar () throws ArqException {
		
		// Tenta pegar o objeto do banco
		populateObj(true);

		valorUnidadeTempoExpiracao = obj.getValorUnidadeTempoExpiracao();
		obj.converteTempoExpiracaoSalvo();
		
		prepareMovimento(ArqListaComando.ALTERAR);

		// Se o objeto foi encontrado, exibe a tela de alterar
		if (obj != null)
			return super.atualizar();

		// Senao, exibe a mensagem de erro
		obj = new FrequenciaPeriodicos();
		addMensagemErro("Essa frequ�ncia j� foi removida.");
		return forward(getListPage());
	}
	
	
	/**
	 * M�todo que cadastra ou altera uma frequ�ncia.
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/FrequenciaPeriodicos/form.jsp
	 */
	@Override
	public String cadastrar() throws ArqException {

		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO );
		
		// Verifica se o objeto nao foi removido
		if (obj == null || ! obj.isAtivo()){
			addMensagemErro("Essa Periodicidade j� foi removida.");
			return forward(getListPage());
		}
		
		obj.configuraUnidadeTempoExpriracao(valorUnidadeTempoExpiracao);
		obj.converteTempoExpiracaoInformatoUsuario();
		
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
			addMessage("Opera��o Realizada com sucesso", TipoMensagemUFRN.INFORMATION);
			
			all = null;
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}

		// Retorna para a pagina de listagem.
		return forward(getListPage());
	}
	
	
	/**
	 * Metodo que redireciona para a p�gina onde o usu�rio vai confirmar remo��o da frequ�ncia e  escolher a nova frequ�ncia 
	 * que as assinturas que possu�am a frequ�ncia antiga teram
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/FrequenciaPeriodicos/lista.jsp
	 * @throws SegurancaException 
	 * @throws SegurancaException 
	 */
	@Override
	public String preRemover()   {
		
		try {
		
			prepareMovimento(SigaaListaComando.REMOVER_FREQUENCIA_PERIODICO);
			GenericDAO dao = null;
			try{
				dao = getGenericDAO();
				frequenciasAtivas = (List <FrequenciaPeriodicos>) getGenericDAO().findAllAtivos(FrequenciaPeriodicos.class, new String[]{"unidadeTempoExpiracao", "tempoExpiracao", "descricao"});
			
				for (FrequenciaPeriodicos frequencias : frequenciasAtivas) {
					frequencias.converteTempoExpiracaoSalvo();
				}
			
			}finally{
				if (dao != null)  dao.close();
			}
			
			populateObj(true); // busca o objeto com o id passado com par�metro
			
			frequenciaParaMigraAssinaturas = new FrequenciaPeriodicos(-1);
			
			return forward(PAGINA_CONFIRMA_REMOCAO_FREQUENCIA_PERIODICO);
			
		} catch (DAOException e) {
			addMensagemErro("Erros ao buscar as frequ�ncias ativos no sistema");
			return null;
		} catch (ArqException e) {
			addMensagemErro("Erros ao tentar remover a frequ�ncias");
			return null;
		}
	}
	
	
	/**
	 * Metodo que remove o objeto, verificando se o mesmo existe
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/FrequenciaPeriodicos/confirmaRemocaoFrequenciaPeriodicos.jsp
	 */
	@Override
	public String remover() throws ArqException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO);

		GenericDAO dao = null; 
		

		try {
			
			dao = getGenericDAO();
			
			// Se o objeto a remover foi encontrado, desativa
			if (obj != null && obj.isAtivo()){
				
				MovimentoRemoveFrequenciaPeriodicos mov = new MovimentoRemoveFrequenciaPeriodicos(obj, frequenciaParaMigraAssinaturas);
				mov.setCodMovimento(SigaaListaComando.REMOVER_FREQUENCIA_PERIODICO);
				execute(mov);
				
				all = null;
				frequenciaParaMigraAssinaturas = getGenericDAO().findByPrimaryKey(frequenciaParaMigraAssinaturas.getId(), FrequenciaPeriodicos.class, new String[]{"id", "descricao"});
				addMensagemInformation("Frequ�ncia removida com sucesso. Assinatura migradas para a frequ�ncia: "+frequenciaParaMigraAssinaturas.getDescricao());
				
				frequenciaParaMigraAssinaturas = null;
				
				return forward(getListPage());
			} else
				addMensagemErro("Esta frequ�ncia j� foi removida");

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
	 * Diz se est� cadastrando ou alterando os dados de uma periodicidade.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/FrequenciaPeriodicos/form.jsp
	 *
	 * @return
	 */
	public boolean isCadastrando(){
		if("Cadastrar".equalsIgnoreCase(getConfirmButton()))
			return true;
		else
			return false;
	}
	
	
	/**
	 * 
	 * Volta para a p�gina que lista as frequ�ncias.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/FrequenciaPeriodicos/form.jsp
	 *
	 * @return
	 */
	public String voltar(){
		return forward(getListPage());
	}
	
	
	/**
	 * Retorna todas as colecao ativas que pode ser alter�veis, ordenadas pela descricao
	 * Chamado na pagina: /sigaa.war/biblioteca/FrequenciaPeriodicos/lista.jsp
	 */
	@Override
	public Collection <FrequenciaPeriodicos> getAll() throws DAOException{
		
		if (all == null){
			
			GenericDAO dao = null;
			
			try{
				dao = getGenericDAO();
				
				all = dao.findAllAtivos(FrequenciaPeriodicos.class, new String[]{"unidadeTempoExpiracao", "tempoExpiracao", "descricao"});
			
				for (FrequenciaPeriodicos frequencias : all) {
					frequencias.converteTempoExpiracaoSalvo();
				}
				
			}finally{
			  if (dao != null)  dao.close();
			}
		}
		return all;
	}

	
	/**
	 * <p>Retorna a quantidade de frequ�ncias cadastradas.</p>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public int getSize () throws ArqException{
		// limpa a lista para que seja atualizada.
		return getAll().size();
	}

	/**
	 *  Retorna as frequ�ncias ativas para usar no campo box.
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/FrequenciaPeriodicos/confirmaRemocaoFrequenciaPeriodicos.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public  Collection <SelectItem> getFrequenciasAtivasComboBox() {
		return toSelectItems(frequenciasAtivas, "id", "descricaoCompleta");
		
	}

	/**
	 *  Retorna as frequ�ncias ativas para usar no campo box.
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/FrequenciaPeriodicos/form.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public  Collection <SelectItem> getUnidadesTempoExpiracaoComboBox() {
		
		Collection <SelectItem> temp = new ArrayList<SelectItem>();
		temp.add( new SelectItem(UnidadeTempoExpiracao.MESES.ordinal(), UnidadeTempoExpiracao.MESES.toString()));
		temp.add( new SelectItem(UnidadeTempoExpiracao.ANOS.ordinal(), UnidadeTempoExpiracao.ANOS.toString()));
		return temp;
		
	}

	public int getValorUnidadeTempoExpiracao() {
		return valorUnidadeTempoExpiracao;
	}

	public void setValorUnidadeTempoExpiracao(int valorUnidadeTempoExpiracao) {
		this.valorUnidadeTempoExpiracao = valorUnidadeTempoExpiracao;
	}

	public FrequenciaPeriodicos getFrequenciaParaMigraAssinaturas() {
		return frequenciaParaMigraAssinaturas;
	}

	public void setFrequenciaParaMigraAssinaturas(FrequenciaPeriodicos frequenciaParaMigraAssinaturas) {
		this.frequenciaParaMigraAssinaturas = frequenciaParaMigraAssinaturas;
	}
	
}
