<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:form id="form">
<div class="descricaoOperacao">
	<h4> Atenção para algumas informações importantes! </h4>
	<p>
	 	Foi criado um processo no Sistema de Protocolos referente à notificação de invenção submetida
	 	e destinado ao Núcleo de Inovação Tecnológica.
	 	A partir de agora será necessário realizar os seguintes procedimentos:
	</p>
	<ol>
		<li>Imprimir a capa do processo através do link no final desta página;</li>
		<li>Anexar ao processo o conteúdo da invenção segundo modelo disponibilizado na página do NIT;</li>
		<li>Entregar o processo em mãos do funcionário responsável do NIT.</li>
	</ol>
</div>

<table class="visualizacao" style="width: 100%">
	<tr>
		<th width="35%"> Número do Processo: </th>
		<td> 23077.<h:outputText id="numeroProtocolo" value="#{invencao.obj.numeroProtocolo}"/>/<h:outputText id="anoProtocolo" value="#{invencao.obj.anoProtocolo}"/> </td>
	</tr>
	<tr>
		<th> Código da Notificação de Invenção: </th>
		<td> <h:outputText id="codigoNotificacao" value="#{invencao.obj.codigo}"/> </td>
	</tr>
</table>

<table  class="subFormulario" align="center">
	<tr>
	<td width="8%" valign="middle" align="center">
		<html:img page="/img/warning.gif"/>
	</td>
	<td valign="middle" style="text-align: justify">
		Para imprimir a capa do processo clique no ícone ao lado.
		Também guarde o número do processo e o código da notificação para futura referência.
	</td>
	<td>
	<table>
		<tr>
			<td align="center">
				<h:commandLink id="linkIcone" title="Imprimir Capa do Processo"  action="#{invencao.consultaCapaProcesso}"  >
		 			<h:graphicImage url="/img/printer_ok.png" />
		 		</h:commandLink>
		 	</td>
		 </tr>
		 <tr>
		 	<td align="center">
		 		<h:commandLink id="linkTexto" title="Imprimir Capa do Processo"  value="Imprimir Capa do Processo" action="#{invencao.consultaCapaProcesso}"  />
		 	</td>
		 </tr>
	</table>
	</td>
	</tr>
</table>
</h:form>
</f:view>
<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>