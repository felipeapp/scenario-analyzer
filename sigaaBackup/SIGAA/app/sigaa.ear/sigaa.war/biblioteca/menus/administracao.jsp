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
	<li> <h:commandLink action="#{etiquetaMBean.listarAllEtiquetasMARC}" onclick="setAba('administracao')" value="Listar/Cadastrar Novo Campo Padr�o do MARC" /></li>

	<li> <h:commandLink action="#{atualizaCacheMARCTitulosMBean.iniciarAgendamentoAtualizacao}" onclick="setAba('administracao')" value="Atualizar Cache dos dados MARC dos T�tulos" /></li>
	
	<li> <h:commandLink action="#{atualizaCacheMARCArtigosMBean.iniciarAgendamentoAtualizacao}" onclick="setAba('administracao')" value="Atualizar Cache dos dados MARC dos Artigos" /></li>
</ul>

<br/><br/><br/><br/>

<ul>
	<li>Tarefas Agendadas
		
	<ul>
			
		<li> <h:commandLink action="#{executaTimersBibliotecaMBean.executaNotificacaoEmprestimos}" onclick="setAba('administracao'); return confirm('Confirma a execu��o da notifica��o ? ');" value="Executar a notifica��o de empr�stimos vencendo" /></li>
		
		<li> <h:commandLink action="#{executaTimersBibliotecaMBean.executaNotificacaoEmprestimosEmAtraso}" onclick="setAba('administracao'); return confirm('Confirma a execu��o da notifica��o ? ');" value="Executar a notifica��o de empr�stimos EM ATRASO" /></li>
		
		<li> <h:commandLink action="#{executaTimersBibliotecaMBean.executaVerificacaoReservasEmEsperaVencidas}" onclick="setAba('administracao'); return confirm('Confirma a execu��o da notifica��o ? ');" value="Executar Verifica��o Reservas Vencidas" /></li>
		
		<li> <h:commandLink action="#{executaTimersBibliotecaMBean.executaAtualizacaoEstatisticasBiblioteca}" onclick="setAba('administracao'); return confirm('Confirma a Atualiza��o das Estat�sticas da Biblioteca? ');" value="Atualizar as Estat�sticas da Biblioteca" /></li>
		
		<li> <h:commandLink action="#{executaTimersBibliotecaMBean.executaNotificacaoUsuariosInteressados}" onclick="setAba('administracao'); return confirm('Confirma o Envio da Notifica��o DSI aos Usu�rios? ');" value="Enviar Notifica��o DSI aos Usu�rios" /></li>
		
		<li> <h:commandLink action="#{executaTimersBibliotecaMBean.executaEnvioInformativoNovasAquisicoes}" onclick="setAba('administracao'); return confirm('Confirma o envio do Informativo de Novas Aquisi��es ? ');" value="Gerar e Enviar o Informativo de Novas Aquisi��es" /></li>
		
		<li> <h:commandLink action="#{executaTimersBibliotecaMBean.executaBaixaMultasPagasAutomaticamente}" onclick="setAba('administracao'); return confirm('Confirma a execu��o da verifica��o e baixa das multas pagas automaticamente ? ');" value="Dar Baixa Multas Pagas Autom�ticamente " /></li>
		
	</ul>
	
</ul>

	