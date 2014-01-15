/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 19/10/2011
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ConfiguracaoAmbienteException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.util.VerificaSituacaoUsuarioBibliotecaUtil;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dao.JubilamentoQuery;
import br.ufrn.sigaa.ensino.dominio.TipoJubilamento;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoCancelamentoAutomatico;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * MBean utilizado para o gerenciamento de jubilamento de discentes por abandono de curso e prazo máximo.
 * 
 */
@Component("jubilamentoMBean") @Scope("session")
public class JubilamentoMBean extends SigaaAbstractController<Object> {	
	
	/**Utilizados como parâmetro da busca a ser realizada.*/	 
	private  List<Integer[]> anosPeriodo; 
	/**Utilizados como parâmetro da busca a ser realizada.*/
	private  Boolean ead; 
	/**Utilizados como parâmetro da busca a ser realizada.*/
	private  Boolean filtroMatriculados; 
	/**Utilizados como parâmetro da busca a ser realizada.*/
	private  Unidade unidade;
	/**Utilizados para armazenar a consulta a ser realizada.*/
	private  JubilamentoQuery jubilamentoQuery;
	/**Utilizado para armazenar o id da busca a ser realizada.*/
	private  Integer tipoJubilamento;
	/**Utilizado para armazenar o tipo de jubilamento atualmente em uso.*/
	private  TipoJubilamento tipoJub;	
	/** Indica o ano de referência escolhido */
	private Integer ano;
	/** Indica o período de referência escolhido */
	private Integer periodo;
	/**Utilizado para armazenar os discentes selecionados para jubilamento de acordo com o tipo de jubilamento em uso*/
	private Collection<Discente> discentes;	
	/** Lista de discentes com pendencias na biblioteca*/
	private Collection<Discente> discentesComPendencias;
	/**Utilizado para armazenar os discentes passíveis de jubilamento de acordo com o tipo de jubilamento em uso*/
	private Collection<Discente> discentesPassiveisJubilamento;
	/** Se pode ignorar as pendências da biblioteca - atualmente não*/
	private boolean ignorarPendencia = false;
	/** Mensagem de Observação no histórico dos discentes */
	private String observacoes;

	private Integer anoSaida;
	private Integer periodoSaida;
	
	/**
	 * Construtor padrão.
	 */
	public JubilamentoMBean() {
		init();
	}
	
	/**
	 * Inicializa atributos do mbean.
	 */
	private void init() {
		anosPeriodo = new ArrayList<Integer[]>();
		ead = false;
		filtroMatriculados = false;
		unidade = new Unidade();
		discentes = new ArrayList<Discente>();
		discentesComPendencias = new ArrayList<Discente>();
		tipoJub = new TipoJubilamento();		
		jubilamentoQuery = null;
		tipoJubilamento = null;
		
		ano = null;
		periodo = null;
		anoSaida = null;
		periodoSaida = null;
		observacoes = null;
	}
	
	/**
	 * Ponto de partida do caso de uso.
	 * 
	 * Chamado por:
	 * sigaa.war/graduacao/menus/administracao.jsp
	 * sigaa.war/stricto/menus/discente.jsp
	 * sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/discente.jsp
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String iniciar() throws DAOException, SegurancaException {
		init();
		verificarPermissoes();
		return iniciar(getCalendarioInicial());
	}
	
	/**
	 * Inicializa informações que aparecerão na view.
	 * @param cal
	 * @return
	 */
	private String iniciar(CalendarioAcademico cal){
		ano = cal.getAno();
		periodo = cal.getPeriodo();
		anosPeriodo = new ArrayList<Integer[]>();		
		return telaInicial();
	}
	
	/**
	 * Redireciona para a página inicial do caso de uso.
	 * 
	 * Chamado por:
	 * sigaa.war/ensino/movimentacao_aluno/lista_passiveis_jubilamento.jsp
	 * 
	 * @return
	 */
	public String telaInicial(){		
		return forward("/ensino/movimentacao_aluno/parametros_cancelamento.jsp");
	}
	
