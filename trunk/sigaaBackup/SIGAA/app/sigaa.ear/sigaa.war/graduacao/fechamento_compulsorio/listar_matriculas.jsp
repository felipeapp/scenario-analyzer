<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<h2><ufrn:subSistema /> > Fechamento Compulsório de Atividades</h2>

<f:view>
<a4j:keepAlive beanName="fechamentoCompulsorioAtividades" />
<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>

	<div class="descricaoOperacao">
			<b> Caro(a) usuário, </b>
			<br/><br/>
			Nesta tela é possível listar os discentes em espera ou matriculados em atividades individuais.
			<br/><br/>
			Serão listados todos os discentes cujo o ano e período da matrícula sejam <b>iguais ou inferior</b> ao ano e período passados no filtro.
			<br/><br/>
			Uma vez que os discentes tenham sido listados, aparecerá o botão "Próximo >>". 
			<br/><br/>
			Após clicar no botão "Próximo >>", o sistema irá redirecioná-lo para uma tela de confirmação onde será exibida a listagem completa dos alunos.
			<br/><br/>
			Nesta tela será possível cancelar as matrículas dos discentes.
	</div>

	<h:form id="form">
	
		<table class="formulario" width="40%">
			<caption>Filtros</caption>
			<tr>
				<th>
					<span style="text-align:right;" class="required">Ano-Período:</span>
				</th>
				<td>
					<h:inputText value="#{fechamentoCompulsorioAtividades.ano}" size="4" maxlength="4"/>-<h:inputText value="#{fechamentoCompulsorioAtividades.periodo}" size="1" maxlength="1"/> 
				</td>	
			</tr>
			<tr>
			<th>Modalidade de Ensino:</th>
				<td>
					<h:selectOneRadio value="#{ fechamentoCompulsorioAtividades.ead }" id="ead">
						<f:selectItem itemLabel="Presencial" itemValue="false"/>
						<f:selectItem itemLabel="EAD" itemValue="true"/>
					</h:selectOneRadio>
				</td> 
			</tr>
			<tfoot><tr><td colspan="2" style="text-align:center;">
				<h:commandButton action="#{ fechamentoCompulsorioAtividades.filtrar }" value=" Filtrar "/>
			</td></tr></tfoot>
		</table>
		<div align="center">
			<span class="required">&nbsp;</span>
			Campos de Preenchimento Obrigatório
		</div>	
		<br/>
		
		<c:if test="${not empty fechamentoCompulsorioAtividades.matriculas}">
			<table class="listagem" style="width:100%">
			<caption class="listagem">Lista de Matrículas para Fechar (${fn:length(fechamentoCompulsorioAtividades.matriculas)})</caption>
			
			<thead>
			<tr>
				<th><input type="checkbox" id="checkTodos" onclick="checkAll()" /></th>
				<th width="10%">Matrícula</th>
				<th>Discente</th>
				<th style="text-align:center;width:10%;">Status do Discente</th>
				<th>Atividade</th>
				<th style="text-align:center; width:7%">Período</th>
			</tr>
			</thead>
				<c:set var="idCurso" value="0" />		
				<c:forEach items="#{fechamentoCompulsorioAtividades.matriculas}" var="m" varStatus="status">

					<c:if test="${idCurso != m.discente.curso.id}">
					<tr><td colspan="6" style="background-color: #C8D5EC;font-weight:bold;"><h:outputText value="#{m.discente.curso.nome} - #{m.discente.curso.municipio.nome}"/></td></tr>
					</c:if>
					<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td><h:selectBooleanCheckbox value="#{m.discente.selecionado}" styleClass="check" /></td>
						<td><h:outputText value="#{m.discente.matricula}"/></td>
						<td><h:outputText value="#{m.discente.nome}"/></td>
						<td align="center"><h:outputText value="#{m.discente.statusString}"/></td>
						<td><h:outputText value="#{m.componente.nome}"/></td>							
						<td align="center"><h:outputText value="#{m.anoPeriodo}"/></td>
					</tr>
					<c:set var="idCurso" value="${m.discente.curso.id}" />		
				</c:forEach>
				<tfoot>
					<tr>
						<td colspan="6" align="center">
							<h:commandButton action="#{fechamentoCompulsorioAtividades.fechar}" value="Próximo >>"/>
							<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{fechamentoCompulsorioAtividades.cancelar}" /> 
						</td>
					</tr>
				</tfoot>
			</table>
		</c:if>
	</h:form>
</f:view>

<script>
function checkAll() {
	$A(document.getElementsByClassName('check')).each(function(e) {
		e.checked = !e.checked;
	});
}
if (document.getElementById('checkTodos') != null){
	document.getElementById('checkTodos').checked = true;
	checkAll();
}
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
