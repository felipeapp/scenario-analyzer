<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsf/html"      prefix="h"%>
<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>

<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>



<div class="descricaoOperacao"> 
	Trarefas administrativas do sistema.
</div>


<ul>
	<li> <h:commandLink action="#{etiquetaMBean.listarAllEtiquetasMARC}" onclick="setAba('administracao')" value="Listar/Cadastrar Novo Campo Padrão do MARC" /></li>

	<li> <h:commandLink action="#{atualizaCacheMARCTitulosMBean.iniciarAgendamentoAtualizacao}" onclick="setAba('administracao')" value="Atualizar Cache dos dados MARC dos Títulos" /></li>
	
	<li> <h:commandLink action="#{atualizaCacheMARCArtigosMBean.iniciarAgendamentoAtualizacao}" onclick="setAba('administracao')" value="Atualizar Cache dos dados MARC dos Artigos" /></li>
</ul>

<br/><br/><br/><br/>

<ul>
	<li>Tarefas Agendadas
		
	<ul>
			
		<li> <h:commandLink action="#{executaTimersBibliotecaMBean.executaNotificacaoEmprestimos}" onclick="setAba('administracao'); return confirm('Confirma a execução da notificação ? ');" value="Executar a notificação de empréstimos vencendo" /></li>
		
		<li> <h:commandLink action="#{executaTimersBibliotecaMBean.executaNotificacaoEmprestimosEmAtraso}" onclick="setAba('administracao'); return confirm('Confirma a execução da notificação ? ');" value="Executar a notificação de empréstimos EM ATRASO" /></li>
		
		<li> <h:commandLink action="#{executaTimersBibliotecaMBean.executaVerificacaoReservasEmEsperaVencidas}" onclick="setAba('administracao'); return confirm('Confirma a execução da notificação ? ');" value="Executar Verificação Reservas Vencidas" /></li>
		
		<li> <h:commandLink action="#{executaTimersBibliotecaMBean.executaAtualizacaoEstatisticasBiblioteca}" onclick="setAba('administracao'); return confirm('Confirma a Atualização das Estatísticas da Biblioteca? ');" value="Atualizar as Estatísticas da Biblioteca" /></li>
		
		<li> <h:commandLink action="#{executaTimersBibliotecaMBean.executaNotificacaoUsuariosInteressados}" onclick="setAba('administracao'); return confirm('Confirma o Envio da Notificação DSI aos Usuários? ');" value="Enviar Notificação DSI aos Usuários" /></li>
		
		<li> <h:commandLink action="#{executaTimersBibliotecaMBean.executaEnvioInformativoNovasAquisicoes}" onclick="setAba('administracao'); return confirm('Confirma o envio do Informativo de Novas Aquisições ? ');" value="Gerar e Enviar o Informativo de Novas Aquisições" /></li>
		
		<li> <h:commandLink action="#{executaTimersBibliotecaMBean.executaBaixaMultasPagasAutomaticamente}" onclick="setAba('administracao'); return confirm('Confirma a execução da verificação e baixa das multas pagas automaticamente ? ');" value="Dar Baixa Multas Pagas Automáticamente " /></li>
		
	</ul>
	
</ul>

	