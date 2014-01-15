<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.pesquisa.form.MembroProjetoDiscenteForm"%>

<%@page import="br.ufrn.sigaa.pesquisa.struts.TipoFiltroBolsista"%>
<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	Relatório de Bolsistas
</h2>

<html:form action="/pesquisa/buscarMembroProjetoDiscente" method="post">
	<table class="formulario" align="center" style="width: 99%">
		<caption class="listagem">Critérios de Busca dos Bolsistas</caption>

		<tr>
			<td> <html:checkbox property="filtros" styleId="grupoPesquisa" value="<%="" + TipoFiltroBolsista.GRUPO_PESQUISA%>" styleClass="noborder"/> </td>
			<td width="23%"> <label for="grupoPesquisa">Grupo de Pesquisa:</label> </td>
			<td>
				<html:select property="obj.planoTrabalho.projetoPesquisa.linhaPesquisa.grupoPesquisa.id" style="width:90%" onchange="$(grupoPesquisa).checked = true;">
					<html:option value="-1">  -- SELECIONE UM GRUPO DE PESQUISA --  </html:option>
					<html:options collection="gruposPesquisa" property="id" labelProperty="nomeCompacto" />
				</html:select>
			</td>
		</tr>
		<tr>
			<td> <html:checkbox property="filtros" styleId="centro" value="<%="" + TipoFiltroBolsista.CENTRO%>" styleClass="noborder"/> </td>
			<td> <label for="centro">Centro:</label> </td>
			<td>
				<html:select property="centro.id" style="width:90%" onchange="$(centro).checked = true;">
					<html:option value="-1">  -- SELECIONE UM CENTRO ACADÊMICO --  </html:option>
					<html:options collection="centros" property="id" labelProperty="codigoNome" />
				</html:select>
			</td>
		</tr>
		<tr>
			<td> <html:checkbox property="filtros" styleId="unidade" value="<%="" + TipoFiltroBolsista.DEPARTAMENTO%>" styleClass="noborder"/> </td>
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
			<td> <html:checkbox property="filtros" styleId="aluno" value="<%="" + TipoFiltroBolsista.NOME%>" styleClass="noborder"/> </td>
			<td> <label for="aluno">Aluno:</label> </td>
			<td>
                <c:set var="idAjax" value="obj.discente.id"/>
                <c:set var="nomeAjax" value="obj.discente.pessoa.nome"/>
                <c:set var="nivel" value="G"/>
                <c:set var="statusDiscente" value="todos"/>
                <%@include file="/WEB-INF/jsp/include/ajax/discente.jsp" %>
                <script type="text/javascript">
					function discenteOnChange() {
						getEl('aluno').dom.checked = true;
					}
				</script>
			</td>
		</tr>

		<tr>
			<td> <html:checkbox property="filtros" styleId="orientador" value="<%="" + TipoFiltroBolsista.ORIENTADOR%>" styleClass="noborder"/> </td>
			<td> <label for="orientador">Orientador:</label> </td>
			<td>
				<c:set var="idAjax" value="membroProjetoServidor.servidor.id"/>
				<c:set var="nomeAjax" value="membroProjetoServidor.servidor.pessoa.nome"/>
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
			<td> <html:checkbox property="filtros" styleId="cota" value="<%="" + TipoFiltroBolsista.COTA%>" styleClass="noborder"/> </td>
			<td> <label for="cota">Cota:</label> </td>
			<td>
				<html:select property="idCota" style="width:50%" onchange="$('cota').checked = true;">
					<html:option value="-1">  -- SELECIONE UMA COTA --  </html:option>
					<html:options collection="cotas" property="id" labelProperty="descricao" />
				</html:select>
			</td>
		</tr>

		<tr>
			<td> <html:checkbox property="filtros" styleId="modalidade" value="<%="" + TipoFiltroBolsista.MODALIDADE_BOLSA%>" styleClass="noborder"/> </td>
			<td> <label for="modalidade">Modalidade da Bolsa:</label> </td>
			<td>
				<html:select property="obj.tipoBolsa.id" style="width:50%" onchange="$('modalidade').checked = true;">
					<html:option value="-1">  -- SELECIONE UMA MODALIDADE --  </html:option>
					<html:options collection="modalidades" property="key" labelProperty="value" />
				</html:select>
			</td>
		</tr>

		<tr>
			<td> <html:checkbox property="filtros" styleId="status" value="<%="" + TipoFiltroBolsista.STATUS_RELATORIO%>" styleClass="noborder"/> </td>
			<td> <label for="status">Status do Plano de Trabalho:</label> </td>
			<td>
				<html:select property="obj.planoTrabalho.status" style="width:50%" onchange="$('status').checked = true;">
					<html:option value="-1">  -- SELECIONE UM STATUS --  </html:option>
					<html:options collection="tiposStatus" property="key" labelProperty="value" />
				</html:select>
			</td>
		</tr>

		<tr>
			<td> <html:checkbox property="filtros" styleId="curso" value="<%="" + TipoFiltroBolsista.CURSO%>" styleClass="noborder"/> </td>
			<td> <label for="curso">Curso:</label> </td>
			<td>
				<html:select property="obj.discente.curso.id" style="width:90%" onchange="$('curso').checked = true;">
					<html:option value="-1">  -- SELECIONE UM CURSO --  </html:option>
					<html:options collection="cursos" property="id" labelProperty="nomeCompleto" />
				</html:select>
			</td>
		</tr>

		<tr>
			<td> <html:checkbox property="filtros" styleId="sexo" value="<%="" + TipoFiltroBolsista.SEXO%>" styleClass="noborder"/> </td>
			<td> <label for="sexo">Sexo:</label> </td>
			<td>
				<html:radio property="obj.discente.pessoa.sexo" value="M" onclick="$('sexo').checked = true;"/> Masculino
				<html:radio property="obj.discente.pessoa.sexo" value="F" onclick="$('sexo').checked = true;"/> Feminino
			</td>
		</tr>
		<tr>
			<td> <html:checkbox property="filtros" styleId="somenteAtivos" value="<%="" + TipoFiltroBolsista.SOMENTE_ATIVOS%>" styleClass="noborder"/> </td>
			<td colspan="3"> <label for="somenteAtivos">Listar somente bolsistas ativos</label> </td>
		</tr>
		<tr>
			<td> <html:checkbox property="filtros" styleId="formatoRelatorio" value="<%="" + TipoFiltroBolsista.FORMATO_RELATORIO%>" styleClass="noborder"/> </td>
			<td colspan="3"> <label for="formatoRelatorio"><b>Gerar Relatório</b></label> </td>
		</tr>

		<tfoot>
			<tr>
				<td colspan="3">
					<html:button dispatch="relatorio" value="Buscar"/>
					<html:button dispatch="cancelar" value="Cancelar"/>
				</td>
			</tr>
		</tfoot>
	</table>
