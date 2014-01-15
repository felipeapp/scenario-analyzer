<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:form id="form">
<div class="descricaoOperacao">
	<h4> Aten��o para algumas informa��es importantes! </h4>
	<p>
	 	Foi criado um processo no Sistema de Protocolos referente � notifica��o de inven��o submetida
	 	e destinado ao N�cleo de Inova��o Tecnol�gica.
	 	A partir de agora ser� necess�rio realizar os seguintes procedimentos:
	</p>
	<ol>
		<li>Imprimir a capa do processo atrav�s do link no final desta p�gina;</li>
		<li>Anexar ao processo o conte�do da inven��o segundo modelo disponibilizado na p�gina do NIT;</li>
		<li>Entregar o processo em m�os do funcion�rio respons�vel do NIT.</li>
	</ol>
</div>

<table class="visualizacao" style="width: 100%">
	<tr>
		<th width="35%"> N�mero do Processo: </th>
		<td> 23077.<h:outputText id="numeroProtocolo" value="#{invencao.obj.numeroProtocolo}"/>/<h:outputText id="anoProtocolo" value="#{invencao.obj.anoProtocolo}"/> </td>
	</tr>
	<tr>
		<th> C�digo da Notifica��o de Inven��o: </th>
		<td> <h:outputText id="codigoNotificacao" value="#{invencao.obj.codigo}"/> </td>
	</tr>
</table>

<table  class="subFormulario" align="center">
	<tr>
	<td width="8%" valign="middle" align="center">
		<html:img page="/img/warning.gif"/>
	</td>
	<td valign="middle" style="text-align: justify">
		Para imprimir a capa do processo clique no �cone ao lado.
		Tamb�m guarde o n�mero do processo e o c�digo da notifica��o para futura refer�ncia.
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