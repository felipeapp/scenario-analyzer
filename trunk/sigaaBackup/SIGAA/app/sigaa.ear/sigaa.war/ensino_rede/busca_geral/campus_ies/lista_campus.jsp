<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<c:set var="confirmSimples"
		value="return confirm('Deseja cancelar a operação?');"
		scope="request" />

<style>
	table.listagem tr.curso td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
</style>


<f:view>
	<h2><ufrn:subSistema /> &gt; Campus ies</h2>


	<h:form id="formulario">
		<table class="formulario" style="width:30%;">
			<caption> Informe os critérios de busca</caption>
			<tbody>
				<tr>
					<td style="width:35%; text-align: right;"> 
						<label for="checkIes" onclick="$('formulario:checkIes').checked = !$('formulario:checkIes').checked;">Sigla IES:</label>
					</td>
					<td>
						<h:selectOneMenu value="#{selecionaCampusRedeMBean.valores.valorIes}" id="status" style="width: 90%;" onfocus="getEl('formulario:checkIes').dom.checked = true;">
							<f:selectItem itemLabel="-- TODAS --" itemValue="0"  />
							<f:selectItems value="#{selecionaCampusRedeMBean.ifesCombo}" id="statusDiscenteCombo"/>
						</h:selectOneMenu>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton actionListener="#{selecionaCampusRedeMBean.buscar}" value="Buscar" id="buscar" />
						<h:commandButton action="#{selecionaCampusRedeMBean.cancelar}" value="Cancelar" id="cancelar" onclick="#{confirmSimples}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	
	<c:if test="${not empty selecionaCampusRedeMBean.resultadosBusca}">
	
		<br />
		<center>
			<div class="infoAltRem" style="width:90%;"> 
				<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Selecionar Campus
			</div>
		</center>
		
		<h:form id="form">
		<table class="listagem" style="width:90%;">
			<caption> Selecione abaixo o campus (${fn:length(selecionaCampusRedeMBean.resultadosBusca)}) </caption>
			<thead>
				<tr>
					<th> Instituição de Ensino - Campus </th>
					<th>  </th>
				</tr>
			</thead>
				
							
			<c:forEach items="#{selecionaCampusRedeMBean.resultadosBusca}" var="_campus" varStatus="status">

				<c:if test="${_instituicaoAtual != _campus.instituicao.id}">
					<c:set var="_instituicaoAtual" value="${_campus.instituicao.id}" />
					<c:set var="controlClassLine" value="0" />
					
					<tr class="curso">
						<td colspan="2">${fn:toUpperCase(_campus.instituicao.sigla)} - ${fn:toUpperCase(_campus.instituicao.nome)}</td>
					</tr>
				</c:if>
				
				<tr class="${controlClassLine % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					<c:set var="controlClassLine" value="${controlClassLine+1 }" />
					<td>
						CAMPUS ${fn:toUpperCase(_campus.sigla )}
					</td>
					<td align="right" width="2%">
						<h:commandButton image="/img/seta.gif" actionListener="#{selecionaCampusRedeMBean.escolheCampus}" title="Selecionar Campus"  id="selecionarCampus">
							<f:attribute name="idCampus" value="#{_campus.id}" />
						</h:commandButton>
					</td>
				</tr>
			</c:forEach>
			
			<tfoot>
				<tr>
					<td colspan="2" style="text-align: center; font-weight: bold;">
					</td>
				</tr>
			</tfoot>
		</table>
		</h:form>
	</c:if>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>