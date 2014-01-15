<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>
	<h2><ufrn:subSistema /> &gt; Selecione uma turma abaixo para enviar a mensagem</h2>
	
	<div class="infoAltRem">
	    <h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Selecionar Turma
	</div>

	<h:form id="form1">
		<c:set var="atividades" value="#{noticiaTurmaSelecionavelBean.listaTurma}"/>
	
		<c:choose>
			<c:when test="${not empty atividades}">
			
				<table width="90%" class="listagem">

					<caption>Listagem das turmas do Departamento</caption>
					<thead>
						<tr>
							<th>Discplina</th>
							<th colspan="2"></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="#{atividades}" var="linha" varStatus="count">
						<tr class="${count.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td>${linha.disciplina.detalhes.nome_ascii}</td>
							<td>
								<h:commandLink styleClass="noborder" title="Casdatrar notícia para a turma"
								id="cadastrarNoticia" 
									action="#{noticiaTurmaSelecionavelBean.selecionarTurma}">
										<f:param name="id" value="#{linha.id}" /> 
										<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />
								</h:commandLink>
							<td>
						</tr>
						
						</c:forEach>
					</tbody>
				</table>
			</c:when>
			<c:otherwise>
				<span style="color: red">Não existe Turma para cadastrada nesse período.</span>
			</c:otherwise>
		</c:choose>

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>