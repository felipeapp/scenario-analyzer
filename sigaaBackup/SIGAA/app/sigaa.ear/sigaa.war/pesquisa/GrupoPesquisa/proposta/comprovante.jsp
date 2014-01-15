<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>
<h:form id="form">
<div class="descricaoOperacao">
	<h4> Atenção para algumas informações importantes! </h4>
	<p>
	 	Foi criado um processo no Sistema de Protocolos referente à proposta de criação de grupo de pesquisa submetida.
	 	Conforme o <strong>Art. 6º do Anexo da Resolução nº 162/2008-CONSEPE</strong>, o processo deve tramitar pelas seguintes instâncias:
	</p>
	<ol>
		<li>Plenária do Departamento ou Unidade Acadêmica Especializada de lotação do Líder;</li>
		<li>Conselho do respectivo Centro ou Colegiado correspondente;</li>
		<li>Comissão de Pesquisa da PROPESQ.</li>
	</ol>
</div>

<table class="visualizacao" style="width: 100%">
	<tr>
		<th width="35%"> Número do Processo: </th>
		<td> 23077.<h:outputText id="numeroProtocolo" value="#{propostaGrupoPesquisaMBean.obj.numeroProtocolo}"/>/<h:outputText id="anoProtocolo" value="#{propostaGrupoPesquisaMBean.obj.anoProtocolo}"/> </td>
	</tr>

</table>

<table  class="subFormulario" align="center">
	<tr>
	<td width="8%" valign="middle" align="center">
		<html:img page="/img/warning.gif"/>
	</td>
	<td valign="middle" style="text-align: justify">
		Para imprimir a capa do processo clique no ícone ao lado.
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