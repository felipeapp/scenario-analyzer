<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.pesquisa.form.MembroProjetoServidorForm"%>

<%
	MembroProjetoServidorForm membroForm = (MembroProjetoServidorForm) request.getAttribute("formMembroProjetoServidor");
	int finalidade = membroForm.getFinalidadeBusca();
%>

<h2>
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	
	<c:if test="<%= finalidade == MembroProjetoServidorForm.DECLARACAO_COORDENACAO %>">
		Declaração de Coordenação de Projeto
	</c:if>

	<c:if test="<%= finalidade == MembroProjetoServidorForm.DECLARACAO_ORIENTACOES %>">
		Declaração de Orientações
	</c:if>
</h2>

<html:form action="/pesquisa/buscarMembrosProjeto" method="post">
<html:hidden property="finalidadeBusca"/>

<table class="formulario" align="center" style="width: 99%">
	<caption class="listagem">Buscar Docente Membro do Projeto</caption>
	<tbody>
	<tr>
		<td width="15%;" style="text-align: right;"> 
 			<html:checkbox property="filtros" styleId="anoProjeto" 	
				styleClass="noborder" value="<%="" + MembroProjetoServidorForm.BUSCA_ANO%>"/> Ano dos Projetos:</td>
		<td>
			<html:text property="obj.projeto.ano" size="4" maxlength="4" 
				onkeyup="formatarInteiro(this)" 
			    onfocus="$(anoProjeto).checked = true;"/>
		</td>
	</tr>
	<tr>
		<%--  <td> <html:checkbox property="filtros" styleId="coordenador" value="<%="" + MembroProjetoServidorForm.BUSCA_COORDENADOR%>" styleClass="noborder"/> </td> --%>
		<td style="text-align:right; vertical-align: middle;"> 
		<br /><br />Membro do Projeto:<span class="required"></span> </td>
		<td style="text-align: left; vertical-align: bottom;">
			<c:set var="idAjax" value="obj.servidor.id"/>
			<c:set var="nomeAjax" value="obj.servidor.pessoa.nome"/>
			<c:set var="todosDocentes" value="true"/>
			<c:set var="buscaInativos" value="true"/>
			<%@include file="/WEB-INF/jsp/include/ajax/docente.jsp" %>
		</td>
	</tr>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
				<html:button dispatch="listar" value="Buscar"/>
				<html:button dispatch="cancelar" value="Cancelar" cancelar="true"/>
			</td>
		</tr>
	</tfoot>
</table>
</html:form>
<br/>
<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>
<c:forEach items="${projetoPesquisaForm.filtros}" var="filtro">
 	<jsp:useBean id="filtro" type="java.lang.Integer"/>
	<c:if test="<%= filtro.intValue() == MembroProjetoServidorForm.BUSCA_ANO%>">
 		<script> $('anoProjeto').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == MembroProjetoServidorForm.BUSCA_COORDENADOR%>">
 		<script> $('coordenador').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == MembroProjetoServidorForm.BUSCA_AREA_CONHECIMENTO%>">
 		<script> $('areaConhecimento').checked = true;</script>
 	</c:if>
</c:forEach>

<br/> <br/>

<c:choose>
<c:when test="${not empty lista }">

<div class="infoAltRem">
	<html:img page="/img/pesquisa/view.gif" style="overflow: visible;"/>: Emitir Declaração
</div>

<table class="listagem">
	<caption>Projetos Encontrados (${fn:length(lista)})</caption>
	<thead>
		<tr>
			<th style="text-align: center;"> Ano </th>
			<th> Título do Projeto </th>
			<th> Status </th>
			<th>  </th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="membro" items="${lista}" varStatus="loop">
		<tr class="${ loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			<td style="text-align: center;">${membro.projeto.ano}</td>
			<td>${membro.projeto.titulo}</td>
			<td>${membro.projeto.situacaoProjeto.descricao}</td>
			
			<c:if test="<%= finalidade == MembroProjetoServidorForm.DECLARACAO_COORDENACAO %>">
			<td>
				<ufrn:link action="/pesquisa/emitirDeclaracaoDocente" param="obj.id=${membro.id}&dispatch=emitirDeclaracaoCoordenacao">
					<img src="${ctx}/img/pesquisa/view.gif"
						alt="Emitir Declaração"
						title="Emitir Declaração" />
				</ufrn:link>
			</td>
			</c:if>
			
			<c:if test="<%= finalidade == MembroProjetoServidorForm.DECLARACAO_ORIENTACOES %>">
			<td>
				<ufrn:link action="/pesquisa/emitirDeclaracaoDocente" param="obj.id=${membro.id}&dispatch=emitirDeclaracaoOrientacoes">
					<img src="${ctx}/img/pesquisa/view.gif"
						alt="Emitir Declaração"
						title="Emitir Declaração" />
				</ufrn:link>
			</td>
			</c:if>
		</tr>
		</c:forEach>
	</tbody>
</table>
</c:when>
</c:choose>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>