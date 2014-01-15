<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.pesquisa.struts.TipoFiltroProjetoPesquisa"%>

<%@page import="br.ufrn.sigaa.pesquisa.struts.FinalidadeBuscaProjeto"%>
<%@page import="br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa"%>

<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%><h2>


	<ufrn:subSistema /> >
		
	<fmt:message key="titulo.listar">
		<fmt:param value="Projeto de Pesquisa"/>
	</fmt:message>
</h2>

<style>
	span.info {
		font-size: 0.8em;
		color: #444;
	}
</style>

<jsp:useBean id="projetoPesquisaForm" type="br.ufrn.sigaa.pesquisa.form.ProjetoPesquisaForm" scope="session"/>

<c:if test="${empty relatorio}">

<html:form action="/pesquisa/projetoPesquisa/buscarProjetos" method="post">
<html:hidden property="buscar" value="true"/>
<html:hidden property="finalidadeBusca"/>

		<table class="formulario" align="center" style="width: 99%">
			<caption class="listagem">Critérios de Busca dos Projetos</caption>

			<tr>
				<td width="3%">
					<html:checkbox property="filtros" styleId="tipoProjeto" value="<%= "" + TipoFiltroProjetoPesquisa.TIPO_PROJETO%>" styleClass="noborder"/>
				</td>
				<td width="22%"> <label for="tipoProjeto"> Tipo: </label> </td>
				<td>
					<html:radio property="obj.interno" styleId="tipoInterno" value="true" onclick="$(tipoProjeto).checked = true;"/> <label for="tipoInterno">Interno</label>
					<html:radio property="obj.interno" styleId="tipoExterno" value="false" onclick="$(tipoProjeto).checked = true;" /> <label for="tipoExterno">Externo</label>
				</td>
			</tr>

			<tr>
				<td> <html:checkbox property="filtros" styleId="codigoProjeto" value="<%="" + TipoFiltroProjetoPesquisa.CODIGO%>" styleClass="noborder" /></td>
				<td> <label for="codigoProjeto">Código:</label> </td>
				<td>
					<html:text property="codigo" size="12" maxlength="13" onclick="$(codigoProjeto).checked = true;"/>
					<span class="info"> (Formato: PPPNNNN-AAAA, onde PPP = prefixo, NNNN = número e AAAA = ano) </span>
				</td>
			</tr>

			<tr>
				<td> <html:checkbox property="filtros" styleId="anoProjeto" value="<%="" + TipoFiltroProjetoPesquisa.ANO%>" styleClass="noborder" /></td>
				<td> <label for="anoProjeto">Ano:</label> </td>
				<td>
					<html:text property="obj.codigo.ano" size="6" maxlength="5" onclick="$(anoProjeto).checked = true;"/>
				</td>
			</tr>

			<tr>
				<td> <html:checkbox property="filtros" styleId="pesquisador" value="<%="" + TipoFiltroProjetoPesquisa.PESQUISADOR%>" styleClass="noborder"/> </td>
				<td> <label for="pesquisador">Pesquisador:</label> </td>
				<td>
					<c:set var="idAjax" value="membroProjeto.servidor.id"/>
					<c:set var="nomeAjax" value="membroProjeto.servidor.pessoa.nome"/>
					<c:set var="todosDocentes" value="true"/>
					<c:set var="buscaInativos" value="true"/>
					<%@include file="/WEB-INF/jsp/include/ajax/docente.jsp" %>
					<script type="text/javascript">
						function docenteOnChange() {
							getEl('pesquisador').dom.checked = true;
						}
					</script>
				</td>
			</tr>


			<tr>
				<td> <html:checkbox property="filtros" styleId="centro" value="<%="" + TipoFiltroProjetoPesquisa.CENTRO%>" styleClass="noborder"/> </td>
				<td> <label for="centro">Centro/Unidade:</label> </td>
				<td>
					<html:select property="centro.id" style="width:90%" onclick="$(centro).checked = true;">
						<html:option value="-1">  -- SELECIONE --  </html:option>
						<html:options collection="centros" property="unidade.id" labelProperty="unidade.codigoNome" />
					</html:select>
				</td>
			</tr>

			<tr>
				<td> <html:checkbox property="filtros" styleId="unidade" value="<%="" + TipoFiltroProjetoPesquisa.UNIDADE%>" styleClass="noborder"/> </td>
				<td> <label for="unidade">Unidade:</label> </td>
				<td>
					<c:set var="idAjax" value="unidade.id"/>
			    	<c:set var="nomeAjax" value="unidade.nome"/>
					<%@include file="/WEB-INF/jsp/include/ajax/unidade.jsp" %>
					<script type="text/javascript">
						function unidadeOnClick() {
							getEl('unidade').dom.checked = true;
						}
					</script>
				</td>
			</tr>
			<tr>
				<td> <html:checkbox property="filtros" styleId="titulo"  value="<%="" + TipoFiltroProjetoPesquisa.TITULO%>" styleClass="noborder" /> </td>
				<td> <label for="titulo"> Título: </label> </td>
				<td>
					<html:text property="titulo" style="width:90%" onclick="$('titulo').checked = true;"/>
				</td>
			</tr>
			<tr>
				<td> <html:checkbox property="filtros" styleId="objetivos"  value="<%="" + TipoFiltroProjetoPesquisa.OBJETIVOS%>" styleClass="noborder" /> </td>
				<td> <label for="objetivos"> Objetivos: </label> </td>
				<td>
					<html:text property="objetivos" style="width:90%" onclick="$('objetivos').checked = true;"/>
				</td>
			</tr>
			<tr>
				<td> <html:checkbox property="filtros" styleId="linhaPesquisa"  value="<%="" + TipoFiltroProjetoPesquisa.LINHA_PESQUISA%>" styleClass="noborder" /> </td>
				<td> <label for="linhaPesquisa"> Linha de Pesquisa: </label> </td>
				<td>
					<html:text property="obj.linhaPesquisa.nome" style="width:90%" onclick="$('linhaPesquisa').checked = true;"/>
				</td>
			</tr>
			<tr>
				<td> <html:checkbox property="filtros" styleId="areaConhecimento"  value="<%="" + TipoFiltroProjetoPesquisa.SUBAREA%>" styleClass="noborder" /> </td>
				<td> <label for="areaConhecimento"> Área de Conhecimento: </label> </td>
				<td>
					<html:select property="subarea.id" style="width:90%" onclick="$(areaConhecimento).checked = true;">
				        <html:option value="-1"> -- SELECIONE UMA ÁREA DE CONHECIMENTO -- </html:option>
				        <html:options collection="areasConhecimento" property="id" labelProperty="nome" />
			        </html:select>
				</td>
			</tr>

			<tr>
				<td> <html:checkbox property="filtros" styleId="grupoPesquisa" value="<%="" + TipoFiltroProjetoPesquisa.GRUPO_PESQUISA%>" styleClass="noborder"/> </td>
				<td> <label for="grupoPesquisa">Grupo de Pesquisa:</label> </td>
				<td>
					<html:select property="obj.linhaPesquisa.grupoPesquisa.id" style="width:90%" onclick="$(grupoPesquisa).checked = true;">
						<html:option value="-1">  -- SELECIONE UM GRUPO DE PESQUISA --  </html:option>
						<html:options collection="gruposPesquisa" property="id" labelProperty="nome" />
					</html:select>
				</td>
			</tr>

			<tr>
				<td> <html:checkbox property="filtros" styleId="agenciaFinanciadora" value="<%="" + TipoFiltroProjetoPesquisa.AGENCIA_FINANCIADORA%>" styleClass="noborder"/> </td>
				<td> <label for="agenciaFinanciadora">Agência Financiadora:</label> </td>
				<td>
					<html:select property="financiamentoProjetoPesq.entidadeFinanciadora.id" style="width:90%" onclick="$(agenciaFinanciadora).checked = true;">
						<html:option value="-1">  -- SELECIONE UMA AGÊNCIA FINANCIADORA --  </html:option>
						<html:options collection="entidadesFinanciadoras" property="id" labelProperty="nome" />
					</html:select>
				</td>
			</tr>

			<tr>
				<td> <html:checkbox property="filtros" styleId="editalPesquisa" value="<%="" + TipoFiltroProjetoPesquisa.EDITAL_PESQUISA%>" styleClass="noborder" /> </td>
				<td> <label for="editalPesquisa"> Edital: </label></td>
				<td>
					<html:select property="obj.edital.id" style="width:90%" onclick="$(editalPesquisa).checked = true;">
						<html:option value="-1">  -- SELECIONE UM EDITAL --  </html:option>
						<html:options collection="editais" property="id" labelProperty="descricao" />
					</html:select>
				</td>
			</tr>

			<c:if test="<%= projetoPesquisaForm.getFinalidadeBusca() != FinalidadeBuscaProjeto.CONCESSAO_COTAS %>">
			<tr>
				<td> <html:checkbox property="filtros" styleId="situacaoProjeto" value="<%="" + TipoFiltroProjetoPesquisa.STATUS_PROJETO%>" styleClass="noborder" /> </td>
				<td> <label for="situacaoProjeto"> Situação do Projeto: </label></td>
				<td>
					<html:select property="obj.situacaoProjeto.id" style="width:50%" onclick="$(situacaoProjeto).checked = true;">
						<html:option value="-1">  -- SELECIONE UMA SITUAÇÃO --  </html:option>
						<html:options collection="situacoesProjeto" property="id" labelProperty="descricao" />
					</html:select>
				</td>
			</tr>
			<tr>
				<td> <html:checkbox property="filtros" styleId="categoriaProjeto" value="<%="" + TipoFiltroProjetoPesquisa.CATEGORIA_PROJETO%>" styleClass="noborder" /> </td>
				<td> <label for="categoriaProjeto"> Categoria do Projeto: </label></td>
				<td>
					<html:select property="obj.categoria.id" style="width:50%" onclick="$(categoriaProjeto).checked = true;">
						<html:option value="-1">  -- SELECIONE UMA CATEGORIA --  </html:option>
						<html:options collection="categorias" property="id" labelProperty="denominacao" />
					</html:select>
				</td>
			</tr>
			<tr>
				<td width="3%"> <html:checkbox property="filtros" styleId="situacaoRelatorio" value="<%="" + TipoFiltroProjetoPesquisa.STATUS_RELATORIO%>" styleClass="noborder" /> </td>
				<td width="22%"> <label for="situacaoRelatorio"> Relatório Final </label></td>
				<td>
					<html:radio property="relatorioSubmetido" styleId="situacaoSubmetido" value="true" onclick="$(situacaoRelatorio).checked = true;"/> <label for="situacaoSubmetido">Submetido</label>
					<html:radio property="relatorioSubmetido" styleId="situacaoNaoSubmetido" value="false" onclick="$(situacaoRelatorio).checked = true;" /> <label for="situacaoNaoSubmetido">Não Submetido</label>
				</td>
			</tr>
			<tr>
				<td> <html:checkbox property="filtros" styleId="formatoRelatorio" value="<%="" + TipoFiltroProjetoPesquisa.FORMATO_RELATORIO%>" styleClass="noborder"/> </td>
				<td colspan="2"> <label for="formatoRelatorio"> <b>Gerar relatório</b> </label> </td>
			</tr>
			</c:if>

			<tfoot>
				<tr>
					<td colspan="3">
						<html:button dispatch="list" value="Buscar"/>
						<html:button dispatch="cancelar" value="Cancelar" cancelar="true"/>
					</td>
				</tr>
			</tfoot>
		</table>
