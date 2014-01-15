<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<style>
	.homologado { color: #292; font-weight: bold;}
	.pendente { color: #922; font-weight: bold;}
</style>

<h2><ufrn:subSistema /> > Planos Individuais do Docente </h2>

<f:view>
<a4j:keepAlive beanName="cargaHorariaPIDMBean" />
<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>

	<div class="descricaoOperacao">
		<h4> Caro(a) docente, </h4>
		<p>
			Abaixo estão listados todos os Planos Individuais submetidos por docentes de sua unidade. Ao visualizar os detalhes de cada um deles será possível proceder a homologação das atividades
			cadastradas ou o retorno ao docente para que algum ajuste seja realizado.
		</p>
	</div>

	<h:form id="form">
	
	<table class="formulario">
		<caption>Filtros</caption>
		<tr>
			<th>	
				&nbsp;<span style="text-align:right;">Docente:</span>&nbsp;
			</th>	
			<td>
				
				<h:inputText value="#{cargaHorariaPIDMBean.servidorFiltroChefia.pessoa.nome}" id="nome" size="60" style="text-align:left;"/>
			 	<h:inputHidden value="#{cargaHorariaPIDMBean.servidorFiltroChefia.id}" id="idServidor" />

				<ajax:autocomplete source="form:nome" target="form:idServidor"
					baseUrl="/sigaa/ajaxServidor" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=unidade,inativos=true"
					parser="new ResponseXmlToHtmlListParser()" />
				<span id="indicator" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
			</td>
		</tr>
		<tr>
			<th>
				&nbsp;<span style="text-align:right;" class="required">Ano-Período:</span>&nbsp;
			</th>
			<td>
				<h:selectOneMenu value="#{ cargaHorariaPIDMBean.dataFiltroChefia }" style="text-align:left;">
					<f:selectItem itemValue = "0" itemLabel = " -- SELECIONE UM ANO -- " />
					<f:selectItems value="#{ cargaHorariaPIDMBean.datasCombo }" />
				</h:selectOneMenu>
			</td>	
		</tr>
		<tr>
			<th>
				&nbsp;<span style="text-align:right;">Situação:</span>&nbsp;
			</th>
			<td>
				<h:selectOneMenu value="#{ cargaHorariaPIDMBean.situacaoFiltroChefia }" style="text-align:left;">
					<f:selectItems value="#{ cargaHorariaPIDMBean.situacaoCombo}" />
				</h:selectOneMenu>
			</td>	
		</tr>
		<tfoot><tr><td colspan="2" style="text-align:center;">
			<h:commandButton action="#{ cargaHorariaPIDMBean.gerarListagemPIDChefeDepartamento }" value=" Filtrar PIDs "/>
		</td></tr></tfoot>
	</table><br/>
	
	<div class="infoAltRem" style="width:80%">
		<h:graphicImage value="/img/buscar.gif"style="overflow: visible;"/> : Visualizar Plano Individual do Docente
	</div>

		<c:if test="${not empty cargaHorariaPIDMBean.pidsHomologados}">
			<table class="listagem" style="width:80%">
			<caption class="listagem">Lista de Planos Individuais em Homologação</caption>
			
			<thead>
			<tr>
				<th style="text-align:center;">Período</th>
				<th>Docente</th>
				<th style="text-align:right;">CH Ensino</th>
				<th style="text-align:right;">CH Outras Atividades</th>
				<th>Situação</th>
				<th colspan="2"></th>
			</tr>
			</thead>
					<c:forEach items="#{cargaHorariaPIDMBean.pidsHomologados}" var="item" varStatus="status">
						<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
							<td style="text-align:center;">${item.ano}.${item.periodo}</td>
							<td>${item.servidor.nome}</td>
							<td align="right">
								<h:outputText value="#{item.totalGrupoEnsino} h">
									<f:convertNumber maxFractionDigits="1" groupingUsed="false" />
								</h:outputText>
							</td>
							<td align="right">
								<h:outputText value="#{item.totalGrupoOutrasAtividades} h">
									<f:convertNumber maxFractionDigits="1" groupingUsed="false" />
								</h:outputText>
							</td>
							
							<c:set var="situacaoPid" value="#{item.homologado ? 'homologado' : 'pendente'}"/>
							<td class="${situacaoPid}"> ${item.descricaoStatusChefiaDepartamento}</td>
							 
							<td align="right"> 
								<h:commandLink action="#{cargaHorariaPIDMBean.visualizarPIDEnviadoChefiaDepartamento}" title="Visualizar Plano Individual do Docente"> 
									<h:graphicImage url="/img/buscar.gif" />
									<f:param name="id" value="#{item.id}"/>
								</h:commandLink>
							</td>
							
						</tr>
					</c:forEach>
			</table>
		</c:if>
		<br/>
		<c:if test="${not empty cargaHorariaPIDMBean.pidsCadastrados}">
			<table class="listagem" style="width:80%">
			<caption class="listagem">Lista de Planos Individuais a serem Cadastrados</caption>
			
			<thead>
			<tr>
				<th style="text-align:center;">Período</th>
				<th>Docente</th>
				<th style="text-align:right;">CH Ensino</th>
				<th style="text-align:right;">CH Outras Atividades</th>
				<th>Situação</th>
			</tr>
			</thead>
					<c:forEach items="#{cargaHorariaPIDMBean.pidsCadastrados}" var="item" varStatus="status">
						<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
							<td align="center">${item.ano}.${item.periodo}</td>
							<td>${item.servidor.nome}</td>
							<td align="right">
								<h:outputText value="#{item.totalGrupoEnsino} h">
									<f:convertNumber maxFractionDigits="1" groupingUsed="false" />
								</h:outputText>
							</td>
							<td align="right">
								<h:outputText value="#{item.totalGrupoOutrasAtividades} h">
									<f:convertNumber maxFractionDigits="1" groupingUsed="false" />
								</h:outputText>
							</td>
							
							<c:set var="situacaoPid" value="#{item.homologado ? 'homologado' : 'pendente'}"/>
							<td class="${situacaoPid}"> ${item.descricaoStatusChefiaDepartamento}</td>
							 							
						</tr>
					</c:forEach>
			</table>
		</c:if>
				
		<c:if test="${empty cargaHorariaPIDMBean.pidsHomologados && empty cargaHorariaPIDMBean.pidsCadastrados}">
			<table class="listagem" style="width:80%">
			<caption class="listagem">Lista de Planos Individuais do Docente</caption>
				<tr>
					<td>
						<div align="center">Não existe nenhum plano cadastrado.</div>
					</td>
				</tr>
			</table>
		</c:if>
		<br/>
		<div align="center">
			<span class="required">&nbsp;</span>
			Campos de Preenchimento Obrigatório
		</div>	
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