	/**
	 * Redireciona para a tela de discentes com pendência.
	 * 
	 * Chamado por:
	 * Não é invocado por JSP;
	 * @return
	 */
	public String telaPendentes(){
		return forward("/ensino/movimentacao_aluno/discentes_pendencia_abandono.jsp");
	}
	
	/**
	 * Redireciona para a tela de confirmação de jubilamento.
	 * 
	 * Chamado por:
	 * Não é invocado por JSP;
	 * @return
	 */
	public String telaConfirmar(){
		return forward("/ensino/movimentacao_aluno/confirmar_jubilamento.jsp");
	}
	
	/**
	 * Redireciona para a tela de discentes com pendência.
	 * 
	 *  Chamado por:
	 * Não é invocado por JSP;
	 * @return
	 */
	public String telaDiscentesPassiveisJubilamento(){
		return forward("/ensino/movimentacao_aluno/lista_passiveis_jubilamento.jsp");
	}
	
	
	
	/**
	 * 
	 * Verifica permissões de acordo com o nível de ensino.
	 * 
	 * @throws SegurancaException
	 */
	private void verificarPermissoes() throws SegurancaException {
		if(getNivelEnsino() == NivelEnsino.GRADUACAO)
			checkRole(SigaaPapeis.ADMINISTRADOR_DAE);
		if(getNivelEnsino() == NivelEnsino.TECNICO)
			checkRole(SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO, SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR);
		if(getNivelEnsino() == NivelEnsino.STRICTO)
			checkRole(SigaaPapeis.PPG, SigaaPapeis.ADMINISTRADOR_STRICTO);
	}

	/**
	 * 
	 * Pega o calendário de acordo com o nível de ensino.
	 * 
	 * @return
	 * @throws DAOException
	 */
	private CalendarioAcademico getCalendarioInicial() throws DAOException {
		if(getNivelEnsino() == NivelEnsino.GRADUACAO)
			return CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();
		if(getNivelEnsino() == NivelEnsino.TECNICO)
			return CalendarioAcademicoHelper.getCalendario(getParametrosAcademicos());
		if(getNivelEnsino() == NivelEnsino.STRICTO) {
			return getCalendarioVigente();
		} 
		if (getNivelEnsino() == NivelEnsino.FORMACAO_COMPLEMENTAR)
			return CalendarioAcademicoHelper.getCalendario(getParametrosAcademicos());
		return null;
	}	
	