</html:form>

<c:forEach items="${projetoPesquisaForm.filtros}" var="filtro">
 	<jsp:useBean id="filtro" type="java.lang.Integer"/>
 	<c:if test="<%= filtro.intValue() == TipoFiltroProjetoPesquisa.TIPO_PROJETO%>">
 		<script> $('tipoProjeto').checked = true;</script>
 	</c:if>
 	 <c:if test="<%= filtro.intValue() == TipoFiltroProjetoPesquisa.CODIGO%>">
 		<script> $('codigoProjeto').checked = true;</script>
 	</c:if>
 	 <c:if test="<%= filtro.intValue() == TipoFiltroProjetoPesquisa.ANO%>">
 		<script> $('anoProjeto').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == TipoFiltroProjetoPesquisa.PESQUISADOR%>">
 		<script> $('pesquisador').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == TipoFiltroProjetoPesquisa.CENTRO%>">
 		<script> $('centro').checked = true;</script>
 	</c:if>
 	 <c:if test="<%= filtro.intValue() == TipoFiltroProjetoPesquisa.UNIDADE%>">
 		<script> $('unidade').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == TipoFiltroProjetoPesquisa.TITULO%>">
 		<script> $('titulo').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == TipoFiltroProjetoPesquisa.OBJETIVOS%>">
 		<script> $('objetivos').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == TipoFiltroProjetoPesquisa.LINHA_PESQUISA%>">
 		<script> $('linhaPesquisa').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == TipoFiltroProjetoPesquisa.SUBAREA%>">
 		<script> $('areaConhecimento').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == TipoFiltroProjetoPesquisa.GRUPO_PESQUISA%>">
 		<script> $('grupoPesquisa').checked = true;</script>
 	</c:if>
 	 <c:if test="<%= filtro.intValue() == TipoFiltroProjetoPesquisa.AGENCIA_FINANCIADORA%>">
 		<script> $('agenciaFinanciadora').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == TipoFiltroProjetoPesquisa.EDITAL_PESQUISA%>">
 		<script> $('editalPesquisa').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == TipoFiltroProjetoPesquisa.STATUS_PROJETO%>">
 		<script> $('situacaoProjeto').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == TipoFiltroProjetoPesquisa.CATEGORIA_PROJETO%>">
 		<script> $('categoriaProjeto').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == TipoFiltroProjetoPesquisa.STATUS_RELATORIO%>">
 		<script> $('situacaoRelatorio').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == TipoFiltroProjetoPesquisa.FORMATO_RELATORIO%>">
 		<script> $('formatoRelatorio').checked = true;</script>
 	</c:if>
