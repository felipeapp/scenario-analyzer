<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsf/html"      prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core"      prefix="f" %>

<%-- Resultado da consulta de bancas de defesas ordenado por discente --%>
<table class="listagem">
	<caption> Resultado da Busca (${fn:length(consultarDefesaMBean.listaBancaPos)})</caption>
	<thead>
		<tr>
			<th> Matrícula </th>
			<th colspan="2"> Nome </th>
			<th> </th>
			<th> </th>
			<th> </th>
		</tr>
		<tr>
			<th></th>
			<th> Tipo </th>
			<th> Título do Trabalho </th>
			<th style="text-align: center;"> Data </th>
			<th></th>
			<th></th>
		</tr>
	</thead>
	<tbody>
		<c:set var="grupoPrograma" value="-1" />
		<c:set var="grupoDiscente" value="-1" />
		<c:set var="index" value="0" />
		<c:forEach items="#{consultarDefesaMBean.listaBancaPos}" var="item" varStatus="status">
				<c:set var="loopPrograma" value="${item.dadosDefesa.discente.gestoraAcademica.id}"/>
				<c:set var="loopDiscente" value="${item.dadosDefesa.discente.id}"/>
				<c:if test="${grupoPrograma != loopPrograma}">
					<tr>
						<td colspan="6" class="subFormulario">${item.dadosDefesa.discente.gestoraAcademica.nome}</td>
					</tr>
					<c:set var="grupoPrograma" value="${loopPrograma}"/>
					<c:set var="index" value="0" />
				</c:if>
				<c:if test="${grupoDiscente != loopDiscente}">
					<c:set var="index" value="${index + 1}" />
					<tr class="${index % 2 != 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>	
							${item.dadosDefesa.discente.matricula}
						</td>
						<td nowrap="nowrap" colspan="2">	
							${item.dadosDefesa.discente.nome}
							(${item.dadosDefesa.discente.nivelDesc})
						</td>
						<td>	
							${item.dadosDefesa.discente.nivelDesc}
						</td>
						<td></td>
						<td></td>
					</tr>
					<c:set var="grupoDiscente" value="${loopDiscente}"/>
				</c:if>
				<tr class="${index % 2 != 0 ? 'linhaPar' : 'linhaImpar' }">
					<td></td>
					<td valign="top">${item.tipoDescricao }</td>
					<td >
						${item.dadosDefesa.tituloStripHtml}
					</td>
					<td valign="top" style="text-align: center;">
						<h:outputText value="#{item.data}" />
					</td>
					<td>
						<h:commandLink action="#{ consultarDefesaMBean.visualizarDadosDefesa }" id="linkParaVisualizarDadosDefesa"> 
								<f:param name="idBancaPos" value="#{ item.id }" id="paramIdDaBancaPos"/>
								<h:graphicImage value="/img/view.gif" title="Visualizar" />
						</h:commandLink>
					</td>
					<td>
						<%-- Usado no caso de uso de catalogação de defesa no Módulo Biblioteca. --%>
						<c:if test="${consultarDefesaMBean.ehCatalogacao}">
							<h:commandLink action="#{ catalogacaoMBean.iniciarDefesa }" id="linkIniciacaoDadosDefesa"> 
									<f:param name="idBancaPos" value="#{ item.id }"/>
									<h:graphicImage value="/img/seta.gif" alt="Catalogar Defesa" title="Catalogar Defesa" />
							</h:commandLink>
						</c:if>
						<%-- Usado no caso de uso de catalogação de defesa no Módulo Biblioteca. --%>
						<c:if test="${consultarDefesaMBean.associacaoComCatalogacao}">
							<h:commandLink action="#{ catalogacaoMBean.associarCatalogacaoDefesa}" id="linkAssociaDefesaCatalogacao"> 
									<f:param name="idBancaPos" value="#{ item.id }"/>
									<h:graphicImage value="/img/seta.gif" alt="Associar Defesa à Catalogação" title="Associar Defesa à Catalogação" />
							</h:commandLink>
						</c:if>
					</td>
				</tr>
		</c:forEach>
	</tbody>
</table>