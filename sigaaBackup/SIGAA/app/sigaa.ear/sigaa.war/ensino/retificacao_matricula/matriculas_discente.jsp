<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h2 class="title"> <ufrn:subSistema /> &gt; Retificação de Aproveitamento e Consolidação de Turmas > Matrículas do Discente</h2>

	<h:messages showDetail="true"></h:messages>
	<h:outputText value="#{retificacaoMatricula.create}" />
	<c:set value="#{retificacaoMatricula.discenteEscolhido}" var="discente" />
	<%@ include file="/graduacao/info_discente.jsp"%>

	<center>
		<div class="infoAltRem" style="width: 90%">
			<h4>Legenda</h4>
			<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Selecionar Matrícula<br/>
		</div>
	</center>

		<table class="formulario" width="90%">
			<caption class="listagem">Selecione um Componente Curricular</caption>
			<tr>
				<td colspan="2">
					<table class="listagem">
						<thead>
							<tr>
								<td></td>
								<td>Componente Curricular</td>
								<td style="text-align: center;">
									<c:choose>
										<c:when test="${retificacaoMatricula.conceito}">
											Conceito
										</c:when>
										<c:when test="${retificacaoMatricula.aptidao}">
											Apto
										</c:when>
										<c:otherwise>
											Média Final
										</c:otherwise>
									</c:choose>
								</td>
								<td>Situação</td>
								<td></td>
							</tr>
						</thead>
						<c:forEach items="${retificacaoMatricula.matriculasConcluidas}" var="mat" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
							<td width="5%"><b>${mat.anoPeriodo }</b></td>
							<td>${mat.componente.descricao}</td>
							<td width="3%" style="text-align: center;">

							<c:choose>
								<c:when test="${retificacaoMatricula.conceito}">
									${mat.conceitoChar }
								</c:when>
								<c:when test="${retificacaoMatricula.aptidao}">
									${mat.apto }
								</c:when>
								<c:otherwise>
									${mat.mediaFinal}
								</c:otherwise>
							</c:choose>
							</td>
							<td width="7%">${mat.situacaoMatricula.descricao } </td>
							<td width="2%">
							<h:form>
							<h:commandLink action="#{retificacaoMatricula.selecionarMatricula}" id="select">
							<input type="hidden" name="id" value="${mat.id }">
							<h:graphicImage url="/img/seta.gif" title="Selecionar Matrícula" />
							</h:commandLink>
							</h:form>
							</td>
						</tr>
						</c:forEach>
					</table>
				</td>
			</tr>
			<tfoot>
			<tr>
				<td colspan="2">
				<h:form>
				<h:commandButton action="#{retificacaoMatricula.buscarAluno}" value="<< Selecionar outro Discente" id="voltar" />
				<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{retificacaoMatricula.cancelar}" id="cancelar" immediate="true"/> 
				</h:form>
				</td>
			</tr>
			</tfoot>
		</table>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
