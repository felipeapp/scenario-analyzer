<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2>
	<ufrn:subSistema /> &gt; Renova��o de Projetos de Pesquisa
</h2>

<c:if test="${not empty lista}">

	<div class="descricaoOperacao">
		<p>
			<strong>Caro docente,</strong>
		</p>
		<p>
			Abaixo est�o listados os projetos de pesquisa coordenados por voc� que s�o pass�veis de renova��o.
		</p>
		<p>
			Caso algum de seus projetos n�o esteja listado abaixo, entre em contato com a <em> ${ siglaNomeGestoraPesquisa } </em>
			para maiores esclarecimentos.
		</p>
		<p style="text-align: center">
			<b> ATEN��O: </b> <br />
			Existe um limite de ${ qtdMaximaRenovacoes } renova��es por projeto. Atingido este limite, � necess�rio
			recadastrar o projeto.
		</p>
	</div>

	<div class="infoAltRem">
		<html:img page="/img/pesquisa/avaliar.gif" style="overflow: visible;"/>
	    : Renovar Projeto
	</div>

	<table cellpadding="3" class="listagem">
		<caption class="listagem">Projetos de Pesquisa</caption>
		<thead>
			<tr>
				<th>C�digo</th>
				<th>T�tulo</th>
				<th>Tipo</th>
				<th>Situa��o</th>
				<th>Num. Renova��es</th>
				<th></th>
			</tr>
		</thead>

		<c:forEach items="${lista}" var="projeto" varStatus="status">
		<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
			<td align="center" nowrap="nowrap">
				${projeto.codigo}
			</td>
			<td>
				${projeto.titulo}
			</td>
			<td> ${ projeto.interno ? "INTERNO" : "EXTERNO" } </td>
			<td align="center" nowrap="nowrap">
				${projeto.situacaoProjeto.descricao}
			</td>
			<td style="text-align: right">
				${projeto.numeroRenovacoes}
			</td>
			<td align="center">
				<c:choose>
				<c:when test="${projeto.numeroRenovacoes < qtdMaximaRenovacoes}">
					<html:link action="/pesquisa/projetoPesquisa/criarProjetoPesquisa?dispatch=renovar&obj.id=${projeto.id}">
						<img src="${ctx}/img/pesquisa/avaliar.gif"
							title="Renovar Projeto"
							alt="Renovar Projeto"/>
					</html:link>
				</c:when>
				<c:otherwise>
					<img src="${ctx}/img/pesquisa/avaliar.gif"
						title="Este projeto atingiu o limite de renova��es estabelecido"
						alt="Este projeto atingiu o limite de renova��es estabelecido"/>
				</c:otherwise>
				</c:choose>
			</td>

		</tr>
		 </c:forEach>

	</table>
</c:if>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
