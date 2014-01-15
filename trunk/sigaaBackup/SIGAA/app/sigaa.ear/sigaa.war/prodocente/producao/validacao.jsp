<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/painel_valida_pd.js"></script>

<f:view>
	<f:subview id="menu">
		<%@include file="/portais/docente/menu_docente.jsp"%>
	</f:subview>
	
	
	<h:form id="formFiltragem">
		<h:messages showDetail="true" />
		<h2>Validação de Produção Intectual</h2>

		<div id="divMsg" style="text-align: center; font-weight: bold; background: #EEE; "></div>
		
		<br>
		<div id="tabs-validacoes">

		<div id="formulario" class="tab-content aba">

		<table width="90%" class="formulario" style="border: 0;">
			<tr>
				<th width="15%">Ano Base:</th>
				<td width="15%">
					<h:selectOneMenu value="#{producao.anoReferencia}">
						<f:selectItems value="#{producao.anos}" />
					</h:selectOneMenu>
				</td>
				<th width="25%">Status da Validação:</th>
				<td>
					<h:selectOneMenu value="#{producao.validados}" style="width: 50%">
						<f:selectItem itemValue="0" itemLabel=" --> TODOS <-- " />
						<f:selectItem itemValue="1" itemLabel="Validados" />
						<f:selectItem itemValue="2" itemLabel="Não Validados" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th>Título:</th>
				<td colspan="3"><h:inputText maxlength="255" value="#{producao.titulo}" style="width: 90%"/></td>
			</tr>
			<c:if test="${not producao.carregarMinhasProducoes}">
			<tr>
				<th>Docente:</th>
				<td colspan="3">
					<h:selectOneMenu value="#{producao.docente.id}" style="width: 90%">
						<f:selectItem itemValue="0" itemLabel="--> TODOS OS DOCENTES <--" />
						<f:selectItems value="#{producao.docentesUnidadeParaValidacao}" />
					</h:selectOneMenu>
				</td>
			</tr>
			</c:if>
			<tr>
				<td colspan="4" align="center">
					<h:commandButton value="Filtrar Produções" action="#{producao.filtrar}" />
				</td>
			</tr>
		</table>
		<c:if test="${producao.arvore.childCount == 0}">
			<center style="color:red"><br>
			<i>Não há resultados para essa busca.</i></center>
		</c:if>
	</h:form>
	</div>
	
	<div id="resultado" class="tab-content aba">
	
	<style>
		table.listagem tr.tipoProducao td {
			font-weight: bold;
			background: #F5F5F5;
			border-bottom: 1px solid #DDD;
			padding-left: 1.5em;
		}
		
		table.listagem tr td.icone {
			padding-left: 1.5em;
		}
		
	</style>
	
	<c:if test="${not empty producao.mapaProducoes}">
		<table class="listagem">
			<c:set var="tipo" />
			<c:forEach var="item" items="#{producao.mapaProducoes}">
				<%-- Servidor --%>
				<tr>
					<td class="subFormulario" colspan="2"> ${item.key.nome} </td>
				</tr>
				
				<c:forEach var="prod" items="#{item.value}" varStatus="loop"> 
					<%-- Tipo da Produção --%>
					<c:if test="${tipo != prod.tipoProducao.id}" >
						<c:set var="tipo" value="${prod.tipoProducao.id}" />
						<tr class="tipoProducao">
							<td colspan="2"> ${prod.tipoProducao}</td>
						</tr>
					</c:if>
					<%-- Produção --%>
					<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td class="icone">
							<c:choose>
								<c:when test="${empty prod.validado}">
									<img src="/sigaa/img/prodocente/help.png" id="img_${prod.id}"/>								
								</c:when>
								<c:when test="${prod.validado}">
									<img src="/sigaa/img/prodocente/accept.png" id="img_${prod.id}"/>								
								</c:when>
								<c:when test="${not prod.validado}">
									<img src="/sigaa/img/prodocente/cancel.png" id="img_${prod.id}"/>								
								</c:when>
							</c:choose>
						</td>
						<td class="producao">
							<a href="#" onclick="exibirPainel(${prod.id}, img_${prod.id})">
								${prod.titulo}
							</a>
						</td>						
					</tr>
				</c:forEach>
				
			</c:forEach>
		</table>
	</c:if>
	
		</div>
	</div>
</f:view>
<script type="text/javascript">
<!--

	var sohExibir = false;
<c:if test="${producao.carregarMinhasProducoes}">
	sohExibir = true;
</c:if>

	function exibirPainel(id, imagem) {
		var getURL = function(t) {
			var url = t.responseText;
			PainelValidacao.show(url,sohExibir,imagem);
		}
		// buscar url
		var ajax = new Ajax.Request('/sigaa/ajaxPreValidacaoProDoc',
		{
			method: 'get',
			parameters: 'id=' + id,
			onSuccess: getURL
		});
	}

//-->
</script>
	<script type="text/javascript">
	var tabs;
	var Tabs = {
	    init : function(){
	        tabs = new YAHOO.ext.TabPanel('tabs-validacoes');
	        tabs.addTab('formulario', "FILTRO");
	        tabs.addTab('resultado', "RESULTADOS");
	        tabs.activate('formulario');
			<c:if test="${not empty producao.mapaProducoes}">
				tabs.activate('resultado');
			</c:if>
	    }
	}
	YAHOO.ext.EventManager.onDocumentReady(Tabs.init, Tabs, true);
	</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