	/**
	 * 
	 * Utilizado para adicionar em um combo as opções de jubilamento cadastradas no banco de dados.
	 * 
	 * Chamado por:
	 * sigaa.war/ensino/movimentacao_aluno/parametros_cancelamento.jsp 
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllTiposJulilamento() throws DAOException {
		Collection<TipoJubilamento> tipos;
		if ( getNivelEnsino() == NivelEnsino.TECNICO )
			tipos = getGenericDAO().findByExactField(TipoJubilamento.class, 
					new String[]{"nivel", "tipoMovimentacao.id"}, new Object[]{getNivelEnsino(), TipoMovimentacaoAluno.ABANDONO});
		else 
			tipos = getGenericDAO().findByExactField(TipoJubilamento.class, "nivel", getNivelEnsino());
		
		if(ValidatorUtil.isEmpty(tipos)) {
			throw new ConfiguracaoAmbienteException("Não há tipos de jubilamento cadastrados para este nível de ensino. Por favor entre em contato com a administração do sistema.");
		}		
		return toSelectItems(tipos,"id", "nome");	
	}	
	
	/**
	 * Método utilizado encontrar os alunos passíveis de jubilamento de acordo com o tipo de jubilamento
	 * selecionado no form.
	 * 
	 * Chamado por:
	 * sigaa.war/ensino/movimentacao_aluno/parametros_cancelamento.jsp
	 * 
	 * 
	 */
	public String listar() throws ArqException {		
		
		if (tipoJubilamento == null){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Tipo de Jubilamento");
		}
				
		if (isEmpty(anosPeriodo) && tipoJub.getTipoMovimentacao().isAbandono()) {
			addMensagemErro("Adicione pelo menos um ano-período para gerar o relatório.");
		}
		
		if ((isEmpty(ano) || isEmpty(periodo)) 
			&& (tipoJub.getTipoMovimentacao().getId() == TipoMovimentacaoAluno.PRAZO_MAXIMO 
			|| tipoJub.getTipoMovimentacao().getId() == TipoMovimentacaoAluno.NAO_CONFIRMACAO_VINCULO)) {
			addMensagemErro("Adicione pelo menos um período de referência para executar a busca.");
		}
		
		if (!(isEmpty(ano) || isEmpty(periodo)) 
			&& (tipoJub.getTipoMovimentacao().getId() == TipoMovimentacaoAluno.PRAZO_MAXIMO 
			|| tipoJub.getTipoMovimentacao().getId() == TipoMovimentacaoAluno.NAO_CONFIRMACAO_VINCULO)) {
			Integer[] anoPeriodo = {ano,periodo};
			anosPeriodo = new ArrayList<Integer[]>();
			anosPeriodo.add(anoPeriodo);
		}
		
		if (anoSaida == null || anoSaida == 0)
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano de Saída");
		if (periodoSaida == null || periodoSaida == 0)
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Período de Saída");
		
		if (hasErrors())
			return null;
		
		verificarPermissoes();
		carregarQuery(tipoJubilamento);		
		setOperacaoAtiva(SigaaListaComando.JUBILAR_DISCENTE.getId());
		
		//o nível de graduação define dois tipos de afastamento específicos 
		if (getNivelEnsino() == NivelEnsino.GRADUACAO) {
			tipoJub = getGenericDAO().findByPrimaryKey(tipoJubilamento, TipoJubilamento.class);
			if (tipoJub.getTipoMovimentacao().getId() == TipoMovimentacaoAluno.ABANDONO_NENHUMA_INTEGRALIZACAO)
				filtroMatriculados = true;
			else filtroMatriculados = false;
		}
					
		discentes = jubilamentoQuery.findAlunosPassiveisJubilamento(anosPeriodo, ead, filtroMatriculados, getUnidadeQuery(), null);		
		
		for (Discente d : discentes){
			if(	VerificaSituacaoUsuarioBibliotecaUtil.temPendencia(d) )
				d.setMarcado(true);
			
		}
		
		if (isEmpty(discentes)) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}		