</html:form>

<c:forEach items="${membroProjetoDiscenteForm.filtros}" var="filtro">
 	<jsp:useBean id="filtro" type="java.lang.Integer"/>
 	<c:if test="<%= filtro.intValue() == TipoFiltroBolsista.GRUPO_PESQUISA%>">
 		<script> $('grupoPesquisa').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == TipoFiltroBolsista.CENTRO%>">
 		<script> $('centro').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == TipoFiltroBolsista.DEPARTAMENTO%>">
 		<script> $('unidade').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == TipoFiltroBolsista.NOME%>">
 		<script> $('aluno').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == TipoFiltroBolsista.ORIENTADOR%>">
 		<script> $('orientador').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == TipoFiltroBolsista.MODALIDADE_BOLSA%>">
 		<script> $('modalidade').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == TipoFiltroBolsista.CURSO%>">
 		<script> $('curso').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == TipoFiltroBolsista.SEXO%>">
 		<script> $('sexo').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == TipoFiltroBolsista.STATUS_RELATORIO%>">
 		<script> $('status').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == TipoFiltroBolsista.COTA%>">
 		<script> $('cota').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == TipoFiltroBolsista.SOMENTE_ATIVOS%>">
 		<script> $('somenteAtivos').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == TipoFiltroBolsista.FORMATO_RELATORIO%>">
 		<script> $('formatoRelatorio').checked = true;</script>
 	</c:if>
</c:forEach>

