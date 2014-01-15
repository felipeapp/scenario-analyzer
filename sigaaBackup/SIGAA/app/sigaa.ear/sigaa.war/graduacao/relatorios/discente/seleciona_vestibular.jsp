<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:messages showDetail="true"></h:messages>
<h:outputText value="#{realtorioDiscente.create}"></h:outputText>
<h2><ufrn:subSistema /> > Quantitativo de Alunos que Entraram por Segunda Opção no Vestibular </h2>

<h:form id="form">
<table align="center" class="formulario" width="45%">
	<caption class="listagem">Parâmetros do Relatório</caption>
	<tbody>
		<tr>
			<th class="required">Processo Seletivo:</th>
			<td><h:selectOneMenu id="processoSeletivo" value="#{relatorioDiscente.processoSeletivo}">
					<f:selectItem itemValue="0"	itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{processoSeletivoVestibular.allAtivoCombo}" />
				</h:selectOneMenu>
			</td>
		</tr>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="3" align="center">
				<h:commandButton id="gerarRelatorio" value="Gerar Relatório" action="#{relatorioDiscente.ingressantesSegundaOpcao}"/> 
				<h:commandButton value="Cancelar" action="#{relatorioDiscente.cancelar}" id="cancelar" onclick="#{confirm}"/>
			</td>
		</tr>
	</tfoot>
</table>

	<br>
	<center>
	<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
	<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	<br>

</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>