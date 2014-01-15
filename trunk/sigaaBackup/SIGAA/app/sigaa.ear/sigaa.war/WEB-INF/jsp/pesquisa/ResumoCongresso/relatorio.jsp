<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<link rel="stylesheet" media="all" href="/shared/javascript/tablekit/style.css"/>
<script type="text/javascript" src="/shared/loadScript?src=javascript/tablekit/fastinit.js"></script>
<script type="text/javascript" src="/shared/loadScript?src=javascript/tablekit/tablekit.js"></script>

<%@page import="br.ufrn.sigaa.pesquisa.form.ResumoCongressoForm"%>

<h2><ufrn:subSistema /> &gt; <c:out value="Resumos para Congresso de Iniciação Científica" /></h2>

<html:form action="/pesquisa/resumoCongresso">

<table class="formulario">
	<caption>Consulta de Resumos</caption>
	<tr>
		<td colspan="2" class="obrigatorio" style="text-align: right; padding-right: 13px;"><label for="congresso">Congresso:</label> </td>
		<td>
			<html:select property="idCongresso" onchange="$('congresso').checked = true;">
				<html:options collection="congressos" property="id" labelProperty="descricao" />
			</html:select>
			</td>
	</tr>

	<c:choose>
		<c:when test="${acesso.pesquisa}">
			<tr>
				<td> <html:checkbox property="filtros" styleId="centro"  value="<%= String.valueOf(ResumoCongressoForm.BUSCA_CENTRO) %>" 
							styleClass="noborder" /> </td>
				<td> <label for="centro"> Centro/Unidade: </label> </td>
				<td>
					<html:select property="centro.id" style="width:90%" onchange="$(centro).checked = true;">
				        <html:option value="-1"> -- SELECIONE -- </html:option>
				        <html:options collection="centros" property="id" labelProperty="codigoNome" />
			        </html:select>
				</td>
			</tr>
		</c:when>
		<c:otherwise>
			<tr>
				<td> <html:hidden property="filtros" styleId="centro"  value="<%= String.valueOf(ResumoCongressoForm.BUSCA_CENTRO) %>" /> </td>
				<td> <label for="centro"> Centro: </label> </td>
				<td> ${ usuario.unidade.gestora.codigoNome } </td>
			</tr>
		</c:otherwise>
	</c:choose>

	<tr>
		<td> <html:checkbox property="filtros" styleId="areaConhecimento"  value="<%= String.valueOf(ResumoCongressoForm.BUSCA_AREA_CONHECIMENTO) %>" 
					styleClass="noborder" /> </td>
		<td> <label for="areaConhecimento"> Área de Conhecimento: </label> </td>
		<td>
			<html:select property="obj.areaConhecimentoCnpq.id" style="width:90%" onchange="$('areaConhecimento').checked = true;">
	        	<html:option value="-1"> -- SELECIONE -- </html:option>
	        	<html:options collection="areasConhecimento" property="id" labelProperty="nome" />
        	</html:select>
		</td>
	</tr>
	<tr>
		<td> <html:checkbox property="filtros" styleId="codigo" value="<%="" + ResumoCongressoForm.BUSCA_CODIGO%>" styleClass="noborder" /> </td>
		<td> <label for="codigo">Código:</label> </td>
		<td> <html:text property="obj.codigo" onfocus="$('codigo').checked = true;" size="6" maxlength="6" onkeyup="CAPS(this);" /> </td>
	</tr>
	<tr>
		<td> <html:checkbox property="filtros" styleId="aluno" value="<%="" + ResumoCongressoForm.BUSCA_NOME_AUTOR%>" styleClass="noborder" /> </td>
		<td> <label for="aluno">Nome do Autor:</label> </td>
		<td> <html:text property="autor.nome" onfocus="$('aluno').checked = true;" size="80" maxlength="100" /> </td>
	</tr>
	<tr>
		<td><html:checkbox property="filtros" styleId="cpf" value="<%= String.valueOf(ResumoCongressoForm.BUSCA_CPF_AUTOR) %>" styleClass="noborder" /></td>
		<td><label for="cpf">CPF do Autor:</label> </td>
		<td><html:text property="cpf" maxlength="14" size="14" onblur="formataCPF(this, event, null)" onfocus="$('cpf').checked = true;" /></td>
	</tr>
	<tr>
		<td> <html:checkbox property="filtros" styleId="orientador" value="<%="" + ResumoCongressoForm.BUSCA_ORIENTADOR%>" styleClass="noborder" /> </td>
		<td> <label for="orientador">Nome do Orientador:</label> </td>
		<td> <html:text property="orientador.nome" onfocus="$('orientador').checked = true;" size="80" maxlength="100" /> </td>
	</tr>
	<tr>
		<td><html:checkbox property="filtros" styleId="status" value="<%= String.valueOf(ResumoCongressoForm.BUSCA_STATUS) %>" styleClass="noborder" /></td>
		<td><label for="status">Status:</label> </td>
		<td>
			<html:select property="obj.status" onchange="$('status').checked = true;">
				<html:options collection="tiposStatus" property="key" labelProperty="value" />
			</html:select>
		</td>
	</tr>
	<tr>
		<td><html:checkbox property="relatorio" styleId="relatorio" styleClass="noborder" /></td>
		<td colspan="3"><label for="relatorio">Gerar Relatório <small>(PDF)</small></label> </td>
	</tr>
	<tfoot>
		<tr>
			<td colspan="3">
				<html:hidden property="buscar" value="true" />
				<html:button dispatch="relatorio" value="Buscar" />
				<html:button dispatch="cancelar" value="Cancelar" />
			</td>
		</tr>
	</tfoot>
