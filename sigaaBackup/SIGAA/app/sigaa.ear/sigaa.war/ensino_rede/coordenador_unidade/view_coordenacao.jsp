<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@ taglib uri="/tags/a4j" prefix="a4j" %>

<f:view>
	<h2> <ufrn:subSistema /> > Coordenação da Unidade</h2>
	
	
	<h:form id="form">
	<table class="formulario" style="width:60%;">
		<caption>Dados do Membro da Coordenação</caption>
		<tbody>
			<tr>
				<th style="width: 20%;"  class="rotulo"> Instituição: </th>
				<td colspan="3">${coordenadorUnidadeMBean.obj.dadosCurso.campus.instituicao.sigla} - ${coordenadorUnidadeMBean.obj.dadosCurso.campus.sigla}</td>
			</tr>
			<tr>
				<th style="width: 20%;" class="rotulo"> Nome: </th>
				<td colspan="3">
					${coordenadorUnidadeMBean.obj.pessoa.nome}
				</td>
			</tr>
			<tr>
				<th class="rotulo">Cargo:</th>
				<td colspan="3">
					<h:outputText value="#{coordenadorUnidadeMBean.obj.cargo.descricao }"/>	
				</td>
			</tr>
			<tr>
				<th class="rotulo">E-mail:</th>
				<td colspan="3">
					<h:outputText value="#{coordenadorUnidadeMBean.obj.dados.email}" id="email" />
				</td>
			</tr>
			<tr>
				<td colspan="4">
					<t:dataTable var="item" value="#{coordenadorUnidadeMBean.obj.dados.telefones}"  id="telefones" style="width: 100%;">
						<tr >						
							<t:column style="text-align: right;width: 13%;">
								<b><h:outputText  value="Tel. Fixo:" rendered="#{item.tipo == 1 }"/></b>
								<b><h:outputText value="Tel. Celular:" rendered="#{item.tipo == 2 }"/></b>
							</t:column>
							<t:column style="text-align: left;width: 22%;" >(
								<h:outputText value="#{item.codigoArea}"/>
								) &nbsp;
								<h:outputText value="#{item.numero}"/>
							</t:column>
							<t:column style="text-align: left;width: 30%;" >
								<h:outputText  value="Ramal:" rendered="#{item.ramal > 0 }"/>&nbsp;
								<h:outputText value="#{item.ramal}" rendered="#{item.ramal > 0 }"/>
							</t:column>
						</tr>
					</t:dataTable>
				</td>
			</tr>
			<c:if test="${empty coordenadorUnidadeMBean.obj.dados.telefones }">
				<tr id="listaVazia">
					<td align="center" colspan="4" id="listaVazia"><span style="color: red;" >Nenhum telefone foi adicionado.</span></td>
				</tr>
			</c:if>
		</tbody>
		<tfoot>
			<tr>	
				<td colspan="4">
					<h:commandButton value="Cancelar" id="Cancelar" immediate="true" action="#{ coordenadorUnidadeMBean.voltar }" />
				</td>
			</tr>
		</tfoot>
	</table>

	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>