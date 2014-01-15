<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>

<h2><ufrn:subSistema /> &gt; Relatório de toda a produtividade </h2>

<ufrn:checkNotRole papel="<%= SigaaPapeis.ADMINISTRADOR_PRODOCENTE %>">
	<div class="descricaoOperacao">
	<p>Caro Docente, este relatório exibe toda sua produção intelectual cadastrada no SIGAA.
	Use este relatório para:</p>
		<ul>
			<li> Verificar a produções que foram validadas pelo chefe </li>
			<li> Verificar as orientações Mestrado/Doutorado lançados pelo seu programa de pós </li>
			<li> Verificar as coordenações de base de pesquisa </li>
		</ul>
	<p>
	Caso julgue útil mais informações a respeito de cada produção, favor abrir chamado que as
	incluiremos no relatório. Use o formulário abaixo para filtrar o estado da produção:</p>
	</div>
</ufrn:checkNotRole>

<table class="formulario">
<caption> Filtro na Validação </caption>
<h:form id="form">

	<tr>
		<c:choose>
			<c:when test="${todaProducao.vinculoChefia}">
				<td class="required">Docente: </td>
					<td>
						<h:inputHidden id="id" value="#{todaProducao.servidor.id}"></h:inputHidden>
						<h:inputText id="nome" value="#{todaProducao.servidor.pessoa.nome}" size="80" />
	
						<ajax:autocomplete
								source="form:nome" target="form:id"
								baseUrl="/sigaa/ajaxServidor" className="autocomplete"
								indicator="indicator" minimumCharacters="3" parameters="tipo=todos"
								parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
								style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
					</td>
			</c:when>
			<c:otherwise>
				<td>Docente: </td>
				<td>
					<c:if test="${ acesso.docenteUFRN }">
						${ usuario.pessoa.nome }
					</c:if>
				</td>
			</c:otherwise>
		</c:choose>
	</tr>

	<tr>
		<td align="right"> Ano Inicial: </td>
		<td>
			<h:selectOneMenu id="anoInicial" value="#{todaProducao.anoInicial}">
				<f:selectItems value="#{todaProducao.anos}"/>
			</h:selectOneMenu>
		</td>
	</tr>
	<tr>
		<td align="right"> Ano Final: </td>
		<td>
			<h:selectOneMenu id="anoFinal" value="#{todaProducao.anoFinal}">
				<f:selectItems value="#{todaProducao.anos}"/>
			</h:selectOneMenu>
		</td>
	</tr>
	<tr>
		<td align="right"> Validação: </td>
		<td>
		<h:selectOneMenu id="filtroValidacao"
			value="#{todaProducao.filtroValidacao}">
			<f:selectItem itemLabel="Produções Validadas" itemValue="1" />
			<f:selectItem itemLabel="Produções com Validação Negada"
				itemValue="0" />
			<f:selectItem itemLabel="Produções Pendentes de Validação"
				itemValue="-1" />
			<f:selectItem itemLabel="Todas as Produções" itemValue="2" />
		</h:selectOneMenu>
		</td>
	</tr>
	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton action="#{todaProducao.emiteRelatorio}" value="Emitir Relatório"/>
			<h:commandButton action="#{todaProducao.cancelar}" value="Cancelar" 
					onclick="#{confirm}" immediate="true"/>
		</td>
	</tr>
	</tfoot>
</h:form>
</table>

<br/>
<div class="obrigatorio"> Campos de preenchimento obrigatório.</div>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>