<c:if test="${empty popular}">
<br/><br/>
	<c:if test="${empty lista}">
		<div align="center">
			Nenhum discente encontrado!
		</div>
	</c:if>
	<c:if test="${!empty lista}">

	<style>
		.listagem tr.topo td {
			padding-top: 10px;
		}

		table.listagem tr.cota td {
			background-color: #C4D2EB;
			padding: 8px 10px 2px;
			border-bottom: 1px solid #BBB;
			font-variant: small-caps;

			font-style: italic;
		}
	</style>

	<div class="infoAltRem">
		<html:img page="/img/pesquisa/info_discente.gif" style="overflow: visible;"/>
		 : Consultar dados do discente
		<html:img page="/img/pesquisa/avaliar.gif" style="overflow: visible;"/>
		 : Consultar Plano de Trabalho
		<html:img page="/img/pesquisa/view.gif" style="overflow: visible;"/>
		 : Resumo da Substituição
		<br>
		<html:img page="/img/pesquisa/indicar_bolsista.gif" style="overflow: visible;"/>
		 : Substituir Bolsista
		<html:img page="/img/pesquisa/remover_bolsista.gif" style="overflow: visible;"/>
		 : Finalizar Bolsista
		<html:img page="/img/delete.gif" style="overflow: visible;"/>
		 : Remover Bolsista
	</div>

	<table class="listagem">
		<caption> Discentes encontrados </caption>
		<thead>
			<tr>
				<th colspan="2" width="60%"> Discente </th>
				<th width="25%"> Tipo de Bolsa</th>
				<th> </th>
			</tr>
		</thead>
		<tbody>
			<c:set var="cota" />
			<c:forEach var="membro" items="${lista}" varStatus="status">
			<c:set var="stripes">${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }</c:set>

			<c:if test="${ cota != membro.planoTrabalho.cota.descricao }">
				<c:set var="cota" value="${ membro.planoTrabalho.cota.descricao }" />
				<tr class="cota">
					<td colspan="4"> Cota ${ cota } </td>
				</tr>
			</c:if>

			<tr class="${stripes} topo">
				<td colspan="2"> ${membro.discente} </td>
				<td align="center"> ${membro.tipoBolsaString} </td>
				<td nowrap="nowrap" rowspan="2" align="center">
					<ufrn:link action="/pesquisa/buscarMembroProjetoDiscente" param="idMembroDiscente=${membro.id}&dispatch=view">
						<img src="${ctx}/img/pesquisa/info_discente.gif"
							alt="Consultar Dados do Discente"
							title="Consultar dados do discente" />
					</ufrn:link>
					<ufrn:link action="/pesquisa/planoTrabalho/wizard" param="obj.id=${membro.planoTrabalho.id}&dispatch=view">
						<img src="${ctx}/img/pesquisa/avaliar.gif"
							alt="Consultar Plano de Trabalho"
							title="Consultar Plano de Trabalho" />
					</ufrn:link>
					<c:if test="${ membro.ativo }">
						<html:link action="/pesquisa/indicarBolsista?dispatch=indicar&obj.id=${membro.planoTrabalho.id}">
							<img src="${ctx}/img/pesquisa/indicar_bolsista.gif" border="0" alt="Substituir Bolsista" title="Substituir Bolsista"/>
						</html:link>
						<html:link action="/pesquisa/indicarBolsista?dispatch=remover&obj.id=${membro.planoTrabalho.id}">
							<img src="${ctx}/img/pesquisa/remover_bolsista.gif" alt="Finalizar Bolsista" title="Finalizar Bolsista"/>
						</html:link>
					</c:if>
					<html:link action="/pesquisa/indicarBolsista?dispatch=resumo&obj.id=${membro.planoTrabalho.id}">
						<img src="${ctx}/img/pesquisa/view.gif"
							alt="Resumo da Substituição"
							title="Resumo da Substituição"/>
					</html:link>
					<html:link action="/pesquisa/buscarMembroProjetoDiscente?idMembroDiscente=${membro.id}&dispatch=inativar" onclick="${confirmDelete}">
						<img src="${ctx}/img/delete.gif"
							alt="Remover Bolsista"
							title="Remover Bolsista" />
					</html:link>
				</td>
			</tr>
			<tr class="${stripes}">
				<td valign="top"> &nbsp;&nbsp;<i>Orientador:</i> ${membro.planoTrabalho.orientador.pessoa.nome }</td>
				<td valign="top" nowrap="nowrap"> <i>Projeto:</i> ${membro.planoTrabalho.projetoPesquisa.codigo }</td>
				<td align="center">
					<i>${membro.planoTrabalho.statusString}
					<c:if test="${ not membro.ativo }">
					 (até <ufrn:format type="data" name="membro" property="dataFim" />)
					 </c:if>
					 </i>
				</td>
			</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="4" align="center"> <b>${fn:length(lista)} bolsistas encontrados </b></td>
			</tr>
		</tfoot>
	</table>
	</c:if>
</c:if>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>