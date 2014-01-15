<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>

<h2><ufrn:subSistema /> &gt; Relat�rio de toda a produtividade </h2>

<ufrn:checkNotRole papel="<%= SigaaPapeis.ADMINISTRADOR_PRODOCENTE %>">
	<div class="descricaoOperacao">
	<p>Caro Docente, este relat�rio exibe toda sua produ��o intelectual cadastrada no SIGAA.
	Use este relat�rio para:</p>
		<ul>
			<li> Verificar a produ��es que foram validadas pelo chefe </li>
			<li> Verificar as orienta��es Mestrado/Doutorado lan�ados pelo seu programa de p�s </li>
			<li> Verificar as coordena��es de base de pesquisa </li>
		</ul>
	<p>
	Caso julgue �til mais informa��es a respeito de cada produ��o, favor abrir chamado que as
	incluiremos no relat�rio. Use o formul�rio abaixo para filtrar o estado da produ��o:</p>
	</div>
</ufrn:checkNotRole>

<table class="formulario">
<caption> Filtro na Valida��o </caption>
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
		<td align="right"> Valida��o: </td>
		<td>
		<h:selectOneMenu id="filtroValidacao"
			value="#{todaProducao.filtroValidacao}">
			<f:selectItem itemLabel="Produ��es Validadas" itemValue="1" />
			<f:selectItem itemLabel="Produ��es com Valida��o Negada"
				itemValue="0" />
			<f:selectItem itemLabel="Produ��es Pendentes de Valida��o"
				itemValue="-1" />
			<f:selectItem itemLabel="Todas as Produ��es" itemValue="2" />
		</h:selectOneMenu>
		</td>
	</tr>
	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton action="#{todaProducao.emiteRelatorio}" value="Emitir Relat�rio"/>
			<h:commandButton action="#{todaProducao.cancelar}" value="Cancelar" 
					onclick="#{confirm}" immediate="true"/>
		</td>
	</tr>
	</tfoot>
</h:form>
</table>

<br/>
<div class="obrigatorio"> Campos de preenchimento obrigat�rio.</div>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>