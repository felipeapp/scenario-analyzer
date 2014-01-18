<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<c:set var="confirmOperacao" value="return confirm('Ao confirmar a opera��o n�o vai ser poss�vel modificar a confirma��o de v�nculo do processo seletivo, deseja confirmar a opera��o?');" scope="application"/>

<f:view>
	<h2> <ufrn:subSistema /> > Tela de Resumo </h2>
	
	<h:form id="form">
	
		<div class="descricaoOperacao">
			<center>
				A opera��o de confirma��o de v�nculo s� � realizada uma �nica vez, devendo ser realizada apenas ao t�rmino da convoca��o.
			</center>
		</div>
	
	<table class="formulario" style="width: 100%">
		<caption> Resumo </caption>
	
			<c:if test="${ not empty confirmacaoVinculoMBean.discenteConfirmados }">
				<tr style="width: 100%">
					<td colspan="2" class="subFormulario"> Discentes Confirmados </td>
				</tr>
				<tr>
					<td colspan="2" width="100%">
						<table class="subFormulario" style="width: 100%" id="tabelaDiscentes">
							<thead>
								<tr>
									<td> Nome </td>
									<td> Curso </td>
									<td> Status </td>
									<td> Programa </td>
								</tr>
							</thead>
							
							<c:forEach items="#{ confirmacaoVinculoMBean.discenteConfirmados }" var="item">
								<tr>
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

			<c:if test="${ not empty confirmacaoVinculoMBean.discenteNaoConfirmados }">
				<tr style="width: 100%">
					<td colspan="2" class="subFormulario"> Discentes N�o Confirmados </td>
				</tr>
				<tr>
					<td colspan="2" width="100%">
						<table class="subFormulario" style="width: 100%" id="tabelaDiscentes">
							<thead>
								<tr>
									<td> Nome </td>
									<td> Curso </td>
									<td> Status </td>
									<td> Programa </td>
								</tr>
							</thead>
							
							<c:forEach items="#{ confirmacaoVinculoMBean.discenteNaoConfirmados }" var="item">
								<tr>
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
						<h:commandButton value="Confirmar" id="resumo" onclick="#{confirmOperacao}" action="#{ confirmacaoVinculoMBean.cadastrar }"/>
						<h:commandButton value="<< Selecionar Discentes" id="Campus" action="#{ confirmacaoVinculoMBean.selecionaCampus }" />
						<h:commandButton value="Cancelar" id="Cancelar" immediate="true" action="#{ confirmacaoVinculoMBean.cancelar }" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
	</table>

	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>