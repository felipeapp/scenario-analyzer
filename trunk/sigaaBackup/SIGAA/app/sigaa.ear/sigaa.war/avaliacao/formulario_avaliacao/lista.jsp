<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:form>
 <h2><ufrn:subSistema /> > Lista de Formulários para Avaliações Institucionais</h2>
	<br/>
	<div class="infoAltRem">
		<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar
		<ufrn:checkRole papel="<%=SigaaPapeis.COMISSAO_AVALIACAO%>">
			<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Alterar
			<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover
		</ufrn:checkRole>
	</div>
	<table class="formulario" width="90%">
		<caption>Lista de Formulários para Avaliações Institucionais</caption>
		<thead>
			<tr>
				<td>Título</td>
				<td>Perfil Entrevistado</td>
				<td style="text-align: center">Ensino à Distância</td>
				<td></td>
				<td></td>
				<td></td>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="#{cadastrarFormularioAvaliacaoInstitucionalMBean.all}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>${item.titulo}</td>
					<td>${item.descricaoTipoAvaliacao}</td>
					<td style="text-align: center"><ufrn:format valor="${item.ead}" type="simNao" /></td>
					<td width="2%">
						<h:commandLink action="#{cadastrarFormularioAvaliacaoInstitucionalMBean.view}" id="Visualizar">
							<h:graphicImage value="/img/view.gif" style="overflow: visible;" title="Alterar Formulário da Avaliação Institucional"/>
							<f:param name="id" value="#{item.id}" />
						</h:commandLink>
					</td>
					<td width="2%">
						<ufrn:checkRole papel="<%=SigaaPapeis.COMISSAO_AVALIACAO%>">
							<h:commandLink action="#{cadastrarFormularioAvaliacaoInstitucionalMBean.atualizar}" id="alterar">
								<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" title="Alterar Formulário da Avaliação Institucional"/>
								<f:param name="id" value="#{item.id}" />
							</h:commandLink>
						</ufrn:checkRole>
					</td>
					<td width="2%">
						<ufrn:checkRole papel="<%=SigaaPapeis.COMISSAO_AVALIACAO%>">
							<h:commandLink action="#{cadastrarFormularioAvaliacaoInstitucionalMBean.preRemover}" id="preRemover">
								<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover Formulário da Avaliação Institucional" />
								<f:param name="id" value="#{item.id}" />
							</h:commandLink>
						</ufrn:checkRole>
					</td>
				</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="6">
					<h:commandButton action="#{cadastrarFormularioAvaliacaoInstitucionalMBean.cancelar}" value="Cancelar" id="Cancelar" onclick="#{ confirm }"/>
				</td>
			</tr>
		</tfoot>
	</table>
	<br/>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>