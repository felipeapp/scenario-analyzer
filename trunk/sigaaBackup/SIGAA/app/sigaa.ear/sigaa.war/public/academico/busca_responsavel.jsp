<%@include file="/public/include/cabecalho.jsp"%>

<style>
	.colCargo{text-transform: uppercase;}
</style>

<f:view>

	<h2>Chefes, Coordenações e Diretores </h2>

	<a4j:keepAlive beanName="consultaPublicaResponsavel"></a4j:keepAlive>

	<h:form id="form" prependId="true">

		<div class="descricaoOperacao">
			<p>
				Esta página permite ao usuário consultar os chefes dos departamentos, 
				as coordenações dos cursos de graduação e pós-graduação,
				 e os diretores das unidades acadêmicas especializadas/centros em exercício.
			</p>
		</div>

		<table class="formulario" width="55%">
			<caption>Informe qual consulta que deseja realizar</caption>
			<tbody>
				<tr>
					<th width="18%">Buscar por:</th>
					<td>
						<h:selectOneMenu id="tipoConsulta" value="#{consultaPublicaResponsavel.tipoConsulta}">
							<f:selectItems value="#{consultaPublicaResponsavel.comboTiposConsulta}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</tbody>
			<tfoot>
					<tr>
						<td colspan="2">
							<h:commandButton value="Buscar" action="#{ consultaPublicaResponsavel.buscar }"
							id="btnBuscar" /> &nbsp;
							<h:commandButton value="Cancelar" action="#{ consultaPublicaResponsavel.cancelar }" 
							id="btnCancelar" onclick="#{confirm}" />
						</td>
					</tr>
			</tfoot>
		</table>

		<br/>
			
		<c:if test="${not empty consultaPublicaResponsavel.listaMapas}">
		<table class="listagem">
			
			<caption>Resultados Encontrados (${fn:length(consultaPublicaResponsavel.listaMapas)})</caption>
			
			<thead>
				<tr>
					<th>
						<h:outputText value="Departamento" rendered="#{!consultaPublicaResponsavel.diretores && !consultaPublicaResponsavel.coordenadores}"/>
						<h:outputText value="Unidade" rendered="#{consultaPublicaResponsavel.diretores}"/>
						<h:outputText value="Curso" rendered="#{consultaPublicaResponsavel.coordenadores}"/>
					</th>
					<th>Nome</th>
					<c:if test="${!consultaPublicaResponsavel.chefesDiretores}">
					<th>Cargo</th>
					</c:if>
					<th>Telefone</th>
					<th>E-mail</th>
				</tr>
			<thead>
			
			<tbody>
			
			<c:set var="ultimo" value=""/>
			<c:set var="servidor" value=""/>
			
			<c:forEach var="linha" varStatus="status" items="#{consultaPublicaResponsavel.listaMapas}">
			
			<c:if test="${ultimo!=linha.agrupador && !consultaPublicaResponsavel.diretores}">
			<tr>
				<td colspan="5"  class="subListagem"><h:outputText value="#{linha.agrupador}"/></td>
			</tr>
			</c:if>
			
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td> <h:outputText value="#{linha.unidade}"/> </td>
				<td> <h:outputText value="#{linha.nome}"/> </td>
				<c:if test="${!consultaPublicaResponsavel.chefesDiretores}">
				<td> <h:outputText value="#{linha.cargo}"/> </td>
				</c:if>
				<td> <h:outputText value="#{linha.telefone}"/> </td>
				<td> <h:outputText value="#{linha.email}"/> </td>
			</tr>
			
			<c:set var="ultimo" value="#{linha.agrupador}"/>
			<c:set var="servidor" value="#{linha.nome}"/>
			
			</c:forEach>
			
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="5">&nbsp;</td>
				</tr>
			</tfoot>
		
		</table>
		</c:if>
	
	</h:form>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>