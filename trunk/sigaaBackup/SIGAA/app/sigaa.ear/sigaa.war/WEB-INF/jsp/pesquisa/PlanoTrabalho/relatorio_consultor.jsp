<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.pesquisa.struts.TipoFiltroPlanoTrabalho"%>

<h2 class="tituloPagina">
	<html:link action="/verPortalConsultor">Portal do Consultor</html:link> &gt;
	Planos de Trabalho
</h2>

<html:form action="/pesquisa/planoTrabalho/consulta" method="post">

	<table class="formulario" align="center" style="width: 99%">
		<caption class="listagem">Critérios de Busca</caption>

	 	<tr>
			<td> <html:checkbox property="filtros" styleId="grupoPesquisa" value='<%="" + TipoFiltroPlanoTrabalho.GRUPO_PESQUISA%>' styleClass="noborder"/> </td>
			<td nowrap="nowrap"> <label for="grupoPesquisa">Grupo de Pesquisa:</label> </td>
			<td>
				<html:select property="grupoPesquisa" style="width:90%" onchange="$(grupoPesquisa).checked = true;">
					<html:option value="-1">  -- SELECIONE UM GRUPO DE PESQUISA --  </html:option>
					<html:options collection="gruposPesquisa" property="id" labelProperty="nome" />
				</html:select>
			</td>
		</tr>
		<tr>
			<td> <html:checkbox property="filtros" styleId="centro" value='<%="" + TipoFiltroPlanoTrabalho.CENTRO%>' styleClass="noborder"/> </td>
			<td> <label for="centro">Centro/Unidade:</esquisalabel> </td>
			<td>
				<html:select property="unidade.gestora.id" style="width:90%" onchange="$(centro).checked = true;">
					<html:option value="-1">  -- SELECIONE --  </html:option>
					<html:options collection="centros" property="unidade.id" labelProperty="unidade.codigoNome" />
				</html:select>
			</td>
		</tr>
		<tr>
			<td> <html:checkbox property="filtros" styleId="unidade" value='<%="" + TipoFiltroPlanoTrabalho.DEPARTAMENTO%>' styleClass="noborder"/> </td>
			<td> <label for="unidade">Departamento:</label> </td>
			<td>
				<c:set var="idAjax" value="unidade.id"/>
		    	<c:set var="nomeAjax" value="unidade.nome"/>
				<%@include file="/WEB-INF/jsp/include/ajax/unidade.jsp" %>
				<script type="text/javascript">
					function unidadeOnFocus() {
						getEl('unidade').dom.checked = true;
					}
				</script>
			</td>
		</tr>

		<tr>
			<td> <html:checkbox property="filtros" styleId="aluno" value='<%="" + TipoFiltroPlanoTrabalho.NOME%>' styleClass="noborder"/> </td>
			<td> <label for="aluno">Aluno:</label> </td>
			<td>
                <c:set var="idAjax" value="obj.membroProjetoDiscente.discente.id"/>
                <c:set var="nomeAjax" value="obj.membroProjetoDiscente.discente.pessoa.nome"/>
                <c:set var="nivel" value="G"/>
                <%@include file="/WEB-INF/jsp/include/ajax/discente.jsp" %>
                <script type="text/javascript">
					function discenteOnChange() {
						getEl('aluno').dom.checked = true;
					}
				</script>
			</td>
		</tr>

		<tr>
			<td> <html:checkbox property="filtros" styleId="orientador" value='<%="" + TipoFiltroPlanoTrabalho.ORIENTADOR%>' styleClass="noborder"/> </td>
			<td> <label for="orientador">Orientador:</label> </td>
			<td>
				<c:set var="idAjax" value="obj.orientador.id"/>
				<c:set var="nomeAjax" value="obj.orientador.pessoa.nome"/>
				<c:set var="todosDocentes" value="true"/>
				<c:set var="buscaInativos" value="true"/>
				<%@include file="/WEB-INF/jsp/include/ajax/docente.jsp" %>
                <script type="text/javascript">
					function docenteOnChange() {
						getEl('orientador').dom.checked = true;
					}
				</script>
			</td>
		</tr>

		<tr>
			<td> <html:checkbox property="filtros" styleId="cota" value='<%="" + TipoFiltroPlanoTrabalho.COTA%>' styleClass="noborder"/> </td>
			<td> <label for="cota">Cota:</label> </td>
			<td>
				<html:select property="obj.cota.id" style="width:90%" onchange="$('cota').checked = true;">
					<html:option value="-1">  -- SELECIONE UMA COTA --  </html:option>
					<html:options collection="cotas" property="id" labelProperty="descricao" />
				</html:select>
			</td>
		</tr>
		
		<tr>
			<td> <html:checkbox property="filtros" styleId="edital" value='<%="" + TipoFiltroPlanoTrabalho.EDITAL%>' styleClass="noborder"/> </td>
			<td> <label for="edital">Edital:</label> </td>
			<td>
				<html:select property="obj.edital.id" style="width:90%" onchange="$('edital').checked = true;">
					<html:option value="-1">  -- SELECIONE UM EDITAL --  </html:option>
					<html:options collection="editais" property="id" labelProperty="descricao" />
				</html:select>
			</td>
		</tr>

		<tr>
			<td> <html:checkbox property="filtros" styleId="modalidade" value='<%="" + TipoFiltroPlanoTrabalho.MODALIDADE_BOLSA%>' styleClass="noborder"/> </td>
			<td> <label for="modalidade">Modalidade:</label> </td>
			<td>
				<html:select property="obj.tipoBolsa.id" style="width:90%" onchange="$('modalidade').checked = true;">
					<html:option value="-1">  -- SELECIONE UMA MODALIDADE --  </html:option>
					<html:options collection="modalidades" property="key" labelProperty="value" />
				</html:select>
			</td>
		</tr>

		<tr>
			<td> <html:checkbox property="filtros" styleId="status" value='<%="" + TipoFiltroPlanoTrabalho.STATUS_PLANO%>' styleClass="noborder"/> </td>
			<td> <label for="status">Status:</label> </td>
			<td>
				<html:select property="obj.status" style="width:90%" onchange="$('status').checked = true;">
					<html:option value="-1">  -- SELECIONE UM STATUS --  </html:option>
					<html:options collection="status" property="key" labelProperty="value" />
				</html:select>
			</td>
		</tr>

		<tfoot>
			<tr>
				<td colspan="3">
					<html:button dispatch="buscarConsultor" value="Buscar"/>
					<html:button dispatch="cancelar" value="Cancelar" cancelar="true"/>
				</td>
			</tr>
		</tfoot>
	</table>