</c:forEach>

<br /><br />
</c:if>

<c:if test="${not empty lista && empty relatorio}">
<center>
	<div class="infoAltRem">
		<c:choose>
			<c:when test="<%= projetoPesquisaForm.getFinalidadeBusca() == FinalidadeBuscaProjeto.CONSULTA %>">
				    <html:img page="/img/view.gif" style="overflow: visible;"/>
				    : Visualizar Projeto
			</c:when>
			<c:when test="<%= projetoPesquisaForm.getFinalidadeBusca() == FinalidadeBuscaProjeto.GERENCIAR %>">
			    <c:if test="${acesso.pesquisa}">
				    <html:img page="/img/view.gif" style="overflow: visible;"/>
				    : Visualizar Projeto
				    <html:img page="/img/alterar.gif" style="overflow: visible;"/>
				    : Alterar Projeto
				    <html:img page="/img/extensao/businessman_refresh.png" style="overflow: visible;"/>
				    : Gerenciar Membros do Projeto
				    <html:img page="/img/delete.gif" style="overflow: visible;"/>
				    : Remover Projeto<br/>
				    <html:img page="/img/pesquisa/report_go.png" style="overflow: visible;"/>
				    : Enviar Relatório Final
				    <html:img page="/img/listar.gif" style="overflow: visible;"/>
				    : Listar Avaliações
				    <html:img page="/img/pesquisa/view.gif" style="overflow: visible;"/>
				    : Emitir Declaração
				    <html:img page="/img/pesquisa/avaliar.gif" style="overflow: visible;"/>
				    : Renovar Projeto
			    </c:if>
		    </c:when>
		    <c:when test="<%= projetoPesquisaForm.getFinalidadeBusca() == FinalidadeBuscaProjeto.CONCESSAO_COTAS %>">
				<c:if test="${acesso.pesquisa}">
					<html:img page="/img/pesquisa/avaliar.gif" style="overflow: visible;"/>
				    : Conceder cotas aos planos de trabalho deste projeto
				</c:if>
			</c:when>
			<c:when test="<%= projetoPesquisaForm.getFinalidadeBusca() == FinalidadeBuscaProjeto.CADASTRO_PLANO_TRABALHO %>">
				<c:if test="${acesso.pesquisa}">
					<html:img page="/img/pesquisa/avaliar.gif" style="overflow: visible;"/>
				    : Cadastrar Plano de Trabalho
				</c:if>
			</c:when>
			<c:when test="<%= projetoPesquisaForm.getFinalidadeBusca() == FinalidadeBuscaProjeto.ALTERAR_SITUACAO %>">
				<c:if test="${acesso.pesquisa}">
					<html:img page="/img/seta.gif" style="overflow: visible;"/>
				    : Alterar Situação do Projeto
				</c:if>
			</c:when>
		</c:choose>
	</div>
