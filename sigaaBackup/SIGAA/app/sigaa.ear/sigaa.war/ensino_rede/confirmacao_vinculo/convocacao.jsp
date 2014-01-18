<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2> <ufrn:subSistema /> > Convocação </h2>

	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
	
	<h:form id="form">
	<table class="formulario" style="width: 100%">
		<caption>Convocações Encontradas</caption>
			<tr>
				<th>Convocação:</th>
				<td>
					<h:selectOneMenu value="#{confirmacaoVinculoMBean.convocacao.id}" id="convocacao">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"  />
						<f:selectItems value="#{confirmacaoVinculoMBean.allConvocacoesCombo}" id="convocacoes"/>
						<a4j:support event="onchange" action="#{confirmacaoVinculoMBean.carregaDiscentes}"
								reRender="form"/>
					</h:selectOneMenu>
				</td>
			</tr>
	
			<c:if test="${ not empty confirmacaoVinculoMBean.discentes }">
				<tr style="width: 100%">
					<td colspan="2" class="subFormulario"> Discentes Encontrados </td>
				</tr>
				<tr>
					<td colspan="2" width="100%">
						<table class="subFormulario" style="width: 100%" id="tabelaDiscentes">
							<thead>
								<tr>
									<td> <h:selectBooleanCheckbox styleClass="chkSelecionaTodos" onclick="selecionarTodos();" /> </td>
									<td> Nome </td>
									<td> Curso </td>
									<td> Status </td>
									<td> Programa </td>
								</tr>
							</thead>
							
							<c:forEach items="#{ confirmacaoVinculoMBean.discentes }" var="item">
								<tr>
									<td> <h:selectBooleanCheckbox value="#{item.selected}" onclick="mudarCor(this)" styleClass="todosChecks"/> </td>
									<td> <h:outputText value="#{ item.pessoa.nome }" /> </td>
									<td> <h:outputText value="#{ item.dadosCurso.curso.nome }" /> </td>
									<td> <h:outputText value="#{ item.status.descricao }" /> </td>
									<td> <h:outputText value="#{ item.dadosCurso.curso.programa.descricao }" /> </td>
								</tr>
							</c:forEach>
						</table>
					</td>
				</tr>
			</c:if>
		
		<tfoot>
			<tr>	
				<td colspan="2">
					<h:commandButton value="<< Selecionar Campus" id="Campus" action="#{ confirmacaoVinculoMBean.iniciarGestor }" 
						rendered="#{ confirmacaoVinculoMBean.coordenadorProgramaRede }"/>
					<h:commandButton value="Cancelar" id="Cancelar" immediate="true" action="#{ confirmacaoVinculoMBean.cancelar }" onclick="#{confirm}"/>
					<h:commandButton value="Selecionar Discentes >>" id="resumo" action="#{ confirmacaoVinculoMBean.resumo }"/>
				</td>
			</tr>
		</tfoot>
	</table>

	</h:form>
</f:view>

	<br>
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	</center>
	

<script type="text/javascript">

function mudarCor(elem) {
	var tdElement = elem.parentNode;
	var trElement = tdElement.parentNode;
	if ( elem.checked == false )
		trElement.style.cssText ="background-color:#F9FBFD";
	else
		trElement.style.cssText ="background-color:#FDF3E1";
}

function selecionarTodos(){
	var todosSelecionados = document.getElementsByClassName("chkSelecionaTodos")[0];
	var checks = document.getElementsByClassName("todosChecks");
	
	for (i=0; i<checks.length; i++){
	   checks[i].checked = todosSelecionados.checked;
	   mudarCor(checks[i]);
	}
}

</script>	

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>