		return forward("/ensino/movimentacao_aluno/lista_passiveis_jubilamento.jsp");
	}
	
	
	/** 
	 *  Ao trocar o tipo de jubilamento na view, a consulta de quais alunos estão passíveis de jubilamento
	 *  deve ser recarregada.
	 *  
	 *  Chamado por:
	 *  sigaa.war/ensino/movimentacao_aluno/parametros_cancelamento.jsp
	 *  
	 * @param e
	 * @throws DAOException
	 */
	public void trocarTipoJubilamento(ValueChangeEvent e) throws DAOException {
		int id = (Integer)(e.getNewValue());
		tipoJubilamento = id;
		carregarQuery(id);
		anosPeriodo = new ArrayList<Integer[]>();
		redirectMesmaPagina();
	}
	
	/**
	 * Utilizado na view para decidir qual formulário apresentar ao usuário
	 * 
	 * Chamado por:
	 * sigaa.war/ensino/movimentacao_aluno/parametros_cancelamento.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public boolean isJubilamentoPrazoMaximo() throws DAOException {		
		inicializarTipoJubilamento();		
		return tipoJub.getTipoMovimentacao().getId() == TipoMovimentacaoAluno.PRAZO_MAXIMO;
	}
	
	/**
	 * Utilizado na view para decidir qual formulário apresentar ao usuário
	 * 
	 * Chamado por:
	 * sigaa.war/ensino/movimentacao_aluno/parametros_cancelamento.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public boolean isJubilamentoNaoConfirmacaoVinculo() throws DAOException {		
		inicializarTipoJubilamento();		
		return tipoJub.getTipoMovimentacao().getId() == TipoMovimentacaoAluno.NAO_CONFIRMACAO_VINCULO;
	}
	
	/**
	 * Utilizado na view para decidir qual formulário apresentar ao usuário
	 * 
	 * Chamado por:
	 * sigaa.war/ensino/movimentacao_aluno/parametros_cancelamento.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public boolean isJubilamentoAbandono() throws DAOException {		
		inicializarTipoJubilamento();		
		return tipoJub.getTipoMovimentacao().isAbandono();
	}
	
	
	/**
	 * Inicializa o tipo de jubilamento de acordo com um nível de ensino 
	 * para que seja possível executar consulta para verificar
	 * quais alunos são passíveis de jubilamento.
	 * @throws DAOException
	 */
	private void inicializarTipoJubilamento() throws DAOException {
		if(ValidatorUtil.isEmpty(tipoJub)) {
			Collection<TipoJubilamento> tipos = getGenericDAO().findByExactField(TipoJubilamento.class, "nivel", getNivelEnsino());
			if(ValidatorUtil.isEmpty(tipos)) {
				throw new ConfiguracaoAmbienteException("Não há tipos de jubilamento cadastrados para este nível de ensino, por favor entre em contato com a administração do sistema.");
			} else {
				tipoJub = tipos.iterator().next();
			} 
		}
	}
	
	/**
	 * 
	 * Prepara para o cancelamento dos alunos.
	 * 
	 * Chamado por:
	 * sigaa.war/ensino/movimentacao_aluno/lista_passiveis_jubilamento.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String cancelarAlunos() throws ArqException {

		if( !checkOperacaoAtiva(SigaaListaComando.JUBILAR_DISCENTE.getId())) {
			return cancelar();
		}
		
		try {
			ArrayList<Discente> discentesEscolhidos = new ArrayList<Discente>();
			for (Discente d : discentes) {
				if (d.isSelecionado())
					discentesEscolhidos.add(d);
			}
			
			if (isEmpty(discentesEscolhidos)){
				addMensagemErro("Nenhum discente escolhido.");
				return null;
			}
			
			discentesComPendencias = new ArrayList<Discente>(); 
			discentesPassiveisJubilamento = new ArrayList<Discente>(); 
			StringBuffer nomes = new StringBuffer();
			
			for (Iterator<Discente> it = discentesEscolhidos.iterator(); it.hasNext();) {
				Discente d = it.next();
				if(	VerificaSituacaoUsuarioBibliotecaUtil.temPendencia(d) ){ 			
					discentesComPendencias.add(d);
					
					nomes.append( d.getMatriculaNome() );
					if( it.hasNext() )
						nomes.append( ", <br/>" );
				}
				discentesPassiveisJubilamento.add(d);
			}
			
			/**
			 * verificando os discentes com pendencias na biblioteca.
			 * Caso o usuário logado seja ADMINISTRADOR_DAE ele pode cancelar os discentes, caso contrário não poderá.
			 */
			boolean ignorarPendenciasParam = getParameterBoolean("ignoraPendenciasAdmDae");
			if( !getUsuarioLogado().isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE) && !isEmpty(discentesComPendencias) ){
				addMensagemErro("Caro Usuário, os discentes listados abaixo possuem empréstimos ativos na biblioteca e por isso não podem ter o vínculo cancelado: <br/>"+ nomes.toString() );
			} else if( getUsuarioLogado().isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE) && !isEmpty(discentesComPendencias) && !ignorarPendenciasParam ){
				return telaPendentes();
			} else if( getUsuarioLogado().isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE) && !isEmpty(discentesComPendencias) && ignorarPendenciasParam ){
				ignorarPendencia = true;
			}			
			
						
			prepareMovimento(SigaaListaComando.JUBILAR_DISCENTE);			

		} catch (Exception e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErroPadrao();
		}

		return telaConfirmar();

	}	
	
	/**
	 * 
	 * Prepara para o cancelamento dos alunos sem pendecia.
	 * 
	 * Chamado por:
	 * sigaa.war/ensino/movimentacao_aluno/lista_passiveis_jubilamento.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String cancelarAlunosSemPendencia() throws ArqException {

		if( !checkOperacaoAtiva(SigaaListaComando.JUBILAR_DISCENTE.getId())) {
			return cancelar();
		}
		
		try {
			ArrayList<Discente> discentesEscolhidos = new ArrayList<Discente>();
			for (Discente d : discentes) {
				if (d.isSelecionado())
					discentesEscolhidos.add(d);
			}
			
			if (isEmpty(discentesEscolhidos)){
				addMensagemErro("Nenhum discente escolhido.");
				return null;
			}
			
			discentesComPendencias = new ArrayList<Discente>(); 
			discentesPassiveisJubilamento = new ArrayList<Discente>(); 
			StringBuffer nomes = new StringBuffer();
			
			// Adiciona aos passíveis de jubilamento apenas aqueles sem pendência.
			for (Iterator<Discente> it = discentesEscolhidos.iterator(); it.hasNext();) {
				Discente d = it.next();
				if(	VerificaSituacaoUsuarioBibliotecaUtil.temPendencia(d) ){ 			
					discentesComPendencias.add(d);
					
					nomes.append( d.getMatriculaNome() );
					if( it.hasNext() )
						nomes.append( ", <br/>" );
				} else
					discentesPassiveisJubilamento.add(d);
			}
			
			if (!isEmpty(nomes.toString()))
				addMensagemWarning("Caro Usuário, os discentes listados não foram selecionados pois possuem empréstimos ativos na biblioteca: <br/>"+ nomes.toString() );
			else
				addMensagemWarning("Nenhum aluno selecionado possui pendência na biblioteca.");

			prepareMovimento(SigaaListaComando.JUBILAR_DISCENTE);			

		} catch (Exception e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErroPadrao();
		}

		return telaConfirmar();

	}	
	
	/**
	 * 
	 * Executa o cancelamento dos alunos.
	 * 
	 * Chamado por:
	 * sigaa.war/ensino/movimentacao_aluno/lista_passiveis_jubilamento.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String confirmar() throws ArqException {

		if( !checkOperacaoAtiva(SigaaListaComando.JUBILAR_DISCENTE.getId())) {
			return cancelar();
		}
		
		try {

			CalendarioAcademico cal = getCalendarioVigente();

			if (anoSaida == null)
				anoSaida = cal.getAno();
			
			if (periodoSaida == null)
				periodoSaida = cal.getPeriodo();
			
			MovimentoCancelamentoAutomatico cancelamento = new MovimentoCancelamentoAutomatico();
			
			for ( Discente d : discentesPassiveisJubilamento ){
				List<Discente> discente = new ArrayList<Discente>();
				discente.add(d);
				prepareMovimento(SigaaListaComando.JUBILAR_DISCENTE);			
				cancelamento.setIgnorarPendencias(ignorarPendencia);
				cancelamento.setCodMovimento(SigaaListaComando.JUBILAR_DISCENTE);			
				cancelamento.setTipoMovimentacao(tipoJub.getTipoMovimentacao());
				cancelamento.setAno(anoSaida);
				cancelamento.setPeriodo(periodoSaida);
				cancelamento.setObservacoes(observacoes);
				cancelamento.setDiscentes(discente);
				execute(cancelamento);
			}
			
			String msg = "";
			if (discentesPassiveisJubilamento.size() > 1)
				msg = " discentes cancelados com sucesso";
			else
				msg = " discente cancelado com sucesso";
			
			addMessage(discentesPassiveisJubilamento.size() + msg, TipoMensagemUFRN.INFORMATION);
			setOperacaoAtiva(null);

		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
		} catch (Exception e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErroPadrao();
		}

		iniciar(getCalendarioVigente());
		return cancelar();

	}	
	
	/**
	 * Retorna a unidade a ser utilizada na consulta dos discentes passíveis de jubilamento.
	 * @return
	 * @throws ArqException
	 */
	private Unidade getUnidadeQuery() throws ArqException {
		if(getNivelEnsino() == NivelEnsino.GRADUACAO){ 
			return null;
		} else if(getNivelEnsino() == NivelEnsino.TECNICO){
			return new Unidade(getUnidadeGestora());
		} else if(getNivelEnsino() == NivelEnsino.STRICTO) {
			return unidade;
		} else if(getNivelEnsino() == NivelEnsino.FORMACAO_COMPLEMENTAR){
			return new Unidade(getUnidadeGestora());
		}	
		return null;
	}
	
	/**
	 * Retorna o calendário a ser utilizado na consulta dos discentes passíveis de jubilamento ou em validações sobre o ano-período inserido 
	 * para o jubilamento.
	 * @return
	 * @throws DAOException
	 */
	private CalendarioAcademico getCalendarioAcademicoQuery() throws DAOException {
		if(getNivelEnsino() == NivelEnsino.GRADUACAO){ 
			return CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();
		}else if(getNivelEnsino() == NivelEnsino.TECNICO){
			return CalendarioAcademicoHelper.getCalendario(getParametrosAcademicos());
		}else if(getNivelEnsino() == NivelEnsino.STRICTO) {
			return getCalendarioVigente();
		} else if (getNivelEnsino() == NivelEnsino.FORMACAO_COMPLEMENTAR)
			return CalendarioAcademicoHelper.getCalendario(getParametrosAcademicos());
		return null;
	}
	
	
	/**
	 * Utilizado para carregar a consulta a ser executada. 
	 * @param id
	 * @throws DAOException
	 */
	private void carregarQuery(Integer id) throws DAOException {
		tipoJub = getGenericDAO().findByPrimaryKey(id, TipoJubilamento.class);
		try {			
			jubilamentoQuery = (JubilamentoQuery) ReflectionUtils.newInstance(tipoJub.getClasseConsulta());			
		} catch (Exception e) {
			throw new ConfiguracaoAmbienteException("Não foi possível carregar a classe de consulta para este tipo de jubilamento. Por favor entre em contato com a administração do sistema.");
		}
	}
	
	/**
	 * Utilizado na view para verificar se o período da última matrícula valida dos discente será exibida
	 * 
	 * Chamado por:
	 * sigaa.war/ensino/movimentacao_aluno/lista_passiveis_jubilamento.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public boolean isMostrarUltimaMatriculaValida() {
		return tipoJub.getTipoMovimentacao() != null && tipoJub.getTipoMovimentacao().isAbandono() && getNivelEnsino() != NivelEnsino.TECNICO;
	}
	
	/**
	 * Utilizado na view para verificar se o prazo de conclusão será exibido na listagem de discentes
	 * 
	 * Chamado por:
	 * sigaa.war/ensino/movimentacao_aluno/lista_passiveis_jubilamento.jsp
	 * 
	 * @return
	 */
	public boolean isMostraPrazoConclusao() {		
		return tipoJub.getTipoMovimentacao().getId() == TipoMovimentacaoAluno.PRAZO_MAXIMO;
	}
	
	/**
	 * Utilizado para adicionar os anos e períodos a considerar na busca de discentes passíveis de jubilamento.
	 * 
	 * Chamado por:
	 * sigaa.war/ensino/movimentacao_aluno/parametros_cancelamento.jsp
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String adicionarAnoPeriodo() throws DAOException {		
		
		if(ano == null || periodo == null || ano < 1958 || ano > CalendarUtils.getAnoAtual() || periodo < 1 || periodo > 2){
			addMensagemErro("Informe um Ano-Período válido.");
			return null;
		}
		
		CalendarioAcademico calendario = getCalendarioAcademicoQuery();
		if(ano+periodo > calendario.getAno()+calendario.getPeriodo()) {
			addMensagemErro("Não é permitido inserir um Ano-Período posterior ao calendário vigente.");
			return null;
		}
		
		
		
		for (Integer[] aps : anosPeriodo) {
			if (aps[0] == ano.intValue() && aps[1] == periodo.intValue()){
				addMensagemErro("Ano-Período já adicionado.");
				return null;
			}
		}
		Integer[] anoPeriodo = new Integer[] { ano, periodo };
		anosPeriodo.add(anoPeriodo);
		return telaInicial();
	}
	
	/**
	 * Remove um ano-período da lista de anos-períodos nos quais serão buscados. 
	 * 
	 * Método chamado pela seguinte JSP: 
	 * sigaa.war/ensino/movimentacao_aluno/parametros_cancelamento.jsp
	 *  
	 * @return
	 */
	public void removerAnoPeriodo(ActionEvent evt){
		int indice = getParameterInt("indice");
		CollectionUtils.removePorPosicao(anosPeriodo, indice);
	}
	
	/**
	 * 
	 * Chamado para mostrar o histórico
	 * 
	 * Chamado por:
	 * sigaa.war/ensino/movimentacao_aluno/lista_passiveis_jubilamento.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String verHistorico() throws ArqException {
		int id = getParameterInt("idDiscente", 0);
		if (id > 0){
			Discente d = new Discente(id);
			HistoricoMBean historico = new HistoricoMBean();
			historico.setDiscente(getGenericDAO().refresh(d));
			return historico.selecionaDiscente();
		} else 
			return null;

	}
	
	/*GETTERS e SETTERS*/
	
	public List<Integer[]> getAnosPeriodo() {
		return anosPeriodo;
	}
	public void setAnosPeriodo(List<Integer[]> anosPeriodo) {
		this.anosPeriodo = anosPeriodo;
	}
	public Boolean getEad() {
		return ead;
	}
	public void setEad(Boolean ead) {
		this.ead = ead;
	}
	public Boolean getFiltroMatriculados() {
		return filtroMatriculados;
	}
	public void setFiltroMatriculados(Boolean filtroMatriculados) {
		this.filtroMatriculados = filtroMatriculados;
	}
	public Unidade getUnidade() {
		return unidade;
	}
	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}
	public JubilamentoQuery getJubilamentoQuery() {
		return jubilamentoQuery;
	}
	public void setJubilamentoQuery(JubilamentoQuery jubilamentoQuery) {
		this.jubilamentoQuery = jubilamentoQuery;
	}
	public Integer getTipoJubilamento() {
		return tipoJubilamento;
	}
	public void setTipoJubilamento(Integer tipoJubilamento) {
		this.tipoJubilamento = tipoJubilamento;
	}
	public Integer getAno() {
		return ano;
	}
	public void setAno(Integer ano) {
		this.ano = ano;
	}
	public Integer getPeriodo() {
		return periodo;
	}
	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}
	public Collection<Discente> getDiscentes() {
		return discentes;
	}
	public void setDiscentes(Collection<Discente> discentes) {
		this.discentes = discentes;
	}
	public TipoJubilamento getTipoJub() {
		return tipoJub;
	}
	public void setTipoJub(TipoJubilamento tipoJub) {
		this.tipoJub = tipoJub;
	}
	public Collection<Discente> getDiscentesComPendencias() {
		return discentesComPendencias;
	}
	public void setDiscentesComPendencias(
			Collection<Discente> discentesComPendencias) {
		this.discentesComPendencias = discentesComPendencias;
	}

	public void setIgnorarPendencia(boolean ignorarPendencia) {
		this.ignorarPendencia = ignorarPendencia;
	}

	public boolean isIgnorarPendencia() {
		return ignorarPendencia;
	}

	public void setDiscentesPassiveisJubilamento(
			Collection<Discente> discentesPassiveisJubilamento) {
		this.discentesPassiveisJubilamento = discentesPassiveisJubilamento;
	}

	public Collection<Discente> getDiscentesPassiveisJubilamento() {
		return discentesPassiveisJubilamento;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public Integer getAnoSaida() {
		return anoSaida;
	}

	public void setAnoSaida(Integer anoSaida) {
		this.anoSaida = anoSaida;
	}

	public Integer getPeriodoSaida() {
		return periodoSaida;
	}

	public void setPeriodoSaida(Integer periodoSaida) {
		this.periodoSaida = periodoSaida;
	}
	
}