<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Acesso ao Resultado da Avaliação Institucional</h2>

	<h:form>
	
		<div class="descricaoOperacao">A consulta ao Resultado da Avaliação
		Institucional pelo docente é definida como suspensa durante o
		processamento. Se desejar, você poderá liberar a consulta ao
		processamento, clicando no ícone correspondente na lista abaixo.</div>
		<br/>
		<div class="infoAltRem">
			<h:graphicImage value="/img/delete.png"style="overflow: visible;"/>: Suspender a Consulta ao Resultado
	        <h:graphicImage value="/img/check.png" style="overflow: visible;"/>: Liberar a Consulta ao Resultado
		</div>
		<table align="center" class="listagem" >
			<caption>Ano-Períodos Processados</caption>
			<thead>
				<tr>
					<th style="text-align: center">Ano-Período</th>
					<th style="text-align: center">Data</th>
					<th style="text-align: center">Liberado para</th>
					<th style="text-align: center">Nº Mín. de Avaliações</th>
					<th style="text-align: left;">Perguntas que Determinam a Validade da Avaliação</th>
					<th style="text-align: center">Consulta pelo Docente</th>
					<th style="text-align: center">Consulta pelo Discente</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="#{processamentoAvaliacaoInstitucional.ultimosProcessamentos}" var="processamento" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td style="text-align: center">${processamento.ano}-${processamento.periodo}</td>
						<td style="text-align: center"><ufrn:format type="data" valor="${processamento.fimProcessamento}" /></td>
						<td style="text-align: center">
							<h:outputText value="Discente" rendered="#{processamento.consultaDiscenteLiberada}"/>
							<h:outputText value=" Docente" rendered="#{processamento.consultaDocenteLiberada}"/>
						</td>
						<td style="text-align: center">${processamento.numMinAvaliacoes}</td>
						<td style="text-align: left;">
							<c:forEach items="#{processamento.perguntaDeterminanteExclusaoAvaliacao }" var="pergunta" varStatus="status">
								<c:if test="${status.index > 0}">,</c:if>
								${pergunta.grupo.titulo } - ${pergunta.descricao}
							</c:forEach>
						</td>
						<td style="text-align: center">
							<c:if test="${processamento.consultaDocenteLiberada}">
								<h:commandLink title="Suspender a Consulta ao Resultado para o Docente" action="#{processamentoAvaliacaoInstitucional.suspendeConsulta}" style="border: 0;">
									<f:param name="id" value="#{processamento.id}" />
									<f:param name="discente" value="false" />
									<h:graphicImage value="/img/delete.png"style="overflow: visible;"/><br/>
									Suspender
								</h:commandLink>
							</c:if>
							<c:if test="${not processamento.consultaDocenteLiberada}">
								<h:commandLink title="Liberar a Consulta ao Resultado para o Docente" action="#{processamentoAvaliacaoInstitucional.liberaConsulta}" style="border: 0;">
									<f:param name="id" value="#{processamento.id}" />
									<f:param name="discente" value="false" />
									<h:graphicImage value="/img/check.png" style="overflow: visible;"/><br/>
									Liberar
								</h:commandLink>
							</c:if>
						</td>
						<td style="text-align: center">
							<c:if test="${processamento.consultaDiscenteLiberada}">
								<h:commandLink title="Suspender a Consulta ao Resultado para o Discente" action="#{processamentoAvaliacaoInstitucional.suspendeConsulta}" style="border: 0;">
									<f:param name="id" value="#{processamento.id}" />
									<f:param name="discente" value="true" />
									<h:graphicImage value="/img/delete.png"style="overflow: visible;"/><br/>
									Suspender
								</h:commandLink>
							</c:if>
							<c:if test="${not processamento.consultaDiscenteLiberada}">
								<h:commandLink title="Liberar a Consulta ao Resultado para o Discente" action="#{processamentoAvaliacaoInstitucional.liberaConsulta}" style="border: 0;">
									<f:param name="id" value="#{processamento.id}" />
									<f:param name="discente" value="true" />
									<h:graphicImage value="/img/check.png" style="overflow: visible;"/><br/>
									Liberar
								</h:commandLink>
							</c:if>
						</td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="7" align="center">
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{processamentoAvaliacaoInstitucional.cancelar}" id="cancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
		<br/>
		
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
