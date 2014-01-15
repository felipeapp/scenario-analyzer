<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript">
	JAWR.loader.script('/javascript/jquery/jquery.js');
</script>
<link rel="stylesheet" type="text/css" href="/sigaa/css/ensino/detalhes_discente.css"/>
<script type="text/javascript" src="/sigaa/javascript/graduacao/busca_discente.js"> </script>

<style> 
	span.info { color: #666; font-size: 0.9em; padding-left: 10px; }
	table.listagem td.detalhesDiscente { display: none; padding: 0;}
</style>

<f:view>
	<h2> <ufrn:subSistema /> > Orientações de Pós-Graduação</h2>

<h:form>
<div class="infoAltRem" style="width: 80%;">
	<img src="${ctx}/img/comprovante.png">: Detalhes do Discente
	<img src="${ctx}/img/view2.gif">: Visualizar Histórico
	<img src="${ctx}/img/view.gif">: Visualizar Orientações Dadas
	<img src="${ctx}/img/submenu.png">: Solicitar Banca
</div>

<table class="listagem" style="width: 80%;">
	<caption> Lista de Orientandos </caption>
	<thead>
	<tr>
		<th> </th>
		<th> Discente </th>
		<th> </th>
		<th> </th>
		<th> </th>
	</tr>
	</thead>
	<tbody>
		<c:set var="nivelLoop" value=""/>
		<c:forEach var="orientacao" items="#{orientacaoAcademica.orientacoes}" varStatus="loop">
			<c:if test="${orientacao.tipoOrientacao == 'O'}">
				<c:set var="stripe" value="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}"/>
				<c:if test="${nivelLoop != orientacao.discente.nivel}">
					<c:set var="nivelLoop" value="${orientacao.discente.nivel}" />
					<tr>
						<td class="subFormulario" colspan="6"> ${orientacao.discente.nivelDesc} </td>
					</tr>
				</c:if>
				<tr class="${stripe}">
					<td width="5%">
						<a href="javascript: void(0);" onclick="habilitarDetalhes(${orientacao.discente.id});">
							<img src="${ctx}/img/comprovante.png"/>
						</a>
					</td>
					<td> 
						${orientacao.discente.matriculaNome}
						<c:if test="${!orientacao.discente.regular}"> 
							<span class="info"> (${orientacao.discente.tipoString}) </span>
						</c:if> 
					</td>
					<td style="width: 1px;"> 
						<h:commandLink title="Visualizar Histórico" action="#{historico.selecionaDiscenteForm}" id="linkParaVisualizarHistorico">
							<f:param value="#{orientacao.discente.id}" name="id"/>
							<h:graphicImage value="/img/view2.gif"/>
						</h:commandLink>
					</td>
					<td style="width: 1px;">
						<h:commandLink title="Visualizar Orientações Dadas" action="#{orientacaoAcademica.visualizarOrientacoesDadas}" id="linkParaVisualizarOrientacoes">
							<f:param value="#{orientacao.discente.id}" name="idDiscente" />
							<f:param value="#{orientacao.discente.nivel}" name="nivelDiscente" />
							<h:graphicImage value="/img/view.gif"/>
						</h:commandLink>					
					</td>
					<td style="width: 1px;">
						<h:graphicImage value="/img/submenu.png" title="Solicitar Banca" style="cursor: pointer;">
							<rich:componentControl event="onclick" for="menuBanca" operation="show">
						        <f:param value="#{orientacao.discente.id}" name="idDiscente" />
			    				<f:param value="#{orientacao.discente.matriculaNome}" name="matriculaNome"/>						        
						    </rich:componentControl>
						</h:graphicImage>
					</td>					
				</tr>
				<tr class="${stripe}"> 
					<td colspan="6" id="linha_${orientacao.discente.id}" class="detalhesDiscente" ><h:graphicImage value="/img/indicator.gif"/></td>				
				</tr>
			</c:if>
		</c:forEach>
	</tbody>
</table>

<rich:contextMenu attached="false" id="menuBanca" hideDelay="300">
    <rich:menuItem disabled="true">
        <b>{matriculaNome}</b>
    </rich:menuItem>
	<rich:menuItem value="Cadastrar Banca de Qualificacao" icon="/img/adicionar.gif" action="#{bancaPos.iniciarQualificacaoOrientador}">
		<f:param name="idDiscente" value="{idDiscente}"/>
	</rich:menuItem>							
	<rich:menuItem value="Cadastrar Banca de Defesa" icon="/img/adicionar.gif" action="#{bancaPos.iniciarDefesaOrientador}">
		<f:param name="idDiscente" value="{idDiscente}"/>
	</rich:menuItem>
	<rich:menuSeparator/>
	<rich:menuItem value="Listar Bancas" icon="/img/listar.gif" action="#{bancaPos.listarBancasOrientador}">
		<f:param name="idDiscente" value="{idDiscente}"/>
	</rich:menuItem>
</rich:contextMenu> 
<br>
<br>
<table class="listagem" style="width: 80%;">
	<caption> Lista de Co-Orientandos </caption>
	<thead>
	<tr>
		<th> </th>
		<th> Discente </th>
		<th> </th>
		<th> </th>
	</tr>
	</thead>
	<tbody>
		<c:set var="nivelLoop" value=""/>
		<c:forEach var="orientacao" items="#{orientacaoAcademica.orientacoes}" varStatus="loop">
			<c:if test="${orientacao.tipoOrientacao == 'C'}">
				<c:if test="${nivelLoop != orientacao.discente.nivel}">
				<c:set var="stripe" value="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}"/>
					<c:set var="nivelLoop" value="${orientacao.discente.nivel}" />
					<tr>
						<td class="subFormulario" colspan="4"> ${orientacao.discente.nivelDesc} </td>
					</tr>
				</c:if>
				<tr class="${stripe}">
					<td width="5%">
						<a href="javascript: void(0);" onclick="habilitarDetalhes(${orientacao.discente.id});">
							<img src="${ctx}/img/comprovante.png"/>
						</a>
					</td>
					<td> 
						${orientacao.discente.matriculaNome}
						<c:if test="${!orientacao.discente.regular}"> 
							<span class="info"> (${orientacao.discente.tipoString}) </span>
						</c:if> 
					</td>
					<td style="width: 1px;"> 
						<h:commandLink title="Visualizar Histórico" action="#{historico.selecionaDiscenteForm}" id="linkVisualizarHist2">
							<f:param value="#{orientacao.discente.id}" name="id"/>
							<h:graphicImage value="/img/view2.gif"/>
						</h:commandLink>
					</td>
					<td style="width: 1px;">
						<h:commandLink title="Visualizar Orientações Dadas" action="#{orientacaoAcademica.visualizarOrientacoesDadas}" id="linkVerOrientacoesDadas">
							<f:param value="#{orientacao.discente.id}" name="idDiscente" />
							<f:param value="#{orientacao.discente.nivel}" name="nivelDiscente" />
							<h:graphicImage value="/img/view.gif"/>
						</h:commandLink>					
					</td>
				</tr>
				<tr class="${stripe}"> 
					<td colspan="4" id="linha_${orientacao.discente.id}" class="detalhesDiscente" ><h:graphicImage value="/img/indicator.gif"/></td>				
				</tr>
			</c:if>
		</c:forEach>
	</tbody>
</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
		