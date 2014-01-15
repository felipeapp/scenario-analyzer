<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	table.listagem tr.vazio td {
		padding: 12px 0;
		text-align: center;
	}

	table.listagem tr.tipoResultado td {
		font-size: 1.1em;
		color: #336;
		padding: 3px 5px 2px;
		font-variant: small-caps;
		background: #C4D2EB;
		border: 1px solid #C4D2EB;
		font-weight: bold;
		border-width: 1px 0;
	}

</style>

<h2>
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> >
	<a href="javascript: history.go(-1)"> Análise de Projetos de Pesquisa</a> >
	Confirmação
</h2>

<html:form action="/pesquisa/analisarAvaliacoes" method="post">
	<table class="listagem" align="center" style="width:75%">
	<caption>Quadro-Resumo com o Resultado da Análise</caption>
	<thead>
		<tr>
			<th>Código do Projeto</th>
			<th>Avaliações Distribuídas</th>
			<th>Avaliações Realizadas</th>
			<th style="text-align: right;">Média</th>
			<th> </th>
		</tr>
	</thead>
	<tbody>
	<!-- projetos aprovados -->

	<tr class="tipoResultado">
		<td colspan="5"> Projetos aprovados </td>
	</tr>
	<c:choose>
		<c:when test="${not empty formAnalisarAvaliacoes.projetosAprovados}">
			<c:forEach items="${formAnalisarAvaliacoes.projetosAprovados}" var="projeto" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td><b>${projeto.codigo}</b></td>
				<td align="center">${projeto.qtdAvaliacoesSubmetidas}</td>
				<td align="center">${projeto.qtdAvaliacoesRealizadas}</td>
				<td align="right">
					<span style="${projeto.media < 5.0 ? "color: #922" : "color: #292" }; font-weight: bold; font-size: 1.1em;">
					<ufrn:format type="valor1" name="projeto" property="media"/>
					</span>
				</td>
				<td>
					<html:link action="/pesquisa/avaliarProjetoPesquisa?dispatch=resumoAvaliacoes&obj.projetoPesquisa.id=${projeto.id}">
						<img src="${ctx}/img/view.gif"
							title="Visualizar Resumo das Avaliações"
							alt="Visualizar Resumo das Avaliações"/>
					</html:link>
				</td>
			</tr>
			</c:forEach>
		</c:when>
		<c:otherwise>
			<tr class="vazio">
				<td colspan="5"> Nenhum projeto foi selecionado como aprovado </td>
			</tr>
		</c:otherwise>
	</c:choose>

	<!-- projetos reprovados -->
	<tr class="tipoResultado">
		<td colspan="5"> Projetos reprovados </td>
	</tr>
	<c:choose>
		<c:when test="${not empty formAnalisarAvaliacoes.projetosReprovados}">
			<c:forEach items="${formAnalisarAvaliacoes.projetosReprovados}" var="projeto" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td><b>${projeto.codigo}</b></td>
				<td align="center">${projeto.qtdAvaliacoesSubmetidas}</td>
				<td align="center">${projeto.qtdAvaliacoesRealizadas}</td>
				<td align="right">
					<span style="${projeto.media < 5.0 ? "color: #922" : "color: #292" }; font-weight: bold; font-size: 1.1em;">
					<ufrn:format type="valor1" name="projeto" property="media"/>
					</span>
				</td>
				<td>
					<html:link action="/pesquisa/avaliarProjetoPesquisa?dispatch=resumoAvaliacoes&obj.projetoPesquisa.id=${projeto.id}">
						<img src="${ctx}/img/view.gif"
							title="Visualizar Resumo das Avaliações"
							alt="Visualizar Resumo das Avaliações"/>
					</html:link>
				</td>
			</tr>
			</c:forEach>
		</c:when>
		<c:otherwise>
			<tr class="vazio">
				<td colspan="5"> Nenhum projeto foi selecionado como reprovado </td>
			</tr>
		</c:otherwise>
	</c:choose>

	<!-- projetos indefinidos -->
	<tr class="tipoResultado">
		<td colspan="5"> Projetos sem avaliação definida </td>
	</tr>
	<c:choose>
		<c:when test="${not empty formAnalisarAvaliacoes.projetosIndefinidos}">
			<c:forEach items="${formAnalisarAvaliacoes.projetosIndefinidos}" var="projeto" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td><b>${projeto.codigo}</b></td>
				<td align="center">${projeto.qtdAvaliacoesSubmetidas}</td>
				<td align="center">${projeto.qtdAvaliacoesRealizadas}</td>
				<td align="right">
					<span style="${projeto.media < 5.0 ? "color: #922" : "color: #292" }; font-weight: bold; font-size: 1.1em;">
					<ufrn:format type="valor1" name="projeto" property="media"/>
					</span>
				</td>
				<td>
					<html:link action="/pesquisa/avaliarProjetoPesquisa?dispatch=resumoAvaliacoes&obj.projetoPesquisa.id=${projeto.id}">
						<img src="${ctx}/img/view.gif"
							title="Visualizar Resumo das Avaliações"
							alt="Visualizar Resumo das Avaliações"/>
					</html:link>
				</td>
			</tr>
			</c:forEach>

		</c:when>
		<c:otherwise>
			<tr class="vazio">
				<td colspan="5"> Todos os projetos da lista tiveram sua avaliação definida </td>
			</tr>
		</c:otherwise>
	</c:choose>

	</tbody>

	<tfoot>
		<tr>
			<td colspan="5" align="center">
				<html:button  property="" onclick="javascript:history.go(-1);"> << Voltar </html:button>
		        <html:button dispatch="finalizar" value="Confirmar"/>
		   		<html:button dispatch="cancelar" value="Cancelar"/>
			</td>
		</tr>
	</tfoot>

	</table>
</html:form>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>