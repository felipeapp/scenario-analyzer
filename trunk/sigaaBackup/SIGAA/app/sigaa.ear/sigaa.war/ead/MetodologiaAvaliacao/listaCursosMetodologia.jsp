<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<% CheckRoleUtil.checkRole(request, response, new int[] { SigaaPapeis.SEDIS, SigaaPapeis.COORDENADOR_PEDAGOGICO_EAD }); %>

<f:view>
	<h:form>
		<h2><ufrn:subSistema /> > Metodologias de Avaliação</h2>
	
		<center>
			<div class="infoAltRem">
				<h:graphicImage value="/img/seta.gif"style="overflow: visible;"/>: Selecionar Curso
			</div>
		</center>	
	
		<table class="listagem">
			<caption>Selecione o curso</caption>
			<c:forEach items="#{curso.allCursosGraduacaoEADCombo}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">				
					<td>
						<h:outputText value="#{item.label}"/>
					</td>
					<td>
						<h:commandLink action="#{metodologiaAvaliacaoEad.selecionarCurso}">
							<f:param name="id" value="#{item.value}" />
							<h:graphicImage value="/img/seta.gif" alt="Selecionar" title="Selecionar" />
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
			<tfoot>
				<tr align="center">
					<td colspan="2">
						<h:commandButton value="Cancelar" action="#{metodologiaAvaliacaoEad.cancelar}" onclick="#{confirm}" immediate="true"/>
					</td>
				</tr>
			</tfoot>			
		</table>
	
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>