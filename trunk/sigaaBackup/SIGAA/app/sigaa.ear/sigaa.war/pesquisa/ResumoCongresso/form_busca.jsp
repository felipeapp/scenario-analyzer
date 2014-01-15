<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> &gt; Resumos do CIC</h2>
	<h:form id="formConsulta">

		<table class="formulario" align="center" width="60%">
		<caption class="listagem">Busca dos Resumos</caption>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{consultaResumoCongressoMBean.filtroCongresso}" id="checkCongresso" styleClass="noborder"/> </td>
				<td width="22%"><label for="checkCongresso" onclick="$('formConsulta:checkCongresso').checked = !$('formConsulta:checkCongresso').checked;">Congresso:</label></td>
				<td>
					<h:selectOneMenu id="congresso"
						value="#{consultaResumoCongressoMBean.buscaCongresso}" style="width: 70%;"
						onfocus="$('formConsulta:checkCongresso').checked = true;">
						<f:selectItem itemValue="-1" itemLabel="-- SELECIONE --"/>
						<f:selectItems value="#{consultaResumoCongressoMBean.congressosCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{consultaResumoCongressoMBean.filtroArea}" id="checkArea" styleClass="noborder"/> </td>
				<td width="22%"><label for="checkArea" onclick="$('formConsulta:checkArea').checked = !$('formConsulta:checkArea').checked;">Área:</label></td>
				<td>
					<h:selectOneMenu id="area"
						value="#{consultaResumoCongressoMBean.buscaArea}" style="width: 70%;"
						onfocus="$('formConsulta:checkArea').checked = true;">
						<f:selectItem itemValue="" itemLabel="-- SELECIONE --"/>
						<f:selectItem itemValue="ET" itemLabel="EXATAS E TECNOLÓGICAS"/>						
						<f:selectItem itemValue="HS" itemLabel="HUMANAS E SOCIAIS"/>						
						<f:selectItem itemValue="SB" itemLabel="SAÚDE E BIOLÓGICAS"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton id="btnBuscar" action="#{consultaResumoCongressoMBean.buscar}" value="Buscar"/>
					<h:commandButton id="btnCancelar" action="#{consultaResumoCongressoMBean.cancelar}" value="Cancelar" onclick="#{confirm}"/>
				</td>
			</tr>
			</tfoot>
		</table>
		<br/>
		
		<c:if test="${not empty consultaResumoCongressoMBean.resultadosBusca}">
			<center>
					<h:messages/>
					<div class="infoAltRem">
						<h:form>
							<h:graphicImage value="/img/buscar.gif" style="overflow: visible;"/>: Visualizar <br/>
						</h:form>
					</div>
			</center>
			<table class="formulario" width="100%">
				<caption> Resumos do CIC (${ fn:length(consultaResumoCongressoMBean.resultadosBusca) }) </caption>
				
				<thead>
					<tr>
						<th>Código</th>
						<th>Título/Autor/Orientador</th>
						<th>Status</th>
						<th></th>
					</tr>
				</thead>
	
				<c:forEach items="#{ consultaResumoCongressoMBean.resultadosBusca }" var="linha" varStatus="status">
					<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td> ${ linha.codigo } </td>
						<td> 
							<i>${ linha.titulo }</i> <br/>
							Autor: ${ linha.autor.nome } <br/> 
							Orientador: ${ linha.orientador.nome } 
						</td>
						<td> ${ linha.statusString } </td>
						<td width="20">
							<h:commandLink id="lnkVisualizar" action="#{ consultaResumoCongressoMBean.view }" >
								<h:graphicImage value="/img/buscar.gif" style="overflow: visible;" title="Visualizar"/>
								<f:param name="id" value="#{ linha.id }"/>
							</h:commandLink>
						</td>
					</tr>				
				</c:forEach>
			</table>
		</c:if>	
	</h:form>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>