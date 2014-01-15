<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>

	<h2 class="tituloPagina"><ufrn:subSistema /> > Consolida&ccedil;&atilde;o de Disciplinas</h2>
	
	<input type="hidden" name="gestor" value="${ param['gestor'] }"/>
	
	<h:outputText value="#{ consolidacaoIndividualMedio.create }"/>
	
	<div class="descricaoOperacao">Selecione uma das disciplinas que o aluno encontra-se matriculado.</div>
	
	<div class="infoAltRem" style="width: 100%">
		<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Selecionar Disciplina
	</div>
	<table class="formulario" width="80%">
		<caption>Disciplinas do Discente</caption>
		<thead>
			<tr><th>Disciplina</th><th></th></tr>
		</thead>
		<tbody>
		<c:forEach items="${ consolidacaoIndividualMedio.matriculasAbertas }" var="mc" varStatus="loop">
		<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
			<td>${ mc.serieAnoTurma }</td>
			<td>
			<h:form prependId="false"> 
				<input type="hidden" name="id" value="${mc.id }">
				<h:commandLink action="#{consolidacaoIndividualMedio.escolherDisciplina}" title="Selecionar Disciplina" id="disciplina">
					<h:graphicImage url="/img/seta.gif" />
				</h:commandLink>
			</h:form>
			</td>
		</tr>
		</c:forEach>
		</tbody>
		<tfoot>
		<tr>
			<td colspan="2">
			<h:form>
				<h:commandButton action="#{ consolidacaoIndividualMedio.iniciar }" value="<< Selecionar outro Discente" id="voltar" />
				<h:commandButton action="#{ consolidacaoIndividualMedio.cancelar }" value="Cancelar" immediate="true" onclick="#{confirm}" id="cancelar"/>
			</h:form>
			</td>
		</tr>
		</tfoot>
	</table>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>