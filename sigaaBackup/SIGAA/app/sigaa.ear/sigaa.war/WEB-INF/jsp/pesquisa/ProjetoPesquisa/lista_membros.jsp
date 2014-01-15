<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2>
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	Membros do Projeto
</h2>

<c:choose>
<c:when test="${not empty membros }">

<div class="infoAltRem">
	<html:img page="/img/pesquisa/view.gif" style="overflow: visible;"/>: Emitir Declaração
</div>

<table class="listagem">
	<caption>Membros Encontrados (${fn:length(membros)})</caption>
	<thead>
		<tr>
			<th style="text-align: left;"> Membro </th>
			<th style="text-align: left;"> Função Membro </th>
			<th></th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="membro" items="${membros}" varStatus="loop">
		<tr class="${ loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			<td style="text-align: left;">${membro.pessoa.nome}</td>
			<td style="text-align: left;">${membro.funcaoMembro.descricao}</td>


			<td>
				<ufrn:link action="/pesquisa/emitirDeclaracaoDocente" param="obj.id=${membro.id}&dispatch=emitirDeclaracaoCoordenacao">
					<img src="${ctx}/img/pesquisa/view.gif"
						alt="Emitir Declaração"
						title="Emitir Declaração" />
				</ufrn:link>
			</td>
		</tr>
		</c:forEach>
	</tbody>
</table>
  <br />
  <div class="btn volta" align="center">
	<a href="javascript:history.go(-1);"><span> << Voltar</span></a>
  </div> 

</c:when>
</c:choose>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>