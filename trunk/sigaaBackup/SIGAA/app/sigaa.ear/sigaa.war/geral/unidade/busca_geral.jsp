<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	.colAtiva{text-align: center !important;width: 50px;}
</style>

<html>

<f:view>
<c:if test="${acesso.discente and componenteCurricular.portalDiscente}">
	<%@include file="/portais/discente/menu_discente.jsp" %>
</c:if>
<c:if test="${acesso.docente and componenteCurricular.portalDocente}">
	<%@include file="/portais/docente/menu_docente.jsp" %>
</c:if>
<c:if test="${acesso.coordenadorCursoStricto and componenteCurricular.portalCoordenadorStricto}">
	<%@include file="/stricto/menu_coordenador.jsp" %>
</c:if>
	<h2><ufrn:subSistema/> > Consulta Geral de Unidades Acadêmicas</h2>
	
	<h:outputText value="#{unidade.create}" />

	<h:form id="formBuscaUnidade" prependId="false">

		<table class="formulario" width="50%">
			<caption class="listagem">Buscar Unidade Acadêmica </caption>

			<tr>
				<td><input type="radio" id="checkNome" name="paramBusca"
					value="nome" class="noborder" ${ unidade.param == 'nome' ? "checked='checked'" : ""}></td>
				<th class="required">Nome: </th>
				<td><h:inputText value="#{unidade.nome}" size="50"
					id="paramNome" onfocus="marcaCheckBox('checkNome')"
					onkeyup="CAPS(this)" /></td>
			</tr>

			<tr>
				<td><input type="radio" id="checkTipo" name="paramBusca"
					value="tipo" class="noborder" ${ unidade.param == 'tipo' ? "checked='checked'" : ""}></td>
				<th class="required">Tipo: </th>
				<td><h:selectOneMenu id="tipo" value="#{unidade.tipo}"
					onchange="marcaCheckBox('checkTipo')">
					<f:selectItem itemValue="0" itemLabel=" FILTRAR POR TIPO " />
					<f:selectItems value="#{unidade.tipos}" />
				</h:selectOneMenu></td>
			</tr>
			
			<tr>
				<td>
					<h:selectBooleanCheckbox value="#{unidade.checkAtivo}" id="checkAtivo" />
				</td>
				<th>Ativa:</th>
				<td colspan="2">
					<h:selectOneRadio value="#{unidade.ativo}" onclick="setCheckAtivo()">
						<f:selectItems value="#{unidade.simNao}" />
					</h:selectOneRadio>
				</td>
			</tr>

			<tr>
				<td><input type="checkbox" id="checkRelatorio" name="paramRelatorio"
					value="gerar" class="noborder"></td>
				<td colspan="2"><label for="checkRelatorio">Formato Relatório</label></td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="3"><h:commandButton
						action="#{unidade.buscarAcademica}" value="Buscar" id="btnBuscar" /> <h:commandButton
						action="#{unidade.cancelar}" value="Cancelar" onclick="#{confirm}" id="btnCancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>


	<h:outputText value="#{unidade.create}"/>

	<c:if test="${not empty unidade.lista}">
	
		<div class="infoAltRem" style="margin-top: 5px; "><h:graphicImage value="/img/view.gif"
			style="overflow: visible;" />: Detalhar Unidade Acadêmica <br />
		</div>

		<table class="listagem" width="100%">
			
			<caption class="listagem">Lista de Unidades Acadêmicas (${unidade.quantResultado})</caption>
			
			<thead>
				<tr>
					<td width="50px"><p align="right">Código</p></td>
					<td>Unidade</td>
					<td>Sigla</td>
					<td>Tipo</td>
					<c:if test="${!unidade.checkAtivo}">
					<td class="colAtiva">Ativa</td>
					</c:if>
					<td></td>
				</tr>
			</thead>
			
			<c:forEach items="#{unidade.lista}" var="item" varStatus="status">
				
				<tr class="${status.index % 2 == 0 ? "
					linhaPar" : "linhaImpar"}" style="font-size: xx-small">
					
					<td align="right">${item.codigo}</td>
					<td>${item.nome}</td>
					<td>${item.sigla}</td>
					<td>${item.tipoAcademicaDesc}</td>
					<c:if test="${!unidade.checkAtivo}">
					<td class="colAtiva">${item.ativo?"SIM":"NÃO"}</td>
					</c:if>
					<td>
						<h:commandLink action="#{unidade.detalhar}">
							<h:graphicImage value="/img/view.gif" title="Detalhar Unidade Acadêmica"></h:graphicImage>
							<f:param name="id" value="#{item.id}"/>
						</h:commandLink>
					</td>
				</tr>
				
			</c:forEach>
			
		</table>
	</c:if> 

	<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>
	
</h:form>
<rich:jQuery selector="#checkAtivo" query="attr('checked','checked')" timing="onJScall" name="setCheckAtivo"></rich:jQuery>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
