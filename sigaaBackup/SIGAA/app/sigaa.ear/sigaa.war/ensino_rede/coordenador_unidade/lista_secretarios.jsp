<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	table.listagem tr.curso td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
</style>
<f:view>
	<h2> <ufrn:subSistema /> > Secretários de Unidade</h2>
	<c:set var="confirmacao" value="if (!confirm('Deseja  Excluir o Secretário(a)?')) return false" scope="request"/>
	<h:form id="formulario">
		<table class="formulario" style="width:45%;">
			<caption> Informe os critérios de busca</caption>
			<tbody>
				<tr>
					<th style="width:35%"> Instituição de Ensino:</th>
					<td>
						<h:selectOneMenu value="#{coordenadorUnidadeMBean.idInstituicao}" id="status" style="width: 90%;" >
							<f:selectItem itemLabel="-- TODOS --" itemValue="0"  />
							<f:selectItems value="#{selecionaCampusRedeMBean.ifesCombo}" id="iesCombo"/>
						</h:selectOneMenu>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton action="#{coordenadorUnidadeMBean.buscarCoordenacao}" value="Buscar" id="buscar" />
						<h:commandButton action="#{coordenadorUnidadeMBean.cancelar}" value="Cancelar" id="cancelar" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<c:if test="${not empty coordenadorUnidadeMBean.resultadosBusca}">
		<br />
		<center>
			<div class="infoAltRem" style="width:90%;"> 
				<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar Secretário(a)
				<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Alterar Secretário(a)
				<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />:  Excluir Secretário(a)
				<h:graphicImage value="/img/user.png" style="overflow: visible;" />: Logar Como
			</div>
		</center>
		<h:form id="form">
			<table class="listagem" style="width:90%;">
				<caption> Lista de Secretários Encontrados (${fn:length(coordenadorUnidadeMBean.resultadosBusca)}) </caption>
				<thead>
					<tr>
						<th> Nome </th>
						<th> Cargo </th>
						<th> Unidade </th>
						<td colspan="4"></td>
					</tr>
				</thead>
				<c:forEach items="#{coordenadorUnidadeMBean.resultadosBusca}" var="_secretario" varStatus="status">

					<c:if test="${_instituicaoAtual != _secretario.instituicao.id or _campusAtual != _secretario.campus.id}">
						<c:set var="_instituicaoAtual" value="${_secretario.instituicao.id}" />
						<c:set var="_campusAtual" value="${_secretario.campus.id}" />
						<c:set var="controlClassLine" value="0" />
						
						<tr class="curso">
							<td colspan="7">${fn:toUpperCase(_secretario.instituicao.nome)} </td>
						</tr>
					</c:if>
				
					<tr class="${controlClassLine % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
						<c:set var="controlClassLine" value="${controlClassLine+1 }" />
						<td>
							${_secretario.pessoa.nome }
						</td>
						<td>
							${_secretario.cargo.descricao }
						</td>
						<td>
							${fn:toUpperCase(_secretario.campus.nome)}
						</td>
						<td align="right" width="1%">
							<h:commandLink  action="#{coordenadorUnidadeMBean.viewCoordenacao}"  id="viewCoordenador">
								<f:param name="idMembroCoordenacao" value="#{_secretario.id}" />
								<h:graphicImage url="/img/view.gif" title="Visualizar Coordenador(a)" />
							</h:commandLink>
						</td>
						<td align="right" width="2%">
							<h:commandLink  action="#{coordenadorUnidadeMBean.alterarSecretaria}"  id="alterarSecretaria">
								<f:param name="idMembroCoordenacao" value="#{_secretario.id}" />
								<h:graphicImage url="/img/alterar.gif" title="Alterar Secretário(a)" />
							</h:commandLink>
						</td>
						<td align="right" width="2%">
							<h:commandLink onclick="#{confirmacao}"  action="#{coordenadorUnidadeMBean.inativarMembroCoordenacao}"  id="inativarMembroCoordenacao">
								<f:param name="idMembroCoordenacao" value="#{_secretario.id}" />
								<h:graphicImage url="/img/delete.gif" title="Excluir Secretário(a)" />
							</h:commandLink>
						</td>
						<td width="20">
							<h:commandLink action="#{ coordenadorUnidadeMBean.logarComo }" >
								<h:graphicImage value="/img/user.png" style="overflow: visible;" title="Logar Como"/>
								<f:param name="login" value="#{ _secretario.usuario.login }"/>
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
			<tfoot>
				<tr>
					<td colspan="7" style="text-align: center; font-weight: bold;">
					</td>
				</tr>
			</tfoot>
			</table>
		</h:form>
		
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>