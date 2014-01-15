<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>
<h:form id="form">
<div class="descricaoOperacao">
	<h4> Aten��o para algumas informa��es importantes! </h4>
	<p>
	 	Foi criado um processo no Sistema de Protocolos referente � proposta de cria��o de grupo de pesquisa submetida.
	 	Conforme o <strong>Art. 6� do Anexo da Resolu��o n� 162/2008-CONSEPE</strong>, o processo deve tramitar pelas seguintes inst�ncias:
	</p>
	<ol>
		<li>Plen�ria do Departamento ou Unidade Acad�mica Especializada de lota��o do L�der;</li>
		<li>Conselho do respectivo Centro ou Colegiado correspondente;</li>
		<li>Comiss�o de Pesquisa da PROPESQ.</li>
	</ol>
</div>

<table class="visualizacao" style="width: 100%">
	<tr>
		<th width="35%"> N�mero do Processo: </th>
		<td> 23077.<h:outputText id="numeroProtocolo" value="#{propostaGrupoPesquisaMBean.obj.numeroProtocolo}"/>/<h:outputText id="anoProtocolo" value="#{propostaGrupoPesquisaMBean.obj.anoProtocolo}"/> </td>
	</tr>

</table>

<table  class="subFormulario" align="center">
	<tr>
	<td width="8%" valign="middle" align="center">
		<html:img page="/img/warning.gif"/>
	</td>
	<td valign="middle" style="text-align: justify">
		Para imprimir a capa do processo clique no �cone ao lado.
	</td>
	<td>
	<table>
		<tr>
			<td align="center">
				<h:commandLink id="linkIcone" title="Imprimir Capa do Processo"  action="#{propostaGrupoPesquisaMBean.consultaCapaProcesso}"  >
		 			<h:graphicImage url="/img/printer_ok.png" />
		 		</h:commandLink>
		 	</td>
		 </tr>
		 <tr>
		 	<td align="center">
		 		<h:commandLink id="linkTexto" title="Imprimir Capa do Processo"  value="Imprimir Capa do Processo" action="#{propostaGrupoPesquisaMBean.consultaCapaProcesso}"  />
		 	</td>
		 </tr>
	</table>
	</td>
	</tr>
</table>
</h:form>
</f:view>
<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>