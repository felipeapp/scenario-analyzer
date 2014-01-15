<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp" %>

<style>
	tr.curso td {padding: 15px 0 0; border-bottom: 1px solid #555}
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
	tr.discente td {border-bottom: 1px solid #888; font-weight: bold; padding-top: 7px;}
	tr.componentes td {padding: 4px 2px 2px ; border-bottom: 1px dashed #888;}
	tr.componentes td.assinatura { padding:2px; border-bottom: 1px solid #888;  width: 40%;}
</style>

<f:view>

	<h2>RELATÓRIO GERAL DE EXTENSÃO</h2>
	<br />
	
	<c:set var="result" value="#{relatoriosAtividades.resultado}" />
	
	<div id="parametrosRelatorio">
		<table>
			<tr>
				<th>Ações com situação:</th>
				<td>
					<h:outputText value="EM EXECUÇÃO e CONCLUÍDA" />
				</td>
			</tr>
			<tr>
				<th>Realizadas no período de:</th>
				<td>
					<h:outputText value="#{relatoriosAtividades.dataInicio}">
						<f:convertDateTime pattern="dd/MM/yyyy"/>
					</h:outputText>
					a
					<h:outputText value="#{relatoriosAtividades.dataFim}">
						<f:convertDateTime pattern="dd/MM/yyyy"/>
					</h:outputText>
				</td>
			</tr>
		</table>
	</div>
	<br />
	
	<%--<h3 class="tituloTabelaRelatorio">Relatório de Total de Docentes Participantes em Atividade de Extensão</h3> --%>
	<h3 class="tituloTabelaRelatorio">
		<h:outputText value="Relatório de Total de Docentes Participantes em Atividade de Extensão" rendered="#{relatoriosAtividades.selecionaRelatorio == 1}" />
		<h:outputText value="Relatório de Total de Discentes Participantes em equipes de projetos em Atividade de Extensão" rendered="#{relatoriosAtividades.selecionaRelatorio == 2}" />
		<h:outputText value="Relatório de Total de Discentes com plano de trabalho Participantes em Ações de Extensão" rendered="#{relatoriosAtividades.selecionaRelatorio == 3}" />
	</h3>
	
	
	<table class="tabelaRelatorio" width="100%">
	
		<thead>
			<tr>
				<th>
					<h:outputText  value="Nome da Unidade" rendered="#{relatoriosAtividades.selecionaRelatorio == 1}" /> 
					<h:outputText  value="Nome do Curso" rendered="#{relatoriosAtividades.selecionaRelatorio != 1}" />
				</th>
				<th>Sigla da Unidade</th>
				<th style="text-align: right">Total</th>
			</tr>			
		</thead>
		
		<tbody>
			<c:set var="total" value="0" />
			<c:set var="idGestoraAtual" value="0" />
			<c:set var="idAcademicaAtual" value="0" />
			<c:choose>
				<c:when test="${relatoriosAtividades.selecionaRelatorio == 1}">
				
					<c:set var="totalGestora" value="0" />
					<c:forEach items="${result}" var="r">
						<%-- Verifica se mudou a unidade gestora --%>
						<c:if test="${r.id_gestora != idGestoraAtual}">
							<c:if test="${idGestoraAtual != 0}">
								<tr class="componentes">
									<td colspan="2" style="text-align: right">
										<b><h:outputText value="Total de Unidades:" /> </b>
									</td>
									<td style="text-align: right; font-weight: bold;">${totalGestora}</td>
								</tr>
							</c:if>
							<tr class="componentes">
								<td colspan="3"><b>${r.nome_gestora}</b></td>
							</tr>
							<c:set var="idGestoraAtual" value="${r.id_gestora}" />
							<c:set var="totalGestora" value="0" />
						</c:if>
						<%-- Fim da verificação --%>
						<%-- Mostra as informações do relatório --%>
						<tr class="componentes">
							<td style="text-align: left">${r.nome}</td>
							<td style="text-align: left">${r.sigla}</td>
							<td style="text-align: right"><fmt:formatNumber value="${r.quantidade}" /> </td>
						</tr>
						<c:set var="total" value="${total + r.quantidade}" />
						<c:set var="totalGestora" value="${totalGestora + 1}" />
					</c:forEach>
					
				</c:when>
				<c:otherwise>
				
					<c:set var="totalAcademica" value="0" />
					<c:forEach items="${result}" var="r">
						<c:if test="${r.id_gestora_unidade != idAcademicaAtual}">
							<c:if test="${idAcademicaAtual != 0}">
								<tr class="componentes">
									<td colspan="2" style="text-align: right">
										<b><h:outputText value="Total de Cursos:" /></b>
									</td>
									<td style="text-align: right; font-weight: bold;">${totalAcademica}</td>
								</tr>
							</c:if>
							<tr class="componentes">
								<td colspan="3"><b>${r.nome_gestora_academica}</b></td>
							</tr>
							<c:set var="idAcademicaAtual" value="${r.id_gestora_unidade}" />
							<c:set var="totalAcademica" value="0" />
						</c:if>
						<%-- Mostra as informações do relatório --%>
						<tr class="componentes">
							<td style="text-align: left">${r.nome}</td>
							<td style="text-align: left">${r.sigla}</td>
							<td style="text-align: right"><fmt:formatNumber value="${r.quantidade}" /> </td>
						</tr>
						<c:set var="total" value="${total + r.quantidade}"/>
						<c:set var="totalAcademica" value="${totalAcademica + 1}" />
					</c:forEach>
					
				</c:otherwise>
			</c:choose>
				
		</tbody>
		
		<tfoot>
			<tr class="componentes">
				<c:if test="${relatoriosAtividades.selecionaRelatorio == 1}">
					<td colspan="2" style="text-align: right">
						<b><h:outputText value="Total de Unidades:" /> </b>
					</td>
					<td style="text-align: right; font-weight: bold;">${totalGestora}</td>
				</c:if>
				<c:if test="${relatoriosAtividades.selecionaRelatorio != 1}">
					<td colspan="2" style="text-align: right">
						<b><h:outputText value="Total de Cursos:" /> </b>
					</td>
					<td style="text-align: right; font-weight: bold;">${totalAcademica}</td>
				</c:if>
			</tr>
		</tfoot>
		
	</table>
	<br />
	<table width="100%">
		<tr>
			<td style="text-align: center"> 
				<b>
					<h:outputText value="Total de Docentes:" rendered="#{relatoriosAtividades.selecionaRelatorio == 1}"/>
					<h:outputText value="Total de Discentes:" rendered="#{relatoriosAtividades.selecionaRelatorio != 1}"/>
					${total}
				</b>
			</td>
			<td style="text-align: right; font-weight: bold;"></td>
		</tr>
	</table>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp" %>