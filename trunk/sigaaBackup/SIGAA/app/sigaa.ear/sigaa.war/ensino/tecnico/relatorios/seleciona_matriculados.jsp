<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>

<h:outputText value="#{relatoriosTecnico.create}"></h:outputText>
<h2><ufrn:subSistema /> &gt; Lista de Alunos Matriculados</h2>

<h:form id="form">
<table align="center" class="formulario" width="40%">
  <caption class="listagem">Dados do Relatório</caption>
	<c:if test="${relatoriosMedio.proplan}">	
		<tr>
			<th width="50%" class="obrigatorio">Unidade:</th>
				<td>
					<h:selectOneMenu value="#{relatoriosTecnico.unidade.id}" style="width: 70%">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
				 		<f:selectItems value="#{relatoriosTecnico.unidades}" /> 
					</h:selectOneMenu>
				</td>
		</tr>
	</c:if>		
	<tr>
		<th class="obrigatorio">Ano-Período: </th>
		<td>
			<h:inputText id="ano" title="Ano" value="#{relatoriosTecnico.ano}" size="4" maxlength="4" onkeyup="return formatarInteiro(this);"/> -
			<h:inputText id="periodo" title="Período" value="#{relatoriosTecnico.periodo}" size="1" maxlength="1" onkeyup="return formatarInteiro(this);"/>
		</td>
	</tr>
	<tfoot>	
		<tr>
			<td colspan="2" align="center">
				<h:commandButton id="gerarRelatorio" value="Gerar Relatório" action="#{relatoriosTecnico.gerarRelatorioListaAlunosMatriculados}"/> 
				<h:commandButton value="Cancelar" action="#{relatoriosTecnico.cancelar}" id="cancelar" onclick="#{confirm}"/>
			</td>
		</tr>
	</tfoot>
</table>
<br />
<div class="obrigatorio" style="width: 90%">Campos de preenchimento obrigatório.</div>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>