</html:form>

<c:forEach items="${formPlanoTrabalho.filtros}" var="filtro">
 	<jsp:useBean id="filtro" type="java.lang.Integer"/>
 	<c:if test="<%= filtro.intValue() == TipoFiltroPlanoTrabalho.GRUPO_PESQUISA%>">
 		<script> $('grupoPesquisa').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == TipoFiltroPlanoTrabalho.CENTRO%>">
 		<script> $('centro').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == TipoFiltroPlanoTrabalho.DEPARTAMENTO%>">
 		<script> $('unidade').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == TipoFiltroPlanoTrabalho.NOME%>">
 		<script> $('aluno').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == TipoFiltroPlanoTrabalho.ORIENTADOR%>">
 		<script> $('orientador').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == TipoFiltroPlanoTrabalho.COTA%>">
 		<script> $('cota').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == TipoFiltroPlanoTrabalho.EDITAL%>">
 		<script> $('edital').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == TipoFiltroPlanoTrabalho.MODALIDADE_BOLSA%>">
 		<script> $('modalidade').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == TipoFiltroPlanoTrabalho.STATUS_PLANO%>">
 		<script> $('status').checked = true;</script>
 	</c:if>
</c:forEach>

<c:if test="${empty popular}">
<style>
	table.listagem tr.cota td {
		background: #EEE;
		font-weight: bold;
		padding: 4px 0 2px 10px;
		border-bottom: 1px solid #AAA;
	}
</style>

<br/><br/>
	<c:if test="${!empty lista}">
	
	<div class="infoAltRem">
		<html:img page="/img/pesquisa/view.gif" style="overflow: visible;"/>: Visualizar Plano de Trabalho
	</div>
	
	<table class="listagem">
		<caption> Planos de Trabalho encontrados ( ${ fn:length(lista) } ) </caption>
		<thead>
			<tr>
				<th> Projeto </th>
				<th> Discente </th>
				<th> Tipo da Bolsa </th>
				<th align="left"> Status </th>
				<th> </th>
			</tr>
		</thead>
		<tbody>
			<c:set var="orientador" value=""/>
			<c:set var="cota" value=""/>

			<c:forEach var="plano" items="${lista}" varStatus="status">

			<c:if test="${ orientador != plano.orientador.pessoa.nome }">
				<c:set var="orientador" value="${ plano.orientador.pessoa.nome }"/>
				<c:set var="cota" value=""/>

				<tr>
					<td colspan="5" style="background: #C8D5EC; font-weight: bold; padding: 4px 0 4px 10px;">
						Orientador(a): ${ plano.orientador.pessoa.nome }
					</td>
				</tr>

			</c:if>

			<c:if test="${ cota != plano.cota.descricao }">
				<c:set var="cota" value="${ plano.cota.descricao }"/>

				<c:if test="${empty cota}">
					<tr class="cota">
						<td colspan="5">
							Planos de trabalho não vinculados à cota
						</td>
					</tr>
				</c:if>
				<c:if test="${not empty cota}">
					<tr class="cota">
						<td colspan="5">
							Cota ${ plano.cota.descricao }
						</td>
					</tr>
				</c:if>
			</c:if>

			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td colspan="4">
					<i>${plano.titulo == null ? "Título não definido" : plano.titulo}</i>
				</td>
				<td rowspan="2" align="left" nowrap="nowrap">&nbsp;
					<ufrn:link action="/pesquisa/planoTrabalho/wizard" param="obj.id=${plano.id}&dispatch=view">
						<img src="${ctx}/img/pesquisa/view.gif" alt="Visualizar Plano de Trabalho" title="Visualizar Plano de Trabalho" />
					</ufrn:link>
				</td>
			</tr>
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td> ${plano.projetoPesquisa.codigo }</td>
				<td>
					<c:choose>
						<c:when test="${!empty plano.membroProjetoDiscente.discente and not plano.membroProjetoDiscente.inativo}">
							${plano.membroProjetoDiscente.discente}
						</c:when>
						<c:otherwise>
							<i> Discente não definido</i>
						</c:otherwise>
					</c:choose>
				</td>
				<td> ${plano.tipoBolsaString}</td>
				<td align="left"> ${plano.statusString}</td>
			</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="5" style="text-align: center; font-style: italic; font-weight: bold; ">
					${ fn:length(lista) } Plano(s) de Trabalho encontrado(s)
				</td>
			</tr>
		</tfoot>
	</table>
	</c:if>
</c:if>


<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>
