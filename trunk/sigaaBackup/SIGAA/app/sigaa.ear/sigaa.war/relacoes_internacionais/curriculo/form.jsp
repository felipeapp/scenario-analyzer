<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="traducaoCurriculoMBean" />
	<h2><ufrn:subSistema /> > Tradução de Componente(s) da Estrutura Curricular</h2> 
	
	<c:set var="turma" value="${traducaoCurriculoMBean.obj}"/>
	
	<h:form id="form_curriculo_componentes">
	
	<table class="visualizacao" style="width: 90%">
		<caption class="formulario">Dados da Estrutura Curricular</caption>
		<tr>
			<th class="rotulo">Código: </th>
			<td><h:outputText value="#{traducaoCurriculoMBean.obj.codigo }" /></td>
		</tr>
		<tr>
			<th class="rotulo">Matriz Curricular: </th>
			<td><h:outputText value="#{traducaoCurriculoMBean.obj.matriz.descricao }" /></td>
		</tr>
		<tr>
			<th class="rotulo">Período Letivo de Entrada em Vigor: </th>
			<td><h:outputText
				value="#{traducaoCurriculoMBean.obj.anoEntradaVigor} - #{traducaoCurriculoMBean.obj.periodoEntradaVigor }" /></td>
		</tr>
		<tr>
			<th class="rotulo">Carga Horária:</th>
			<td>
			<table>
				<tr>
					<th>Total Mínima</th>
					<td><h:outputText value="#{traducaoCurriculoMBean.obj.chTotalMinima}" /></td>
					<th>Optativas Mínima</th>
					<td><h:outputText value="#{traducaoCurriculoMBean.obj.chOptativasMinima}" /></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>	
	<br/><br/>
	<table class="formulario" width="90%">
		<caption>Internacionalização de Componente(s) da Estrutura Curricular</caption>
		<thead>
			<tr>
				<th style="text-align:left; padding-left: 5px; font-weight: bold">Campo</th>
				<th style="text-align:left; padding-left: 5px;">Tradução</th>
			</tr>	
		</thead>
		<tbody>		
			<tr><td colspan="2">
			<c:forEach items="#{traducaoCurriculoMBean.mapaComponente}" var="map" varStatus="loop">
			<table class="subFormulario" width="100%">
				<caption>${map.key.codigoNome}</caption>
				<c:forEach items="#{map.value}" var="item" varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<th style="text-align:left; padding-left: 20px; font-weight: bold" valign="center" width="15%">${item.itemTraducao.nome}:</th>
						<td style="text-align:left;">
							<table>
							<c:forEach items="#{item.elementos}" var="elemento" varStatus="loop">
								<tr>
									<td><h:outputText value="#{elemento.descricaoIdioma}:"/></td>
								</tr>
								<tr>
									<td><h:inputTextarea value="#{elemento.valor}" onkeyup="CAPS(this)" disabled="#{elemento.idiomaLocal or elemento.inputDisabled}" cols="#{item.itemTraducao.tipoAreaTexto ? '80' : '60'}" rows="#{item.itemTraducao.tipoAreaTexto ? '5' : '1'}"/></td>
								</tr>	
							</c:forEach>
							</table>
						</td>
					</tr>
				</c:forEach>
			</table>
			<br/><br/>
			</c:forEach>
			</td></tr>		
		</tbody>
		<tfoot>
		<tr>
			<td colspan=2>
				<h:commandButton value="#{traducaoCurriculoMBean.confirmButton}" action="#{traducaoCurriculoMBean.cadastrar}" onclick="setAbaHistorico('componentes')"/> 
				<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{traducaoCurriculoMBean.cancelar}" />
			</td>
		</tr>
	</table>	
		
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>