</center>
</c:if>

<style>
	table.listagem th {
		padding: 1px 4px;
	}

	table.listagem tr.ano td {
		font-weight: bold;
		background-color: #C4D2EB;
		padding: 3px 15px;
		border-bottom: 1px solid #CCC;
		font-size: 1.1em;
	}
</style>

<c:if test="${not empty lista}">
<table class="listagem">
<caption> Projetos de Pesquisa encontrados ( ${ fn:length(lista) } ) </caption>
<thead>
	<tr>
		<th> Código </th>
		<th> Centro </th>
		<th width="40%"> Título/Coordenador </th>
		<th> Tipo </th>
		<th> Situação </th>
		<th> Data de Cadastro </th>
		<th> </th>
	</tr>
</thead>
<tbody>
	<c:set var="anoProjeto" value="0" />
	<c:forEach var="projeto" items="${lista}" varStatus="status">

	<c:if test="${projeto.codigo.ano != anoProjeto}">
		<c:set var="anoProjeto" value="${projeto.codigo.ano}" />
		<tr class="ano">
			<td colspan="8"> ${projeto.codigo.ano} </td>
		</tr>
	</c:if>

	<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
		<td> ${projeto.codigo} </td>
		<td> ${projeto.centro.sigla} </td>
		<td>
			${projeto.titulo} <br/> 
			<i>${ not empty projeto.coordenador ? projeto.coordenador.pessoa.nome : "Não Informado" }</i> 
		</td>
		<td> ${ projeto.interno ? "INT" : "EXT" } </td>
		<td> ${projeto.situacaoProjeto.descricao} </td>
		<td> <ufrn:format type="data" name="projeto" property="dataCadastro"/> </td>
		<td nowrap="nowrap">
			<c:choose>
				<c:when test="<%= projetoPesquisaForm.getFinalidadeBusca() == FinalidadeBuscaProjeto.CONSULTA %>">
					<html:link action="/pesquisa/projetoPesquisa/buscarProjetos?dispatch=view&id=${projeto.id}">
						<img src="${ctx}/img/view.gif"
							title="Visualizar Projeto"
							alt="Visualizar Projeto"/>
					</html:link>
				</c:when>
				<c:when test="<%= projetoPesquisaForm.getFinalidadeBusca() == FinalidadeBuscaProjeto.GERENCIAR %>">
					<c:if test="${acesso.pesquisa && !projetoPesquisaForm.consulta}">
						<html:link action="/pesquisa/projetoPesquisa/buscarProjetos?dispatch=view&id=${projeto.id}">
							<img src="${ctx}/img/view.gif"
								title="Visualizar Projeto"
								alt="Visualizar Projeto"/>
						</html:link>
						<html:link action="/pesquisa/projetoPesquisa/criarProjetoPesquisa?dispatch=edit&id=${projeto.id}">
							<img src="${ctx}/img/alterar.gif"
								title="Alterar Projeto"
								alt="Alterar Projeto"/>
						</html:link>
						<html:link action="/pesquisa/projetoPesquisa/criarProjetoPesquisa?dispatch=gerenciarMembros&id=${projeto.id}">
							<img src="${ctx}/img/extensao/businessman_refresh.png"
								title="Gerenciar Membros do Projeto"
								alt="Gerenciar Membros do Projeto"/>
						</html:link>
						<html:link action="/pesquisa/projetoPesquisa/criarProjetoPesquisa?dispatch=remove&id=${projeto.id}">
							<img src="${ctx}/img/delete.gif"
								title="Remover Projeto"
								alt="Remover Projeto"/>
						</html:link>
						<html:link action="/pesquisa/relatorioProjeto?dispatch=popularEnvioPropesq&idProjeto=${projeto.id}&gestor=true">
							<img src="${ctx}/img/pesquisa/report_go.png"
								alt="Enviar Relatório Final"
								title="Enviar Relatório Final" />
						</html:link>
						<html:link action="/pesquisa/avaliarProjetoPesquisa?dispatch=resumoAvaliacoes&obj.projetoPesquisa.id=${projeto.id}">
							<img src="${ctx}/img/listar.gif"
								alt="Listar Avaliações"
								title="Listar Avaliações" />
						</html:link>
						<c:set var="andamento" value="<%= TipoSituacaoProjeto.EM_ANDAMENTO %>"/>
						<c:set var="fim" value="<%= TipoSituacaoProjeto.FINALIZADO %>" />
						<c:set var="renovado" value="<%= TipoSituacaoProjeto.RENOVADO %>" />
						<c:if test="${projeto.situacaoProjeto.id == andamento || projeto.situacaoProjeto.id == fim 
										|| projeto.situacaoProjeto.id == renovado}">
							<html:link action="/pesquisa/projetoPesquisa/criarProjetoPesquisa?dispatch=listarMembroByProjeto&obj.id=${projeto.id}">
								<img src="${ctx}/img/pesquisa/view.gif"
									alt="Emitir Declaração" title="Emitir Declaração" />
							</html:link>
						</c:if>
						<html:link action="/pesquisa/projetoPesquisa/criarProjetoPesquisa?dispatch=renovar&obj.id=${projeto.id}">
							<img src="${ctx}/img/pesquisa/avaliar.gif"
								title="Renovar Projeto"
								alt="Renovar Projeto"/>
						</html:link>
					</c:if>
				</c:when>
				
				<c:when test="<%= projetoPesquisaForm.getFinalidadeBusca() == FinalidadeBuscaProjeto.CADASTRO_PLANO_TRABALHO %>">
					<c:if test="${acesso.pesquisa}">
						<html:link action="/pesquisa/planoTrabalho/wizard?dispatch=selecionarProjeto&idProjeto=${projeto.id}">
							<img src="${ctx}/img/pesquisa/avaliar.gif"
								title="Cadastrar Plano de Trabalho"
								alt="Cadastrar Plano de Trabalho"/>
						</html:link>
					</c:if>
				</c:when>

				<c:when test="<%= projetoPesquisaForm.getFinalidadeBusca() == FinalidadeBuscaProjeto.ALTERAR_SITUACAO %>">
					<c:if test="${acesso.pesquisa}">
						<html:link action="/pesquisa/alterarSituacaoProjetoPesquisa?dispatch=popular&idProjeto=${projeto.id}">
							<img src="${ctx}/img/seta.gif"
								title="Alterar Situação do Projeto"
								alt="Alterar Situação do Projeto"/>
						</html:link>
					</c:if>
				</c:when>
			</c:choose>
		</td>
	</tr>
	</c:forEach>
</tbody>
<tfoot>
	<tr>
		<td colspan="8" style="text-align: center; font-weight: bold;">
			${ fn:length(lista) } Projeto(s) de Pesquisa encontrado(s)
		</td>
	</tr>
</tfoot>
</table>
</c:if>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
