<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<f:view>
<a4j:keepAlive mbean="relatorioTurmasDocenciaAssistida" />

<c:if test="${not empty relatorioTurmasDocenciaAssistida.turmasDocenciaAssistida}">
	
	<br />
	
	<h:form>
		
		<h2> Relatório de Turmas de Docência Assistida</h2>
		
		<div id="parametrosRelatorio">
			<table width="100%">
				<tr>
					<th width="10%">
						Ano.Período:			
					</th>
					<td>
						<h:outputText value="#{relatorioTurmasDocenciaAssistida.ano}.#{relatorioTurmasDocenciaAssistida.periodo}"/>
					</td>
				</tr>
			</table>
		</div>
		<br><br>
		
		<c:set var="qtdTotal" value="0"/>

		<c:forEach items="#{relatorioTurmasDocenciaAssistida.turmasDocenciaAssistida}" var="_departamento" varStatus="status">
			
			<table class=tabelaRelatorioBorda width="100%">
				<thead>
					<tr>
						<th colspan="4"><h:outputText value="#{_departamento.departamento}"/></th>
					</tr>
					<tr>
						<th width="10%"> Código </th>
						<th width="10%"> Turma </th>
						<th> Componente </th>
						<th width="50%"> Docente(s) da Turma </th>
					</tr>
				</thead>
				<tbody>
					
					<c:forEach items="#{_departamento.turmas}" var="_turma" varStatus="status2">
						
						<tr>
							<td width="10%"><h:outputText value="#{_turma.codigoComponente}"/></td>
							<td width="10%"><h:outputText value="#{_turma.codigo}"/></td>
							<td><h:outputText value="#{_turma.componente}"/></td>
							<td width="50%" colspan="2">
								<c:forEach items="#{_turma.docentes}" var="_docente" varStatus="status2">
									<h:outputText value="#{_docente}"/>
									<c:if test="${!status2.last}">, </c:if>
								</c:forEach>	
							</td>
						</tr>
						<c:set var="qtdDepto" value="${qtdDepto+1}"/>
						
					</c:forEach>
				</body>
			</table>
			<table class=tabelaRelatorioBorda width="100%">	
				<tfoot>
					<tr>
						<td  align="right" colspan="3">Total por Departamento:</td>
						<td width="3%" align="right">${qtdDepto}</td>
					</tr>
				</tfoot>
			</table>	
			<br>
			<c:set var="qtdTotal" value="${qtdTotal+qtdDepto}"/>
			<c:set var="qtdDepto" value="0"/>
		</c:forEach>
	<br>
		
	<table class=tabelaRelatorioBorda width="100%">
		<tfoot>
			<tr>
				<td align="center"> Total de Turmas de Docência Assistida: ${qtdTotal} </td>
			</tr>
		</tfoot>
	</table>
	
	</h:form>
	
</c:if>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>