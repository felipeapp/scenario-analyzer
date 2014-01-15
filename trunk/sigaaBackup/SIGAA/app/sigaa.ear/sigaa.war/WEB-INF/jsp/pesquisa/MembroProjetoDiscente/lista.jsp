<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.pesquisa.form.MembroProjetoDiscenteForm"%>

<%
	MembroProjetoDiscenteForm membroForm = (MembroProjetoDiscenteForm) request.getAttribute("membroProjetoDiscenteForm");
	int finalidade = membroForm.getFinalidadeBusca();
%>

<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;

	<c:if test="<%= finalidade == MembroProjetoDiscenteForm.EMISSAO_DECLARACAO %>">
		Declaração de Bolsista
	</c:if>

	<c:if test="<%= finalidade == MembroProjetoDiscenteForm.PARECER_RELATORIOS_PARCIAIS %>">
		Pareceres de Relatórios Parciais
	</c:if>

	<c:if test="<%= finalidade == MembroProjetoDiscenteForm.PARECER_RELATORIOS_FINAIS %>">
		Pareceres de Relatórios Finais
	</c:if>

</h2>

<html:form action="/pesquisa/buscarMembroProjetoDiscente" method="post">
<html:hidden property="finalidadeBusca"/>

<table class="formulario" width="95%">
    <caption>Informe os critérios de busca do bolsista</caption>
    <tbody>
       <tr>
       		<td> <html:radio property="tipoBusca" value="<%=String.valueOf(MembroProjetoDiscenteForm.BUSCA_ORIENTADOR)%>"></html:radio> </td>
       		<td> Orientador: </td>
       		<td>
       			<c:set var="idAjax" value="obj.planoTrabalho.orientador.id"/>
				<c:set var="nomeAjax" value="obj.planoTrabalho.orientador.pessoa.nome"/>
				<c:set var="buscaInativos" value="true"/>
				<%@include file="/WEB-INF/jsp/include/ajax/docente.jsp" %>
       		</td>
       </tr>
	   <tr>
       		<td> <html:radio property="tipoBusca" value="<%=String.valueOf(MembroProjetoDiscenteForm.BUSCA_DISCENTE)%>"></html:radio> </td>
       		<td> Discente: </td>
       		<td>
       			<c:set var="idAjax" value="obj.discente.id"/>
                <c:set var="nomeAjax" value="obj.discente.pessoa.nome"/>
				<c:set var="statusDiscente" value="todos"/>
                <%@include file="/WEB-INF/jsp/include/ajax/discente.jsp" %>
       		</td>
       </tr>
	   <tr>
       		<td> <html:radio property="tipoBusca" value="<%=String.valueOf(MembroProjetoDiscenteForm.BUSCA_TODOS_ATIVOS)%>" styleId="todos"/> </td>
       		<td colspan="2"> <label for="todos"> Todos os bolsistas ativos </label> </td>
       </tr>
    </tbody>
	<tfoot>
		<tr>
			<td colspan="3">
				<html:button dispatch="listar" value="Buscar"/>
				<html:button dispatch="cancelar" value="Cancelar"/>
			</td>
		</tr>
	</tfoot>
</table>
</html:form>


<br/>
<c:if test="${empty lista}">
	<div align="center">
		Nenhum registro encontrado!
	</div>
</c:if>

<c:if test="${!empty lista}">
	<table class="listagem" style="width: 100%;">
		<caption> Registros encontrados </caption>

		<thead>
		<tr>
			<th> Matrícula </th>
			<th align="left"> Nome </th>
			<th align="left"> Orientador </th>
			<th align="left"> Projeto de Pesquisa </th>
			<th> </th>
		</tr>
		</thead>

		<tbody>
			<c:forEach items="${lista}" var="membro" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td> ${membro.discente.matricula} </td>
				<td> ${membro.discente.pessoa.nome} </td>
				<td> ${membro.planoTrabalho.orientador.pessoa.nome} </td>
				<td> ${membro.planoTrabalho.projetoPesquisa.codigo} </td>

				<c:if test="<%= finalidade == MembroProjetoDiscenteForm.EMISSAO_DECLARACAO %>">
					<td>
						<html:link action="/pesquisa/emitirDeclaracaoDiscente?dispatch=emitir&obj.id=${membro.id}">
                      		 <img src="<%= request.getContextPath() %>/img/view.gif"
                      		 	alt="Emitir Declaração"
                      		 	title="Emitir Declaração" border="0"/>
						</html:link>
					</td>
				</c:if>

				<c:if test="<%= finalidade == MembroProjetoDiscenteForm.PARECER_RELATORIOS_PARCIAIS %>">
					<td align="center">
						<c:if test="${empty membro.planoTrabalho.relatorioBolsaParcial}">
							SEM RELATÓRIO
						</c:if>
						<c:if test="${not empty membro.planoTrabalho.relatorioBolsaParcial}">
							${membro.planoTrabalho.relatorioBolsaParcial}
						</c:if>
					</td>
				</c:if>

				<c:if test="<%= finalidade == MembroProjetoDiscenteForm.PARECER_RELATORIOS_FINAIS %>">
					<td align="center">
						<c:if test="${empty membro.relatoriosDiscenteFinais}">
							SEM RELATÓRIO
						</c:if>
						<c:if test="${not empty membro.relatoriosDiscenteFinais}">
							${membro.relatoriosDiscenteFinais}
						</c:if>
					</td>
				</c:if>

			</tr>
			</c:forEach>
		</tbody>

		<tfoot>
			<tr>
				<td colspan="5" align="center"> <em>${fn:length(lista)} registro(s) encontrado(s)</em> </td>
			</tr>
		</tfoot>
	</table>
</c:if>



<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>