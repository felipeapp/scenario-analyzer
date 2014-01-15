<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<h2> <ufrn:subSistema/> > Alunos Ausentes no Cadastramento</h2>

<h:form id="form">
<table align="center" class="formulario" width="70%">
	<caption class="listagem">Dados do Relatório</caption>

	<tr id="processo_seletivo" >
		<th width="20%" class="obrigatorio">Processo Seletivo: </th>
		<td>
		<a4j:region>
			<h:selectOneMenu id="processoSeletivo" immediate="true"
				value="#{relatorioProcessoSeletivoDiscente.processoSeletivo}">
				<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
				<f:selectItems value="#{processoSeletivoVestibular.allAtivoCombo}" />
				<a4j:support event="onchange"
					reRender="form"
					action="#{relatorioProcessoSeletivoDiscente.carregarChamadas}" />
			</h:selectOneMenu>
			<a4j:status>
				<f:facet name="start">
					<h:graphicImage value="/img/indicator.gif"></h:graphicImage>
				</f:facet>
			</a4j:status>
		</a4j:region>
		</td>
	</tr>
	<tr id="convocacao_processo_seletivo" >
		<th width="30%" class="obrigatorio">Chamada: </th>
		<td width="70%">
			<h:panelGrid id="fasePanel">
				<h:selectOneMenu id="fase" value="#{relatorioProcessoSeletivoDiscente.convocacao}">
					<f:selectItem itemValue="0" itemLabel="-- TODAS --" />
					<f:selectItems value="#{relatorioProcessoSeletivoDiscente.chamadas}" />
					<t:saveState value="#{relatorioProcessoSeletivoDiscente.chamadas}" />  
				</h:selectOneMenu>
			</h:panelGrid>
		</td>
	</tr>	
	
	
	<tfoot>
  	  <tr>
		 <td colspan="3" align="center">
			<h:commandButton id="gerarRelatorio" value="Gerar Relatório"
				action="#{relatorioProcessoSeletivoDiscente.alunosConvocadosExcluidos}"/> 
			<h:commandButton value="Cancelar" action="#{relatorioProcessoSeletivoDiscente.cancelar}" id="cancelar" 
				onclick="#{confirm}" />
		 </td>
	  </tr>
	</tfoot>
</table>

</h:form>

	<br>
	<center>
	<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
	<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>

</f:view>
		
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>