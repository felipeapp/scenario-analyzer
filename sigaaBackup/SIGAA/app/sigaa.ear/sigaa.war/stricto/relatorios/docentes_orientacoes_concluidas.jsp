<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:outputText value="#{relatorioDocentesOrientacoes.create}" />

<h2> <ufrn:subSistema /> > ${relatorioDocentesOrientacoes.titulo} </h2>

<h:form id="form">
<a4j:keepAlive beanName="relatorioDocentesOrientacoes" />
<table class="formulario" style="width: 95%">
<caption> Informe os critérios para a emissão do relatório </caption>

	<tr>
		<th class="required">Programa: </th>
		<td>
			<h:selectOneMenu id="programa" value="#{relatorioDocentesOrientacoes.unidade.id}">
				<f:selectItem itemLabel="TODOS" itemValue="-1"/>
				<f:selectItems value="#{unidade.allProgramaPosCombo}"/>
			</h:selectOneMenu>
		</td>
	</tr>
	
	<tr>
		<th class="required">Ano: </th>
		<td>
			<h:inputText value="#{relatorioDocentesOrientacoes.ano}" id="ano" size="5" maxlength="4" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/>
		</td>
	</tr>
	
	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton action="#{relatorioDocentesOrientacoes.gerarRelatorio}" value="Emitir Relatório"/>
			<h:commandButton action="#{relatorioDocentesOrientacoes.cancelar}" value="Cancelar"/>
		</td>
	</tr>
	</tfoot>
</table>
<br/>
<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>