</table>
</html:form>
<br />&nbsp;

<c:forEach items="${formResumoCongresso.filtros}" var="filtro">
 	<jsp:useBean id="filtro" type="java.lang.Integer" />
 	<c:if test="<%= filtro.intValue() == ResumoCongressoForm.BUSCA_AREA_CONHECIMENTO%>">
 		<script> $('areaConhecimento').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == ResumoCongressoForm.BUSCA_CENTRO%>">
 		<script> $('centro').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == ResumoCongressoForm.BUSCA_NOME_AUTOR%>">
 		<script> $('aluno').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == ResumoCongressoForm.BUSCA_CPF_AUTOR%>">
 		<script> $('cpf').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == ResumoCongressoForm.BUSCA_STATUS%>">
 		<script> $('status').checked = true;</script>
 	</c:if>
</c:forEach>

<c:if test="${not empty resumos}">
<div class="infoAltRem">
	<html:img page="/img/pesquisa/view.gif" style="overflow: visible;" />
	 : Visualizar Resumo
	<c:if test="${ acesso.pesquisa }">
		 <html:img page="/img/pesquisa/certificado.png" style="overflow: visible;" />
		 : Emitir Certificado <br />
		<html:img page="/img/alterar.gif" style="overflow: visible;" />
		 : Alterar Resumo
		<html:img page="/img/delete.gif" style="overflow: visible;" />
		 : Remover Resumo
	</c:if>
</div>

<table class="listagem sortable">
	<caption> Resumos encontrados (${fn:length(resumos)}) </caption>
	<thead>
		<tr>
			<th>Código</th>
			<th class="sortfirstasc">Autor</th>
			<th>Orientador</th>
			<th>Status</th>
			<th>Painel Nº</th>
			<th nowrap="nowrap" class="nosort"></th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="resumo" items="${resumos}" varStatus="loop">
			<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
				<td> ${resumo.codigo} </td>
				<td> ${resumo.autor.nome}</td>
				<td> ${resumo.orientador.nome}</td>

				<td> ${resumo.statusString} </td>
				<td> ${resumo.numeroPainel} </td>
				<td nowrap="nowrap">
					<ufrn:link action="/pesquisa/resumoCongresso" param="id=${resumo.id}&dispatch=view">
						<img src="${ctx}/img/pesquisa/view.gif" alt="Visualizar Resumo" title="Visualizar Resumo" />
					</ufrn:link>
					<c:if test="${ acesso.pesquisa }">
					<ufrn:link action="/pesquisa/resumoCongresso" param="id=${resumo.id}&dispatch=emitirCertificado">
						<img src="${ctx}/img/pesquisa/certificado.png" alt="Emitir Certificado" title="Emitir Certificado" />
					</ufrn:link>

					<html:link action="/pesquisa/resumoCongresso.do?id=${resumo.id}&dispatch=edit">
						<img src="${ctx}/img/alterar.gif" alt="Alterar Resumo" title="Alterar Resumo" />
					</html:link>

					<html:link action="/pesquisa/resumoCongresso.do?obj.id=${resumo.id}&dispatch=remove" 
							onclick="return confirm('Deseja realmente remover este resumo?');">
						<img src="${ctx}/img/delete.gif" alt="Remover Resumo" title="Remover Resumo" />
					</html:link>
					</c:if>
				</td>
			</tr>
		</c:forEach>
	</tbody>
<%--
	<tfoot>
		<tr>
			<td colspan="6" align="center"><b> Total de resumos submetidos: ${fn:length(resumos)} </b> </td>
		</tr>
	</tfoot>
 --%>
</table>
</c:if>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>